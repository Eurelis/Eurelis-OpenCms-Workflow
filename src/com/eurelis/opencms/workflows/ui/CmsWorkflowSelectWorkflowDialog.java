/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplaceSettings;

import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This class permits to diplay the list of all available workflows
 * 
 * @author Sébastien Bianco
 */
public class CmsWorkflowSelectWorkflowDialog extends CmsDialog {

	/** The URI of the JSP that load this Dialog object */
	public static final String		DIALOG_URI						= UISharedVariables.WORKFLOW_MAIN_FOLDER
																			+ "listAvailableWorkflows.jsp";

	/** The log object for this class. */
	private static final Logger		LOGGER							= Logger
																			.getLogger(CmsWorkflowSelectWorkflowDialog.class);

	/** Request parameter name for the resource list. */
	protected static final String	PARAM_RESOURCELIST				= "resourcelist";

	/** Request parameter name for the resource list. */
	protected static final String	PARAM_RESOURCE					= "resource";

	/** The tag-name of the variable "workflowname" */
	protected static final String	FORMPROPERTY_WORKFLOWNAME		= "workflowname";

	/** The tag-name of the variable "comment" */
	protected static final String	FORMPROPERTY_COMMENT			= "comment";

	/** The tag-name of the variable "selectedFiles" */
	protected static final String	FORMPROPERTY_SELECTEDFILES		= "selectedfiles";

	/** The tag-name of the variable "instanceName" */
	protected static final String	FORMPROPERTY_INSTANCENAME		= "instancename";

	/** The String coding the CSS style of fieldset */
	protected static final String	FIELDSET_STYLE_CSS_FRAGMENT		= "border: 1px hidden DarkGray;";

	/**
	 * The path of the file that contains the part of javascript to append in the file
	 */
	protected static final String	JAVASCRIPT_PART_FILEPATH		= UISharedVariables.WORKFLOW_RESOURCE_FOLDER_FROM_ROOT
																			+ UISharedVariables.WORKFLOW_JAVASCRIPT_RESOURCE_FOLDER
																			+ "javascriptFragmentForWorkflowsSelector.txt";

	/** The delimiter that is used in the resource list request parameter. */
	protected static final String	DELIMITER_RESOURCES				= "|";

	/** The selected workflow name. */
	protected String				_paramWorkflowName;

	/**
	 * The instance of the OSWorkflowManager so as to create instances of workflows
	 * 
	 * @uml.property name="_osWorkflowManager"
	 * @uml.associationEnd
	 */
	protected A_OSWorkflowManager	_osWorkflowManager				= null;

	/**
	 * The serialized form of the list of selected files
	 */
	protected String				_paramListoffiles				= null;

	/**
	 * The variable that will store the entered comment
	 */
	protected String				_paramComment					= null;

	/**
	 * The variable that will store the selected files
	 */
	protected String				_paramSelectedFiles				= null;

	/**
	 * The variable that will store the paramResource
	 */
	protected String				_paramResource					= "";

	/**
	 * The variable that will store the paramResourceList
	 */
	protected String				_paramResourcelist				= "";

	/**
	 * The variable that will store the instanceName;
	 */
	protected String				_paramInstancename				= "";

	/**
	 * The list of files passed as parameter
	 */
	protected List<String>			_resourceList					= new ArrayList<String>();

	/**
	 * Indicate if the submit button must be disable (in case there is no available workflows)
	 */
	private boolean					_disableSubmitButton			= false;

	/**
	 * The list of the columns title (workflow choice)
	 */
	protected static final String[]	WORKFLOWCHOICE_COLUMNS_TITLES	= new String[] {
			Messages.GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_RADIOBUTTON_0,
			Messages.GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_NAME_0,
			Messages.GUI_SELECTWORKFLOW_WORKFLOWCHOICE_COLUMNNAME_DESCRIPTION_0 };

