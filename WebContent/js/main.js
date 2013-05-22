var loginPhase = true;
var gamePhase = false;
var startButtonVisible = false;
var content;

//Websocket 
var loginURL = "localhost:8080/WebQuiz/LogicServlet";
var ws = new WebSocket("ws://"+loginURL);

/* Wird ausgeführt wenn HTML-Content geladen ist */
$(document).ready(function() {
	content = $("#content");
	//console.log("Content geladen");

	$("#loginButton").click(function(event){
		//send(event,"name",$("#nameInput").val());
		if(document.getElementById("nameInput").value.length > 0){
			// Spielername senden
			testFunc();
			$("#highscore table tbody").empty();
			$.ajax({ 
			    type: 'POST', 
			    url: 'PlayerServlet', 
			    data: { rID: '1' , name: $("#nameInput").val() }, 
			    dataType: 'json',
			    success: function (data) { 
			    	console.log(data);
			     	if(data==255) {
		        		alert("Fehler, Spieler konnte nicht eingeloggt werden");
		        	}else if (data == 2){
		        		loggedIn();
//		        		$("#highscore table tbody").append("<tr><td>"+element+"</td><td>0</td></tr>"); 
		        	}
			    }
			});
			// Katalogliste anfragen
			$(".catList").empty();
			$.ajax({ 
			    type: 'POST', 
			    url: 'CatalogServlet', 
			    data: { rID: '3' }, 
			    dataType: 'json',
			    success: function (data) { 
			        $.each(data, function(index, element) {
			        	$(".catList").append("<li>"+element.name+"</li>");
			        });
			        initCatalogList();
			    }
			});
	
			
			ws.onopen = function(){
	        };
	        ws.onmessage = function(message){
	        	$(".catList").append("<li>"+message.data+"</li>");
	        };
	        function postToServer(){
	            ws.send("LoginServlet(WebSocket): "+$("#nameInput").val());
	        }
	        function closeConnection(){
	            ws.close();
	        }
	        postToServer();
	        //closeConnection();
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