/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows.workflows.availableworkflows;

import java.util.List;
import java.util.Map;

import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This interface describe the list of method required by an object
 * I_AvailableWorkflowCollector. This kind of objects are responsible of
 * collecting the list of file (type "workflowlist") containing a description of
 * workflow available for the system. This collection of workflow doesn't take
 * care of rights about those collected workflows.
 * 
 * @author Sébastien Bianco
 * 
 */
public interface I_AvailableWorkflowCollector {

	/**
	 * get the Map of all available workflows
	 * 
	 * @return the _mapOfAvailableWorkflows
	 */
	public abstract Map<WorkflowKey, DescriptionContainer> get_mapOfAvailableWorkflows();

	/**
	 * Add a new workflow in the list of all available workflows. An error is
	 * mention (warning) in case of inserting a workflows with the same name.
	 * 
	 * @param title
	 *            the name of the workflow
	 * @param description
	 *            the human-readable description of the workflow
	 * @param filePath
	 *            the file containing the workflow description (OSWorkflow
	 *            language)
	 * @param uuid
	 *            the OpenCms UUID of this file
	 * @param rightsFilePath
	 *            the VFS file path of the file containing the rights associated
	 *            to the workflow
	 * @param rightsUuid
	 *            the UUID of the file containing the rights
	 */
	public abstract void addNewWorkflow(String title, String description,
			String filePath, String uuid, String rightsFilePath,
			String rightsUuid);

	/**
	 * Get the list of available workflows
	 * 
	 * @return the list of container that store the description of available
	 *         workflows
	 */
	public abstract List<DescriptionContainer> getListOfAvailableWorkflows();
	
	/**
	 * Get the key in the map of the workflow with the given name
	 * @param title the name of the required workflow
	 * @return the key in the map of this workflow
	 */
	public abstract WorkflowKey getKeyOfWorkflowWithTitle(String title);
	
	/**
	 * Clear the map and create an instance of WorkflowsListLoader.<br/>
	 * This object will get the singleton object and add the collected values.
	 *
	 */
	public abstract void updateContent();

}