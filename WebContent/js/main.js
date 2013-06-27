var loginPhase = true;
var gamePhase = false;
var startButtonVisible = false;
var catalogSelected = false;
var content;
var userId = -1;
//Websocket 
var loginURL = "localhost:8080/WebQuiz/LogicServlet";
var ws;
var acceptAnswer = true;
var moreThan2 = false;

/* Wird ausgeführt wenn HTML-Content geladen ist */
$(document).ready(function() {
	content = $("#content");
	// Scrolls the website directly to the game
	$(this).scrollTop(185);

	
	$("#loginButton").click(function(event){
		// Checks whether at least one sign was used
		if(document.getElementById("nameInput").value.length > 0){
			// Sends the name of the player
			$("#highscore table tbody").empty();
			sendMessages(1); 
			// Requests a catalog
			$(".catList").empty();
			sendMessages(3);
	   
		} else {
			alert("Es wurde kein Loginname eingegeben. Bitte versuche es erneut.");
		}
		
	});
	
	// If enter is pressed do the same as when login is clicked
	$("#nameInput").bind("keypress", {}, function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
	    if (code == 13) { //Enter keycode                        
	        e.preventDefault();
	        $("#loginButton").trigger('click');
	    }
	});
	
});
