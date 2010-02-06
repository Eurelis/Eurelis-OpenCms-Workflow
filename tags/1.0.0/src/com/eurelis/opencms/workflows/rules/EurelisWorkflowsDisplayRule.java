package com.eurelis.opencms.workflows.rules;

import org.opencms.file.CmsObject;
import org.opencms.workplace.explorer.CmsResourceUtil;
import org.opencms.workplace.explorer.menu.A_CmsMenuItemRule;
import org.opencms.workplace.explorer.menu.CmsMenuItemVisibilityMode;

import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.A_OSWorkflowManagerFactory;

/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * This class implements the rules so as to display the menu "EurelisWorkflows"
 * in the explorer view of the workplace.<br/> The menu must be displayed if
 * workflows exist in the system and if the user is allowed to use, at least,
 * one of the defined workflow.
 * 
 * @author Sébastien Bianco
 * 
 */
public class EurelisWorkflowsDisplayRule extends A_CmsMenuItemRule {

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.explorer.menu.I_CmsMenuItemRule#getVisibility(org.opencms.file.CmsObject,
	 *      org.opencms.workplace.explorer.CmsResourceUtil[])
	 */
	public CmsMenuItemVisibilityMode getVisibility(CmsObject cms,
			CmsResourceUtil[] resourceUtil) {		
		
		//get instance of A_OSWorkflowManager
		A_OSWorkflowManager osWorkflowManager = A_OSWorkflowManagerFactory.getInstance();

		if(resourceUtil.length>0){
		//check that the user can access to, a least, one workflow
		if(osWorkflowManager.getMapOfAccessibleWorkflows(cms.getRequestContext(),cms.getRequestContext()
				.addSiteRoot(resourceUtil[0].getFullPath())).size()>0){		
			return CmsMenuItemVisibilityMode.VISIBILITY_ACTIVE;
		}else{
			return CmsMenuItemVisibilityMode.VISIBILITY_INACTIVE;
		}
		}else{
			return CmsMenuItemVisibilityMode.VISIBILITY_INACTIVE;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opencms.workplace.explorer.menu.I_CmsMenuItemRule#matches(org.opencms.file.CmsObject,
	 *      org.opencms.workplace.explorer.CmsResourceUtil[])
	 */
	public boolean matches(CmsObject cms, CmsResourceUtil[] resourceUtil) {
		return true;
	}
	


	
}

