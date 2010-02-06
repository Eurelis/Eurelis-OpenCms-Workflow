/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsLog;

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.eurelis.opencms.workflows.workflows.security.AllowedPeople;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This class implement a individual action that can be use during a workflow. <br/>
 * This function modify the dynamics rights associated to an instance of WF<br/>
 * <br/>
 * The required parameters to use this function is only the parameter "changetoapply" (cf
 * {@link ChangeAllowedPeopleFunction#PARAM_CHANGETOAPPLY}). This parameter contains a list of change to apply split by
 * ";". The syntax to use for each element is : <b>propertyType:userGroupRole:NameOrProperty:newRight</b> <br/>
 * <br/>
 * <u><i>propertyType</i></u> can be :
 * <ul>
 * <li><i>opencms</i> : the property is a OpenCms object (Role/User/Role)</li>
 * <li><i>instance</i> : the property is attached to an instance of workflow. <b>BE CAREFULL</b> the only property knows
 * for this type is "author" (the author of the wf) and "lastactor" (the actor of the last action)</li>
 * <li><i>parameter</i> : the modification is apply to the value (or list of value split with ";") contains in a
 * parameter of the instance of workflow. Theses values MUST BE OpenCms login.</li>
 * <li><i>property</i> : the modification is apply to the value (or list of value split with ";") contains in a property
 * of files associated to the instance of workflow. Theses values MUST BE OpenCms login.</li>
 * </ul>
 * <br/>
 * <br/>
 * <u><i>userGroupRole</i></u> can be :
 * <ul>
 * <li><i>u</i> : the modification is apply to a user</li>
 * <li><i>g</i> : the modification is apply to a group</li>
 * <li><i>r</i> : the modification is apply to a role</li>
 * </ul>
 * <br/>
 * <br/>
 * <u><i>NameOrProperty</i></u> is the name of the user/role/group on witch you can apply the modification if
 * propertyType is "opencms". In that case it could also be the pattern name if it starts with "{" and ends with "}". If
 * propertytype is not "opencms" NameOrProperty contains the name of the property/parameter to deal with. <br/>
 * <br/>
 * <u><i>newRight</i></u> is the new value to attribut for rights. It must contains a "c" if you want that the user can
 * have creation right on this instance of WF (useless), a "r" for read right and "w" for write rights. <br/>
 * <br/>
 * <u><i>Some examples</i></u>
 * <ul>
 * <li><b>opencms:u:Toto:rw</b> the user with login Toto receive the read/write rights</li>
 * <li><b>opencms:r:Root Administrator:w</b> the role Root Administrator receive the write rights</li>
 * <li><b>opencms:u:{^a*$}:r</b> all user there login starts with a "a" receive the read rights</li>
 * <li><b>c</b> all group there names starts with a "a" have no more rights</li>
 * <li><b>instance:u:author:r</b> the author of instance of workflow receive the read rights</li>
 * <li><b>parameter:u:receivers:rw</b> the list of people indicate in the parameter "receivers" of the instance of
 * workflow will receive the read/write rights</li>
 * </ul>
 * <br/>
 * <br/>
 * <b><u><font color="red">Remarque :</font></u></b> Les règles de changement de droits sont appliquées dans l'ordre
 * d'écriture. Pour éviter tout problème, il est fortement recommandé d'écrire l'ensemble des règles resteignants les
 * droits avant celles les ouvrants.
 * 
 * @author Sébastien Bianco
 */
public class ChangeAllowedPeopleFunction extends EurelisWorkflowFunction {

	/** The log object for this class. */
	private static final Log	LOGGER						= CmsLog.getLog(ChangeAllowedPeopleFunction.class);

	/**
	 * The name of the parameter to look for
	 */
	private static final String	PARAM_CHANGETOAPPLY			= "changeToApply";

	/**
	 * A particular value for property type (opencms)
	 */
	private static final String	PROPERTYTYPE_OPENCMS		= "opencms";

	/**
	 * A particular value for property type (instance)
	 */
	private static final String	PROPERTYTYPE_INSTANCE		= "instance";

	/**
	 * A particular value for property type (parameter)
	 */
	private static final String	PROPERTYTYPE_PARAMETER		= "parameter";

	/**
	 * A particular value for property type (property)
	 */
	private static final String	PROPERTYTYPE_PROPERTY		= "property";

	/**
	 * The particular value for parameter name if property type is instance (author)
	 */
	private static final String	PROPERTYNAME_AUTHOR			= "author";

	/**
	 * The particular value for parameter name if property type is instance (lastactor)
	 */
	private static final String	PROPERTYNAME_LASTACTOR		= "lastactor";

	/**
	 * A particular value for userGroupRole (user)
	 */
	private static final String	USERGROUPROLE_USER			= "u";

	/**
	 * A particular value for userGroupRole (group)
	 */
	private static final String	USERGROUPROLE_GROUP			= "g";

	/**
	 * A particular value for userGroupRole (role)
	 */
	private static final String	USERGROUPROLE_ROLE			= "r";

	/**
	 * The string that starts the description of a Pattern
	 */
	private static final String	PATTERN_STARTING_SUBSTRING	= ModuleConfigurationLoader.getConfiguration().PATTERN_STARTING_SUBSTRING;

	/**
	 * The string that ends the description of a Pattern
	 */
	private static final String	PATTERN_ENDING_SUBSTRING	= ModuleConfigurationLoader.getConfiguration().PATTERN_ENDING_SUBSTRING;

	/**
	 * The string use to separate each part of the property
	 */
	private static final String	PART_SEPARATOR				= ":";

	/**
	 * the allowed people
	 */
	private AllowedPeople		_allowedPeople;

	/*
	 * (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.functions.EurelisWorkflowFunction#executeFunction(java.util.Map,
	 * java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	@Override
	protected void executeFunction(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

		if (this._propertyContainer != null) {

			if (this._propertyContainer.get_allowedPeople() != null) {
				this._allowedPeople = this._propertyContainer.get_allowedPeople();
			}
			else {
				this._allowedPeople = new AllowedPeople();
			}

			// LOGGER.debug("WF | avant allowedPeople = " + this._allowedPeople);

			String parameterValue = (String) args.get(PARAM_CHANGETOAPPLY);
			if (StringChecker.isNotNullOrEmpty(parameterValue)) {

				// get the list of value to treat
				String[] valueToTreat = parameterValue.trim().split(
						ModuleConfigurationLoader.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR);

				/*
				 * Treat each value
				 */
				for (int i = 0; i < valueToTreat.length; i++) {

					if (StringChecker.isNotNullOrEmpty(valueToTreat[i])) {

						// get each part of the code to dipatch treatement
						String[] codesParts = valueToTreat[i].trim().split(PART_SEPARATOR);
						if (codesParts.length > 3) {

							// check first part of the code to get the type of treatment to apply to
							if (codesParts[0].equalsIgnoreCase(PROPERTYTYPE_INSTANCE)) {
								this.applyInstancePropertyRights(codesParts);
							}
							else if (codesParts[0].equalsIgnoreCase(PROPERTYTYPE_OPENCMS)) {
								this.applyOpenCmsRights(codesParts);
							}
							else if (codesParts[0].equalsIgnoreCase(PROPERTYTYPE_PARAMETER)) {
								this.applyParametersRights(codesParts);
							}
							else if (codesParts[0].equalsIgnoreCase(PROPERTYTYPE_PROPERTY)) {
								this.applyPropertyRights(codesParts);
							}
							else {
								LOGGER.warn("The receive PropertyType is unknown: " + codesParts[0]);
							}
						}
						else {
							LOGGER.warn("The code " + valueToTreat[i] + " is not valid (length<4)");
						}
					}
				}

				// LOGGER.debug("WF | après allowedPeople = " + this._allowedPeople);

				// save change about allowed people
				this._propertyContainer.set_allowedPeople(this._allowedPeople);

			}
			else {
				LOGGER.warn(this.getClass() + " doesn't receive any argument " + PARAM_CHANGETOAPPLY);
			}

		}
		else {
			LOGGER.warn("The property container is null");
		}

	}

	/**
	 * Extract informations and apply new rights if the property type is "property"
	 * 
	 * @param codesParts
	 *            the complete code to treat;
	 */
	private void applyPropertyRights(String[] codesParts) {
		if (codesParts.length > 2) {
			String propertyName = codesParts[2];
			if (this._associatedFiles != null && !this._associatedFiles.isEmpty()) {

				/*
				 * collect property for each files
				 */
				Iterator<String> listOfFilesIterator = this._associatedFiles.iterator();
				while (listOfFilesIterator.hasNext()) {
					String filePath = listOfFilesIterator.next();

					CmsResource resource = OpenCmsEasyAccess.getResource(filePath);

					if (resource != null) {
						// get the property object
						CmsProperty propertyObject = OpenCmsEasyAccess.getProperty(propertyName, resource);

						// check Property
						if (propertyObject != null) {
							String propertyValue = propertyObject.getValue();
							if (StringChecker.isNotNullOrEmpty(propertyValue)) {

								String[] arrayOfLogin = propertyValue.split(ModuleConfigurationLoader
										.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR);

								// apply the new rights
								this.associateRightsToAListOfThings(arrayOfLogin, codesParts[1], codesParts[3]);

							}
							else {
								LOGGER.warn("The property " + propertyName + " for " + filePath
										+ " has an invalid value");
							}
						}
						else {
							LOGGER.warn("The property " + propertyName + " has not been found for " + filePath);
						}
					}
					else {
						LOGGER.warn("The resource " + filePath + " has not been found");
					}
				}

			}
			else {
				LOGGER
						.info("The current instance of WF don't have any associated files. The required change cannot be apply.");
			}

		}
		else {
			LOGGER.warn("The parameter name has not been given ");
		}

	}

	/**
	 * Extract informations and apply new rights if the property type is "parameter"
	 * 
	 * @param codesParts
	 *            the complete code to treat;
	 */
	private void applyParametersRights(String[] codesParts) {
		if (codesParts.length > 2) {
			String parameterName = codesParts[2];
			if (this._propertyContainer != null) {
				if (this._propertyContainer.get_parameters() != null
						&& !this._propertyContainer.get_parameters().isEmpty()) {
					// get the value of parameter
					Parameter parameter = this._propertyContainer.get_parameters(parameterName);
					if (parameter != null) {
						Object parameterValue = parameter.get_value();

						// check value
						if (parameterValue instanceof String) {
							String listOfEmails = (String) parameterValue;
							if (StringChecker.isNotNullOrEmpty(listOfEmails)) {

								String[] arrayOfLogin = listOfEmails
										.split(ModuleConfigurationLoader.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR);

								// apply the new rights
								this.associateRightsToAListOfThings(arrayOfLogin, codesParts[1], codesParts[3]);

							}
							else {
								LOGGER.warn("The parameter " + parameterName + " contains an empty String");
							}
						}
						else {
							LOGGER.warn("The parameter " + parameterName
									+ " has not been found in the list of paremeters");
						}
					}
					else {
						LOGGER.warn("The parameter " + parameterName + " has not been found in the list of paremeters");
					}
				}
				else {
					LOGGER.warn("The property container do not contain any parameter");
				}
			}
			else {
				LOGGER.warn("No property container has been found for the instance of WF");
			}
		}
		else {
			LOGGER.warn("The parameter name has not been given ");
		}
	}

	/**
	 * Associate the rights to a number of login (can be pattern)
	 * 
	 * @param arrayOfLogin
	 *            the array of login to treat
	 * @param typeOfLogin
	 *            the type of login (groupe/role/user)
	 * @param rightToGive
	 *            the new rights to give
	 */
	private void associateRightsToAListOfThings(String[] arrayOfLogin, String typeOfLogin, String rightToGive) {

		/*
		 * Treat each login
		 */
		for (int i = 0; i < arrayOfLogin.length; i++) {
			String login = arrayOfLogin[i];
			boolean isAPattern = login.startsWith(PATTERN_STARTING_SUBSTRING);

			if (isAPattern) {
				login = login.substring(PATTERN_STARTING_SUBSTRING.length(), login.length()
						- PATTERN_ENDING_SUBSTRING.length());
				this.associateRightsToAPattern(login, typeOfLogin, rightToGive);
			}

			this.associateRights(login, typeOfLogin, rightToGive);

		}

	}

	/**
	 * Associate the rights to a login
	 * 
	 * @param login
	 *            the name of the login
	 * @param typeOfLogin
	 *            the type of login (groupe/role/user)
	 * @param rightToGive
	 *            the new rights to give
	 */
	private void associateRights(String login, String typeOfLogin, String rightToGive) {
		if (this._propertyContainer != null) {

			/*
			 * AllocateRights
			 */
			if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_USER)) {
				this._allowedPeople.updateAllowedUser(login, rightToGive);
			}
			else if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_GROUP)) {
				this._allowedPeople.updateAllowedGroup(login, rightToGive);
			}
			else if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_ROLE)) {
				this._allowedPeople.updateAllowedRole(login, rightToGive);
			}
			else {
				LOGGER.warn("The type of User/Group/Role entered (" + typeOfLogin + ") is not accepted by the system");
			}
		}
		else {
			LOGGER.warn("The property container associated to this instance of WF is null");
		}

	}

	/**
	 * Associate the rights to a login (a pattern)
	 * 
	 * @param patternName
	 *            the name of the pattern
	 * @param typeOfLogin
	 *            the type of login (groupe/role/user)
	 * @param rightToGive
	 *            the new rights to give
	 */
	private void associateRightsToAPattern(String patternName, String typeOfLogin, String rightToGive) {
		if (this._propertyContainer != null) {

			/*
			 * AllocateRights
			 */
			if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_USER)) {
				this._allowedPeople.updateAllowedUserPattern(patternName, rightToGive);
			}
			else if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_GROUP)) {
				this._allowedPeople.updateAllowedGroupPattern(patternName, rightToGive);
			}
			else if (typeOfLogin.equalsIgnoreCase(USERGROUPROLE_ROLE)) {
				this._allowedPeople.updateAllowedRolePattern(patternName, rightToGive);
			}
			else {
				LOGGER.warn("The type of User/Group/Role entered (" + typeOfLogin + ") is not accepted by the system");
			}
		}
		else {
			LOGGER.warn("The property container associated to this instance of WF is null");
		}
	}

	/**
	 * Extract informations and apply new rights if the property type is "opencms"
	 * 
	 * @param codesParts
	 *            the complete code to treat;
	 */
	private void applyOpenCmsRights(String[] codesParts) {
		String[] arrayOfLogin = new String[] { codesParts[2] };

		// apply the new rights
		this.associateRightsToAListOfThings(arrayOfLogin, codesParts[1], codesParts[3]);
	}

	/**
	 * Extract informations and apply new rights if the property type is "instance"
	 * 
	 * @param codesParts
	 *            the complete code to treat;
	 */
	private void applyInstancePropertyRights(String[] codesParts) {
		String propertyName = codesParts[2];

		if (this._propertyContainer != null) {
			if (this._propertyContainer.get_parameters() != null && !this._propertyContainer.get_parameters().isEmpty()) {

				if (propertyName.equalsIgnoreCase(PROPERTYNAME_AUTHOR)) {

					// apply the new rights
					this.associateRightsToAListOfThings(new String[] { this._propertyContainer.getCreator() },
							codesParts[1], codesParts[3]);

				}
				else if (propertyName.equals(PROPERTYNAME_LASTACTOR)) {

					// apply the new rights
					this
							.associateRightsToAListOfThings(new String[] { this._propertyContainer
									.getOwner(this._propertyContainer.getNumberOfActions() - 1) }, codesParts[1],
									codesParts[3]);

				}
				else {
					LOGGER.warn("The property with name" + propertyName + " is not supported");
				}

			}
			else {
				LOGGER.warn("The property container do not contain any parameter");
			}
		}
		else {
			LOGGER.warn("No property container has been found for the instance of WF");
		}
	}
}
