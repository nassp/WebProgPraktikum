/* Katalogliste anzeigen und (für Spielleiter) selektierbar machen */
var initCatalogList = function () {
	var catElements = $(".catList");
	
	catElements.children().each(function() {   
		$(".catList li").addClass("active");
		$(this).click(function(event){
			
			// TODO: Prüfen ob dieser Spieler Spielleiter ist
			if(gamePhase==false) {
		    	$(".catList .selected").removeClass("selected");
		    	$(this).addClass("selected");
				sendMessages(5);
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
		
		content.empty();
		content.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");	
		
		loginPhase=false;
	}
};
var sseFunc = function () {
	var eventSource = new EventSource('http://localhost:8080/WebQuiz/SSEServlet');
	eventSource.addEventListener('playerListEvent', function(playerListEvent) {
	    var data = JSON.parse(playerListEvent.data);
	    readMessages(data);
	},false);
	eventSource.addEventListener('catalogChangeEvent', function(catalogChangeEvent) {
	    var data = JSON.parse(catalogChangeEvent.data);
	    readMessages(data);
	},false);
	eventSource.addEventListener('errorEvent', function(errorEvent) {
	    $("#highscore table tbody").empty();
	    var data = JSON.parse(errorEvent.data);
	    readMessages(data);
	},false);
	$("#highscore table tbody").empty();
};



