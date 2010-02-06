/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */
package com.eurelis.opencms.workflows.ui;

import java.text.MessageFormat;
import java.util.Locale;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.
 * <p>
 * 
 * @author Sébastien Bianco
 */
public final class Messages extends A_CmsMessageBundle {

	/** Name of the used resource bundle. */
	private static final String BUNDLE_NAME = "com.eurelis.opencms.workflows.ui.messages";

	/**
	 * Message constant for key in the resource bundle. (The message to display
	 * by default in the comment area)
	 */
	public static final String GUI_ENTERCOMMENT_0 = "GUI_ENTERCOMMENT_0";

	/**
	 * Message constant for key in the resource bundle. (The message display
	 * when a file doesn't exists anymore)
	 */
	public static final String GUI_ERROR_FILE_NOTEXISTANYMORE_0 = "GUI_ERROR_FILE_NOTEXISTANYMORE_0";

	/**
	 * Message constant for key in the resource bundle. (The prefix of the
	 * message display when a file doesn't exists anymore)
	 */
	public static final String GUI_ERROR_FILE_NOTEXISTANYMORE_PREFIX_0 = "GUI_ERROR_FILE_NOTEXISTANYMORE_PREFIX_0";

	/**
	 * Message constant for key in the resource bundle. (The message display
	 * when the user doesn't have read access right to a file)
	 */
	public static final String GUI_ERROR_FILE_READRIGHTDENY_0 = "GUI_ERROR_FILE_READRIGHTDENY_0";

	/**
	 * Message constant for key in the resource bundle. (The message display
	 * when the user doesn't have read access right)
	 */
	public static final String GUI_ERROR_READRIGHTDENY_0 = "GUI_ERROR_READRIGHTDENY_0";

	/**
	 * Message constant for key in the resource bundle. (The format of date to
	 * use)
	 */
	public static final String GUI_HISTORIC_DATEFORMAT_0 = "GUI_HISTORIC_DATEFORMAT_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns file path of the associated file section of workflow selection
	 * dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0 = "GUI_SELECTWORKFLOW_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0";

	/**
	 * Message constant for key in the resource bundle. (The title of windows
	 * select workflow)
	 */
	public static final String GUI_SELECTWORKFLOW_PAGETITLE_0 = "GUI_SELECTWORKFLOW_PAGETITLE_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section list of files of workflow selection dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_SECTIONNAME_LISTOFFILES_0 = "GUI_SELECTWORKFLOW_SECTIONNAME_LISTOFFILES_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section select workflow of workflow selection dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_SECTIONNAME_SELECTWORKFLOW_0 = "GUI_SELECTWORKFLOW_SECTIONNAME_SELECTWORKFLOW_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * sub-section comment of workflow display dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_SUBSECTIONNAME_COMMENT_0 = "GUI_SELECTWORKFLOW_SUBSECTIONNAME_COMMENT_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * sub-section list of available workflows of workflow display dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_SUBSECTIONNAME_SELECTWORKFLOW_0 = "GUI_SELECTWORKFLOW_SUBSECTIONNAME_SELECTWORKFLOW_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * sub-section workflow instance name of workflow display dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_SUBSECTIONNAME_WORKFLOWINSTANENAME_0 = "GUI_SELECTWORKFLOW_SUBSECTIONNAME_WORKFLOWINSTANENAME_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns Description of the workflow choice of workflow selection dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_DESCRIPTION_0 = "GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_DESCRIPTION_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns name of the workflow choice of workflow selection dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_NAME_0 = "GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_NAME_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns radio button of the workflow choice of workflow selection dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_RADIOBUTTON_0 = "GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_RADIOBUTTON_0";

	/**
	 * Message constant for key in the resource bundle. (The text displayed to
	 * introduce input "workflowInstanceName" of workflow display dialog)
	 */
	public static final String GUI_SELECTWORKFLOW_WORKFLOWINSTANENAME_TEXT_0 = "GUI_SELECTWORKFLOW_WORKFLOWINSTANENAME_TEXT_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns file path of the associated file section of workflow display
	 * dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0 = "GUI_WORKFLOWDISPLAY_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns action date of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONDATE_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONDATE_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns action name of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONNAME_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONNAME_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns auto of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_AUTO_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_AUTO_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns comment of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_COMMENT_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_COMMENT_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns End date of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ENDDATE_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ENDDATE_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns owner of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_OWNER_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_OWNER_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns Start date of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STARTDATE_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STARTDATE_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns Status of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STATUS_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STATUS_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * columns step name of the historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STEPNAME_0 = "GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STEPNAME_0";

