function readMessages(data) {
	//console.log(data);
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
			//console.log(message.data);
			var obj = jQuery.parseJSON(message.data);

			if (obj.id == "9") {
				showQuestion(obj.question, obj.answer1, obj.answer2,
						obj.answer3, obj.answer4, obj.timeout);
			} else if (obj.id == "11" && acceptAnswer) {
				acceptAnswer=false;
				var rightAnswer = obj.answer;
				if (rightAnswer == answered) {
					$("#answer" + rightAnswer).addClass("green");
				} else if (rightAnswer >= 10) {
					bool = true;
					$(".answer").prop("disabled", true);
					$("#answer" + (rightAnswer-10)).addClass("red");
					
				} else {
					$("#answer" + answered).addClass("red");
					$("#answer" + rightAnswer).addClass("green");

				}
				if (bool) {
					setTimeout(function() {
						console.log("Timeout: Vor ws.send");
						ws.send(8);
						acceptAnswer=true;
						console.log("Timeout: Nach ws.send");
					}, 3500);
				} else {
					setTimeout(function() {
						console.log("Vor ws.send");
						ws.send(8);
						acceptAnswer=true;
						console.log("Nach ws.send");
					}, 3500);
				}
			} else if (obj.id == "12") {
				alert("Herzlichen Glückwunsch!\nSie sind Rang " + obj.ranking);
			} else if (obj.id == "11") {
				
			} else {
				alert("Es ist etwas schief gegangen!");
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
		var playerCount = $('#highscore table tbody tr').length;
		if (startButtonVisible == false && playerCount>1 && userId==0){
			initGameStartButton();
		}
		break;
	case 7:
		startGame();
		gamePhase = true;
		loginPhase = false;
		sendMessages(8);
		break;
	case 9:
		showQuestion(data.question, data.answer1, data.answer2, data.answer3,
				data.answer4, data.timeout);
		break;
	case 11:
		// alert(data.selection);
		// alert(data.correct);
		break;
	case 12:
		// alert("Herzlichen Glückwunsch, Sie sind: " + data.rank + ".");
		break;
	case 255:
		alert(data.message);
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
		/*
		 * $.ajax({ type : 'POST', url : 'CatalogServlet', data : { rID : '8', },
		 * dataType : 'json', success : function(data) { readMessages(data);
		 * $.each(data, function(index, element) {
		 * 
		 * }); } });
		 */

		ws.send(8);
		acceptAnswer=true;

		break;
	case 10:

		ws.send("10" + answered);

		break;
	default:
		break;
	}

}
