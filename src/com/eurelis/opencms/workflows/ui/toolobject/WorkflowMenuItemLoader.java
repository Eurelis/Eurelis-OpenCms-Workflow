/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui.toolobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRole;

import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class load the content of the define folder (/system/workplace/views/workflows) so as to load the different file that could be a file that define a new MenuItem description. In that case, it will look at the properties of the file and collect them.<br/> This class follows the design pattern Singleton, so you will get an instance of this class with function <b>WorkflowMenuItemLoader.getInstance()</b>
 * @author      Sébastien Bianco
 */
public class WorkflowMenuItemLoader {

	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(WorkflowMenuItemLoader.class);

	/**
	 * The path of the view "worflows"
	 */
	private static final String WOOKFLOWS_VIEW_PATH = "/system/workplace/views/workflows/";

	/**
	 * The name of the last user that required an instance of menu
	 */
	private static String lastUserName = null; 
	
	/**
	 * The name of the required property isAView
	 */
	private static final String PROPERTYNAME_ISAVIEW = "IsAView";

	/**
	 * The name of the required property NavImage
	 */
	private static final String PROPERTYNAME_NAVIMAGE = "NavImage";

	/**
	 * The name of the required property Title
	 */
	private static final String PROPERTYNAME_TITLE = "Title";

	/**
	 * The name of the required property HELPTEXT
	 */
	private static final String PROPERTYNAME_HELPTEXT = "NavText";

	/**
	 * The name of the required property position
	 */
	private static final String PROPERTYNAME_POSITION = "Position";
	
	/**
	 * The name of the optional property 
	 */
	private static final String PROPERTYNAME_RESTRICTEDROLES = "RestrictedRoles";

	/**
	 * The single instance of WorkflowMenuItemLoader
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
	private static WorkflowMenuItemLoader instance = null;

	/**
	 * The map where will be stored the WorkflowMenuItem objects
	 * @uml.property  name="_mapOfItems"
	 */
	public Map<String, WorkflowMenuItem> _mapOfItems = null;

	/**
	 * The list of found position
	 */
	private List<Float> _foundPosition = new ArrayList<Float>();

	/**
	 * Set a new default position in case of an already found position
	 */
	private float _lastPositionSet = 10000;

	/**
	 * The default constructor set as private so as to forbid other class to
	 * create instance of this class
	 * 
	 * @param cmsObject
	 *            the object that contains the request context
	 */
	private WorkflowMenuItemLoader(CmsObject cmsObject) {
		_mapOfItems = new HashMap<String, WorkflowMenuItem>();
		this.fillTheMap(cmsObject);
	}

	/**
	 * This method look at the repository /system/workplace/views/workflows so
	 * as to load all the JSP file. It will look at the properties of the file
	 * so as to update the map of menuItem that can be displayed.
	 * 
	 * @param cmsObject
	 *            the object that contains the request context
	 * 
	 */
	private void fillTheMap(CmsObject cmsObject) {
				

		// Get the list of files in the view repository
		List<CmsResource> listOfFiles = OpenCmsEasyAccess.getListOfFile(
				WOOKFLOWS_VIEW_PATH);

	
		/*
		 * Treat each resource 
		 *  (check required properties and fill the map of available menu)
		 */
		Iterator<CmsResource> listOfFilesIterator = listOfFiles.iterator();
		while (listOfFilesIterator.hasNext()) {
			CmsResource resource = listOfFilesIterator.next();

			// check that it is a view
			if (this.checkIsAView(resource)) {

				WorkflowMenuItem menuItem = this.collectProperties(resource,cmsObject);
				if (menuItem != null) {
					_mapOfItems.put(menuItem.get_name()+"_"+menuItem.get_position(), menuItem);
				} else {
					LOGGER
							.warn("The view associated to "
									+ resource.getRootPath()
									+ " has not been loaded (error during collect of properties).");
				}
			}
		}
	}

