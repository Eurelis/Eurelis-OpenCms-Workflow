/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.main.CmsException;

import com.eurelis.opencms.workflows.util.CmsUtil;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;

/**
 * This class implement a individual action that can be use during a workflow. <br/>
 * This function move the ressource into the project with name given in parameter.<br/>
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
 * {@link MoveToProjectFunction#PARAM_TARGETPROJECTNAME_VALUE})</li>
 * </ol>
 * <br/>
 * <br/>
 * <font color="red"><b>WARNING:</b></font> This function cannot be use to move project to Online Project (see
 * {@link PublishResourcesFunction})
 * 
 * @author Sébastien Bianco
 */
public class MoveToProjectFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Logger	LOGGER							= Logger.getLogger(MoveToProjectFunction.class);

	/**
	 * The name of property to look for in the map args to get the new value of lock
	 */
	private static final String	PARAM_TARGETPROJECTNAME_VALUE	= "destination";

	/**
	 * Online project name
	 */
	private static final String	PROJECTNAME_ONLINE				= "Online";

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

		// apply the lock function
		this.applyMoveToProject(targetValueString, _associatedFiles, _cmsObject);
	}

	/**
	 * Move all resources to the given project
	 * 
	 * @param targetProjectName
	 *            the name of the target project
	 * @param listOfFiles
	 *            the list of files on which the lock must be set
	 * @param cmsObject
	 *            the CmsObject that will do such actions
	 */
	private void applyMoveToProject(String targetProjectName, List<String> listOfFiles, CmsObject cmsObject) {

		/*
		 * Set admin object
		 */
		CmsObject adminCms = CmsUtil.getAdminCmsObject();
		CmsProject currentUserProject = cmsObject.getRequestContext().currentProject();

		// backup current project for admin cms (to switch back after the process)
		CmsProject backupAdminProject = adminCms.getRequestContext().currentProject();

		// move to current user project
		adminCms.getRequestContext().setCurrentProject(currentUserProject);

		/*
		 * Check params and execute function
		 */
		if (listOfFiles != null && !listOfFiles.isEmpty()) {
			if (StringChecker.isNotNullOrEmpty(targetProjectName)) {

				/*
				 * Change project value on the entire list of file
				 */
				Iterator<String> listOfFilesIterator = listOfFiles.iterator();
				while (listOfFilesIterator.hasNext()) {
					String filePath = listOfFilesIterator.next();
					try {
						this.moveResourceToProject(targetProjectName, filePath, adminCms);
					}
					catch (CmsException e) {
						LOGGER.warn("The resource " + filePath + " cannot be moved to project " + targetProjectName
								+ " (" + e.getMessage() + ")");
						// LOGGER.debug("WF | "+ErrorFormatter.formatException(e));
					}
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

		// switch back to previous project
		adminCms.getRequestContext().setCurrentProject(backupAdminProject);
	}

	/**
	 * move the given resource in the target project
	 * 
	 * @param targetProjectName
	 *            the name of the target project
	 * @param resourcePath
	 *            the resource VFS path that must receive this new value
	 * @param cmsAdminObject
	 *            the CmsObject that will do such actions (must have Administrator rights)
	 * @throws CmsException
	 *             if the value of lock cannot be change, or if another problem occurs
	 */
	private void moveResourceToProject(String targetProjectName, String resourcePath, CmsObject cmsAdminObject)
			throws CmsException {

		// get the target project
		CmsProject targetProject = null;

		try {
			//read project
			targetProject = cmsAdminObject.readProject(targetProjectName);
		}
		catch (CmsException e) {
			// backup current project for admin cms (to switch back after the process)
			CmsProject currentProject = cmsAdminObject.getRequestContext().currentProject();
			
			// try create project if an error occurs (it could be that the project doesn't exist)
			cmsAdminObject.createProject(targetProjectName, "Project used to store some files waiting for validation", "Users",
						"Projectmanagers");
			
			// switch back to previous project
			cmsAdminObject.getRequestContext().setCurrentProject(currentProject);
			
			//read project
			targetProject = cmsAdminObject.readProject(targetProjectName);
			
		}

		if (targetProject != null) {

			// backup current project for admin cms (to switch back after the process)
			CmsProject currentProject = cmsAdminObject.getRequestContext().currentProject();

			// switch to target project
			cmsAdminObject.getRequestContext().setCurrentProject(targetProject);

			/*
			 * if the target project is Online, use publish instead of copying the resource in another project
			 */
			if (targetProjectName.equals(PROJECTNAME_ONLINE)) {
				LOGGER.warn("The function " + this.getClass().getName() + " cannot move objects to online project.");
			}
			else {
				// copy the resource to the current project
				LOGGER.debug("set date");
				//cmsAdminObject.setDateLastModified(resourcePath, (new Date()).getTime(), false);
				LOGGER.debug("copy resource");
				cmsAdminObject.copyResourceToProject(resourcePath);
				
			}

			// switch back to previous project
			cmsAdminObject.getRequestContext().setCurrentProject(currentProject);
		}

	}

}
