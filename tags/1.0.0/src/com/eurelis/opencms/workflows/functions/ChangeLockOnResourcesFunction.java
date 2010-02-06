/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
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
import org.opencms.lock.CmsLock;
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
 * This function set a lock on a list of files.<br/><br/>
 * The required parameters to use this function is :
 * <ol>
 * <li> <b>The list of files:</b> This list must be stored in the transcientVar map with the key {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST} (done by {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li> <b>The CmsOject instance:</b> This object must be stored in the transcientVar map with the key {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT} (done by {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li> <b>The value of the lock to set:</b> This value is indicated in the description of the workflow as parameter, and so will be stored in the Map args (cf {@link ChangeLockOnResourcesFunction#PARAM_LOCKVALUE})</li>
 * </ol> 
 * @author Sébastien Bianco
 * 
 */
public class ChangeLockOnResourcesFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(ChangeLockOnResourcesFunction.class);

	/**
	 * The name of property to look for in the map args to get the new value of
	 * lock
	 */
	private static final String PARAM_LOCKVALUE = "lockValue";
	
	
	/**
	 * Execute this function
	 * 
	 * @param transientVars
	 *            Variables that will not be persisted. These include inputs
	 *            given in the {@link Workflow#initialize} and
	 *            {@link Workflow#doAction} method calls. There are a number of
	 *            special variable names:
	 *            <ul>
	 *            <li><code>entry</code>: (object type:
	 *            {@link com.opensymphony.workflow.spi.WorkflowEntry}) The
	 *            workflow instance
	 *            <li><code>context</code>: (object type:
	 *            {@link com.opensymphony.workflow.WorkflowContext}). The
	 *            workflow context.
	 *            <li><code>actionId</code>: The Integer ID of the current
	 *            action that was take (if applicable).
	 *            <li><code>currentSteps</code>: A Collection of the current
	 *            steps in the workflow instance.
	 *            <li><code>store</code>: The
	 *            {@link com.opensymphony.workflow.spi.WorkflowStore}.
	 *            <li><code>descriptor</code>: The
	 *            {@link com.opensymphony.workflow.loader.WorkflowDescriptor}.
	 *            </ul>
	 *            Also, any variable set as a
	 *            {@link com.opensymphony.workflow.Register}), will also be
	 *            available in the transient map, no matter what. These
	 *            transient variables only last through the method call that
	 *            they were invoked in, such as {@link Workflow#initialize} and
	 *            {@link Workflow#doAction}.
	 * @param args
	 *            The properties for this function invocation. Properties are
	 *            created from arg nested elements within the xml, an arg
	 *            element takes in a name attribute which is the properties key,
	 *            and the CDATA text contents of the element map to the property
	 *            value.
	 * @param ps
	 *            The persistent variables that are associated with the current
	 *            instance of the workflow. Any change made to the propertyset
	 *            are persisted to the propertyset implementation's persistent
	 *            store.
	 * 
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map,
	 *      java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void executeFunction(Map transientVars, Map args, PropertySet ps) throws WorkflowException{		
	
		// Get the new value of lock to set
		String lockValueString = (String) args.get(PARAM_LOCKVALUE);		
		
		//apply the lock function
		this.applyNewLock(lockValueString, this._associatedFiles, this._cmsObject);
	}

	/**
	 * Apply the new lock on the list of resource
	 * @param lockValueString the new lock value to set (String encoded)
	 * @param listOfFiles the list of files on which the lock must be set
	 * @param cmsObject the CmsObject that will do such actions
	 */
	private void applyNewLock(String lockValueString,List<String> listOfFiles,CmsObject cmsObject) {
		
		/*
		 * Set admin object
		 */
		CmsObject adminCms = CmsUtil.getAdminCmsObject();    	
    	CmsProject currentUserProject = cmsObject.getRequestContext().currentProject();
    	
    	//backup current project for admin cms (to switch back after the process)      	
    	CmsProject backupAdminProject = adminCms.getRequestContext().currentProject();
	
    	//move to current user project    	
    	adminCms.getRequestContext().setCurrentProject(currentUserProject);      	
    	
		/*
		 * Check params and execute function
		 */
		if (listOfFiles != null && !listOfFiles.isEmpty()) {
			if (StringChecker.isNotNullOrEmpty(lockValueString)) {
				try {
					boolean newLockValue = Boolean
							.parseBoolean(lockValueString);					
					
					
					/*
					 * Change lock value on the entire list of file
					 */
					Iterator<String> listOfFilesIterator = listOfFiles
							.iterator();
					while (listOfFilesIterator.hasNext()) {
						String filePath = listOfFilesIterator.next();
						try {
							this.setNewLockValue(newLockValue, filePath,adminCms);
						} catch (CmsException e) {
							LOGGER.warn("The resource "+filePath+" cannot receive the lock value "+newLockValue+" ("+e.getMessage()+")");
							//LOGGER.debug("WF | "+ErrorFormatter.formatException(e));
						}
					}
				} catch (NumberFormatException e) {
					LOGGER
							.warn("The function "
									+ this.getClass().getName()
									+ " receive the param "
									+ PARAM_LOCKVALUE
									+ ", but the parameter value set is not a boolean.");
				}
			} else {
				LOGGER.warn("The function " + this.getClass().getName()
						+ " doesn't receive the param " + PARAM_LOCKVALUE);
			}
		} else {
			LOGGER.warn("The initialized workflow has no associated files...");
		}
		
		// switch back to previous project            
    	adminCms.getRequestContext().setCurrentProject(backupAdminProject);
	}
	
	/**
	 * Set the new lock value on a resource
	 * @param newLockValue the new value of lock to set
	 * @param resourcePath the resource VFS path that must receive this new value
	 * @param adminCmsObject the CmsObject that will do such actions (must have admin rights)
	 * @throws CmsException if the value of lock cannot be change, or if another problem occurs
	 */
	private void setNewLockValue(boolean newLockValue, String resourcePath,CmsObject adminCmsObject) throws CmsException {
		
	
		
    	
		
		
		//get the current value of lock
		CmsLock currentLock = adminCmsObject.getLock(resourcePath);
		
		//check if the lock exist. If it doesn't exist, and if one is required, then create it.
		if(currentLock.isNullLock() && newLockValue){
			adminCmsObject.lockResource(resourcePath);	
			return;
		}
		
		//if the lock exist, steals it from another user...
		if(!currentLock.isNullLock()){
			adminCmsObject.changeLock(resourcePath);
		}
		
		//get current lock value
		boolean currentLockValue = !(currentLock.getType().isUnlocked());
		
		//update lock if required
		if(currentLockValue!=newLockValue){			
			if(newLockValue){
				adminCmsObject.lockResource(resourcePath);
			}
			else{
				adminCmsObject.unlockResource(resourcePath);
			}
		}
		
		
	}

	
}
