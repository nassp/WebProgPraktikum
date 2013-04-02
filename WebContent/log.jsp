<!DOCTYPE html>

<%@page import="de.quiz.LoggingManager.ILoggingManager"%>
<%@page import="de.quiz.ServiceManager.ServiceManager"%>
<html>
	<head>
		<title>Log Page</title>
		
		<meta name="author" content="Alexander St�cker">
		<meta name="description" content="Log Seite zum Augaben des eigenen Logs">
		
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
	</head>
<body>	
	

	<% 	response.getWriter().println("<h1>Gibt alle Eintr�ge des internen Logs aus:</h1>");
		for(int i = 0; i < ServiceManager.getInstance().getService(ILoggingManager.class).getLogContainer().size(); i++) {
			
			String msg = ServiceManager.getInstance().getService(ILoggingManager.class).getLogContainer().get(i);
			response.getWriter().println(msg + "<br />");
	} 
	%>

</body>
</html>