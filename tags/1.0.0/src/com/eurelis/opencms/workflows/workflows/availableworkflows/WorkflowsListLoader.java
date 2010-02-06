/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows.workflows.availableworkflows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.opencms.file.CmsResource;
import org.opencms.loader.CmsLoaderException;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;

import com.eurelis.opencms.workflows.jaxb.listworkflow.Instance;
import com.eurelis.opencms.workflows.jaxb.listworkflow.ListWorkflowTypes;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.workflows.Messages;

/**
 * @author   Sébastien Bianco
 */
public class WorkflowsListLoader {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(WorkflowsListLoader.class);

	/**
	 * The map of association between the workflow name and the workflow description
	 * @uml.property  name="_mapOfDescription"
	 */
	private Map<String, String> _mapOfDescription = null;	

	/**
	 * Initialise the map by reading and parsing the _Description_File
	 * 
	 */
	public WorkflowsListLoader() {
		// initialise the map by parsing the xml file;
		try {
			this._mapOfDescription = this.loadFile();
		} catch (IOException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (CmsLoaderException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (Exception e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}

	}

	/**
	 * Launch the search of all files of type "workflowlist" and parse them
	 * @return an empty map
	 * @throws IOException if an error occurs during the read of a file
	 * @throws CmsException if another error occurs2
	 */
	private synchronized Map<String, String> loadFile() throws IOException, CmsException {
		// create resulting map
		Map<String, String> result = new HashMap<String, String>();

		// initialize list that will store all file that contains description of
		// workflows
		List<CmsResource> listOfCollectedFiles = null;

		// get the id of the required type of files
		int typeID = OpenCms.getResourceManager().getResourceType(
				ModuleConfigurationLoader.getConfiguration().WORKLFOWLIST_TYPE)
				.getTypeId();

		// go through the entire VFS (system part) so load file with type
		// "WorkflowList" from the startingSearchFolder
		String startingSearchFolder = ModuleConfigurationLoader
				.getConfiguration().WORKFLOWLIST_CONFIGFILE_FOLDER.trim();
		CmsResource resource = OpenCmsEasyAccess
				.getResource(startingSearchFolder);
		if (resource != null && resource.isFolder()) {
			listOfCollectedFiles = this.workflowListFileCollector(
					startingSearchFolder, typeID);
		} else {
			LOGGER
					.warn("The value of configuration constant WORKFLOWLIST_CONFIGFILE_FOLDER ("
							+ startingSearchFolder
							+ ") is incorrect (not found or not a folder). The look up of all \"workflowlist\" list files will be start from the root of the VFS");
			listOfCollectedFiles = this.workflowListFileCollector("/", typeID);
		}

		// read and parse all collected files
		this.parseWorkflowListFiles(listOfCollectedFiles);

		// return the resulting map
		return result;
	}

	/**
	 * Read all the resources (corresponding to "workflowlist" files) and parse
	 * the embedded informations
	 * 
	 * @param listOfCollectedFiles
	 *            the list of resources corresponding to "workflowlist" files
	 */
	private void parseWorkflowListFiles(
			final List<CmsResource> listOfCollectedFiles) {

		/*
		 * Get one file after the other and treat it
		 */
		Iterator<CmsResource> listOfCollectedFileIterator = listOfCollectedFiles
				.iterator();
		while (listOfCollectedFileIterator.hasNext()) {
			try {
				this.parseFile(listOfCollectedFileIterator.next());
			} catch (DocumentException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			} catch (CmsException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			} catch (JAXBException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
		}

	}

	/**
	 * Parse the file given by the {@link CmsResource} object and get all stored
	 * informations
	 * 
	 * @param resource
	 *            the CmsResource object corresponding to the file to parse
	 * @throws DocumentException
	 *             This exception could be thrown if an error occurs during the
	 *             parsing of the XmlDocument
	 * @throws CmsException
	 *             This exception is thrown if an error occurs during the read
	 *             of the CmsFile
	 * @throws JAXBException
	 *             This exception is thrown if an orror occurs with JAXB
	 *             Unmarshaller
	 */
	private void parseFile(CmsResource resource) throws DocumentException,
			CmsException, JAXBException {

		if (resource.isFile()) {
			// Read the file
			byte[] fileContent = OpenCmsEasyAccess.readFile(resource);

			if (fileContent.length > 0) {

				// Load JAXB Context and unmarshaller
				JAXBContext jc = JAXBContext
						.newInstance(ModuleConfigurationLoader
								.getConfiguration().PARSER_JAXBCONTEXT_LISTWORKFLOW);
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				// Unmarshall file and get the list of instances
				List<Instance> instances = ((ListWorkflowTypes) unmarshaller
						.unmarshal(new ByteArrayInputStream(fileContent)))
						.getListWorkflowType().getInstance();

				// Go through all Instance of Workflow
				Iterator<Instance> instanceIterator = instances.iterator();
				while (instanceIterator.hasNext()) {
					Instance instance = instanceIterator.next();

					/*
					 * Collect values
					 */
					String title = null;
					String description = null;
					String workflowDescriptionFileTarget = null;
					String workflowDescriptionFileUUID = null;
					String workflowAllowanceFileTarget = null;
					String workflowAllowanceFileUUID = null;

					// collect title
					try {
						title = instance.getTitle().getContent();
					} catch (NullPointerException e) {
						LOGGER
								.error("The file "
										+ resource.getRootPath()
										+ " content an instance that doesn't have any title.");
						throw new CmsException(Messages.get().container(
								Messages.GUI_ERROR_PARSEINSTANCE_TITLE_1,
								resource.getRootPath()));
					}

					// collect description
					try {
						description = instance.getDescription().getContent()
								.getContent();
					} catch (NullPointerException e) {
						LOGGER
								.warn("The file "
										+ resource.getRootPath()
										+ " contains an instance that doesn't have any description.");
					}

					// collect workflow description file
					try {
						workflowDescriptionFileTarget = instance
								.getWorkflowFile().getLink().getTarget()
								.getContent();
						workflowDescriptionFileUUID = instance
								.getWorkflowFile().getLink().getUuid()
								.getContent();
					} catch (NullPointerException e) {
						LOGGER
								.error("The file "
										+ resource.getRootPath()
										+ " contains an instance that doesn't have a valid description of the link for description file of the workflow.");
						throw new CmsException(
								Messages
										.get()
										.container(
												Messages.GUI_ERROR_PARSEINSTANCE_WORKFLOWDESCRIPTIONFILE_1,
												resource.getRootPath()));
					}

					// collect workflow allowances file
					try {
						workflowAllowanceFileTarget = instance.getRightsFile()
								.getLink().getTarget().getContent();
						workflowAllowanceFileUUID = instance.getRightsFile()
								.getLink().getUuid().getContent();
					} catch (NullPointerException e) {
						LOGGER
								.error("The file "
										+ resource.getRootPath()
										+ " contains an instance that doesn't have a valid description of the link for allowances file of the workflow.");
						throw new CmsException(
								Messages
										.get()
										.container(
												Messages.GUI_ERROR_PARSEINSTANCE_WORKFLOWALLOWANCEFILE_1,
												resource.getRootPath()));
					}

					// Register the read workflows
					AvailableWorkflowCollectorFactory.getInstance()
							.addNewWorkflow(title, description,
									workflowDescriptionFileTarget,
									workflowDescriptionFileUUID,
									workflowAllowanceFileTarget,
									workflowAllowanceFileUUID);

				}// end Iterator of instance

			}// end data in the file
			else {
				LOGGER
						.warn("WF | A CmsResource ("
								+ resource.getRootPath()
								+ ") that content no data has been collected to be parsed !");
			}
		}// end resource is a file
		else {
			LOGGER.warn("WF | A CmsResource (" + resource.getRootPath()
					+ ") that is not a file has been collected to be parsed !");
		}
	}

	/**
	 * This recursive method goes through the VFS to collect Resources with
	 * given type
	 * 
	 * @param parentPath
	 *            the parent path in the OpenCMS VFS
	 * @return the collected CmsResource objects in the VFS part starting with
	 *         parentPath
	 * @param typeID
	 *            the ID of the required type of file
	 * @throws CmsException
	 *             Exception could be throws in case of Security Access trouble
	 */
	private List<CmsResource> workflowListFileCollector(String parentPath,
			int typeID) throws CmsException {
		CmsResource parentResource = OpenCmsEasyAccess.getResource(parentPath);

		// initialise the result set
		List<CmsResource> result = new ArrayList<CmsResource>();

		/* Recursive call in case it is a Folder */
		if (parentResource.isFolder()) {
			List<CmsResource> rootContent = OpenCmsEasyAccess
					.getListOfFile(parentPath);
			Iterator<CmsResource> rootContentIterator = rootContent.iterator();
			while (rootContentIterator.hasNext()) {
				CmsResource childResource = rootContentIterator.next();
				result.addAll(this.workflowListFileCollector(childResource
						.getRootPath(), typeID));
			}
		} else {
			/*
			 * This is a file => If its the correct typeID, add it in the list
			 * of File
			 */
			if (parentResource.getTypeId() == typeID
					&& !result.contains(parentResource)) {
				LOGGER.debug("WF | Add " + parentResource.getRootPath()
						+ " in the list of WorkflowList files");
				result.add(parentResource);
			}
		}

		// return the set of result
		return result;
	}

	/**
	 * Get the map of description of existing workflows
	 * @return   the _mapOfDescription
	 * @uml.property  name="_mapOfDescription"
	 */
	public Map<String, String> get_mapOfDescription() {
		return _mapOfDescription;
	}

}
