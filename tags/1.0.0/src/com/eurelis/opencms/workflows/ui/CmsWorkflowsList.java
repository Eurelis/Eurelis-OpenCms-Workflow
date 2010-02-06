/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsRuntimeException;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsHtmlList;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListDirectAction;
import org.opencms.workplace.list.CmsListDropdownAction;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListItemActionIconComparator;
import org.opencms.workplace.list.CmsListItemDetails;
import org.opencms.workplace.list.CmsListItemDetailsFormatter;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListOrderEnum;
import org.opencms.workplace.list.CmsListSearchAction;
import org.opencms.workplace.tools.A_CmsHtmlIconButton;

import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowDisplayContainer;

/**
 * This class implements the windows that display the list of ongoing and ended workflows.
 * @author   Sébastien Bianco
 */
public class CmsWorkflowsList extends A_CmsListDialog {

	/** The URI of the JSP that load this Dialog object */
	public static final String DIALOG_URI = UISharedVariables.WORKFLOWVIEW_ROOT_LOCATION
			+ "displayWorkflows.jsp";

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(CmsWorkflowsList.class);

	/** list id constant. */
	public static final String LIST_ID = "listWorkflow";

	/** list column id constant. (the ID of the instance of the workflow) */
	public static final String LIST_COLUMN_INSTANCEID = "workflowID";

	/**
	 * list column id constant. (the name of the workflow that have been
	 * instancied)
	 */
	public static final String LIST_COLUMN_NAME = "workflowName";

	/** list column id constant. (the state of the instance of the workflow) */
	public static final String LIST_COLUMN_STATE = "workflowState";

	/**
	 * list item detail id constant. (the columns with an icon that represent
	 * the workflow)
	 */
	public static final String LIST_COLUMN_WORKFLOWICON = "workflowWorkflowIcon";

	/**
	 * list item detail id constant. (the columns with an icon that represent
	 * the state ended for a workflow)
	 */
	public static final String LIST_COLUMN_STATEICON_ENDED = "workflowStateIcon_Ended";

	/**
	 * list column id constant. (the value of the state of the instance of the
	 * workflow)
	 */
	private static final String LIST_COLUMN_STATE_VALUE = "workflowStateValue";

	/**
	 * list item detail id constant. (the columns with an icon that represent
	 * the state for a workflow)
	 */
	public static final String LIST_COLUMN_DELETEICON = "workflowStateIcon";

	/**
	 * list item detail id constant. (the columns with an icon that represent
	 * the state ongoing for a workflow)
	 */
	public static final String LIST_COLUMN_STATEICON_ONGOING = "workflowStateIcon_Ongoing";

	/**
	 * list item detail id constant. (the columns that indicate the list of
	 * available actions)
	 */
	public static final String LIST_COLUMN_NEXTACTIONS = "workflowAction";

	/**
	 * list item detail id constant. (the columns that indicate the last action
	 * done)
	 */
	public static final String LIST_COLUMN_LASTACTION = "workflowLastAction";

	/**
	 * list item detail id constant. (the columns that indicate the number of
	 * associated files)
	 */
	public static final String LIST_COLUMN_NUMBEROFFILES = "workflowNumberOfFiles";

	/**
	 * list item detail id constant. (the columns with the name of the instance)
	 */
	public static final String LIST_COLUMN_INSTANCENAME = "instanceName";
	
	/**
	 * list item detail id constant. (the columns that indicate is the User is allowed to write the WF)
	 */
	protected static final String LIST_COLUMN_ACCESSTOWRITE_VALUE = "allowedToWrite";

	/** list item detail id constant. (display the creator of the workflow) */
	public static final String LIST_DETAIL_CREATOR = "workflowDetailledCreator";

	/** list item detail id constant. (display the owner of the last action) */
	public static final String LIST_DETAIL_LASTACTIONOWNER = "workflowDetailledLastActionOwner";

	/** list item detail id constant. (display the comment of the last action) */
	public static final String LIST_DETAIL_LASTACTIONCOMMENT = "workflowDetailledLastActionComment";

	/** list item detail id constant. (display the list of associated files) */
	public static final String LIST_DETAIL_LISTOFFILES = "workflowDetailledListOfFiles";

	/** list item detail id constant. (the size of the column instanceID) */
	public static final String LIST_COLUMNSIZE_INSTANCESID = "6%";

	/** list item detail id constant. (the size of the column stateIcon_ended) */
	public static final String LIST_COLUMNSIZE_WORKFLOW_ICON = "3%";

	/** list item detail id constant. (the size of the column stateIcon_ended) */
	public static final String LIST_COLUMNSIZE_LASTACTION = "15%";

	/** list item detail id constant. (the size of the column number of files) */
	public static final String LIST_COLUMNSIZE_NUMBEROFFILES = "20%";

	/** list item detail id constant. (the size of the column next actions) */
	public static final String LIST_COLUMNSIZE_NEXTACTIONS = "25%";

	/** list item detail id constant. (the size of the column name) */
	public static final String LIST_COLUMNSIZE_NAME = "15%";

	/** list item detail id constant. (the size of the column state) */
	public static final String LIST_COLUMNSIZE_STATE = "10%";

