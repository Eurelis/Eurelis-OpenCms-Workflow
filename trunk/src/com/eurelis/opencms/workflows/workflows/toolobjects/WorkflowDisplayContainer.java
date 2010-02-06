/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.toolobjects;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.opencms.i18n.CmsMessageContainer;

import com.eurelis.opencms.workflows.ui.Messages;
import com.eurelis.opencms.workflows.workflows.Parameter;

/**
 * This class store the informations that will be displayed into the GUI
 * @author   Sébastien Bianco
 */
public class WorkflowDisplayContainer {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(WorkflowDisplayContainer.class);

	/**
	 * The ID of the workflow instance
	 * @uml.property  name="_instanceID"
	 */
	private long _instanceID = -1;

	/**
	 * The name of the workflow
	 * @uml.property  name="_workflowName"
	 */
	private String _workflowName = "No name";

	/**
	 * The state of the workflow
	 * @uml.property  name="_workflowState"
	 */
	private String _workflowState = "No state";

	/**
	 * The Creator of the workflow
	 * @uml.property  name="_creator"
	 */
	private String _creator = "No owner";

	/**
	 * The last action name
	 * @uml.property  name="_lastActionName"
	 */
	private String _lastActionName = "No last action name";

	/**
	 * The last action owner
	 * @uml.property  name="_lastActionOwner"
	 */
	private String _lastActionOwner = "No last action owner";

	/**
	 * The last action comment
	 * @uml.property  name="_lastActionComment"
	 */
	private String _lastActionComment = "No last action comment";

	/**
	 * The list of files associated to the workflow
	 * @uml.property  name="_listOfFiles"
	 */
	private List<String> _listOfFiles = new ArrayList<String>();

	/**
	 * The number of files associated to the workflow
	 * @uml.property  name="_numberOfFiles"
	 */
	private int _numberOfFiles = 0;

	/**
	 * The array of following steps
	 * @uml.property  name="_followingSteps"
	 */
	private int[] _followingSteps = new int[] {};

	/**
	 * The array of following steps name
	 * @uml.property  name="_followingStepsNames"
	 */
	private String[] _followingStepsNames = new String[] {};

	/**
	 * Indicate if the workflow is ended or not
	 * @uml.property  name="_isEnded"
	 */
	private boolean _isEnded = true;

	/**
	 * The historic of the workflow
	 * @uml.property  name="_historic"
	 */
	private List<HistoricElement> _historic = new ArrayList<HistoricElement>();

	/**
	 * Indicate that the user doesn't have the right to modify the state of a workflow
	 * @uml.property  name="_isWriteAccessDeny"
	 */
	private boolean _isWriteAccessDeny = false;
	
	/**
	 * The name associated to the instance of workflow
	 * @uml.property  name="_instanceName"
	 */
	private String _instanceName = "No instance name";
	
	/**
	 * The list of initial Parameters of the workflow
	 * @uml.property  name="_parameters"
	 */
	private List<Parameter> _parameters = new ArrayList<Parameter>();
	

	/**
	 * Constructor that initialize a container with value "NO_OWNER" as owner
	 * and an empty list of files
	 * 
	 * @param instanceID
	 * @param workflowName
	 * @param workflowState
	 */
	public WorkflowDisplayContainer(long instanceID, String workflowName,
			String workflowState) {
		this(instanceID, workflowName, workflowState, "NO_OWNER",
				new ArrayList<String>());
	}

	/**
	 * Default constructor (initialize all fields with default values)
	 */
	public WorkflowDisplayContainer() {
	}

	/**
	 * Constructor that initialize a container with an empty list of files
	 * 
	 * @param instanceID
	 * @param workflowName
	 * @param workflowState
	 * @param owner
	 */
	public WorkflowDisplayContainer(long instanceID, String workflowName,
			String workflowState, String owner) {
		this(instanceID, workflowName, workflowState, owner,
				new ArrayList<String>());
	}

	/**
	 * 
	 * @param instanceid
	 * @param name
	 * @param state
	 * @param owner
	 * @param listOfFiles
	 */
	public WorkflowDisplayContainer(long instanceid, String name, String state,
			String owner, List<String> listOfFiles) {
		super();
		this._instanceID = instanceid;
		this._workflowName = name;
		this._workflowState = state;
		this._creator = owner;
		this._listOfFiles = listOfFiles;
		this._numberOfFiles = listOfFiles.size();
	}

