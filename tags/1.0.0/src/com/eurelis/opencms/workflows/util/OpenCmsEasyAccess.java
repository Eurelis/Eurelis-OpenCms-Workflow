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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.relations.CmsRelation;
import org.opencms.relations.CmsRelationFilter;
import org.opencms.security.CmsAccessControlList;
import org.opencms.security.CmsRole;

/**
 * This class contains a number of methods that allows to easily access a number
 * of properties in OpenCms. This class use the highest level of right thanks
 * the initializer cmsObject that is stored in CmsUtil class.
 * 
 * @author Sébastien Bianco
 * 
 */
public class OpenCmsEasyAccess {

	/** The log object for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(OpenCmsEasyAccess.class);

	/**
	 * The admin object that allows to go through VFS and open files
	 */
	private static CmsObject _cmsAdminObj = null;

	/**
	 * Initialize the _cmsAdminObject with the guest right on offline project
	 */
	static {
		OpenCmsEasyAccess._cmsAdminObj = CmsUtil.getAdminCmsObject();
	}

	/**
	 * Private constructor to deny access in a non static way
	 */
	private OpenCmsEasyAccess() {
	}

	/**
	 * Get the list of CmsResource object of the repository with given path
	 * 
	 * @param repositoryPath
	 *            the path of the repository
	 * @param adminObject
	 *            the admin object to access ressources
	 * @return the list of children of the repository, <i>empty list</i> if the
	 *         repository content no child or if the given path correspond to a
	 *         File
	 */
	public static List<CmsResource> getListOfFile(String repositoryPath,
			CmsObject adminObject) {
		// initialize the result set
		List<CmsResource> result = new ArrayList<CmsResource>();

		try {
			CmsResource parentResource = adminObject.readResource(
					repositoryPath, CmsResourceFilter.ALL);

			if (parentResource.isFile()) {
				return result;
			} else {
				List folderContent = adminObject
						.getResourcesInFolder(parentResource.getRootPath(),
								CmsResourceFilter.DEFAULT);
				return folderContent;
			}

		} catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (Exception e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}
		return result;
	}

	/**
	 * Get the list of CmsResource object of the repository with given path
	 * 
	 * @param repositoryPath
	 *            the path of the repository
	 * @return the list of children of the repository, <i>empty list</i> if the
	 *         repository content no child or if the given path correspond to a
	 *         File
	 */
	public static List<CmsResource> getListOfFile(String repositoryPath) {
		return getListOfFile(repositoryPath, _cmsAdminObj);
	}

	/**
	 * Read the content of a File in the VFS
	 * 
	 * @param resource
	 *            the resource corresponding to the file to read
	 * @return the content of the file, an <i>empty array</i> if the file
	 *         content no data, or if the resource doesn't correspond to a file
	 */
	public static byte[] readFile(CmsResource resource) {
		if (resource.isFile()) {
			// Read the file
			CmsFile resourceToParse;
			try {
				resourceToParse = _cmsAdminObj.readFile(resource);
			} catch (CmsException e) {
				LOGGER.error(ErrorFormatter.formatException(e));
				return new byte[0];
			}
			byte[] fileContent = resourceToParse.getContents();
			return fileContent;
		} else {
			return new byte[0];
		}
	}

	/**
	 * Get a CmsResource object from its VFS Path
	 * 
	 * @param path
	 *            the path of the object to get
	 * @return the required CmsResource object,<b>null</b> if an error occurs.
	 */
	public static CmsResource getResource(String path) {
		try {
			return _cmsAdminObj.readResource(path, CmsResourceFilter.ALL);
		} catch (CmsException e) {
			return null;
		}
	}

	/**
	 * Get the list of resources that are in association with a given resource
	 * (no recursive) (CmsResouceFilter.TARGETS)
	 * 
	 * @param relationType
	 *            the type of relation to collect
	 * @param sourcePath
	 *            the given resource path
	 * @return the list of relation of the path, an <i>empty list</i> if the
	 *         sourcePath doesn't correspond to a file, or if the given file
	 *         doesn't have any relations.
	 * 
	 */
	public static List<CmsResource> getListOfAssociatedResource(
			String sourcePath) {
		List<CmsResource> result = new ArrayList<CmsResource>();

		// get the list of relations
		List<CmsRelation> listOfRelations;
		try {
			listOfRelations = _cmsAdminObj.getRelationsForResource(sourcePath,
					CmsRelationFilter.TARGETS);

			// Collect the resources
			Iterator<CmsRelation> listOfRelationsIterator = listOfRelations
					.iterator();
			while (listOfRelationsIterator.hasNext()) {
				CmsRelation currentRelation = listOfRelationsIterator.next();
				// get the target resource of the relation
				CmsResource targetResource = _cmsAdminObj
						.readResource(currentRelation.getTargetPath());
				result.add(targetResource);
			}
		} catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		} catch (Exception e) {
			LOGGER.error(ErrorFormatter.formatException(e));
		}

		return result;

	}

	/**
	 * Get a Property of a resource (search in parent directory if required)
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param resource
	 *            the resource
	 * @return the required Property,<b>null</b> if a problem occurs (see logs
	 *         for details)
	 */
	public static CmsProperty getProperty(String propertyName,
			CmsResource resource) {
		return getProperty(propertyName, resource, _cmsAdminObj);
	}

	/**
	 * Get a Property of a resource (search in parent directory if required)
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param resource
	 *            the resource
	 * @param adminObject
	 *            the admin object to access ressources
	 * @return the required Property,<b>null</b> if a problem occurs (see logs
	 *         for details)
	 */
	public static CmsProperty getProperty(String propertyName,
			CmsResource resource, CmsObject adminObject) {
		try {
			return adminObject.readPropertyObject(resource, propertyName, true);
		} catch (CmsException e) {
			LOGGER.error(ErrorFormatter.formatException(e));
			return null;
		}
	}

	/**
	 * Get the acl associated to a resource
	 * 
	 * @param filePath
	 *            the file which required ACL
	 * @return the ACL, <b>null</b> if an exception occurs
	 */
	public static CmsAccessControlList getACL(String filePath) {
		try {
			return _cmsAdminObj.getAccessControlList(filePath);
		} catch (CmsException e) {
			return null;
		}
	}

	/**
	 * Read and get the CmsUser object from its name
	 * 
	 * @param userName
	 *            the name of the required object
	 * @return the CmsUser object, <b>null</b> if an exception occurs
	 */
	public static CmsUser getUser(String userName) {
		try {
			return _cmsAdminObj.readUser(userName);
		} catch (CmsException e) {
			return null;
		}
	}

	/**
	 * Get the list of group that the current user is a member of
	 * 
	 * @param username
	 *            the name of the user
	 * @return the list of group, an <i>empty list</i> if an error occurs (or
	 *         if the user is a member of none group)
	 */
	public static List<CmsGroup> getGroupOfUser(String username) {
		try {
			return _cmsAdminObj.getGroupsOfUser(username, true, true);
		} catch (CmsException e) {
			return new ArrayList<CmsGroup>();
		}
	}

	/**
	 * Get the list of role that the current user is a member of
	 * 
	 * @param username
	 *            the name of the user
	 * @return the list of role, an <i>empty list</i> if an error occurs (or if
	 *         the user is a member of none role)
	 */
	public static List<CmsRole> getRolesOfUser(String username) {
		try {
			return OpenCms.getRoleManager().getRolesOfUser(_cmsAdminObj,
					username, "/", true, true, true);
		} catch (CmsException e) {
			return new ArrayList<CmsRole>();
		}
	}
}
