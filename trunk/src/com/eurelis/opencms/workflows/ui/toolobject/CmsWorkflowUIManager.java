/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui.toolobject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.opencms.flex.CmsFlexRequest;
import org.opencms.i18n.CmsEncoder;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsRequestUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.tools.CmsToolDialog;

import com.eurelis.opencms.workflows.util.ErrorFormatter;

/**
 * @author   Sébastien Bianco
 */
public class CmsWorkflowUIManager {

	/** The static log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(CmsWorkflowUIManager.class);

	/**
	 * The path of the current page
	 * @uml.property  name="_currentPagePath"
	 */
	private String _currentPagePath = null;

	/**
	 * The single instance of this object
	 */
	private static Map<String, CmsWorkflowUIManager> instancePerUser = new HashMap<String, CmsWorkflowUIManager>();

	/**
	 * Default constructor
	 */
	private CmsWorkflowUIManager() {
	}

	/**
	 * Get the single instance (per User) of this class (initialize it if
	 * required
	 * 
	 * @param the
	 *            user associated to this instance
	 * @return the single instance of this class
	 */
	public static CmsWorkflowUIManager getInstance(String userName) {
		if (!instancePerUser.containsKey(userName)) {
			instancePerUser.put(userName, new CmsWorkflowUIManager());
		}
		return instancePerUser.get(userName);
	}

	/**
	 * Delete the instance of {@link CmsWorkflowUIManager} associated to a user
	 * 
	 * @param userName
	 */
	public static void deleteInstance(String userName) {
		instancePerUser.remove(userName);
	}

	/**
	 * Returns the OpenCms link for the given tool path which requires
	 * parameters.
	 * <p>
	 * 
	 * Please note: Don't overuse the parameter map because this will likely
	 * introduce issues with encoding. If possible, don't pass parameters at
	 * all, or only very simple parameters with no special chars that can easily
	 * be parsed.
	 * <p>
	 * 
	 * @param jsp
	 *            the jsp action element
	 * @param toolPath
	 *            the tool path
	 * @param params
	 *            the map of required tool parameters
	 * 
	 * @return the OpenCms link for the given tool path which requires
	 *         parameters
	 */
	public static String linkForToolPath(CmsJspActionElement jsp,
			String toolPath, Map<String, String> params) {
		Map<String, String> newParams = new HashMap<String, String>(params);

		if (newParams == null) {
			// no parameters - take the shortcut
			return linkForToolPath(jsp, toolPath);
		}
		newParams.put(CmsToolDialog.PARAM_PATH, toolPath);
		return CmsRequestUtil.appendParameters(jsp.link(UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION),
				newParams, true);
	}

	/**
	 * Returns the OpenCms link for the given tool path which requires no
	 * parameters.
	 * <p>
	 * 
	 * @param jsp
	 *            the jsp action element
	 * @param toolPath
	 *            the tool path
	 * 
	 * @return the OpenCms link for the given tool path which requires
	 *         parameters
	 */
	public static String linkForToolPath(CmsJspActionElement jsp,
			String toolPath) {

		StringBuffer result = new StringBuffer();
		result.append(jsp.link(UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION));
		result.append('?');
		result.append(CmsToolDialog.PARAM_PATH);
		result.append('=');
		result.append(CmsEncoder.encode(toolPath, jsp.getRequestContext()
				.getEncoding()));
		return result.toString();
	}

