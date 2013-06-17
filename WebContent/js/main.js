var loginPhase = true;
var gamePhase = false;
var startButtonVisible = false;
var content;
var userId = -1;
//Websocket 
var loginURL = "localhost:8080/WebQuiz/LogicServlet";
var ws;

/* Wird ausgeführt wenn HTML-Content geladen ist */
$(document).ready(function() {
	content = $("#content");
	$(this).scrollTop(185);
	//console.log("Content geladen");

	$("#loginButton").click(function(event){
		if(document.getElementById("nameInput").value.length > 0){
			// Spielername senden
			$("#highscore table tbody").empty();
			sendMessages(1); 
			// Katalogliste anfragen
			$(".catList").empty();
			sendMessages(3);
	
			

//			ws.send("LoginServlet(WebSocket): "+$("#nameInput").val());
	   
		} else {
			alert("Es wurde kein Loginname eingegeben. Bitte versuche es erneut.");
		}
		
	});
	$("#nameInput").bind("keypress", {}, function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
	    if (code == 13) { //Enter keycode                        
	        e.preventDefault();
	        $("#loginButton").trigger('click');
	    }
	});
	
});