<%@ page
	import="org.opencms.jsp.CmsJspActionElement,com.eurelis.opencms.workflows.ui.CmsWorkflowDialog"%>
<%
			CmsJspActionElement jsp = new CmsJspActionElement(pageContext,
					request, response);
			CmsWorkflowDialog wp = new CmsWorkflowDialog(jsp);
			wp.displayDialog();%>
<html>
<body>
<h1>An error occurs in workflows-main.jsp</h1>
You may not see this page...
</body>
</html>