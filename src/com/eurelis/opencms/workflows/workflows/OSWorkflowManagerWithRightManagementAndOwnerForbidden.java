/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows;

import org.apache.log4j.Logger;
import org.opencms.file.CmsRequestContext;

import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer;



/**
 * This class manage the connection with OSWOrkflows. 
 * This implementation take care of access right on workflows with such politic :
 * <ul> 
 * <li>If there is no file attached to the WF, the rights apply are those of NO_FILES branch.</li>
 * <li>the creator of a workflow always has read rights on his workflow.</li>
 * <li>the creator of a workflow never has write rights on his workflow. </li>
 * <li>the other users follow the rule define statically in the config file.</li>
 * </ul>
 * @author Sébastien Bianco
 *
 */
public class OSWorkflowManagerWithRightManagementAndOwnerForbidden extends OSWorkflowManagerWithRightManagement{

	/** The log object for this class. */
	private static final Logger		LOGGER				= Logger.getLogger(OSWorkflowManagerWithRightManagementAndOwnerForbidden.class);

	
	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagement#hasWriteRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String, com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer)
	 */
	@Override
	protected boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer) {
	
		return !super.checkOwner(propertyContainer, cmsRequestContext.currentUser().getName()) && super.hasWriteRightOnWorkflow(key, cmsRequestContext, parentFolder, propertyContainer);
	}

	/**
	 * Default constructor
	 */
	/* package */OSWorkflowManagerWithRightManagementAndOwnerForbidden() {
		//LOGGER.debug("WF | securityManager = \n" + _securityManager);
	}
	
}
