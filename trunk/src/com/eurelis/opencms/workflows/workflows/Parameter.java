/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.eurelis.opencms.workflows.jaxb.workflowconfig.WorkflowParameterType;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.StringChecker;

/**
 * This class store the information to a parameter associated to a workflow
 * 
 * @author Sébastien Bianco
 */
public class Parameter implements Serializable {

	/**
	 * The serial version ID
	 */
	private static final long	serialVersionUID		= 1L;

	/** The log object for this class. */
	private static final Logger	LOGGER					= Logger.getLogger(Parameter.class);

	/**
	 * The type value for int
	 */
	public static final int		INT						= 0;

	/**
	 * The type value for double
	 */
	public static final int		DOUBLE					= 1;

	/**
	 * The type value for float
	 */
	public static final int		FLOAT					= 2;

	/**
	 * The type value for long
	 */
	public static final int		LONG					= 3;

	/**
	 * The type value for short
	 */
	public static final int		SHORT					= 5;

	/**
	 * The type value for byte
	 */
	public static final int		BYTE					= 6;

	/**
	 * The type value for char
	 */
	public static final int		CHAR					= 7;

	/**
	 * The type value for String
	 */
	public static final int		STRING					= 8;

	/**
	 * The type value for boolean
	 */
	public static final int		BOOLEAN					= 9;

	/**
	 * The type value for Other
	 */
	public static final int		OTHER					= 999;

	/**
	 * The default size of the input in HTML code
	 */
	private static final int	DEFAULT_SIZE_OF_INPUT	= 20;

	/**
	 * The name of the parameter
	 * 
	 * @uml.property name="_name"
	 */
	private String				_name;

	/**
	 * The type of the parameter
	 * 
	 * @uml.property name="_type"
	 */
	private int					_type;

	/**
	 * The name to display
	 * 
	 * @uml.property name="_displayName"
	 */
	private String				_displayName;

	/**
	 * The value of the parameter
	 * 
	 * @uml.property name="_value"
	 */
	private Object				_value					= null;

	/**
	 * The list of default value
	 * 
	 * @uml.property name="_defaultValues"
	 */
	private List<String>		_defaultValues			= new ArrayList<String>();

	/**
	 * Default constructor only use to get the list of constant. This constructor initialize no fields.
	 */
	public Parameter() {};

	/**
	 * @param _name
	 * @param _type
	 * @param name
	 */
	public Parameter(String _name, int _type, String name) {
		super();
		this._name = _name;
		this._type = _type;
		_displayName = name;
	}

	/**
	 * Constructor of the class
	 * 
	 * @param name
	 *            the name of the parameter
	 * @param type
	 *            the type of the parameter
	 * @param displayName
	 *            the name to display of the parameter
	 */
	public Parameter(String name, WorkflowParameterType type, String displayName) {
		super();
		this._name = name;
		this._displayName = displayName;

		/*
		 * convert type as a number (beurk method)
		 */
		if (type.equals(WorkflowParameterType.BOOLEAN)) {
			this._type = BOOLEAN;
		}
		else if (type.equals(WorkflowParameterType.INT)) {
			this._type = INT;
		}
		else if (type.equals(WorkflowParameterType.LONG)) {
			this._type = LONG;
		}
		else if (type.equals(WorkflowParameterType.DOUBLE)) {
			this._type = DOUBLE;
		}
		else if (type.equals(WorkflowParameterType.BYTE)) {
			this._type = BYTE;
		}
		else if (type.equals(WorkflowParameterType.CHAR)) {
			this._type = CHAR;
		}
		else if (type.equals(WorkflowParameterType.STRING)) {
			this._type = STRING;
		}
		else if (type.equals(WorkflowParameterType.FLOAT)) {
			this._type = FLOAT;
		}
		else if (type.equals(WorkflowParameterType.SHORT)) {
			this._type = SHORT;
		}
		else
			this._type = OTHER;
	}

	/**
	 * @return the _displayName
	 * @uml.property name="_displayName"
	 */
	public String get_displayName() {
		return _displayName;
	}

