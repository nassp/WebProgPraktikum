<!DOCTYPE html>
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
		
		<meta http-equiv="refresh" content="5; url=/~webprog03/tomcat/index.html">
		<%@page import="de.quiz.UserManager.IUserManager"%>
		<%@page import="de.quiz.LoggingManager.ILoggingManager"%>
		<%@page import="de.quiz.ServiceManager.ServiceManager"%>
	</head>
	<body>
		
			<section id="quiz-app">
			<section id="logoutJSP">
			<br /> <br />
			<br /> <br /> 
			<img src="../img/abgemeldet.png" alt="abgemeldet">

			<p>Du bist jetzt abgemeldet!</p>
			<p>Bis zum nächsten mal.</p>
			<% 
			HttpSession tmp = request.getSession(false);
			System.out.println("SESSION:!!!! "+tmp);
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
				//response.sendRedirect(response.encodeRedirectURL("/WebQuiz/"));
			}
			%>
			<br /> <br />
			<a href="/~webprog03/tomcat/index.html">-Zurück zur Anmeldeseite-</a>
			</section>
			</section>
		
	</body>
</html>