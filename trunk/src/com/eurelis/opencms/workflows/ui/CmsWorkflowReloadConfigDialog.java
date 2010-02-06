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

import org.apache.log4j.Logger;
import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRoleViolationException;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.tools.CmsToolDialog;

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.ui.toolobject.CmsWorkflowUIManager;
import com.eurelis.opencms.workflows.ui.toolobject.UISharedVariables;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;
import com.eurelis.opencms.workflows.workflows.WorkflowConfigManager;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollectorFactory;
import com.eurelis.opencms.workflows.workflows.security.WorkflowSecurityManager;

/**
 * This class is relative to the main jsp page of the workflow view
 * (workflows-main.jsp)<br/> This class dispatch the receive request to the
 * right page, depending the parameter "workflowpath".
 * 
 * @author Sébastien Bianco
 */
public class CmsWorkflowReloadConfigDialog extends CmsDialog {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(CmsWorkflowDialog.class);

	/**
	 * The CmsWorkflowUIManager associated to the current user
	 * 
	 * @uml.property name="_uiManager"
	 * @uml.associationEnd
	 */
	private CmsWorkflowUIManager _uiManager = CmsWorkflowUIManager
			.getInstance(getCms().getRequestContext().currentUser().getName());

	/**
	 * @param jsp
	 */
	public CmsWorkflowReloadConfigDialog(CmsJspActionElement jsp) {
		super(jsp);
	}

	/**
	 * @param context
	 * @param req
	 * @param res
	 */
	public CmsWorkflowReloadConfigDialog(PageContext context,
			HttpServletRequest req, HttpServletResponse res) {
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

		// if the action is cancel, then go on the default page
		if (getAction() == CmsDialog.ACTION_CANCEL) {

			return;
		}

		// reload Action
		this.reloadAction();

		// init the map of parameters from the request
		// Next page to load -> The windows with the list of configured workflows
		String pageToLoad = CmsWorkflowsConfiguredWorkflowList.DIALOG_URI;
		Map<String, String[]> params = initWorkflowParams();
		params.put(UISharedVariables.PARAM_WORKFLOWPATH,
				new String[] { CmsEncoder.decode(pageToLoad, getCms()
						.getRequestContext().getEncoding()) });

		// the URI of the default page to load
		String defaultPageToLoad = OpenCms.getSystemInfo().getOpenCmsContext()
				+ UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION;

		// LOGGER.debug("WF | 1 - Go to " + defaultPageToLoad);
		_uiManager.jspForwardPage(this, defaultPageToLoad, params);
		return;

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

		// LOGGER.debug(ErrorFormatter.formatMap(getParameterMap(), "WF |
		// initWorkflowParams"));

		// copy the parameter map
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

		// LOGGER.debug(ErrorFormatter.formatMap(params, "WF | params maps"));
		return params;
	}

	/**
	 * Reload the different config files of the module
	 */
	private void reloadAction() {

		// reload the Module configuration
		ModuleConfigurationLoader.reload();

		// reload list of available workflows
		AvailableWorkflowCollectorFactory.reload();

		// reload the config Manager
		WorkflowConfigManager.reload();

		// reload the security manager
		WorkflowSecurityManager.reload();

		// reload the A_OSWorkflowManager
		A_OSWorkflowManagerFactory.reload();

	}

}
