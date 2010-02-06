<%@ page
	import="org.opencms.jsp.CmsJspActionElement,com.eurelis.opencms.workflows.ui.CmsWorkflowReloadConfigDialog"%>
<%
			CmsJspActionElement jsp = new CmsJspActionElement(pageContext,
					request, response);
			CmsWorkflowReloadConfigDialog wp = new CmsWorkflowReloadConfigDialog(jsp);
			wp.displayDialog();%>
<html>
<body>
<h1>An error occurs in reloadConfigWorkflows.jsp</h1>
You may not see this page...
</body>
</html>