	/**
	 * The list of the columns size (workflow choice)
	 */
	protected static final String[]	WORKFLOWCHOICE_COLUMNS_SIZES	= new String[] { "3", "27", "70" };

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
	public CmsWorkflowSelectWorkflowDialog(PageContext context, HttpServletRequest request, HttpServletResponse response) {
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
	public CmsWorkflowSelectWorkflowDialog(CmsJspActionElement jsp) {
		super(jsp);

		// initialize the object
		initialize();
	}

	/**
	 * This method does the main things to do during the initialization
	 */
	protected void initialize() {

		// load the _osWorkflowManager
		this._osWorkflowManager = A_OSWorkflowManagerFactory.getInstance();

	}

	/**
	 * Returns an initialized CmsDialog instance that is read from the request attributes.
	 * <p>
	 * This method is used by dialog elements. The dialog elements do not initialize their own workplace class, but use
	 * the initialized instance of the "master" class. This is required to ensure that parameters of the "master" class
	 * can properly be kept on the dialog elements.
	 * <p>
	 * To prevent null pointer exceptions, an empty dialog is returned if nothing is found in the request attributes.
	 * <p>
	 * 
	 * @param context
	 *            the JSP page context
	 * @param req
	 *            the JSP request
	 * @param res
	 *            the JSP response
	 * @return an initialized CmsDialog instance that is read from the request attributes
	 */
	public static CmsWorkflowSelectWorkflowDialog initCmsDialog(PageContext context, HttpServletRequest req,
			HttpServletResponse res) {

		CmsWorkflowSelectWorkflowDialog wp = new CmsWorkflowSelectWorkflowDialog(new CmsJspActionElement(context, req,
				res));
		wp.fillParamValues(req);

		return wp;
	}

	/**
	 * Returns the HTML to build the input form of the upload dialog.
	 * <p>
	 * 
	 * @return the HTML to build the input form of the upload dialog
	 */
	protected String defaultActionHtml() {

		StringBuffer result = new StringBuffer(32);

		result.append(htmlStart());
		result.append(bodyStart(null));
		// result.append(dialogStart());
		result.append(dialogContentStart(""));

		// result.append("<div class=\"dialogcontent\" unselectable=\"on\">\n");
		// result.append("<!-- dialogcontent start -->\n");

		// create Script that will display/hide the form
		result.append(this.getJavascriptFunction());

		// HTML title of the page
		result.append("<h1>" + this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_PAGETITLE_0) + "</h1>");

		/*
		 * Create hidden part of page
		 */

		result.append("<hr/>\n");
		result.append("<form method=\"POST\" action=\"" + getJsp().link(getDialogReturnUri())
				+ "\" onsubmit=\"return mySubmitAction('");
		result.append(DIALOG_OK);
		result.append("', null, 'main',currentDiv);\" name=\"main\" id=\"main\">");
		result.append(allParamsAsHidden());

		/*
		 * Append parameter resource/resourcelist to not lose them when reload
		 */
		result.append("<p>");
		result.append("<input type=\"hidden\" name=\"");
		result.append(PARAM_RESOURCELIST);
		result.append("\" value=\"" + _paramResourcelist + "\">");
		result.append("<input type=\"hidden\" name=\"");
		result.append(PARAM_RESOURCE);
		result.append("\" value=\"" + _paramResource + "\">");

		if (getParamFramename() == null) {
			result.append("<input type=\"hidden\" name=\"");
			result.append(PARAM_FRAMENAME);
			result.append("\" value=\"\">");
		}

		result.append("<p>");

		/*
		 * Display zone to select workflow and enter comment (if there is available actions)
		 */
		result.append("<div class=\"selectworkflow\">");
		result.append("<fieldset class=\"dialogblock\">");
		result.append("<legend><span class=\"textbold\" unselectable=\"on\">"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_SECTIONNAME_SELECTWORKFLOW_0)
				+ "</span></legend>");
		result.append(this.createFormPart());
		result.append("</fieldset>");
		result.append("</div>");

		/*
		 * The list of selected files
		 */

