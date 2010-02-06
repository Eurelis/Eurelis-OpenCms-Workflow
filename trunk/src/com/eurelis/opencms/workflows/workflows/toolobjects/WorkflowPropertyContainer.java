/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.toolobjects;

import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.AllowedGroup;
import com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.AllowedRole;
import com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.AllowedUser;
import com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.File;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.eurelis.opencms.workflows.workflows.security.AllowedPeople;

/**
 * This class is a container of all information that need to be stored as an OSWorkflow property. <br>
 * <br>
 * For keep more informations about the initial selected files and the other one, a convention has been set for storing
 * associated files:
 * <ul>
 * <li>If the file was initially selected, the path of the file is stored without modification. Use
 * {@link WorkflowPropertyContainer#addInitiallySelectedFile(List)} or
 * {@link WorkflowPropertyContainer#addInitiallySelectedFile(String)} to store those files</li>
 * <li>If the file has been added following any algorithm, a prefix (
 * {@link WorkflowPropertyContainer#PREFIX_FOR_ADDED_FILES} ) is set before the filepath. Use
 * {@link WorkflowPropertyContainer#addAssociatedFile(List)} or
 * {@link WorkflowPropertyContainer#addInitiallySelectedFile(String)} to add some associated files</li>
 * </ul>
 * Anyway, the method {@link WorkflowPropertyContainer#get_listOfAssociatedFile()} returns the list of files without any
 * prefix. <br>
 * To get the list of files initially selected or the list of added files, use respectively the methods
 * {@link WorkflowPropertyContainer#getListOfInitiallyAssociatedFiles()} and
 * {@link WorkflowPropertyContainer#getListOfAddedAssociatedFiles()}
 * 
 * @author Sébastien Bianco
 */
public class WorkflowPropertyContainer implements Serializable {

	/**
	 * Serial Version ID
	 */
	private static final long						serialVersionUID			= 1L;

	/** The log object for this class. */
	static final Logger								LOGGER						= Logger
																						.getLogger(WorkflowPropertyContainer.class);

	/**
	 * The prefix set to the file path added by any algorithms.
	 */
	private static final String						PREFIX_FOR_ADDED_FILES		= "(A)";

	/**
	 * The string that starts the description of a Pattern
	 */
	private static final String						PATTERN_STARTING_SUBSTRING	= ModuleConfigurationLoader
																						.getConfiguration().PATTERN_STARTING_SUBSTRING;

	/**
	 * The string that ends the description of a Pattern
	 */
	private static final String						PATTERN_ENDING_SUBSTRING	= ModuleConfigurationLoader
																						.getConfiguration().PATTERN_ENDING_SUBSTRING;

	/**
	 * The list of files associated to a workflow
	 * 
	 * @uml.property name="_listOfAssociatedFile"
	 */
	private List<String>							_listOfAssociatedFile		= new ArrayList<String>();								;

	/**
	 * The list of Element that will be stored for each step of the workflow (owner and comment)
	 * 
	 * @uml.property name="_listOfElements"
	 */
	private List<WorkflowPropertyContainerElement>	_listOfElements				= new ArrayList<WorkflowPropertyContainerElement>();

	/**
	 * The list of initial Parameters of the workflow
	 * 
	 * @uml.property name="_parameters"
	 */
	private List<Parameter>							_parameters					= new ArrayList<Parameter>();

	/**
	 * The name associated to this instance of workflow
	 * 
	 * @uml.property name="_workflowInstanceName"
	 */
	private String									_workflowInstanceName		= "No name";

	/**
	 * The dynamic rights associated to the instance of workflow
	 */
	private AllowedPeople							_allowedPeople				= null;

	/**
	 * Default constructor. Initialize lists to empty lists
	 */
	public WorkflowPropertyContainer() {}

	/**
	 * Constructor of the element. The list of files is set as initially selected files (so without any prefix)
	 * 
	 * @param listOfFiles
	 *            the list of files to add
	 */
	public WorkflowPropertyContainer(List<String> listOfFiles) {
		this.addInitiallySelectedFile(listOfFiles);
	}

