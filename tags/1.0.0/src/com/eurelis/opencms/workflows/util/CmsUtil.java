/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows.util;

import org.opencms.file.CmsObject;

import com.eurelis.opencms.workflows.InitWorkflowAction;

/**
 * This Class store an cmsObject that have the administrator rights.
 * @author Sébastien Bianco
 *
 */
public class CmsUtil {

	/**
	 * The cmsObject with the admin rights
	 */
	private static CmsObject adminCms;
	
	/**
	 * Set the cmsObject with admin right. This master cmsObject is given by the CmsModuleLoader of open cms when the web-app starts (but set by {@link InitWorkflowAction#initialize(CmsObject, org.opencms.configuration.CmsConfigurationManager, org.opencms.module.CmsModule)}).
	 * @param adminCms the master cmsObject
	 */
	public static void setAdminCmsObject(CmsObject adminCms) {
		CmsUtil.adminCms = adminCms;
	}
	
	/**
	 * Get the cmsObject with admin right
	 * @return the static CmsObject store in this class
	 */
	public static CmsObject getAdminCmsObject() {
		return adminCms;
	}
}
