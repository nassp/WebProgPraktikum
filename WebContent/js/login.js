/* Katalogliste anzeigen und (für Spielleiter) selektierbar machen */
var initCatalogList = function() {
	var catElements = $(".catList");
	
	catElements.children().each(function() {   
		if(userId==0){
			$(".catList li").addClass("active");
		}
		$(this).click(function(event){
			console.log(userId);
			if(gamePhase==false && userId==0) {
		    	$(".catList .selected").removeClass("selected");
		    	$(this).addClass("selected");
				sendMessages(5);
				if (startButtonVisible == false) {
					initGameStartButton();
				}
			}
		});
	});
};

/* Fragekatalog Auswahl zuslassen wenn Loginname eingegeben ist */
var loggedIn = function(userId) {
	if (loginPhase == true) {
		if (userId == 0) {
			content.empty();
			content.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");
		} else {
			content.empty();
			content.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte warte bis ein Fragekatalog ausgew&aumlhlt wurde.</td></table>");
		}
		loginPhase = false;
	}
};
var sseFunc = function () {
	var eventSource = new EventSource('http://localhost:8080/WebQuiz/SSEServlet?uID='+userId);
	eventSource.addEventListener('playerListEvent', function(playerListEvent) {
	    var data = JSON.parse(playerListEvent.data); 
	    readMessages(data);
	},false);
	eventSource.addEventListener('catalogChangeEvent', function(catalogChangeEvent) {
	    var data = JSON.parse(catalogChangeEvent.data);
	    readMessages(data);
	},false);
	eventSource.addEventListener('gameStartEvent', function(gameStartEvent) {
		console.log(gameStartEvent);
	    var data = JSON.parse(gameStartEvent.data);
	    readMessages(data);
	},false);
	eventSource.addEventListener('errorEvent', function(errorEvent) {
	    var data = JSON.parse(errorEvent.data);
	    readMessages(data);
	},false);
	
};



