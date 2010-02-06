/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

package com.eurelis.opencms.workflows.workflows.toolobjects;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * An element of the historic that will be displayed
 * @author       Sébastien Bianco
 */
public class HistoricElement{
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(HistoricElement.class);
	
	/**
	 * The name of the step
	 * @uml.property  name="_stepName"
	 */
	private String _stepName = "";
	
	/**
	 * The status of the step
	 * @uml.property  name="_status"
	 */
	private String _status = "";
	
	/**
	 * The starting Date
	 * @uml.property  name="_startingDate"
	 */
	private Date _startingDate = new Date();
	
	/**
	 * The ending Date
	 * @uml.property  name="_endingDate"
	 */
	private Date _endingDate = new Date();
	
	/**
	 * The action Date
	 * @uml.property  name="_actionDate"
	 */
	private Date _actionDate = new Date();
	
	/**
	 * The owner
	 * @uml.property  name="_owner"
	 */
	private String _owner = "";
	
	/**
	 * The comment
	 * @uml.property  name="_comment"
	 */
	private String _comment = "";
	
	/**
	 * The last action name
	 * @uml.property  name="_lastActionName"
	 */
	private String _lastActionName = "";
	
	/**
	 * indicate if the historic element corresponds to an automatic action or not
	 * @uml.property  name="_isAutomatic"
	 */
	private boolean _isAutomatic = false;

	/**
	 * @return       the _stepName
	 * @uml.property  name="_stepName"
	 */
	public String get_stepName() {
		return _stepName;
	}

	/**
	 * @param name       the _stepName to set
	 * @uml.property  name="_stepName"
	 */
	public void set_stepName(String name) {
		_stepName = name;
	}

	/**
	 * @return       the _status
	 * @uml.property  name="_status"
	 */
	public String get_status() {
		return _status;
	}

	/**
	 * @param _status       the _status to set
	 * @uml.property  name="_status"
	 */
	public void set_status(String _status) {
		this._status = _status;
	}

	/**
	 * @return       the _startingDate
	 * @uml.property  name="_startingDate"
	 */
	public Date get_startingDate() {
		return _startingDate;
	}

	/**
	 * @param date       the _startingDate to set
	 * @uml.property  name="_startingDate"
	 */
	public void set_startingDate(Date date) {
		_startingDate = date;
	}

	/**
	 * @return       the _endingDate
	 * @uml.property  name="_endingDate"
	 */
	public Date get_endingDate() {
		return _endingDate;
	}

	/**
	 * @param date       the _endingDate to set
	 * @uml.property  name="_endingDate"
	 */
	public void set_endingDate(Date date) {
		_endingDate = date;
	}

	/**
	 * @return       the owner
	 * @uml.property  name="_owner"
	 */
	public String get_owner() {
		return _owner;
	}

	/**
	 * @param owner       the owner to set
	 * @uml.property  name="_owner"
	 */
	public void set_owner(String owner) {
		this._owner = owner;
	}

	/**
	 * @return       the _comment
	 * @uml.property  name="_comment"
	 */
	public String get_comment() {
		return _comment;
	}

	/**
	 * @param _comment       the _comment to set
	 * @uml.property  name="_comment"
	 */
	public void set_comment(String _comment) {
		this._comment = _comment;
	}

	
	/**
	 * @return       the _lastActionName
	 * @uml.property  name="_lastActionName"
	 */
	public String get_lastActionName() {
		return _lastActionName;
	}

	/**
	 * @param actionName       the _lastActionName to set
	 * @uml.property  name="_lastActionName"
	 */
	public void set_lastActionName(String actionName) {
		_lastActionName = actionName;
	}
	
	/**
	 * @return the _isAutomatic
	 */
	public boolean is_isAutomatic() {
		return _isAutomatic;
	}

	/**
	 * @param automatic the _isAutomatic to set
	 */
	public void set_isAutomatic(boolean automatic) {
		_isAutomatic = automatic;
	}

	/**
	 * @return the _actionDate
	 */
	public Date get_actionDate() {
		return _actionDate;
	}

	/**
	 * @param date the _actionDate to set
	 */
	public void set_actionDate(Date date) {
		_actionDate = date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actionDate == null) ? 0 : _actionDate.hashCode());
		result = prime * result
				+ ((_comment == null) ? 0 : _comment.hashCode());
		result = prime * result
				+ ((_endingDate == null) ? 0 : _endingDate.hashCode());
		result = prime * result + (_isAutomatic ? 1231 : 1237);
		result = prime * result
				+ ((_lastActionName == null) ? 0 : _lastActionName.hashCode());
		result = prime * result + ((_owner == null) ? 0 : _owner.hashCode());
		result = prime * result
				+ ((_startingDate == null) ? 0 : _startingDate.hashCode());
		result = prime * result + ((_status == null) ? 0 : _status.hashCode());
		result = prime * result
				+ ((_stepName == null) ? 0 : _stepName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HistoricElement other = (HistoricElement) obj;
		if (_actionDate == null) {
			if (other._actionDate != null)
				return false;
		} else if (!_actionDate.equals(other._actionDate))
			return false;
		if (_comment == null) {
			if (other._comment != null)
				return false;
		} else if (!_comment.equals(other._comment))
			return false;
		if (_endingDate == null) {
			if (other._endingDate != null)
				return false;
		} else if (!_endingDate.equals(other._endingDate))
			return false;
		if (_isAutomatic != other._isAutomatic)
			return false;
		if (_lastActionName == null) {
			if (other._lastActionName != null)
				return false;
		} else if (!_lastActionName.equals(other._lastActionName))
			return false;
		if (_owner == null) {
			if (other._owner != null)
				return false;
		} else if (!_owner.equals(other._owner))
			return false;
		if (_startingDate == null) {
			if (other._startingDate != null)
				return false;
		} else if (!_startingDate.equals(other._startingDate))
			return false;
		if (_status == null) {
			if (other._status != null)
				return false;
		} else if (!_status.equals(other._status))
			return false;
		if (_stepName == null) {
			if (other._stepName != null)
				return false;
		} else if (!_stepName.equals(other._stepName))
			return false;
		return true;
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
				result += myFields[i].getName() + " = "
						+ myFields[i].get(this).toString();
			} catch (IllegalArgumentException e) {
				LOGGER.warn("One of the field of WorkflowDisplayContainer (id="
						+ super.toString() + ") cannot be read ! ("
						+ e.getMessage() + ")");
			} catch (IllegalAccessException e) {
				LOGGER.warn("One of the field of WorkflowDisplayContainer (id="
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
	
}