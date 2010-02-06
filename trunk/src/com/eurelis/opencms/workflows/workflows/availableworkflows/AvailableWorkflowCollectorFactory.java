/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.availableworkflows;

import org.apache.log4j.Logger;

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class implements a Factory for object type I_AvailableWorkflowCollector. This factory maintains only one instance of I_AvailableWorkflowCollector. An object from this class must not be used by any class that is directly use by final User do to unmanagement of rights.  <br><br> The class to load is set in the config file /system/modules/com.eurelis.opencms.eurelisworkflow/conf/module_config.txt by AVAILABLEWORKFLOWCOLLECTOR_CLASSNAME <br> If this property is not found (or is incorrect) the default collector is used (ie.   {@link AvailableWorkflowCollector}  ).
 * @author      Sébastien Bianco
 */
public class AvailableWorkflowCollectorFactory {
	
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(AvailableWorkflowCollectorFactory.class);

	/**
	 * The current instance of I_AvailableWorkflowCollector
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
	private static I_AvailableWorkflowCollector instance = null;
	
	
	/**
	 * Get an instance of I_AvailableWorkflowCollector. If none instance was initialized, then a new Instance of      {@link AvailableWorkflowCollector}      is created.
	 * @return      the single instance of      {@link I_AvailableWorkflowCollector}
	 * @uml.property  name="instance"
	 */
	public static synchronized I_AvailableWorkflowCollector getInstance(){
		if(instance==null){
			String className =  ModuleConfigurationLoader.getConfiguration().AVAILABLEWORKFLOWCOLLECTOR_CLASSNAME;
			
			if(StringChecker.isNotNullOrEmpty(className)){
				try {
					Class<I_AvailableWorkflowCollector> classObject = (Class<I_AvailableWorkflowCollector>) Class.forName(className);
					instance = classObject.newInstance();
				} catch (Exception e) {
					LOGGER.warn("The class "+className+" cannot be load ("+e.getMessage()+")");
					instance = new AvailableWorkflowCollector();
				} 				
			}else{
				instance = new AvailableWorkflowCollector();
			}
		}
		return instance;
	}
	
	/**
	 * This method update the list of available workflows. The process of going through the VFS so as to get the file with type "workflowlist" will be reload.
	 *
	 */
	public static synchronized void updateAvailableWorkflows(){
		AvailableWorkflowCollectorFactory.getInstance().updateContent();
	}
	
	/**
	 * This method reload the AvailableWorkflowConfiguration
	 *
	 */
	public static synchronized void reload(){
		LOGGER.debug("WF | Reload AvaialableWorkflowCollector");
		if(instance != null){
			instance = null;
			instance = getInstance();
			updateAvailableWorkflows();
		}
	}

	
}