	/**
	 * @param name
	 *            the _displayName to set
	 * @uml.property name="_displayName"
	 */
	public void set_displayName(String name) {
		_displayName = name;
	}

	/**
	 * @return the _value
	 * @uml.property name="_value"
	 */
	public Object get_value() {
		return _value;
	}

	/**
	 * @param _value
	 *            the _value to set
	 * @uml.property name="_value"
	 */
	private void set_value(Object _value) {
		this._value = _value;
	}

	/**
	 * @return the _name
	 * @uml.property name="_name"
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @param _name
	 *            the _name to set
	 * @uml.property name="_name"
	 */
	public void set_name(String _name) {
		this._name = _name;
	}

	/**
	 * @return the _type
	 * @uml.property name="_type"
	 */
	public int get_type() {
		return _type;
	}

	/**
	 * @param _type
	 *            the _type to set
	 * @uml.property name="_type"
	 */
	public void set_type(int _type) {
		this._type = _type;
	}

	/**
	 * @return the _defaultValues
	 * @uml.property name="_defaultValues"
	 */
	public List<String> get_defaultValues() {
		return _defaultValues;
	}

	/**
	 * @param values
	 *            the _defaultValues to set
	 * @uml.property name="_defaultValues"
	 */
	public void set_defaultValues(List<String> values) {
		_defaultValues = values;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_displayName == null) ? 0 : _displayName.hashCode());
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + _type;
		result = prime * result + ((_value == null) ? 0 : _value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
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
		final Parameter other = (Parameter) obj;
		if (_displayName == null) {
			if (other._displayName != null)
				return false;
		}
		else if (!_displayName.equals(other._displayName))
			return false;
		if (_name == null) {
			if (other._name != null)
				return false;
		}
		else if (!_name.equals(other._name))
			return false;
		if (_type != other._type)
			return false;
		if (_value == null) {
			if (other._value != null)
				return false;
		}
		else if (!_value.equals(other._value))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "[" + this._name + ";" + this._type + ";" + _displayName + ";" + _value + ",{";
		for (int i = 0; i < _defaultValues.size(); i++) {
			result += _defaultValues.get(i);
			if (i < _defaultValues.size() - 1) {
				result += ",";
			}
		}

		result += "}]";

		return result;
	}

	/**
	 * Create a HTML part of code corresponding to the parameter
	 * 
	 * @param workflowName
	 *            the name of the workflow to prefix the variable names
	 * @return the HTML corresponding to a row of a table with three columns (name, separator, value)
	 */
	public String getAsHTMLTableRowString(String workflowName) {
		StringBuffer result = new StringBuffer(32);

		// start row
		result.append("<tr>");

		// name of the paramter
		result.append("<td style=\"text-align: center; font-style:normal; font-weight:bold;\">");
		result.append(this.get_displayName());
		result.append("</td>");

		// column separator
		result.append("<td></td>");

		// append input (if boolean, use checkbox)
		result.append("<td style=\"text-align: left;\">");

		// Check if input must be a combo or an simple input
		if (this.get_type() == BOOLEAN || this._defaultValues.size() < 2) {
			result.append(this.getHTMLInput(workflowName));
		}
		else {
			result.append(this.getHTMLComboBox(workflowName));
		}

		// close tags
		result.append("</td>");
		result.append("</tr>");

		return result.toString();
	}

	/**
	 * Generate the HTML part of code of an combobox
	 * 
	 * @param workflowName
	 *            the name of the workflow to prefix the variable names
	 * @return the HTML code corresponding to an HTML input
	 */
	private Object getHTMLComboBox(String workflowName) {
		StringBuffer result = new StringBuffer(32);
		result.append("<select name=\"" + workflowName + "_" + this.get_name()
				+ "\"  onClick=\"resetStyle(this);\" paramType=\"" + this.get_type() + "\">");
		// append the default values
		for (int i = 0; i < _defaultValues.size(); i++) {
			String value = _defaultValues.get(i);
			if (StringChecker.isNotNullOrEmpty(value)) {
				result.append("<option ");
				// select first value
				if (i == 0) {
					result.append("selected");
				}
				result.append(">");
				result.append(value);
				result.append("</option>");
			}
		}
		result.append("</select>");
		return result;
	}

	/**
	 * Generate the HTML part of code of an input
	 * 
	 * @param workflowName
	 *            the name of the workflow to prefix the variable names
	 * @return the HTML code corresponding to an HTML input
	 */
	private Object getHTMLInput(String workflowName) {
		StringBuffer result = new StringBuffer(32);
		result.append("<input name=\"" + workflowName + "_" + this.get_name()
				+ "\"  onClick=\"resetStyle(this);\" paramType=\"" + this.get_type() + "\" type=\"");
		if (this.get_type() == BOOLEAN) {
			result.append("checkbox");
			result.append("\" value=\"" + Boolean.TRUE.toString() + "\"");
			// add "checked" if default value = true or if value = true
			/*
			 * if (_value != null && _value.equals(Boolean.TRUE)) { result.append(" checked"); } else {
			 */
			// check Default value (for boolean, list are not accepted)
			if (_defaultValues.size() == 1) {
				String defaultValue = _defaultValues.get(0);
				if (StringChecker.isNotNullOrEmpty(defaultValue)
						&& defaultValue.equalsIgnoreCase(Boolean.TRUE.toString())) {
					result.append(" checked");
				}
			}
			// }
		}
		else {
			result.append("text\" size=\"");
			if (this.get_type() == CHAR) {
				result.append("1");
			}
			else {
				result.append(DEFAULT_SIZE_OF_INPUT + "");
			}
			result.append("\"");

			// check if there is value or default value
			result.append(" value=\"");
			/*
			 * if (_value != null) { result.append(_value); } else {
			 */
			// if >2 then display with a combox box (selection already done)
			if (_defaultValues.size() == 1) {
				result.append(this._defaultValues.get(0));
			}
			// }
			result.append("\"");
		}
		result.append(">");

		return result;
	}

	/**
	 * Convert the given value to an Object corresponding to the type of Parameter (Boolean, Integer, ...)
	 * 
	 * @param value
	 *            the string corresponding to the value to convert
	 * @return the Object corresponding to the given value (return an unconverted value (String) if the given paremeter
	 *         is of an other type than a java primitive data type)
	 * @exception NumberFormatException
	 *                if the given string doesn't correspond to the right type of paramater
	 */
	public Object getConvertedValue(String value) throws NumberFormatException {

		switch (this.get_type()) {
			case INT:
				return new Integer(value);
			case DOUBLE:
				return new Double(value);
			case LONG:
				return new Long(value);
			case SHORT:
				return new Short(value);
			case BYTE:
				return new Byte(value);
			case FLOAT:
				return new Float(value);
			case CHAR:
				if (value.length() > 0) {
					if (value.length() == 1) {
						return new Character(value.charAt(0));
					}
					else {
						throw new NumberFormatException("The String " + value
								+ " doesn't correspond to a Character (too long)");
					}
				}
				else {
					throw new NumberFormatException("The String " + value
							+ " doesn't correspond to a Character (too short)");
				}
			case BOOLEAN:
				return new Boolean(value);
			default:
				return value;
		}
	}

	/**
	 * Get the string representation of the type
	 * 
	 * @param type
	 *            the value of the type
	 * @return the Object corresponding to the given value (return an unconverted value (String) if the given paremeter
	 *         is of an other type than a java primitive data type)
	 */
	public static String getStringValueofType(int type) {

		switch (type) {
			case INT:
				return "Integer";
			case DOUBLE:
				return "Double";
			case LONG:
				return "Long";
			case SHORT:
				return "Short";
			case BYTE:
				return "Byte";
			case FLOAT:
				return "Float";
			case CHAR:
				return "Character";
			case BOOLEAN:
				return "Boolean";
			case STRING:
				return "String";
			default:
				return "Other";
		}
	}

	/**
	 * Get the list of static variables of this class as a list of javascript constant
	 * 
	 * @return the part of javascript
	 */
	public static String getStaticConstantAsString() {
		Class thisClass = Parameter.class;
		String result = "";

		// get an instance of this class by using default constructor
		Constructor<Parameter> defaultConstructor;
		try {
			defaultConstructor = thisClass.getConstructor();
			Parameter instanceOfParameter = defaultConstructor.newInstance();

			// get the list of fields of this class
			Field[] fields = thisClass.getDeclaredFields();

			/*
			 * Generate the String containing the association name/value as javascript variables
			 */
			for (int i = 0; i < fields.length; i++) {
				// remove "LOGGER"
				if (!fields[i].getName().equals("LOGGER")) {
					// check if the field is static and final
					int modifierValue = fields[i].getModifiers();
					if (Modifier.isStatic(modifierValue) && Modifier.isFinal(modifierValue)) {
						try {
							result += "var " + fields[i].getName().toLowerCase() + "_constant = '"
									+ fields[i].get(instanceOfParameter).toString() + "';\n";
						}
						catch (IllegalArgumentException e) {
							LOGGER.info(ErrorFormatter.formatException(e));
						}
						catch (IllegalAccessException e) {
							LOGGER.info(ErrorFormatter.formatException(e));
						}
					}
				}
			}
		}
		catch (SecurityException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		catch (NoSuchMethodException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		catch (IllegalArgumentException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		catch (InstantiationException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		catch (IllegalAccessException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		catch (InvocationTargetException e) {
			LOGGER.info(ErrorFormatter.formatException(e));
		}
		return result;
	}

	/**
	 * Set the value of the parameter
	 * 
	 * @param stringValue
	 *            the value of the object as String
	 */
	public void set_value(String stringValue) {
		try {
			if (StringChecker.isNotNullOrEmpty(stringValue)) {
				this.set_value(this.getConvertedValue(stringValue));
			}
		}
		catch (NumberFormatException e) {
			LOGGER.info("NumberFormatException : " + e.getMessage());
		}
	}

	/**
	 * Generate a XML Representation of the given object.
	 * 
	 * @return the xml corresponding to this class
	 */
	public String convertInXML() {

		StringBuffer xmlString = new StringBuffer(1024);

		// get the class
		Class<Parameter> thisClass = (Class<Parameter>) this.getClass();

		// get the fields
		Field[] fields = thisClass.getDeclaredFields();

		// open tag with name of the class
		xmlString.append("<" + this.getClass().getSimpleName());
		xmlString.append(" ");

		/*
		 * Add all fields in a XML (this class has only simple tag, so use only attribute)
		 */
		for (int i = 0; i < fields.length; i++) {

			// get modifier
			int modifierValue = fields[i].getModifiers();

			// don't write static variable neither the default values
			if (!(Modifier.isStatic(modifierValue) || fields[i].getName().equals("_defaultValues"))) {

				// get field name
				String fieldName = fields[i].getName();

				// don't write if name startWith "this"
				if (!fieldName.startsWith("this")) {

					// remove "_" before variable if required
					if (fieldName.startsWith("_")) {
						xmlString.append(fieldName.substring(1));
					}
					else {
						xmlString.append(fieldName);
					}
					xmlString.append("=\"");
					try {
						Object value = fields[i].get(this);
						xmlString.append((value != null) ? value.toString() : "null");
					}
					catch (IllegalArgumentException e) {
						LOGGER.info(ErrorFormatter.formatException(e));
					}
					catch (IllegalAccessException e) {
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

	/**
	 * Get a simple HTML code representing the Parameter
	 * 
	 * @return the HTML string corresponding to the parameter
	 */
	public String getHTML() {
		String result = this._displayName + " - " + Parameter.getStringValueofType(this._type);
		if (_defaultValues == null || _defaultValues.size() == 0) {
			return result;
		}
		else {
			result += " - {";
			for (int i = 0; i < _defaultValues.size(); i++) {
				result += _defaultValues.get(i);
				if (i < _defaultValues.size() - 1) {
					result += ",";
				}
			}
		}

		result += "}";

		return result;
	}

}
