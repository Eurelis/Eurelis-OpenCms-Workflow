<%@ page
	import="org.opencms.jsp.CmsJspActionElement,com.eurelis.opencms.workflows.ui.CmsWorkflowsConfiguredWorkflowList"%>
<%
	CmsJspActionElement jsp = new CmsJspActionElement(pageContext,
			request, response);
CmsWorkflowsConfiguredWorkflowList wp = new CmsWorkflowsConfiguredWorkflowList(jsp);
	wp.displayDialog();
%>

