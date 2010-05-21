/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsLog;

import com.eurelis.opencms.workflows.jaxb.workflowconfig.Param;
import com.eurelis.opencms.workflows.jaxb.workflowconfig.Workflow;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfiguration;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.availableworkflows.AvailableWorkflowCollectorFactory;
import com.eurelis.opencms.workflows.workflows.availableworkflows.I_AvailableWorkflowCollector;
import com.eurelis.opencms.workflows.workflows.toolobjects.DescriptionContainer;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;

/**
 * This object take care of the configuration of workflows (the initial
 * parameters). It store the rights informations relative to a workflow. This
 * object is a singleton, so Use {@link WorkflowConfigManager#getInstance()} to
 * access it.
 * 
 * @author Sébastien Bianco
 */
public class WorkflowConfigManager {

	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(WorkflowConfigManager.class);

	/**
	 * The map of parameters associated to each workflows
	 */
	private Map<WorkflowKey, List<Parameter>> _mapsOfParameterPerWorkflows = new HashMap<WorkflowKey, List<Parameter>>();

	/**
	 * The single instance of the class
	 * 
	 * @uml.property name="instance"
	 * @uml.associationEnd
	 */
	private static WorkflowConfigManager instance = null;

	/**
	 * private default constructor to deny access to constructor
	 */
	private WorkflowConfigManager() {
		// initialize the manager
		this.initialize();
	}

	/**
	 * Get the single instance of {@link WorkflowConfigManager} . If the
	 * instance is null, this method initialize it.
	 * 
	 * @return the singleton
	 * @uml.property name="instance"
	 */
	public static WorkflowConfigManager getInstance() {
		if (instance == null) {
			instance = new WorkflowConfigManager();
		}
		return instance;
	}

	/**
	 * This method initialize the Workflow Right Manager. It loads the list of
	 * available workflows and parse the associated files so as to obtain the
	 * associated rights.
	 */
	private void initialize() {

		// The workflow Collector so as to get the list of workflows to manage
		I_AvailableWorkflowCollector workflowCollector = AvailableWorkflowCollectorFactory
				.getInstance();

		// get map of associated workflows
		Map<WorkflowKey, DescriptionContainer> listOfAvailablesWorkflows = workflowCollector
				.get_mapOfAvailableWorkflows();

		/*
		 * for each workflows, get the right file path and parse the file to
		 * collect the informations
		 */
		Iterator<Entry<WorkflowKey, DescriptionContainer>> listOfAvailablesWorkflowsIterator = listOfAvailablesWorkflows
				.entrySet().iterator();
		while (listOfAvailablesWorkflowsIterator.hasNext()) {
			Entry<WorkflowKey, DescriptionContainer> entry = listOfAvailablesWorkflowsIterator
					.next();
			// get right file path
			String filePath = entry.getValue().get_rightsFilePath();
			// parse right file and store result
			try {
				_mapsOfParameterPerWorkflows
						.put(
								entry.getKey(),
								this
										.extractWorkflowParameterFromCongifAndRightFile(filePath));
			} catch (JAXBException e) {
				LOGGER.warn("An error occurs during the parsing of " + filePath
						+ " (" + e.getMessage() + ")");
			}

		}
	}

	/**
	 * This method parse the file with the given filepath and extract the list
	 * of parameters of the workflow
	 * 
	 * @param filePath
	 *            the path of the file containing the configuration and rights
	 *            associated to a workflow
	 * @return the list of parameters, an <i>empty list</i> if there is no
	 *         parameters.
	 * @throws JAXBException
	 *             if a problem occurs during parsing
	 */
	private List<Parameter> extractWorkflowParameterFromCongifAndRightFile(
			String filePath) throws JAXBException {

		// read the resource
		CmsResource resource = OpenCmsEasyAccess.getResource(filePath);

		if (resource != null) {
			if (resource.isFile()) {
				// Read the file
				byte[] fileContent = OpenCmsEasyAccess.readFile(resource);

				if (fileContent.length > 0) {

					// Load JAXB Context and unmarshaller
					JAXBContext jc = JAXBContext
							.newInstance(ModuleConfigurationLoader
									.getConfiguration().PARSER_JAXBCONTEXT_WORKFLOWCONFIG);
					Unmarshaller unmarshaller = jc.createUnmarshaller();

					try {
						// Unmarshall file and get the list of instances
						List<Param> parameters = ((Workflow) unmarshaller
								.unmarshal(new ByteArrayInputStream(fileContent)))
								.getParams().getParam();

						/*
						 * Collect the list of parameter
						 */
						List<Parameter> listOfParameters = new ArrayList<Parameter>();
						for (int i = 0; i < parameters.size(); i++) {
							Param param = parameters.get(i);
							// extract basic fields of Parameter
							Parameter newParam = new Parameter(param.getName(),
									param.getType(), param.getDisplay());
							// Extract the list of default value
							newParam
									.set_defaultValues(this
											.extractDefaultValueList(param
													.getDefault()));
							listOfParameters.add(newParam);
						}

						return listOfParameters;
					} catch (NullPointerException e) {
						LOGGER
								.warn("An error occurs during parsing of "
										+ filePath
										+ " to get the list of parameters. It will be no parameters set for the associated workflows");
					}
				}
			} else {
				LOGGER.info("The resource " + filePath + " is not a File");
			}
		} else {
			LOGGER.info("The resource with path " + filePath
					+ " has not been found");
		}
		return new ArrayList<Parameter>();
	}

	/**
	 * Extract the list of default value from the String stored in the config
	 * file. The main values are separated with the String stored in
	 * {@link ModuleConfiguration#WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR}.
	 * 
	 * @param defaultString
	 *            the list of default value as read in the config file
	 * @return the list of default value, an empty list if there is no default
	 *         values.
	 */
	private List<String> extractDefaultValueList(String defaultString) {
		return StringChecker.splitListOfValueWithModuleSeparator(defaultString);
	}

	/**
	 * The list of parameters of a workflow
	 * 
	 * @param workflowKey
	 *            The name of the workflow
	 * @return the List of parameters corresponding to this workflow, <i>Empty
	 *         list</i> if the workflow has no parameters
	 */
	public List<Parameter> getListOfParameters(WorkflowKey workflowKey) {
		if (_mapsOfParameterPerWorkflows.containsKey(workflowKey)) {
			return _mapsOfParameterPerWorkflows.get(workflowKey);
		} else
			return new ArrayList<Parameter>();
	}
	
	/**
	 * The A HTML representation of the list of parameters
	 * 
	 * @param workflowKey
	 *            The name of the workflow
	 * @return a HTML String of the list of parameters <i>Empty
	 *         String</i> if the workflow has no parameters
	 */
	public String getHTMLOfListOfParameters(WorkflowKey workflowKey) {
		List<Parameter> listOfParameters  = this.getListOfParameters(workflowKey);
		String result = "<ul>";
			for(int i =0; i<listOfParameters.size();i++){
				result+="<li>"+listOfParameters.get(i).getHTML()+"</li>";
			}
			result+="</ul>";
			return result;
	}

	/**
	 * Reload the content of the config files
	 */
	public static void reload() {
		if (instance != null)
			instance = new WorkflowConfigManager();
		
		LOGGER.debug(ErrorFormatter.formatMap(instance._mapsOfParameterPerWorkflows, "WF | reloaded configuration = \n"));
	}
}
