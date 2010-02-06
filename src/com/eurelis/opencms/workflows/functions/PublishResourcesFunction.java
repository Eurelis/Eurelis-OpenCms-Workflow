/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */
package com.eurelis.opencms.workflows.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opencms.db.CmsPublishList;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.report.CmsHtmlReport;

import com.eurelis.opencms.workflows.util.CmsUtil;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;

/**
 * This class implement a individual action that can be use during a workflow.
 * <br/> This function publish the list of resources.<br/><br/> The required
 * parameters to use this function is :
 * <ol>
 * <li> <b>The list of files:</b> This list must be stored in the transcientVar
 * map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST}
 * (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li> <b>The CmsOject instance:</b> This object must be stored in the
 * transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * </ol>
 * 
 * @author Sébastien Bianco
 * 
 */
public class PublishResourcesFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Logger	LOGGER	= Logger.getLogger(PublishResourcesFunction.class);

	/**
	 * Execute this function
	 * 
	 * @param transientVars
	 *            Variables that will not be persisted. These include inputs
	 *            given in the {@link Workflow#initialize} and
	 *            {@link Workflow#doAction} method calls. There are a number of
	 *            special variable names:
	 *            <ul>
	 *            <li><code>entry</code>: (object type:
	 *            {@link com.opensymphony.workflow.spi.WorkflowEntry}) The
	 *            workflow instance
	 *            <li><code>context</code>: (object type:
	 *            {@link com.opensymphony.workflow.WorkflowContext}). The
	 *            workflow context.
	 *            <li><code>actionId</code>: The Integer ID of the current
	 *            action that was take (if applicable).
	 *            <li><code>currentSteps</code>: A Collection of the current
	 *            steps in the workflow instance.
	 *            <li><code>store</code>: The
	 *            {@link com.opensymphony.workflow.spi.WorkflowStore}.
	 *            <li><code>descriptor</code>: The
	 *            {@link com.opensymphony.workflow.loader.WorkflowDescriptor}.
	 *            </ul>
	 *            Also, any variable set as a
	 *            {@link com.opensymphony.workflow.Register}), will also be
	 *            available in the transient map, no matter what. These
	 *            transient variables only last through the method call that
	 *            they were invoked in, such as {@link Workflow#initialize} and
	 *            {@link Workflow#doAction}.
	 * @param args
	 *            The properties for this function invocation. Properties are
	 *            created from arg nested elements within the xml, an arg
	 *            element takes in a name attribute which is the properties key,
	 *            and the CDATA text contents of the element map to the property
	 *            value.
	 * @param ps
	 *            The persistent variables that are associated with the current
	 *            instance of the workflow. Any change made to the propertyset
	 *            are persisted to the propertyset implementation's persistent
	 *            store.
	 * 
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map,
	 *      java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void executeFunction(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException

	{

		// apply the publish function
		try {
			this.applyPublish(_associatedFiles, _cmsObject);
		} catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
	}

	/**
	 * Publish a list of resources
	 * 
	 * @param listOfFiles
	 *            the list of files to publish
	 * @param cmsObject
	 *            the CmsObject that will do such actions
	 * @throws CmsException
	 *             if an error occurs during publication process
	 */
	private void applyPublish(List<String> listOfFiles, CmsObject cmsObject)
			throws CmsException {

		LOGGER.debug("enter publish !");
		
		/*
		 * Set admin object
		 */
		CmsObject adminCms = CmsUtil.getAdminCmsObject();
		CmsProject currentUserProject = cmsObject.getRequestContext()
				.currentProject();

		// backup current project for admin cms (to switch back after the
		// process)
		CmsProject backupAdminProject = adminCms.getRequestContext()
				.currentProject();

		// move to current user project
		adminCms.getRequestContext().setCurrentProject(currentUserProject);
		
		// The list of resources to publish
		List<CmsResource> listResourcesToPublish = new ArrayList<CmsResource>();
		
		LOGGER.debug("enter publish !");

		try {

			// some properties required to publish
			boolean publishSubFolder = true;
			boolean publishSibling = true;
			

			// Add all resource in the list of resource to publish
			Iterator<String> listOfFilesIterator = listOfFiles.iterator();
			while (listOfFilesIterator.hasNext()) {
				String path = listOfFilesIterator.next();
				try {
					// read resource object
					CmsResource relatedResource = adminCms.readResource(path);
					if (relatedResource != null) {
						LOGGER.debug("add "+relatedResource.getRootPath()+" to publish list");
						// add in the publish list
						listResourcesToPublish.add(relatedResource);
					}else{
						LOGGER.debug(path+" is not relative to any resource");
					}
				} catch (CmsException e) {
					LOGGER.warn("The file " + path + " cannot be read ("
							+ e.getMessage() + ")");
				}
			}

			LOGGER.debug(ErrorFormatter.formatList(listResourcesToPublish, "WF | listResourcesToPublish"));

			// Create publish List
			CmsPublishList publishList = OpenCms.getPublishManager()
					.getPublishList(adminCms, listResourcesToPublish,
							publishSibling, publishSubFolder);

			// publish Resources
			OpenCms.getPublishManager().publishProject(
					adminCms,
					new CmsHtmlReport(
							cmsObject.getRequestContext().getLocale(),
							cmsObject.getRequestContext().getSiteRoot()),
					publishList);

			// wait 2 seconds, may be it finishes fast
			OpenCms.getPublishManager().waitWhileRunning(1500);
		} catch (CmsException e) {
			LOGGER.warn(ErrorFormatter.formatList(listResourcesToPublish, "The list of following resources have not be published correctly: "));
		} finally {
			// switch back to previous project
			adminCms.getRequestContext().setCurrentProject(backupAdminProject);
		}
	}

}
