/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows.workflows;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsRequestContext;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;

import com.eurelis.opencms.workflows.ui.Messages;
import com.eurelis.opencms.workflows.util.CmsUtil;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.FileWriter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollectorFactory;
import com.eurelis.opencms.workflows.workflows.availableworkflows.I_AvailableWorkflowCollector;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.HistoricElement;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowDisplayContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainerElement;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.DuplicateEntityException;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.InvalidActionException;
import com.opensymphony.workflow.InvalidEntryStateException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.query.FieldExpression;
import com.opensymphony.workflow.query.WorkflowExpressionQuery;

/**
 * This class implements some method to interface OSWorkflow with OpenCMS.
 * 
 * @author Sébastien Bianco
 */
public abstract class A_OSWorkflowManager {

	/** The log object for this class. */
	private static final Logger		LOGGER												= Logger
																								.getLogger(A_OSWorkflowManager.class);

	/** The name of the file that will content the list of workflows */
	protected static final String	WORKFLOWLIST_FILENAME								= "workflows.xml";

	/**
	 * The name of the repository where will be created the all required file for OSWorkflow (relative to WEB-INF
	 * repository of the web-app)
	 */
	protected static final String	WORKFLOWLIST_REPOSITORYPATH							= "classes";

	/**
	 * The path of the OSWorkflows config file in the RFS of OpenCMS relatively to the WEB-INF repository of the web-app
	 * filepath (RFS).
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_RFSFILEPATH					= "classes"
																								+ ModuleSharedVariables.SYSTEM_FILE_SEPARATOR
																								+ "osworkflow.xml";

	/**
	 * The XPath of the property to update in the OSWorkflow config file
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_XPATH			= "/osworkflow/factory/property[@key=\"resource\"]";

	/**
	 * The parent XPath of the property to update in the OSWorkflow config file
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_PARENT_XPATH	= "/osworkflow/factory";

	/**
	 * The name of the attribute of the property to update in the OSWorkflow config file
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_ATTRIBUTNAME	= "value";

	/**
	 * The name of the property to update in the OSWorkflow config file
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_TAGNAME		= "property";

	/**
	 * The value of the key of the required value to update in the OSWorkflow config file
	 */
	protected static final String	OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_VALUE			= "resource";

	/**
	 * The name of the default user of OSWorkflow
	 */
	protected static final String	OSWORKFLOW_DEFAULTUSERNAME							= "defaultUser";

	/**
	 * The default String to use as comment if action are done automatically
	 */
	protected static final String	OSWORKFLOW_AUTOACTION_COMMENT						= "";

	/**
	 * The default String to use as owner if action are done automatically
	 */
	protected static final String	OSWORKFLOW_AUTOACTION_OWNER							= "Wf Engine";

	/**
	 * The base system path
	 */
	protected static final String NO_FILE_PATH = "NO_FILES";
	
	/**
	 * The admin object that allows to go through VFS and open files
	 */
	protected CmsObject				_cmsAdminObj										= null;
	/**
	 * The Configuration of OSWorkflow
	 */
	protected Configuration			_osWorkflowConfig;
	/**
	 * The WorkflowConfigManager
	 * 
	 * @uml.property name="_workflowConfigManager"
	 * @uml.associationEnd
	 */
	protected WorkflowConfigManager	_workflowConfigManager;