	/** list item detail id constant. (the size of the column stateIcon_ended) */
	public static final String LIST_COLUMNSIZE_DELETEICON = "3%";

	/** list item detail id constant. (the size of the column stateIcon_ended) */
	public static final String LIST_COLUMNSIZE_INSTANCENAME = "15%";

	/** The path of the icons */
	public static final String ICONPATH = UISharedVariables.WORKFLOW_UI_ICON_RESOURCE_FOLDER;

	/** list item detail id constant. (path for icon workflow) */
	public static final String LIST_ICONPATH_WORKFLOW = ICONPATH
			+ "workflow.png";

	/** list item detail id constant. (path for icon stateicon ended) */
	public static final String LIST_ICONPATH_STATEICON_ENDED = ICONPATH
			+ "ended.png";

	/** list item detail id constant. (path for icon stateicon Ongoing) */
	public static final String LIST_ICONPATH_STATEICON_ONGOING = ICONPATH
			+ "underway.png";

	/**
	 * list item detail id constant. (the name of action associated to
	 * workflowicon column)
	 */
	public static final String LIST_ACTION_WORKFLOW = "workflowActionWorkflow";

	/**
	 * list item detail id constant. (the name of action associated to state
	 * column)
	 */
	public static final String LIST_ACTION_STATE = "workflowActionState";
	
	/**
	 * list item detail id constant. (the name of action associated to delete
	 * column)
	 */
	private static final String LIST_ACTION_DELETE = "workflowActionDelete";
	
	/**
	 * the id of the combo-box action
	 */
	private static final String LIST_ACTION_COMBOBOX = "comboboxActionFilter";

	/**
	 * The JSP page to open when clicking on a Workflow button
	 */
	private static final String LIST_ACTION_REDIRECT_LINK = UISharedVariables.WORKFLOW_MAIN_FOLDER
			+ "displayWorkflow.jsp";
	
	
	
	/**
	 * The name of the filter corresponding to all workflow
	 */
	private static final String FILTER_ALL = "all";
	
	/**
	 * The name of the filter corresponding to workflow completed
	 */
	private static final String FILTER_COMPLETED = "completed";
	
	/**
	 * The name of the filter corresponding to workflow underway
	 */
	private static final String FILTER_UNDERWAY = "underway";
	
	/**
	 * The name of the filter corresponding to workflow that current user is creator
	 */
	private static final String FILTER_MY = "my";
	
	
	
	/**
	 * The list of filter available for the combobox
	 */
	private static final String [] availableFilterForCombo = new String[]{FILTER_ALL,FILTER_COMPLETED,FILTER_UNDERWAY,FILTER_MY};
	
	/**
	 * The list of messages associated to each available filter for the combobox
	 */
	private static final String [] availableFilterMessageForCombo = new String[]{Messages.GUI_WORKFLOWSLIST_ACTION_FILTERNAME_ALL,Messages.GUI_WORKFLOWSLIST_ACTION_FILTERNAME_COMPLETED,Messages.GUI_WORKFLOWSLIST_ACTION_FILTERNAME_UNDERWAY,Messages.GUI_WORKFLOWSLIST_ACTION_FILTERNAME_MY};
	
	
	

	/** list item detail id constant. (path for icon delete) */
	private static final String LIST_ICONPATH_DELETEICON = ICONPATH
	+ "deletecontent.png";

	


	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * @uml.property  name="_uiManager"
	 * @uml.associationEnd  
	 */
	private CmsWorkflowUIManager _uiManager = CmsWorkflowUIManager
			.getInstance(getCms().getRequestContext().currentUser().getName());

	/** a set of action id's to use for edition. */
	private static Set m_editActionIds = new HashSet();

	/**
	 * The OSWorkflowManager instance
	 * @uml.property  name="_osWorkflowManager"
	 * @uml.associationEnd  
	 */
	private A_OSWorkflowManager _osWorkflowManager;

	/**
	 * The list of workflows to display
	 */
	Map<String, WorkflowDisplayContainer> _workflowsToDisplay;

	/**
	 * Public constructor.
	 * <p>
	 * 
	 * @param jsp
	 *            an initialized JSP action element
	 * @param listId
	 *            the id of the list
	 * @param listName
	 *            the list name
	 */
	public CmsWorkflowsList(CmsJspActionElement jsp, String listId,
			CmsMessageContainer listName) {
		super(jsp, listId, listName, LIST_COLUMN_INSTANCEID,
				CmsListOrderEnum.ORDER_DESCENDING, null);

		this._osWorkflowManager = A_OSWorkflowManagerFactory.getInstance();
		this._workflowsToDisplay = this._osWorkflowManager
				.getOngoingWorkflows(getCms());

		// set the selected item (read request)
		String selItem = getJsp().getRequest().getParameter(PARAM_SEL_ITEMS);
		if (selItem != null && !selItem.equals("") && !selItem.equals("null")) {
			this.setParamSelItems(selItem);
		}

		// update CmsWoroflowUIManager
		_uiManager.set_currentPagePath(DIALOG_URI);

	}

