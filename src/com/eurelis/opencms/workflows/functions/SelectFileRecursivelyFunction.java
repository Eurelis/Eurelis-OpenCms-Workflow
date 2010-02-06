/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.util.CmsUUID;

import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * This class implement a individual action that can be use during a workflow.
 * <br/> This function get the list of file set as parameters and update it
 * depending the initial parameters.<br/><br/> The required parameters to use
 * this function is :
 * <ol>
 * <li> <b>The list of initial parameters:</b> This list must be stored in the
 * transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_INITIALPARAMETERS} (done
 * by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject, List)})</li>
 * <li> <b>The CmsOject instance:</b> This object must be stored in the
 * transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject, List)})</li>
 * </ol>
 * <br>
 * <br>
 * The required initial parameters required are : (<b><font
 * color="red">Default value is false</font></b>)
 * <ol>
 * <li> <b>SelectFileRecursively</b> : select the files recursively in the case
 * of the selected files is a folder</li>
 * <li> <b>SelectAssociatedFile</b> : select the associated files of the
 * selected files</li>
 * <li> <b>CheckOffline</b> : add the file only if it is in Offline project</li>
 * </ol>
 * 
 * 
 * @author Sébastien Bianco
 * 
 */
public class SelectFileRecursivelyFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(SelectFileRecursivelyFunction.class);

	/**
	 * The name of the parameter that indicate the recursivity of the selection
	 */
	private static final String PARAM_RECURSIVE = "SelectFileRecursively";

	/**
	 * The name of the parameter that indicate that the selection includes
	 * relative faile
	 */
	private static final String PARAM_RELATIVE = "SelectAssociatedFile";

	/**
	 * The name of the parameter that indicate if it need to check the
	 */
	private static final String PARAM_CHECKOFFLINE = "CheckOffline";

	/**
	 * The name of offline project
	 */
	private static final String OFFLINEPROJECT_NAME = "Offline";

	/**
	 * indicate if the selection must be done recursively
	 */
	private boolean _recursive = false;

	/**
	 * indicate if the selection must be done to relatives files
	 */
	private boolean _relative = false;

	/**
	 * indicate if the selection only concern offline resources
	 */
	private boolean _checkOffline = false;

	/**
	 * The list of file that are not offline
	 */
	private List<String> _fileNotOffline = new ArrayList<String>();

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
	public void executeFunction(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {

		// read list and get required parameters
		Iterator<Parameter> initialParametersIterator = _initialParameters
				.iterator();
		while (initialParametersIterator.hasNext()) {
			Parameter param = initialParametersIterator.next();
			if (param != null) {
				// get if the selection is recursive
				if (param.get_name().equals(PARAM_RECURSIVE)) {
					if (param.get_value() instanceof Boolean) {
						_recursive = ((Boolean) param.get_value())
								.booleanValue();
					} else {
						LOGGER
								.warn("The parameter "
										+ param.get_name()
										+ " is not of the right type (required : boolean)");
					}
				}

				// get if the selection concern the associated resources
				if (param.get_name().equals(PARAM_RELATIVE)) {
					if (param.get_value() instanceof Boolean) {
						_relative = ((Boolean) param.get_value())
								.booleanValue();
					} else {
						LOGGER
								.warn("The parameter "
										+ param.get_name()
										+ " is not of the right type (required : boolean)");
					}
				}

				// get if the selection concern not offline resources
				if (param.get_name().equals(PARAM_CHECKOFFLINE)) {
					if (param.get_value() instanceof Boolean) {
						_checkOffline = ((Boolean) param.get_value())
								.booleanValue();
					} else {
						LOGGER
								.warn("The parameter "
										+ param.get_name()
										+ " is not of the right type (required : boolean)");
					}
				}
			} else {
				LOGGER.warn("A null initial parameter has been stored !!!");
			}
		}

	
		// treat list of files depending those parameters
		this.treatListOfFiles();

	}

	/**
	 * Treat the given list of files (get recursively associated files)
	 */
	private void treatListOfFiles() {

		List<String> resultingListOfFiles = new ArrayList<String>();

		/*
		 * Create a Stack of the file to treat from the given path
		 */
		List<CmsResource> filesToTreat = new ArrayList<CmsResource>();
		Iterator<String> associatedFilesIterator = _associatedFiles.iterator();
		while (associatedFilesIterator.hasNext()) {
			filesToTreat.add(OpenCmsEasyAccess
					.getResource(associatedFilesIterator.next()));
		}

		/*
		 * For each file to treat, get the list of files to add
		 */
		Iterator<CmsResource> filesToTreatIterator = filesToTreat.iterator();
		while (filesToTreatIterator.hasNext()) {
			resultingListOfFiles.addAll(this.treatResource(filesToTreatIterator
					.next()));
		}

		// clean the resultingListOfFiles by checking if there is not double
		List<String> cleanList = new ArrayList<String>();
		Iterator<String> resultingListOfFilesIterator = resultingListOfFiles
				.iterator();
		while (resultingListOfFilesIterator.hasNext()) {
			String toAdd = resultingListOfFilesIterator.next();
			if (!cleanList.contains(toAdd)) {
				cleanList.add(toAdd);
			}
		}

		// update associatedFile list
		this._associatedFiles = cleanList;
	}

	/**
	 * Get the list of file that need to be add to the list of files from the
	 * CmsResource object (recursive launch if folder or relative resource)
	 * 
	 * @param resource
	 *            The CmsResource to start from
	 * @return the list of file path to add to the list of selected files or
	 *         <i>empty list</i>
	 */
	private List<String> treatResource(CmsResource resource) {
		
		

		List<String> result = new ArrayList<String>();

		if (resource != null) {

			// check if it a file
			if (resource.isFile()) {
				result.addAll(this.treatFile(resource));
			}

			// check if it a folder
			else if (resource.isFolder()) {
				result.addAll(this.treatFolder(resource));
			}

			else {// it's neither a file neither a folder ???
				LOGGER
						.debug("WF | The given resource is neither a file neither a folder");
			}
		} else {
			LOGGER.debug("WF | The given resource is null !");
		}

		return result;
	}

	/**
	 * Get the resources, check it and get its associated resources if required
	 * and add all in the list of file (after checking if offline or not)
	 * 
	 * @param resource
	 *            The CmsResource to start from (this resource is a file)
	 * @return the list of file path to add to the list of selected files or
	 *         <i>empty list</i>
	 */
	private List<String> treatFile(CmsResource resource) {
		List<String> result = new ArrayList<String>();
		
		LOGGER.debug("treat file "+resource.getRootPath());

		// check if associated resources is required or not
		if (_relative) {

			// check if it has some relative resources
			List<CmsResource> listOfAssociatedFiles = OpenCmsEasyAccess
					.getListOfAssociatedResource(resource.getRootPath());

			// launch recursive call on associated resources
			Iterator<CmsResource> listOfAssociatedFilesIterator = listOfAssociatedFiles
					.iterator();
			while (listOfAssociatedFilesIterator.hasNext()) {
				result.addAll(this.treatResource(listOfAssociatedFilesIterator
						.next()));

			}
		}

		// in any case add the resource in the list of files to add
		// check if only offline required
		if (_checkOffline) {

			// check if the resource is offline
			if (this.isOffline(resource)) {
					result.add(resource.getRootPath());
			} else {
				_fileNotOffline.add(resource.getRootPath());
			}
		} else {
			result.add(resource.getRootPath());
		}

		return result;
	}

	/**
	 * Check that the resource is in project Offline
	 * 
	 * @param resource
	 *            the resource object to check
	 * @return <b>true</b> if the resource is offline, <b>false</b> otherwise.
	 */
	private boolean isOffline(CmsResource resource) {
		
		
		// test if the resource is published
		if (resource.getState().isUnchanged())
			return false;

		// get UUID of the project of the resource
		CmsUUID projectUUID = resource.getProjectLastModified();

		// get the project object of the resource
		CmsProject projectObject;
		try {
			projectObject = _cmsObject.readProject(projectUUID);

			// read the project name
			if (projectObject != null) {
				return projectObject.getName().endsWith(OFFLINEPROJECT_NAME);
			} else {
				LOGGER.debug("WF | The project object is null");
			}

			return false;
		} catch (CmsException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
			return false;
		}
	}

	/**
	 * Get the resources, check it and get its child resources if required and
	 * add all in the list of file (after checking if offline or not)
	 * 
	 * @param resource
	 *            The CmsResource to start from (this resource is a file)
	 * @return the list of file path to add to the list of selected files or
	 *         <i>empty list</i>
	 */
	private List<String> treatFolder(CmsResource resource) {
		List<String> result = new ArrayList<String>();
		

		// if recursive selection is active, launch recursively the selection
		if (_recursive) {

			// list the folder content
			List<CmsResource> folderContent = OpenCmsEasyAccess
					.getListOfFile(resource.getRootPath());		

			// launch recursively this method on children files
			Iterator<CmsResource> folderContentIterator = folderContent
					.iterator();
			while (folderContentIterator.hasNext()) {
				result.addAll(this.treatResource(folderContentIterator.next()));
			}

		}

		// in any case add the resource in the list of files to add
		// check if only offline required
		if (_checkOffline) {

			// check if the resource is offline
			if (this.isOffline(resource)) {
				result.add(resource.getRootPath());
			} else {
				_fileNotOffline.add(resource.getRootPath());
			}
		} else {
			result.add(resource.getRootPath());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.eurelis.opencms.workflows.functions.EurelisWorkflowFunction#updateProperty(com.opensymphony.module.propertyset.PropertySet,
	 *      java.util.Map)
	 */
	@Override
	protected void updateProperty(PropertySet ps, Map transientVars) {
		// get the action name
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor) transientVars
				.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_DESCRIPTOR);
		if (workflowDescriptor != null) {
			
			ActionDescriptor actionDescription = workflowDescriptor
					.getAction(_actionID);
			String actionName = actionDescription.getName();

			// get if the action is auto-executed
			boolean isAutoExecuted = actionDescription.getAutoExecute();
			
			
			//if auto executed comment must be replace by the "static comment" (idem for user)
			if(isAutoExecuted){
				this._comment = this._staticComment;
				this._userName = this._staticOwner;
				
			}
			
			// Store the current step (for the history)
			_propertyContainer.addNewStepProperty(this._userName,
					this._comment, actionName, isAutoExecuted,System.currentTimeMillis());

			// delete the comment from the transient map are overwrite it with
			// the Default comment value
			// ps.setString(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_COMMENT,DEFAULT_COMMENT);
		} else {
			LOGGER.info("The workflow Descriptor cannot be loaded");
		}

		
		/*
		 * Get the list of already stored files, compare it with the current
		 * one, and add the missing files
		 */
		List<String> alreadyStoredFiles = _propertyContainer
				.get_listOfAssociatedFile();

		// the list of missing files in the property container
		List<String> missingFiles = new ArrayList<String>();
		Iterator<String> fileIterator = _associatedFiles.iterator();
		while (fileIterator.hasNext()) {
			String filesToCheck = fileIterator.next();
			// check that the ressource is in the container, else add in the
			// list of missing files (chack that it is not in the list NotOffLine
			if (!alreadyStoredFiles.contains(filesToCheck) && ! _fileNotOffline.contains(filesToCheck)) {
				missingFiles.add(filesToCheck);
			}
		}
		
			
		//update the list of associated files
		_propertyContainer.addAssociatedFile(missingFiles);
		_propertyContainer.deleteNotOffLineFile(_fileNotOffline);
		
		/*
		 * Store the Workflow Instance Name
		 */
		_propertyContainer.set_workflowInstanceName(this._workflowInstanceName);

		/*
		 * Store the list of initial parameters
		 */
		_propertyContainer.set_parameters(_initialParameters);
		ps.setObject(ModuleSharedVariables.WORKFLOWPROPERTY_VARIABLENAME,
				_propertyContainer.convertInXML());
	}

}