	/**
	 * Construct a new Instance of the object from the data content into the XML
	 * 
	 * @param xmlContent
	 *            the xml description of the object
	 * @throws Exception
	 *             if problem occurs during parse of the property
	 */
	public WorkflowPropertyContainer(String xmlContent) throws Exception {
		// Load JAXB Context and unmarshaller
		JAXBContext jc;

		jc = JAXBContext
				.newInstance(ModuleConfigurationLoader.getConfiguration().PARSER_JAXBCONTEXT_WORKFLOWPROPERTYCONTAINER);

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		// Unmarshall file and get the list of instances
		com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.WorkflowPropertyContainer propertyContainer = (com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.WorkflowPropertyContainer) unmarshaller
				.unmarshal(new StringReader(xmlContent));

		/*
		 * Extract informations
		 */
		if (propertyContainer != null) {

			// get Name
			String name = propertyContainer.getWorkflowInstanceName();
			if (StringChecker.isNotNullOrEmpty(name)) {
				this._workflowInstanceName = name;
			}

			/*
			 * Get Files
			 */
			List<File> storedListOfFiles = propertyContainer.getAssociatedFiles().getFile();
			Iterator<File> storedListOfFilesIterator = storedListOfFiles.iterator();
			while (storedListOfFilesIterator.hasNext()) {
				_listOfAssociatedFile.add(storedListOfFilesIterator.next().getPath());
			}

			/*
			 * Get Elements
			 */
			List<com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.WorkflowPropertyContainerElement> elements = propertyContainer
					.getElements().getWorkflowPropertyContainerElement();
			Iterator<com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.WorkflowPropertyContainerElement> elementsIterator = elements
					.iterator();
			while (elementsIterator.hasNext()) {
				com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.WorkflowPropertyContainerElement element = elementsIterator
						.next();
				boolean isAutoExecuted = false;
				long storedTimeMillis = 0;

				// get the autoexecuted value
				if (StringChecker.isNotNullOrEmpty(element.getIsAutoExecuted())) {
					isAutoExecuted = Boolean.parseBoolean(element.getIsAutoExecuted());
				}
				else {
					LOGGER
							.debug("WF | A false value has been set by default for a workflowContainerElement for isAutoExecuted");
				}

				// get the action date
				if (StringChecker.isNotNullOrEmpty(element.getActionDate())) {
					storedTimeMillis = Long.parseLong(element.getActionDate());
				}
				else {
					LOGGER
							.debug("WF | A 0 value has been set by default for a workflowContainerElement for actionDate");
				}

				// add the collected value to the list of historic elements
				_listOfElements.add(new WorkflowPropertyContainerElement(element.getCmsUserUUID(),
						element.getComment(), element.getActionName(), isAutoExecuted, storedTimeMillis));
			}

			/*
			 * Get initial parameters
			 */
			List<com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.Parameter> storedParameters = propertyContainer
					.getParameters().getParameter();
			Iterator<com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.Parameter> storedParametersIterator = storedParameters
					.iterator();
			while (storedParametersIterator.hasNext()) {
				com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.Parameter parameter = storedParametersIterator
						.next();
				Parameter newParam = new Parameter(parameter.getName(), Integer.parseInt(parameter.getType()),
						parameter.getDisplayName());
				newParam.set_value(parameter.getValue());
				_parameters.add(newParam);
			}

			/*
			 * Get allowed People
			 */
			com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.AllowedPeople storedAllowedPeople = propertyContainer
					.getAllowedPeople();
			AllowedPeople allowed = this.extractAllowedPeople(storedAllowedPeople);
			_allowedPeople = allowed;

		}
		else {
			throw new Exception("The xmlContent has not been parse correctly (No WorkflowPropertyElement found)");
		}
	}