		result.append("<div class=\"displayfiles\">");
		result.append("<fieldset class=\"dialogblock\">");
		result
				.append("<legend><span class=\"textbold\" unselectable=\"on\">"
						+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_SECTIONNAME_LISTOFFILES_0)
						+ "</span></legend>");
		result.append(this.createTableOfFiles());
		result.append("</fieldset>");
		result.append("</div>");

		// result.append(dialogBlockEnd());

		// result.append(dialogContentEnd());
		// result.append(dialogButtonsOkCancel("ok", "cancel"));
		result.append("</form>\n");

		result.append("</div>");

		// result.append(dialogEnd());
		result.append(bodyEnd());
		result.append(htmlEnd());
		return result.toString();
	}

	/**
	 * Create the part of code (HTML) corresponding to the form (availables workflows + comment + buttons)
	 * 
	 * @return the HTML part of corresponding to the form
	 */
	private String createFormPart() {

		StringBuffer result = new StringBuffer(32);

		/*
		 * The workflow instance name
		 */
		/*
		 * Display zone to select workflow and enter comment (if there is available actions)
		 */
		result.append("<div class=\"workflowInstanceName\">");
		result.append("<fieldset class=\"dialogblock\" style=\"" + FIELDSET_STYLE_CSS_FRAGMENT + "\">");
		result.append("<legend><span class=\"textbold\" style=\"color:DarkGrey;\" unselectable=\"on\">"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_SUBSECTIONNAME_WORKFLOWINSTANENAME_0)
				+ "</span></legend>");
		result.append(this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_WORKFLOWINSTANENAME_TEXT_0));
		result.append("<input id=\"" + FORMPROPERTY_INSTANCENAME + "\" name=\"" + FORMPROPERTY_INSTANCENAME
				+ "\" type=\"text\" size=\"100\" onClick=\"resetStyle(this);\"/>");
		result.append("</fieldset>");
		result.append("</div>");

		/*
		 * List of available workflows
		 */
		result.append("<div class=\"SelectWorkflowType\">");
		result.append("<fieldset class=\"dialogblock\" style=\"" + FIELDSET_STYLE_CSS_FRAGMENT + "\">");
		result.append("<legend><span class=\"textbold\" style=\"color:DarkGrey;\" unselectable=\"on\">"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_SUBSECTIONNAME_SELECTWORKFLOW_0)
				+ "</span></legend>");
		result.append(this.createTableOfAvailablesWorkflows());
		result.append("</fieldset>");
		result.append("</div>");

		/*
		 * The text area for comment
		 */
		result.append("<div class=\"SelectWorkflowType\">");
		result.append("<fieldset class=\"dialogblock\" style=\"" + FIELDSET_STYLE_CSS_FRAGMENT + "\">");
		result.append("<legend><span class=\"textbold\" style=\"color:DarkGrey;\" unselectable=\"on\">"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_SUBSECTIONNAME_COMMENT_0) + "</span></legend>");
		result.append("<table cellspacing=\"3\" cellpadding=\"2\"  width=\"100%\" border=\"0\">");
		result.append("<tbody>");
		result
				.append("<tr><td style=\"text-align:center;\"><textarea onFocus=\"this.value=''; this.onfocus=null;\" rows=\"4\" cols=\"100\" name=\""
						+ FORMPROPERTY_COMMENT
						+ "\" wrap=\"soft\">"
						+ this.getExternalizedLabel(Messages.GUI_ENTERCOMMENT_0) + "</textarea></td></tr>\n");

		result.append("</tbody>\n");
		result.append("</table>\n");
		result.append("</fieldset>");
		result.append("</div>");

		//disable submit button if there is no available wf
		if(this._disableSubmitButton){
			result.append(dialogButtonsOkCancel(" disabled=\"true\"",""));
		}else{
			result.append(dialogButtonsOkCancel());
		}

		return result.toString();

	}
	
	
    
	
	

	/**
	 * Performs the dialog actions depending on the initialized action and displays the dialog form.
	 * <p>
	 * 
	 * @throws JspException
	 *             if dialog actions fail
	 * @throws IOException
	 *             if writing to the JSP out fails, or in case of errors forwarding to the required result page
	 * @throws ServletException
	 *             in case of errors forwarding to the required result page
	 */
	public void displayDialog() throws IOException, JspException, ServletException {
		switch (getAction()) {

			case ACTION_CANCEL:
				// ACTION: cancel button pressed
				actionCloseDialog();
				break;

			case ACTION_OK:
				// ACTION: ok button pressed
				setParamAction(DIALOG_OK);
				// commit the action
				actionCommit();
				// close the windows and go back to previous frame
				actionCloseDialog();
				break;
			case ACTION_DEFAULT:
			default:
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
		String workflowKey = (String) getCms().getRequestContext().getAttribute(FORMPROPERTY_WORKFLOWNAME);
		String workflowName = _osWorkflowManager.getTitleOfWorkflow(new WorkflowKey(workflowKey, true));

		/*
		 * Update list of parameters
		 */
		List<Parameter> parameters = _osWorkflowManager.getListOfParameters(new WorkflowKey(workflowKey, true));
		Iterator<Parameter> parametersIterator = parameters.iterator();
		while (parametersIterator.hasNext()) {
			Parameter parameter = parametersIterator.next();
			// get the corresponding value in the request
			String paramValueFromRequest = (String) getJsp().getRequest().getParameter(
					workflowKey + "_" + parameter.get_name());

			if (StringChecker.isNotNullOrEmpty(paramValueFromRequest)) {
				parameter.set_value(paramValueFromRequest);
			}
			else {
				// if the parameter is a boolean, not found => box unchecked =>
				// false
				if (parameter.get_type() == Parameter.BOOLEAN) {
					parameter.set_value(Boolean.FALSE.toString());
				}
				LOGGER.debug("WF | The parameter " + parameter.get_name() + " has not been found in the request.");
			}

		}

		// create instance
		_osWorkflowManager.createInstanceOfWorkflow(workflowName, this._paramInstancename, getCms().getRequestContext()
				.currentUser().getName(), (String) getCms().getRequestContext().getAttribute(FORMPROPERTY_COMMENT),
				this.getListOfAdjustedFiles(), getCms(), parameters);
	}

	/**
	 * Get the list of files with adjusted FilePath (prefix with the current user site path)
	 * 
	 * @return the list of file path (absolut in VFS) of the selected files, an <i>empty list</i> if there is no files
	 *         selected
	 */
	private List<String> getListOfAdjustedFiles() {
		/*
		 * Update list of Selected files
		 */
		List<String> listOfFiles = new ArrayList<String>();
		Iterator<String> resourceIterator = _resourceList.iterator();
		while (resourceIterator.hasNext()) {
			String fileToCheck = resourceIterator.next();
			try {
				CmsResource resource = OpenCmsEasyAccess.getResource(fileToCheck);
				// if ok
				if (resource != null) {
					listOfFiles.add(resource.getRootPath());
				}
				else {
					// it's not ok
					// get the user site root and update filepath
					String adjustedFilePath = getCms().getRequestContext().addSiteRoot(fileToCheck);
					CmsResource adjustedResource = OpenCmsEasyAccess.getResource(adjustedFilePath);
					// if ok
					if (adjustedResource != null) {
						listOfFiles.add(adjustedResource.getRootPath());
					}
					else {
						LOGGER.warn("The file with adjusted File path " + adjustedFilePath
								+ " has not been added (probably file not found error)");
					}
				}

			}
			catch (Exception e) {
				LOGGER.error(ErrorFormatter.formatException(e));
			}
		}
		return listOfFiles;
	}

	/**
	 * @see org.opencms.workplace.administration.A_CmsImportFromHttp#getDialogReturnUri()
	 */
	public String getDialogReturnUri() {

		return DIALOG_URI;
	}

	/**
	 * @see org.opencms.workplace.CmsWorkplace#initMessages()
	 */
	protected void initMessages() {

		// add default resource bundles
		addMessages(org.opencms.workplace.Messages.get().getBundleName());
		addMessages(org.opencms.workplace.tools.Messages.get().getBundleName());
	}

	/**
	 * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

		// set the dialog type
		setParamDialogtype(getClass().getName());

		// fill the parameter values in the get/set methods
		fillParamValues(request);

		// set the action for the JSP switch
		if (DIALOG_OK.equals(getParamAction())) {
			// ok button pressed
			setAction(ACTION_OK);
		}
		else if (DIALOG_CANCEL.equals(getParamAction())) {
			// cancel button pressed
			setAction(ACTION_CANCEL);
		}
		else {
			// first dialog call, set the default action
			setAction(ACTION_DEFAULT);
		}
	}

	/**
	 * Sets the selected workflow name that has been selected.
	 * 
	 * @param workflowName
	 *            the name of the selected workflow
	 */
	public void setParamWorkflowname(String workflowName) {
		_paramWorkflowName = workflowName;
		getCms().getRequestContext().setAttribute(FORMPROPERTY_WORKFLOWNAME, workflowName);

	}

	/**
	 * Sets the comment that have been entered
	 * 
	 * @param comment
	 *            the comment entered
	 */
	public void setParamComment(String comment) {
		_paramComment = comment;
		getCms().getRequestContext().setAttribute(FORMPROPERTY_COMMENT, comment);
	}

	/**
	 * Sets the list of selected files that have been entered
	 * 
	 * @param selectedFiles
	 *            the list of selected files
	 */
	public void setParamSelectedfiles(String selectedFiles) {
		_paramSelectedFiles = selectedFiles;
		getCms().getRequestContext().setAttribute(FORMPROPERTY_SELECTEDFILES, selectedFiles);
	}

	/**
	 * Sets the instanceName files that have been entered
	 * 
	 * @param instanceName
	 *            the name of the instance of workflow
	 */
	public void setParamInstancename(String instanceName) {
		_paramInstancename = instanceName;
		getCms().getRequestContext().setAttribute(FORMPROPERTY_INSTANCENAME, instanceName);
	}

	/**
	 * Sets the value of the resourcelist parameter.
	 * 
	 * @param paramResourcelist
	 *            the value of the resourcelist parameter
	 */
	public void setParamResourcelist(String paramResourcelist) {
		if (StringChecker.isNotNullOrEmpty(paramResourcelist)) {
			_resourceList = CmsStringUtil.splitAsList(paramResourcelist, DELIMITER_RESOURCES, true);
			Collections.sort(_resourceList);
		}
		if (paramResourcelist != null)
			_paramResourcelist = paramResourcelist;

		getCms().getRequestContext().setAttribute(PARAM_RESOURCELIST, paramResourcelist);
	}

	/**
	 * Sets the value of the file parameter.
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setParamResource(String value) {
		super.setParamResource(value);
		if (StringChecker.isNotNullOrEmpty(value)) {
			_resourceList = new ArrayList<String>();
			_resourceList.add(value);
		}
		if (value != null)
			_paramResource = value;

		getCms().getRequestContext().setAttribute(PARAM_RESOURCE, value);
	}

	/**
	 * Create the string corresponding to the table containing the list of available workflows
	 * 
	 * @return the HTML code of the table containing the list of available workflows
	 */
	private String createTableOfAvailablesWorkflows() {
		StringBuffer result = new StringBuffer(32);

		// get available workflow
		Map<WorkflowKey, DescriptionContainer> availableWorkflows = _osWorkflowManager.getMapOfAccessibleWorkflows(
				getCms().getRequestContext(), this.getListOfAdjustedFiles());

		//if there is no available message juste display an error message
		if (availableWorkflows.size() == 0) {
			result.append("<p>"+this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_WORKFLOWCHOICE_ERROR_NOAVAILABLEWORKFLOWS_0)+"<p>");
			this._disableSubmitButton = true;
		}else{

			// HTML table header
			result.append("<table class=\"list\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\"><thead><tr>");
			for (int i = 0; i < WORKFLOWCHOICE_COLUMNS_TITLES.length; i++) {
				result.append("<th width=\"" + WORKFLOWCHOICE_COLUMNS_SIZES[i] + "%\" style=\"white-space: nowrap;\">"
						+ this.getExternalizedLabel(WORKFLOWCHOICE_COLUMNS_TITLES[i]) + "</th>");
			}
			result.append("</tr></thead><tbody>");

			/*
			 * Write all available workflows
			 */
			Iterator<Entry<WorkflowKey, DescriptionContainer>> mapIterator = availableWorkflows.entrySet().iterator();

			// LOGGER.debug(ErrorFormatter.formatMap(availableWorkflows, "WF | availableWorkflows"));

			// permit to change background
			int rowIndex = 0;
			while (mapIterator.hasNext()) {
				Entry<WorkflowKey, DescriptionContainer> entry = mapIterator.next();
				// change bg depending row or even line
				if (rowIndex % 2 == 0) {
					result.append("<tr class=\"oddrowbg\">");
				}
				else {
					result.append("<tr class=\"evenrowbg\">");
				}

				// append the radio button
				result.append("<td style=\"text-align: center;\"><input type=radio name=\"" + FORMPROPERTY_WORKFLOWNAME
						+ "\" value=\"" + (String) entry.getKey().get_key() + "\" onClick=\"displayWorkflowParameter('"
						+ entry.getKey() + "_param')\"");

				// check per default the first workflow button
				if (rowIndex == 0) {
					result.append(" checked");
				}
				result.append("/></td>");

				// append the name of the workflow
				if (entry.getValue() != null) {
					result.append("<td style=\"text-align: center;\">"
							+ ((DescriptionContainer) entry.getValue()).get_title() + "</td>");
				}
				else {
					result.append("<td>null</td>");
				}

				// append the description
				result.append("<td style=\"text-align: left; font-style:italic;\">");

				if (entry.getValue() != null) {
					result.append(((DescriptionContainer) entry.getValue()).get_description());
				}
				else {
					result.append("<td>null</td>");
				}

				/*
				 * append the list of parameters
				 */
				result.append("<div id=\"" + entry.getKey() + "_param\"");
				// display the parameter table of the first workflow
				if (rowIndex != 0) {
					result.append(" style=\"display:none;visibility:hidden;\"");
				}
				else {
					result.append(" style=\"display:inline;visibility:visible;\"");
				}
				result.append(">");
				result.append(this.createParameterTable(entry.getKey().get_key()));

				// close tags
				result.append("</div>");
				result.append("</td>");
				result.append("</tr>");

				// increment rowIndex
				rowIndex++;
			}

			// HTML footer of the table
			result.append("</tbody>");
			result.append("</table>");

		}
		return result.toString();
	}

	/**
	 * Generate a script javascript to hide/display the lists of parameters
	 * 
	 * @return
	 */
	private String getJavascriptFunction() {
		StringBuffer result = new StringBuffer(32);
		result.append("<script type=\"text/javascript\">");

		// Load the part of javascript stored in a File
		CmsResource javascriptFileResource = OpenCmsEasyAccess.getResource(JAVASCRIPT_PART_FILEPATH);
		if (javascriptFileResource != null) {
			if (javascriptFileResource.isFile()) {
				result.append(new String(OpenCmsEasyAccess.readFile(javascriptFileResource)));

			}
			else {
				LOGGER.warn("the resource " + JAVASCRIPT_PART_FILEPATH + " is not a file !");
			}
		}
		else {
			LOGGER.warn("the resource " + JAVASCRIPT_PART_FILEPATH + " has not been found !");
		}

		// Add the define constants
		result.append(Parameter.getStaticConstantAsString());

		/*
		 * Hide all div of workflow
		 */
		result.append("function displayWorkflowParameter(divToDisplay){");
		Map<WorkflowKey, DescriptionContainer> availableWorkflows = _osWorkflowManager.getMapOfAccessibleWorkflows(
				getCms().getRequestContext(), this.getListOfAdjustedFiles());
		Iterator<Entry<WorkflowKey, DescriptionContainer>> mapIterator = availableWorkflows.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Entry<WorkflowKey, DescriptionContainer> entry = mapIterator.next();
			result.append("document.getElementById('" + entry.getKey() + "_param').style.display = 'none';");
			result.append("document.getElementById('" + entry.getKey() + "_param').style.visibility = 'hidden';");
		}

		// display the right one
		result.append("document.getElementById(divToDisplay).style.display = 'inline';");
		result.append("document.getElementById(divToDisplay).style.visibility = 'visible';");

		// update the current selected workflow
		result.append("currentDiv = divToDisplay;");
		result.append("}");

		// initialize variable to first available workflow
		Iterator<Entry<WorkflowKey, DescriptionContainer>> mapIterator2 = availableWorkflows.entrySet().iterator();
		if (mapIterator2.hasNext()) {
			result.append("var currentDiv = '" + mapIterator2.next().getKey() + "_param';");
		}
		result.append("</script>");
		return result.toString();
	}

	/**
	 * Create the html part corresponding to the list of parameters of the workflow
	 * 
	 * @param key
	 *            the workflow name
	 * @return the HTML section corresponding to the list of parameter, an <i>empty string</i> if there is no parameters
	 *         to display
	 */
	private String createParameterTable(String key) {
		StringBuffer result = new StringBuffer(32);

		// check if there is some parameters
		List<Parameter> listOfParams = _osWorkflowManager.getListOfParameters(new WorkflowKey(key, true));

		if (listOfParams.size() > 0) {

			// HTML table header
			result
					.append("<table class=\"list\" cellspacing=\"0\" cellpadding=\"1\" width=\"70%\" style=\"border:1px solid black;\">");
			result.append("<tbody>");

			// get list of parameters
			Iterator<Parameter> listOfParamsIterator = listOfParams.iterator();
			while (listOfParamsIterator.hasNext()) {
				result.append(listOfParamsIterator.next().getAsHTMLTableRowString(key));
			}

			// close tags
			result.append("</tbody>");
			result.append("</table>");

			return result.toString();
		}
		else
			return "";
	}

	/**
	 * Create the string corresponding to the table containing the list of selected files
	 * 
	 * @return the HTML code of the table containing the list of files
	 */
	protected String createTableOfFiles() {

		StringBuffer result = new StringBuffer(32);

		// HTML table header
		result.append("<table class=\"list\" cellspacing=\"0\" cellpadding=\"1\" width=\"100%\" id=\"listoffiles\">");

		// display header
		result.append("<tbody>");
		result.append("<tr>");
		result.append("<th width=\"100%\" style=\"white-space: nowrap;\">"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_ASSOCIATEDFILES_COLUMNNAME_FILEPATH_0)
				+ "</th>");
		result.append("</tr>");

		// display table content
		if (_resourceList != null) {
			for (int rowIndex = 0; rowIndex < _resourceList.size(); rowIndex++) {
				// change bg depending row or even line
				if (rowIndex % 2 == 0) {
					result.append("<tr class=\"oddrowbg\">");
				}
				else {
					result.append("<tr class=\"evenrowbg\">");
				}
				result.append("<td style=\"text-align: left;\">" + _resourceList.get(rowIndex) + "</td>");
				result.append("</tr>");
			}
		}

		// HTML footer of the table
		result.append("</tbody>\n");
		result.append("</table>\n");

		return result.toString();
	}

	/**
	 * Get the value of the externalized string
	 * 
	 * @param labelName
	 *            the label of the string
	 * @return the value of the string
	 */
	protected String getExternalizedLabel(String labelName) {
		Locale locale = this.getLocale();
		CmsMessageContainer messageContainer = Messages.get().container(labelName);
		return new MessageFormat(messageContainer.key(locale), locale).format(new Object[] { messageContainer
				.key(locale) });
	}
}