	/**
	 * Take the resource, check if this one has the property "IsAView". If it is
	 * the case, then collect the other properties and put them in the map of
	 * workflow menu items
	 * 
	 * @param resource
	 *            the resource to treat	
	 * @return <i>true</i> if the resource is a view, <i>false</i> otherwise
	 *         (see log for exceptions);
	 */
	private boolean checkIsAView(CmsResource resource) {
		// check if the resource is a File
		if (resource != null && resource.isFile()) {

			// try to get the property isAView
			String isAViewpropertyValue = this.treatProperty(
					PROPERTYNAME_ISAVIEW, resource);
			
			
			if (StringChecker.isNotNullOrEmpty(isAViewpropertyValue)) {
				try {
					return Boolean.parseBoolean(isAViewpropertyValue);
				} catch (Exception e) {
					LOGGER.info("The property " + PROPERTYNAME_ISAVIEW
							+ " of the file " + resource.getRootPath()
							+ " is probably not a valid boolean ("
							+ e.getMessage() + ")");
					return false;
				}
			}// empty value
		}// resource null
		return false;
	}

	/**
	 * Collect all the informations relative to a view and create the
	 * WorkflowMenuItem associated
	 * 
	 * @param resource
	 *            the resource to treat
	 
	 * @param cmsObject
	 *            the cmsObject of the opened dialog
	 * @return a {@link WorkflowMenuItem} object, <b>null</b> if a error occurs
	 *         (see logs for details) or if the user doesn't have right to access the menu item
	 */
	private WorkflowMenuItem collectProperties(CmsResource resource,
			 CmsObject cmsObject) {

		// Get NavImage
		String navImage = this.treatProperty(PROPERTYNAME_NAVIMAGE, resource);

		// get Link
		//required link as "/opencms/opencms/workplace/workflows-main.jsp?workflowpath=/system/workplace/views/workflows/myPage.jsp"
		String link = OpenCms.getSystemInfo().getOpenCmsContext()+UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION+"?"+UISharedVariables.PARAM_WORKFLOWPATH+"="+CmsEncoder.encode(resource.getRootPath());

		// get HelpText
		String helpText = this.treatProperty(PROPERTYNAME_HELPTEXT, resource);

		// get Title
		String title = this.treatProperty(PROPERTYNAME_TITLE, resource);

		// get and check position
		float position = this.checkAnUpdatePosition(this.treatProperty(
				PROPERTYNAME_POSITION, resource));
		
		//Get the list of role that are allowed to access the menu item
		List<String> restrictedRoles = this.collectListOfRestictedRoles(this.treatProperty(PROPERTYNAME_RESTRICTEDROLES, resource));
		
		if(restrictedRoles==null){
			return new WorkflowMenuItem(title, helpText, navImage, link, position);
		}else{
			
			//get the list of roles of the current user
			List<CmsRole> listOfRole = OpenCmsEasyAccess.getRolesOfUser(cmsObject.getRequestContext().currentUser().getName());
			
			//get the current locale
			Locale locale = cmsObject.getRequestContext().getLocale();
	
			/*
			 * Try to get a match
			 */
			for(int restrictedRoleIndex = 0; restrictedRoleIndex<restrictedRoles.size();restrictedRoleIndex++){
				String restrictedRole = restrictedRoles.get(restrictedRoleIndex);
				for(int listOfRoleIndex = 0;listOfRoleIndex<listOfRole.size();listOfRoleIndex++){
					//check if the restricted role match with the a in the list of role of the current user 
					if(restrictedRole.equalsIgnoreCase(listOfRole.get(listOfRoleIndex).getName(locale))){
						return new WorkflowMenuItem(title, helpText, navImage, link, position);
					}
				}
			}
			
			LOGGER.debug("The current user doesn't have right to access the menu item.");
			
			//no match found => return null
			return null;
			
		}

	}

	/**
	 * Parse the property content and collect the list of allowed roles 
	 * @param propertyContent the value read as property
	 * @return a list of allowed role, <i>null</i> if there is no allowed role or is the property doesn't exists
	 */
	private List<String> collectListOfRestictedRoles(String propertyContent) {		
		//test if the property is define and contains something
		if(StringChecker.isNotNullOrEmpty(propertyContent)){
			return StringChecker.splitListOfValueWithModuleSeparator(propertyContent);			
		}else{
			return null;
		}		
	}

