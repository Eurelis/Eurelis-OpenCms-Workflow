/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */
package com.eurelis.opencms.workflows.workflows;

import org.opencms.i18n.A_CmsMessageBundle;
import org.opencms.i18n.I_CmsMessageBundle;

/**
 * Convenience class to access the localized messages of this OpenCms package.<p> 
 * 
 * @author Sébastien Bianco
 */
public final class Messages extends A_CmsMessageBundle {

	/** Name of the used resource bundle. */
	private static final String BUNDLE_NAME = "com.eurelis.opencms.workflows.workflows.messages";

	/** Static instance member. */
	private static final I_CmsMessageBundle INSTANCE = new Messages();

	/** Message constant for key in the resource bundle. (error when parsing workflowlist type file - no title) */
	public static final String GUI_ERROR_PARSEINSTANCE_TITLE_1 = "GUI_ERROR_PARSEINSTANCE_TITLE_1";

	/** Message constant for key in the resource bundle. (error when parsing workflowlist type file - no valid link for description file) */
	public static final String GUI_ERROR_PARSEINSTANCE_WORKFLOWDESCRIPTIONFILE_1 = "GUI_ERROR_PARSEINSTANCE_WORKFLOWDESCRIPTIONFILE_1";
	
	/** Message constant for key in the resource bundle. (error when parsing workflowlist type file - no valid link for allowance file) */
	public static final String GUI_ERROR_PARSEINSTANCE_WORKFLOWALLOWANCEFILE_1 = "GUI_ERROR_PARSEINSTANCE_WORKFLOWALLOWANCEFILE_1";
	
	/**
	 * Hides the public constructor for this utility class.<p>
	 */
	private Messages() {

		// hide the constructor
	}

	/**
	 * Returns an instance of this localized message accessor.<p>
	 * 
	 * @return an instance of this localized message accessor
	 */
	public static I_CmsMessageBundle get() {

		return INSTANCE;
	}

	/**
	 * Returns the bundle name for this OpenCms package.<p>
	 * 
	 * @return the bundle name for this OpenCms package
	 */
	public String getBundleName() {

		return BUNDLE_NAME;
	}
}