	/**
	 * Extract the information about allowedPeople from the Object corresponding to XML file (fragment AllowedPeople)
	 * 
	 * @param storedAllowedPeople
	 *            the JAXB object allowing to access the information about allowed people
	 * @return an object containing the information about allowed people.
	 */
	private AllowedPeople extractAllowedPeople(
			com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer.AllowedPeople storedAllowedPeople) {

		AllowedPeople allowed = new AllowedPeople();

		if (storedAllowedPeople != null) {

			/*
			 * Get the list of allowed User
			 */
			if (storedAllowedPeople.getAllowedUser() != null) {
				Iterator<AllowedUser> allowedUserIterator = storedAllowedPeople.getAllowedUser().iterator();
				while (allowedUserIterator.hasNext()) {
					AllowedUser allowedUser = allowedUserIterator.next();
					String userName = allowedUser.getName();

					// check if the name correspond to a pattern (start with "{")
					if (userName.startsWith(PATTERN_STARTING_SUBSTRING)) {
						// remove the String that indicates that it is a pattern
						String pattern = userName.substring(PATTERN_STARTING_SUBSTRING.length(), userName.length()
								- PATTERN_ENDING_SUBSTRING.length());

						// add pattern in the list
						allowed.addAllowedUserPattern(pattern, allowedUser.getType());
					}
					else {
						allowed.addAllowedUser(userName, allowedUser.getType());
					}
				}
			}

			/*
			 * Get the list of allowed Group
			 */
			if (storedAllowedPeople.getAllowedGroup() != null) {
				Iterator<AllowedGroup> allowedGroupIterator = storedAllowedPeople.getAllowedGroup().iterator();
				while (allowedGroupIterator.hasNext()) {
					AllowedGroup allowedGroup = allowedGroupIterator.next();
					String groupName = allowedGroup.getName();

					// check if the name correspond to a pattern (start with "{")
					if (groupName.startsWith(PATTERN_STARTING_SUBSTRING)) {
						// remove the String that indicates that it is a pattern
						String pattern = groupName.substring(PATTERN_STARTING_SUBSTRING.length(), groupName.length()
								- PATTERN_ENDING_SUBSTRING.length());

						// add pattern in the list
						allowed.addAllowedGroupPattern(pattern, allowedGroup.getType());
					}
					else {
						allowed.addAllowedGroup(groupName, allowedGroup.getType());
					}
				}
			}

			/*
			 * Get the list of allowed Role
			 */
			if (storedAllowedPeople.getAllowedUser() != null) {
				Iterator<AllowedRole> allowedRoleIterator = storedAllowedPeople.getAllowedRole().iterator();
				while (allowedRoleIterator.hasNext()) {
					AllowedRole allowedRole = allowedRoleIterator.next();
					String roleName = allowedRole.getName();

					// check if the name correspond to a pattern (start with "{")
					if (roleName.startsWith(PATTERN_STARTING_SUBSTRING)) {
						// remove the String that indicates that it is a pattern
						String pattern = roleName.substring(PATTERN_STARTING_SUBSTRING.length(), roleName.length()
								- PATTERN_ENDING_SUBSTRING.length());

						// add pattern in the list
						allowed.addAllowedRolePattern(pattern, allowedRole.getType());
					}
					else {
						allowed.addAllowedRole(roleName, allowedRole.getType());
					}
				}
			}
		}

		return allowed;

	}

	/**
	 * Get the list of all associated files, without considering if the files was initially selected or added later by
	 * an algorithm. This means that files that was previously prefixed are cleaned of it before being return.
	 * 
	 * @return the _listOfAssociatedFile the list of all associated files in a clean way to be displayed/treated
	 * @uml.property name="_listOfAssociatedFile"
	 */
	public synchronized List<String> get_listOfAssociatedFile() {
		List<String> result = new ArrayList<String>();

		/*
		 * Clean file path of there prefix if required
		 */
		Iterator<String> filesIterator = this._listOfAssociatedFile.iterator();
		while (filesIterator.hasNext()) {
			String storedFilePath = filesIterator.next();
			if (storedFilePath != null) {
				// check if there is a prefix
				if (storedFilePath.startsWith(PREFIX_FOR_ADDED_FILES)) {
					// remove prefix
					storedFilePath = storedFilePath.substring(PREFIX_FOR_ADDED_FILES.length());
				}
				// store result
				result.add(storedFilePath);
			}
			else {
				LOGGER.debug("WF | A null value was stored in the list of classpath !!");
			}
		}

		// sort the result
		Collections.sort(result);
		return result;
	}

	/**
	 * @return the _listOfElements
	 * @uml.property name="_listOfElements"
	 */
	public synchronized List<WorkflowPropertyContainerElement> get_listOfElements() {
		return _listOfElements;
	}

	/**
	 * @return the _parameters
	 * @uml.property name="_parameters"
	 */
	public synchronized List<Parameter> get_parameters() {
		return _parameters;
	}