	/**
	 * Check that the given value of position doesn't exist. If this one exists,
	 * then create a new position and use it.
	 * 
	 * @param positionProperty
	 *            the string value of the position given as property
	 * @return the value that can be use to manage position (sure that it's not
	 *         use)
	 */
	private float checkAnUpdatePosition(String positionProperty) {
		if (StringChecker.isNotNullOrEmpty(positionProperty)) {
			try {
				Float position = new Float(positionProperty);
				if (!_foundPosition.contains(position)) {
					// store new position
					_foundPosition.add(position);

					// return value
					return position.floatValue();
				}
			} catch (NumberFormatException e) {
				// do nothing, a default position will be set when outgoing if
			}
		}

		// get new Position and update property
		float newPosition = _lastPositionSet + 1;
		this._lastPositionSet = newPosition;

		// store the use position
		_foundPosition.add(new Float(newPosition));

		return newPosition;

	}

	/**
	 * Get the property and extract the String value
	 * 
	 * @param propertyName
	 *            The name of the property to treat
	 * @param resource
	 *            the resource to treat		
	 * @return the property value, <i>empty string </i> if a problem occurs (see
	 *         logs for more details)
	 */
	private String treatProperty(String propertyName, CmsResource resource) {

		CmsProperty property = OpenCmsEasyAccess.getProperty(propertyName, resource);		
		if (property != null) {
			String propertyValue = property.getValue();
			if (StringChecker.isNotNullOrEmpty(propertyValue)) {
				return propertyValue.trim();
			} else {
				LOGGER.info("The property " + propertyName + " of file "
						+ resource.getRootPath() + " is probably empty.");
			}
		}
		return "";
	}

	/**
	 * Get the single instance of WorkflowMenuItemLoader. If none instance of the class exists then the instance is created.
	 * @param cmsObject   the object that contains the request context
	 * @return   the single instance of WorkflowMenuItemLoader
	 */
	public synchronized static WorkflowMenuItemLoader getInstance(CmsObject cmsObject) {
		
		LOGGER.debug("WF | userName = "+cmsObject.getRequestContext().currentUser().getName());
		LOGGER.debug("WF | lastUserName = "+lastUserName);
		
		//check that the last user is the same, else, reload the map of item
		if (instance == null || lastUserName == null || !(lastUserName.equalsIgnoreCase(cmsObject.getRequestContext().currentUser().getName()))){
			createInstance(cmsObject);			
		}
		return instance;
	}
	
	/**
	 * Create an instance of WorkflowMenuItemLoader
	 * @param cmsObject the object that contains the request context
	 */
	private synchronized static void createInstance(CmsObject cmsObject){
		LOGGER.debug("WF | Create new instance");
		instance = new WorkflowMenuItemLoader(cmsObject);
		lastUserName = cmsObject.getRequestContext().currentUser().getName();
	}

	/**
	 * @return      the _mapOfItems
	 * @uml.property  name="_mapOfItems"
	 */
	public Map<String, WorkflowMenuItem> get_mapOfItems() {
		return _mapOfItems;
	}

	/**
	 * Get the list of all collected Menu items, sorted by the index position
	 * 
	 * @return the list of WorkflowMenuItem that must be displayed
	 */
	public List<WorkflowMenuItem> getMenuItems() {
		// Create the map that will store the WorkflowMenuItem by position
		TreeMap<Float, WorkflowMenuItem> sortedMapByPosition = new TreeMap<Float, WorkflowMenuItem>();

		LOGGER.debug(ErrorFormatter.formatMap(_mapOfItems,
		"WF | _mapOfItems"));
		
		/*
		 * add the map elements into the sorted map if none object with the same
		 * position exists
		 */
		Iterator<WorkflowMenuItem> mapOfItemIterator = this._mapOfItems
				.values().iterator();
		while (mapOfItemIterator.hasNext()) {
			WorkflowMenuItem workflowMenuItem = mapOfItemIterator.next();
			Float position = new Float(workflowMenuItem.get_position());
			// check if an object with such position exist
			if (!sortedMapByPosition.containsKey(position)) {
				sortedMapByPosition.put(position, workflowMenuItem);
			} else {
				// if two object with same position exists, then the second one
				// is not added into the map of object to display and a warning
				// message is return to the user.
				LOGGER.warn("WF | Two objects with position " + position
						+ " exists. The menu " + workflowMenuItem.get_name()
						+ " will not be displayed");
			}
		}

		// return the list of menu item sorted by position
		return new ArrayList<WorkflowMenuItem>(sortedMapByPosition.values());
	}

}
