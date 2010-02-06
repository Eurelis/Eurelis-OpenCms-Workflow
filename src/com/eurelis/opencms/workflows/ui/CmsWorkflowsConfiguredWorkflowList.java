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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.CmsRuntimeException;
import org.opencms.workplace.list.A_CmsListDialog;
import org.opencms.workplace.list.CmsListColumnAlignEnum;
import org.opencms.workplace.list.CmsListColumnDefinition;
import org.opencms.workplace.list.CmsListItem;
import org.opencms.workplace.list.CmsListItemActionIconComparator;
import org.opencms.workplace.list.CmsListItemDetails;
import org.opencms.workplace.list.CmsListItemDetailsFormatter;
import org.opencms.workplace.list.CmsListMetadata;
import org.opencms.workplace.list.CmsListOrderEnum;
import org.opencms.workplace.list.CmsListSearchAction;

import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;
import com.eurelis.opencms.workflows.workflows.WorkflowConfigManager;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollectorFactory;
import com.eurelis.opencms.workflows.workflows.availableworkflows.I_AvailableWorkflowCollector;
import com.eurelis.opencms.workflows.workflows.security.WorkflowRights;
import com.eurelis.opencms.workflows.workflows.security.WorkflowSecurityManager;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This class implements the windows that display the list of ongoing and ended
 * workflows.
 * 
 * @author Sébastien Bianco
 */
public class CmsWorkflowsConfiguredWorkflowList extends A_CmsListDialog {

	/** The URI of the JSP that load this Dialog object */
	public static final String DIALOG_URI = UISharedVariables.WORKFLOWVIEW_ROOT_LOCATION
			+ "listOfDefinedWorkflows.jsp";

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(CmsWorkflowsConfiguredWorkflowList.class);

	/** list id constant. */
	public static final String LIST_ID = "listConfiguredWorkflow";

	/** list column id constant. (the ID of the instance of the workflow) */
	public static final String LIST_COLUMN_WORKFLOWID = "workflowID";

	/** list column id constant. (the parameters of the WF) */
	public static final String LIST_COLUMN_PARAMETERS = "workflowParameters";

	/** list column id constant. (the rights associated to the WF) */
	public static final String LIST_COLUMN_RIGHTS = "workflowRights";

	/** list column id constant. (the size of the column ID) */
	public static final String LIST_COLUMN_WORKFLOWID_SIZE = "35%";

	/** list column id constant. (the size of the columns parameters) */
	public static final String LIST_COLUMN_PARAMETERS_SIZE = "25%";
	
	/** list column id constant. (the size of the columns parameters) */
	public static final String LIST_COLUMN_DESCRIPTION_SIZE = "25%";

	/** list column id constant. (the size of the column rights) */
	public static final String LIST_COLUMN_RIGHTS_SIZE = "40%";
	
	/** list item detail id constant. (display the list of rights) */
	public static final String LIST_DETAIL_RIGHTS = "workflowRightsDetails";
	
	/** list item detail id constant. (display the list of parameters) */
	public static final String LIST_DETAIL_PARAMETERS = "workflowParamatersDetails";
	
	/** list item detail id constant. (display the description file) */
	public static final String LIST_DETAIL_DESCRIPTIONFILE = "workflowDescriptionFileDetails";
	
	/** list item detail id constant. (display the description of the wf) */
	public static final String LIST_DETAIL_DESCRIPTION = "workflowDescriptionDetails";

	/** list column id constant. (the description associated to the WF) */
	private static final String LIST_COLUMN_DESCRIPTION = "workflowDescription";

	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * 
	 * @uml.property name="_uiManager"
	 * @uml.associationEnd
	 */
	private CmsWorkflowUIManager _uiManager = CmsWorkflowUIManager
			.getInstance(getCms().getRequestContext().currentUser().getName());

	/**
	 * The OSWorkflowManager instance
	 * 
	 * @uml.property name="_osWorkflowManager"
	 * @uml.associationEnd
	 */
	private A_OSWorkflowManager _osWorkflowManager;

	/**
	 * The collector object to access all the configured workflows.
	 */
	private I_AvailableWorkflowCollector _wfCollector = AvailableWorkflowCollectorFactory
			.getInstance();

	/**
	 * The configured workflows manager
	 */
	private WorkflowConfigManager _wfConfigManager = WorkflowConfigManager
			.getInstance();

