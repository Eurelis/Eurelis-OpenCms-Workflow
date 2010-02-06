/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRoleViolationException;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.tools.CmsToolDialog;

import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class is relative to the main jsp page of the workflow view (workflows-main.jsp)<br/> This class dispatch the receive request to the right page, depending the parameter "workflowpath".
 * @author   Sébastien Bianco
 */
public class CmsWorkflowDialog extends CmsDialog {

	/** The log object for this class. */
	//private static final Logger LOGGER = Logger.getLogger(CmsWorkflowDialog.class);

	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * @uml.property  name="_uiManager"
	 * @uml.associationEnd  
	 */
	private CmsWorkflowUIManager _uiManager = CmsWorkflowUIManager
			.getInstance(getCms().getRequestContext().currentUser().getName());

	/**
	 * @param jsp
	 */
	public CmsWorkflowDialog(CmsJspActionElement jsp) {
		super(jsp);
	}

	/**
	 * @param context
	 * @param req
	 * @param res
	 */
	public CmsWorkflowDialog(PageContext context, HttpServletRequest req,
			HttpServletResponse res) {
		this(new CmsJspActionElement(context, req, res));
	}

	/**
	 * Performs the dialog actions depending on the initialized action and
	 * displays the dialog form.
	 * <p>
	 * 
	 * @throws Exception
	 *             if writing to the JSP out fails
	 */
	public void displayDialog() throws Exception {

		// init the map of parameters from the request
		Map<String, String[]> params = initWorkflowParams();

	
		
		// if the action is cancel, then go on the default page
		if (getAction() == CmsDialog.ACTION_CANCEL) {		
			
			
			return; 
		}
		
		//the URI of the default page to load
		String defaultPageToLoad = OpenCms.getSystemInfo().getOpenCmsContext()+UISharedVariables.DEFAULTPAGETOLOAD;
		

		if (params.containsKey(UISharedVariables.PARAM_WORKFLOWPATH)) {
			/*
			 * Get the actual page and display the right one
			 */
			String[] workflowPath = params
					.get(UISharedVariables.PARAM_WORKFLOWPATH);

			if (workflowPath != null) {
				if (workflowPath.length > 0) {
					String nextPagePath = CmsEncoder.decode(workflowPath[0],getCms().getRequestContext().getEncoding());

					/*
					 * Get the next page to load
					 */
					if (StringChecker.isNotNullOrEmpty(nextPagePath)) {
						String context = OpenCms.getSystemInfo()
								.getOpenCmsContext();
						
						
						//add web-app context (ex: /opencms/openscms) if
						// required
						if (!nextPagePath.startsWith(context)) {
							nextPagePath = context+nextPagePath;
						}
						
						//remove useLess params in request
						params.put(PARAM_ACTION, new String[]{""});
						params.put(UISharedVariables.PARAM_WORKFLOWPATH,new String[]{""});

						// forward page
						_uiManager.jspForwardPage(this, nextPagePath, params);
						return;
					} else {
						//LOGGER.debug("WF | 0 - Go to "					+	defaultPageToLoad);
						_uiManager.jspForwardPage(this,
								defaultPageToLoad, params);
						return;
					}
				} else {
					//LOGGER.debug(ErrorFormatter.formatArray(workflowPath,		"WF | workflowPath = "));
					return;
				}
			} else {
				//LOGGER.debug("WF | workflowPath = null");
				return;
			}

		} else {
			//LOGGER.debug("WF | 1 - Go to "	+	defaultPageToLoad);
			_uiManager.jspForwardPage(this,
					defaultPageToLoad, params);
			return;
		}

	}

	/**
	 * Initializes the workflow tool main view.
	 * <p>
	 * 
	 * @return the new modified params array
	 * @throws CmsRoleViolationException
	 *             in case the dialog is opened by a user without the necessary
	 *             privileges
	 */
	public Map<String, String[]> initWorkflowParams() {

		//LOGGER.debug(ErrorFormatter.formatMap(getParameterMap(), "WF | initWorkflowParams"));

		//copy the parameter map
		Map<String, String[]> params = new HashMap(getParameterMap());

		// adjust params if called as default
		if (!useNewStyle()) {
			params.put(PARAM_STYLE, new String[] { CmsToolDialog.STYLE_NEW });
			setParamStyle(CmsToolDialog.STYLE_NEW);
		}

		try {
			// a dialog just for the close link param accessors
			CmsDialog wp = (CmsDialog) this;
			// set close link
			if (StringChecker.isNotNullOrEmpty(wp.getParamCloseLink())) {
				wp.setParamCloseLink(_uiManager.linkForToolPath(getJsp(),
						getParentPath()));
				params.put(CmsDialog.PARAM_CLOSELINK, new String[] { wp
						.getParamCloseLink() });
			}
		} catch (Exception e) {
			// ignore
		}

		//LOGGER.debug(ErrorFormatter.formatMap(params, "WF | params maps"));
		return params;
	}

}
