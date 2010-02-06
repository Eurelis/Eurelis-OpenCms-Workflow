/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsUser;
import org.opencms.mail.CmsMailHost;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;

import com.eurelis.opencms.workflows.moduleconfiguration.ModuleConfigurationLoader;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.ModuleSharedVariables;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.util.StringChecker;
import com.eurelis.opencms.workflows.workflows.OSWorkflowManager;
import com.eurelis.opencms.workflows.workflows.Parameter;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.WorkflowEntry;

/**
 * This class implement a individual action that can be use during a workflow. <br/>
 * This function notify by email that an action is required.<br/>
 * <br/>
 * The required parameters to use this function is :
 * <ol>
 * <li><b>The list of files:</b> This list must be stored in the transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_ASSOCIATEDFILELIST} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li><b>The CmsOject instance:</b> This object must be stored in the transcientVar map with the key
 * {@link ModuleSharedVariables#FUNCTION_INPUTARGUMENT_CMSOBJECT} (done by
 * {@link OSWorkflowManager#createInstanceOfWorkflow(String, String, String, List, CmsObject)})</li>
 * <li><b>The receivers:</b> This value is indicated in the description of the workflow as parameter, and so will be
 * stored in the Map args (cf {@link EmailNotificationFunction#PARAM_RECEIVERS})</li>
 * <li><b>The message body of the email:</b> This value is indicated in the description of the workflow as parameter,
 * and so will be stored in the Map args (cf {@link EmailNotificationFunction#PARAM_EMAIL_BODY}). This parameter is
 * optionnal. If it is not set, a default message will be used</li>
 * </ol>
 * <br/>
 * The receiver value is a list of email separated with a ";". It could also be
 * <ul>
 * <li><b>a cms property</b>. In that case, use "opencms:NameOfTheProperty" to set the name of the property to look for.
 * Be careful, the value of the property <b>MUST BE</b> the name (login) of a user that have a valid email. <br/>
 * </li>
 * <li><b>a workflow parameter</b>. In that case, use "parameter:NameOfTheParameter" to set the name of the parameter to
 * look for. Be careful, the value of the parameter <b>MUST BE</b> a list of OpenCms login (split with ";") that have a
 * valid email.</li>
 * <li><b>a workflow instance property</b>. In that case, use "instance:NameOfTheProperty" to set the name of the
 * property to look for. The only property currently available is "author".</li>
 * </ul>
 * <u>Example:</u> toto@eurelis.com;titi@myCompagny.com;opencms:reviewer;parameter:receivers;instance:author<br/>
 * <br/>
 * The message body can contains some keywords that will be converted into there value :
 * <ul>
 * <li>"[[initialcomment]]" : the comment entered when created the instance of WF</li>
 * <li>"[[lastcomment]]" : the last comment entered</li>
 * <li>"[[lastlastcomment]]" : the comment entered two actions before</li>
 * <li>"[[creator]]" : the login of the one who created the instance of WF</li>
 * <li>"[[lastactor]]" : the actor of the last action</li>
 * <li>"[[lastlastactor]]" : the actor of the action done two action before</li>
 * </ul>
 * 
 * @author Sébastien Bianco
 */
public class EmailNotificationFunction extends EurelisWorkflowFunction {

	/**
	 * This class implement a Thread dedicated to the mail sending
	 * 
	 * @author Sébastien Bianco
	 */
	private class SendThread extends Thread {

		/**
		 * The properties object containing informations for smtp
		 */
		private Properties	_props		= null;

		/**
		 * The email address of the receiver
		 */
		private String		_receiver	= "";

		/**
		 * The sender of the mail
		 */
		private String		_sender		= "";

		/**
		 * The email subject
		 */
		private String		_subject	= "";

		/**
		 * The email content
		 */
		private String		_content	= "";

		/**
		 * @param props
		 * @param receiver
		 * @param sender
		 * @param subject
		 * @param content
		 */
		public SendThread(Properties props, String receiver, String sender, String subject, String content) {
			super();
			this._props = props;
			this._receiver = receiver;
			this._sender = sender;
			this._subject = subject;
			this._content = content;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {

			try {
				Session s = Session.getInstance(_props, null);

				InternetAddress from = new InternetAddress(_sender);
				InternetAddress to = new InternetAddress(_receiver);

				MimeMessage message = new MimeMessage(s);
				message.setFrom(from);
				message.addRecipient(Message.RecipientType.TO, to);

				message.setSubject(_subject);
				message.setText(_content);
				Transport.send(message);
			}
			catch (MessagingException e) {
				LOGGER.warn("The notification message has not be sent to " + _receiver + " (" + e.getMessage() + ")");
			}

		}

	}

