function readMessages(data) {
	// console.log(data);
	switch (data.id) {
	case 2:
		userId = data.userID;
		sseFunc();
		console.log(userId);
		loggedIn(data.userID);
		ws = new WebSocket("ws://" + loginURL);
		ws.onopen = function() {
			console.log("WEBSOCKET WURDE GEÖFFNET");
		};

		ws.onmessage = function(message) {
			console.log(message);
			var bool = false;
			// console.log(message.data);
			var obj = jQuery.parseJSON(message.data);

			if (obj.id == "9") {
				if (obj.question == "0" && obj.timeout == "0") {
					content.empty();
					content
							.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte warte bis alle Spieler fertig sind.</td></table>");
				} else {
					showQuestion(obj.question, obj.answer1, obj.answer2,
							obj.answer3, obj.answer4, obj.timeout);
				}
			} else if (obj.id == "11" && acceptAnswer) {
				acceptAnswer = false;
				var rightAnswer = obj.answer;
				if (rightAnswer == answered) {
					$("#answer" + rightAnswer).addClass("green");
				} else if (rightAnswer >= 10) {
					bool = true;
					$(".answer").prop("disabled", true);
					$("#answer" + (rightAnswer - 10)).addClass("red");

				} else {
					$("#answer" + answered).addClass("red");
					$("#answer" + rightAnswer).addClass("green");

				}
				if (bool) {
					setTimeout(function() {
						console.log("Timeout: Vor ws.send");
						var case8 = "{\"id\": \"8\"}";
						ws.send(case8);
						acceptAnswer = true;
						console.log("Timeout: Nach ws.send");
					}, 3500);
				} else {
					setTimeout(function() {
						console.log("Vor ws.send");
						var case8 = "{\"id\": \"8\"}";
						ws.send(case8);
						acceptAnswer = true;
						console.log("Nach ws.send");
					}, 3500);
				}
			} else if (obj.id == "12") {
				alert("Herzlichen Glückwunsch!\nSie sind Rang " + obj.ranking);
				location.reload();
			} else if (obj.id == "11") {

			} else if(obj.id == "255"){
				alert(obj.message);
				location.reload();
			}else {
				alert("Es ist etwas schief gegangen!");
				location.reload();
			}

		};

		break;
	case 4:
		$.each(data, function(index, element) {
			if (element.name != undefined) {
				$(".catList").append("<li>" + element.name + "</li>");
			}
		});
		initCatalogList();
		break;
	case 5:
		var catElements = $(".catList");
		$(".catList .selected").removeClass("selected");
		catElements.children().each(function() {
			if ($(this).text() == data.filename) {
				$(this).addClass("selected");
			}
		});
		break;
	case 6:
		$("#highscore table tbody").empty();
		var playerCounter = 1;
		$.each(data, function(index, element) {
			console.log("SSE: " + index + " " + element + "   playerCounter:"
					+ playerCounter);
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
				if (userId == element)
					$(
							'#highscore table tbody #player' + playerCounter
									+ ' td:first').css("text-decoration",
							"underline");
				playerCounter = playerCounter + 1;
			}

		});
		// Start Game Button für SuperUser anzeigen falls jetzt genug Player da
		// sind und ein Katalog ausgewählt ist
		var playerCount = $('#highscore table tbody tr').length;
		if (startButtonVisible == false && playerCount > 1 && userId == 0
				&& catalogSelected) {
			initGameStartButton();
		}
		break;
	case 7:
		startGame();
		gamePhase = true;
		loginPhase = false;
		sendMessages(8);
		break;

	case 255:
		alert(data.message);
		location.reload();
		break;

	default:
		break;
	}
}

function sendMessages(id) {

	switch (id) {
	case 1:
		$.ajax({
			type : 'POST',
			url : 'PlayerServlet',
			data : {
				rID : '1',
				name : $("#nameInput").val()
			},
			dataType : 'json',
			success : function(data) {
				console.log(data);
				readMessages(data);

			}
		});
		break;
	case 3:
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
		// == 0 klappt aus mir unbekannten Gründen nicht.
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
					$.each(data, function(index, element) {
					});
				}
			});
		}
		break;
	case 7:
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
				$.each(data, function(index, element) {

				});
			}
		});
		break;
	case 8:

		var case8 = '{"id": "8"}';
		ws.send(case8);
		acceptAnswer = true;

		break;
	case 10:
		var case10 = '{"id": "10", "answered": "'+answered+'"}';
		ws.send(case10);
		break;
	default:
		break;
	}

}
