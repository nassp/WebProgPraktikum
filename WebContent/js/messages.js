/**
 * processes the messages from the server
 * 
 */
function readMessages(data) {
	switch (data.id) {
	case 2:
		userId = data.userID;
		sseFunc();
		loggedIn();
		processWS();

		break;
	case 4:
		
		// shows the catalognames in the cataloglist
		$.each(data, function(index, element) {
			if (element.name != undefined) {
				$(".catList").append(
						'<li data-qc="' + element.questions + '">'
								+ element.name + '</li>');
			}
		});
		initCatalogList();
		break;
	case 5:
		// selects the chosen catalog and deselects the old one
		var catElements = $(".catList");
		$(".catList .selected").removeClass("selected");
		catElements.children().each(function() {
			if ($(this).text() == data.filename) {
				$(this).addClass("selected");
			}
		});
		break;
	case 6:
		// shows the playerlist with playername and score
		$("#highscore table tbody").empty();
		var playerCounter = 1;
		$.each(data, function(index, element) {

			if (index == ("name" + playerCounter)) {
				$("#highscore table tbody").append(
						'<tr id="player' + playerCounter + '"></tr>');
				$('#highscore table tbody #player' + playerCounter).append(
						"<td>" + element + "</td>");
			}
			if (index == ("score" + playerCounter)) {
				$('#highscore table tbody #player' + playerCounter).append(
						"<td>" + element + "</td>");
			}
			if (index == ("id" + playerCounter)) {
				if (userId == element) {
					$(
							'#highscore table tbody #player' + playerCounter
									+ ' td:first').css("text-decoration",
							"underline");
				}
				playerCounter = playerCounter + 1;
			}

		});

		var playerCount = $('#highscore table tbody tr').length;

		// shows the game start button if enough players are there
		// and a catalog is selected
		if (gamePhase == false && playerCount > 1
				&& userId == 0 && catalogSelected == true) {
			initGameStartButton();
			
		// if one player left after a catalog was selected it tells
		// the superuser to wait for another player
		} else if (gamePhase == false && moreThan2 == true) {
			content.empty();
			content
					.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte warte bis mindestens noch 1 Spieler angemeldet ist.</td></table>");
		}
		break;
	case 7:
		startGame();
		gamePhase = true;
		loginPhase = false;
		// Requests a question, used only at the gamestart
		sendMessages(8);
		break;

	case 255:
		// Shows the errormessage and reloads the site
		alert(data.message);			
		location.reload();
		break;

	default:
		break;
	}
}

/**
 * sends messages to the server
 * 
 */
function sendMessages(id) {

	switch (id) {
	case 1:
		// sends the chosen loginname to the server
		$.ajax({
			type : 'POST',
			url : 'PlayerServlet',
			data : {
				rID : '1',
				name : $("#nameInput").val()
			},
			dataType : 'json',
			success : function(data) {
				readMessages(data);
			}
		});
		break;
	case 3:
		// requests a list of the questioncatalogs
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '3'
			},
			dataType : 'json',
			success : function(data) {
				readMessages(data);
			}
		});
		break;
	case 5:
		// the newly selected catalog is send
		if (userId == 0) {
			$.ajax({
				type : 'POST',
				url : 'CatalogServlet',
				data : {
					rID : '5',
					filename : $(".catList .selected").text()
				},
				dataType : 'json',
				success : function(data) {
					readMessages(data);
				}
			});
		}
		break;
	case 7:
		// sends the chosen catalog with the gamestart message
		$.ajax({
			type : 'POST',
			url : 'PlayerServlet',
			data : {
				rID : '7',
				filename : $(".catList .selected").text()
			},
			dataType : 'json',
			success : function(data) {
				readMessages(data);
			}
		});
		break;
	case 8:
		// sends a questionrequest to the server
		var case8 = '{"id": "8"}';
		ws.send(case8);
		acceptAnswer = true;

		break;
	case 10:
		// sends the answer to the server
		var case10 = '{"id": "10", "answered": "' + answered + '"}';
		ws.send(case10);
		break;
	default:
		break;
	}

}