	/** The log object for this class. */
	private static final Log			LOGGER							= CmsLog
																				.getLog(EmailNotificationFunction.class);

	/**
	 * The name of property to look for in the map args to get the list of receivers
	 */
	private static final String			PARAM_RECEIVERS					= "receivers";

	/**
	 * The name of property to look for in the map args to get the body of the email
	 */
	private static final String			PARAM_EMAIL_BODY				= "email_body";

	/**
	 * The name of property to look for in the map args to get the body of the email
	 */
	private static final String			PARAM_EMAIL_SUBJECT				= "email_subject";

	/**
	 * The default text to use as email body
	 */
	private static final String			DEFAULT_EMAIL_BODY				= "";

	/**
	 * The String use in given email addresses to indicate that it is a OpenCms property to treat
	 */
	private static final String			OPENCMSPROPERTY_STARTSTRING		= "opencms:";

	/**
	 * The String use in given email addresses to indicate that it is a workflow property to treat
	 */
	private static final String			WORKFLOWPROPERTY_STARTSTRING	= "instance:";

	/**
	 * The String use in given email addresses to indicate that it is a workflow parameter to treat
	 */
	private static final String			WORKFLOWPARAMETER_STARTSTRING	= "parameter:";

	/**
	 * The Regex of a valid email
	 */
	private static final String			EMAIL_PATTERN					= "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$";

	/**
	 * The subject of the mail send as notification
	 */
	private static final String			DEFAULT_EMAIL_SUBJET			= "[EurelisWorkflow] A workflow required your intervention...";

	/**
	 * The default sender use in the field from of notification
	 */
	private static final String			MAIL_DEFAULTSENDER				= "workflow@noreply.com";

	/**
	 * the keyword author
	 */
	private static final String			KEYWORD_AUTHOR					= "[[creator]]";

	/**
	 * the keyword lastactor
	 */
	private static final String			KEYWORD_LASTACTOR				= "[[lastactor]]";

	/**
	 * the keyword lastlastactor
	 */
	private static final String			KEYWORD_LASTLASTACTOR			= "[[lastlastactor]]";

	/**
	 * the keyword comment
	 */
	private static final String			KEYWORD_COMMENT					= "[[initialcomment]]";

	/**
	 * the keyword lastcomment
	 */
	private static final String			KEYWORD_LASTCOMMENT				= "[[lastcomment]]";

	/**
	 * the keyword lastlastcomment
	 */
	private static final String			KEYWORD_LASTLASTCOMMENT			= "[[lastlastcomment]]";

	/**
	 * The property author
	 */
	private static final String			PROPERTY_AUTHOR					= "author";

	/**
	 * The body of the email.
	 */
	private String						_email_body						= DEFAULT_EMAIL_BODY;

	/**
	 * The subject of the email.
	 */
	private String						_email_subject					= DEFAULT_EMAIL_SUBJET;

	/**
	 * The map that associated for each reviewer the list of files relative to the reviewer
	 */
	private Map<String, List<String>>	_resourcesPerReviewer			= new HashMap<String, List<String>>();

	/**
	 * Execute this function
	 * 
	 * @param transientVars
	 *            Variables that will not be persisted. These include inputs given in the {@link Workflow#initialize}
	 *            and {@link Workflow#doAction} method calls. There are a number of special variable names:
	 *            <ul>
	 *            <li><code>entry</code>: (object type: {@link com.opensymphony.workflow.spi.WorkflowEntry}) The
	 *            workflow instance
	 *            <li><code>context</code>: (object type: {@link com.opensymphony.workflow.WorkflowContext}). The
	 *            workflow context.
	 *            <li><code>actionId</code>: The Integer ID of the current action that was take (if applicable).
	 *            <li><code>currentSteps</code>: A Collection of the current steps in the workflow instance.
	 *            <li><code>store</code>: The {@link com.opensymphony.workflow.spi.WorkflowStore}.
	 *            <li><code>descriptor</code>: The {@link com.opensymphony.workflow.loader.WorkflowDescriptor}.
	 *            </ul>
	 *            Also, any variable set as a {@link com.opensymphony.workflow.Register}), will also be available in the
	 *            transient map, no matter what. These transient variables only last through the method call that they
	 *            were invoked in, such as {@link Workflow#initialize} and {@link Workflow#doAction}.
	 * @param args
	 *            The properties for this function invocation. Properties are created from arg nested elements within
	 *            the xml, an arg element takes in a name attribute which is the properties key, and the CDATA text
	 *            contents of the element map to the property value.
	 * @param ps
	 *            The persistent variables that are associated with the current instance of the workflow. Any change
	 *            made to the propertyset are persisted to the propertyset implementation's persistent store.
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map, java.util.Map,
	 *      com.opensymphony.module.propertyset.PropertySet)
	 */
	public void executeFunction(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

		// Get the workflow object
		WorkflowEntry workflow = (WorkflowEntry) transientVars.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_ENTRY);

