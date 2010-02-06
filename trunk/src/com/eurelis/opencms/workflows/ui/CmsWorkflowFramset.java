/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.ui;

import javax.servlet.http.HttpServletRequest;

import org.opencms.jsp.CmsJspActionElement;
import org.opencms.workplace.CmsWorkplaceSettings;
import org.opencms.workplace.tools.CmsToolDialog;

/**
 * 
 * Helper class to create the administration frameset.<p> 
 * 
 * It allows to specify if you want or not an left side menu.<p>
 * 
 *
 * @author Sébastien Bianco
 *
 */
public class CmsWorkflowFramset extends CmsToolDialog {

	 /** Request parameter name for the "with menu" flag. */
    public static final String PARAM_MENU = "menu";

    /** Request parameter value. */
    private String m_paramMenu;

    /**
     * Public constructor.<p>
     * 
     * @param jsp an initialized JSP action element
     */
    public CmsWorkflowFramset(CmsJspActionElement jsp) {

        super(jsp);
    }

    /**
     * Returns the menu parameter value.<p>
     *
     * @return the menu parameter value
     */
    public String getParamMenu() {

        return m_paramMenu;
    }

    /**
     * Sets the menu parameter value.<p>
     *
     * @param paramMenu the menu parameter value to set
     */
    public void setParamMenu(String paramMenu) {

        m_paramMenu = paramMenu;
    }

    /**
     * Tests if the current dialog should be displayed with or without menu.<p>
     * 
     * The default is with menu, use <code>menu=no</code> for avoiding it.<p>
     * 
     * @return <code>true</code> if the dialog should be displayed with menu
     */
    public boolean withMenu() {

        return getParamMenu() == null || !getParamMenu().equals("no");
    }

    /**
     * @see org.opencms.workplace.CmsWorkplace#initWorkplaceRequestValues(org.opencms.workplace.CmsWorkplaceSettings, javax.servlet.http.HttpServletRequest)
     */
    protected void initWorkplaceRequestValues(CmsWorkplaceSettings settings, HttpServletRequest request) {

        // fill the parameter values in the get/set methods
        fillParamValues(request);
    }
   
}
