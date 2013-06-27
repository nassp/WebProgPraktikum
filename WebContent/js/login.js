/**
 * shows catalog list and makes it selectable for the superuser
 * 
 */
var initCatalogList = function() {
	var catElements = $(".catList");
	
	catElements.children().each(function() {   
		if(userId==0){
			$(".catList li").addClass("active");
		}
		// on click of one of the catalogs it checks if the game has not
		// started yet and the user is the superuser. If so the catalog
		// is selected.
		$(this).click(function(event){
			if(gamePhase==false && userId==0) {
				catalogSelected=true;
		    	$(".catList .selected").removeClass("selected");
		    	$(this).addClass("selected");
		    	// sends the selected catalog
				sendMessages(5);
				var playerCount = $('#highscore table tbody tr').length;
				// makes the startbutton visible if more than on player
				// is there and the user is the superuser.
				if (startButtonVisible == false && playerCount>1 && userId==0){
					initGameStartButton();
				}
			}
		});
	});
};

/**
 * tells the player what to do after the login
 * 
 */
var loggedIn = function() {
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

/**
 * adds the listeners for the Server Send Events
 * 
 */
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
	    var data = JSON.parse(gameStartEvent.data);
	    readMessages(data);
	},false);
	eventSource.addEventListener('errorEvent', function(errorEvent) {
	    var data = JSON.parse(errorEvent.data);
	    readMessages(data);
	},false);
	
};
