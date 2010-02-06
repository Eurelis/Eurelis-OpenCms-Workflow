/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms. Redistribution and use in
 * source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.workflows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsRequestContext;
import org.opencms.file.CmsUser;
import org.opencms.security.CmsAccessControlList;
import org.opencms.security.CmsPermissionSetCustom;
import org.opencms.security.CmsRole;

import com.eurelis.opencms.workflows.ui.Messages;
import com.eurelis.opencms.workflows.util.ErrorFormatter;
import com.eurelis.opencms.workflows.util.OpenCmsEasyAccess;
import com.eurelis.opencms.workflows.workflows.security.WorkflowSecurityManager;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowKey;
import com.eurelis.opencms.workflows.workflows.toolobjects.WorkflowPropertyContainer;

/**
 * This class manage the connection with OSWOrkflows. 
 * This implementation take care of access right on workflows with such politic :
 * <ul>
 * <li>If there is no file attached to the WF, the rights apply are those of NO_FILES branch.</li>
 * <li>the creator of a workflow always has read rights on his workflow.</li>
 * <li>the other users follow the rule define statically in the config file.</li>
 * </ul>
 * @author Sébastien Bianco
 */
public class OSWorkflowManagerWithRightManagement extends A_OSWorkflowManager {

	/**
	 * The Security manager that manage rights to access the workflows
	 * 
	 * @uml.property name="_securityManager"
	 * @uml.associationEnd
	 */
	protected WorkflowSecurityManager	_securityManager	= WorkflowSecurityManager.getInstance();

