/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
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
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;

import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class stores the rights associated to a workflow.
 * 
 * @author Sébastien Bianco
 * 
 */
public class WorkflowRights {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorkflowRights.class);

	/**
	 * This map store the allowance to access workflow per branches
	 */
	private Map<String, AllowedPeople> _allowancePerBranches = new HashMap<String, AllowedPeople>();

	/**
	 * This map store the allowance to access workflow per branches pattern
	 */
	private Map<String, AllowedPeople> _allowancePerBranchesPattern = new HashMap<String, AllowedPeople>();

	/**
	 * Add a new branch with associated allowed people
	 * 
	 * @param branchPath
	 *            the path of the branch
	 * @param allowedPeople
	 *            the list of allowed people
	 */
	public void addBranch(String branchPath, AllowedPeople allowedPeople) {
		if (branchPath != null && allowedPeople != null) {
			if (!this._allowancePerBranches.containsKey(branchPath)) {
				this._allowancePerBranches.put(branchPath, allowedPeople);
			} else {
				LOGGER
						.info("Some rights associated to branch "
								+ branchPath
								+ " has already been registered. This second occurence will no be added");
			}
		} else {
			LOGGER.info("An error occurs during add of a branches (branchPath="
					+ branchPath + ",allowedPeople=" + allowedPeople + ")");
		}
	}

	/**
	 * Add a new branch pattern with associated allowed people
	 * 
	 * @param branchPath
	 *            the pattern of path of the branch
	 * @param allowedPeople
	 *            the list of allowed people
	 */
	public void addBranchPattern(String branchPathPattern,
			AllowedPeople allowedPeople) {
		if (branchPathPattern != null && allowedPeople != null) {
			if (!this._allowancePerBranchesPattern
					.containsKey(branchPathPattern)) {
				this._allowancePerBranchesPattern.put(branchPathPattern,
						allowedPeople);
			} else {
				LOGGER
						.info("Some rights associated to branch pattern "
								+ branchPathPattern
								+ " has already been registered. This second occurence will no be added");
			}
		} else {
			LOGGER
					.info("An error occurs during add of a branches pattern(branchPathPattern="
							+ branchPathPattern
							+ ",allowedPeople="
							+ allowedPeople + ")");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";
		{
			Iterator<Entry<String, AllowedPeople>> iterator = _allowancePerBranches
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, AllowedPeople> entry = iterator.next();
				result += "\t"
						+ entry.getKey()
						+ " : \n"
						+ ((entry.getValue() != null) ? entry.getValue()
								.toString() : "null") + "\n";
			}
		}
		{
			Iterator<Entry<String, AllowedPeople>> iterator = _allowancePerBranchesPattern
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, AllowedPeople> entry = iterator.next();
				result += "\t"
						+ entry.getKey()
						+ " : \n"
						+ ((entry.getValue() != null) ? entry.getValue()
								.toString() : "null") + "\n";
			}
		}
		return result;
	}

	/**
	 * Convert the object into a HTML representation
	 * @return the HTML representation of the workflow right
	 */
	public String toHTML() {
		String result = "";
		{
			Iterator<Entry<String, AllowedPeople>> iterator = _allowancePerBranches
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, AllowedPeople> entry = iterator.next();
				result += ""
						+ entry.getKey()
						+ " : <br>"
						+ ((entry.getValue() != null) ? entry.getValue()
								.toHTML() : "null") + "<br>"; 
			}
		}
		{
			Iterator<Entry<String, AllowedPeople>> iterator = _allowancePerBranchesPattern
					.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, AllowedPeople> entry = iterator.next();
				result += ""
						+ entry.getKey()
						+ " : <br>"
						+ ((entry.getValue() != null) ? entry.getValue()
								.toHTML() : "null") + "<br>";
			}
		}
		return result;
	}
	
	/**
	 * Check if the user has rights to access the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param key
	 *            the name of the workflow to access
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to access the workflow,
	 *         <i>false</i> otherwise.
	 */
	public boolean hasReadRightOnWorkflow(CmsUser currentUser,
			String parentFolder, Locale locale) {
	
		/*
		 * Get the list of all allowance to check => collect branches and
		 * pattern
		 */
		List<AllowedPeople> allowanceToCheck = this
				.getListOfAllowanceToCheck(parentFolder);

		/*
		 * try to get at least one true
		 */
		Iterator<AllowedPeople> allowanceToCheckIterator = allowanceToCheck
				.iterator();
		while (allowanceToCheckIterator.hasNext()) {
			AllowedPeople allowance = allowanceToCheckIterator.next();
			if (allowance != null) {
				// check right
				boolean right = allowance.hasReadRightOnWorkflows(currentUser,
						locale);
				if (right)
					return true;
			} else {
				LOGGER.debug("WF | allowance = null");
			}
		}

		LOGGER.debug("WF | No branch associated to the folder " + parentFolder
				+ " => No read rights for the user " + currentUser.getName()
				+ " !");
		return false;
	}

	/**
	 * Check if the user has rights to modify the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param parentFolder
	 *            the current branch
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to modify the workflow,
	 *         <i>false</i> otherwise.
	 */
	public boolean hasWriteRightOnWorkflow(CmsUser currentUser,
			String parentFolder, Locale locale) {

		/*
		 * Get the list of all allowance to check => collect branches and
		 * pattern
		 */
		List<AllowedPeople> allowanceToCheck = this
				.getListOfAllowanceToCheck(parentFolder);

		/*
		 * try to get at least one true
		 */
		Iterator<AllowedPeople> allowanceToCheckIterator = allowanceToCheck
				.iterator();
		while (allowanceToCheckIterator.hasNext()) {
			AllowedPeople allowance = allowanceToCheckIterator.next();
			if (allowance != null) {
				// check right
				boolean right = allowance.hasWriteRightOnWorkflows(currentUser,
						locale);
				if (right)
					return true;
			} else {
				LOGGER.debug("WF | allowance = null");
			}
		}

		LOGGER.debug("WF | No branch associated to the folder " + parentFolder
				+ " => No write rights for the user " + currentUser.getName()
				+ " !");
		return false;
	}

	/**
	 * Get the list of allowance to check
	 * 
	 * @param folderName
	 *            the name of the current parent folder
	 * @return the list of allowance to check, an <i>empty list</i> if there is
	 *         none of them.
	 */
	private List<AllowedPeople> getListOfAllowanceToCheck(String folderName) {

		List<AllowedPeople> allowanceToCheck = new ArrayList<AllowedPeople>();

		// Get the list of pattern
		List<String> parentPattern = this.getMatchedPattern(folderName);
		if (parentPattern != null) {
			Iterator<String> parentPatternIterator = parentPattern.iterator();
			while (parentPatternIterator.hasNext()) {
				String pattern = parentPatternIterator.next();
				if (pattern != null) {
					AllowedPeople allowance = this._allowancePerBranchesPattern
							.get(pattern);
					if (allowance != null) {
						allowanceToCheck.add(allowance);
					} else {
						LOGGER.debug("WF | allowance = null");
					}
				} else {
					LOGGER.debug("WF | pattern = null");
				}
			}
		}

		// get the list of branch
		List<String> parentBranches = this.getParentBranch(folderName);
		if (parentBranches != null) {
			Iterator<String> parentBranchesIterator = parentBranches.iterator();

			while (parentBranchesIterator.hasNext()) {
				String parentBranch = parentBranchesIterator.next();
				if (parentBranch != null) {
					AllowedPeople allowance = this._allowancePerBranches
							.get(parentBranch);
					if (allowance != null) {
						allowanceToCheck.add(allowance);
					} else {
						LOGGER.debug("WF | allowance = null");
					}
				} else {
					LOGGER.debug("WF | parentBranch = null");
				}
			}
		}
		return allowanceToCheck;
	}

	/**
	 * Get the list of parent branch corresponding to the given folder
	 * 
	 * @param folder
	 *            the folder to get the parent branch of
	 * @return the list of parent branch path, <b>null</b> if there is no
	 *         parent branch.
	 */
	private List<String> getParentBranch(String folder) {
		List<String> result = new ArrayList<String>();

		// check null
		if (!StringChecker.isNotNullOrEmpty(folder)) {
			return null;
		}

		// get parent branch
		String parentBranch = folder;
		do {
			// check if the branch exist
			if (this._allowancePerBranches.containsKey(parentBranch)) {
				result.add(parentBranch);
			}

			// get the parent folder
			parentBranch = CmsResource.getParentFolder(parentBranch);
		} while (parentBranch != null);
		return result;
	}

	/**
	 * The list of pattern that matche the folder name
	 * 
	 * @param folderName
	 *            the name of folder to match
	 * @return the list of pattern that matche the folder name, an <i>empty list</i>
	 *         if there is no pattern that match
	 */
	private List<String> getMatchedPattern(String folderName) {
		List<String> matchedPattern = new ArrayList<String>();

		// try to match all known pattern
		Iterator<String> patternIterator = this._allowancePerBranchesPattern
				.keySet().iterator();
		while (patternIterator.hasNext()) {
			String patternToCheck = patternIterator.next();
			if (StringChecker.isNotNullOrEmpty(patternToCheck)) {
				try {
					if (Pattern.matches(patternToCheck, folderName)) {
						matchedPattern.add(patternToCheck);
					}
				} catch (PatternSyntaxException e) {
					LOGGER.info("The pattern " + patternToCheck
							+ " is invalid.");
				}
			}
		}

		return matchedPattern;
	}

	/**
	 * Check if the user has rights to create the workflow
	 * 
	 * @param currentUser
	 *            the current user
	 * @param key
	 *            the name of the workflow to access
	 * @param locale
	 *            the current local
	 * @return <i>true</i> if the user has rights to access the workflow,
	 *         <i>false</i> otherwise.
	 */
	public boolean hasCreateRightOnWorkflow(CmsUser currentUser, String parentFolder, Locale locale) {
		/*
		 * Get the list of all allowance to check => collect branches and
		 * pattern
		 */
		List<AllowedPeople> allowanceToCheck = this
				.getListOfAllowanceToCheck(parentFolder);

		/*
		 * try to get at least one true
		 */
		Iterator<AllowedPeople> allowanceToCheckIterator = allowanceToCheck
				.iterator();
		while (allowanceToCheckIterator.hasNext()) {
			AllowedPeople allowance = allowanceToCheckIterator.next();
			if (allowance != null) {
				// check right
				boolean right = allowance.hasCreateRightOnWorkflows(currentUser,
						locale);
				if (right)
					return true;
			} else {
				LOGGER.debug("WF | allowance = null");
			}
		}

		LOGGER.debug("WF | No branch associated to the folder " + parentFolder
				+ " => No c rights for the user " + currentUser.getName()
				+ " !");
		return false;
	}

}
