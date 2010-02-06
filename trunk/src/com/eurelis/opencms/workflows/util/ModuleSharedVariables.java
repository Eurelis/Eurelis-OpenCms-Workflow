/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.util;

import org.opencms.workplace.CmsWorkplace;

/**
 * This class will contain a list of shared variable that is use in many places in the module
 * @author Sébastien Bianco
 *
 */
public class ModuleSharedVariables {

	/**
	 * The String that will be use as separator so as to separate the list of file to associate with the workflow
	 */
	public static final String LIST_SERIALIZER_SEPARATOR = ";";
	
	/**
	 * The property name of the request context attribute that contains the list
	 * of selected files
	 */
	public static final String WAITINGAREA_PROPERTYNAME_LISTOFSELECTEDFILES = "listOfSelectedFiles";
	
	/**
	 * The File separator of the system
	 */
	public static final String SYSTEM_FILE_SEPARATOR = System.getProperty("file.separator");
	
	/**
	 * The name of the property where will be stored the information associated to a workflow
	 */
	public static final String WORKFLOWPROPERTY_VARIABLENAME = "eurelisProperty";
	
	/**
	 * The name of the property that will be stored in the waiting area. This property represent the next page to load when opening the workflows view.
	 */
	public static final String WAITINGAREA_PROPERTYNAME_NEXTWORKFLOWSVIEWMAINPAGE = "nextWorkflowsViewsMainPage";
		
	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to the list of files associated with the instance of workflow 
	 */
	public static final String FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST = "associatedFileList";

	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to the CmsObject containing the main information about the current session
	 */
	public static final String FUNCTION_INPUTARGUMENT_CMSOBJECT = "cmsObject";
	
	/**
	 * The folder that will content all that is relatives to the Workflow management
	 */
	public static final String WORKFLOW_WORKPLACE_FOLDER =CmsWorkplace.PATH_WORKPLACE+"workflows/";

	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to list of initial parameters of the workflow.
	 */
	public static final String FUNCTION_INPUTARGUMENT_INITIALPARAMETERS = "initialParameters";

	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to comment enter by the user.
	 */
	public static final String FUNCTION_INPUTARGUMENT_COMMENT = "comment";

	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to current Action ID.
	 */
	public static final Object FUNCTION_INPUTARGUMENT_ACTIONID = "actionId";
	
	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to current workflow descriptor.
	 */
	public static final Object FUNCTION_INPUTARGUMENT_DESCRIPTOR = "descriptor";
	
	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to current workflow entry object.
	 */
	public static final Object FUNCTION_INPUTARGUMENT_ENTRY = "entry";

	/**
	 * The name of the key in the map of inputs (workflow initialization) that is given as parameter of OSWorkflow functions. This key is associated to current workflow instance name.
	 */
	public static final String FUNCTION_INPUTARGUMENT_INSTANCENAME = "instanceName";
}
