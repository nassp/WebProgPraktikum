var loginPhase = true;
var gamePhase = false;
var startButtonVisible = false;
var content;

//Websocket 
var loginURL = "localhost:8080/WebQuiz/LogicServlet";
var ws = new WebSocket("ws://"+loginURL);

/* CountdownTimer Funktion */
jQuery.fn.countDown = function(settings,to) {
	settings = jQuery.extend({
		startFontSize: "36px",
		endFontSize: "12px",
		duration: 1000,
		startNumber: 10,
		endNumber: 0,
		callBack: function() { }
	}, settings);
	return this.each(function() {
		
		//where do we start?
		if(!to && to != settings.endNumber) { to = settings.startNumber; }
		
		//set the countdown to the starting number
		jQuery(this).text(to).css("fontSize",settings.startFontSize);
		
		//loopage
		jQuery(this).animate({
			fontSize: settings.endFontSize
		}, settings.duration, "", function() {
			if(to > settings.endNumber + 1) {
				jQuery(this).css("fontSize", settings.startFontSize).text(to - 1).countDown(settings, to - 1);
			}
			else {
				settings.callBack(this);
			}
		});
				
	});
};
/* Quizfrage anzeigen */
var showQuestion = function(question, answer1,answer2,answer3,answer4,timeout) {
	content.empty();
	content.append("<table></table>");
	var contentTable = $("#content table");
	contentTable.append("<tr><th>"+question+"</th></tr>");
	contentTable.append("<tr><td>"+answer1+"</td></tr>");
	contentTable.append("<tr><td>"+answer2+"</td></tr>");
	contentTable.append("<tr><td>"+answer3+"</td></tr>");
	contentTable.append("<tr><td>"+answer4+"</td></tr>");
	content.append('<div id="countdown"></div>');
	jQuery("#content #countdown").countDown({
		startNumber: timeout,
		callBack: function(me) {
			jQuery(me).text("");
		}
	});
};
/* Startet das Spiel und l‰dt erste Frage*/
var startGame = function() {
	gamePhase = true;
	loginPhase = false;
	// Spielstart Paket senden
	$.ajax({ 
	    type: 'POST', 
	    url: 'CatalogServlet', 
	    data: { rID: '7' , filename: $(".catList .selected").text() }, 
	    dataType: 'json',
	    success: function (data) { 
	        $.each(data, function(index, element) {
	        	
	        });
	    }
	});
	// Quizfrage zum testen
	var question ="Ein Thread soll auf ein durch einen anderen Thread ausgel√∂stes Ereignis warten. Welcher Mechanismus ist geeignet?";
	var answer1 = "nur Semaphore";
	var answer2 = "nur Mutex";
	var answer3 = "weder Semaphore noch Mutexe";
	var answer4 = "sowohl Semaphore als auch Mutexe";
	var timeout = 30;

	showQuestion(question, answer1,answer2,answer3,answer4,timeout);
};
/* Spielstart Button anzeigen*/
var initGameStartButton = function () {
	var button = '<input class="startButton" type="button" name="Text 2" value="Spiel starten"></input>';
	content.append(button);
	startButtonVisible = true;
	$("#content .startButton").click(function(e) {
		startGame();
	});
};
/* Katalogliste anzeigen und selektierbar machen */
var initCatalogList = function () {
	var catElements = $(".catList");
	catElements.children().each(function() {   
		$(this).click(function(event){
			if(gamePhase==false) {
				$.ajax({ 
				    type: 'POST', 
				    url: 'CatalogServlet', 
				    data: { rID: '5' , filename: $(this).text() }, 
				    dataType: 'json',
				    success: function (data) { 
				        $.each(data, function(index, element) {
				        	
				        });
				    }
				});
		    	$(".catList .selected").removeClass("selected");
		    	$(this).addClass("selected");
		    	if(startButtonVisible==false){
		    		initGameStartButton();
		    	}
			}
		});
	});
};
/* Fragekatalog Auswahl zuslassen wenn Loginname eingegeben ist */
var loggedIn = function (e) {
	if(loginPhase==true){
		if(document.getElementById("nameInput").value.length <= 0){
			alert("Es wurde kein Loginname eingegeben. Bitte versuche es erneut.");
		}
		else {
			$("#login").hide();
			content.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");		
		}
		loginPhase=false;
	}
};
//var send = function (event, ElemDesc,ElemVal) {
//	//var button = event.target;
//	request = new XMLHttpRequest();
//	request.onreadystatechange = DatenVomServerVearbeiten;
//	request.open("POST","CatalogServlet",true);
//	request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
//	//console.log(getElem.val());
//	var requestString = "";
//	var length = arr.length;
//	for (var i = 0; i < length; i++) {
//		requestString += ""+ElemDesc[i]+"="+ElemVal[i]+"";
//	  // Do something with element i.
//	}
//	request.send(requestString);
//};


/* Wird ausgef¸hrt wenn HTML-Content geladen ist */
$(document).ready(function() {
	content = $("#content");
	//console.log("Content geladen");

	$("#loginButton").click(function(event){
		//send(event,"name",$("#nameInput").val());
		
		// Spielername senden
		$.ajax({ 
		    type: 'POST', 
		    url: 'PlayerServlet', 
		    data: { rID: '1' , name: $("#nameInput").val() }, 
		    dataType: 'json',
		    success: function (data) { 
		        $.each(data, function(index, element) {
		        	loggedIn();
		        	$("#highscore table tbody").append("<tr><td>"+element+"</td><td>0</td></tr>");
		        });
		    }
		});
		// Katalogliste anfragen
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
        closeConnection();
		
	});
	$("#nameInput").bind("keypress", {}, function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
	    if (code == 13) { //Enter keycode                        
	        e.preventDefault();
	        send(event,"name",$("#nameInput").val());
	    }
	});
	
});
