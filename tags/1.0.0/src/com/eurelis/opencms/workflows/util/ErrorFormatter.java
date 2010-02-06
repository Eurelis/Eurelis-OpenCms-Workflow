/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class contains method to format the error so as to be return in a log
 * 
 * @author Sébastien Bianco
 * 
 */
public class ErrorFormatter {

	/**
	 * Set if the Stack of an Exception must be displayed
	 */
	private static boolean displayStackTrace = true;

	/**
	 * Format an Exception so as to be displayed in a error message.<br/>This
	 * method take care of the value of displayStackTrace so as to format the
	 * text;
	 * 
	 * @param e
	 *            the Exception that as to be displayed
	 * @return the formated String
	 */
	public static String formatException(Exception e) {
		String result = "";

		// test if there is a Localized Message. If there is one, this message
		// wille be used, in case of not, the standard message will be used.
		String localizedMessage = e.getLocalizedMessage();
		if (localizedMessage == null || localizedMessage.equals("")
				|| localizedMessage.equals(" ")) {
			localizedMessage = e.getMessage();
		}

		result += localizedMessage;

		// Display the stack trace
		if (ErrorFormatter.displayStackTrace) {
			StackTraceElement[] st = e.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				result += "\n\t" + st[i];
			}
		}

		// return the resulting string
		return result;
	}

	/**
	 * Format a list object so as to be easely printed in a Log
	 * 
	 * @param list
	 *            the list to be printed
	 * @param text
	 *            the text that describe the list (will be displayed before the
	 *            list content)
	 * @return a "pretty-print" of the list
	 */
	public static String formatList(List list, String text) {
		String result = text;
		if (list.isEmpty()) {
			result += " === EMPTY ===";
		} else {
			for (int i = 0; i < list.size(); i++) {
				result += "\n\t" + i + ": " + ((list.get(i)!=null)?list.get(i).toString():"null");
			}
		}
		return result;
	}

	/**
	 * Format a map object so as to be easely printed in a Log
	 * 
	 * @param map
	 *            the map to be printed
	 * @param text
	 *            the text that describe the map (will be displayed before the
	 *            map content)
	 * @return a "pretty-print" of the map
	 */
	public static String formatMap(Map map, String text) {
		String result = text;
		if (map.isEmpty()) {
			result += " === EMPTY ===";
		} else {
			Iterator<Entry> mapEntryIterator = map.entrySet().iterator();
			while (mapEntryIterator.hasNext()) {
				Entry entry = mapEntryIterator.next();
				if (entry.getValue() instanceof Object[]) {
					result += "\n\t" + entry.getKey().toString() + " : ";
					Object[] tab = (Object[]) entry.getValue();
					for (int i = 0; i < tab.length; i++) {
						result += "\n\t\t" + tab[i].toString();
					}
				} else {
					if (entry.getValue() != null) {
						result += "\n\t" + entry.getKey().toString() + " : "
								+ entry.getValue().toString();
					} else {
						result += "\n\t" + entry.getKey().toString() + " : "
								+ "null";
					}
				}
			}

		}
		return result;
	}

	/**
	 * Format an array of objects so as to be easely printed in a Log
	 * 
	 * @param objectArray
	 *            the array to be printed
	 * @param text
	 *            the text that describe the map (will be displayed before the
	 *            map content)
	 * @return a "pretty-print" of the map
	 */
	public static String formatArray(Object[] objectArray, String text) {
		String result = text;
		if (objectArray.length == 0) {
			result += " === EMPTY ===";
		} else {
			for (int i = 0; i < objectArray.length; i++) {
				result += "\n\t" + i + " : " + objectArray[i].toString();
			}
		}
		return result;
	}

}
