/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * This class give method so as to write in a file (manage the stream creation and closure)
 * @author Sébastien Bianco
 *
 */
public class FileWriter {
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(FileWriter.class);
	
	/**
	 * Create a new File and its content (if the file exist, this one is updated without any prompt)
	 * @param filePath the path of the file to create/update
	 * @param fileContent the content of this file
	 * @throws IOException this exception will be thrown if an problem occurs during the write of the file
	 */
	public static void writeFile(String filePath, String fileContent) throws IOException{
		
		File file  = new File(filePath);
		
		//check if file exist (if exist, delete it)
		if(file.exists()){
			file.delete();
		}
		
		//create the new File
		file.createNewFile();
		
		//create Stream
		PrintWriter out = new PrintWriter(file);
		
		//write content
		out.print(fileContent);
		
		//flush and close stream
		out.flush();
		out.close();
		
		
		
		
	}
	
	/**
	 * Create a repository if this one doesn't exist
	 * @param repositoryPath the repository path to create
	 */
	public static void createRepositoryIfDoesntExist(String repositoryPath){		
		
		File repository = new File(repositoryPath);
		
		//check that repository doesn't exist
		if(!repository.exists()){
			LOGGER.info("WF | repository "+repositoryPath+ " will be create !");
			repository.mkdirs();			
		}else{
			//LOGGER.info("WF | repository "+repositoryPath+ " exists !");
		}
		
	}
	
	

}