	/**
	 * @param ofElements
	 *            the _listOfElements to set
	 * @uml.property name="_listOfElements"
	 */
	public synchronized void set_listOfElements(List<WorkflowPropertyContainerElement> ofElements) {
		_listOfElements = ofElements;
	}

	/**
	 * @param _parameters
	 *            the _parameters to set
	 * @uml.property name="_parameters"
	 */
	public synchronized void set_parameters(List<Parameter> _parameters) {
		this._parameters = _parameters;
	}

	/**
	 * @return the _workflowInstanceName
	 * @uml.property name="_workflowInstanceName"
	 */
	public synchronized String get_workflowInstanceName() {
		return _workflowInstanceName;
	}

	/**
	 * @param instanceName
	 *            the _workflowInstanceName to set
	 * @uml.property name="_workflowInstanceName"
	 */
	public synchronized void set_workflowInstanceName(String instanceName) {
		_workflowInstanceName = instanceName;
	}

	/**
	 * Add the property due to progression of the associated workflow
	 * 
	 * @param owner
	 *            the user UUID that makes the workflow evolve
	 * @param comment
	 *            the comment associated to the evolution
	 * @param actionName
	 *            the name of the action done
	 * @param isAutoExecuted
	 *            indicate if the action is an auto action or not
	 * @param actionDate
	 *            the date of the action (the milliseconds since January 1, 1970, 00:00:00 GMT.)
	 */
	public synchronized void addNewStepProperty(String owner, String comment, String actionName,
			boolean isAutoExecuted, long actionDate) {
		this._listOfElements.add(new WorkflowPropertyContainerElement(owner, comment, actionName, isAutoExecuted,
				actionDate));
	}

	/**
	 * Add a file in the list of file without any prefix
	 * 
	 * @param filePath
	 *            the file path to add
	 */
	public synchronized void addInitiallySelectedFile(String filePath) {
		if (filePath != null) {
			if (!this._listOfAssociatedFile.contains(filePath)) {
				this._listOfAssociatedFile.add(filePath);
			}
		}
		else {
			LOGGER.debug("WF | try to add null value in the list of selected files");
		}
	}

	/**
	 * Add a list of file without any prefix
	 * 
	 * @param listOfFiles
	 *            the list of files to add
	 */
	public synchronized void addInitiallySelectedFile(List<String> listOfFiles) {
		if (listOfFiles != null) {
			// add each value in the list
			for (int i = 0; i < listOfFiles.size(); i++) {
				this.addInitiallySelectedFile(listOfFiles.get(i));
			}
		}
		else {
			LOGGER.debug("WF | try to add null list in the list of selected files");
		}
	}

	/**
	 * Add a list of file in the list of file with a prefix ({@link WorkflowPropertyContainer#PREFIX_FOR_ADDED_FILES})
	 * that indicates that the file is not one of the initially selected files.
	 * 
	 * @param filePath
	 *            the file path to add
	 */
	public synchronized void addAssociatedFile(String filePath) {
		if (filePath != null) {
			// check that the file is not already in the list (as initial
			// selected file)
			if (!this._listOfAssociatedFile.contains(filePath)) {
				// add the prefix
				String newFilePath = PREFIX_FOR_ADDED_FILES + filePath;
				// check to avoid double and add in the list
				if (!this._listOfAssociatedFile.contains(newFilePath)) {
					this._listOfAssociatedFile.add(newFilePath);
				}
			}
		}
		else {
			LOGGER.debug("WF | try to add null value in the list of selected files");
		}
	}

	/**
	 * Remove the file stored in the given list from the list of associated files
	 * 
	 * @param listOfNotOfflineFiles
	 */
	public synchronized void deleteNotOffLineFile(List<String> listOfNotOfflineFiles) {
		List<String> initiallalyAssociatedFiles = this.getListOfInitiallyAssociatedFiles();
		List<String> addedAssociatedListOfFiles = this.getListOfAddedAssociatedFiles();

		/*
		 * Check the list of not offline file and remove them from the list of associated file if required
		 */
		for (int i = 0; i < listOfNotOfflineFiles.size(); i++) {
			String fileToDelete = listOfNotOfflineFiles.get(i);

			if (initiallalyAssociatedFiles.contains(fileToDelete)) {
				this._listOfAssociatedFile.remove(fileToDelete);
			}

			if (addedAssociatedListOfFiles.contains(fileToDelete)) {
				this._listOfAssociatedFile.remove(PREFIX_FOR_ADDED_FILES + fileToDelete);
			}
		}

	}