		// get the workflow descriptor
		WorkflowDescriptor descriptor = (WorkflowDescriptor) transientVars
				.get(ModuleSharedVariables.FUNCTION_INPUTARGUMENT_DESCRIPTOR);

		// get the subject and the body of the email
		String entered_body = (String) args.get(PARAM_EMAIL_BODY);
		if (StringChecker.isNotNullOrEmpty(entered_body)) {
			//LOGGER.debug("WF | entered body = " + entered_body);
			_email_body = this.treatEmailBody(entered_body.trim());
		}
		String entered_subject = (String) args.get(PARAM_EMAIL_SUBJECT);
		if (StringChecker.isNotNullOrEmpty(entered_subject)) {
			_email_subject = entered_subject.trim();
		}

		// Get the list of receiver encoded in a String
		String receiverString = ((String) args.get(PARAM_RECEIVERS)).trim();
		if (StringChecker.isNotNullOrEmpty(receiverString)) {
			List<String> receivers = this.extractListOfReceivers(receiverString, _cmsObject, _associatedFiles);

			/*
			 * get smtp
			 */
			String smtp = ((CmsMailHost) OpenCms.getSystemInfo().getMailSettings().getMailHosts().get(0)).getHostname();
			Properties props = new Properties();
			props.put("mail.smtp.host", smtp);

			/*
			 * Send emails
			 */
			Iterator<String> emailsIterator = receivers.iterator();
			while (emailsIterator.hasNext()) {
				String email = emailsIterator.next();
				try {
					this.sendEmail(props, email, this._email_subject, this.generateEmailText(email, _associatedFiles,
							workflow, descriptor), MAIL_DEFAULTSENDER);
				}
				catch (MessagingException e) {
					LOGGER.warn("The notification message has not be sent to " + email + " (" + e.getMessage() + ")");
				}
			}
		}
		else {
			LOGGER.warn("The function " + this.getClass().getName() + " doesn't receive a valid parameter "
					+ PARAM_RECEIVERS);
		}

	}

	/**
	 * This method check the content of the email body entered by user to check some keywords. This keywords are changed
	 * into the value it represents.
	 * 
	 * @param email_body
	 * @return the email body with converted special values
	 */
	private String treatEmailBody(String email_body) {
		String str = email_body;

		//LOGGER.debug("WF | (avant) email_body = " + email_body);

		if (_propertyContainer != null) {

			// replace author
			if (str.contains(KEYWORD_AUTHOR)) {
				str = str.replaceAll(convertToRegex(KEYWORD_AUTHOR), _propertyContainer.getCreator());
			}

			// replace last actor
			if (str.contains(KEYWORD_LASTACTOR)) {
				str = str.replaceAll(convertToRegex(KEYWORD_LASTACTOR), _propertyContainer.getOwner(_propertyContainer
						.getNumberOfActions() - 1));
			}

			// replace last last actor
			if (str.contains(KEYWORD_LASTLASTACTOR)) {
				str = str.replaceAll(convertToRegex(KEYWORD_LASTLASTACTOR), _propertyContainer
						.getOwner(_propertyContainer.getNumberOfActions() - 2));
			}

			// replace comment
			if (str.contains(KEYWORD_COMMENT)) {
				str = str.replaceAll(convertToRegex(KEYWORD_COMMENT), _propertyContainer.getComment(0));
			}

			// replace lastcomment
			if (str.contains(KEYWORD_LASTCOMMENT)) {
				str = str.replaceAll(convertToRegex(KEYWORD_LASTCOMMENT), _propertyContainer
						.getComment(_propertyContainer.getNumberOfActions() - 1));
			}

			// replace lastlastcomment
			if (str.contains(KEYWORD_LASTLASTCOMMENT)) {
				str = str.replaceAll(convertToRegex(KEYWORD_LASTLASTCOMMENT), _propertyContainer
						.getComment(_propertyContainer.getNumberOfActions() - 2));
			}

		}
		else {
			LOGGER.info("The property container is null");
		}
		//LOGGER.debug("WF | (apres) email_body = " + str);
		return str;
	}

