/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsUser;
import org.opencms.security.CmsRole;

import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class store the list of groups/roles/users that are allowed to access a workflow
 * 
 * @author Sébastien Bianco
 */
public class AllowedPeople {

	/** The log object for this class. */
	private static final Logger	LOGGER							= Logger.getLogger(AllowedPeople.class);

	/**
	 * The String representing right of reading but not modifying
	 */
	public static final String	RIGHT_CREATE_STRING				= "c";

	/**
	 * The String representing right of reading but not modifying
	 */
	public static final String	RIGHT_READ_STRING				= "r";

	/**
	 * The String representing right of modifying but not reading
	 */
	public static final String	RIGHT_WRITE_STRING				= "w";

	/**
	 * The String representing right of reading and modifying
	 */
	public static final String	RIGHT_READ_WRITE_STRING			= "rw";

	/**
	 * The String representing right of reading, modifying and creating
	 */
	public static final String	RIGHT_READ_WRITE_CREATE_STRING	= "rwc";

	/**
	 * The String representing right of reading and creating
	 */
	public static final String	RIGHT_READ_CREATE_STRING		= "rc";

	/**
	 * The String representing right of writing and creating
	 */
	public static final String	RIGHT_WRITE_CREATE_STRING		= "wc";

	/**
	 * The String representing right of doing nothing
	 */
	public static final String	RIGHT_NONE_STRING				= "";

	/**
	 * The right of reading but not modifying
	 */
	public static final short	RIGHT_READ						= 0;

	/**
	 * The right of modifying but not reading
	 */
	public static final short	RIGHT_WRITE						= 1;

	/**
	 * The right of reading and modifying
	 */
	public static final short	RIGHT_READ_WRITE				= 2;

	/**
	 * The right of create
	 */
	public static final short	RIGHT_CREATE					= 4;

	/**
	 * The right of doing nothing
	 */
	public static final short	RIGHT_NONE						= 3;

	/**
	 * the all rights
	 */
	private static final short	RIGHT_READ_WRITE_CREATE			= 5;

	/**
	 * the rights to read and create
	 */
	private static final short	RIGHT_READ_CREATE				= 6;

	/**
	 * the rights to write and create
	 */
	private static final short	RIGHT_WRITE_CREATE				= 7;

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a user
	 */
	private Map<String, Short>	_userRights						= new HashMap<String, Short>();

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a group
	 */
	private Map<String, Short>	_groupRights					= new HashMap<String, Short>();

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a role
	 */
	private Map<String, Short>	_roleRights						= new HashMap<String, Short>();

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a user pattern
	 */
	private Map<String, Short>	_userPatternRights				= new HashMap<String, Short>();

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a group pattern
	 */
	private Map<String, Short>	_groupPatternRights				= new HashMap<String, Short>();

	/**
	 * The map that store the rights (Read/write/Read_Write/None) associated to a role pattern
	 */
	private Map<String, Short>	_rolePatternRights				= new HashMap<String, Short>();