	/**
	 * Public constructor.
	 * <p>
	 * 
	 * @param jsp
	 *            an initialized JSP action element
	 */
	public CmsWorkflowsList(CmsJspActionElement jsp) {
		this(jsp, LIST_ID, Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_WINDOWNAME_0));
	}

	/**
	 * Public constructor with JSP variables.
	 * <p>
	 * 
	 * @param context
	 *            the JSP page context
	 * @param req
	 *            the JSP request
	 * @param res
	 *            the JSP response
	 */
	public CmsWorkflowsList(PageContext context, HttpServletRequest req,
			HttpServletResponse res) {
		this(new CmsJspActionElement(context, req, res));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#executeListMultiActions()
	 */
	@Override
	public void executeListMultiActions() throws IOException, ServletException,
			CmsRuntimeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
	 */
	@Override
	public void executeListSingleActions() throws IOException,
			ServletException, CmsRuntimeException {

		// get ID of the workflow
		String workflowId = getSelectedItem().get(LIST_COLUMN_INSTANCEID)
				.toString();

		if (getParamListAction().equals(LIST_ACTION_WORKFLOW)) {
			this.actionCommitOpenWorkflow(workflowId);
			return;
		}

		if(getParamListAction().equals(LIST_ACTION_DELETE)){
			LOGGER.debug("WF | required delete of "+workflowId);
			this._osWorkflowManager.deleteWorkflow(getCms(),Long.parseLong(workflowId));
			this.refreshList();
		}
		
		// save state of the list
		listSave();

	}
	
	/**
	 * Open a new windows with detail of the selected workflow
	 * @param workflowId the ID of the selected workflow
	 * @throws ServletException 
	 * @throws IOException 
	 */
	private void actionCommitOpenWorkflow(String workflowId) throws IOException, ServletException{
		// get userName
		String userName = getCms().getRequestContext().currentUser().getName();		

		/*WaitingDataArea.getInstance().storeObject(
				UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID,
				workflowId, userName);		*/
		
		// Set the target by adding the CmsContext path
		String target = LIST_ACTION_REDIRECT_LINK;

		// Set the ID of workflow in the request
		getJsp().getRequest().setAttribute(
				UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID,
				workflowId);
		getJsp().getRequest().setAttribute(
				UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID,
				workflowId);

		// Redirect the request !
		this.setForwarded(true);
		Map<String, String[]> newParams = new HashMap<String, String[]>();

		/*
		 * Copy the request map in the new map
		 */
		Iterator<Entry<String, String[]>> paramsIterator = getParameterMap()
				.entrySet().iterator();
		while (paramsIterator.hasNext()) {
			Entry<String, String[]> entry = paramsIterator.next();
			newParams.put(entry.getKey(), entry.getValue());
		}

		/*
		 * Create map with params to add in the request map
		 */
		Map<String, String> paramsToAdd = new HashMap<String, String>();
		paramsToAdd.put(
				UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID,
				workflowId);
		// set action parameter to initial dialog call
		paramsToAdd.put(CmsDialog.PARAM_ACTION, CmsDialog.DIALOG_INITIAL);
		paramsToAdd.put(UISharedVariables.PARAM_WORKFLOWPATH, CmsEncoder
				.encode(LIST_ACTION_REDIRECT_LINK, getJsp()
						.getRequestContext().getEncoding()));

		/*
		 * Complete the new request map
		 */
		newParams.putAll(CmsRequestUtil.createParameterMap(paramsToAdd));

		// forward the request
		target = UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION;
		_uiManager.jspForwardPage(this, target, newParams);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#fillDetails(java.lang.String)
	 */
	@Override
	protected void fillDetails(String detailId) {

		// get content
		List<CmsListItem> workflowsItem = getList().getAllContent();
		Iterator<CmsListItem> workflowsItemsIterator = workflowsItem.iterator();
		while (workflowsItemsIterator.hasNext()) {
			CmsListItem item = workflowsItemsIterator.next();
			WorkflowDisplayContainer workflowsContainer = this._workflowsToDisplay
					.get(item.getId());

			// fill creator
			if (detailId.equals(LIST_DETAIL_CREATOR)) {
				StringBuffer htmlCreator = new StringBuffer(512);
				htmlCreator.append(workflowsContainer.get_creator());
				item.set(detailId, htmlCreator.toString());
			}

			// fill last action owner
			if (detailId.equals(LIST_DETAIL_LASTACTIONOWNER)) {
				StringBuffer htmlLastActionOwner = new StringBuffer(512);
				htmlLastActionOwner.append(workflowsContainer
						.get_lastActionOwner());
				item.set(detailId, htmlLastActionOwner.toString());
			}

			// fill last action comment
			if (detailId.equals(LIST_DETAIL_LASTACTIONCOMMENT)) {
				StringBuffer htmlLastActionComment = new StringBuffer(512);
				htmlLastActionComment.append(workflowsContainer
						.get_lastActionComment());
				item.set(detailId, htmlLastActionComment.toString());
			}

			// fill list of files
			if (detailId.equals(LIST_DETAIL_LISTOFFILES)) {
				StringBuffer htmlListOfFiles = new StringBuffer(512);
				if (workflowsContainer.get_listOfFiles() != null
						&& workflowsContainer.get_listOfFiles().size() > 0) {
					Iterator<String> listOfFileIterator = workflowsContainer
							.get_listOfFiles().iterator();
					htmlListOfFiles.append("<ul>");
					while (listOfFileIterator.hasNext()) {
						htmlListOfFiles.append("<li>");
						htmlListOfFiles.append(listOfFileIterator.next());
						htmlListOfFiles.append("</li>");
					}
					htmlListOfFiles.append("</ul>");
				} else {
					/*
					 * If there is no files, display a localized message
					 */
					Locale locale = this.getLocale();
					CmsMessageContainer messageContainer = Messages
							.get()
							.container(
									Messages.GUI_WORKFLOWSLIST_DETAIL_WARNING_NOFILE_0);

					htmlListOfFiles
							.append(new MessageFormat(messageContainer
									.key(locale), locale)
									.format(new Object[] { messageContainer
											.key(locale) }));
				}
				item.set(detailId, htmlListOfFiles.toString());
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#getListItems()
	 */
	@Override
	protected List<CmsListItem> getListItems() throws CmsException {
		//LOGGER.debug("WF | enter getListItem()");
		
		// Create the list of result
		List<CmsListItem> ret = new ArrayList<CmsListItem>();

		// Get the list of object to display
		List<WorkflowDisplayContainer> workflowsToDisplay = new ArrayList<WorkflowDisplayContainer>(
				_workflowsToDisplay.values());
		
		CmsHtmlList list = super.getList();
		CmsListDropdownAction listAction = null;
        if (list != null) {
            listAction = ((CmsListDropdownAction)list.getMetadata().getIndependentAction(LIST_ACTION_COMBOBOX));
        }

		// set Content to display
		Iterator<WorkflowDisplayContainer> workflowsToDisplayIterator = workflowsToDisplay
				.iterator();
		while (workflowsToDisplayIterator.hasNext()) {
			WorkflowDisplayContainer workflow = workflowsToDisplayIterator
					.next();

			// create the CmsListItem with the list of informations to display
			CmsListItem item = getList()
					.newItem(workflow.get_instanceID() + "");
			setUserData(workflow, item);
			if(listAction!=null){
				//filer depending mode
				if(this.matchTheFilter(listAction.getSelection(),workflow)){
					ret.add(item);
				}
				
			}else{	
				//if filter not initialized, display all
				ret.add(item);
			}
		}

		return ret;
	}
	
	/**
	 * Check the value of the item and the value of the filter
	 * @param selection the selected mode of the filter
	 * @param workflow the item that will be add in the list if it match the filter
	 * @return <b>true</b> if the item match the filter (and so must be added), <b>false</b> otherwise
	 */
	 private boolean matchTheFilter(String selection, WorkflowDisplayContainer workflow) {
		 //LOGGER.debug("WF | selection = "+selection+" - workflow = "+workflow);
		 if(selection.equals(FILTER_ALL)){
			 return true;
		 }
		 if(selection.equals(FILTER_COMPLETED)){
			return workflow.is_isEnded();
		 }
		 if(selection.equals(FILTER_UNDERWAY)){
			 return !workflow.is_isEnded();
		 }
		 if(selection.equals(FILTER_MY)){
			 String creator = workflow.get_creator();
			 String currentUser = this.getJsp().getRequestContext().currentUser().getName();
			 if(currentUser!=null){
				 return currentUser.equals(creator);
			 }
		 }
		 
		return false;
	}

	/**
     * @see org.opencms.workplace.list.A_CmsListDialog#getList()
     */
    public CmsHtmlList getList() {

        CmsHtmlList list = super.getList();
        // get parameter
        String m_filter = getJsp().getRequest().getParameter(LIST_ACTION_COMBOBOX + CmsListDropdownAction.SUFFIX_PARAM);
        CmsListDropdownAction listAction = null;
        if (list != null) {
            listAction = ((CmsListDropdownAction)list.getMetadata().getIndependentAction(LIST_ACTION_COMBOBOX));
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_filter)) {
                // if no param, get old value
                m_filter = listAction.getSelection();
            }
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_filter)) {
            m_filter = FILTER_ALL;
        }
        if (listAction != null) {
            listAction.setSelection(m_filter);
        }
        return list;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
	 */
	@Override
	protected void setMultiActions(CmsListMetadata arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets all needed data of the workflow into the list item object.
	 * <p>
	 * 
	 * @param workflowDescription
	 *            the description of the workflow to get the information from
	 * @param item
	 *            the list item object to set the data into
	 */
	protected void setUserData(WorkflowDisplayContainer workflowDescription,
			CmsListItem item) {

		if (workflowDescription != null) {
			item.set(LIST_COLUMN_DELETEICON, "");
			item.set(LIST_COLUMN_WORKFLOWICON, "");
			item.set(LIST_COLUMN_INSTANCEID, workflowDescription
					.get_instanceID());
			item.set(LIST_COLUMN_NAME, workflowDescription.get_workflowName());
			/*
			 * item.set(LIST_COLUMN_STATE,
			 * this.getStatusLabel(workflowDescription .is_isEnded()));
			 */
			item.set(LIST_COLUMN_STATE, "");
			item.set(LIST_COLUMN_STATE_VALUE, new Boolean(workflowDescription
					.is_isEnded()));
			item.set(LIST_COLUMN_LASTACTION, workflowDescription
					.get_lastActionName());
			item.set(LIST_COLUMN_NUMBEROFFILES, workflowDescription
					.get_numberOfFiles()
					+ "");
			item.set(LIST_COLUMN_NEXTACTIONS, workflowDescription
					.formatListOfAvailableActions(getCms().getRequestContext()
							.getLocale()));
			item.set(LIST_COLUMN_INSTANCENAME, workflowDescription
					.get_instanceName());
			item.set(LIST_COLUMN_ACCESSTOWRITE_VALUE,new Boolean(!workflowDescription.is_isWriteAccessDeny()));

		} else {
			LOGGER
					.info("An null instance of WorkflowDisplayContainer has been inserted !");
			item.set(LIST_COLUMN_DELETEICON, "");
			item.set(LIST_COLUMN_WORKFLOWICON, "");
			item.set(LIST_COLUMN_INSTANCEID, "Null Instance inserted");
			item.set(LIST_COLUMN_NAME, "");
			item.set(LIST_COLUMN_STATE, "");
			item.set(LIST_COLUMN_STATE_VALUE, Boolean.TRUE);
			item.set(LIST_COLUMN_LASTACTION, "");
			item.set(LIST_COLUMN_NUMBEROFFILES, "");
			item.set(LIST_COLUMN_NEXTACTIONS, "");
			item.set(LIST_COLUMN_INSTANCENAME, "");
			item.set(LIST_COLUMN_ACCESSTOWRITE_VALUE,Boolean.FALSE);
		}
	}

	/**
	 * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
	 */
	protected void setColumns(CmsListMetadata metadata) {

		/* **************** */
		/* hidden columns */
		/* **************** */

		// column that store the state of the instance of wf
		CmsListColumnDefinition stateValueColumn = new CmsListColumnDefinition(
				LIST_COLUMN_STATE_VALUE);
		stateValueColumn.setVisible(false);
		metadata.addColumn(stateValueColumn);
		
		// column that store the right  of the user to modify the wf
		CmsListColumnDefinition rightValueColumn = new CmsListColumnDefinition(
				LIST_COLUMN_ACCESSTOWRITE_VALUE);
		rightValueColumn.setVisible(false);
		metadata.addColumn(rightValueColumn);

		/* ************************ */
		/* column 1 (workflow icon) */
		/* ************************ */

		// create column to display the workflow icon
		CmsListColumnDefinition workflowIconCol = new CmsListColumnDefinition(
				LIST_COLUMN_WORKFLOWICON);
		workflowIconCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_0));
		workflowIconCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_WORKFLOWICON_HELP_0));
		workflowIconCol.setWidth(LIST_COLUMNSIZE_WORKFLOW_ICON);
		workflowIconCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		workflowIconCol.setSorteable(false);

		// add workflow action
		setWorkflowAction(workflowIconCol);
		m_editActionIds.addAll(workflowIconCol.getDirectActionIds());

		// add it to the list definition
		metadata.addColumn(workflowIconCol);

		/* ********************** */
		/* column 2 (delete icon) */
		/* ********************** */		

		// create column to display the state icon
		CmsListColumnDefinition deleteIconCol = new CmsListColumnDefinition(
				LIST_COLUMN_DELETEICON);
		deleteIconCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_DELETEICON_0));
		deleteIconCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_DELETEICON_HELP_0));
		deleteIconCol.setWidth(LIST_COLUMNSIZE_DELETEICON);
		deleteIconCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		deleteIconCol.setSorteable(false);
		deleteIconCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		/*
		 * This columns is hidden due to impossibility of easily delete workflow entry
		 */
		deleteIconCol.setVisible(false);

		// add action
		setDeleteAction(deleteIconCol);
		
		// add it to the list definition
		metadata.addColumn(deleteIconCol);

		/* ********************** */
		/* column 3 (instance ID) */
		/* ********************** */

		// create column to display the instance
		CmsListColumnDefinition idCol = new CmsListColumnDefinition(
				LIST_COLUMN_INSTANCEID);
		idCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_INSTANCEID_0));
		idCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_INSTANCEID_HELP_0));
		idCol.setWidth(LIST_COLUMNSIZE_INSTANCESID);
		idCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		idCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(idCol);

		/* ************************** */
		/* column 4 (instance name) */
		/* ************************** */

		// create column to display the instance
		CmsListColumnDefinition instanceNameCol = new CmsListColumnDefinition(
				LIST_COLUMN_INSTANCENAME);
		instanceNameCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_INSTANCENAME_0));
		instanceNameCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_INSTANCENAME_HELP_0));
		instanceNameCol.setWidth(LIST_COLUMNSIZE_INSTANCENAME);
		instanceNameCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		instanceNameCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(instanceNameCol);

		/* ***************** */
		/* column 5 (Name) */
		/* ***************** */

		// create column
		CmsListColumnDefinition nameCol = new CmsListColumnDefinition(
				LIST_COLUMN_NAME);
		nameCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_0));
		nameCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_WORKFLOWNAME_HELP_0));
		nameCol.setWidth(LIST_COLUMNSIZE_NAME);
		nameCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		nameCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(nameCol);

		/* ************************ */
		/* column 6 (Last Action) */
		/* ************************ */

		// create column
		CmsListColumnDefinition lastActionCol = new CmsListColumnDefinition(
				LIST_COLUMN_LASTACTION);
		lastActionCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_LASTACTION_0));
		lastActionCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_LASTACTION_HELP_0));
		lastActionCol.setWidth(LIST_COLUMNSIZE_LASTACTION);
		lastActionCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		lastActionCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(lastActionCol);

		/* ****************************** */
		/* column 7 (Available actions) */
		/* ****************************** */

		// create column
		CmsListColumnDefinition nextActionsCol = new CmsListColumnDefinition(
				LIST_COLUMN_NEXTACTIONS);
		nextActionsCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_0));
		nextActionsCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_NEXTACTIONS_HELP_0));
		nextActionsCol.setWidth(LIST_COLUMNSIZE_NEXTACTIONS);
		nextActionsCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		nextActionsCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(nextActionsCol);

		/* **************************** */
		/* column 8 (Number of files) */
		/* **************************** */

		// create column
		CmsListColumnDefinition numberOfFilesCol = new CmsListColumnDefinition(
				LIST_COLUMN_NUMBEROFFILES);
		numberOfFilesCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_0));
		numberOfFilesCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_NUMBEROFFILES_HELP_0));
		numberOfFilesCol.setWidth(LIST_COLUMNSIZE_NUMBEROFFILES);
		numberOfFilesCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		numberOfFilesCol.setSorteable(true);

		// add it to the list definition
		metadata.addColumn(numberOfFilesCol);

		/* ****************** */
		/* column 9 (State) */
		/* ****************** */

		// create column to display the name of the workflow
		CmsListColumnDefinition stateCol = new CmsListColumnDefinition(
				LIST_COLUMN_STATE);
		stateCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_STATE_0));
		stateCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_COLS_STATE_HELP_0));
		stateCol.setWidth(LIST_COLUMNSIZE_STATE);
		stateCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		stateCol.setSorteable(true);
		// add direct action
		setStateAction(stateCol);

		// add it to the list definition
		metadata.addColumn(stateCol);
	}

	/**
	 * Sets the needed action for column delete.
	 * <p>
	 * 
	 * @param deleteIconCol
	 *            the list column for delete a workflow.
	 */
	private void setDeleteAction(CmsListColumnDefinition deleteIconCol) {
		CmsListDirectAction deleteAction = new CmsListDirectAction(
				LIST_ACTION_DELETE) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.list.CmsListDirectAction#buttonHtml(org.opencms.workplace.CmsWorkplace)
			 */
			@Override
			public String buttonHtml(CmsWorkplace wp) {
				if (!isVisible()) {
					return "";
				}

				return A_CmsHtmlIconButton.defaultButtonHtml(
						resolveButtonStyle(), getId() + getItem().getId(),
						getId() + getItem().getId(),
						resolveName(wp.getLocale()), resolveHelpText(wp
								.getLocale()), isEnabled(), getIconPath(),
						null, resolveOnClic(wp.getLocale()),
						getColumnForTexts() == null, null);

			}
			

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.list.CmsListDirectAction#getColumnForTexts()
			 */
			@Override
			public String getColumnForTexts() {
				return LIST_COLUMN_STATE;
			}

			
			  /**
             * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#isVisible()
             */
            public boolean isVisible() {

                if (getItem() == null) {
                    return super.isVisible();
                }
                Boolean isEnded = (Boolean)getItem().get(LIST_COLUMN_STATE_VALUE);
                Boolean isAllowed = (Boolean)getItem().get(LIST_COLUMN_ACCESSTOWRITE_VALUE);
                if (isEnded == null || isAllowed == null) {
                    return super.isVisible();
                }
                //set visible only for ended workflows and if the user has write access right
                return isEnded.booleanValue() && isAllowed.booleanValue();
            }

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getHelpText()
			 */
			@Override
			public CmsMessageContainer getHelpText() {
				if (getItem() == null) {
					return super.getHelpText();
				}
				Boolean isEnded = (Boolean) getItem().get(
						LIST_COLUMN_STATE_VALUE);
				if (isEnded == null) {
					return super.getHelpText();
				}
				return Messages.get().container(
						Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_CANBEDELETE_0);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getName()
			 */
			@Override
			public CmsMessageContainer getName() {
				if (getItem() == null) {
					return super.getName();
				}
				Boolean isEnded = (Boolean) getItem().get(
						LIST_COLUMN_STATE_VALUE);
				if (isEnded == null) {
					return super.getName();
				}
				return Messages.get().container(
						Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_CANBEDELETE_0);
			}

		};

		deleteAction.setName(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_DELETE_0));
		deleteAction.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_DELETE_HELP_0));
		deleteAction.setIconPath(LIST_ICONPATH_DELETEICON);
		deleteIconCol.addDirectAction(deleteAction);
		
	}

	/**
	 * Sets the needed edit action(s).
	 * <p>
	 * 
	 * @param workflowIconCol
	 *            the list column for edition of workflow.
	 */
	private void setWorkflowAction(CmsListColumnDefinition workflowIconCol) {
		CmsListDirectAction worflowAction = new CmsListDirectAction(
				LIST_ACTION_WORKFLOW);
		worflowAction.setName(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_WORKFLOW_0));
		worflowAction.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_WORKFLOW_HELP_0));
		worflowAction.setIconPath(LIST_ICONPATH_WORKFLOW);
		workflowIconCol.addDirectAction(worflowAction);
	}

	/**
	 * Sets the needed action for column state.
	 * <p>
	 * 
	 * @param stateCol
	 *            the list column for state of workflow.
	 */
	private void setStateAction(CmsListColumnDefinition stateCol) {
		CmsListDirectAction stateAction = new CmsListDirectAction(
				LIST_ACTION_STATE) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.list.CmsListDirectAction#buttonHtml(org.opencms.workplace.CmsWorkplace)
			 */
			@Override
			public String buttonHtml(CmsWorkplace wp) {
				if (!isVisible()) {
					return "";
				}

				return A_CmsHtmlIconButton.defaultButtonHtml(
						resolveButtonStyle(), getId() + getItem().getId(),
						getId() + getItem().getId(),
						resolveName(wp.getLocale()), resolveHelpText(wp
								.getLocale()), isEnabled(), getIconPath(),
						null, resolveOnClic(wp.getLocale()),
						getColumnForTexts() == null, null);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.list.CmsListDirectAction#resolveOnClic(java.util.Locale)
			 */
			@Override
			protected String resolveOnClic(Locale locale) {
				// Desactive any action on click
				return "";
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.list.CmsListDirectAction#getColumnForTexts()
			 */
			@Override
			public String getColumnForTexts() {
				return LIST_COLUMN_STATE;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getIconPath()
			 */
			@Override
			public String getIconPath() {
				if (getItem() == null) {
					return super.getIconPath();
				}
				Boolean isEnded = (Boolean) getItem().get(
						LIST_COLUMN_STATE_VALUE);
				if (isEnded == null) {
					return super.getIconPath();
				}
				if (isEnded.booleanValue()) {
					return LIST_ICONPATH_STATEICON_ENDED;
				} else {
					return LIST_ICONPATH_STATEICON_ONGOING;
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getHelpText()
			 */
			@Override
			public CmsMessageContainer getHelpText() {
				if (getItem() == null) {
					return super.getHelpText();
				}
				Boolean isEnded = (Boolean) getItem().get(
						LIST_COLUMN_STATE_VALUE);
				if (isEnded == null) {
					return super.getHelpText();
				}
				return getStatusLabel(isEnded.booleanValue());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.opencms.workplace.tools.A_CmsHtmlIconButton#getName()
			 */
			@Override
			public CmsMessageContainer getName() {
				if (getItem() == null) {
					return super.getName();
				}
				Boolean isEnded = (Boolean) getItem().get(
						LIST_COLUMN_STATE_VALUE);
				if (isEnded == null) {
					return super.getName();
				}
				return getStatusLabel(isEnded.booleanValue());
			}

		};

		stateAction.setName(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_STATE_0));
		stateAction.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWLIST_ACTION_STATE_HELP_0));
		stateAction.setIconPath(LIST_ICONPATH_STATEICON_ENDED);
		stateCol.addDirectAction(stateAction);
	}

	/**
	 * @see org.opencms.workplace.list.A_CmsListDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
	 */
	protected void setIndependentActions(CmsListMetadata metadata) {
		
		
		

		/*
		 * add creator details
		 */
		CmsListItemDetails ownerDetails = new CmsListItemDetails(
				LIST_DETAIL_CREATOR);
		ownerDetails.setAtColumn(LIST_COLUMN_NAME);
		ownerDetails.setVisible(false);
		ownerDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_0));
		ownerDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_OWNER_HELP_0));
		ownerDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_0));
		ownerDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_OWNER_HELP_0));
		ownerDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_OWNER_LABEL_0));
		ownerDetails.setFormatter(new CmsListItemDetailsFormatter(Messages
				.get().container(
						Messages.GUI_WORKFLOWSLIST_DETAIL_OWNER_LABEL_0)));
		metadata.addItemDetails(ownerDetails);

		/*
		 * add owner of last actions details
		 */
		CmsListItemDetails lastActionOwnerDetails = new CmsListItemDetails(
				LIST_DETAIL_LASTACTIONOWNER);
		lastActionOwnerDetails.setAtColumn(LIST_COLUMN_LASTACTION);
		lastActionOwnerDetails.setVisible(false);
		lastActionOwnerDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_0));
		lastActionOwnerDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONOWNER_HELP_0));
		lastActionOwnerDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_0));
		lastActionOwnerDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONOWNER_HELP_0));
		lastActionOwnerDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_LASTACTIONOWNER_LABEL_0));
		lastActionOwnerDetails
				.setFormatter(new CmsListItemDetailsFormatter(
						Messages
								.get()
								.container(
										Messages.GUI_WORKFLOWSLIST_DETAIL_LASTACTIONOWNER_LABEL_0)));
		metadata.addItemDetails(lastActionOwnerDetails);

		/*
		 * add comments of last actions details
		 */
		CmsListItemDetails lastActionCommentDetails = new CmsListItemDetails(
				LIST_DETAIL_LASTACTIONCOMMENT);
		lastActionCommentDetails.setAtColumn(LIST_COLUMN_LASTACTION);
		lastActionCommentDetails.setVisible(false);
		lastActionCommentDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_0));
		lastActionCommentDetails
				.setShowActionHelpText(Messages
						.get()
						.container(
								Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LASTACTIONCOMMENT_HELP_0));
		lastActionCommentDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_0));
		lastActionCommentDetails
				.setHideActionHelpText(Messages
						.get()
						.container(
								Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LASTACTIONCOMMENT_HELP_0));
		lastActionCommentDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_LASTACTIONCOMMENT_LABEL_0));
		lastActionCommentDetails
				.setFormatter(new CmsListItemDetailsFormatter(
						Messages
								.get()
								.container(
										Messages.GUI_WORKFLOWSLIST_DETAIL_LASTACTIONCOMMENT_LABEL_0)));
		metadata.addItemDetails(lastActionCommentDetails);

		/*
		 * add list of files details
		 */
		CmsListItemDetails listOfFilesDetails = new CmsListItemDetails(
				LIST_DETAIL_LISTOFFILES);
		listOfFilesDetails.setAtColumn(LIST_COLUMN_NUMBEROFFILES);
		listOfFilesDetails.setVisible(false);
		listOfFilesDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_0));
		listOfFilesDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_SHOW_LISTOFFILES_HELP_0));
		listOfFilesDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_0));
		listOfFilesDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_HIDE_LISTOFFILES_HELP_0));
		listOfFilesDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_DETAIL_LISTOFFILES_LABEL_0));
		listOfFilesDetails
				.setFormatter(new CmsListItemDetailsFormatter(
						Messages
								.get()
								.container(
										Messages.GUI_WORKFLOWSLIST_DETAIL_LISTOFFILES_LABEL_0)));
		metadata.addItemDetails(listOfFilesDetails);
		
		/*
		 * Add a combo-box with some filter 
		 */
        CmsListDropdownAction filterAction = new CmsListDropdownAction(LIST_ACTION_COMBOBOX);
        filterAction.setName(Messages.get().container(Messages.GUI_WORKFLOWSLIST_ACTION_COMBOBOX_LABEL_0));
        filterAction.setHelpText(Messages.get().container(Messages.GUI_WORKFLOWSLIST_ACTION_COMBOBOX_HELP_0));
       //add available filters       
        for(int i=0; i<availableFilterForCombo.length && i<availableFilterMessageForCombo.length;i++){
            filterAction.addItem(availableFilterForCombo[i],Messages.get().container(availableFilterMessageForCombo[i]));
        }
        filterAction.setSelection(FILTER_ALL);
        metadata.addIndependentAction(filterAction);

		// makes the list searchable
		CmsListSearchAction searchAction = new CmsListSearchAction(metadata
				.getColumnDefinition(LIST_COLUMN_INSTANCEID));
		searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_NAME));
		searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_INSTANCENAME));
		searchAction.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_SEARCH_HELPTEXT_0));
		searchAction.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_SEARCH_LABEL_0));
		metadata.setSearchAction(searchAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.tools.CmsToolDialog#dialogTitle()
	 */
	@Override
	public String dialogTitle() {
		/*
		 * This method allow to change windows part with "Administration View"
		 */
		StringBuffer html = new StringBuffer(512);

		CmsMessageContainer messageContainer = Messages.get().container(
				Messages.GUI_WORKFLOWSLIST_FRAME_TITLE_0);
		Locale locale = this.getLocale();

		// build title
		html.append("<div class='screenTitle'>\n");
		html.append("\t<table width='100%' cellspacing='0'>\n");
		html.append("\t\t<tr>\n");
		html.append("\t\t\t<td>\n");
		html.append(new MessageFormat(messageContainer.key(locale), locale)
				.format(new Object[] { messageContainer.key(locale) }));
		html.append("\n\t\t\t</td>");
		html.append("\t\t</tr>\n");
		html.append("\t</table>\n");
		html.append("</div>\n");		
		
		return html.toString();

	}

	/**
	 * Get the text to be displayed as status
	 * 
	 * @param isEnded
	 *            indicate if the workflow is ended or not
	 * @return the container for the String to display
	 */
	private CmsMessageContainer getStatusLabel(boolean isEnded) {

		Locale locale = this.getLocale();

		if (isEnded) {
			return Messages.get().container(
					Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_ENDED_0);
		} else {
			return Messages.get().container(
					Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_UNDERWAY_0);
		}

	}

	/* (non-Javadoc)
	 * @see org.opencms.workplace.list.A_CmsListDialog#executeSort()
	 */
	@Override
	protected void executeSort() {
		if(getParamSortCol().equals(LIST_COLUMN_STATE)){
			getList().setSortedColumn(LIST_COLUMN_STATE_VALUE);
		}else{
		 getList().setSortedColumn(getParamSortCol());
		}
	}

	
	
	

}
