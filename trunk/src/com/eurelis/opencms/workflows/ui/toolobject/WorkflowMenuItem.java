/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui.toolobject;

/**
 * This class will store some informations about the Menu Items of workflows. This MenuItem will be displayed into the workflows menu frame (Workflows view) 
 * @author        Sébastien Bianco
 */
public class WorkflowMenuItem {

	/**
	 * The name of the menu Item that will be displayed
	 * @uml.property  name="_name"
	 */
	private String _name;
	
	/**
	 * The Description of the menu item (that will be displayed in the help zone)
	 * @uml.property  name="_helpText"
	 */
	private String _helpText;
	
	/**
	 * The path of the icon file
	 * @uml.property  name="_iconFilePath"
	 */
	private String _iconFilePath;
	
	/**
	 * The link to follows so as to do the action
	 * @uml.property  name="_link"
	 */
	private String _link;
	
	
	/**
	 * The position of the element (order index)
	 * @uml.property  name="_position"
	 */
	private float _position;


	/**
	 * @return        the helpText
	 * @uml.property  name="_helpText"
	 */
	public String get_helpText() {
		return _helpText;
	}


	
	
	/**
	 * @param name
	 * @param helpText
	 * @param iconFilePath
	 * @param link
	 * @param position
	 */
	public WorkflowMenuItem(String name, String helpText, String iconFilePath, String link, float position) {
		super();
		this._name = name;
		this._helpText = helpText;
		this._iconFilePath = iconFilePath;
		this._link = link;
		this._position = position;
	}




	/**
	 * @param helpText        the helpText to set
	 * @uml.property  name="_helpText"
	 */
	public void set_helpText(String helpText) {
		this._helpText = helpText;
	}


	/**
	 * @return        the iconFilePath
	 * @uml.property  name="_iconFilePath"
	 */
	public String get_iconFilePath() {
		return _iconFilePath;
	}


	/**
	 * @param iconFilePath        the iconFilePath to set
	 * @uml.property  name="_iconFilePath"
	 */
	public void set_iconFilePath(String iconFilePath) {
		this._iconFilePath = iconFilePath;
	}


	/**
	 * @return        the link
	 * @uml.property  name="_link"
	 */
	public String get_link() {
		return _link;
	}


	/**
	 * @param link        the link to set
	 * @uml.property  name="_link"
	 */
	public void set_link(String link) {
		this._link = link;
	}


	/**
	 * @return        the name
	 * @uml.property  name="_name"
	 */
	public String get_name() {
		return _name;
	}


	/**
	 * @param name        the name to set
	 * @uml.property  name="_name"
	 */
	public void set_name(String name) {
		this._name = name;
	}


	/**
	 * @return        the position
	 * @uml.property  name="_position"
	 */
	public float get_position() {
		return _position;
	}


	/**
	 * @param position        the position to set
	 * @uml.property  name="_position"
	 */
	public void set_position(float position) {
		this._position = position;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((_helpText == null) ? 0 : _helpText.hashCode());
		result = PRIME * result + ((_iconFilePath == null) ? 0 : _iconFilePath.hashCode());
		result = PRIME * result + ((_link == null) ? 0 : _link.hashCode());
		result = PRIME * result + ((_name == null) ? 0 : _name.hashCode());
		result = PRIME * result + Float.floatToIntBits(_position);
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
		final WorkflowMenuItem other = (WorkflowMenuItem) obj;
		if (_helpText == null) {
			if (other._helpText != null)
				return false;
		} else if (!_helpText.equals(other._helpText))
			return false;
		if (_iconFilePath == null) {
			if (other._iconFilePath != null)
				return false;
		} else if (!_iconFilePath.equals(other._iconFilePath))
			return false;
		if (_link == null) {
			if (other._link != null)
				return false;
		} else if (!_link.equals(other._link))
			return false;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (Float.floatToIntBits(_position) != Float.floatToIntBits(other._position))
			return false;
		return true;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "[WorkflowMenuItem]";
		result += this._name + " - " + this._helpText + " - " +this._link+ " - " +this._position + " - " + this._iconFilePath;
		return result;
	}
	
	
	
	
	
}
