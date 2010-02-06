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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.opencms.flex.CmsFlexController;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.staticexport.CmsLinkManager;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.CmsWorkplaceSettings;

import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.eurelis.opencms.workflows.workflows.toolobjects.HistoricElement;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowDisplayContainer;

/**
 * This windows allows to display the next steps of a workflow and to make it
 * evolve
 * 
 * @author Sébastien Bianco
 */
public class CmsWorkflowDisplay extends CmsDialog {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(CmsWorkflowDisplay.class);

	/** The URI of the JSP that load this Dialog object */
	public static final String DIALOG_URI = UISharedVariables.WORKFLOW_MAIN_FOLDER
			+ "displayWorkflow.jsp";

	/** The tag-name of the variable "actionname" */
	private static final String FORMPROPERTY_ACTIONAME = "actionname";

	/** The tag-name of the variable "comment" */
	private static final String FORMPROPERTY_COMMENT = "comment";

	/**
	 * The URL of the jsp that call that class
	 */
	private String _dialogURI;

	/**
	 * The current instance of workflow that the dialog display
	 * 
	 * @uml.property name="_workflowInstanceID"
	 */
	private long _workflowInstanceID = DEFAULT_WORKFLOWID;

	/**
	 * The default value of the _workflowInstanceID
	 */
	private static final long DEFAULT_WORKFLOWID = -1;

	/**
	 * The instance of the OSWorkflowManager so as to create instances of
	 * workflows
	 * 
	 * @uml.property name="_osWorkflowManager"
	 * @uml.associationEnd
	 */
	private A_OSWorkflowManager _osWorkflowManager = null;

	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * 
	 * @uml.property name="_uiManager"
	 * @uml.associationEnd
	 */
	private CmsWorkflowUIManager _uiManager = CmsWorkflowUIManager
			.getInstance(getCms().getRequestContext().currentUser().getName());

	/**
	 * The list of the columns title (historic)
	 */
	private static final String[] HISTORIC_COLUMNS_TITLES = new String[] {
			Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONNAME_0,
			// Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STEPNAME_0,
			Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_OWNER_0,
			Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_COMMENT_0,
			//Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STARTDATE_0,
			//Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ENDDATE_0,
			//Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_STATUS_0, 
			Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_ACTIONDATE_0,
			Messages.GUI_WORKFLOWDISPLAY_HISTORIC_COLUMNNAME_AUTO_0};

	/**
	 * The list of the columns size (historic)
	 */
	private static final String[] HISTORIC_COLUMNSSIZES = new String[] { "20",
	/* "10", */"20", "30"/*, "15"*/, "20", "10" };

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
	public CmsWorkflowDisplay(PageContext context, HttpServletRequest request,
			HttpServletResponse response) {
		super(context, request, response);

		// initialize the object
		initialize();
	}

	/**
	 * Public constructor with JSP action element.
	 * <p>
	 * 
	 * @param jsp
	 *            an initialized JSP action element
	 */
	public CmsWorkflowDisplay(CmsJspActionElement jsp) {
		super(jsp);

		// initialize the object
		initialize();
	}

	/**
	 * This method does the main things to do during the initialization
	 * 
	 */
	private void initialize() {

		// update CmsWoroflowUIManager
		_uiManager.set_currentPagePath(DIALOG_URI);

		// save request parameter
		fillParamValues(getJsp().getRequest());

		// load the _osWorkflowManager
		this._osWorkflowManager = A_OSWorkflowManagerFactory.getInstance();

		/*
		 * Get the workflowInstanceID (check the parameter "workflowID" and (if
		 * previous is null) "selectItems"). If no value is found, set a default
		 * value.
		 */
		String workflowID = null;
		String selectedItems = (String) getJsp().getRequest().getParameter(
				UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID);
		if (StringChecker.isNotNullOrEmpty(selectedItems)) {
			// LOGGER.debug("WF | selectedItems = " + selectedItems);
			workflowID = selectedItems;
		} else {
			// LOGGER.debug("WF | selectedItems = null");
			workflowID = (String) getJsp().getRequest().getParameter(
					"selectItems");
		}
		if (StringChecker.isNotNullOrEmpty(workflowID)) {
			try {
				_workflowInstanceID = Long.parseLong(workflowID);
			} catch (NumberFormatException e) {
				LOGGER
						.warn("The attribut "
								+ UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID
								+ " is not a Long, so a default value (1) has been set.");
				_workflowInstanceID = 1;
			}
		} else {
			// set new value if none is already set
			if (_workflowInstanceID == DEFAULT_WORKFLOWID) {
				_workflowInstanceID = 1;
				LOGGER
						.warn("No attribute "
								+ UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID
								+ " has been found, so a default value (1) has been set.");
			}
		}
	}

