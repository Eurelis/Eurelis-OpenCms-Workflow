<%@ page
	import="org.opencms.jsp.CmsJspActionElement,com.eurelis.opencms.workflows.ui.CmsWorkflowsList"%>
<%
	CmsJspActionElement jsp = new CmsJspActionElement(pageContext,
			request, response);
	CmsWorkflowsList wp = new CmsWorkflowsList(jsp);
	wp.displayDialog();
%>