	/**
	 * Redirects to the given page with the given parameters.
	 * <p>
	 * 
	 * @param wp
	 *            the workplace object
	 * @param pagePath
	 *            the path to the page to redirect to (without cms context ex:
	 *            /opencms/opencms)
	 * @param params
	 *            the parameters to send
	 * 
	 * @throws IOException
	 *             in case of errors during forwarding
	 * @throws ServletException
	 *             in case of errors during forwarding
	 */
	public void jspForwardPage(CmsWorkplace wp, String pagePath,
			Map<String, String[]> params) throws IOException, ServletException {

		String context = OpenCms.getSystemInfo().getOpenCmsContext();

		// add web-app context (ex: /opencms/openscms) if
		// required
		String pageToLoad = pagePath;
		if (!pageToLoad.startsWith(context)) {
			pageToLoad = context + pageToLoad;
		}

		// LOGGER.debug(ErrorFormatter.formatMap(params, "WF | Enter
		// jspFowardPage - link = "+wp.getJsp().link(pageToLoad)));

		Map<String, String[]> newParams = createToolParams(wp, pageToLoad,
				params);
		
		//remove "?" incase of params indicated
		if (pageToLoad.indexOf("?") > 0) {
			pageToLoad = pageToLoad.substring(0, pageToLoad.indexOf("?"));
		}

		/*LOGGER.debug(ErrorFormatter.formatMap(newParams,
				"WF | jspFowardPage params"));

		LOGGER.debug("WF | request = "
				+ wp.getJsp().getRequest()
				+ " - response = "
				+ wp.getJsp().getResponse()
				+ " - context = "
				+ wp.getJsp().getRequestContext()
				+ " - cmsDispatcher = "
				+ ((CmsFlexRequest) wp.getJsp().getRequest())
						.getRequestDispatcher(pageToLoad)
				+ " - controller = "
				+ CmsFlexController.getController(wp.getJsp().getRequest())
				+ " - cmsObject = "
				+ CmsFlexController.getController(wp.getJsp().getRequest())
						.getCmsObject()
				+ " - cmsObject.request = "
				+ CmsFlexController.getController(wp.getJsp().getRequest())
						.getCmsObject().getRequestContext()
				+ " - Absolut URI = "
				+ CmsLinkManager.getAbsoluteUri(pageToLoad, CmsFlexController
						.getController(wp.getJsp().getRequest())
						.getCurrentRequest().getElementUri()));*/

		wp.setForwarded(true);
		
		// forward to the requested page uri
		//CmsRequestUtil.forwardRequest(pageToLoad, newParams,  wp.getJsp().getRequest(), wp.getJsp().getResponse());
		
	
		try {
			
		HttpServletRequest req = wp.getJsp().getRequest();
		HttpServletResponse res = wp.getJsp().getResponse();
		

			
			// cast the request back to a flex request so the parameter map can be accessed
	        CmsFlexRequest f_req = (CmsFlexRequest)req;
	        // set the parameters
	        f_req.setParameterMap(params);
	        // check for links "into" OpenCms, these may need the webapp name to be removed
	        String vfsPrefix = OpenCms.getStaticExportManager().getVfsPrefix();
	        if (pageToLoad.startsWith(vfsPrefix)) {
	            // remove VFS prefix (will also work for empty vfs prefix in ROOT webapp case with proxy rules)
	        	pageToLoad = pageToLoad.substring(vfsPrefix.length());
	            // append the servlet name
	        	pageToLoad = OpenCms.getSystemInfo().getServletPath() + pageToLoad;
	        }
	        // forward the request
	        f_req.getRequestDispatcher(pageToLoad).forward(f_req, res);
			
		} catch (RuntimeException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
			wp.getJsp().getResponse()
					.sendRedirect(wp.getJsp().link(pageToLoad));
		}
	}

	/**
	 * Redirects to the given tool with the given parameters.
	 * <p>
	 * 
	 * @param wp
	 *            the workplace object
	 * @param toolPath
	 *            the path to the tool to redirect to
	 * @param params
	 *            the parameters to send
	 * 
	 * @throws IOException
	 *             in case of errors during forwarding
	 * @throws ServletException
	 *             in case of errors during forwarding
	 */
	private void jspForwardTool(CmsWorkplace wp, String toolPath,
			Map<String, String[]> params) throws IOException, ServletException {

		Map<String, String[]> newParams;
		if (params == null) {
			newParams = new HashMap<String, String[]>();
		} else {
			newParams = new HashMap<String, String[]>(params);
		}
		// update path param
		newParams.put(CmsToolDialog.PARAM_PATH, new String[] { toolPath });
		jspForwardPage(wp, UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION, newParams);
	}

	/**
	 * Creates a parameter map from the given url and additional parameters.
	 * <p>
	 * 
	 * @param wp
	 *            the workplace context
	 * @param url
	 *            the url to create the parameter map for (extracting query
	 *            params)
	 * @param params
	 *            additional parameter map
	 * 
	 * @return the new parameter map
	 */
	private Map<String, String[]> createToolParams(CmsWorkplace wp, String url,
			Map<String, String[]> params) {

		Map<String, String[]> newParams = new HashMap<String, String[]>();
		// add query parameters to the parameter map if required
		if (url.indexOf("?") > 0) {
			String query = url.substring(url.indexOf("?"));
			Map<String, String[]> reqParameters = CmsRequestUtil
					.createParameterMap(query);
			newParams.putAll(reqParameters);
		}
		if (params != null) {
			newParams.putAll(params);
		}

		// put close link if not set
		if (!newParams.containsKey(CmsDialog.PARAM_CLOSELINK)) {
			Map<String, String> argMap = new HashMap<String, String>();
			newParams.put(CmsDialog.PARAM_CLOSELINK,
					new String[] { linkForToolPath(wp.getJsp(),
							get_currentPagePath(), argMap) });
		}
		return newParams;
	}

	/**
	 * @return   the _currentPagePath
	 * @uml.property  name="_currentPagePath"
	 */
	public String get_currentPagePath() {
		return _currentPagePath;
	}

	/**
	 * @param pagePath   the _currentPagePath to set
	 * @uml.property  name="_currentPagePath"
	 */
	public void set_currentPagePath(String pagePath) {
		_currentPagePath = pagePath;
	}

}