	/**
	 * @return   the _isWriteAccessDeny
	 * @uml.property  name="_isWriteAccessDeny"
	 */
	public boolean is_isWriteAccessDeny() {
		return _isWriteAccessDeny;
	}

	/**
	 * @param writeAccessDeny   the _isWriteAccessDeny to set
	 * @uml.property  name="_isWriteAccessDeny"
	 */
	public void set_isWriteAccessDeny(boolean writeAccessDeny) {
		_isWriteAccessDeny = writeAccessDeny;
	}

	/**
	 * @return   the instanceID
	 * @uml.property  name="_instanceID"
	 */
	public long get_instanceID() {
		return _instanceID;
	}

	/**
	 * @param instanceID   the instanceID to set
	 * @uml.property  name="_instanceID"
	 */
	public void set_instanceID(long instanceID) {
		this._instanceID = instanceID;
	}

	/**
	 * @return   the workflowName
	 * @uml.property  name="_workflowName"
	 */
	public String get_workflowName() {
		return _workflowName;
	}

	/**
	 * @param workflowName   the workflowName to set
	 * @uml.property  name="_workflowName"
	 */
	public void set_workflowName(String workflowName) {
		this._workflowName = workflowName;
	}

	/**
	 * @return   the workflowState
	 * @uml.property  name="_workflowState"
	 */
	public String get_workflowState() {
		return _workflowState;
	}

	/**
	 * @param workflowState   the workflowState to set
	 * @uml.property  name="_workflowState"
	 */
	public void set_workflowState(String workflowState) {
		this._workflowState = workflowState;
	}

	/**
	 * @return   the _listOfFiles
	 * @uml.property  name="_listOfFiles"
	 */
	public List<String> get_listOfFiles() {
		return _listOfFiles;
	}

	/**
	 * @param ofFiles   the _listOfFiles to set
	 * @uml.property  name="_listOfFiles"
	 */
	public void set_listOfFiles(List<String> ofFiles) {
		_listOfFiles = ofFiles;
	}

	/**
	 * @return   the _followingSteps
	 * @uml.property  name="_followingSteps"
	 */
	public int[] get_followingSteps() {
		return _followingSteps;
	}

	/**
	 * @param steps   the _followingSteps to set
	 * @uml.property  name="_followingSteps"
	 */
	public void set_followingSteps(int[] steps) {
		_followingSteps = steps;
	}

	/**
	 * @return   the _followingStepsNames
	 * @uml.property  name="_followingStepsNames"
	 */
	public String[] get_followingStepsNames() {
		return _followingStepsNames;
	}

	/**
	 * @param stepsNames   the _followingStepsNames to set
	 * @uml.property  name="_followingStepsNames"
	 */
	public void set_followingStepsNames(String[] stepsNames) {
		_followingStepsNames = stepsNames;
	}

	/**
	 * @return   the _isEnded
	 * @uml.property  name="_isEnded"
	 */
	public boolean is_isEnded() {
		return _isEnded;
	}

	/**
	 * @param ended   the _isEnded to set
	 * @uml.property  name="_isEnded"
	 */
	public void set_isEnded(boolean ended) {
		_isEnded = ended;
	}

	/**
	 * @return   the _creator
	 * @uml.property  name="_creator"
	 */
	public String get_creator() {
		return _creator;
	}

	/**
	 * @param _creator   the _creator to set
	 * @uml.property  name="_creator"
	 */
	public void set_creator(String _creator) {
		this._creator = _creator;
	}

	/**
	 * @return   the _lastActionName
	 * @uml.property  name="_lastActionName"
	 */
	public String get_lastActionName() {
		return _lastActionName;
	}

	/**
	 * @param actionName   the _lastActionName to set
	 * @uml.property  name="_lastActionName"
	 */
	public void set_lastActionName(String actionName) {
		_lastActionName = actionName;
	}

	/**
	 * @return   the _lastActionOwner
	 * @uml.property  name="_lastActionOwner"
	 */
	public String get_lastActionOwner() {
		return _lastActionOwner;
	}

