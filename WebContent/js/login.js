/* Katalogliste anzeigen und (für Spielleiter) selektierbar machen */
var initCatalogList = function() {
	var catElements = $(".catList");
	catElements.children().each(function() {
		if(userID == "0")
			{
				$(".catList li").addClass("active");
			}
		$(this).click(function(event) {
			if (gamePhase == false && userID == "0") {
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
var loggedIn = function(userID) {
	if (loginPhase == true) {
		if (userID == 0) {
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



