/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.security;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;

import com.eurelis.opencms.workflows.jaxb.workflowconfig.AllowGroup;
import com.eurelis.opencms.workflows.jaxb.workflowconfig.AllowRole;
import com.eurelis.opencms.workflows.jaxb.workflowconfig.AllowUser;
import com.eurelis.opencms.workflows.jaxb.workflowconfig.Branch;
import com.eurelis.opencms.workflows.jaxb.workflowconfig.Workflow;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollectorFactory;
import com.eurelis.opencms.workflows.workflows.availableworkflows.I_AvailableWorkflowCollector;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This class is loaded with the security configuration store in the XML file associated to all workflows. <br>
 * It manage the right of the users to access/modify the workflows. <br>
 * <br>
 * This class follows the design pattern singleton. Please use {@link WorkflowSecurityManager#getInstance()} to get the
 * instance of the class.
 * 
 * @author Sébastien Bianco
 */
public class WorkflowSecurityManager {

	/** The log object for this class. */
	static final Logger							LOGGER						= Logger
																					.getLogger(WorkflowSecurityManager.class);

	/**
	 * The string that starts the description of a Pattern
	 */
	private static final String					PATTERN_STARTING_SUBSTRING	= ModuleConfigurationLoader
																					.getConfiguration().PATTERN_STARTING_SUBSTRING;

	/**
	 * The string that ends the description of a Pattern
	 */
	private static final String					PATTERN_ENDING_SUBSTRING	= ModuleConfigurationLoader
																					.getConfiguration().PATTERN_ENDING_SUBSTRING;

	/**
	 * Store the association WorkflowKey/WorkflowRights
	 */
	private Map<WorkflowKey, WorkflowRights>	_rightsPerWorkflow			= new HashMap<WorkflowKey, WorkflowRights>();

	/**
	 * The single instance of the class
	 * 
	 * @uml.property name="instance"
	 * @uml.associationEnd
	 */
	private static WorkflowSecurityManager		instance					= null;

	/**
	 * private default constructor to deny construction from outside
	 */
	private WorkflowSecurityManager() {

		// The workflow Collector so as to get the list of workflows to manage
		I_AvailableWorkflowCollector workflowCollector = AvailableWorkflowCollectorFactory.getInstance();
		// AvailableWorkflowCollectorFactory.updateAvailableWorkflows();

		// get map of associated workflows
		Map<WorkflowKey, DescriptionContainer> listOfAvailablesWorkflows = workflowCollector
				.get_mapOfAvailableWorkflows();

		/*
		 * for each workflows, get the right file path and parse the file to collect the informations
		 */
		Iterator<Entry<WorkflowKey, DescriptionContainer>> listOfAvailablesWorkflowsIterator = listOfAvailablesWorkflows
				.entrySet().iterator();
		while (listOfAvailablesWorkflowsIterator.hasNext()) {
			Entry<WorkflowKey, DescriptionContainer> entry = listOfAvailablesWorkflowsIterator.next();
			// get right file path
			String filePath = entry.getValue().get_rightsFilePath();
			// parse right file and store result
			if (filePath != null) {
				try {
					WorkflowRights workflowRights = this.extractWorkflowRightsFromCongifAndRightFile(filePath);
					if (workflowRights != null) {
						_rightsPerWorkflow.put(entry.getKey(), workflowRights);
					}
					else {
						LOGGER.debug("An error occurs during the parsing of " + filePath + " (WorkflowRights == null)");
					}
				}
				catch (JAXBException e) {
					LOGGER.debug("An error occurs during the parsing of " + filePath + " (" + e.getMessage() + ")");
				}
			}
			else {
				LOGGER.debug("An error occurs during the parsing of rights (A null filepath has been found)");
			}

		}
	}

	/**
	 * This method parse the file with the given filepath and extract the list of parameters of the workflow
	 * 
	 * @param filePath
	 *            the path of the file containing the configuration and rights associated to a workflow
	 * @return the list of parameters, an <i>empty list</i> if there is no parameters.
	 * @throws JAXBException
	 *             if a problem occurs during parsing
	 */
	private WorkflowRights extractWorkflowRightsFromCongifAndRightFile(String filePath) throws JAXBException {

		// read the resource
		CmsResource resource = OpenCmsEasyAccess.getResource(filePath);

		if (resource != null) {
			if (resource.isFile()) {
				// Read the file
				byte[] fileContent = OpenCmsEasyAccess.readFile(resource);

				if (fileContent.length > 0) {

					// Load JAXB Context and unmarshaller
					JAXBContext jc = JAXBContext
							.newInstance(ModuleConfigurationLoader.getConfiguration().PARSER_JAXBCONTEXT_WORKFLOWCONFIG);
					Unmarshaller unmarshaller = jc.createUnmarshaller();

					// Unmarshall file and get the list of instances
					List<Branch> branches = ((Workflow) unmarshaller.unmarshal(new ByteArrayInputStream(fileContent)))
							.getRights().getBranch();

					return this.extractWorkflowRights(branches);
				}
				else {
					LOGGER.info("The resource " + filePath + " is empty");
				}
			}
			else {
				LOGGER.info("The resource " + filePath + " is not a File");
			}
		}
		else {
			LOGGER.info("The resource with path " + filePath + " has not been found");
		}
		return null;
	}

	/**
	 * This method parse the different branches and create the WorkflowRight object associated
	 * 
	 * @param branches
	 *            the list of rights object corresponding to rights of each branches
	 * @return the WorkflowRight object corresponding to the parsed file, <b>null</b> if a problem occurs.
	 */
	private WorkflowRights extractWorkflowRights(List<Branch> branches) {

		WorkflowRights resultingWorkflowRights = new WorkflowRights();

		/*
		 * For each branch treat it to get the list of allowed people
		 */
		Iterator<Branch> branchesIterator = branches.iterator();
		while (branchesIterator.hasNext()) {
			Branch branch = branchesIterator.next();
			String branchPath = branch.getPath();

			// check if it's a pattern or not
			if (branchPath.startsWith(PATTERN_STARTING_SUBSTRING)) {
				// remove String that identify the pattern
				String branchPathPattern = branchPath.substring(PATTERN_STARTING_SUBSTRING.length(), branchPath
						.length()
						- PATTERN_ENDING_SUBSTRING.length());

				// create the object
				AllowedPeople allowedPeople = this.extractAllowedPeople(branch);
				if (allowedPeople != null) {
					resultingWorkflowRights.addBranchPattern(branchPathPattern, allowedPeople);
				}
				else {
					LOGGER.info("An error occurs during the parsing of a branch -> No allowed people found");
				}
			}
			else {
				// create the object
				AllowedPeople allowedPeople = this.extractAllowedPeople(branch);
				if (allowedPeople != null) {
					resultingWorkflowRights.addBranch(branchPath, allowedPeople);
				}
				else {
					LOGGER.info("An error occurs during the parsing of a branch -> No allowed people found");
				}
			}
		}

		return resultingWorkflowRights;
	}

	/**
	 * Extract the list of allowed people from the parsed file
	 * 
	 * @param branch
	 *            the branch object containing the list of allowed people
	 * @return the AllowedPeople object, <b>null</b> if a problem occurs.
	 */
	private AllowedPeople extractAllowedPeople(Branch branch) {
		AllowedPeople allowedPeople = new AllowedPeople();

		/*
		 * Get the list of allowed User
		 */
		Iterator<AllowUser> allowedUserIterator = branch.getAllowUser().iterator();
		while (allowedUserIterator.hasNext()) {
			AllowUser allowedUser = allowedUserIterator.next();
			String userName = allowedUser.getName();

			// check if the name correspond to a pattern (start with "{")
			if (userName.startsWith(PATTERN_STARTING_SUBSTRING)) {
				// remove the String that indicates that it is a pattern
				String pattern = userName.substring(PATTERN_STARTING_SUBSTRING.length(), userName.length()
						- PATTERN_ENDING_SUBSTRING.length());

				// add pattern in the list
				allowedPeople.addAllowedUserPattern(pattern, allowedUser.getType());
			}
			else {
				allowedPeople.addAllowedUser(userName, allowedUser.getType());
			}
		}

		/*
		 * Get the list of allowed Group
		 */
		Iterator<AllowGroup> allowedGroupIterator = branch.getAllowGroup().iterator();
		while (allowedGroupIterator.hasNext()) {
			AllowGroup allowedGroup = allowedGroupIterator.next();
			String groupName = allowedGroup.getName();

			// check if the name correspond to a pattern (start with "{")
			if (groupName.startsWith(PATTERN_STARTING_SUBSTRING)) {
				// remove the String that indicates that it is a pattern
				String pattern = groupName.substring(PATTERN_STARTING_SUBSTRING.length(), groupName.length()
						- PATTERN_ENDING_SUBSTRING.length());

				// add pattern in the list
				allowedPeople.addAllowedGroupPattern(pattern, allowedGroup.getType());
			}
			else {
				allowedPeople.addAllowedGroup(groupName, allowedGroup.getType());
			}
		}

		/*
		 * Get the list of allowed Role
		 */
		Iterator<AllowRole> allowedRoleIterator = branch.getAllowRole().iterator();
		while (allowedRoleIterator.hasNext()) {
			AllowRole allowedRole = allowedRoleIterator.next();
			String roleName = allowedRole.getName();

			// check if the name correspond to a pattern (start with "{")
			if (roleName.startsWith(PATTERN_STARTING_SUBSTRING)) {
				// remove the String that indicates that it is a pattern
				String pattern = roleName.substring(PATTERN_STARTING_SUBSTRING.length(), roleName.length()
						- PATTERN_ENDING_SUBSTRING.length());

				// add pattern in the list
				allowedPeople.addAllowedRolePattern(pattern, allowedRole.getType());
			}
			else {
				allowedPeople.addAllowedRole(roleName, allowedRole.getType());
			}
		}

		return allowedPeople;
	}

	/**
	 * Get the single instance of this class. If the current instance is null, then create the instance.
	 * 
	 * @return the instance of SecurityManager
	 * @uml.property name="instance"
	 */
	public static WorkflowSecurityManager getInstance() {
		if (instance == null) {
			instance = new WorkflowSecurityManager();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";
		Iterator<Entry<WorkflowKey, WorkflowRights>> iterator = _rightsPerWorkflow.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<WorkflowKey, WorkflowRights> entry = iterator.next();
			result += entry.getKey() + " : \n" + ((entry.getValue() != null) ? entry.getValue().toString() : "null")
					+ "\n";
		}

		return result;
	}

	/**
	 * Check if the user has rights to access the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param key
	 *            the name of the workflow to access
	 * @param parentFolder
	 *            the current branch
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to access the workflow, <i>false</i> otherwise.
	 */
	public boolean hasReadRightOnWorkflow(CmsUser currentUser, WorkflowKey key, String parentFolder, Locale locale) {
		if (this._rightsPerWorkflow.containsKey(key)) {
			WorkflowRights wfRights = this._rightsPerWorkflow.get(key);
			if (wfRights != null) {
				boolean result = wfRights.hasReadRightOnWorkflow(currentUser, parentFolder, locale);
				//LOGGER.debug("hasReadRightOnWorkflow(" + currentUser.getName() + "," + key + "," + parentFolder + ") = " + result);
				return result;
			}
			else {
				LOGGER.debug("WF | WorkflowRight null  for workflow with key " + key + ".");
			}
		}
		else {
			LOGGER.debug("WF | The right for the workflow with key " + key + " has not been found.");
		}
		return false;
	}

	/**
	 * Check if the user has rights to modify the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param key
	 *            the name of the workflow to access
	 * @param parentFolder
	 *            the current branch
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to modify the workflow, <i>false</i> otherwise.
	 */
	public boolean hasWriteRightOnWorkflow(CmsUser currentUser, WorkflowKey key, String parentFolder, Locale locale) {
		if (this._rightsPerWorkflow.containsKey(key)) {
			WorkflowRights wfRights = this._rightsPerWorkflow.get(key);
			if (wfRights != null) {
				boolean result = wfRights.hasWriteRightOnWorkflow(currentUser, parentFolder, locale);
				//LOGGER.debug("hasWriteRightOnWorkflow(" + currentUser.getName() + "," + key + "," + parentFolder	+ ") = " + result);
				return result;
			}
			else {
				LOGGER.debug("WF | WorkflowRight null  for workflow with key " + key + ".");
			}
		}
		else {
			LOGGER.debug("WF | The right for the workflow with key " + key + " has not been found.");
		}
		return false;
	}

	/**
	 * Reload the Security manager
	 */
	public static void reload() {
		if (instance != null) {
			instance = new WorkflowSecurityManager();
			LOGGER.debug("WF | Reloaded security configuration = \n" + instance.toString());
		}
		else {
			LOGGER.debug("WF | Security configuration has not been required yet => no (re)-loading done yet !");
		}

	}

	/**
	 * Get the rights associated to a workflow
	 * 
	 * @param key
	 *            the key of the workflow
	 * @return the rights associated, <b>null</b> if there is none
	 */
	public WorkflowRights getRights(WorkflowKey key) {
		return this._rightsPerWorkflow.get(key);
	}

	/**
	 * Check if the user has rights to create the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param key
	 *            the name of the workflow to access
	 * @param parentFolder
	 *            the current branch
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to modify the workflow, <i>false</i> otherwise.
	 */
	public boolean hasCreateRightOnWorkflow(CmsUser currentUser, WorkflowKey key, String parentFolder, Locale locale) {
		if (this._rightsPerWorkflow.containsKey(key)) {
			WorkflowRights wfRights = this._rightsPerWorkflow.get(key);
			if (wfRights != null) {
				boolean result = wfRights.hasCreateRightOnWorkflow(currentUser, parentFolder, locale);
				//LOGGER.debug("WF | hasWriteRightOnWorkflow(" + currentUser.getName() + "," + key + "," + parentFolder	+ ") = " + result);
				return result;
			}
			else {
				LOGGER.debug("WF | WorkflowRight null  for workflow with key " + key + ".");
			}
		}
		else {
			LOGGER.debug("WF | The right for the workflow with key " + key + " has not been found.");
		}
		return false;
	}

}
