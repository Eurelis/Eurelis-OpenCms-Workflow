/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.toolobjects;

import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class represent the key of the workflow when an object associated to a
 * given workflow is stored in a Map
 * 
 * @author Sébastien Bianco
 */
public class WorkflowKey implements Comparable<WorkflowKey> {

	/**
	 * The key of the workflow
	 * 
	 * @uml.property name="_key"
	 */
	private String _key;

	/**
	 * Create a key from the title of the workflow
	 * 
	 * @param title
	 *            the workflow name
	 * @param encodeRequired
	 *            if the string given as title need to be transform into key
	 */
	public WorkflowKey(String title, boolean encodeRequired) {
		if (encodeRequired) {
			// the key is given by the name of the workflow when removing space
			this._key = StringChecker.removeSpace(title);
		} else {
			this._key = title;
		}
	}

	/**
	 * @return the _key
	 * @uml.property name="_key"
	 */
	public String get_key() {
		return _key;
	}

	/**
	 * @param _key
	 *            the _key to set
	 * @uml.property name="_key"
	 */
	public void set_key(String _key) {
		this._key = _key;
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
		result = prime * result + ((_key == null) ? 0 : _key.hashCode());
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
		final WorkflowKey other = (WorkflowKey) obj;
		if (_key == null) {
			if (other._key != null)
				return false;
		} else if (!_key.equals(other._key))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(WorkflowKey o) {
		return this.get_key().compareTo(o.get_key());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _key;
	}

}