	/**
	 * Message constant for key in the resource bundle. (The label creator of
	 * the information section of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_INFORMATIONS_CREATOR_0 = "GUI_WORKFLOWDISPLAY_INFORMATIONS_CREATOR_0";

	/**
	 * Message constant for key in the resource bundle. (The label instance id
	 * of the information section of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_INFORMATIONS_INSTANCEID_0 = "GUI_WORKFLOWDISPLAY_INFORMATIONS_INSTANCEID_0";

	/**
	 * Message constant for key in the resource bundle. (The label status of the
	 * information section of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_INFORMATIONS_STATUS_0 = "GUI_WORKFLOWDISPLAY_INFORMATIONS_STATUS_0";

	/**
	 * Message constant for key in the resource bundle. (The label workflow name
	 * of the information section of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWNAME_0 = "GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWNAME_0";

	/**
	 * Message constant for key in the resource bundle. (The label type of the
	 * information section of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWTYPE_0 = "GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWTYPE_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section associated files of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_SECTIONNAME_ASSOCIATEDFILES_0 = "GUI_WORKFLOWDISPLAY_SECTIONNAME_ASSOCIATEDFILES_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section historic of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_SECTIONNAME_HISTORIC_0 = "GUI_WORKFLOWDISPLAY_SECTIONNAME_HISTORIC_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section information of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_SECTIONNAME_INFORMATION_0 = "GUI_WORKFLOWDISPLAY_SECTIONNAME_INFORMATION_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the
	 * section select action of workflow display dialog)
	 */
	public static final String GUI_WORKFLOWDISPLAY_SECTIONNAME_SELECTACTION_0 = "GUI_WORKFLOWDISPLAY_SECTIONNAME_SELECTACTION_0";

	/**
	 * Message constant for key in the resource bundle. (The title of windows
	 * Workflow display)
	 */
	public static final String GUI_WORKFLOWDISPLAY_TITLE_0 = "GUI_WORKFLOWDISPLAY_TITLE_0";

	/**
	 * Message constant for key in the resource bundle. (the action associated
	 * to column delete)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_DELETE_0 = "GUI_WORKFLOWLIST_ACTION_DELETE_0";

	/**
	 * Message constant for key in the resource bundle. (the tooltip text of the
	 * action associated to column delete)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_DELETE_HELP_0 = "GUI_WORKFLOWLIST_ACTION_DELETE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (the action associated
	 * to column state)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_STATE_0 = "GUI_WORKFLOWLIST_ACTION_STATE_0";

	/**
	 * Message constant for key in the resource bundle. (the tooltip text of the
	 * action associated to column state)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_STATE_HELP_0 = "GUI_WORKFLOWLIST_ACTION_STATE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (the action associated
	 * to column workflow)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_WORKFLOW_0 = "GUI_WORKFLOWLIST_ACTION_WORKFLOW_0";

	/**
	 * Message constant for key in the resource bundle. (the tooltip text of the
	 * action associated to column workflow)
	 */
	public static final String GUI_WORKFLOWLIST_ACTION_WORKFLOW_HELP_0 = "GUI_WORKFLOWLIST_ACTION_WORKFLOW_0";

