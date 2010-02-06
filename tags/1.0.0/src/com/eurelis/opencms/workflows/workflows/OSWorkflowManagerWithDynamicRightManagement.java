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
 * <li>If there is allowed people define for the instance of workflow, this one take priority on rights statically defined. This dynamic right system is only use to calculate write access.</li>
 * </ul>
 * @author Sébastien Bianco
 *
 */
public class OSWorkflowManagerWithDynamicRightManagement extends OSWorkflowManagerWithRightManagementAndOwnerForbidden {

	/** The log object for this class. */
	static final Logger	LOGGER	= Logger.getLogger(OSWorkflowManagerWithDynamicRightManagement.class);
	
	/**
	 * Default constructor
	 */
	/* package */OSWorkflowManagerWithDynamicRightManagement() {
		//LOGGER.debug("WF | securityManager = \n" + _securityManager);
	}
	
	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagementAndOwnerForbidden#hasWriteRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String, com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer)
	 */
	@Override
	protected boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer) {
		boolean dynamicRight = false;
		if(propertyContainer.get_allowedPeople()!=null){
			dynamicRight = propertyContainer.get_allowedPeople().hasWriteRightOnWorkflows(cmsRequestContext.currentUser(), cmsRequestContext.getLocale());
		}
		//LOGGER.debug("WF | dynamic write rights for "+cmsRequestContext.currentUser().getName()+" : "+dynamicRight);
		
		/*
		 * Get the static right if dynamic right are false;
		 */
		if(!dynamicRight){
			return super.hasWriteRightOnWorkflow(key, cmsRequestContext, parentFolder, propertyContainer);
		}else{
			return true;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.OSWorkflowManagerWithRightManagement#hasReadRightOnWorkflow(com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String, com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer)
	 */
	@Override
	protected boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer) {
		boolean dynamicRight = false;
		if(propertyContainer.get_allowedPeople()!=null){
			dynamicRight = propertyContainer.get_allowedPeople().hasReadRightOnWorkflows(cmsRequestContext.currentUser(), cmsRequestContext.getLocale());
		}
		//LOGGER.debug("WF | dynamic read rights for "+cmsRequestContext.currentUser().getName()+" : "+dynamicRight);
		
		/*
		 * Get the static right if dynamic right are false;
		 */
		if(!dynamicRight){
			return super.hasReadRightOnWorkflow(key, cmsRequestContext, parentFolder, propertyContainer);
		}else{
			return true;
		}
	}

	

}