	/** The log object for this class. */
	private static final Logger		LOGGER				= Logger.getLogger(OSWorkflowManagerWithRightManagement.class);

	
	
	
	/**
	 * Default constructor
	 */
	/* package */OSWorkflowManagerWithRightManagement() {
		//LOGGER.debug("WF | securityManager = \n" + _securityManager);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#checkOwner(com.eurelis.opencms.workflows.workflows
	 * .toolobjects.WorkflowPropertyContainer, java.lang.String)
	 */
	@Override
	protected boolean checkOwner(WorkflowPropertyContainer propertyContainer, String name) {
		return (propertyContainer.getCreator() != null) ? propertyContainer.getCreator().equals(name) : false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#filterListOfFilesAccordingToUserRights(java.util.
	 * List)
	 */
	@Override
	protected List<String> filterListOfFilesAccordingToUserRights(List<String> listOfFiles, CmsObject cmsObject) {
		/*
		 * Check that the current user has rights to see each file
		 */
		List<String> filteredListOfFiles = new ArrayList<String>();
		Iterator<String> listOfFilesIterator = listOfFiles.iterator();
		CmsUser user = cmsObject.getRequestContext().currentUser();
		List<CmsGroup> groups = OpenCmsEasyAccess.getGroupOfUser(user.getName());
		List<CmsRole> roles = OpenCmsEasyAccess.getRolesOfUser(user.getName());

		while (listOfFilesIterator.hasNext()) {
			String filePath = listOfFilesIterator.next();
			CmsAccessControlList acls = OpenCmsEasyAccess.getACL(filePath);
			if (acls != null) {
				CmsPermissionSetCustom acl = acls.getPermissions(user, groups, roles);
				if (acl != null) {
					// check read rights
					String permissionString = acl.getPermissionString();

					if (permissionString.contains("+r") || permissionString.contains("+v")) {
						filteredListOfFiles.add(filePath);
					}
					else {
						filteredListOfFiles.add(Messages.getMessages(Messages.GUI_ERROR_FILE_READRIGHTDENY_0, cmsObject
								.getRequestContext().getLocale()));
					}

				}
				else {
					//LOGGER.debug("WF | ACL for " + filePath + " not accessible for user " + user.getName());
					filteredListOfFiles.add(Messages.getMessages(Messages.GUI_ERROR_FILE_READRIGHTDENY_0, cmsObject
							.getRequestContext().getLocale()));
				}
			}
			else {
				//LOGGER.debug("WF | ACL not accssible for " + filePath);
				filteredListOfFiles.add(Messages.getMessages(Messages.GUI_ERROR_FILE_NOTEXISTANYMORE_PREFIX_0,
						cmsObject.getRequestContext().getLocale())
						+ " "
						+ filePath
						+ " "
						+ Messages.getMessages(Messages.GUI_ERROR_FILE_NOTEXISTANYMORE_0, cmsObject.getRequestContext()
								.getLocale()));
			}

		}
		return filteredListOfFiles;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasReadRightOnWorkflow(com.eurelis.opencms.workflows
	 * .workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String)
	 */
	@Override
	protected boolean hasReadRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext, String parentFolder,
			WorkflowPropertyContainer propertyContainer) {
		if (parentFolder == null)
			return this._securityManager.hasReadRightOnWorkflow(cmsRequestContext.currentUser(), key, NO_FILE_PATH,
					cmsRequestContext.getLocale());
		if (parentFolder != null && key != null && cmsRequestContext.currentUser() != null) {
			return this._securityManager.hasReadRightOnWorkflow(cmsRequestContext.currentUser(), key, parentFolder,
					cmsRequestContext.getLocale());
		}
		else {
			LOGGER.debug("WF | A blocking null value has been found : \n\tkey = " + key + "\n\tuser = "
					+ cmsRequestContext.currentUser() + "\n\tparentFolder = " + parentFolder);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasWriteRightOnWorkflow(com.eurelis.opencms.workflows
	 * .workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.lang.String)
	 */
	@Override
	protected boolean hasWriteRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			String parentFolder, WorkflowPropertyContainer propertyContainer) {
		if (parentFolder == null)
			return this._securityManager.hasWriteRightOnWorkflow(cmsRequestContext.currentUser(), key, NO_FILE_PATH,
					cmsRequestContext.getLocale());
		if (parentFolder != null && key != null && cmsRequestContext.currentUser() != null) {
			return this._securityManager.hasWriteRightOnWorkflow(cmsRequestContext.currentUser(), key, parentFolder,
					cmsRequestContext.getLocale());
		}
		else {
			LOGGER.debug("WF | A blocking null value has been found : \n\tkey = " + key + "\n\tuser = "
					+ cmsRequestContext.currentUser() + "\n\tparentFolder = " + parentFolder);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.eurelis.opencms.workflows.workflows.A_OSWorkflowManager#hasCreateRightOnWorkflow(com.eurelis.opencms.workflows
	 * .workflows.toolobjects.WorkflowKey, org.opencms.file.CmsRequestContext, java.util.List)
	 */
	@Override
	protected boolean hasCreateRightOnWorkflow(WorkflowKey key, CmsRequestContext cmsRequestContext,
			List<String> fileList) {
		List<String> parentFolders = this.getParentPath(fileList);
		if (parentFolders == null || parentFolders.isEmpty())
			return  this._securityManager.hasCreateRightOnWorkflow(cmsRequestContext.currentUser(), key,
					NO_FILE_PATH, cmsRequestContext.getLocale());
		if (parentFolders != null && key != null && cmsRequestContext.currentUser() != null) {
			boolean result = true;
			for (int i = 0; i < parentFolders.size(); i++) {
				result &= this._securityManager.hasCreateRightOnWorkflow(cmsRequestContext.currentUser(), key,
						parentFolders.get(i), cmsRequestContext.getLocale());
				if (!result) {
					break;
				}
			}
			return result;
		}
		else {
			LOGGER.debug("WF | A blocking null value has been found : \n\tkey = " + key + "\n\tuser = "
					+ cmsRequestContext.currentUser() + "\n"
					+ ErrorFormatter.formatList(parentFolders, "parentFolders"));
			return false;
		}
	}

}
