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
import org.opencms.main.CmsLog;

import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * This class is the master class of the all Function developed by Eurelis. It provides some method to update the workflow property with a new Historic Element. <br> <br> The required parameters to use this function is : <ol> <li> <b>The list of initial parameters:</b> This list must be stored in the transcientVar map with the key    {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_INITIALPARAMETERS}     (done by    {@link OSWorkflowManager#createInstanceOfWorkflow(String,String,String,List,CmsObject,List)}    )</li> <li> <b>The comment of enter by the user :</b> This comment must be stored in the transcientVar map with the key    {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_COMMENT}     (done by    {@link OSWorkflowManager#createInstanceOfWorkflow(String,String,String,List,CmsObject,List)}    )</li> <li> <b>The list of files:</b> This list must be stored in the transcientVar map with the key    {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST}     (done by    {@link OSWorkflowManager#createInstanceOfWorkflow(String,String,String,List,CmsObject)}    )</li> <li> <b>The CmsOject instance:</b> This object must be stored in the transcientVar map with the key    {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT}     (done by    {@link OSWorkflowManager#createInstanceOfWorkflow(String,String,String,List,CmsObject,List)}    )</li> <li> <b>The workflow instance name:</b> This object must be stored in the transcientVar map with the key    {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_INSTANCENAME}     (done by    {@link OSWorkflowManager#createInstanceOfWorkflow(String,String,String,List,CmsObject,List)}    )</li> </ol> <br>
 * @author     Sébastien Bianco
 */
public abstract class EurelisWorkflowFunction implements FunctionProvider {

	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(EurelisWorkflowFunction.class);

	/**
	 * The default comment to set
	 */
	private static final String DEFAULT_COMMENT = "";
	
	/**
	 * The default user to set
	 */
	private static final String DEFAULT_USER = "WF Engine";
	
	/**
	 * The key to search in map of argument to get a static comment
	 */
	private static final String PARAM_STATICCOMMENT_VALUE = "comment";
	
	/**
	 * The key to search in map of argument to get a static comment
	 */
	private static final String PARAM_STATICOWNER_VALUE = "actor";

	/**
	 * The list of selected Files
	 */
	protected List<String> _associatedFiles = new ArrayList<String>();

	/**
	 * The cmsObject used to get some information about the session/other
	 */
	protected CmsObject _cmsObject = null;

	/**
	 * The list of initial parameters
	 */
	protected List<Parameter> _initialParameters = new ArrayList<Parameter>();

	/**
	 * The last comment found in transient variable map
	 */
	protected String _comment = "No Comment";

	/**
	 * The name of the current user
	 */
	protected String _userName = "No User";
	
	/**
	 * The comment that could be stored in a static way (in WF Description) and could be use in case of automatic action
	 */
	protected String _staticComment = "No static comment";
	
	/**
	 * The comment that could be stored in a static way (in WF Description) and could be use in case of automatic action
	 */
	protected String _staticOwner = "No static Owner";
	

	/**
	 * The name of the instance of workflow
	 */
	protected String _workflowInstanceName = "No named instance";

	/**
	 * The propertyContainer associated to the workflow
	 * @uml.property  name="_propertyContainer"
	 * @uml.associationEnd  
	 */
	protected WorkflowPropertyContainer _propertyContainer = null;

	/**
	 * The last actionID found in transient variable map
	 */
	protected int _actionID = 0;

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
	public void execute(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		if (this.extractVariable(transientVars, args,ps)) {
			this.executeFunction(transientVars, args, ps);
			this.updateProperty(ps, transientVars);
		} else {
			LOGGER
					.warn("An error occurs during initialization of function fields... The function cannot be executed.");
		}
	}

	/**
	 * Extract the initial value of the fields
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
	 * @return <i>true</i> if all extraction is ok, <i>false</i> otherwise
	 */
	private boolean extractVariable(Map<String, Object> transientVars,
			Map<String,String> args,PropertySet ps) {

		// Get the CmsObject
		try {
			_cmsObject = (CmsObject) transientVars
					.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_CMSOBJECT);
		} catch (ClassCastException e) {
			LOGGER.info("The function " + this.getClass().getName()
					+ " doesn't receive a valid transcient variable "
					+ ModuleSharedVariables.FUNCTION_INPUTARGUMENT_CMSOBJECT);
			return false;
		}
		if (_cmsObject == null) {
			LOGGER.info("The function " + this.getClass().getName()
					+ " doesn't receive the transcient variable "
					+ ModuleSharedVariables.FUNCTION_INPUTARGUMENT_CMSOBJECT);

			return false;
		}

		// store the userName
		_userName = _cmsObject.getRequestContext().currentUser().getName();

		/*
		 * ¨Try to get property container. If the property container is
		 * accessible, use information stored inside it. Else use information of
		 * transient variable
		 */
		// Get the property container
		try {
			_propertyContainer = new WorkflowPropertyContainer(
					(String) ps
							.getObject(ModuleSharedVariables.WORKFLOWPROPERTY_VARIABLENAME));
		} catch (Exception e) {
			// This exception append if the property is null (that is the case
			// when the function is the first of the treatment)
			LOGGER.debug("WF | Exception occurs during load of property (exception ok if initialization of property)");
		}
		if (_propertyContainer != null) {
			_associatedFiles = _propertyContainer.get_listOfAssociatedFile();
			_initialParameters = _propertyContainer.get_parameters();
			_workflowInstanceName = _propertyContainer
					.get_workflowInstanceName();

		} else {

			// Get the list of selected files
			_associatedFiles = (List<String>) transientVars
					.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST);
			if (_associatedFiles == null)
				return false;

			// Get the list of initial parameters
			_initialParameters = (List<Parameter>) transientVars
					.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_INITIALPARAMETERS);
			if (_initialParameters == null)
				return false;

			// create _property container with initial selected files
			_propertyContainer = new WorkflowPropertyContainer(_associatedFiles);

			// get the workflow instance name
			_workflowInstanceName = (String) transientVars
					.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_INSTANCENAME);
			if (_workflowInstanceName == null)
				return false;
		}
		// Get the action ID
		try {
			Integer _actionIDInteger = (Integer) transientVars
					.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_ACTIONID);
			if (_actionIDInteger == null) {
				LOGGER
						.warn("The field actionID has not been found in the transient map");
				return false;
			}
			_actionID = _actionIDInteger.intValue();
		} catch (NumberFormatException e) {
			LOGGER.warn(ErrorFormatter.formatException(e));
			return false;
		}

		// Get the comment
		_comment = (String) transientVars
				.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_COMMENT);
		if (_comment == null)
			return false;
		
		
		/*
		 * try to get the static value of owner and comment
		 */
		// Get static comment
		String staticCommentValue = (String) args.get(PARAM_STATICCOMMENT_VALUE);	
		if(staticCommentValue != null){
			this._staticComment = staticCommentValue;
		}else{
			this._staticComment = DEFAULT_COMMENT;
		}
		
		// Get static actor
		String staticActorValue = (String) args.get(PARAM_STATICOWNER_VALUE);	
		if(staticActorValue != null){
			this._staticOwner = staticActorValue;
		}else{
			this._staticOwner = DEFAULT_USER;
		}

		// all goes right
		return true;

	}

	/**
	 * Update the property by adding all required informations
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
	 * @param ps
	 *            the property set to update
	 */
	protected void updateProperty(PropertySet ps, Map transientVars) {

		// get the action name
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor) transientVars
				.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_DESCRIPTOR);
		if (workflowDescriptor != null) {
			
			ActionDescriptor actionDescription = workflowDescriptor
					.getAction(_actionID);
			String actionName = actionDescription.getView();
			if(!StringChecker.isNotNullOrEmpty(actionName)){
				actionName = actionDescription.getName();
			}

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
			// list of missing files
			if (!alreadyStoredFiles.contains(filesToCheck)) {
				missingFiles.add(filesToCheck);
			}
		}

		_propertyContainer.addAssociatedFile(missingFiles);
		
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

	/**
	 * Execute The core of the function
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
	 */
	protected abstract void executeFunction(Map transientVars, Map args,
			PropertySet ps) throws WorkflowException;

}