	/**
	 * Returns an initialized CmsDialog instance that is read from the request
	 * attributes.
	 * <p>
	 * 
	 * This method is used by dialog elements. The dialog elements do not
	 * initialize their own workplace class, but use the initialized instance of
	 * the "master" class. This is required to ensure that parameters of the
	 * "master" class can properly be kept on the dialog elements.
	 * <p>
	 * 
	 * To prevent null pointer exceptions, an empty dialog is returned if
	 * nothing is found in the request attributes.
	 * <p>
	 * 
	 * @param context
	 *            the JSP page context
	 * @param req
	 *            the JSP request
	 * @param res
	 *            the JSP response
	 * @return an initialized CmsDialog instance that is read from the request
	 *         attributes
	 */
	public static CmsWorkflowDisplay initCmsDialog(PageContext context,
			HttpServletRequest req, HttpServletResponse res) {

		CmsWorkflowDisplay wp = (CmsWorkflowDisplay) req
				.getAttribute(CmsWorkplace.SESSION_WORKPLACE_CLASS);
		// LOGGER.debug("WF | wp = " + wp);
		if (wp == null) {
			// ensure that we don't get null pointers if the page is directly
			// called
			wp = new CmsWorkflowDisplay(new CmsJspActionElement(context, req,
					res));
			wp.fillParamValues(req);
		}
		return wp;

	}

	/**
	 * Returns the HTML to build the input form of the upload dialog.
	 * <p>
	 * 
	 * @return the HTML to build the input form of the upload dialog
	 */
	protected String defaultActionHtml() {

		// get Details of workflow
		WorkflowDisplayContainer workflowPropertyContainer = _osWorkflowManager
				.collectInformationOfInstanceID(_workflowInstanceID, true,
						getCms());

		// LOGGER.info("WF | workflowPropertyContainer = "+
		// workflowPropertyContainer);

		StringBuffer result = new StringBuffer(32);

		result.append(htmlStart());
		result.append(bodyStart(null));
		result.append(dialogContentStart(""));

		// Display title
		result
				.append("<h1>"
						+ this
								.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_TITLE_0)
						+ "</h1>");
		result.append("<hr>");
		result.append("<br/>");

