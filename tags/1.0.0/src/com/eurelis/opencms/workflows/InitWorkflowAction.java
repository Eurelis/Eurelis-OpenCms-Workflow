/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows;

import org.apache.commons.logging.Log;
import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsLog;
import org.opencms.module.A_CmsModuleAction;
import org.opencms.module.CmsModule;
import org.opencms.module.I_CmsModuleAction;

import com.eurelis.opencms.workflows.util.CmsUtil;


/**
 * This class is load by the CmsModuleManager when the Web-app starts. This class set the CmsObject with administrator rights in the CmsUtil class.
 * @author Sébastien Bianco
 *
 */
public class InitWorkflowAction extends A_CmsModuleAction implements I_CmsModuleAction{

	/** The log object for this class. */
	private static final Log LOG = CmsLog.getLog(InitWorkflowAction.class);
 		
    /**
     * @see org.opencms.module.I_CmsModuleAction#initialize(org.opencms.file.CmsObject, CmsConfigurationManager, CmsModule)
     */
    @Override
	public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module) {
    	super.initialize(adminCms,configurationManager,module);    	
    	
    	LOG.debug("WF | enter init Object");
    	
    	//store the adminCms object 
    	CmsUtil.setAdminCmsObject(adminCms);                
       
    	
        
    }
    

        
}