	/**
	 * Convert the key word into a regex (protect regex special char with \)
	 * 
	 * @param keyword
	 *            the keyword to convert
	 * @return the corresponding regex
	 */
	private String convertToRegex(String keyword) {
		String str = keyword;
		char charToReplace[] = new char[] { '$', '[', ']', '{', '}' };
		for (int i = 0; i < charToReplace.length; i++) {
			if (str.contains(charToReplace[i]+"")) {
				str = replaceChar(str, charToReplace[i]);
			}
			//LOGGER.debug("WF | "+i+" str = " + str);
		}
		return str.toString();
	}

	/**
	 * Replace a character in the string buffer
	 * 
	 * @param buffer
	 *            the string buffer to change
	 * @param character
	 *            the character to update
	 * @return the modified string buffer
	 */
	private String replaceChar(String buffer, char character) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < buffer.length(); i++) {
			if (buffer.charAt(i) == character) {
				result.append("\\" + character);
			}
			else {
				result.append(buffer.charAt(i));
			}
		}
		return result.toString();
	}

	/**
	 * Send notification message
	 * 
	 * @param props
	 *            the propery object containing configuration of smtp
	 * @param email
	 *            the email address to send to
	 * @param defaultSender
	 *            the address to set in "from" field
	 * @param subject
	 *            the subject of the notification
	 * @param mailContent
	 *            the content of the notification
	 * @throws MessagingException
	 *             if a problem occurs during sending process
	 */
	private void sendEmail(Properties props, String email, String subject, String mailContent, String defaultSender)
			throws MessagingException {
		// LOGGER.debug("WF | send email to : "+email);
		new SendThread(props, email, defaultSender, subject, mailContent).start();
	}

	/**
	 * Generate the content of the notification mail
	 * 
	 * @param email
	 *            the email address of receiver
	 * @param descriptor
	 *            the workflow descriptor object
	 * @param workflow
	 *            the workflow entry object
	 * @param listOfFiles
	 *            the list of files associated to this action
	 * @return the text content of the message to send
	 */
	private String generateEmailText(String email, List<String> listOfFiles, WorkflowEntry workflow,
			WorkflowDescriptor descriptor) {
		StringBuffer result = new StringBuffer();

		if (StringChecker.isNotNullOrEmpty(this._email_body)) {
			result.append(this._email_body + "\n\n");
			result.append("-----------------------------------------------------------------------\n\n");
		}

		result.append("The workflow #" + workflow.getId() + " (" + descriptor.getName() + ") require action.\n");

		// display list of files
		if (listOfFiles.size() > 0) {
			result.append("The files concerned by this action are :");
			Iterator<String> listOfFilesIterator = listOfFiles.iterator();
			while (listOfFilesIterator.hasNext()) {
				result.append("\n\t- " + listOfFilesIterator.next());
			}
		}

		result.append("\n");
		result.append("This message has been automatically sent by an engine, please do not reply to this email");
		return result.toString();
	}

	/**
	 * Extract the list of receivers from the given recieverString that contains the list of address email concatened
	 * with a ";". It contains also some opencms properties name that need to be collected and converted into email
	 * address thanks opencms user manager.
	 * 
	 * @param receiverString
	 *            the list of receiver "encoded"
	 * @param cmsObject
	 *            the cmsObject that will allow to get some information into openCms
	 * @param listOfFiles
	 *            the list of files associated to the workflow
	 * @return the list of email address of receivers, an <i>Empty list</i> if none address are valid
	 */
	private List<String> extractListOfReceivers(String receiverString, CmsObject cmsObject, List<String> listOfFiles) {

		// get all sub string (remove separator)
		String[] arrayOfEmails = receiverString
				.split(ModuleConfigurationLoader.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR);

		/*
		 * Treat each email and check that it is a valid email. If not, check that it's not a property. In the case that
		 * is a property, treat it to get the email address contained into opencms account manager.
		 */
		for (int addressIndex = 0; addressIndex < arrayOfEmails.length; addressIndex++) {
			String emailAddress = arrayOfEmails[addressIndex];
			// remove the "return" if there is one
			if (emailAddress.startsWith("\n")) {
				emailAddress = emailAddress.substring(1);
			}
			// check that it is not a opencms property (kw = opencms)
			else if (emailAddress.startsWith(OPENCMSPROPERTY_STARTSTRING)) {
				this.extractEmailAddressFromProperty(emailAddress.trim()
						.substring(OPENCMSPROPERTY_STARTSTRING.length()), cmsObject, listOfFiles);
			}
			// check that it is not a workfklow property (kw = instance)
			else if (emailAddress.startsWith(WORKFLOWPROPERTY_STARTSTRING)) {
				this.extractEmailAddressFromWorkflowProperty(emailAddress.trim().substring(
						WORKFLOWPROPERTY_STARTSTRING.length()), cmsObject);
			}
			// check that it is not a workfklow parameter (kw = parameter)
			else if (emailAddress.startsWith(WORKFLOWPARAMETER_STARTSTRING)) {
				this.extractEmailAddressFromParameter(emailAddress.trim().substring(
						WORKFLOWPARAMETER_STARTSTRING.length()), cmsObject);
			}
			else {
				// check that it is a valid email address
				if (Pattern.matches(EMAIL_PATTERN, emailAddress)) {
					// add associated files to the reviewer
					this.addResourcesInMap(emailAddress, listOfFiles);
				}
				else {
					LOGGER.warn("The email address " + emailAddress + " is not valid.");
				}
			}
		}
		return new ArrayList<String>(_resourcesPerReviewer.keySet());
	}

	/**
	 * Extract the email from a property
	 * 
	 * @param propertyName
	 *            the name of the property to consider
	 * @param cmsObject
	 *            the cmsObject that will allow to get some information into openCms
	 * @return the list of email address corresponding to the parameter
	 */
	private List<String> extractEmailAddressFromWorkflowProperty(String propertyName, CmsObject cmsObject) {
		List<String> collectedEmails = new ArrayList<String>();
		if (this._propertyContainer != null) {
			if (this._propertyContainer.get_listOfElements() != null
					|| !this._propertyContainer.get_listOfElements().isEmpty()) {

				if (propertyName.equalsIgnoreCase(PROPERTY_AUTHOR)) {
					String creator = this._propertyContainer.getCreator();
					// Get the email value from account manager
					String email = this.getEmailAddressFromAccountManager(creator, cmsObject);

					if (StringChecker.isNotNullOrEmpty(email)) {
						// add email in the list of result
						collectedEmails.add(email);
						// add resources in the map of resources per reviewer
						this.addResourceInMap(email, "");
					}
				}
				else {
					LOGGER.warn("The property with name " + WORKFLOWPROPERTY_STARTSTRING + propertyName
							+ " is not yet supported");
				}

			}
			else {
				LOGGER.warn("The property container do not contain any historic elements");
			}
		}
		else {
			LOGGER.warn("The property container do not contain any parameter");
		}

		return collectedEmails;
	}

	/**
	 * Extract the email from a parameter (suppose to be a list of OpenCms login split with ";")
	 * 
	 * @param parameterName
	 *            the name of the parameter to consider
	 * @param cmsObject
	 *            the cmsObject that will allow to get some information into openCms
	 * @return the list of email address corresponding to the parameter
	 */
	private List<String> extractEmailAddressFromParameter(String parameterName, CmsObject cmsObject) {
		List<String> collectedEmails = new ArrayList<String>();

		if (this._propertyContainer != null) {
			if (this._propertyContainer.get_parameters() != null && !this._propertyContainer.get_parameters().isEmpty()) {
				// get the value of parameter
				Parameter parameter = this._propertyContainer.get_parameters(parameterName);
				if (parameter != null) {
					Object parameterValue = parameter.get_value();

					// check value
					if (parameterValue instanceof String) {
						String listOfEmails = (String) parameterValue;
						if (StringChecker.isNotNullOrEmpty(listOfEmails)) {

							String[] arrayOfEmail = listOfEmails
									.split(ModuleConfigurationLoader.getConfiguration().WORKFLOW_RIGHTCONFIGFILE_PARAMETER_SEPARATOR);
							for (int i = 0; i < arrayOfEmail.length; i++) {
								// Get the email value from account manager
								String email = this.getEmailAddressFromAccountManager(arrayOfEmail[i], cmsObject);
								if (StringChecker.isNotNullOrEmpty(email)) {
									// add email in the list of result
									collectedEmails.add(email);
									// add resources in the map of resources per reviewer
									this.addResourceInMap(email, "");
								}
							}

						}
						else {
							LOGGER.warn("The parameter " + parameterName + " contains an empty String");
						}
					}
					else {
						LOGGER.warn("The parameter " + parameterName + " has not been found in the list of paremeters");
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
		return collectedEmails;
	}

	/**
	 * Get the email from a given property using the Opencms account manager
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param cmsObject
	 *            cmsObject the cmsObject that will allow to get some information into openCms
	 * @param listOfFiles
	 *            the list of files associated to the workflow
	 * @return the list of email address corresponding to the login given as value of the property, <i>empty list</i> if
	 *         any problem occurs (see logs for more details)
	 */
	private List<String> extractEmailAddressFromProperty(String propertyName, CmsObject cmsObject,
			List<String> listOfFiles) {

		List<String> collectedEmails = new ArrayList<String>();

		/*
		 * collect property for each files
		 */
		Iterator<String> listOfFilesIterator = listOfFiles.iterator();
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
						// Get the email value from account manager
						String email = this.getEmailAddressFromAccountManager(propertyValue, cmsObject);

						if (StringChecker.isNotNullOrEmpty(email)) {
							// add email in the list of result
							collectedEmails.add(email);
							// add resources in the map of resources per reviewer
							this.addResourceInMap(email, filePath);
						}
					}
					else {
						LOGGER.warn("The property " + propertyName + " for " + filePath + " has an invalid value");
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

		return collectedEmails;
	}

	/**
	 * Contact the account manager of open cms to get the email of the reviewer
	 * 
	 * @param reviewerName
	 *            the login of the reviewer
	 * @param cmsObject
	 *            cmsObject the cmsObject that will allow to get some information into openCms
	 * @return the required email, <b>null</b> if any problem occurs (see logs for details)s
	 */
	private String getEmailAddressFromAccountManager(String reviewerName, CmsObject cmsObject) {

		// List list = OpenCms.getOrgUnitManager().getUsers(obj, "/", true);
		try {
			if (StringChecker.isNotNullOrEmpty(reviewerName)) {
				CmsUser userObject = cmsObject.readUser(reviewerName);
				String email = userObject.getEmail().trim();

				// Check that the email is valid
				if (StringChecker.isNotNullOrEmpty(email) && Pattern.matches(EMAIL_PATTERN, email)) {
					return email;
				}
				else
					return null;
			}
		}
		catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		return null;
	}

	/**
	 * Add a new Value in the map _resourcePerReviewer
	 * 
	 * @param emailAddress
	 *            the email address of the reviewer
	 * @param resourcePath
	 *            the file path associated
	 */
	private void addResourceInMap(String emailAddress, String resourcePath) {
		// get the list of associated file if the email address is already in
		// the map
		if (_resourcesPerReviewer.containsKey(emailAddress)) {
			List<String> registeredPath = _resourcesPerReviewer.get(emailAddress);

			// check value to avoid double
			if (!registeredPath.contains(resourcePath)) {
				registeredPath.add(resourcePath);
			}
		}
		else {
			// create a new entry in the map
			ArrayList<String> newList = new ArrayList<String>();
			newList.add(resourcePath);
			_resourcesPerReviewer.put(emailAddress, newList);
		}
	}

	/**
	 * Add a new Value in the map _resourcePerReviewer
	 * 
	 * @param emailAddress
	 *            the email address of the reviewer
	 * @param listOfFiles
	 *            the list of files associated
	 */
	private void addResourcesInMap(String emailAddress, List<String> listOfFiles) {
		// get the list of associated file if the email address is already in
		// the map
		if (_resourcesPerReviewer.containsKey(emailAddress)) {
			List<String> registeredPath = _resourcesPerReviewer.get(emailAddress);

			/*
			 * Copy the content of the list of files and check value to avoid double
			 */
			Iterator<String> listOfFilesIterator = listOfFiles.iterator();
			while (listOfFilesIterator.hasNext()) {
				String filePath = listOfFilesIterator.next();
				if (!registeredPath.contains(filePath)) {
					registeredPath.add(filePath);
				}
			}
		}
		else {
			/*
			 * Copy the list of files
			 */
			ArrayList<String> newList = new ArrayList<String>();
			Iterator<String> listOfFilesIterator = listOfFiles.iterator();
			while (listOfFilesIterator.hasNext()) {
				String filePath = listOfFilesIterator.next();
				if (!newList.contains(filePath)) {
					newList.add(filePath);
				}

				// create a new entry in the map
				_resourcesPerReviewer.put(emailAddress, newList);
			}
		}
	}

}
