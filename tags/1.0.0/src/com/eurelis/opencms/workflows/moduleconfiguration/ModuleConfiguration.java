/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.moduleconfiguration;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

/**
 * This class contains some variable that are loaded when starting the module.<br>
 * 
 * This class is initialize by the module configuration loader.
 * @author Sébastien Bianco
 *
 */
public class ModuleConfiguration {
	
	/**
	 * Default constructor
	 */
	/* package */ModuleConfiguration() {}
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(ModuleConfiguration.class);

	/**
	 * The context of the jaxb parser for file of type workflowlist
	 */
	public String PARSER_JAXBCONTEXT_LISTWORKFLOW = "com.eurelis.opencms.workflows.jaxb.listworkflow";
		
	/**
	 * The context of the jaxb parser for file storing the configuration/rights associated to a workflow 
	 */
	public String PARSER_JAXBCONTEXT_WORKFLOWCONFIG = "com.eurelis.opencms.workflows.jaxb.workflowconfig";
	
	/**
	 * The context of the jaxb parser for file storing the information associated to an instance of workflow (stored in the db)
	 */
	public String PARSER_JAXBCONTEXT_WORKFLOWPROPERTYCONTAINER = "com.eurelis.opencms.workflows.jaxb.workflowpropertycontainer";
	
	
	/**
	 * The name of the type of file where information about available workflows are describe
	 */
	public String WORKLFOWLIST_TYPE = "workflowlist";
	
	/**
	 * The string that starts the description of a Pattern
	 */
	public String PATTERN_STARTING_SUBSTRING = "{";
	
	/**
	 * The string that ends the description of a Pattern
	 */
	public String PATTERN_ENDING_SUBSTRING = "}";
	
	/**
	 * The separator use in the workflow right config file to separate the list of default values.
	 */
	public String WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR = "|";
	
	/**
	 * The name of the class to load as Available Workflow Collector
	 */
	public String AVAILABLEWORKFLOWCOLLECTOR_CLASSNAME = "com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollector";

	/**
	 * The name of the class to load as OSWorkflowManager
	 */
	public String OSWORKFLOWMANAGER_CLASSNAME = "com.eurelis.opencms.workflows.workflows.OSWorkflowManager";
	
	/**
	 * The path where the search of "workflowlist" files will starts from
	 */
	public String WORKFLOWLIST_CONFIGFILE_FOLDER = "/system/modules/com.eurelis.opencms.workflows/workflows/";
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Class thisClass = this.getClass();
		String result = "[";
		
		//get all the fields of the current class
		Field[] myFields = thisClass.getDeclaredFields();
		for(int i=0;i<myFields.length;i++){
			
			try {
				result+=myFields[i].getName()+" = "+myFields[i].get(this).toString();				
			} catch (IllegalArgumentException e) {
				LOGGER.info("One of the field of WorkflowDisplayContainer (id="+super.toString()+") cannot be read ! ("+e.getMessage()+")");
			} catch (IllegalAccessException e) {
				LOGGER.info("One of the field of WorkflowDisplayContainer (id="+super.toString()+") cannot be read ! ("+e.getMessage()+")");
			}
			
			//if it's no the last element then add a separator
			if(i<myFields.length-1){
				result+= " ; "; 
			}
		}
		return result + "]";
	}
}