	/**
	 * @param actionOwner   the _lastActionOwner to set
	 * @uml.property  name="_lastActionOwner"
	 */
	public void set_lastActionOwner(String actionOwner) {
		_lastActionOwner = actionOwner;
	}

	/**
	 * @return   the _lastActionComment
	 * @uml.property  name="_lastActionComment"
	 */
	public String get_lastActionComment() {
		return _lastActionComment;
	}

	/**
	 * @param actionComment   the _lastActionComment to set
	 * @uml.property  name="_lastActionComment"
	 */
	public void set_lastActionComment(String actionComment) {
		_lastActionComment = actionComment;
	}

	/**
	 * @return   the _historic
	 * @uml.property  name="_historic"
	 */
	public List<HistoricElement> get_historic() {
		return _historic;
	}

	/**
	 * @return   the _numberOfFiles
	 * @uml.property  name="_numberOfFiles"
	 */
	public int get_numberOfFiles() {
		return _numberOfFiles;
	}

	/**
	 * @param ofFiles   the _numberOfFiles to set
	 * @uml.property  name="_numberOfFiles"
	 */
	public void set_numberOfFiles(int ofFiles) {
		_numberOfFiles = ofFiles;
	}
	

	/**
	 * @return   the _instanceName
	 * @uml.property  name="_instanceName"
	 */
	public String get_instanceName() {
		return _instanceName;
	}

	/**
	 * @param name   the _instanceName to set
	 * @uml.property  name="_instanceName"
	 */
	public void set_instanceName(String name) {
		_instanceName = name;
	}

	/**
	 * @param _historic   the _historic to set
	 * @uml.property  name="_historic"
	 */
	public void set_historic(List<HistoricElement> _historic) {
		this._historic = _historic;
	}
	
	/**
	 * @return the _parameters
	 */
	public List<Parameter> get_parameters() {
		return _parameters;
	}

	/**
	 * @param _parameters the _parameters to set
	 */
	public void set_parameters(List<Parameter> _parameters) {
		this._parameters = _parameters;
	}

	/**
	 * Add a new Element in the historic
	 * 
	 * @param historicElement
	 *            the element to add
	 */
	public void addHistoricalElement(HistoricElement historicElement) {
		this._historic.add(historicElement);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Class thisClass = this.getClass();
		String result = "[";

		// get all the fields of the current class
		Field[] myFields = thisClass.getDeclaredFields();
		for (int i = 0; i < myFields.length; i++) {

			try {
				Object fieldValue = myFields[i].get(this);
				result += myFields[i].getName() + " = "
						+ ((fieldValue==null)?"null":fieldValue.toString());
			} catch (IllegalArgumentException e) {
				LOGGER.info("One of the field of WorkflowDisplayContainer (id="
						+ super.toString() + ") cannot be read ! ("
						+ e.getMessage() + ")");
			} catch (IllegalAccessException e) {
				LOGGER.info("One of the field of WorkflowDisplayContainer (id="
						+ super.toString() + ") cannot be read ! ("
						+ e.getMessage() + ")");
			}

			// if it's no the last element then add a separator
			if (i < myFields.length - 1) {
				result += " ; ";
			}
		}
		return result + "]";
	}

	/**
	 * This method generate a String with the list of available actions that
	 * could be directly printed in a windows
	 * 
	 * @param locale
	 *            the locale used by the user
	 * @return the pretty-print of the available actions
	 */
	public String formatListOfAvailableActions(Locale locale) {
		String result = "";
		if (!_isWriteAccessDeny) {
			for (int i = 0; i < _followingStepsNames.length; i++) {
				result += _followingStepsNames[i];
				// add a separator if it's not the last element
				if (i < _followingStepsNames.length - 1) {
					result += " / ";
				}
			}
		} else {
			//add message if the workflow is not ended
			if(!_isEnded){
				CmsMessageContainer messageContainer = Messages
					.get()
					.container(
							Messages.GUI_WORKFLOWSLIST_CONTENT_AVAILABLEACTION_WRITERIGHTDENY_0);
				return new MessageFormat(messageContainer.key(locale), locale)
					.format(new Object[] { messageContainer.key(locale) });
			}
		}

		return result;
	}

}
