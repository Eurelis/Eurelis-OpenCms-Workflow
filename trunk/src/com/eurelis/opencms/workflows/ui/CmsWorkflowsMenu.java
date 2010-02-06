/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.opencms.i18n.CmsEncoder;
import org.opencms.i18n.CmsMessageContainer;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.OpenCms;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.CmsWorkplaceSettings;
import org.opencms.workplace.administration.CmsAdminContextHelpMenuItem;
import org.opencms.workplace.administration.CmsAdminMenuGroup;
import org.opencms.workplace.administration.CmsAdminMenuItem;
import org.opencms.workplace.tools.CmsIdentifiableObjectContainer;
import org.opencms.workplace.tools.CmsToolDialog;
import org.opencms.workplace.tools.CmsToolManager;
import org.opencms.workplace.tools.I_CmsIdentifiableObjectContainer;

import com.eurelis.opencms.workflows.ui.toolobject.WorkflowMenuItem;
import com.eurelis.opencms.workflows.ui.toolobject.WorkflowMenuItemLoader;



/**
 * This class personalize the CmsAdminMenu so as to take care of type for workflows
 * @author Sébastien Bianco
 *
 */
public class CmsWorkflowsMenu extends CmsToolDialog {
	
	/** The log object for this class. */
	private static final Logger LOGGER = Logger.getLogger(CmsWorkflowsMenu.class);


	/** Default link target constant. */
	public static final String DEFAULT_TARGET = "admin_content";

	/** Group container. */
	private I_CmsIdentifiableObjectContainer m_groupContainer = new CmsIdentifiableObjectContainer(
			true, true);

	/**
	 * Default Constructor.<p>
	 * 
	 * @param jsp the jsp context
	 */
	public CmsWorkflowsMenu(CmsJspActionElement jsp) {

		super(jsp);
		try {
			initAdminTool();
		} catch (Exception e) {
			// ignore, only a role violation, not important for left side menu
		}
		installMenu();
	}

	/**
	 * Adds a group.<p>
	 * 
	 * @param group the group
	 * 
	 * @see I_CmsIdentifiableObjectContainer#addIdentifiableObject(String, Object)
	 */
	public void addGroup(CmsAdminMenuGroup group) {

		m_groupContainer.addIdentifiableObject(group.getName(), group);
	}

	/**
	 * Adds a menu item at the given position.<p>
	 * 
	 * @param group the group
	 * @param position the position
	 * 
	 * @see I_CmsIdentifiableObjectContainer#addIdentifiableObject(String, Object, float)
	 */
	public void addGroup(CmsAdminMenuGroup group, float position) {

		m_groupContainer
				.addIdentifiableObject(group.getName(), group, position);
	}

	/**
	 * Adds a new item to the specified menu.<p>
	 * 
	 * If the menu does not exist, it will be created.<p>
	 * 
	 * @param groupName the name of the group
	 * @param name the name of the item
	 * @param icon the icon to display
	 * @param link the link to open when selected
	 * @param helpText the help text to display
	 * @param enabled if enabled or not
	 * @param position the relative position to install the item
	 * @param target the target frame to open the link into
	 * 
	 * @return the new item
	 */
	public CmsAdminMenuItem addItem(String groupName, String name, String icon,
			String link, String helpText, boolean enabled, float position,
			String target) {

		//groupName = CmsToolMacroResolver.resolveMacros(groupName, this);
		CmsAdminMenuGroup group = getGroup(groupName);
		if (group == null) {
			String gid = "group" + m_groupContainer.elementList().size();
			group = new CmsAdminMenuGroup(gid, groupName);
			addGroup(group, position);
		}
		String id = "item" + group.getId() + group.getMenuItems().size();
		CmsAdminMenuItem item = new CmsAdminMenuItem(id, name, icon, link,
				helpText, enabled, target);
		group.addMenuItem(item, position);
		return item;
	}