	/**
	 * Add a new allowed User
	 * 
	 * @param userName
	 *            the name of the user
	 * @param userRight
	 *            the associated right of the user
	 */
	public void addAllowedUser(String userName, String userRight) {
		if (userName != null && userRight != null) {
			if (!this._userRights.containsKey(userName)) {
				this._userRights.put(userName, this.parseRights(userRight));
			}
			else {
				LOGGER.info("Some rights associated to user " + userName
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed user(userName=" + userName + ",userRight=" + userRight
					+ ")");
		}
	}

	/**
	 * Add a new allowed Group
	 * 
	 * @param groupName
	 *            the name of the group
	 * @param userRight
	 *            the associated right of the group
	 */
	public void addAllowedGroup(String groupName, String groupRight) {
		if (groupName != null && groupRight != null) {
			if (!this._groupRights.containsKey(groupName)) {
				this._groupRights.put(groupName, this.parseRights(groupRight));
			}
			else {
				LOGGER.info("Some rights associated to group " + groupName
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed group(groupName=" + groupName + ",groupRight="
					+ groupRight + ")");
		}
	}

	/**
	 * Add a new allowed role
	 * 
	 * @param roleName
	 *            the name of the role
	 * @param roleRight
	 *            the associated right of the role
	 */
	public void addAllowedRole(String roleName, String roleRight) {
		if (roleName != null && roleRight != null) {
			if (!this._roleRights.containsKey(roleName)) {
				this._roleRights.put(roleName, this.parseRights(roleRight));
			}
			else {
				LOGGER.info("Some rights associated to role " + roleName
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed role(roleName=" + roleName + ",roleRight=" + roleRight
					+ ")");
		}
	}

	/**
	 * Add a new allowed User pattern
	 * 
	 * @param userNamePattern
	 *            the pattern of name of the user
	 * @param userRight
	 *            the associated right of the user
	 */
	public void addAllowedUserPattern(String userNamePattern, String userRight) {
		if (userNamePattern != null && userRight != null) {
			if (!this._userPatternRights.containsKey(userNamePattern)) {
				this._userPatternRights.put(userNamePattern, this.parseRights(userRight));
			}
			else {
				LOGGER.info("Some rights associated to user pattern " + userNamePattern
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed user pattern(userNamePattern=" + userNamePattern
					+ ",userRight=" + userRight + ")");
		}
	}

	/**
	 * Add a new allowed Group Pattern
	 * 
	 * @param groupNamePattern
	 *            the pattern of name of the group
	 * @param userRight
	 *            the associated right of the group
	 */
	public void addAllowedGroupPattern(String groupNamePattern, String groupRight) {
		if (groupNamePattern != null && groupRight != null) {
			if (!this._groupPatternRights.containsKey(groupNamePattern)) {
				this._groupPatternRights.put(groupNamePattern, this.parseRights(groupRight));
			}
			else {
				LOGGER.info("Some rights associated to group pattern " + groupNamePattern
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed group pattern(groupNamePattern=" + groupNamePattern
					+ ",groupRight=" + groupRight + ")");
		}
	}

	/**
	 * Add a new allowed role Pattern
	 * 
	 * @param roleNamePattern
	 *            the pattern of name of the role
	 * @param roleRight
	 *            the associated right of the role
	 */
	public void addAllowedRolePattern(String roleNamePattern, String roleRight) {
		if (roleNamePattern != null && roleRight != null) {
			if (!this._rolePatternRights.containsKey(roleNamePattern)) {
				this._rolePatternRights.put(roleNamePattern, this.parseRights(roleRight));
			}
			else {
				LOGGER.info("Some rights associated to role pattern " + roleNamePattern
						+ " has already been registered. This second occurence will no be added");
			}
		}
		else {
			LOGGER.info("An error occurs during add of a allowed role pattern(roleNamePattern=" + roleNamePattern
					+ ",roleRight=" + roleRight + ")");
		}
	}

	/**
	 * update an allowed User (create if required)
	 * 
	 * @param userName
	 *            the name of the user
	 * @param userRight
	 *            the associated right of the user
	 */
	public void updateAllowedUser(String userName, String userRight) {
		if (userName != null && userRight != null) {
			this._userRights.put(userName, this.parseRights(userRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed user(userName=" + userName + ",userRight=" + userRight
					+ ")");
		}
	}

	/**
	 * update an allowed Group (create if required)
	 * 
	 * @param groupName
	 *            the name of the group
	 * @param userRight
	 *            the associated right of the group
	 */
	public void updateAllowedGroup(String groupName, String groupRight) {
		if (groupName != null && groupRight != null) {
			this._groupRights.put(groupName, this.parseRights(groupRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed group(groupName=" + groupName + ",groupRight="
					+ groupRight + ")");
		}
	}

	/**
	 * update an allowed role (create if required)
	 * 
	 * @param roleName
	 *            the name of the role
	 * @param roleRight
	 *            the associated right of the role
	 */
	public void updateAllowedRole(String roleName, String roleRight) {
		if (roleName != null && roleRight != null) {
			this._roleRights.put(roleName, this.parseRights(roleRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed role(roleName=" + roleName + ",roleRight=" + roleRight
					+ ")");
		}
	}

	/**
	 * update an allowed User pattern (create if required)
	 * 
	 * @param userNamePattern
	 *            the pattern of name of the user
	 * @param userRight
	 *            the associated right of the user
	 */
	public void updateAllowedUserPattern(String userNamePattern, String userRight) {
		if (userNamePattern != null && userRight != null) {
			this._userPatternRights.put(userNamePattern, this.parseRights(userRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed user pattern(userNamePattern=" + userNamePattern
					+ ",userRight=" + userRight + ")");
		}
	}

	/**
	 * update an allowed Group Pattern (create if required)
	 * 
	 * @param groupNamePattern
	 *            the pattern of name of the group
	 * @param userRight
	 *            the associated right of the group
	 */
	public void updateAllowedGroupPattern(String groupNamePattern, String groupRight) {
		if (groupNamePattern != null && groupRight != null) {
			this._groupPatternRights.put(groupNamePattern, this.parseRights(groupRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed group pattern(groupNamePattern=" + groupNamePattern
					+ ",groupRight=" + groupRight + ")");
		}
	}

	/**
	 * update an allowed role Pattern (create if required)
	 * 
	 * @param roleNamePattern
	 *            the pattern of name of the role
	 * @param roleRight
	 *            the associated right of the role
	 */
	public void updateAllowedRolePattern(String roleNamePattern, String roleRight) {
		if (roleNamePattern != null && roleRight != null) {
			this._rolePatternRights.put(roleNamePattern, this.parseRights(roleRight));
		}
		else {
			LOGGER.info("An error occurs during add of a allowed role pattern(roleNamePattern=" + roleNamePattern
					+ ",roleRight=" + roleRight + ")");
		}
	}

	/**
	 * Extract the user right depending the String coding the user rights<br>
	 * This method try to get "r" and "w" character into the String. If found, the associated value is set.<br>
	 * This method is not case sensitive
	 * 
	 * @param userRight
	 *            the string that codes the user rights ("r"/"w"/"rw"/"");
	 * @return the Short object corresponding to right of the user. Invalid value will result in deny of all rights
	 */
	private Short parseRights(String userRight) {
		// convert all the string to lower case
		String userRights_lc = userRight.toLowerCase();

		// try to get "r" character
		boolean containsReadAllowance = userRights_lc.contains(RIGHT_READ_STRING);

		// try to get "w" character
		boolean containsWriteAllowance = userRights_lc.contains(RIGHT_WRITE_STRING);

		// try to get the "c" character
		boolean containsCreateAllowance = userRights_lc.contains(RIGHT_CREATE_STRING);

		// get the Short object corresponding
		if (containsReadAllowance && containsWriteAllowance && containsCreateAllowance)
			return new Short(RIGHT_READ_WRITE_CREATE);
		if (containsCreateAllowance && containsWriteAllowance)
			return new Short(RIGHT_WRITE_CREATE);
		if (containsReadAllowance && containsCreateAllowance)
			return new Short(RIGHT_READ_CREATE);
		if (containsReadAllowance && containsWriteAllowance)
			return new Short(RIGHT_READ_WRITE);
		if (containsReadAllowance)
			return new Short(RIGHT_READ);
		if (containsWriteAllowance)
			return new Short(RIGHT_WRITE);
		if (containsCreateAllowance)
			return new Short(RIGHT_CREATE);

		// contains neither "r" neither "w" => No rights
		return new Short(RIGHT_NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";

		/*
		 * List user
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userRights.entrySet().iterator();
			result += "\t\tUsers:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}

		/*
		 * List group
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupRights.entrySet().iterator();
			result += "\t\tGroup:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}

		/*
		 * List role
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _roleRights.entrySet().iterator();
			result += "\t\tRole:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}

		/*
		 * List user pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userPatternRights.entrySet().iterator();
			result += "\t\tUser patterns:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}

		/*
		 * List group pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupPatternRights.entrySet().iterator();
			result += "\t\tGroup patterns:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}

		/*
		 * List role pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _rolePatternRights.entrySet().iterator();
			result += "\t\tRole patterns:\n";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "\t\t\t"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\n";
			}
		}
		return result;
	}

	/**
	 * Get the String associated to the right value (""/"w"/"r"/"rw")
	 * 
	 * @param rightValue
	 *            the value of the right
	 * @return the String representation of the right, <b>null</b> if a invalid value is set to the right
	 */
	public static String getStringVisualisationOfRights(short rightValue) {
		switch (rightValue) {
			case RIGHT_NONE:
				return RIGHT_NONE_STRING;
			case RIGHT_READ:
				return RIGHT_READ_STRING;
			case RIGHT_WRITE:
				return RIGHT_WRITE_STRING;
			case RIGHT_READ_WRITE:
				return RIGHT_READ_WRITE_STRING;
			case RIGHT_CREATE:
				return RIGHT_CREATE_STRING;
			case RIGHT_READ_CREATE:
				return RIGHT_READ_CREATE_STRING;
			case RIGHT_WRITE_CREATE:
				return RIGHT_WRITE_CREATE_STRING;
			case RIGHT_READ_WRITE_CREATE:
				return RIGHT_READ_WRITE_CREATE_STRING;
			default:
				return null;
		}
	}

	/**
	 * Check if the user has rights to access the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to access the workflow, <i>false</i> otherwise.
	 */
	public boolean hasReadRightOnWorkflows(CmsUser currentUser, Locale locale) {
		// check by name then by group then by role
		return this.checkValue(this._userPatternRights, this._userRights, currentUser.getName(), new short[] {
				RIGHT_READ, RIGHT_READ_WRITE, RIGHT_READ_CREATE, RIGHT_READ_WRITE_CREATE })
				|| this.checkReadRightForGroup(currentUser) || this.checkReadRightForRole(currentUser, locale);
	}

	/**
	 * Get the list of role of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has right to read the workflow,<i>false</i> otherwise.
	 */
	private boolean checkReadRightForRole(CmsUser currentUser, Locale locale) {
		// get the list of role of the user
		List<CmsRole> roles = OpenCmsEasyAccess.getRolesOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_READ, RIGHT_READ_WRITE, RIGHT_READ_CREATE, RIGHT_READ_WRITE_CREATE };
		Iterator<CmsRole> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext()) {
			CmsRole role = rolesIterator.next();
			// check role
			boolean allowed = this.checkValue(this._rolePatternRights, this._roleRights, role.getName(locale),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Get the list of group of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @return <i>true</i> if the user has right to read the workflow,<i>false</i> otherwise.
	 */
	private boolean checkReadRightForGroup(CmsUser currentUser) {
		// get the list of group of the user
		List<CmsGroup> groups = OpenCmsEasyAccess.getGroupOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_READ, RIGHT_READ_WRITE, RIGHT_READ_CREATE, RIGHT_READ_WRITE_CREATE };
		Iterator<CmsGroup> groupIterator = groups.iterator();
		while (groupIterator.hasNext()) {
			CmsGroup group = groupIterator.next();
			// check group
			boolean allowed = this.checkValue(this._groupPatternRights, this._groupRights, group.getName(),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Check if the user has rights to modify the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to modify the workflow, <i>false</i> otherwise.
	 */
	public boolean hasWriteRightOnWorkflows(CmsUser currentUser, Locale locale) {
		// check by name then by group then by role
		return this.checkValue(this._userPatternRights, this._userRights, currentUser.getName(), new short[] {
				RIGHT_WRITE, RIGHT_READ_WRITE, RIGHT_WRITE_CREATE, RIGHT_READ_WRITE_CREATE })
				|| this.checkWriteRightForGroup(currentUser) || this.checkWriteRightForRole(currentUser, locale);
	}

	/**
	 * Get the list of role of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has right to modify the workflow,<i>false</i> otherwise.
	 */
	private boolean checkWriteRightForRole(CmsUser currentUser, Locale locale) {

		// get the list of role of the user
		List<CmsRole> roles = OpenCmsEasyAccess.getRolesOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_WRITE, RIGHT_READ_WRITE, RIGHT_WRITE_CREATE,
				RIGHT_READ_WRITE_CREATE };
		Iterator<CmsRole> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext()) {
			CmsRole role = rolesIterator.next();
			// check role
			boolean allowed = this.checkValue(this._rolePatternRights, this._roleRights, role.getName(locale),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Get the list of group of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @return <i>true</i> if the user has right to modify the workflow,<i>false</i> otherwise.
	 */
	private boolean checkWriteRightForGroup(CmsUser currentUser) {

		// get the list of group of the user
		List<CmsGroup> groups = OpenCmsEasyAccess.getGroupOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_WRITE, RIGHT_READ_WRITE, RIGHT_WRITE_CREATE,
				RIGHT_READ_WRITE_CREATE };
		Iterator<CmsGroup> groupIterator = groups.iterator();
		while (groupIterator.hasNext()) {
			CmsGroup group = groupIterator.next();
			// check group
			boolean allowed = this.checkValue(this._groupPatternRights, this._groupRights, group.getName(),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Compare the allowed values to the value stored in the map with key equals to name
	 * 
	 * @param mapOfPattern
	 *            the map of pattern to check the name
	 * @param mapOfValue
	 *            the map of value to check the name
	 * @param name
	 *            the name to check
	 * @param allowedValues
	 *            the value to check with
	 * @return <i>true</i> if there is at least one match, <i>false</i> otherwise
	 */
	private boolean checkValue(Map<String, Short> mapOfPattern, Map<String, Short> mapOfValue, String name,
			short[] allowedValues) {

		// check pattern
		List<String> matchedPattern = this.getMatchedPattern(name, mapOfPattern.keySet());
		Iterator<String> matchedPatternIterator = matchedPattern.iterator();
		while (matchedPatternIterator.hasNext()) {
			String pattern = matchedPatternIterator.next();
			Short rightValue = mapOfPattern.get(pattern);
			for (int i = 0; i < allowedValues.length; i++) {
				if (allowedValues[i] == rightValue) {
					return true;
				}
			}
		}

		// no pattern match
		// => check name
		if (mapOfValue.containsKey(name)) {
			Short rightValue = mapOfValue.get(name);
			for (int i = 0; i < allowedValues.length; i++) {
				if (allowedValues[i] == rightValue) {
					return true;
				}
			}
		}

		// no name match
		return false;
	}

	/**
	 * The list of pattern that match the folder name
	 * 
	 * @param name
	 *            the name to match with one of the potential pattern
	 * @param setOfPotentialPattern
	 *            the set of all potential pattern that could match
	 * @return the list of pattern that match the name, an <i>empty list</i> if there is no pattern that match
	 */
	private List<String> getMatchedPattern(String name, Set<String> setOfPotentialPattern) {
		List<String> matchedPattern = new ArrayList<String>();

		// try to match all known pattern
		Iterator<String> patternIterator = setOfPotentialPattern.iterator();
		while (patternIterator.hasNext()) {
			String patternToCheck = patternIterator.next();
			if (StringChecker.isNotNullOrEmpty(patternToCheck)) {
				try {
					if (Pattern.matches(patternToCheck, name)) {
						matchedPattern.add(patternToCheck);
					}
				}
				catch (PatternSyntaxException e) {
					LOGGER.info("The pattern " + patternToCheck + " is invalid.");
				}
			}
		}

		return matchedPattern;
	}

	/**
	 * Convert the object into a HTML representation
	 * 
	 * @return the HTML representation of the workflow right
	 */
	public String toHTML() {
		String result = "";

		/*
		 * List user
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;Users:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}

		/*
		 * List group
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;Group:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}

		/*
		 * List role
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _roleRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;Role:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}

		/*
		 * List user pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userPatternRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;User patterns:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}

		/*
		 * List group pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupPatternRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;Group patterns:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}

		/*
		 * List role pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _rolePatternRights.entrySet().iterator();
			result += "&nbsp;&nbsp;&nbsp;Role patterns:<br>";
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				result += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ entry.getKey()
						+ " : "
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "<br>";
			}
		}
		return result;
	}

	/**
	 * Check if the user has rights to create the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to access the workflow, <i>false</i> otherwise.
	 */
	public boolean hasCreateRightOnWorkflows(CmsUser currentUser, Locale locale) {
		// check by name then by group then by role
		return this.checkValue(this._userPatternRights, this._userRights, currentUser.getName(), new short[] {
				RIGHT_CREATE, RIGHT_READ_CREATE, RIGHT_READ_WRITE_CREATE, RIGHT_WRITE_CREATE })
				|| this.checkCreateRightForGroup(currentUser) || this.checkCreateRightForRole(currentUser, locale);
	}

	/**
	 * Get the list of role of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has right to create the workflow,<i>false</i> otherwise.
	 */
	private boolean checkCreateRightForRole(CmsUser currentUser, Locale locale) {

		// get the list of role of the user
		List<CmsRole> roles = OpenCmsEasyAccess.getRolesOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_CREATE, RIGHT_READ_CREATE, RIGHT_READ_WRITE_CREATE,
				RIGHT_WRITE_CREATE };
		Iterator<CmsRole> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext()) {
			CmsRole role = rolesIterator.next();
			// check role
			boolean allowed = this.checkValue(this._rolePatternRights, this._roleRights, role.getName(locale),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Get the list of group of the user and check if the user match an allowance or a pattern thanks its roles
	 * 
	 * @param currentUser
	 *            the user object
	 * @return <i>true</i> if the user has right to create the workflow,<i>false</i> otherwise.
	 */
	private boolean checkCreateRightForGroup(CmsUser currentUser) {

		// get the list of group of the user
		List<CmsGroup> groups = OpenCmsEasyAccess.getGroupOfUser(currentUser.getName());

		// Check if of them
		short[] allowedValues = new short[] { RIGHT_WRITE, RIGHT_READ_WRITE, RIGHT_WRITE_CREATE,
				RIGHT_READ_WRITE_CREATE };
		Iterator<CmsGroup> groupIterator = groups.iterator();
		while (groupIterator.hasNext()) {
			CmsGroup group = groupIterator.next();
			// check group
			boolean allowed = this.checkValue(this._groupPatternRights, this._groupRights, group.getName(),
					allowedValues);
			if (allowed)
				return true;
		}
		return false;
	}

	/**
	 * Generate a XML fragment containing the right about allowed people
	 * 
	 * @return the XML fragment corresponding to this object
	 */
	public Object toXML() {
		String xml = "";

		/*
		 * List user
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedUser name=\""
						+ entry.getKey()
						+ "\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}

		}

		/*
		 * List group
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedGroup name=\""
						+ entry.getKey()
						+ "\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}
		}

		/*
		 * List role
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _roleRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedRole name=\""
						+ entry.getKey()
						+ "\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}
		}

		/*
		 * List user pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _userPatternRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedUser name=\"{"
						+ entry.getKey()
						+ "}\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}
		}

		/*
		 * List group pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _groupPatternRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedGroup name=\"{"
						+ entry.getKey()
						+ "}\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}
		}

		/*
		 * List role pattern
		 */
		{// create block to allow use of same variable names
			Iterator<Entry<String, Short>> iterator = _rolePatternRights.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Short> entry = iterator.next();
				xml += "<allowedRole name=\"{"
						+ entry.getKey()
						+ "}\" type=\""
						+ ((entry.getValue() != null) ? AllowedPeople.getStringVisualisationOfRights(entry.getValue()
								.shortValue()) : "null") + "\"/>";
			}
		}

		return xml;
	}

}
