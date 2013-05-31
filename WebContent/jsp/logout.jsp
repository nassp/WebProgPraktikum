<!DOCTYPE html>

<%@page import="de.quiz.UserManager.IUserManager"%>
<%@page import="de.quiz.LoggingManager.ILoggingManager"%>
<%@page import="de.quiz.ServiceManager.ServiceManager"%>
<html>
	<head>
		<title>Logout Page</title>

		<meta name="author" content="Patrick Naß">
		<meta name="description" content="Log Seite zum Augaben des eigenen Logs">

		<meta http-equiv="content-type" content="text/html; charset=utf-8">

		<!-- icon für Mozilla, Chrome, Safari und andere -->
		<link rel="icon" href="../img/titleicon.ico" type="image/x-icon">
		<!-- icon für Internet Explorer -->
		<link rel="shortcut icon" href="../img/titleicon.ico" type="image/x-icon">

		<link type="text/css" href="../css/style.css" rel="stylesheet">
		<link type="text/css" href="../css/font-awesome.css" rel="stylesheet">
		
		<meta http-equiv="refresh" content="5; url=/WebQuiz/">
		
	</head>
	<body>
		<%
			response.getWriter().println("<section id=\"quiz-app\">");
			response.getWriter().println("<section id=\"logoutJSP\">");
			response.getWriter().println("<br /> <br />");
			response.getWriter()
			.println(
					"<br /> <br /> <img src=\"../img/abgemeldet.png\" alt=\"abgemeldet\">");

			response.getWriter().println("<p>Du bist jetzt abgemeldet!</p>");
			response.getWriter().println("<p>Bis zum nächsten mal.</p>");

			HttpSession tmp = request.getSession(false);
			try {
				if (tmp != null) {
					ServiceManager.getInstance().getService(IUserManager.class)
							.logoutUser(tmp);
				}

			} catch (Exception e) {
				ServiceManager
						.getInstance()
						.getService(ILoggingManager.class)
						.log(e,
								"User couldn't logout, because Session is already invalid");
				response.sendRedirect(response.encodeRedirectURL("/WebQuiz/"));
			}

			response.getWriter().println("<br /> <br />");
			response.getWriter().println("<p>");
			response.getWriter().println(
					"<a href=\"/WebQuiz/\">-Zurück zur Anmeldeseite-</a>");
			response.getWriter().println("</section>");
			response.getWriter().println("</section>");
		%>
	</body>
</html>