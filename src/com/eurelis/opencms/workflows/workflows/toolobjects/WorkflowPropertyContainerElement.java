/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.toolobjects;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.opencms.main.CmsLog;

import com.eurelis.opencms.workflows.util.ErrorFormatter;

/**
 * This class will store the association of the owner and the comment associated to each step of the workflow
 * @author   Sébastien Bianco
 */
public class WorkflowPropertyContainerElement implements Serializable {
	
	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(WorkflowPropertyContainerElement.class);

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The user UUID that make the workflow evolved
	 * @uml.property  name="_cmsUserUUID"
	 */
	private String _cmsUserUUID = null;

	/**
	 * The comment associated to each step of the workflow progression
	 * @uml.property  name="_comment"
	 */
	private String _comment = null;

	/**
	 * The name of the last action done
	 * @uml.property  name="_actionName"
	 */
	private String _actionName = null;

	/**
	 * Indicate if the action is an auto action or not
	 * @uml.property  name="_isAutoExecuted"
	 */
	private boolean _isAutoExecuted = false;
	
	/**
	 * The date when the action has been done (the milliseconds since January 1, 1970, 00:00:00 GMT.)
	 */
	private long _actionDate = 0;

	/**
	 * Create a new instance 
	 * @param userUUID the uuid/name of the User
	 * @param comment the comment set by the user
	 * @param name the name of the action done
	 * @param isAutoExecuted indicate if the action is an autoaction or not
	 * @param actionDate the date of the action (the milliseconds since January 1, 1970, 00:00:00 GMT.)
	 */
	public WorkflowPropertyContainerElement(String userUUID, String comment,
			String name, boolean isAutoExecuted, long actionDate) {
		super();
		_cmsUserUUID = userUUID;
		_comment = comment;
		_actionName = name;
		_actionDate = actionDate;
		_isAutoExecuted = isAutoExecuted;
	}

	/**
	 * @return   the _cmsUserUUID
	 * @uml.property  name="_cmsUserUUID"
	 */
	public String get_cmsUserUUID() {
		return _cmsUserUUID;
	}

	/**
	 * @param userUUID   the _cmsUserUUID to set
	 * @uml.property  name="_cmsUserUUID"
	 */
	public void set_cmsUserUUID(String userUUID) {
		_cmsUserUUID = userUUID;
	}

	/**
	 * @return   the _comment
	 * @uml.property  name="_comment"
	 */
	public String get_comment() {
		return _comment;
	}

	/**
	 * @param _comment   the _comment to set
	 * @uml.property  name="_comment"
	 */
	public void set_comment(String _comment) {
		this._comment = _comment;
	}

	/**
	 * @return   the _actionName
	 * @uml.property  name="_actionName"
	 */
	public String get_actionName() {
		return _actionName;
	}

	/**
	 * @param name   the _actionName to set
	 * @uml.property  name="_actionName"
	 */
	public void set_actionName(String name) {
		_actionName = name;
	}

	/**
	 * @return   the _isAutoExecuted
	 * @uml.property  name="_isAutoExecuted"
	 */
	public boolean is_isAutoExecuted() {
		return _isAutoExecuted;
	}

	/**
	 * @param autoExecuted   the _isAutoExecuted to set
	 * @uml.property  name="_isAutoExecuted"
	 */
	public void set_isAutoExecuted(boolean autoExecuted) {
		_isAutoExecuted = autoExecuted;
	}
	

	/**
	 * @return the _actionDate
	 */
	public long get_actionDate() {
		return _actionDate;
	}

	/**
	 * @param date the _actionDate to set
	 */
	public void set_actionDate(long date) {
		_actionDate = date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_actionName == null) ? 0 : _actionName.hashCode());
		result = prime * result
				+ ((_cmsUserUUID == null) ? 0 : _cmsUserUUID.hashCode());
		result = prime * result
				+ ((_comment == null) ? 0 : _comment.hashCode());
		result = prime * result + (_isAutoExecuted ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		final WorkflowPropertyContainerElement other = (WorkflowPropertyContainerElement) obj;
		if (_actionName == null) {
			if (other._actionName != null)
				return false;
		} else if (!_actionName.equals(other._actionName))
			return false;
		if (_cmsUserUUID == null) {
			if (other._cmsUserUUID != null)
				return false;
		} else if (!_cmsUserUUID.equals(other._cmsUserUUID))
			return false;
		if (_comment == null) {
			if (other._comment != null)
				return false;
		} else if (!_comment.equals(other._comment))
			return false;
		if (_isAutoExecuted != other._isAutoExecuted)
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

		return "[" + this._cmsUserUUID + ";" + this._comment + ";"
				+ this._actionName + ";" + this._isAutoExecuted + "]";
	}

	/**
	 * Generate a XML Representation of the given object.
	 * 
	 * @return the xml corresponding to this class
	 */
	public String convertInXML() {
		StringBuffer xmlString = new StringBuffer(1024);

		// get the class
		Class<WorkflowPropertyContainerElement> thisClass = (Class<WorkflowPropertyContainerElement>) this.getClass();

		// get the fields
		Field[] fields = thisClass.getDeclaredFields();

		// open tag with name of the class
		xmlString.append("<" + this.getClass().getSimpleName());
		xmlString.append(" ");

		/*
		 * Add all fields in a XML (this class has only simple tag, so use
		 * only attribute)
		 */
		for (int i = 0; i < fields.length; i++) {

			// get modifier
			int modifierValue = fields[i].getModifiers();

			// don't write static variable
			if (!Modifier.isStatic(modifierValue)) {

				// get field name
				String fieldName = fields[i].getName();

				// don't write if name startWith "this"
				if (!fieldName.startsWith("this")) {

					// remove "_" before variable if required
					if (fieldName.startsWith("_")) {
						xmlString.append(fieldName.substring(1));
					} else {
						xmlString.append(fieldName);
					}
					xmlString.append("=\"");
					try {
						xmlString.append(fields[i].get(this).toString());
					} catch (IllegalArgumentException e) {
						LOGGER.info(ErrorFormatter.formatException(e));
					} catch (IllegalAccessException e) {
						LOGGER.info(ErrorFormatter.formatException(e));
					}
					xmlString.append("\" ");
				}
			}
		}

		// close tag
		xmlString.append("/>");
		return xmlString.toString();
	}

}