	/**
	 * Returns all initialized parameters of the current request
	 * that are not in the given exclusion list as hidden field tags that can be inserted in a form.<p>
	 * 
	 * @param excludes the parameters to exclude 
	 * 
	 * @return all initialized parameters of the current request
	 */
	public String allRequestParamsAsUrl(Collection excludes) {

		StringBuffer result = new StringBuffer(512);
		Map params = getJsp().getRequest().getParameterMap();
		Iterator i = params.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			String param = (String) entry.getKey();
			if ((excludes == null) || (!excludes.contains(param))) {
				if (result.length() > 0) {
					result.append("&");
				}
				result.append(param);
				result.append("=");
				String value;
				if (entry.getValue() instanceof String[]) {
					value = ((String[]) entry.getValue())[0];
				} else {
					value = (String) entry.getValue();
				}
				String encoded = CmsEncoder.encode(value, getCms()
						.getRequestContext().getEncoding());
				result.append(encoded);
			}
		}
		return result.toString();
	}

	/**
	 * Returns the requested group.<p>
	 * 
	 * @param name the name of the group
	 * 
	 * @return the group
	 * 
	 * @see I_CmsIdentifiableObjectContainer#getObject(String)
	 */
	public CmsAdminMenuGroup getGroup(String name) {

		return (CmsAdminMenuGroup) m_groupContainer.getObject(name);
	}

	/**
	 * Returns the admin manager.<p>
	 * 
	 * @return the admin manager
	 */
	public CmsToolManager getToolManager() {

		return OpenCms.getWorkplaceManager().getToolManager();
	}

	/**
	 * Generates the necesary html code for the groups.<p>
	 * 
	 * @param wp the page for which the code is generated
	 * 
	 * @return html code
	 */
	public String groupHtml(CmsWorkplace wp) {		
		StringBuffer html = new StringBuffer(512);
		Iterator itHtml = m_groupContainer.elementList().iterator();
		while (itHtml.hasNext()) {
			CmsAdminMenuGroup group = (CmsAdminMenuGroup) itHtml.next();
			html.append(group.groupHtml(wp));
		}
		return html.toString();
	}

	/**
	 * Creates the default menu as the root tool structure.<p>
	 */
	public void installMenu() {

		// initialize the menu groups
		m_groupContainer.clear();

		// creates the context help menu
		CmsAdminMenuGroup helpMenu = new CmsAdminMenuGroup("help", Messages
				.get().getBundle(getLocale()).key(
						Messages.GUI_WORKFLOWS_MENU_HELP_GROUP_0));
		helpMenu.addMenuItem(new CmsAdminContextHelpMenuItem());
		addGroup(helpMenu);

		/*
		 * Create the Workflows menu
		 */
		//Get the group Name
		CmsMessageContainer messageContainer = Messages.get().container(
				Messages.GUI_WORKFLOWS_MENU_WORKFLOWS_GROUP_0);
		Locale locale = this.getLocale();
		String groupName = new MessageFormat(messageContainer.key(locale),
				locale).format(new Object[] { messageContainer.key(locale) });
		
		//get the list of items to display and add it in the groupContainer 
		WorkflowMenuItemLoader itemsLoader = WorkflowMenuItemLoader.getInstance(this.getCms());
		Iterator<WorkflowMenuItem> menuItemIterator = itemsLoader
				.getMenuItems().iterator();
		while (menuItemIterator.hasNext()) {
			WorkflowMenuItem menuItem = menuItemIterator.next();

			addItem(groupName, menuItem.get_name(),
					menuItem.get_iconFilePath(), menuItem.get_link(), menuItem
							.get_helpText(), true, menuItem.get_position(),
					CmsWorkflowsMenu.DEFAULT_TARGET);

		}
	}

	/**
	 * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings, javax.servlet.http.HttpServletRequest)
	 */
	protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings,
			HttpServletRequest request) {

		fillParamValues(request);
	}

}
