/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui.toolobject;

import org.opencms.workplace.CmsWorkplace;

/**
 * This class collect a number of variables that are used by the majority of JSP pages/relatives class to manage the UI
 * @author Sébastien Bianco
 *
 */
public class UISharedVariables {

	/** Location of the default workflow view jsp page. */
	public static final String WORKFLOW_MAINJSPPAGE_LOCATION = UISharedVariables.WORKFLOWVIEW_ROOT_LOCATION
			+ "workflows-main.jsp";
	
	/** Root location of the workflow view. */
	public static final String WORKFLOWVIEW_ROOT_LOCATION = CmsWorkplace.VFS_PATH_VIEWS+"workflows/";

	/** The first page to load when coming on view windows */
	public static final String DEFAULTPAGETOLOAD = WORKFLOWVIEW_ROOT_LOCATION
			+ "displayWorkflows.jsp";

	/**
	 * The parameter of the request that indicates the page to load
	 */
	public static final String PARAM_WORKFLOWPATH = "workflowpath";

	/**
	 * The name of the property that will be stored in the waiting area. This property represent the id of the selected workflow to visualize
	 */
	public static final String WAITINGAREA_PROPERTYNAME_WORFLOWID = "workflowID";

	/**
	 * The path of the folder that contains the main pages of the workflow (that is not in view folder)
	 */
	public static final String WORKFLOW_MAIN_FOLDER = CmsWorkplace.PATH_WORKPLACE+"workflows/";
	

	/**
	 * The path of the folder where all resources will be stored from root of VFS 
	 */
	public static final String WORKFLOW_RESOURCE_FOLDER_FROM_ROOT = CmsWorkplace.PATH_WORKPLACE+"resources/";
	
	/**
	 * The path of the folder where all resources will be stored
	 */
	public static final String WORKFLOW_RESOURCE_FOLDER = "workflows/";
	
	/**
	 * The path of the folder where all resources for the javascript will be stored
	 */
	public static final String WORKFLOW_JAVASCRIPT_RESOURCE_FOLDER = WORKFLOW_RESOURCE_FOLDER+"javascript/";
	
	/**
	 * The path of the folder where all resources for the ui will be stored
	 */
	public static final String WORKFLOW_UI_RESOURCE_FOLDER = WORKFLOW_RESOURCE_FOLDER+"ui/";
	
	/**
	 * The path of the folder where all resources for the ui will be stored
	 */
	public static final String WORKFLOW_UI_ICON_RESOURCE_FOLDER = WORKFLOW_UI_RESOURCE_FOLDER+"icon/";
	
}
