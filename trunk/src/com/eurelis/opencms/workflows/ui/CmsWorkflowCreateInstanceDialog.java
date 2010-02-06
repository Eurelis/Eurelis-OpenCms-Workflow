/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;

import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class implements a windows that allows to instanciate an instance of workflow. It allows moreover to add the
 * files to associate with the WF.
 * 
 * @author Sébastien Bianco
 */
public class CmsWorkflowCreateInstanceDialog extends CmsWorkflowSelectWorkflowDialog {

	/** The URI of the JSP that load this Dialog object */
	public static final String		DIALOG_URI	= UISharedVariables.WORKFLOWVIEW_ROOT_LOCATION + "createInstance.jsp";

	/** The log object for this class. */
	private static final Logger		LOGGER		= Logger.getLogger(CmsWorkflowCreateInstanceDialog.class);

	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * 
	 * @uml.property name="_uiManager"
	 * @uml.associationEnd
	 */
	private CmsWorkflowUIManager	_uiManager	= null;

	/**
	 * explicit Constructor
	 * 
	 * @param jsp
	 */
	public CmsWorkflowCreateInstanceDialog(CmsJspActionElement jsp) {
		super(jsp);
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
	public CmsWorkflowCreateInstanceDialog(PageContext context, HttpServletRequest request, HttpServletResponse response) {
		super(context, request, response);
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
	public static CmsWorkflowCreateInstanceDialog initCmsDialog(PageContext context, HttpServletRequest req,
			HttpServletResponse res) {

		CmsWorkflowCreateInstanceDialog wp = new CmsWorkflowCreateInstanceDialog(new CmsJspActionElement(context, req,
				res));
		wp.fillParamValues(req);

		return wp;
	}

	/*
	 * (non-Javadoc)
	 * @see org.opencms.workplace.CmsWorkplace#fillParamValues(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void fillParamValues(HttpServletRequest request) {
		super.fillParamValues(request);

		// update CmsWoroflowUIManager
		this._uiManager = CmsWorkflowUIManager.getInstance(getCms().getRequestContext().currentUser().getName());
		if (_uiManager != null) {
			//LOGGER.debug("WF | uri = " + DIALOG_URI);
			this._uiManager.set_currentPagePath(DIALOG_URI);
		}
		else {
			LOGGER.warn("WF | uiManager is null");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.ui.CmsWorkflowSelectWorkflowDialog#initialize()
	 */
	@Override
	protected void initialize() {
		super.initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.ui.CmsWorkflowSelectWorkflowDialog#actionCommit()
	 */
	@Override
	public void actionCommit() throws IOException, ServletException {
		//LOGGER.debug(ErrorFormatter.formatMap(getParameterMap(), "WF | parameterMap"));
	
		// collect the list of files
		List<String> listOfFiles = new ArrayList<String>();
		String[] resourceArray = (String[]) getParameterMap().get(PARAM_RESOURCELIST);
		//LOGGER.debug(ErrorFormatter.formatArray(resourceArray, "WF | resourceArray"));
		if (resourceArray != null && resourceArray.length > 0) {
			for (int i = 0; i < resourceArray.length; i++) {
				if (StringChecker.isNotNullOrEmpty(resourceArray[i])) {
					String filepath = resourceArray[i].trim();
					if (!listOfFiles.contains(filepath)) {
						listOfFiles.add(filepath);
					}
				}
			}

		}
		this._resourceList = listOfFiles;

		super.actionCommit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.ui.CmsWorkflowSelectWorkflowDialog#createTableOfFiles()
	 */
	@Override
	protected String createTableOfFiles() {
		String result = "";
		result += "\n<script language=\"Javascript\">\n";
		result += "var labelToDisplayForInput = '"
				+ this.getExternalizedLabel(Messages.GUI_SELECTWORKFLOW_ASSOCIATEDFILES_LABEL_INPUT_0) + "';\n";
		result += "</script>\n";
		result += "<table cellspacing=\"0\" cellpadding=\"1\" width=\"100%\"><tr><td>\n";
		// add buttons to add some inputs
		String buttonURL = OpenCms.getSystemInfo().getContextPath() + "/resources/buttons/new.png";
		String buttonSelectFile = OpenCms.getSystemInfo().getContextPath() + "/resources/buttons/folder.png";
		result += "<img src=\"" + buttonURL + "\" onclick=\"addNewInput('tableofinput','" + buttonSelectFile
				+ "');\"/>";
		result += "\n</td></tr><tr><td>\n";

		// add a table where will be added the different input to add
		result += "<table cellspacing=\"0\" cellpadding=\"1\" width=\"100%\" id=\"tableofinput\">\n";
		result += "</table>\n";

		// result += "\n</td></tr><tr><td>\n";
		// add the table of files
		// result += super.createTableOfFiles();
		result += "\n</td></tr></table>\n";
		return result;
	}

	/**
	 * Used to close the current JSP dialog.
	 * <p>
	 * This page is fowarded to main page of Workflow menu
	 * 
	 * @throws JspException
	 *             if including an element fails
	 */
	@Override
	public void actionCloseDialog() throws JspException {
		// LOGGER.debug("WF | actionCloseDialog");
		// LOGGER.debug("WF | UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION = "+ UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION);
		// LOGGER.debug("uiManager = " + _uiManager);
		try {
			this._uiManager.jspForwardPage(this, UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION,
					new HashMap<String, String[]>());
		}
		catch (IOException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		catch (ServletException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
	}

	/*
	 * @see org.opencms.workplace.administration.A_CmsImportFromHttp#getDialogReturnUri()
	 */
	@Override
	public String getDialogReturnUri() {

		return DIALOG_URI;
	}

	/**
	 * Sets the value of the resourcelist parameter.
	 * 
	 * @param paramResourcelist
	 *            the value of the resourcelist parameter
	 */
	@Override
	public void setParamResourcelist(String paramResourcelist) {
		//LOGGER.debug("paramResourcelist = " + paramResourcelist);
		if (StringChecker.isNotNullOrEmpty(paramResourcelist)) {

			_resourceList = CmsStringUtil.splitAsList(paramResourcelist, DELIMITER_RESOURCES, true);
			Collections.sort(_resourceList);
		}
		if (paramResourcelist != null)
			_paramResourcelist = paramResourcelist;

		getCms().getRequestContext().setAttribute(PARAM_RESOURCELIST, paramResourcelist);
	}

}