		/*
		 * Display area with Workflows informations
		 */
		result.append("<div class=\"displayworkflow\">");
		result.append("<fieldset class=\"dialogblock\">");
		result
				.append("<legend><span class=\"textbold\" unselectable=\"on\">"
						+ this
								.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_SECTIONNAME_INFORMATION_0)
						+ "</span></legend>");
		result.append("<table border=\"0\"><tbody>");
		result.append("<tr><td>");
		result.append(this.createInformationPart(workflowPropertyContainer));
		result.append("</td>");

		List<Parameter> parameters = workflowPropertyContainer.get_parameters();
		if (parameters != null && parameters.size() > 0) {
			result.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>");
			result.append(this.createParameterPart(parameters));
			result.append("</td>");
		}

		result.append("</tr></tbody></table>");		
		result.append("</fieldset");
		result.append("</div>");
		result.append("<br/>");

		/*
		 * Display zone to select workflow and enter comment (if there is
		 * available actions)
		 */
		if (workflowPropertyContainer.get_followingSteps().length > 0) {
			result.append("<div class=\"executeaction\">");
			result.append("<fieldset class=\"dialogblock\">");
			result
					.append("<legend><span class=\"textbold\" unselectable=\"on\">"
							+ this
									.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_SECTIONNAME_SELECTACTION_0)
							+ "</span></legend>");
			result.append(this.createFormPart(workflowPropertyContainer));
			result.append("</fieldset");
			result.append("</div>");
			result.append("<br/>");
		}

		/*
		 * The list of selected files
		 */
		result.append("<div class=\"listfiles\">");
		result.append("<fieldset class=\"dialogblock\">");
		result
				.append("<legend><span class=\"textbold\" unselectable=\"on\">"
						+ this
								.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_SECTIONNAME_ASSOCIATEDFILES_0)
						+ "</span></legend>");
		result.append(this
				.createTableOfAssociatedFiles(workflowPropertyContainer));
		result.append("</fieldset");
		result.append("</div>");
		result.append("<br/>");

		/*
		 * The historic of actions
		 */
		result.append("<div class=\"historic\">");
		result.append("<fieldset class=\"dialogblock\">");
		result
				.append("<legend><span class=\"textbold\" unselectable=\"on\">"
						+ this
								.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_SECTIONNAME_HISTORIC_0)
						+ "</span></legend>");
		result.append(this
				.createTableOfHistoricalActions(workflowPropertyContainer));
		result.append("</fieldset");
		result.append("</div>");
		result.append("<br/>");

		// Close opened tags
		result.append(dialogContentEnd());
		result.append(bodyEnd());
		result.append(htmlEnd());

		return result.toString();
	}

	/**
	 * Create the parameter part containing the display of the initial
	 * parameters
	 * 
	 * @param parameters
	 *            the list of initial parameters
	 * 
	 * @return the HTML code of the table containing the main information about
	 *         the workflow
	 */
	private Object createParameterPart(final List<Parameter> parameters) {
		StringBuffer result = new StringBuffer(32);

		// table header
		result
				.append("<table class=\"xmlTable\" cellspacing=\"0\" cellpadding=\"0\">");
		result.append("<tbody>");

		/*
		 * Display all parameters
		 */
		for (int i = 0; i < parameters.size(); i++) {
			Parameter param = parameters.get(i);
			result.append("<tr><td>");
			result.append(param.get_displayName() + " ("
					+ Parameter.getStringValueofType(param.get_type()) + ") :");
			result.append("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>");
			result.append(param.get_value());
			result.append("</td></tr>");
		}

		// table footer
		result.append("</tbody>");
		result.append("</table>");

		return result.toString();
	}

	/**
	 * Create the information part containing some basic informations about the
	 * workflow
	 * 
	 * @param workflowPropertyContainer
	 *            the container with the known following steps
	 * 
	 * @return the HTML code of the table containing the main information about
	 *         the workflow
	 */
	private Object createInformationPart(
			WorkflowDisplayContainer workflowPropertyContainer) {

		StringBuffer result = new StringBuffer(32);

		// table header
		result
				.append("<table class=\"xmlTable\" cellspacing=\"0\" cellpadding=\"0\">");
		result.append("<tbody>");

		// display workflow instance ID
		result.append("<tr>");
		result.append("<td class=\"xmlLabel\">");
		result
				.append(this
						.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_INFORMATIONS_INSTANCEID_0));
		result.append("</td>");
		result.append("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		result.append("<td class=\"xmlTd\">");
		result.append(workflowPropertyContainer.get_instanceID());
		result.append("</td>");
		result.append("</tr>");

		// display workflow name
		result.append("<tr>");
		result.append("<td class=\"xmlLabel\">");
		result
				.append(this
						.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWNAME_0));
		result.append("</td>");
		result.append("<td>  </td>");
		result.append("<td class=\"xmlTd\">");
		result.append(workflowPropertyContainer.get_instanceName());
		result.append("</td>");
		result.append("</tr>");

		// display workflow type
		result.append("<tr>");
		result.append("<td class=\"xmlLabel\">");
		result
				.append(this
						.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_INFORMATIONS_WORKFLOWTYPE_0));
		result.append("</td>");
		result.append("<td>  </td>");
		result.append("<td class=\"xmlTd\">");
		result.append(workflowPropertyContainer.get_workflowName());
		result.append("</td>");
		result.append("</tr>");

		// display workflow creator
		result.append("<tr>");
		result.append("<td class=\"xmlLabel\">");
		result
				.append(this
						.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_INFORMATIONS_CREATOR_0));
		result.append("</td>");
		result.append("<td>  </td>");
		result.append("<td class=\"xmlTd\">");
		result.append(workflowPropertyContainer.get_creator());
		result.append("</td>");
		result.append("</tr>");

		// display workflow status
		result.append("<tr>");
		result.append("<td class=\"xmlLabel\">");
		result
				.append(this
						.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_INFORMATIONS_STATUS_0));
		result.append("</td>");
		result.append("<td>  </td>");
		result.append("<td class=\"xmlTd\">");
		result.append(this.getStatusLabel(workflowPropertyContainer
				.is_isEnded()));
		result.append("</td>");
		result.append("</tr>");

		// table footer
		result.append("</tbody>");
		result.append("</table>");

		return result.toString();
	}

	/**
	 * Create the form part, with a table with all available actions and a text
	 * area to enter a comment
	 * 
	 * @param workflowPropertyContainer
	 *            the container with the known following steps
	 * 
	 * @return the HTML code of the table containing the form body
	 */
	private String createFormPart(
			WorkflowDisplayContainer workflowPropertyContainer) {

		StringBuffer result = new StringBuffer(32);

		// forward page to current one to force treat the form
		result
				.append("<form name=\"workflowaction-form\" method=\"POST\" action=\""
						+ getJsp().link(DIALOG_URI)
						+ "\" onsubmit=\"return submitAction('");
		result.append(DIALOG_OK);
		result.append("', null, 'main');\">\n");
		result.append(allParamsAsHidden());

		if (getParamFramename() == null) {
			result.append("<input type=\"hidden\" name=\"");
			result.append(PARAM_FRAMENAME);
			result.append("\" value=\"\">");
		}

		// add wokflowpath
		result.append("<input type=\"hidden\" name=\"");
		result.append(UISharedVariables.PARAM_WORKFLOWPATH);
		result.append("\" value=\""
				+ CmsEncoder.encode(DIALOG_URI, getCms().getRequestContext()
						.getEncoding()) + "\">");

		// add ID of the workflow
		result.append("<input type=\"hidden\" name=\"");
		result.append(UISharedVariables.WAITINGAREA_PROPERTYNAME_WORFLOWID);
		result.append("\" value=\"" + _workflowInstanceID + "\">");

		/*
		 * List of available workflows
		 */
		result.append(this
				.createTableOfAvailablesActions(workflowPropertyContainer));

		/*
		 * The text area for comment
		 */
		result
				.append("<table cellspacing=\"3\" cellpadding=\"2\"  width=\"100%\" border=\"0\">\n");
		result.append("<tbody>\n");
		result
				.append("<tr><td><textarea onFocus=\"this.value=''; this.onfocus=null;\" rows=\"4\" cols=\"150\" name=\""
						+ FORMPROPERTY_COMMENT
						+ "\" wrap=\"soft\">"
						+ this
								.getExternalizedLabel(Messages.GUI_ENTERCOMMENT_0)
						+ "</textarea></td></tr>\n");

		result.append("</tbody>\n");
		result.append("</table>\n");

		result.append(dialogButtonsOkCancel("ok", "cancel"));

		result.append("</form>");
		return result.toString();
	}

	/**
	 * Performs the dialog actions depending on the initialized action and
	 * displays the dialog form.
	 * <p>
	 * 
	 * @throws JspException
	 *             if dialog actions fail
	 * @throws IOException
	 *             if writing to the JSP out fails, or in case of errors
	 *             forwarding to the required result page
	 * @throws ServletException
	 *             in case of errors forwarding to the required result page
	 */
	public void displayDialog() throws IOException, JspException,
			ServletException {
		// LOGGER.info("WF | enter displayDialog");
		switch (getAction()) {

		case ACTION_CANCEL:
			// LOGGER.debug("WF | enter ACTION_CANCEL");
			// ACTION: cancel button pressed
			actionCloseDialog();
			break;

		case ACTION_OK:
			// LOGGER.debug("WF | enter ACTION_OK");
			// ACTION: ok button pressed
			setParamAction(DIALOG_OK);
			// commit the action
			actionCommit();
			// close the windows and go back to previous frame
			actionCloseDialog();
			break;
		case ACTION_DEFAULT:
			// LOGGER.debug("WF | enter ACTION_DEFAULT");
		default:
			// LOGGER.debug("WF | enter ACTION_DEFAULT 2");
			// ACTION: show dialog (default)
			setParamAction(DIALOG_OK);
			JspWriter out = getJsp().getJspContext().getOut();
			out.print(defaultActionHtml());
		}
	}

	/**
	 * @see org.opencms.workplace.administration.A_CmsImportFromHttp#actionCommit()
	 */
	public void actionCommit() throws IOException, ServletException {

		// get actionID
		String actionID = (String) getCms().getRequestContext().getAttribute(
				FORMPROPERTY_ACTIONAME);

		if (actionID != null && !actionID.equals("")) {

			// execute action
			_osWorkflowManager.executeAction(Integer.parseInt(actionID),
					getCms().getRequestContext().currentUser().getName(),
					_workflowInstanceID, (String) getCms().getRequestContext()
							.getAttribute(FORMPROPERTY_COMMENT), getCms());

		} else {
			LOGGER.warn("No action selected, no action will be executed");
		}

	}

	/**
	 * @see org.opencms.workplace.administration.A_CmsImportFromHttp#getDialogReturnUri()
	 */
	public String getDialogReturnUri() {
		// LOGGER.debug("WF | getDialogURI = "+_dialogURI);
		return _dialogURI;
	}

	/**
	 * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings,
			HttpServletRequest request) {

		// set the dialog type
		setParamDialogtype(getClass().getName());

		// fill the parameter values in the get/set methods
		fillParamValues(request);

		// LOGGER.debug("WF | initWorkplaceRequestValues - action = "+
		// getParamAction());

		// set the action for the JSP switch
		if (DIALOG_OK.equals(getParamAction())) {
			// ok button pressed
			setAction(ACTION_OK);
		} else if (DIALOG_CANCEL.equals(getParamAction())) {
			// cancel button pressed
			setAction(ACTION_CANCEL);
		} else {
			// first dialog call, set the default action
			setAction(ACTION_DEFAULT);
		}
	}

	/**
	 * Create the string corresponding to the table containing the list of
	 * associated files
	 * 
	 * @param workflowPropertyContainer
	 *            the container with the known following steps
	 * 
	 * @return the HTML code of the table containing the list of associated
	 *         files
	 */
	private String createTableOfAssociatedFiles(
			WorkflowDisplayContainer workflowPropertyContainer) {
		StringBuffer result = new StringBuffer(32);

		// HTML table header
		result
				.append("<table class=\"list\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">");

		// display header
		result.append("<tbody>");
		result.append("<tr>");
		result
				.append("<th width=\"100%\" style=\"white-space: nowrap;\">"
						+ this
								.getExternalizedLabel(Messages.GUI_WORKFLOWDISPLAY_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0)
						+ "</th>");
		result.append("</tr>");

		// display table content
		List<String> filesArray = workflowPropertyContainer.get_listOfFiles();
		if (filesArray != null) {
			for (int rowIndex = 0; rowIndex < filesArray.size(); rowIndex++) {
				// change bg depending row or even line
				if (rowIndex % 2 == 0) {
					result.append("<tr class=\"oddrowbg\">");
				} else {
					result.append("<tr class=\"evenrowbg\">");
				}
				String filepath = filesArray.get(rowIndex);
				
				 CmsFlexController controller = CmsFlexController.getController(this.getJsp().getRequest());
			     String link  = OpenCms.getLinkManager().substituteLinkForUnknownTarget(
			            controller.getCmsObject(),
			            CmsLinkManager.getAbsoluteUri(filepath, controller.getCurrentRequest().getElementUri()));
				result.append("<td style=\"text-align: left;\"><a href=\""
						+link+"\" target=\"_blank\"\">"+filepath+"</a></td>");
				result.append("</tr>");
			}
		}

		// HTML footer of the table
		result.append("</tbody>\n");
		result.append("</table>\n");

		return result.toString();
	}

	/**
	 * Create the string corresponding to the table containing the list of
	 * available actions for the workflow
	 * 
	 * @param workflowPropertyContainer
	 *            the container with the known following steps
	 * 
	 * @return the HTML code of the table containing the list of available
	 *         actions
	 */
	private String createTableOfAvailablesActions(
			WorkflowDisplayContainer workflowPropertyContainer) {
		StringBuffer result = new StringBuffer(32);

		// HTML table header
		result
				.append("<table cellspacing=\"3\" cellpadding=\"2\" border=\"0\">\n");

		// display each actions by there name
		result.append("<tbody>\n");
		for (int i = 0; i < workflowPropertyContainer.get_followingStepsNames().length; i++) {

			result.append("<tr><td><INPUT type=radio name=\""
					+ FORMPROPERTY_ACTIONAME + "\" value=\""
					+ workflowPropertyContainer.get_followingSteps()[i] + "\"");

			// select the first item by default
			if (i == 0) {
				result.append("checked>");
			} else {
				result.append(">");
			}

			result
					.append(workflowPropertyContainer.get_followingStepsNames()[i]
							+ "</td></tr>");

		}

		// HTML footer of the table
		result.append("</tbody>\n");
		result.append("</table>\n");

		return result.toString();
	}

	/**
	 * Create the string corresponding to the table containing the list of
	 * historic actions for the workflow
	 * 
	 * @param workflowPropertyContainer
	 *            the container with the known historical steps
	 * 
	 * @return the HTML code of the table containing the list of historical
	 *         actions
	 */
	private Object createTableOfHistoricalActions(
			WorkflowDisplayContainer workflowPropertyContainer) {

			
		StringBuffer result = new StringBuffer(32);
		result
				.append("<table class=\"list\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\">");

		/*
		 * Set columns names
		 */
		result.append("<tbody>");
		result.append("<tr>");
		for (int i = 0; i < HISTORIC_COLUMNS_TITLES.length; i++) {
			result.append("<th width=\"" + HISTORIC_COLUMNSSIZES[i]
					+ "%\" style=\"white-space: nowrap;\">"
					+ this.getExternalizedLabel(HISTORIC_COLUMNS_TITLES[i])
					+ "</th>");
		}
		result.append("</tr>");

		// get date formater the date pattern is store in the
		// messages.properties file
		SimpleDateFormat dateFormat = new SimpleDateFormat(this
				.getExternalizedLabel(Messages.GUI_HISTORIC_DATEFORMAT_0));

		
		/*
		 * Complete the body of the table
		 */
		// count row to alternate bg
		int rowNumber = 0;
		Iterator<HistoricElement> historicElementsIterator = workflowPropertyContainer
				.get_historic().iterator();
		while (historicElementsIterator.hasNext()) {
			HistoricElement historicElement = historicElementsIterator.next();
			
			boolean mustBeClearUp = historicElement.is_isAutomatic(); 
			
			// set good background depending row number
			if (rowNumber % 2 == 0) {
				result.append("<tr class=\"oddrowbg\"");
			} else {
				result.append("<tr class=\"evenrowbg\"");
			}
			if(mustBeClearUp){
				result.append(" style=\"color:#808080;\"");
			}
			result.append(">");
			result.append("<td style=\"text-align: center;\">"
					+ historicElement.get_lastActionName() + "</td>");
			/*
			 * result.append("<td style=\"text-align: center;\">" +
			 * historicElement.get_stepName() + "</td>");
			 */
			result.append("<td style=\"text-align: center;\">"
					+ historicElement.get_owner() + "</td>");
			result.append("<td style=\"text-align: center;\">"
					+ historicElement.get_comment() + "</td>");
			result.append("<td style=\"text-align: center;\">"
					+ dateFormat.format(historicElement.get_actionDate())
					+ "</td>");
			/*result.append("<td style=\"text-align: center;\">"
					+ dateFormat.format(historicElement.get_endingDate())
					+ "</td>");*/
			result.append("<td style=\"text-align: center;\">"
					+ historicElement.is_isAutomatic() + "</td>");
			result.append("</tr>");

			// iterate rownumber
			rowNumber++;
		}

		result.append("</tbody>");
		result.append("</table>");
		return result.toString();
	}

	/**
	 * @return the _workflowInstanceID
	 * @uml.property name="_workflowInstanceID"
	 */
	public long get_workflowInstanceID() {
		return _workflowInstanceID;
	}

	/**
	 * @param instanceID
	 *            the _workflowInstanceID to set
	 * @uml.property name="_workflowInstanceID"
	 */
	public void set_workflowInstanceID(long instanceID) {
		_workflowInstanceID = instanceID;
	}

	/**
	 * Sets the selected action name that has been selected.
	 * <p>
	 * 
	 * @param actionName
	 *            the name of the selected action
	 */
	public void setParamActionname(String actionName) {
		// LOGGER.debug("WF | setParamActionname() actionName = " + actionName);

		getCms().getRequestContext().setAttribute(FORMPROPERTY_ACTIONAME,
				actionName);

	}

	/**
	 * Sets the comment that have been entered
	 * <p>
	 * 
	 * @param comment
	 *            the comment entered
	 */
	public void setParamComment(String comment) {
		// LOGGER.debug("WF | setParamComment() comment = " + comment);
		getCms().getRequestContext()
				.setAttribute(FORMPROPERTY_COMMENT, comment);
	}

	/**
	 * Get the text to be displayed as status
	 * 
	 * @param isEnded
	 *            indicate if the workflow is ended or not
	 * @return the String to display
	 */
	private String getStatusLabel(boolean isEnded) {
		if (isEnded) {
			return this
					.getExternalizedLabel(Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_ENDED_0);
		} else {
			return this
					.getExternalizedLabel(Messages.GUI_WORKFLOWSLIST_CONTENT_STATUS_UNDERWAY_0);
		}

	}

	/**
	 * Get the value of the externalized string
	 * 
	 * @param labelName
	 *            the label of the string
	 * @return the value of the string
	 */
	private String getExternalizedLabel(String labelName) {
		Locale locale = this.getLocale();
		CmsMessageContainer messageContainer = Messages.get().container(
				labelName);
		return new MessageFormat(messageContainer.key(locale), locale)
				.format(new Object[] { messageContainer.key(locale) });
	}

	/**
	 * Used to close the current JSP dialog.
	 * <p>
	 * 
	 * This page is fowarded to main page of Workflow menu
	 * 
	 * @throws JspException
	 *             if including an element fails
	 */
	public void actionCloseDialog() throws JspException {
		// LOGGER.debug("WF | actionCloseDialog");

		try {
			_uiManager.jspForwardPage(this,
					UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION,
					new HashMap<String, String[]>());
		} catch (IOException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (ServletException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
	}

}