	/** Message constant for key in the resource bundle. (Label of the help zone) */
	public static final String GUI_WORKFLOWS_MENU_HELP_GROUP_0 = "GUI_WORKFLOWS_MENU_HELP_GROUP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the workflows
	 * menu zone)
	 */
	public static final String GUI_WORKFLOWS_MENU_WORKFLOWS_GROUP_0 = "GUI_WORKFLOWS_MENU_WORKFLOWS_GROUP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * workflow description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column workflow description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * workflow rights file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column workflow rights file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * workflow title)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_0";

	/**
	 * Message constant for key in the resource bundle. (Tooltip text of the
	 * column workflow title)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (the label that wille be
	 * use for detailled field description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTION_LABEL_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTION_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (the label that wille be
	 * use for detailled field description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTIONFILE_LABEL_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTIONFILE_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (the label that wille be
	 * use for detailled field parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_PARAMETERS_LABEL_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_PARAMETERS_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (the label that wille be
	 * use for detailled field rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_RIGHTS_LABEL_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_RIGHTS_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field description)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field description file)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field parameters)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field rights)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_HELP_0 = "GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_HELP_0";

	/** Message constant for key in the resource bundle. (Frame name) */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_FRAME_TITLE_0 = "GUI_WORKFLOWSCONFIGUREDLIST_FRAME_TITLE_0";

	/**
	 * Message constant for key in the resource bundle. (The help message to
	 * display when passing on the search button)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_HELPTEXT_0 = "GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_HELPTEXT_0";

	/**
	 * Message constant for key in the resource bundle. (The label to display
	 * for the search button)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_LABEL_0 = "GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (The title of windows
	 * Configured workflow display)
	 */
	public static final String GUI_WORKFLOWSCONFIGUREDLIST_WINDOWNAME_0 = "GUI_WORKFLOWSCONFIGUREDLIST_WINDOWNAME_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the columns
	 * deleteicon )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_DELETEICON_0 = "GUI_WORKFLOWSLIST_COLS_DELETEICON_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * columns deleteicon )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_DELETEICON_HELP_0 = "GUI_WORKFLOWSLIST_COLS_DELETEICON_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * Instance ID)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_INSTANCEID_0 = "GUI_WORKFLOWSLIST_COLS_INSTANCEID_0";

	/**
	 * Message constant for key in the resource bundle. (Tooltip text of the
	 * column Instance ID)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_INSTANCEID_HELP_0 = "GUI_WORKFLOWSLIST_COLS_INSTANCEID_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * Instance Name)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_INSTANCENAME_0 = "GUI_WORKFLOWSLIST_COLS_INSTANCENAME_0";

	/**
	 * Message constant for key in the resource bundle. (Tooltip text of the
	 * column Instance Name)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_INSTANCENAME_HELP_0 = "GUI_WORKFLOWSLIST_COLS_INSTANCENAME_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * last action )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_LASTACTION_0 = "GUI_WORKFLOWSLIST_COLS_LASTACTION_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column last action )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_LASTACTION_HELP_0 = "GUI_WORKFLOWSLIST_COLS_LASTACTION_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * next actions)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_0 = "GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column next actions)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_HELP_0 = "GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * number of files)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_0 = "GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * column number of files)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_HELP_0 = "GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * State)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATE_0 = "GUI_WORKFLOWSLIST_COLS_STATE_0";

	/**
	 * Message constant for key in the resource bundle. (Tooltip text of the
	 * State)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATE_HELP_0 = "GUI_WORKFLOWSLIST_COLS_STATE_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the columns
	 * ended stateicon )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATEICON_ENDED_0 = "GUI_WORKFLOWSLIST_COLS_STATEICON_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * columns ended stateicon)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATEICON_ENDED_HELP_0 = "GUI_WORKFLOWSLIST_COLS_STATEICON_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the columns
	 * on-going stateicon )
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATEICON_ONGOING_0 = "GUI_WORKFLOWSLIST_COLS_STATEICON_ONGOING_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * columns on-going stateicon)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_STATEICON_ONGOING_HELP_0 = "GUI_WORKFLOWSLIST_COLS_STATEICON_ONGOING_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the columns
	 * workflowicon)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_0 = "GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip text of the
	 * columns workflowicon)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_HELP_0 = "GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of the column
	 * Name)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_0 = "GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_0";

	/**
	 * Message constant for key in the resource bundle. (Tooltip text of the
	 * column Name)
	 */
	public static final String GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_HELP_0 = "GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (The message display in
	 * dialog ListOfWorkflow (table content, column available actions) when the
	 * user doesn't have write right to a workflow)
	 */
	public static final String GUI_WORKFLOWSLIST_CONTENT_AVAILABLEACTION_WRITERIGHTDENY_0 = "GUI_WORKFLOWSLIST_CONTENT_AVAILABLEACTION_WRITERIGHTDENY_0";

	/**
	 * Message constant for key in the resource bundle. (Text to display if
	 * status = ended in the columns delete)
	 */
	protected static final String GUI_WORKFLOWSLIST_CONTENT_STATUS_CANBEDELETE_0 = "GUI_WORKFLOWSLIST_CONTENT_STATUS_CANBEDELETE_0";

	/**
	 * Message constant for key in the resource bundle. (Text to display if
	 * status = ended)
	 */
	public static final String GUI_WORKFLOWSLIST_CONTENT_STATUS_ENDED_0 = "GUI_WORKFLOWSLIST_CONTENT_STATUS_ENDED_0";

	/**
	 * Message constant for key in the resource bundle. (Text to display if
	 * status = underway)
	 */
	public static final String GUI_WORKFLOWSLIST_CONTENT_STATUS_UNDERWAY_0 = "GUI_WORKFLOWSLIST_CONTENT_STATUS_UNDERWAY_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field last action comment)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field last action comment)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field last action owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field last action owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field list of files)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field list of files)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to hide
	 * detailled field owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to hide detailled field owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Label of detailled
	 * field last action comment)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_LASTACTIONCOMMENT_LABEL_0 = "GUI_WORKFLOWSLIST_DETAIL_LASTACTIONCOMMENT_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Label of detailled
	 * field last action owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_LASTACTIONOWNER_LABEL_0 = "GUI_WORKFLOWSLIST_DETAIL_LASTACTIONOWNER_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Label of detailled
	 * field list of files)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_LISTOFFILES_LABEL_0 = "GUI_WORKFLOWSLIST_DETAIL_LISTOFFILES_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Label of detailled
	 * field owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_OWNER_LABEL_0 = "GUI_WORKFLOWSLIST_DETAIL_OWNER_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field last action comment)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field last action comment)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field last action owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field last action owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field list of files)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field list of files)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Action to show
	 * detailled field owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_0";

	/**
	 * Message constant for key in the resource bundle. (tooltip of the action
	 * to show detailled field owner)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_HELP_0 = "GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (Text if no files for
	 * list of files details)
	 */
	public static final String GUI_WORKFLOWSLIST_DETAIL_WARNING_NOFILE_0 = "GUI_WORKFLOWSLIST_DETAIL_WARNING_NOFILE_0";

	/**
	 * Message constant for key in the resource bundle. (Name of the main
	 * windows)
	 */
	public static final String GUI_WORKFLOWSLIST_FRAME_TITLE_0 = "GUI_WORKFLOWSLIST_FRAME_TITLE_0";

	/**
	 * Message constant for key in the resource bundle. (The help message to
	 * display when passing on the search button)
	 */
	public static final String GUI_WORKFLOWSLIST_SEARCH_HELPTEXT_0 = "GUI_WORKFLOWSLIST_SEARCH_HELPTEXT_0";

	/**
	 * Message constant for key in the resource bundle. (The label to display
	 * for the search button)
	 */
	public static final String GUI_WORKFLOWSLIST_SEARCH_LABEL_0 = "GUI_WORKFLOWSLIST_SEARCH_LABEL_0";

	/** Message constant for key in the resource bundle. (Windows name) */
	public static final String GUI_WORKFLOWSLIST_WINDOWNAME_0 = "GUI_WORKFLOWSLIST_WINDOWNAME_0";

	/** Static instance member. */
	private static final I_CmsMessageBundle INSTANCE = new Messages();

	/**
	 * Message constant for key in the resource bundle. (The helptext of the combobox)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_COMBOBOX_HELP_0	= "GUI_WORKFLOWSLIST_ACTION_COMBOBOX_HELP_0";

	/**
	 * Message constant for key in the resource bundle. (The label of the combobox)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_COMBOBOX_LABEL_0	= "GUI_WORKFLOWSLIST_ACTION_COMBOBOX_LABEL_0";

	/**
	 * Message constant for key in the resource bundle. (The name of the filter in combobox corresponding to the workflow the current user is the creator of)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_FILTERNAME_MY	= "GUI_WORKFLOWSLIST_ACTION_FILTERNAME_MY";

	/**
	 * Message constant for key in the resource bundle. (The name of the filter in combobox corresponding to all workflows)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_FILTERNAME_ALL	= "GUI_WORKFLOWSLIST_ACTION_FILTERNAME_ALL";

	
	/**
	 * Message constant for key in the resource bundle. (The name of the filter in combobox corresponding to the workflow completed)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_FILTERNAME_COMPLETED	= "GUI_WORKFLOWSLIST_ACTION_FILTERNAME_COMPLETED";

	
	/**
	 * Message constant for key in the resource bundle. (The name of the filter in combobox corresponding to the workflow the current user is the creator of)
	 */
	public static final String	GUI_WORKFLOWSLIST_ACTION_FILTERNAME_UNDERWAY	= "GUI_WORKFLOWSLIST_ACTION_FILTERNAME_UNDERWAY";

	public static final String	GUI_SELECTWORKFLOW_ASSOCIATEDFILES_LABEL_INPUT_0	= "GUI_SELECTWORKFLOW_ASSOCIATEDFILES_LABEL_INPUT_0";

	public static final String	GUI_SELECTWORKFLOW_WORKFLOWCHOICE_ERROR_NOAVAILABLEWORKFLOWS_0	= "GUI_SELECTWORKFLOW_WORKFLOWCHOICE_ERROR_NOAVAILABLEWORKFLOWS_0";

	
	
	/**
	 * Returns an instance of this localized message accessor.
	 * <p>
	 * 
	 * @return an instance of this localized message accessor
	 */
	public static I_CmsMessageBundle get() {
		return INSTANCE;
	}

	/**
	 * Get the message corresponding to an internationilized string
	 * 
	 * @param messageName
	 *            the name of the message
	 * @param locale
	 *            the current locale
	 * @return the message to display
	 */
	public static String getMessages(String messageName, Locale locale) {
		CmsMessageContainer messageContainer = Messages.get().container(
				messageName);
		return new MessageFormat(messageContainer.key(locale), locale)
				.format(new Object[] { messageContainer.key(locale) });
	}

	/**
	 * Hides the public constructor for this utility class.
	 * <p>
	 */
	private Messages() {

		// hide the constructor
	}

	/**
	 * Returns the bundle name for this OpenCms package.
	 * <p>
	 * 
	 * @return the bundle name for this OpenCms package
	 */
	public String getBundleName() {

		return BUNDLE_NAME;
	}
}