	/**
	 * Add a file in the list of file with a prefix ({@link WorkflowPropertyContainer#PREFIX_FOR_ADDED_FILES}) that
	 * indicates that the file is not one of the initially selected files.
	 * 
	 * @param listOfFiles
	 *            the list of files to add
	 */
	public synchronized void addAssociatedFile(List<String> listOfFiles) {
		if (listOfFiles != null) {
			// add each value in the list
			for (int i = 0; i < listOfFiles.size(); i++) {
				this.addAssociatedFile(listOfFiles.get(i));
			}
		}
		else {
			LOGGER.debug("WF | try to add null list in the list of selected files");
		}
	}

	/**
	 * Get the list of initially selected files
	 * 
	 * @return get the list of files initially selected.
	 */
	public synchronized List<String> getListOfInitiallyAssociatedFiles() {
		List<String> result = new ArrayList<String>();

		/*
		 * Clean file path of there prefix if required
		 */
		Iterator<String> filesIterator = this._listOfAssociatedFile.iterator();
		while (filesIterator.hasNext()) {
			String storedFilePath = filesIterator.next();
			if (storedFilePath != null) {
				// check if there is a prefix
				// If there is a prefix, it means that it is not a initially
				// selected files
				// => Don't add in the list of result
				if (!storedFilePath.startsWith(PREFIX_FOR_ADDED_FILES)) {
					// store result
					result.add(storedFilePath);
				}

			}
			else {
				LOGGER.debug("WF | A null value was stored in the list of classpath !!");
			}
		}

		// sort the result
		Collections.sort(result);
		return result;
	}

	/**
	 * Get the list of addes selected files (<=> the files stored with a prefix)
	 * 
	 * @return get the list of files initially selected after removing its prefix
	 */
	public synchronized List<String> getListOfAddedAssociatedFiles() {
		List<String> result = new ArrayList<String>();

		/*
		 * Clean file path of there prefix if required
		 */
		Iterator<String> filesIterator = this._listOfAssociatedFile.iterator();
		while (filesIterator.hasNext()) {
			String storedFilePath = filesIterator.next();
			if (storedFilePath != null) {
				// check if there is a prefix
				// If there is a prefix, it means that it is not a initially
				// selected files
				// => Add in the list of result
				if (storedFilePath.startsWith(PREFIX_FOR_ADDED_FILES)) {
					// remove prefix
					storedFilePath = storedFilePath.substring(PREFIX_FOR_ADDED_FILES.length());
					// store result
					result.add(storedFilePath);
				}
			}
			else {
				LOGGER.debug("WF | A null value was stored in the list of classpath !!");
			}
		}

		// sort the result
		Collections.sort(result);
		return result;
	}

	/**
	 * Get the uuid of the user that made the step "stepNumber" of the workflow
	 * 
	 * @param stepNumber
	 *            the index of the step. (0 = Creation)
	 * @return the user uuid, <b>null</b> if none step with such index exist;
	 */
	public synchronized String getOwner(int stepNumber) {
		// check step number to avoid going out of the list
		if (stepNumber >= _listOfElements.size() || stepNumber < 0) {
			return null;
		}
		else {
			return _listOfElements.get(stepNumber).get_cmsUserUUID();
		}
	}

	/**
	 * Get the comment enter by the user that made the step "stepNumber" of the workflow
	 * 
	 * @param stepNumber
	 *            the index of the step. (0 = Creation)
	 * @return the required comment, <b>null</b> if none step with such index exist;
	 */
	public synchronized String getComment(int stepNumber) {
		// check step number to avoid going out of the list
		if (stepNumber >= _listOfElements.size() || stepNumber < 0) {
			return null;
		}
		else {
			return _listOfElements.get(stepNumber).get_comment();
		}
	}