	/**
	 * Private default constructor so as to diseable the creation of the object from another class
	 */
	protected A_OSWorkflowManager() {
		super();

		// initialise the Admin Object that will permit to go though the VFS
		this._cmsAdminObj = CmsUtil.getAdminCmsObject();

		// update the list of available workflows
		AvailableWorkflowCollectorFactory.updateAvailableWorkflows();

		// Initialize the list of rights files
		_workflowConfigManager = WorkflowConfigManager.getInstance();

		// load OSWorkflow
		this.loadOSWorkflow();

		// create the default user
		try {
			this.createDefaultUser();
		}
		catch (DuplicateEntityException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (ImmutableException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
	}

	/**
	 * This method load OSWorkflow. So as to perform this functionality, the system :
	 * <ol>
	 * <li>Create a copy of each known Workflows Description in the RFS</li>
	 * <li>Create the configuration file with the list of thoses workflows</li>
	 * <li>Update the config file of OsWorkflows with the URL of the file generated in step 2</li>
	 * <li>Load OSWorkflow with the URL of the updated config file</li>
	 * </ol>
	 */
	protected void loadOSWorkflow() {

		// create the repository that will content all files
		FileWriter.createRepositoryIfDoesntExist(this.getRepositoryPath());

		// Create a copy of each known Workflows Description in the RFS
		this.createCopyOfWorkflowsDescriptionFile();

		try {
			// Create the configuration file with the list of thoses workflows
			String generatedFilePath = this.createWorkflowsListFile();

			// Update the config file of OsWorkflows with the URL of the file
			// generated in step 2
			String updatedConfigFilepath = this.updateOSWorkflowConfigFile(generatedFilePath);

			// Load OSWorkflow with configFile
			this.loadOSWorkflowWithConfigFile(updatedConfigFilepath);

		}
		catch (IOException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (DocumentException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (FactoryException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}

	}

	/**
	 * Load OsWorkflow with the the config File.
	 * 
	 * @param updatedConfigFilepath
	 *            the path of the config File
	 * @throws MalformedURLException
	 *             this exception can be thrown if the given paramter cannot be successfully converted into an URL
	 * @throws FactoryException
	 *             this exception can be thrown if the loading of OSWorkflow config file generate trouble
	 */
	private void loadOSWorkflowWithConfigFile(String updatedConfigFilepath) throws FactoryException,
			MalformedURLException {

		// initialize the configuration object of OSWorkflow
		this._osWorkflowConfig = new DefaultConfiguration();
		File f = new File(updatedConfigFilepath);
		// load config file
		this._osWorkflowConfig.load(f.toURL());

		LOGGER.debug(ErrorFormatter.formatArray(this._osWorkflowConfig.getWorkflowNames(),
				"list of registered workflows"));

	}

	/**
	 * Get the list of available workflows and create a copy of the workflow description file.<br/>
	 * The RFS path of each workflow description path will be updated.
	 */
	private void createCopyOfWorkflowsDescriptionFile() {

		// get the list of available workflows
		I_AvailableWorkflowCollector availableWorkflowCollector = AvailableWorkflowCollectorFactory.getInstance();
		List<DescriptionContainer> collectedWorkflows = availableWorkflowCollector.getListOfAvailableWorkflows();

		try {
			// switch the context to Offline project so as to go though the
			// entire list of files
			CmsRequestContext cmsContext = this._cmsAdminObj.getRequestContext();
			CmsProject offlineProject = this._cmsAdminObj.readProject("Offline");
			cmsContext.setCurrentProject(offlineProject);
		}
		catch (CmsException e) {
			LOGGER
					.warn("The admin object cannot switch to project Offline. Not published workflowlist file will not be into account ! ");
		}

		/*
		 * For each file generate the copy of the VFS description file content
		 */
		Iterator<DescriptionContainer> collectedWorkflowsIterator = collectedWorkflows.iterator();
		while (collectedWorkflowsIterator.hasNext()) {

			try {
				DescriptionContainer instanceOfWorkflow = collectedWorkflowsIterator.next();

				// get the UUID of the file
				String pathOfDescriptionFile = instanceOfWorkflow.get_workflowFilePath();

				// get the OpenCMS Resource thanks UUID
				CmsResource descriptionFileResource = this._cmsAdminObj.readResource(pathOfDescriptionFile);
				if (descriptionFileResource.isFile()) {

					// Read the file
					CmsFile resourceToRead = this._cmsAdminObj.readFile(descriptionFileResource);
					byte[] fileContent = resourceToRead.getContents();

					String filePath = getRepositoryPath() + ModuleSharedVariables.SYSTEM_FILE_SEPARATOR
							+ descriptionFileResource.getName();

					// copy the file content
					FileWriter.writeFile(filePath, new String(fileContent));

					// update the RFS filepath
					instanceOfWorkflow.set_realFilePathOfDescriptionFile(filePath);

					// LOGGER.debug( "WF | instanceOfWorkflow =
					// "+instanceOfWorkflow);

				}
				else {
					LOGGER.warn("A collected instance (" + instanceOfWorkflow.get_title()
							+ ") has an UUID of the description file path that doesn't correspond to a file ("
							+ instanceOfWorkflow.get_workflowFilePath() + " - "
							+ instanceOfWorkflow.get_workflowFileUuid() + ")");
				}

			}
			catch (NumberFormatException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (CmsException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (IOException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
		}

	}

	/**
	 * Create the file with list of available workflows so as to load OSWorkflow.
	 * 
	 * @return the filepath of the generated file
	 * @throws IOException
	 *             if an error occurs during the write of the generated file
	 */
	private String createWorkflowsListFile() throws IOException {

		// get the list of available workflows
		I_AvailableWorkflowCollector availableWorkflowCollector = AvailableWorkflowCollectorFactory.getInstance();
		List<DescriptionContainer> collectedWorkflow = availableWorkflowCollector.getListOfAvailableWorkflows();

		// check result size
		if (collectedWorkflow.size() == 0) {
			LOGGER
					.warn("There is no workflow available. Please create a file with type \"workflowlist\" and publish it.");
		}

		// initialize filecontent
		String fileContent = "<workflows>";

		// generate file content
		Iterator<DescriptionContainer> collectedWorkflowIterator = collectedWorkflow.iterator();
		while (collectedWorkflowIterator.hasNext()) {
			DescriptionContainer instance = collectedWorkflowIterator.next();
			if (instance.get_realFilePathOfDescriptionFile() != null) {
				File realFile = new File(instance.get_realFilePathOfDescriptionFile());

				fileContent += "\n\t<workflow name=\"" + instance.get_title() + "\" type=\"resource\" location=\""
						+ realFile.getName() + "\"/>";
			}
			else {
				LOGGER.warn("The Workflow " + instance.get_title() + "has no associated RFS filepath.");
			}
		}

		// end file content
		fileContent += "\n</workflows>";

		// write file content
		String generatedFilePath = this.getRepositoryPath() + ModuleSharedVariables.SYSTEM_FILE_SEPARATOR
				+ WORKFLOWLIST_FILENAME;
		FileWriter.writeFile(generatedFilePath, fileContent);

		// return the path of the generated file
		return generatedFilePath;
	}

	/**
	 * Get the OpenCMS RFS OSWorkflow configuration file and update it with the given filepath.
	 * 
	 * @param listOfWorkflowsFilepath
	 *            the path of the file containing the list of available workflow descriptions
	 * @return the path in RFS of the updated file
	 * @throws DocumentException
	 *             this exception is thrown if an error occurs during the parsing of the document
	 * @throws IOException
	 *             this exception is thrown if a problem occurs during overwriting of the config file
	 */
	private String updateOSWorkflowConfigFile(String listOfWorkflowsFilepath) throws CmsException, DocumentException,
			IOException {

		// get file path
		String configFilePath = this.getWebINFPath() + ModuleSharedVariables.SYSTEM_FILE_SEPARATOR
				+ OSWORKFLOWCONFIGFILE_RFSFILEPATH;

		File listOfWorkflowsFile = new File(listOfWorkflowsFilepath);
		// Load Jdom parser
		SAXReader reader = new SAXReader();
		Document document = reader.read(configFilePath);
		Node propertyNode = document.selectSingleNode(OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_XPATH);
		if (propertyNode != null) {
			if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
				// convert Node into element
				Element propertyElement = (Element) propertyNode;

				// update the Attribute
				Attribute valueAttribute = propertyElement
						.attribute(OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_ATTRIBUTNAME);

				valueAttribute.setValue(listOfWorkflowsFile.toURI().toString());

			}
			else {
				LOGGER.debug("the node with Xpath " + OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_XPATH + " in the file "
						+ configFilePath + " doesn't correspond to an element");
			}

		}
		else {
			Node parentNode = document.selectSingleNode(OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_PARENT_XPATH);
			if (parentNode != null) {

				if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
					// convert Node into element
					Element parentElement = (Element) parentNode;

					// add new property
					Element propertyElement = parentElement.addElement("property");

					// add attributs
					propertyElement.addAttribute("key", "resource");
					propertyElement.addAttribute("value", listOfWorkflowsFile.toURI().toString());

				}
				else {
					LOGGER.debug("the node with Xpath " + OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_XPATH + " in the file "
							+ configFilePath + " doesn't correspond to an element");
				}

			}
			else {
				LOGGER.debug("the node with Xpath " + OSWORKFLOWCONFIGFILE_PROPERTYTOUPDATE_PARENT_XPATH
						+ " in the file " + configFilePath + " has not been found.");
			}
		}

		/*
		 * Get a string of the resulting file
		 */

		// creating of a buffer that will collect result
		ByteArrayOutputStream xmlContent = new ByteArrayOutputStream();

		// Pretty print the document to xmlContent
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(xmlContent, format);
		writer.write(document);
		writer.flush();
		writer.close();

		// get the config file content as a String
		String documentContent = new String(xmlContent.toByteArray());

		/*
		 * Overwrite the config file
		 */
		FileWriter.writeFile(configFilePath, documentContent);

		return configFilePath;
	}

	/**
	 * Get the RFS path of the repository where will be stored all file required to load OSWorkflow. This path is
	 * calculated relativly to the OpenCMS package filepath (RFS).
	 * 
	 * @return the formated repository path
	 */
	private String getRepositoryPath() {

		return this.getWebINFPath() + ModuleSharedVariables.SYSTEM_FILE_SEPARATOR + WORKFLOWLIST_REPOSITORYPATH;
	}

	/**
	 * Get the RFS path of the repository WEB-INF of the current Web-App
	 * 
	 * @return the formated repository path
	 */
	private String getWebINFPath() {

		String rfsPackageFilepath = OpenCms.getSystemInfo().getPackagesRfsPath();
		File packageRepository = new File(rfsPackageFilepath);
		String webInfPath = packageRepository.getParent();

		return webInfPath;
	}

	/**
	 * OSWorkflow required a user so as to create instance of workflows. But we doesn't want use the OSWorkflow User
	 * system. So, a default user will be created and use to manage workflows.
	 * 
	 * @throws ImmutableException
	 *             this error can be thrown if an error occurs during the creation of the default user
	 * @throws DuplicateEntityException
	 *             this error can be thrown if an error occurs during the creation of the default user (it already
	 *             exists)
	 */
	protected void createDefaultUser() throws DuplicateEntityException, ImmutableException {

		// get OSWorkflow User manager
		UserManager um = UserManager.getInstance();

		if (um != null) {
			User newuser = null;
			try {
				// try to access the default user
				um.getUser(OSWORKFLOW_DEFAULTUSERNAME);
			}
			catch (EntityNotFoundException e) {
				// if this user doesn't exist, then create it
				newuser = um.createUser(OSWORKFLOW_DEFAULTUSERNAME);
				newuser.setPassword(OSWORKFLOW_DEFAULTUSERNAME);
			}

		}
		else {
			LOGGER.warn("The UserManager of OSWorkflow cannot be acceded");
		}

	}

	/**
	 * The list of parameters of a workflow
	 * 
	 * @param workflowKey
	 *            The key of the workflow
	 * @return the List of parameters corresponding to this workflow, <i>Empty list</i> if the workflow has no
	 *         parameters
	 */
	public List<Parameter> getListOfParameters(WorkflowKey workflowKey) {
		return this._workflowConfigManager.getListOfParameters(workflowKey);
	}

	/**
	 * Get the title of a stored workflow
	 * 
	 * @param workflowKey
	 *            the key of the workflow
	 * @return the title of the workflow corresponding to this key
	 */
	public String getTitleOfWorkflow(WorkflowKey workflowKey) {
		return AvailableWorkflowCollectorFactory.getInstance().get_mapOfAvailableWorkflows().get(workflowKey)
				.get_title();
	}

	/**
	 * The map of the workflows loaded by OSWorkflows, filtered accordint to the user rights
	 * 
	 * @param cmsRequestContext
	 *            The context of the request so as to get an access to current user
	 * @param pathFile
	 *            path of the selected file/folder
	 * @return the map of available workflows, <b>Empty map</b> if a problem occurs (see log for details)
	 */
	public synchronized Map<WorkflowKey, DescriptionContainer> getMapOfAccessibleWorkflows(
			CmsRequestContext cmsRequestContext, List<String> fileList) {
		I_AvailableWorkflowCollector collector = AvailableWorkflowCollectorFactory.getInstance();

		// get the map of all workflows
		Map<WorkflowKey, DescriptionContainer> availableWorkflows = collector.get_mapOfAvailableWorkflows();

		try {
			// get the list of loaded workflows
			String[] arrayOfLoadedWorkflows = _osWorkflowConfig.getWorkflowNames();

			// Put the loaded workflows in the list of available workflows
			Map<WorkflowKey, DescriptionContainer> results = new HashMap<WorkflowKey, DescriptionContainer>();
			for (int i = 0; i < arrayOfLoadedWorkflows.length; i++) {
				WorkflowKey key = collector.getKeyOfWorkflowWithTitle(arrayOfLoadedWorkflows[i]);
				// check write rights (to get the accessible workflows and
				// create it)
				/*
				 * if (this.hasWriteRightOnWorkflow(key, cmsRequestContext, fileList)) {
				 */
				if (this.hasCreateRightOnWorkflow(key, cmsRequestContext, fileList)) {
					results.put(key, availableWorkflows.get(key));
				}
			}
			return results;
		}
		catch (FactoryException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
			return new HashMap<WorkflowKey, DescriptionContainer>();
		}
	}

	/**
	 * The map of the workflows loaded by OSWorkflows (without any filter)
	 * 
	 * @return the map of available workflows, <b>Empty map</b> if a problem occurs (see log for details)
	 */
	public synchronized Map<WorkflowKey, DescriptionContainer> getMapOfAllAccessibleWorkflows() {
		I_AvailableWorkflowCollector collector = AvailableWorkflowCollectorFactory.getInstance();

		// get the map of all workflows
		Map<WorkflowKey, DescriptionContainer> availableWorkflows = collector.get_mapOfAvailableWorkflows();

		try {
			// get the list of loaded workflows
			if (_osWorkflowConfig != null) {
				String[] arrayOfLoadedWorkflows = _osWorkflowConfig.getWorkflowNames();

				// Put the loaded workflows in the list of available workflows
				Map<WorkflowKey, DescriptionContainer> results = new HashMap<WorkflowKey, DescriptionContainer>();
				for (int i = 0; i < arrayOfLoadedWorkflows.length; i++) {
					WorkflowKey key = collector.getKeyOfWorkflowWithTitle(arrayOfLoadedWorkflows[i]);
					results.put(key, availableWorkflows.get(key));
				}
				return results;
			}
			else {
				LOGGER.debug("WF | The object _osWorflowConfig is null !");
				return new HashMap<WorkflowKey, DescriptionContainer>();
			}

		}
		catch (FactoryException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
			return new HashMap<WorkflowKey, DescriptionContainer>();
		}
	}

	/**
	 * The map of the workflows loaded by OSWorkflows
	 * 
	 * @param cmsRequestContext
	 *            The context of the request so as to get an access to current user
	 * @param pathFile
	 *            path of the selected file/folder
	 * @return the map of available workflows, <b>Empty map</b> if a problem occurs (see log for details)
	 */
	public synchronized Map<WorkflowKey, DescriptionContainer> getMapOfAccessibleWorkflows(
			CmsRequestContext cmsRequestContext, String filePath) {

		// create a list from the single file
		List<String> fileList = new ArrayList<String>();
		fileList.add(filePath);

		// call the method that use a list of String
		return this.getMapOfAccessibleWorkflows(cmsRequestContext, fileList);
	}

	/**
	 * Get the property container associated to an instance of workflow
	 * 
	 * @param workflowInstanceID
	 *            the ID of the instance of workflow
	 * @param workflow
	 *            the workflow object that allows accessing parameters
	 * @return the required container, <b>null</b> if a problem occurs (please see log for details)
	 */
	protected synchronized WorkflowPropertyContainer getPropertyContainer(Workflow workflow, long workflowInstanceID) {
		PropertySet ps = workflow.getPropertySet(workflowInstanceID);
		WorkflowPropertyContainer workflowPropertyContainer = null;
		if (ps != null) {

			try {
				String object = (String) ps.getObject(ModuleSharedVariables.WORKFLOWPROPERTY_VARIABLENAME);
				if (object != null) {
					workflowPropertyContainer = new WorkflowPropertyContainer(object);
				}
				else {
					LOGGER.debug("WF | The object associated to the property of the workflow with ID = "
							+ workflowInstanceID + " is null.");
				}

			}
			catch (Exception e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
		}
		else {
			LOGGER.warn("No property set is associated with the workflow with instanceID = " + workflowInstanceID);
		}
		return workflowPropertyContainer;
	}

	/**
	 * Get the list of files associated to an instance of workflow
	 * 
	 * @param instanceID
	 *            the id of the instance of workflow
	 * @param cmsObject
	 *            the object currently associated to the dialog
	 * @return the list of the path of the associated files, an <i>empty list</i> if a problem occurs or if no files are
	 *         associated
	 */
	public synchronized List<String> getListOfFiles(long instanceID, CmsObject cmsObject) {

		// Create the workflow object that permit to access informations
		Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);

		// get the property container
		WorkflowPropertyContainer wfPropertyContainer = this.getPropertyContainer(workflow, instanceID);

		List<String> listOfFiles = null;

		// check and return result
		if (wfPropertyContainer != null && wfPropertyContainer.get_listOfAssociatedFile() != null) {
			listOfFiles = wfPropertyContainer.get_listOfAssociatedFile();
		}
		else {
			listOfFiles = new ArrayList<String>();
		}

		return this.filterListOfFilesAccordingToUserRights(listOfFiles, cmsObject);
	}

	/**
	 * Get the list of files where the files that the current user doesn't have access right doesn't appears
	 * 
	 * @param listOfFiles
	 *            the list of file to check
	 * @param cmsObject
	 *            the object currently associated to the dialog
	 * @return the list of files to display according to the current user rights
	 */
	protected abstract List<String> filterListOfFilesAccordingToUserRights(List<String> listOfFiles, CmsObject cmsObject);

	/**
	 * Get the map of ongoing workflows to display with the all associated informations
	 * 
	 * @param cmsObject
	 *            the object currently associated to the dialog
	 * @return the map of all informations to display
	 */
	public synchronized Map<String, WorkflowDisplayContainer> getOngoingWorkflows(CmsObject cmsObject) {

		Map<String, WorkflowDisplayContainer> result = new HashMap<String, WorkflowDisplayContainer>();

		// Get the list of all workflows name available
		try {
			String[] listOfWorkflowNames = _osWorkflowConfig.getWorkflowNames();

			/*
			 * For each workflow name, collect the list of instance and store them in the map of result (a TreeMap, so
			 * the resulting list of workflows will be ordered)
			 */
			Map<Long, Long> mapOfCollectedWorkflows = new TreeMap<Long, Long>();

			for (int i = 0; i < listOfWorkflowNames.length; i++) {
				// prepare the query to get all stored workflows with a
				// given name of workflow
				WorkflowExpressionQuery query = new WorkflowExpressionQuery(new FieldExpression(FieldExpression.NAME, // Check
						// the
						// Name (of
						// the
						// workflow)
						// field
						FieldExpression.ENTRY, // Look in the current
						// steps context
						FieldExpression.EQUALS, // check equality
						listOfWorkflowNames[i])); // the equality

				// Get the result of the request
				List<Long> resultingList = this._osWorkflowConfig.getWorkflowStore().query(query);

				// store the list of result in the map
				Iterator<Long> resultingListIterator = resultingList.iterator();
				while (resultingListIterator.hasNext()) {
					Long osWorkflowInstanceID = resultingListIterator.next();
					mapOfCollectedWorkflows.put(osWorkflowInstanceID, osWorkflowInstanceID);
				}
			}

			// Treat the list of oSWorkflow so as to collect the required
			// information to display
			Iterator<Long> workflowInstanceIDIterator = mapOfCollectedWorkflows.values().iterator();
			Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);
			while (workflowInstanceIDIterator.hasNext()) {
				Long instanceID = workflowInstanceIDIterator.next();
				WorkflowPropertyContainer propertyContainer = this.getPropertyContainer(workflow, instanceID);

				if (propertyContainer != null) {
					// check if the current user is the owner
					if (this.checkOwner(propertyContainer, cmsObject.getRequestContext().currentUser().getName())) {
						WorkflowDisplayContainer displayContainer = this.collectInformationOfInstanceID(instanceID
								.longValue(), false, cmsObject);
						result.put(instanceID.toString(), displayContainer);
					}
					else {
						if (this.hasReadRightOnWorkflow(new WorkflowKey(workflow.getWorkflowName(instanceID), true),
								cmsObject.getRequestContext(), propertyContainer)) {
							WorkflowDisplayContainer displayContainer = this.collectInformationOfInstanceID(instanceID
									.longValue(), false, cmsObject);
							result.put(instanceID.toString(), displayContainer);
						}
						else {
							LOGGER.info("The user " + cmsObject.getRequestContext().currentUser().getName()
									+ " has no read rights on workflow with ID = " + instanceID + " ("
									+ workflow.getWorkflowName(instanceID) + ")");
						}
					}
				}
				else {
					/*
					 * LOGGER .debug("WF | The propertyContainer associated to the instance " + instanceID + " is
					 * null.");
					 */
				}
			}

		}
		catch (StoreException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (FactoryException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}

		return result;
	}

	/**
	 * Collect the data that need to be displayed
	 * 
	 * @param instanceID
	 *            the Id if the instance of workflow
	 * @param collectAll
	 *            if this parameter is set to <i>true</i> then all historic steps, actions and labels are collected.
	 * @param cmsObject
	 *            the object currently associated to the dialog
	 * @return a container with all the required data
	 */
	public synchronized WorkflowDisplayContainer collectInformationOfInstanceID(long instanceID, boolean collectAll,
			CmsObject cmsObject) {

		// Create the result object
		WorkflowDisplayContainer result = new WorkflowDisplayContainer();
		result.set_instanceID(instanceID);

		// Create the workflow object that permit to access informations
		Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);

		// set the name of the workflow
		String workflowName = workflow.getWorkflowName(instanceID);
		result.set_workflowName(workflowName);

		// get the workflow descriptor associated to this workflow and the
		// property set container
		WorkflowDescriptor wd = workflow.getWorkflowDescriptor(workflowName);
		WorkflowPropertyContainer workflowPropertyContainer = this.getPropertyContainer(workflow, instanceID);

		/*
		 * Set the property store in the workflowPropertyContainer
		 */
		if (workflowPropertyContainer != null) {

			// set the owner (= creator)
			result.set_creator(workflowPropertyContainer.getCreator());

			// set the instance name
			result.set_instanceName(workflowPropertyContainer.get_workflowInstanceName());

			// Check that the user has read right on following informations
			// or is the owner of this workflow
			if (this.checkOwner(workflowPropertyContainer, cmsObject.getRequestContext().currentUser().getName())
					|| this.hasReadRightOnWorkflow(new WorkflowKey(workflowName, true), cmsObject.getRequestContext(),
							workflowPropertyContainer)) {

				// filter the list of file to not display unallowed file
				List<String> filteredFiles = this.getListOfFiles(instanceID, cmsObject);

				// set the list of parameters
				result.set_parameters(workflowPropertyContainer.get_parameters());

				// set list of selected files
				result.set_listOfFiles(filteredFiles);

				// set the number of files
				result.set_numberOfFiles(workflowPropertyContainer.get_listOfAssociatedFile().size());

				// get the number of actions done
				int indexOfLastActions = workflowPropertyContainer.getNumberOfActions() - 1;

				if (indexOfLastActions >= 0) {
					// set the last action informations
					result.set_lastActionComment(workflowPropertyContainer.getComment(indexOfLastActions));
					result.set_lastActionOwner(workflowPropertyContainer.getOwner(indexOfLastActions));
					result.set_lastActionName(workflowPropertyContainer.getActionName(indexOfLastActions));
				}

				/*
				 * Set the state of the workflow and the list of available actions
				 */
				// get the list of available actions
				// => Check if the user has rights.
				List<Integer> availablesActionsList = this.getAvailableActionsForUser(workflow, instanceID, cmsObject
						.getRequestContext(), workflowPropertyContainer);

				// if the user have write rights
				if (availablesActionsList != null) {

					// Collect the name of the following steps
					String[] followingStepNames = new String[availablesActionsList.size()];
					int[] availablesActions = new int[availablesActionsList.size()];
					for (int i = 0; i < availablesActionsList.size(); i++) {
						availablesActions[i] = availablesActionsList.get(i).intValue();
						//try to get the view text of action. If none, get the name of the action
						followingStepNames[i] = wd.getAction(availablesActions[i]).getView();
						if(!StringChecker.isNotNullOrEmpty(followingStepNames[i])){
							followingStepNames[i] = wd.getAction(availablesActions[i]).getName();
						}
					}
					result.set_followingStepsNames(followingStepNames);
					result.set_followingSteps(availablesActions);
				}
				else {
					result.set_followingStepsNames(new String[0]);
					result.set_followingSteps(new int[0]);
					result.set_isWriteAccessDeny(true);

				}

				// set if the workflow is ended or not (if there is available
				// action or
				// not)
				result.set_isEnded(workflow.getAvailableActions(instanceID, new HashMap()).length == 0 ? true : false);

				/*
				 * Set the list of historic
				 */
				if (collectAll) {
					// int stepIndex = 0;
					//
					// /*
					// * Get all steps
					// */
					// List<Step> historicSteps = workflow
					// .getHistorySteps(instanceID);
					// //
					// historicSteps.addAll(workflow.getCurrentSteps(instanceID));
					//
					// /*
					// * Fill each Historical element
					// */
					// Iterator<Step> historicStepsIterator = historicSteps
					// .iterator();
					// while (historicStepsIterator.hasNext()) {
					// Step step = historicStepsIterator.next();
					//
					// // store HistoricalElement
					// result.addHistoricalElement(this
					// .collectHistoricElement(step, wd,
					// workflowPropertyContainer, stepIndex,
					// instanceID));
					// stepIndex++;
					// }

					result.set_historic(this.collectAllHistoricElement(wd, workflowPropertyContainer, instanceID));

				}

			}
			else {
				/*
				 * LOGGER.info("The user " + cmsObject.getRequestContext().currentUser().getName() + " has no read right
				 * on workflow " + workflowName);
				 */
				String noReadAccess = Messages.getMessages(Messages.GUI_ERROR_READRIGHTDENY_0, cmsObject
						.getRequestContext().getLocale());
				result.set_lastActionName(noReadAccess);
				result.set_creator(noReadAccess);
				result.set_lastActionComment(noReadAccess);

			}

		}
		else {
			LOGGER.warn("No property " + ModuleSharedVariables.WORKFLOWPROPERTY_VARIABLENAME
					+ " has been stored for the workflow with ID " + instanceID);
		}

		return result;
	}

	/**
	 * Collect all the required data for an Historic Element
	 * 
	 * @param wd
	 *            the current workflow descriptor
	 * @param workflowPropertyContainer
	 *            the current workflow property container
	 * @param instanceID
	 *            the id of the current instance of workflow
	 * @return the list of HistoricElement that could be displayed
	 */
	private synchronized List<HistoricElement> collectAllHistoricElement(WorkflowDescriptor wd,
			WorkflowPropertyContainer workflowPropertyContainer, long instanceID) {

		List<HistoricElement> result = new ArrayList<HistoricElement>();
		List<WorkflowPropertyContainerElement> historicElements = workflowPropertyContainer.get_listOfElements();

		if (historicElements != null) {

			/*
			 * Create HistoricalElement object from workflowPropertyContainerElement
			 */
			Iterator<WorkflowPropertyContainerElement> historicElementsIterator = historicElements.iterator();
			while (historicElementsIterator.hasNext()) {
				WorkflowPropertyContainerElement containerElement = historicElementsIterator.next();

				// create the object that will store the collected Element
				HistoricElement historicElement = new HistoricElement();

				// set Owner
				String owner = containerElement.get_cmsUserUUID();
				historicElement.set_owner(owner == null ? OSWORKFLOW_AUTOACTION_OWNER : owner);

				// set Comment
				String comment = containerElement.get_comment();
				historicElement.set_comment(comment == null ? OSWORKFLOW_AUTOACTION_COMMENT : comment);

				// set last action name(LAN). If the LAN is
				// null, then
				// use the stepName
				String lan = containerElement.get_actionName();
				historicElement.set_lastActionName(lan == null ? historicElement.get_stepName() : lan);

				// set isAutomatic
				historicElement.set_isAutomatic(containerElement.is_isAutoExecuted());

				// set date
				historicElement.set_actionDate(new Date(containerElement.get_actionDate()));

				// store the element in the list of result
				result.add(historicElement);

			}
		}

		return result;

	}

	// /**
	// * Collect all the required data for an Historic Element
	// *
	// * @param step
	// * The step of the workflow corresponding to this element
	// * @param wd
	// * the current workflow descriptor
	// * @param workflowPropertyContainer
	// * the current workflow property container
	// * @param stepIndex
	// * the index of the step
	// * @param instanceID
	// * the id of the current instance of workflow
	// * @return an HistoricElement that could be displayed
	// */
	// private synchronized HistoricElement collectHistoricElement(Step step,
	// WorkflowDescriptor wd,
	// WorkflowPropertyContainer workflowPropertyContainer, int stepIndex,
	// long instanceID) {
	//
	// HistoricElement historicElement = new HistoricElement();
	//
	// // Store step name
	// ActionDescriptor actionDescriptor = wd.getAction(step.getActionId());
	// if (actionDescriptor != null) {
	// historicElement.set_stepName(actionDescriptor.getName());
	// }
	//
	// // store dates
	// historicElement.set_startingDate(step.getStartDate());
	// historicElement.set_endingDate(step.getFinishDate());
	//
	// // store status
	// historicElement.set_status(step.getStatus());
	//		
	// //st
	//
	// // store owner and comment
	// if (workflowPropertyContainer != null) {
	// try {
	//
	// // set Owner
	// String owner = workflowPropertyContainer.getOwner(stepIndex);
	// historicElement
	// .set_owner(owner == null ? OSWORKFLOW_AUTOACTION_OWNER
	// : owner);
	//
	// // set Comment
	// String comment = workflowPropertyContainer
	// .getComment(stepIndex);
	// historicElement
	// .set_comment(comment == null ? OSWORKFLOW_AUTOACTION_COMMENT
	// : comment);
	//
	// // set last action name(LAN). If the LAN is
	// // null, then
	// // use the stepName
	// String lan = workflowPropertyContainer.getActionName(stepIndex);
	// historicElement
	// .set_lastActionName(lan == null ? historicElement
	// .get_stepName() : lan);
	// } catch (IndexOutOfBoundsException e) {
	// LOGGER
	// .warn("Try to access element "
	// + stepIndex
	// + " in the workfowPropertyContainer associated to the workflow with
	// instance "
	// + instanceID + ", but this index doesn't exist");
	// }
	// }
	//
	// return historicElement;
	// }

	/**
	 * Get the list of action that the current user is allowed to do
	 * 
	 * @param workflow
	 *            the workflow object (to access instance informations)
	 * @param instanceID
	 *            the workflow instance ID
	 * @param cmsRequestContext
	 *            The context of the request so as to get an access to current user
	 * @param propertyContainer
	 *            the property container of the instance of workflow
	 * @return <ul>
	 *         <li>list of available actions</li>
	 *         <li><i>empty list</i> if there is no available workflow</li>
	 *         <li><b>null</b> if the user has no write rights</li>
	 *         </ul>
	 */
	protected synchronized List<Integer> getAvailableActionsForUser(Workflow workflow, long instanceID,
			CmsRequestContext cmsRequestContext, WorkflowPropertyContainer propertyContainer) {

		// get all available actions for the instance of workflow
		int[] allAvailableActions = workflow.getAvailableActions(instanceID, new HashMap());

		// Create the list of result
		List<Integer> result = new ArrayList<Integer>();

		// if the user has write rights then add available actions else return
		// null
		if (this.hasWriteRightOnWorkflow(new WorkflowKey(workflow.getWorkflowName(instanceID), true),
				cmsRequestContext, propertyContainer)) {
			// copy available actions in the list
			for (int i = 0; i < allAvailableActions.length; i++) {
				result.add(new Integer(allAvailableActions[i]));
			}
			return result;
		}
		else {
			return null;
		}
	}

	/**
	 * Execute an action on the given workflow
	 * 
	 * @param actionID
	 *            the ID of the action
	 * @param userName
	 *            the name of the user that will execute the action
	 * @param workflowID
	 *            the instance of the workflow on which the action must be executed
	 * @param comment
	 *            the associated comment
	 * @param cmsObject
	 *            the cmsObject that can be required for functions during execution
	 */
	public synchronized void executeAction(int actionID, String userName, long workflowID, String comment,
			CmsObject cmsObject) {

		// Create the workflow object that permit to access informations
		Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);

		// get the workflowName
		String workflowName = workflow.getWorkflowName(workflowID);

		try {
			/*
			 * update the property
			 */
			// get the workflow descriptor associated to this workflow and
			// the property set container
			WorkflowPropertyContainer workflowPropertyContainer = this.getPropertyContainer(workflow, workflowID);

			if (workflowPropertyContainer != null) {
				/*
				 * Execute the action
				 */
				List<String> listOfFiles = null;
				List<Parameter> initialParameters = null;
				if (workflowPropertyContainer != null) {
					// Get the list of files associated to the instance so as to
					// give them as parameter of functions that must be executed
					listOfFiles = workflowPropertyContainer.get_listOfAssociatedFile();
					// also get the list of initial parameters
					initialParameters = workflowPropertyContainer.get_parameters();
				}
				else {
					listOfFiles = new ArrayList<String>();
					initialParameters = new ArrayList<Parameter>();
				}

				// create the map of inputs use for the different functions
				Map<String, Object> inputs = new HashMap<String, Object>();
				inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST, listOfFiles);
				inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_CMSOBJECT, cmsObject);
				inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_INITIALPARAMETERS, initialParameters);
				inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_COMMENT, comment);

				// Check if the user as write right before doing operation
				if (this.hasWriteRightOnWorkflow(new WorkflowKey(workflowName, true), cmsObject.getRequestContext(),
						workflowPropertyContainer)) {
					// execute the action
					workflow.doAction(workflowID, actionID, inputs);
				}
				else {
					LOGGER.info("The user " + cmsObject.getRequestContext().currentUser().getName()
							+ " cannot execute action on instance of " + workflowName);
				}
			}
			else {
				LOGGER.debug("WF | the WorkflowPropertyContainer associated to workflow with instance " + workflowID
						+ " is null.");
			}

		}
		catch (InvalidInputException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (WorkflowException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}

	}

