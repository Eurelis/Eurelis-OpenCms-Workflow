/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.availableworkflows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This class  gather all the description of workflows that have been found in the VFS. <br/> It uses the class      {@link WorkflowListLoader}      to load the map containing the available workflows.
 * @author          Sébastien Bianco
 */
public class AvailableWorkflowCollector implements I_AvailableWorkflowCollector {
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(AvailableWorkflowCollector.class);

	
	/**
	 * The map the will store all the available workflows
	 * @uml.property  name="_mapOfAvailableWorkflows"
	 */
	private Map<WorkflowKey,DescriptionContainer> _mapOfAvailableWorkflows = null;
	
	
	/**
	 * Default constructor
	 *
	 */
	/* package */ AvailableWorkflowCollector(){
		//initialise the map
		this._mapOfAvailableWorkflows =  new HashMap<WorkflowKey, DescriptionContainer>();	
		
	}
		
	/**
	 * Clear the map and create an instance of WorkflowsListLoader.<br/>
	 * This object will get the singleton object and add the collected values.
	 *
	 */
	public void updateContent() {		
		this._mapOfAvailableWorkflows.clear();
		new WorkflowsListLoader();	
		LOGGER.debug(ErrorFormatter.formatMap(_mapOfAvailableWorkflows, "WF | Map of available workflows"));
	}

	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.I_AvailableWorkflowCollector#get_mapOfAvailableWorkflows()
	 */
	/**
	 * @return
	 * @uml.property  name="_mapOfAvailableWorkflows"
	 */
	public Map<WorkflowKey, DescriptionContainer> get_mapOfAvailableWorkflows() {
		return _mapOfAvailableWorkflows;
	}
	
	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.I_AvailableWorkflowCollector#addNewWorkflow(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addNewWorkflow(String title, String description, String filePath, String uuid, String rightsFilePath, String rightsUuid){
		String key = StringChecker.removeSpace(title);
		if(!this.get_mapOfAvailableWorkflows().containsKey(key)){
			this.get_mapOfAvailableWorkflows().put(new WorkflowKey(key,true),new DescriptionContainer(title,description,filePath,uuid,rightsFilePath,rightsUuid));
		}else{
			LOGGER.warn("WF | a workflow with name "+title+" already exists. The current one has not been added.");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.I_AvailableWorkflowCollector#getListOfAvailableWorkflows()
	 */
	public List<DescriptionContainer> getListOfAvailableWorkflows(){
		return new ArrayList<DescriptionContainer>(this.get_mapOfAvailableWorkflows().values());
	}

	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.workflows.I_AvailableWorkflowCollector#getKeyOfWorkflowWithTitle(java.lang.String)
	 */
	public WorkflowKey getKeyOfWorkflowWithTitle(String title) {	
		return new WorkflowKey(title,true);
	}
	
	
	
	
}
