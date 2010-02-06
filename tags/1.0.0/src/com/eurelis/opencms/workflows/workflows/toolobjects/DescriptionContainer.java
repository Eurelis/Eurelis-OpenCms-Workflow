/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.toolobjects;

/**
 * This class store the extracted information of the workflowlist files
 * @author        Sébastien Bianco
 */
public class DescriptionContainer {

	/**
	 * The _title of the workflow
	 * @uml.property  name="_title"
	 */
	private String _title = null;
	
	/**
	 * The _description of the file
	 * @uml.property  name="_description"
	 */
	private String _description = null;
	
	/**
	 * The VFS File Path of the workflow _description file
	 * @uml.property  name="_workflowFilePath"
	 */
	private String _workflowFilePath = null;
	
	/**
	 * The CmsUuid of the resource (Workflow _description path)
	 * @uml.property  name="_workflowFileUuid"
	 */
	private String _workflowFileUuid = null;
	
	/**
	 * The VFS File Path of the rights _description file
	 * @uml.property  name="_rightsFilePath"
	 */
	private String _rightsFilePath = null;
	
	/**
	 * The CmsUuid of the resource (rights file path)
	 * @uml.property  name="_rightsFileUuid"
	 */
	private String _rightsFileUuid = null;

	/**
	 * The path in RFS where is stored a copy of the workflow description
	 * @uml.property  name="_realFilePathOfDescriptionFile"
	 */
	private String _realFilePathOfDescriptionFile = null;

	/**
	 * Construct a new Description container object
	 * @param title the title of the workflow 
	 * @param description the description of the workflow
	 * @param workflowFilePath the VFS file path of the workflow description file
	 * @param workflowFileUuid the corresponding uuid
	 * @param rightsFilePath the VFS file path of the associated rights of the workflow
	 * @param rightsFileUuid the corresponding uuid
	 */
	public DescriptionContainer(String title, String description, String workflowFilePath, String workflowFileUuid, String rightsFilePath, String rightsFileUuid) {
		super();
		this._title = title;
		this._description = description;
		this._workflowFilePath = workflowFilePath;
		this._workflowFileUuid = workflowFileUuid;
		this._rightsFilePath = rightsFilePath;
		this._rightsFileUuid = rightsFileUuid;
	}



	/**
	 * @return        the description
	 * @uml.property  name="_description"
	 */
	public String get_description() {
		return _description;
	}



	/**
	 * @param description        the description to set
	 * @uml.property  name="_description"
	 */
	public void set_description(String description) {
		this._description = description;
	}



	/**
	 * @return        the rightsFilePath
	 * @uml.property  name="_rightsFilePath"
	 */
	public String get_rightsFilePath() {
		return _rightsFilePath;
	}



	/**
	 * @param rightsFilePath        the rightsFilePath to set
	 * @uml.property  name="_rightsFilePath"
	 */
	public void set_rightsFilePath(String rightsFilePath) {
		this._rightsFilePath = rightsFilePath;
	}



	/**
	 * @return        the rightsFileUuid
	 * @uml.property  name="_rightsFileUuid"
	 */
	public String get_rightsFileUuid() {
		return _rightsFileUuid;
	}



	/**
	 * @param rightsFileUuid        the rightsFileUuid to set
	 * @uml.property  name="_rightsFileUuid"
	 */
	public void set_rightsFileUuid(String rightsFileUuid) {
		this._rightsFileUuid = rightsFileUuid;
	}



	/**
	 * @return        the title
	 * @uml.property  name="_title"
	 */
	public String get_title() {
		return _title;
	}



	/**
	 * @param title        the title to set
	 * @uml.property  name="_title"
	 */
	public void set_title(String title) {
		this._title = title;
	}



	/**
	 * @return        the _workflowFilePath
	 * @uml.property  name="_workflowFilePath"
	 */
	public String get_workflowFilePath() {
		return _workflowFilePath;
	}



	/**
	 * @param workflowFilePath        the workflowFilePath to set
	 * @uml.property  name="_workflowFilePath"
	 */
	public void set_workflowFilePath(String workflowFilePath) {
		this._workflowFilePath = workflowFilePath;
	}



	/**
	 * @return        the workflowFileUuid
	 * @uml.property  name="_workflowFileUuid"
	 */
	public String get_workflowFileUuid() {
		return _workflowFileUuid;
	}



	/**
	 * @param workflowFileUuid        the workflowFileUuid to set
	 * @uml.property  name="_workflowFileUuid"
	 */
	public void set_workflowFileUuid(String workflowFileUuid) {
		this._workflowFileUuid = workflowFileUuid;
	}



	


	/**
	 * @return        the _realFilePathOfDescriptionFile
	 * @uml.property  name="_realFilePathOfDescriptionFile"
	 */
	public String get_realFilePathOfDescriptionFile() {
		return _realFilePathOfDescriptionFile;
	}



	/**
	 * @param filePathOfDescriptionFile        the _realFilePathOfDescriptionFile to set
	 * @uml.property  name="_realFilePathOfDescriptionFile"
	 */
	public void set_realFilePathOfDescriptionFile(String filePathOfDescriptionFile) {
		_realFilePathOfDescriptionFile = filePathOfDescriptionFile;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((_description == null) ? 0 : _description.hashCode());
		result = PRIME * result + ((_realFilePathOfDescriptionFile == null) ? 0 : _realFilePathOfDescriptionFile.hashCode());
		result = PRIME * result + ((_rightsFilePath == null) ? 0 : _rightsFilePath.hashCode());
		result = PRIME * result + ((_rightsFileUuid == null) ? 0 : _rightsFileUuid.hashCode());
		result = PRIME * result + ((_title == null) ? 0 : _title.hashCode());
		result = PRIME * result + ((_workflowFilePath == null) ? 0 : _workflowFilePath.hashCode());
		result = PRIME * result + ((_workflowFileUuid == null) ? 0 : _workflowFileUuid.hashCode());
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
		final DescriptionContainer other = (DescriptionContainer) obj;
		if (_description == null) {
			if (other._description != null)
				return false;
		} else if (!_description.equals(other._description))
			return false;
		if (_realFilePathOfDescriptionFile == null) {
			if (other._realFilePathOfDescriptionFile != null)
				return false;
		} else if (!_realFilePathOfDescriptionFile.equals(other._realFilePathOfDescriptionFile))
			return false;
		if (_rightsFilePath == null) {
			if (other._rightsFilePath != null)
				return false;
		} else if (!_rightsFilePath.equals(other._rightsFilePath))
			return false;
		if (_rightsFileUuid == null) {
			if (other._rightsFileUuid != null)
				return false;
		} else if (!_rightsFileUuid.equals(other._rightsFileUuid))
			return false;
		if (_title == null) {
			if (other._title != null)
				return false;
		} else if (!_title.equals(other._title))
			return false;
		if (_workflowFilePath == null) {
			if (other._workflowFilePath != null)
				return false;
		} else if (!_workflowFilePath.equals(other._workflowFilePath))
			return false;
		if (_workflowFileUuid == null) {
			if (other._workflowFileUuid != null)
				return false;
		} else if (!_workflowFileUuid.equals(other._workflowFileUuid))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result  = "[DescriptionContainer] - tt:";
		result+= _title +" - desc:";
		result+= _description+ " - dfp:";
		result+= _workflowFilePath+" - uuid:";
		result+= _workflowFileUuid+ " - rfp:";
		result+= _rightsFilePath + " - uuid:";
		result+= _rightsFileUuid + " - RFS:";
		result+= _realFilePathOfDescriptionFile;
		return result;
	}
	
	
	
	
}

