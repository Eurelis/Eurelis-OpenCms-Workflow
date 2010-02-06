/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows;

import java.util.List;

import org.apache.log4j.Logger;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsRequestContext;

import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer;

/**
 * This class manage the connection with OSWOrkflows. This implementation doesn't take care of access right on workflows.
 * 
 * @author Sébastien Bianco
 */
public class OSWorkflowManager extends A_OSWorkflowManager {

	/** The log object for this class. */
	static final Logger	LOGGER	= Logger.getLogger(OSWorkflowManager.class);

	

	/**
	 * package constructor so as to diseable the creation of the object
	 * from another class
	 * 
	 */
	/* package */ OSWorkflowManager() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasReadRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String, com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer)
	 */
	@Override
	protected boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext, String filePath,
			WorkflowPropertyContainer propertyContainer) {
		return true;
	}


	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasWriteRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String, com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer)
	 */
	@Override
	protected boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext, String filePath,
			WorkflowPropertyContainer propertyContainer) {
		return true;
	}
	

	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#checkOwner(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer, java.lang.String)
	 */
	@Override
	protected boolean checkOwner(WorkflowPropertyContainer propertyContainer,
			String name) {
		String creator = propertyContainer.getOwner(0);
		if(creator!=null)
			return creator.equals(name);
		else return true;
	}


	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#filterListOfFilesAccordingToUserRights(java.util.List)
	 */
	@Override
	protected List<String> filterListOfFilesAccordingToUserRights(
			List<String> listOfFiles, CmsObject cmsObject) {
		
		return listOfFiles;
	}


	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasCreateRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.util.List)
	 */
	@Override
	protected boolean hasCreateRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			List<String> fileList) {
		return true;
	}


}