	/**
	 * Delete an instance of workflow (ended)<br>
	 * <font color="red"><b><u>WARNING: </u></b></font> This function is not implemented (delete is not a basic function
	 * of OSWorkflow)
	 * 
	 * @param cms
	 *            the cmsObject that can be required for functions during initialization
	 * @param workflowID
	 *            the ID of the workflow to delete
	 */
	public synchronized void deleteWorkflow(CmsObject cmsObject, long workflowID) {
		// Create the workflow object that permit to access informations
		Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);

		// get the workflowName
		String workflowName = workflow.getWorkflowName(workflowID);

		// get the workflow descriptor associated to this workflow and
		// the property set container
		WorkflowPropertyContainer workflowPropertyContainer = this.getPropertyContainer(workflow, workflowID);

		if (workflowPropertyContainer != null) {

			// Check if the user as write right before doing operation
			if (this.hasWriteRightOnWorkflow(new WorkflowKey(workflowName, true), cmsObject.getRequestContext(),
					workflowPropertyContainer)) {

				// TODO Delete an instance of Workflow !

			}
			else {
				LOGGER.info("The user " + cmsObject.getRequestContext().currentUser().getName()
						+ " cannot execute action on instance of " + workflowName);
			}
		}
		else {
			LOGGER.debug("WF | the WorkflowPropertyContainer associated to workflow with instance " + workflowID
					+ " is null.");
		}

	}

	/**
	 * This method create and initialize the new instance of workflow
	 * 
	 * @param instanceWorkflowName
	 *            the name associated to this instance of workflow
	 * @param workflowName
	 *            the name of the workflow to create
	 * @param openCmsUserUUID
	 *            the UUID of the OpenCMS User that required the workflow creation
	 * @param comment
	 *            the comment associated to this creation
	 * @param listOfFiles
	 *            the list of resources associated to this instance of workflow
	 * @param cmsObject
	 *            the cmsObject that can be required for functions during initialization
	 * @param parameters
	 *            The list of initial parameters of the request
	 */
	public synchronized void createInstanceOfWorkflow(String workflowName, String instanceWorkflowName,
			String openCmsUserUUID, String comment, List<String> listOfFiles, CmsObject cmsObject,
			List<Parameter> parameters) {

		// check if the user has write right
		/*
		 * if (this.hasWriteRightOnWorkflow(new WorkflowKey(workflowName, true), cmsObject.getRequestContext(),
		 * listOfFiles)) {
		 */
		if (this.hasCreateRightOnWorkflow(new WorkflowKey(workflowName, true), cmsObject.getRequestContext(),
				listOfFiles)) {

			// Create map of inputs for initialization
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST, listOfFiles);
			inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_CMSOBJECT, cmsObject);

			inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_INITIALPARAMETERS, parameters);

			inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_COMMENT, comment);

			inputs.put(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_INSTANCENAME, instanceWorkflowName);

			Workflow workflow = new BasicWorkflow(OSWORKFLOW_DEFAULTUSERNAME);
			try {

				// initialize the workflow
				workflow.initialize(workflowName, 1, inputs);

			}
			catch (InvalidActionException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (InvalidRoleException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (InvalidInputException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (InvalidEntryStateException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
			catch (WorkflowException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
		}
		else {
			LOGGER.info("The user " + cmsObject.getRequestContext().currentUser().getName()
					+ " cannot create instance of " + workflowName);
		}
	}

	/**
	 * Get the parent path of a list of file. It is assumed in this method that all files are in the same repository.
	 * 
	 * @param listOfFiles
	 *            The list of files to get the parent folder of
	 * @return the list of parent folder of the list of files, <b>empty list</b> if the list of files is empty.
	 */
	protected List<String> getParentPath(List<String> listOfFiles) {

		// the list of result Doublon will be avoid
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.size(); i++) {
			String firstPath = listOfFiles.get(i);
			if (firstPath != null) {
				String parentPath = this.getParentPath(firstPath);
				if (parentPath != null && !result.contains(parentPath)) {
					result.add(parentPath);
				}
			}
		}
		return result;
	}

	/**
	 * Get the parent path of a file.
	 * 
	 * @param filePath
	 *            The file to get the parent folder of
	 * @return the parent folder of the file.
	 */
	protected String getParentPath(String filePath) {
		String parentPath = CmsResource.getParentFolder(filePath);
		
		// if the given file parent is "/", the getParentFolder method return
		// null
		return (parentPath == null) ? "/" : parentPath;
	}

	/**
	 * Check if the current user has right to see this workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @param cmsRequestContext
	 *            the request context
	 * @param propertyContainer
	 *            the property container of the instance of workflow
	 * @return <i>true</i> if the user is allow to see the workflow, <i>false</i> otherwise.
	 */
	protected boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			WorkflowPropertyContainer propertyContainer) {

		List<String> initiallySelectedFiles = propertyContainer.getListOfInitiallyAssociatedFiles();
		List<String> parentFolders = this.getParentPath(initiallySelectedFiles);
		if (parentFolders == null || parentFolders.isEmpty())
			return this.hasReadRightOnWorkflow(key, cmsRequestContext, NO_FILE_PATH, propertyContainer);
		if (parentFolders != null && key != null && cmsRequestContext.currentUser() != null) {
			boolean result = true;
			for (int i = 0; i < parentFolders.size(); i++) {
				result &= this.hasReadRightOnWorkflow(key, cmsRequestContext, parentFolders.get(i), propertyContainer);
				if (!result) {
					break;
				}
			}
			return result;
		}
		else {
			LOGGER.debug("WF | A blocking null value has been found : \n\tkey = " + key + "\n\tuser = "
					+ cmsRequestContext.currentUser() + "\n"
					+ ErrorFormatter.formatList(parentFolders, "parentFolders"));
			return false;
		}

	}

	/**
	 * Check if the current user has right to see this workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @param cmsRequestContext
	 *            the request context
	 * @param propertyContainer
	 *            the property container of the instance of workflow
	 * @return <i>true</i> if the user is allow to see the workflow, <i>false</i> otherwise.
	 */
	protected boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			WorkflowPropertyContainer propertyContainer) {
		List<String> initiallySelectedFiles = propertyContainer.getListOfInitiallyAssociatedFiles();
		List<String> parentFolders = this.getParentPath(initiallySelectedFiles);
		if (parentFolders == null || parentFolders.isEmpty())
			return this.hasWriteRightOnWorkflow(key, cmsRequestContext, NO_FILE_PATH, propertyContainer);
		if (parentFolders != null && key != null && cmsRequestContext.currentUser() != null) {
			boolean result = true;
			for (int i = 0; i < parentFolders.size(); i++) {
				result &= this.hasWriteRightOnWorkflow(key, cmsRequestContext, parentFolders.get(i), propertyContainer);
				if (!result) {
					break;
				}
			}
			return result;
		}
		else {
			LOGGER.debug("WF | A blocking null value has been found : \n\tkey = " + key + "\n\tuser = "
					+ cmsRequestContext.currentUser() + "\n"
					+ ErrorFormatter.formatList(parentFolders, "parentFolders"));
			return false;
		}
	}

	/**
	 * Check if the current user has right to see this workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @param cmsRequestContext
	 *            the request context
	 * @param parentFolder
	 *            the path of file
	 * @param propertyContainer
	 *            the property container of the instance of workflow
	 * @return <i>true</i> if the user is allow to see the workflow, <i>false</i> otherwise.
	 */
	protected abstract boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer);

	/**
	 * Check if the current user has right to see this workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @param cmsRequestContext
	 *            the request context
	 * @param parentFolder
	 *            the path of file
	 * @param propertyContainer
	 *            the property container of the instance of workflow
	 * @return <i>true</i> if the user is allow to see the workflow, <i>false</i> otherwise.
	 */
	protected abstract boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer);

	/**
	 * Check that the current user is the owner of the workplace. If this method return true, the own workflows of the
	 * creator will be displayed whatever its rights.
	 * 
	 * @param propertyContainer
	 *            the container of the property of the workflow
	 * @param name
	 *            the name of the current user
	 * @return <i>true</i> if the user is the owner of the instance of workflow, <i>false</i> otherwise
	 */
	protected abstract boolean checkOwner(WorkflowPropertyContainer propertyContainer, String name);

	/**
	 * Check if the current user has right to create this workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @param cmsRequestContext
	 *            the request context
	 * @param listOfFiles
	 *            the list of initially selected files
	 * @return <i>true</i> if the user is allow to see the workflow, <i>false</i> otherwise.
	 */
	protected abstract boolean hasCreateRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			List<String> fileList);

}