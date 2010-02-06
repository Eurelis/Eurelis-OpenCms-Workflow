<%@ page buffer="none"
	import="org.opencms.workplace.*,org.opencms.workplace.administration.*,org.opencms.workplace.tools.*,org.opencms.jsp.*,com.eurelis.opencms.workflows.ui.*,com.eurelis.opencms.workflows.ui.toolobject.*,org.opencms.file.*"%>

<%
	CmsJspActionElement jsp = new CmsJspActionElement(pageContext,
			request, response);
	CmsWorkflowFramset wp = new CmsWorkflowFramset(jsp);
	// reset root
	wp.setParamRoot("workflows");
	wp.setParamBase("/");
	//wp.getToolManager().initParams(wp);

	// rewrite params
	String params = wp.allParamsAsRequest();
	if (params == null) {
		params = "";
	}
	if (params.length() > 0) {
		params = "?" + params;
	}
	boolean sb = Boolean.valueOf(request.getParameter("scroll"))
			.booleanValue();

	// jsp.link(CmsToolManager.VIEW_JSPPAGE_LOCATION) + params
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">

<%@page
	import="com.eurelis.opencms.workflows.util.WaitingDataArea"%>
<%@page
	import="com.eurelis.opencms.workflows.util.ModuleSharedVariables"%>
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;"
	charset=<%= wp.getEncoding() %>">
<meta HTTP-EQUIV="Pragma" content="no-cache">
<title><%=wp.key("label.wptitle")%> <%=wp.getSettings().getUser().getName()%>@<%=request.getServerName()%></title>

<script language="javascript">

</script>

</head>
<frameset cols="<%=(wp.withMenu()?(sb?"236,":"212,"):"")%>*" border="0"
	frameborder="0" framespacing="0">
	<%
		if (wp.withMenu()) {
	%>
	<frame
		<%= wp.getFrameSource("admin_menu", jsp.link("workflows-menu.jsp") + params) %>
		frameborder="0" border="0" noresize scrolling="<%=sb?"yes":"auto"%>">
	<%
		}
	%>
	<frame
		<%= wp.getFrameSource("admin_content", jsp.link(UISharedVariables.WORKFLOW_MAINJSPPAGE_LOCATION) + params) %>
		frameborder="0" border="0" framespacing="0" marginheight="7"
		marginwidth="7" noresize scrolling="auto">
</frameset>
</html>
