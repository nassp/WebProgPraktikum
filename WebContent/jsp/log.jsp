<!DOCTYPE html>

<%@page import="de.quiz.LoggingManager.ILoggingManager"%>
<%@page import="de.quiz.ServiceManager.ServiceManager"%>
<html>
	<head>
		<title>Log Page</title>
		
		<meta name="author" content="Patrick Naß">
		<meta name="description" content="Log Seite zum Augaben des eigenen Logs">
		
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		
		<!-- icon für Mozilla, Chrome, Safari und andere -->
		<link rel="icon" href="/img/titleicon.ico" type="image/x-icon">
		<!-- icon für Internet Explorer -->
		<link rel="shortcut icon" href="/img/titleicon.ico" type="image/x-icon">
		
		<link type="text/css" href="../css/style.css" rel="stylesheet">
		<link type="text/css" href="../css/font-awesome.css" rel="stylesheet">
	</head>
	<body>	
	
		<%
		 	response.getWriter().println("<section id=\"quiz-app\">");
			response.getWriter().println("<h1><i class =\"icon-list-alt icon-large\"></i> Gibt alle Einträge des internen Logs aus:</h1>");
			for(int i = 0; i < ServiceManager.getInstance().getService(ILoggingManager.class).getLogContainer().size(); i++) {
			
				String msg = ServiceManager.getInstance().getService(ILoggingManager.class).getLogContainer().get(i);
				response.getWriter().println(msg + "<br />");
			} 
			response.getWriter().println("<div id=\"log\"><a href=\"../index.html\">Back</a></div>");
			response.getWriter().println("</section>");
		%>
		
	</body>
</html>