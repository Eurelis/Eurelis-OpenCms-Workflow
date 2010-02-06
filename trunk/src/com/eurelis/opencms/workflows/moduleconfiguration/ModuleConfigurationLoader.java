/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.moduleconfiguration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsLog;

import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;

/**
 * This class take car of loading the config file and to maintain a single instance of ModuleConfiguration available This config file is <b>/system/modules/com.eurelis.opencms.eurelisworkflow/conf/module_config.txt</b>
 * @author   Sébastien Bianco
 */
public class ModuleConfigurationLoader {

	/** The log object for this class. */
	private static final Log LOGGER = CmsLog
			.getLog(ModuleConfigurationLoader.class);

	/**
	 * The name of the file that contains the workflows properties
	 */
	private static final String CONFIG_FILE = "/system/modules/com.eurelis.opencms.workflows/conf/module_config.txt";

	/**
	 * The single instance of ModuleConfiguration
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
	private static ModuleConfiguration instance = null;

	/**
	 * The String that indicates comment in the config file
	 */
	private static final String COMMENT_STRING = "#";

	/**
	 * The String that is used as separator in the config file
	 */
	private static final String SEPARATOR_STRING = "=";

	/**
	 * Get the single instance of ModuleConfiguration. if this instance is null,
	 * create it.
	 * 
	 * @return The ModuleConfiguration instance
	 */
	public static ModuleConfiguration getConfiguration() {
		if (instance == null) {
			reload();
		}
		return instance;
	}

	/**
	 * Load the config file, extract value
	 * 
	 * @param instance
	 *            the instance to update
	 * @throws IOException
	 *             if a problem occurs during initialization
	 */
	private static void loadConfigFile(ModuleConfiguration instance)
			throws IOException {
		CmsResource resource = OpenCmsEasyAccess.getResource(CONFIG_FILE);

		if (resource != null) {
			if (resource.isFile()) {
				// Read the file
				byte[] fileContent = OpenCmsEasyAccess.readFile(resource);

				if (fileContent.length > 0) {

					// get a reader on the file
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new ByteArrayInputStream(
									fileContent)));

					/*
					 * Read all lines of the files
					 */
					String readLine = null;
					while ((readLine = reader.readLine()) != null) {

						//remove useless space and other
						readLine =readLine.trim();
						
						// ignore line starting with "#" or emptyline
						if (!readLine.startsWith(COMMENT_STRING)
								&& !readLine.equals("")) {

							String[] configValues = readLine
									.split(SEPARATOR_STRING);
							if (configValues.length > 1) {
								Class moduleConfigurationClass = instance
										.getClass();
								if (moduleConfigurationClass != null) {
									try {
										//set the new value
										Field fieldToFill = moduleConfigurationClass
												.getField(configValues[0].trim());
										if(fieldToFill!=null){
											try {
												fieldToFill.set(instance,configValues[1].trim());
											} catch (IllegalArgumentException e) {
												LOGGER.info(ErrorFormatter.formatException(e));
											} catch (IllegalAccessException e) {
												LOGGER.info(ErrorFormatter.formatException(e));
											}
										}else{
											LOGGER.debug("No field associated to "+configValues[0]+" has been found");
										}
										
									} catch (SecurityException e) {
										LOGGER.info(ErrorFormatter.formatException(e));
									} catch (NoSuchFieldException e) {
										LOGGER.info(ErrorFormatter.formatException(e));
									}
								} else {
									LOGGER
											.debug("The class of ModuleConfiguration instance has not been found !");
								}
							} else {
								LOGGER
										.info("The line "
												+ readLine
												+ " doesn't correspond to a valid property");
							}

						}
					}

				} else {
					LOGGER
							.warn("The config file " + CONFIG_FILE
									+ " is empty.");
				}
			} else {
				LOGGER.warn("The config file " + CONFIG_FILE
						+ " is not a file.");
			}

		} else {
			LOGGER.warn("The config file " + CONFIG_FILE
					+ " has not been found.");
		}
		
		//LOGGER.debug("WF | instance = "+instance);
	}
	
	/**
	 * Reload the Module Configuration
	 */
	public static void reload(){
		LOGGER.debug("WF | Reload ModuleConfiguration");
		instance = new ModuleConfiguration();
		try {
			loadConfigFile(instance);
		} catch (IOException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
	}

}