	/**
	 * Get the action name of the action done at "stepNumber"
	 * 
	 * @param stepNumber
	 *            the index of the step. (0 = Creation)
	 * @return the required action name, <b>null</b> if none step with such index exist;
	 */
	public synchronized String getActionName(int stepNumber) {
		// check step number to avoid going out of the list
		if (stepNumber >= _listOfElements.size()) {
			return null;
		}
		else {
			return _listOfElements.get(stepNumber).get_actionName();
		}
	}

	/**
	 * Get the number of actions done by using the list of element size(so including the initialization)
	 * 
	 * @return the number of done actions
	 */
	public synchronized int getNumberOfActions() {
		return this._listOfElements.size();
	}

	/**
	 * Get the creator of the workflow
	 * 
	 * @return the name of the workflow creator (the one that has initialized it), <b>null</b> if this one is unknown
	 */
	public synchronized String getCreator() {
		if (this._listOfElements.size() > 0) {
			return this.getOwner(0);
		}
		return null;
	}

	/**
	 * @return the _allowedPeople
	 */
	public synchronized AllowedPeople get_allowedPeople() {
		return _allowedPeople;
	}

	/**
	 * @param people
	 *            the _allowedPeople to set
	 */
	public synchronized void set_allowedPeople(AllowedPeople people) {
		_allowedPeople = people;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Class thisClass = this.getClass();
		String result = "[";

		// get all the fields of the current class
		Field[] myFields = thisClass.getDeclaredFields();
		for (int i = 0; i < myFields.length; i++) {

			try {
				result += myFields[i].getName() + " = " + myFields[i].get(this).toString();
			}
			catch (IllegalArgumentException e) {
				LOGGER.warn("One of the field of WorkflowDisplayContainer (id=" + super.toString()
						+ ") cannot be read ! (" + e.getMessage() + ")");
			}
			catch (IllegalAccessException e) {
				LOGGER.warn("One of the field of WorkflowDisplayContainer (id=" + super.toString()
						+ ") cannot be read ! (" + e.getMessage() + ")");
			}

			// if it's no the last element then add a separator
			if (i < myFields.length - 1) {
				result += " ; ";
			}
		}
		return result + "]";
	}

	/**
	 * Generate a XML Representation of the given object.
	 * 
	 * @return the xml corresponding to this class
	 */
	public synchronized String convertInXML() {
		StringBuffer xmlString = new StringBuffer(1024);

		// open tag with name of the class
		xmlString.append("<" + this.getClass().getSimpleName() + " workflowInstanceName=\""
				+ this.get_workflowInstanceName() + "\">");

		// append list of parameters
		xmlString.append("<parameters>");
		for (int i = 0; i < _parameters.size(); i++) {
			xmlString.append(_parameters.get(i).convertInXML());
		}
		xmlString.append("</parameters>");

		// Append list of associated files
		xmlString.append("<associatedFiles>");
		for (int i = 0; i < _listOfAssociatedFile.size(); i++) {
			xmlString.append("<file path=\"" + _listOfAssociatedFile.get(i) + "\"/>");
		}
		xmlString.append("</associatedFiles>");

		// append list of elements
		xmlString.append("<elements>");
		for (int i = 0; i < _listOfElements.size(); i++) {
			xmlString.append(_listOfElements.get(i).convertInXML());
		}
		xmlString.append("</elements>");

		// append the allowedPeople
		xmlString.append("<allowedPeople>");
		if (_allowedPeople != null) {
			xmlString.append(_allowedPeople.toXML());
		}
		xmlString.append("</allowedPeople>");

		// close tag
		xmlString.append("</" + this.getClass().getSimpleName() + ">");

		LOGGER.debug("WF | stored xmlFile :\n" + xmlString.toString());
		return xmlString.toString();
	}

	/**
	 * Get the parameter with such name
	 * @param parameterName the name of the required parameter
	 * @return the parameter object required, <b>null</b> if there is no parameter with such name
	 */
	public Parameter get_parameters(String parameterName) {
		if(this.get_parameters()==null || this.get_parameters().isEmpty())
			return null;
		
		//loop to get the required param
		Iterator<Parameter> paramIterator = this.get_parameters().iterator();
		while(paramIterator.hasNext()){
			Parameter param = paramIterator.next();
			if(param.get_name().equalsIgnoreCase(parameterName)){
				return param;
			}
		}
		
		return null;
	}

}
