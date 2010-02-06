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

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollector;

/**
 * This class implements a Factory for object type A_OSWorkflowManagerFactory. This factory maintains only one instance of A_OSWorkflowManagerFactory. <br><br> The class to load is set in the config file /system/modules/com.eurelis.opencms.eurelisworkflow/conf/module_config.txt by OSWORKFLOWMANAGER_CLASSNAME. <br> If this property is not found (or is incorrect) the default collector is used (ie.   {@link AvailableWorkflowCollector}  ).
 * @author   Sébastien Bianco
 */
public class A_OSWorkflowManagerFactory {

	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(A_OSWorkflowManagerFactory.class);

	/**
	 * The current instance of I_AvailableWorkflowCollector
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
	private static A_OSWorkflowManager instance = null;
	
	
	/**
	 * Get an instance of A_OSWorkflowManager. If none instance was initialized, then a new Instance of      {@link OSWorkflowManager}      is created.
	 * @return      the single instance of      {@link A_OSWorkflowManager}
	 * @uml.property  name="instance"
	 */
	public static A_OSWorkflowManager getInstance(){
		if(instance==null){
			String className =  ModuleConfigurationLoader.getConfiguration().OSWORKFLOWMANAGER_CLASSNAME;
			
			if(StringChecker.isNotNullOrEmpty(className)){
				try {
					Class<A_OSWorkflowManager> classObject = (Class<A_OSWorkflowManager>) Class.forName(className);
					instance = classObject.newInstance();
				} catch (Exception e) {
					LOGGER.warn("The class "+className+" cannot be load ("+e.getMessage()+")");
					LOGGER.warn("!!!!!!!!!! BE CAREFUL : Default value used => RIGHT WILL NO BE MANAGED !!!!!!!!");
					instance = new OSWorkflowManager();
				} 
			}else{
				LOGGER.warn("!!!!!!!!!! BE CAREFUL : Default value used => RIGHT WILL NO BE MANAGED !!!!!!!!");
				instance = new OSWorkflowManager();
			}
		}
		return instance;
	}


	/**
	 * Reload the A_OSWorkflow instance
	 */
	public static void reload() {
		LOGGER.debug("WF | Reload A_OSWorkflowManager");
		if(instance != null){
			instance = null;
			instance = getInstance();			
		}
		
	}
}
