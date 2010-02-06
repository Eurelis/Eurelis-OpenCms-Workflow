/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.lock.CmsLock;
import org.opencms.main.CmsException;

import com.eurelis.opencms.workflows.util.CmsUtil;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;

/**
 * This class implement a individual action that can be use during a workflow. <br/>
 * This function move the ressource into the project with name given in parameter and set the new lock value in this
 * project<br/>
 * <br/>
 * The required parameters to use this function is :
 * <ol>
 * <li><b>The list of files:</b> This list must be stored in the transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li><b>The CmsOject instance:</b> This object must be stored in the transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li><b>The name of the project to move the resource in:</b> This value is indicated in the description of the
 * workflow as parameter, and so will be stored in the Map args (cf
 * {@link MoveToProjectAndSetLockFunction#PARAM_TARGETPROJECTNAME_VALUE})</li>
 * <li><b>The value of the lock to set:</b> This value is indicated in the description of the workflow as parameter, and
 * so will be stored in the Map args (cf {@link ChangeLockOnResourcesFunction#PARAM_LOCKVALUE})</li>
 * </ol>
 * <br/>
 * <br/>
 * <font color="red"><b>WARNING:</b></font> This function cannot be use to move project to Online Project (see
 * {@link PublishResourcesFunction})
 * 
 * @author Sébastien Bianco
 */
public class MoveToProjectAndSetLockFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Logger	LOGGER							= Logger
																		.getLogger(MoveToProjectAndSetLockFunction.class);

	/**
	 * The name of property to look for in the map args to get the new value of lock
	 */
	private static final String	PARAM_TARGETPROJECTNAME_VALUE	= "destination";

	/**
	 * The name of property to look for in the map args to get the new value of lock
	 */
	private static final String	PARAM_LOCKVALUE					= "lockValue";

	/**
	 * Online project name
	 */
	private static final String	PROJECTNAME_ONLINE				= "Online";
	
	/**
	 * Offline project name
	 */
	private static final String	PROJECTNAME_OFFLINE				= "Offline";

	/**
	 * Execute this function
	 * 
	 * @param transientVars
	 *            Variables that will not be persisted. These include inputs given in the {@link Workflow#initialize}
	 *            and {@link Workflow#doAction} method calls. There are a number of special variable names:
	 *            <ul>
	 *            <li><code>entry</code>: (object type: {@link com.opensymphony.workflow.spi.WorkflowEntry}) The
	 *            workflow instance
	 *            <li><code>context</code>: (object type: {@link com.opensymphony.workflow.WorkflowContext}). The
	 *            workflow context.
	 *            <li><code>actionId</code>: The Integer ID of the current action that was take (if applicable).
	 *            <li><code>currentSteps</code>: A Collection of the current steps in the workflow instance.
	 *            <li><code>store</code>: The {@link com.opensymphony.workflow.spi.WorkflowStore}.
	 *            <li><code>descriptor</code>: The {@link com.opensymphony.workflow.loader.WorkflowDescriptor}.
	 *            </ul>
	 *            Also, any variable set as a {@link com.opensymphony.workflow.Register}), will also be available in the
	 *            transient map, no matter what. These transient variables only last through the method call that they
	 *            were invoked in, such as {@link Workflow#initialize} and {@link Workflow#doAction}.
	 * @param args
	 *            The properties for this function invocation. Properties are created from arg nested elements within
	 *            the xml, an arg element takes in a name attribute which is the properties key, and the CDATA text
	 *            contents of the element map to the property value.
	 * @param ps
	 *            The persistent variables that are associated with the current instance of the workflow. Any change
	 *            made to the propertyset are persisted to the propertyset implementation's persistent store.
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map, java.util.Map,
	 *      com.opensymphony.module.propertyset.PropertySet)
	 */
	public void executeFunction(Map transientVars, Map args, PropertySet ps) throws WorkflowException

	{

		// Get the new value of the target project
		String targetValueString = (String) args.get(PARAM_TARGETPROJECTNAME_VALUE);

		// Get the new value of lock to set
		String lockValueString = (String) args.get(PARAM_LOCKVALUE);

		// apply the lock function
		this.applyChangeToResources(targetValueString, lockValueString, _associatedFiles, _cmsObject);
	}

	/**
	 * Move all resources to the given project
	 * 
	 * @param targetProjectName
	 *            the name of the target project
	 * @param lockValueString
	 *            the new lock value to set
	 * @param listOfFiles
	 *            the list of files on which the lock must be set
	 * @param cmsObject
	 *            the CmsObject that will do such actions
	 */
	private void applyChangeToResources(String targetProjectName, String lockValueString, List<String> listOfFiles,
			CmsObject cmsObject) {

		// get the admin object
		CmsObject adminCms = CmsUtil.getAdminCmsObject();

		// create target project
		this.createTheTargetProject(adminCms, targetProjectName);
		
		/*
		 * Reorder files
		 */
		if(targetProjectName.endsWith(PROJECTNAME_OFFLINE)){
			Collections.sort(this._associatedFiles,Collections.reverseOrder());
		}

		/*
		 * Check params and execute function
		 */
		if (listOfFiles != null && !listOfFiles.isEmpty()) {
			if (StringChecker.isNotNullOrEmpty(targetProjectName)) {

				try {
					CmsProject targetProject = adminCms.readProject(targetProjectName);

					// unlock all file to be sure to move them
					this.setNewLockValue(false, listOfFiles, cmsObject, cmsObject.getRequestContext().currentProject());
					LOGGER.debug("unlock done");

					// move the all files
					this.moveAllResourcesToProject(targetProject, listOfFiles, cmsObject);
					LOGGER.debug("move done");

					// set new lock value to all files
					boolean lockValue = Boolean.parseBoolean(lockValueString);
					this.setNewLockValue(lockValue, listOfFiles, cmsObject, targetProject);
					LOGGER.debug("new lock set");
				}
				catch (NumberFormatException e) {
					LOGGER.warn("The given newLock value is invalid");
				}
				catch (CmsException e) {
					LOGGER.error(ErrorFormatter.formatException(e));
				}
			}
			else {
				LOGGER.warn("The function " + this.getClass().getName() + " doesn't receive the param "
						+ PARAM_TARGETPROJECTNAME_VALUE);
			}
		}
		else {
			LOGGER.warn("The initialized workflow has no associated files...");
		}

	}

	/**
	 * Create the target project if doesn't exist
	 * 
	 * @param adminCms
	 *            the cms admin object
	 * @param targetProjectName
	 *            the name of the target project
	 */
	private void createTheTargetProject(CmsObject adminCms, String targetProjectName) {
		// get the target project
		CmsProject targetProject = null;

		/*
		 * Create the target project
		 */
		try {
			try {
				// read project
				targetProject = adminCms.readProject(targetProjectName);
			}
			catch (CmsException e) {
				// backup current project for admin cms (to switch back after the process)
				CmsProject currentProject = adminCms.getRequestContext().currentProject();

				// try create project if an error occurs (it could be that the project doesn't exist)
				adminCms.createProject(targetProjectName, "Project used to store some files waiting for validation",
						"Users", "Projectmanagers");

				// switch back to previous project
				adminCms.getRequestContext().setCurrentProject(currentProject);

				// read project
				targetProject = adminCms.readProject(targetProjectName);

			}
		}
		catch (CmsException e) {
			LOGGER.error("The target project doesn't exist and cannot be create !");
		}
	}

	/**
	 * Move all resource to the target project
	 * 
	 * @param targetProject
	 *            the target project
	 * @param listOfFiles
	 *            the list of files
	 * @param cmsObject
	 *            the current cms object
	 */
	private void moveAllResourcesToProject(CmsProject targetProject, List<String> listOfFiles, CmsObject cmsObject) {
		/*
		 * Set admin object
		 */
		CmsObject adminCms = CmsUtil.getAdminCmsObject();
		CmsProject currentUserProject = cmsObject.getRequestContext().currentProject();

		// backup current project for admin cms (to switch back after the process)
		CmsProject backupAdminProject = adminCms.getRequestContext().currentProject();

		

		if (targetProject != null) {
			/*
			 * Treat each files
			 */
			Iterator<String> listOfFilesIterator = listOfFiles.iterator();
			while (listOfFilesIterator.hasNext()) {
				String filePath = listOfFilesIterator.next();
				try {
					// move to current user project
					adminCms.getRequestContext().setCurrentProject(targetProject);
					
					//move the resources
					this.moveResourceToProject(targetProject, filePath, adminCms);
					
					// switch back to previous project
					adminCms.getRequestContext().setCurrentProject(backupAdminProject);
				}
				catch (CmsException e) {
					LOGGER.debug("The file " + filePath + " cannot be moved to " + targetProject);
				}
			}
		}

	

	}

	/**
	 * Move a resource to the right project
	 * 
	 * @param targetProject
	 *            the target project
	 * @param filePath
	 *            the file to treat
	 * @param adminCms
	 *            the cms Admin object
	 * @throws CmsException
	 *             if a problem occurs with opencms
	 */
	private void moveResourceToProject(CmsProject targetProject, String filePath, CmsObject adminCms)
			throws CmsException {

		/*
		 * if the target project is Online, use publish instead of copying the resource in another project
		 */
		if (targetProject.getName().equals(PROJECTNAME_ONLINE)) {
			LOGGER.warn("The function " + this.getClass().getName() + " cannot move objects to online project.");
		}
		else {
			LOGGER.debug("move " + filePath + " to " + targetProject);

		
			// copy the resource to the current project
			adminCms.copyResourceToProject(filePath);
		}

	}

	/**
	 * Unlock the all files
	 * 
	 * @param newLockValue
	 *            the lock value to set
	 * @param listOfFiles
	 *            the files to unlock
	 * @param cmsObject
	 *            the current cmsObject
	 * @param targetProject
	 * 				the project where must be put the lock
	 * 			
	 */
	private void setNewLockValue(boolean newLockValue, List<String> listOfFiles, CmsObject cmsObject,
			CmsProject targetProject) {

		/*
		 * Set admin object
		 */
		CmsObject adminCms = CmsUtil.getAdminCmsObject();

		CmsProject currentUserProject = cmsObject.getRequestContext().currentProject();

		// backup current project for admin cms (to switch back after the process)
		CmsProject backupAdminProject = adminCms.getRequestContext().currentProject();

		

		/*
		 * Treat each files
		 */
		Iterator<String> listOfFilesIterator = listOfFiles.iterator();
		while (listOfFilesIterator.hasNext()) {
			String filePath = listOfFilesIterator.next();
			try {
				// move to current user project
				adminCms.getRequestContext().setCurrentProject(targetProject);
				
				//set the new lock value
				this.setNewLockValue(newLockValue, filePath, adminCms);
				
				// switch back to previous project
				adminCms.getRequestContext().setCurrentProject(backupAdminProject);
			}
			catch (CmsException e) {
				LOGGER.warn("The resource " + filePath + " cannot be unlocked");
				// LOGGER.debug("WF | "+ErrorFormatter.formatException(e));
			}
		}

		
	}



	/**
	 * Set the new lock value on a resource
	 * 
	 * @param newLockValue
	 *            the new value of lock to set
	 * @param resourcePath
	 *            the resource VFS path that must receive this new value
	 * @param adminCmsObject
	 *            the CmsObject that will do such actions (must have admin rights)
	 * @throws CmsException
	 *             if the value of lock cannot be change, or if another problem occurs
	 */
	private void setNewLockValue(boolean newLockValue, String resourcePath, CmsObject adminCmsObject)
			throws CmsException {

		// get the current value of lock
		CmsLock currentLock = adminCmsObject.getLock(resourcePath);

		// check if the lock exist. If it doesn't exist, and if one is required, then create it.
		if (currentLock.isNullLock() && newLockValue) {
			adminCmsObject.lockResource(resourcePath);
			return;
		}

		// if the lock exist, steals it from another user...
		if (!currentLock.isNullLock()) {
			adminCmsObject.changeLock(resourcePath);
		}

		// get current lock value
		boolean currentLockValue = !(currentLock.getType().isUnlocked());

		// update lock if required
		if (currentLockValue != newLockValue) {
			if (newLockValue) {
				LOGGER.debug("lock");
				adminCmsObject.lockResource(resourcePath);
			}
			else {
				LOGGER.debug("unlock");
				adminCmsObject.unlockResource(resourcePath);
			}
		}

	}

}
