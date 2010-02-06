/*
 * Copyright (c) Eurelis. All rights reserved. CONFIDENTIAL - Use is subject to license terms.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are not permitted without prior written permission of Eurelis.
 */

/**
 * 
 */
package com.eurelis.opencms.workflows.functions;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This class doesn't do something special, but update the workflow property to maintain the historic
 * @author Sébastien Bianco
 *
 */
public class JustFillPropertyFunction extends EurelisWorkflowFunction {

	/* (non-Javadoc)
	 * @see com.eurelis.opencms.workflows.functions.EurelisWorkflowFunction#executeFunction(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	@Override
	protected void executeFunction(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		//DO nothing
	}

}
