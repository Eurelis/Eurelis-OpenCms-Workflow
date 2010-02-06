/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.util;

import java.util.ArrayList;
import java.util.List;

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfiguration;
import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;

/**
 * This class contains some static method that check some properties on String
 * object
 * 
 * @author Sébastien Bianco
 * 
 */
public class StringChecker {

	/**
	 * Check if the given string is empty or null
	 * 
	 * @param stringToCheck
	 * @return <i>true</i> if the String is not null and not empty,, <i>false</i>
	 *         otherwise
	 */
	public static boolean isNotNullOrEmpty(String stringToCheck) {
		return stringToCheck != null && !stringToCheck.equals("")
				&& !stringToCheck.equals("null");
	}

	/**
	 * Remove all spaces of the string given as parameter
	 * 
	 * @param string
	 *            the string to treat
	 * @return the same string without space character
	 */
	public static String removeSpace(String string) {
		return string.replace(" ", "");
	}

	/**
	 * Split a string using {@link ModuleConfiguration#WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR} and return splitted value is a list
	 * @param stringToSplit
	 * @return the list of splitted value, <i>empty</i> empty list if there is no values.
	 */
	public static List<String> splitListOfValueWithModuleSeparator(
			String stringToSplit) {
		String separator = ModuleConfigurationLoader.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR;
		List<String> result = new ArrayList<String>();

		// check that it is not a control character use by the regex
		if (separator.equals("|") || separator.equals("*")) {
			separator = "\\" + separator;
		}

		if (StringChecker.isNotNullOrEmpty(stringToSplit)) {
			// get array of values
			String[] splitted = stringToSplit.trim().split(separator);
			// collect default values
			for (int i = 0; i < splitted.length; i++) {
				String splittedValue = splitted[i];
				if (StringChecker.isNotNullOrEmpty(splittedValue))
					result.add(splittedValue.trim());
			}
		}
		return result;

	}
}