	/**
	 * The Security Manager
	 */
	private WorkflowSecurityManager _wfSecurityManager = WorkflowSecurityManager
			.getInstance();

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
	public CmsWorkflowsConfiguredWorkflowList(CmsJspActionElement jsp,
			String listId, CmsMessageContainer listName) {
		super(jsp, listId, listName, LIST_COLUMN_WORKFLOWID,
				CmsListOrderEnum.ORDER_DESCENDING, null);

		this._osWorkflowManager = A_OSWorkflowManagerFactory.getInstance();

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
	public CmsWorkflowsConfiguredWorkflowList(CmsJspActionElement jsp) {
		this(jsp, LIST_ID, Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_WINDOWNAME_0));
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
	public CmsWorkflowsConfiguredWorkflowList(PageContext context,
			HttpServletRequest req, HttpServletResponse res) {
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
		// No actions

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#executeListSingleActions()
	 */
	@Override
	public void executeListSingleActions() throws IOException,
			ServletException, CmsRuntimeException {
		// No actions
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#setMultiActions(org.opencms.workplace.list.CmsListMetadata)
	 */
	@Override
	protected void setMultiActions(CmsListMetadata arg0) {
		// No Actions
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.list.A_CmsListDialog#fillDetails(java.lang.String)
	 */
	@Override
	protected void fillDetails(String detailId) {
		
		// get the list of loaded WF
		Map<WorkflowKey, DescriptionContainer> listOfLoadedWF = _osWorkflowManager
				.getMapOfAllAccessibleWorkflows();

		// get content
		List<CmsListItem> workflowsItem = getList().getAllContent();
		Iterator<CmsListItem> workflowsItemsIterator = workflowsItem.iterator();
		while (workflowsItemsIterator.hasNext()) {
			CmsListItem item = workflowsItemsIterator.next();
			

			// fill rights
			if (detailId.equals(LIST_DETAIL_RIGHTS)) {
				WorkflowRights rights = _wfSecurityManager.getRights(new WorkflowKey(item.getId(),false));
				StringBuffer htmlCreator = new StringBuffer(512);
				htmlCreator.append(rights.toHTML());
				item.set(detailId, htmlCreator.toString());
			}
			
			//fill description file
			/*if (detailId.equals(LIST_DETAIL_DESCRIPTION)) {
				DescriptionContainer desc = listOfLoadedWF.get(new WorkflowKey(item.getId(),false));
				StringBuffer htmlCreator = new StringBuffer(512);
				htmlCreator.append(desc.get_description());
				item.set(detailId, htmlCreator.toString());
			}*/
			
			//fill description file
			if (detailId.equals(LIST_DETAIL_DESCRIPTIONFILE)) {
				DescriptionContainer desc = listOfLoadedWF.get(new WorkflowKey(item.getId(),false));
				StringBuffer htmlCreator = new StringBuffer(512);
				htmlCreator.append(desc.get_workflowFilePath());
				item.set(detailId, htmlCreator.toString());
			}

			//fill description file
			if (detailId.equals(LIST_DETAIL_PARAMETERS)) {					
				StringBuffer htmlCreator = new StringBuffer(512);
				htmlCreator.append(_wfConfigManager.getHTMLOfListOfParameters(new WorkflowKey(item.getId(),false)));
				item.set(detailId, htmlCreator.toString());
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
		// Create the list of result
		List<CmsListItem> ret = new ArrayList<CmsListItem>();

		// get the list of loaded WF
		Map<WorkflowKey, DescriptionContainer> listOfLoadedWF = _osWorkflowManager
				.getMapOfAllAccessibleWorkflows();

		// set Content to display
		Iterator<Entry<WorkflowKey, DescriptionContainer>> listOfLoadedWFIterator = listOfLoadedWF
				.entrySet().iterator();
		while (listOfLoadedWFIterator.hasNext()) {
			Entry<WorkflowKey, DescriptionContainer> entry = listOfLoadedWFIterator
					.next();

			// create the CmsListItem with the list of informations to display
			CmsListItem item = getList().newItem(entry.getKey().toString());
			item.set(LIST_COLUMN_WORKFLOWID, entry.getValue().get_title());
			//item.set(LIST_COLUMN_DESCRIPTIONFILES, entry.getValue().get_workflowFilePath());			
			//item.set(LIST_COLUMN_RIGHTSFILES, entry.getValue().get_rightsFilePath());
			
			//get the rights
			WorkflowRights rights = _wfSecurityManager.getRights(entry.getKey());
			item.set(LIST_COLUMN_RIGHTS, entry.getValue().get_rightsFilePath());
			
			
			//get the list of parameters
			//List<Parameter> parameters = _wfConfigManager.getListOfParameters(entry.getKey());
			//item.set(LIST_COLUMN_PARAMETERS, parameters.size()+"");
			
			//get the description
			item.set(LIST_COLUMN_DESCRIPTION, entry.getValue().get_description());
			

			ret.add(item);
		}

		return ret;
	}

	/**
	 * @see org.opencms.workplace.list.A_CmsListDialog#setColumns(org.opencms.workplace.list.CmsListMetadata)
	 */
	protected void setColumns(CmsListMetadata metadata) {

		/* **************** */
		/* hidden columns */
		/* **************** */
		


		/* ************************* */
		/* column 1 (workflow title) */
		/* ************************* */

		CmsListColumnDefinition workflowTitleCol = new CmsListColumnDefinition(
				LIST_COLUMN_WORKFLOWID);
		workflowTitleCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_0));
		workflowTitleCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_WORKFLOWTITLE_HELP_0));
		workflowTitleCol.setWidth(LIST_COLUMN_WORKFLOWID_SIZE);
		workflowTitleCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		workflowTitleCol.setListItemComparator(new CmsListItemActionIconComparator());
		workflowTitleCol.setSorteable(true);

		
		// add it to the list definition
		metadata.addColumn(workflowTitleCol);

		/* *************************** */
		/* column 2 (description path) */
		/* *************************** */

		/*CmsListColumnDefinition descriptionPathCol = new CmsListColumnDefinition(
				LIST_COLUMN_DESCRIPTIONFILES);
		descriptionPathCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_0));
		descriptionPathCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTIONFILE_HELP_0));
		descriptionPathCol.setWidth(LIST_COLUMN_DESCRIPTIONFILES_SIZE);
		descriptionPathCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		descriptionPathCol.setSorteable(true);
		descriptionPathCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		// add it to the list definition
		metadata.addColumn(descriptionPathCol,1);*/
		
		/* ********************** */
		/* column 3 (rights path) */
		/* ********************** */

		/*CmsListColumnDefinition rightsPathCol = new CmsListColumnDefinition(
				LIST_COLUMN_RIGHTSFILES);
		rightsPathCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_0));
		rightsPathCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTSFILE_HELP_0));
		rightsPathCol.setWidth(LIST_COLUMN_RIGHTSFILES_SIZE);
		rightsPathCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		rightsPathCol.setSorteable(true);
		rightsPathCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		// add it to the list definition
		metadata.addColumn(rightsPathCol,2);/*
		
		/* ********************** */
		/* column 2 (parameters) */
		/* ********************** */

		/*CmsListColumnDefinition parametersCol = new CmsListColumnDefinition(
				LIST_COLUMN_PARAMETERS);
		parametersCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_0));
		parametersCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_PARAMETERS_HELP_0));
		parametersCol.setWidth(LIST_COLUMN_PARAMETERS_SIZE);
		parametersCol.setAlign(CmsListColumnAlignEnum.ALIGN_CENTER);
		parametersCol.setSorteable(true);
		parametersCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		// add it to the list definition
		metadata.addColumn(parametersCol);*/
		
		/* ********************** */
		/* column 2 (description) */
		/* ********************** */

		CmsListColumnDefinition descriptionCol = new CmsListColumnDefinition(
				LIST_COLUMN_DESCRIPTION);
		descriptionCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_0));
		descriptionCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_DESCRIPTION_HELP_0));
		descriptionCol.setWidth(LIST_COLUMN_DESCRIPTION_SIZE);
		descriptionCol.setAlign(CmsListColumnAlignEnum.ALIGN_LEFT);
		descriptionCol.setSorteable(true);
		descriptionCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		// add it to the list definition
		metadata.addColumn(descriptionCol);
		
		/* ***************** */
		/* column 3 (rights) */
		/* ***************** */

		CmsListColumnDefinition rightsCol = new CmsListColumnDefinition(
				LIST_COLUMN_RIGHTS);
		rightsCol.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_0));
		rightsCol.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_COLS_RIGHTS_HELP_0));
		rightsCol.setWidth(LIST_COLUMN_RIGHTS_SIZE);
		rightsCol.setAlign(CmsListColumnAlignEnum.ALIGN_LEFT);
		rightsCol.setSorteable(true);
		rightsCol
				.setListItemComparator(new CmsListItemActionIconComparator());
		
		// add it to the list definition
		metadata.addColumn(rightsCol);
		
	}

	/**
	 * @see org.opencms.workplace.list.A_CmsListDialog#setIndependentActions(org.opencms.workplace.list.CmsListMetadata)
	 */
	protected void setIndependentActions(CmsListMetadata metadata) {
		
		/*
		 * add rights details
		 */
		CmsListItemDetails rightsDetails = new CmsListItemDetails(
				LIST_DETAIL_RIGHTS);
		rightsDetails.setAtColumn(LIST_COLUMN_RIGHTS);
		rightsDetails.setVisible(false);
		rightsDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_0));
		rightsDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_RIGHTS_HELP_0));
		rightsDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_0));
		rightsDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_RIGHTS_HELP_0));
		rightsDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_RIGHTS_LABEL_0));
		rightsDetails.setFormatter(new CmsListItemDetailsFormatter(Messages
				.get().container(
						Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_RIGHTS_LABEL_0)));
		metadata.addItemDetails(rightsDetails);
		
		/*
		 * add description details
		 */
		/*CmsListItemDetails descriptionDetails = new CmsListItemDetails(
				LIST_DETAIL_DESCRIPTION);
		descriptionDetails.setAtColumn(LIST_COLUMN_WORKFLOWID);
		descriptionDetails.setVisible(false);
		descriptionDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_0));
		descriptionDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTION_HELP_0));
		descriptionDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_0));
		descriptionDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTION_HELP_0));
		descriptionDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTION_LABEL_0));
		descriptionDetails.setFormatter(new CmsListItemDetailsFormatter(Messages
				.get().container(
						Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTION_LABEL_0)));
		metadata.addItemDetails(descriptionDetails);*/
		
		/*
		 * add description file details
		 */
		CmsListItemDetails descriptionFileDetails = new CmsListItemDetails(
				LIST_DETAIL_DESCRIPTIONFILE);
		descriptionFileDetails.setAtColumn(LIST_COLUMN_WORKFLOWID);
		descriptionFileDetails.setVisible(false);
		descriptionFileDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_0));
		descriptionFileDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_DESCRIPTIONFILE_HELP_0));
		descriptionFileDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_0));
		descriptionFileDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_DESCRIPTIONFILE_HELP_0));
		descriptionFileDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTIONFILE_LABEL_0));
		descriptionFileDetails.setFormatter(new CmsListItemDetailsFormatter(Messages
				.get().container(
						Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_DESCRIPTIONFILE_LABEL_0)));
		metadata.addItemDetails(descriptionFileDetails);
		
		/*
		 * add parameter details
		 */
		CmsListItemDetails parametersDetails = new CmsListItemDetails(
				LIST_DETAIL_PARAMETERS);
		parametersDetails.setAtColumn(LIST_COLUMN_DESCRIPTION);
		parametersDetails.setVisible(false);
		parametersDetails.setShowActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_0));
		parametersDetails.setShowActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_SHOW_PARAMETERS_HELP_0));
		parametersDetails.setHideActionName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_0));
		parametersDetails.setHideActionHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_HIDE_PARAMETERS_HELP_0));
		parametersDetails.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_PARAMETERS_LABEL_0));
		parametersDetails.setFormatter(new CmsListItemDetailsFormatter(Messages
				.get().container(
						Messages.GUI_WORKFLOWSCONFIGUREDLIST_DETAIL_PARAMETERS_LABEL_0)));
		metadata.addItemDetails(parametersDetails);

		// makes the list searchable
		CmsListSearchAction searchAction = new CmsListSearchAction(metadata
				.getColumnDefinition(LIST_COLUMN_WORKFLOWID));
		searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_RIGHTS));
		searchAction.addColumn(metadata.getColumnDefinition(LIST_COLUMN_DESCRIPTION));
		searchAction.setHelpText(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_HELPTEXT_0));
		searchAction.setName(Messages.get().container(
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_SEARCH_LABEL_0));
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
				Messages.GUI_WORKFLOWSCONFIGUREDLIST_FRAME_TITLE_0);
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

}
