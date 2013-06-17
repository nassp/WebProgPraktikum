function readMessages(data) {

	switch (data.id) {
	case 2:
		sseFunc();
		userId = data.userID;
		console.log(userId);
		loggedIn(data.userID);
		ws = new WebSocket("ws://" + loginURL);
		ws.onopen = function() {
		};
		ws.onmessage = function(message) {
			// MUSS AUSGEBAUT WERDEN !!!!
			// $(".catList").append("<li>"+message.data+"</li>");
		};
		// $("#highscore table
		// tbody").append("<tr><td>"+element+"</td><td>0</td></tr>");
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
		$.each(data, function(index, element) {
			if (index != "id") {
				console.log("SSE: " + index + " " + element);
				$("#highscore table tbody").append(
						"<tr><td>" + element + "</td><td>0</td></tr>");
			}
		});
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
		// alert("Herzlichen Gl�ckwunsch, Sie sind: " + data.rank + ".");
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
		// == 0 klappt aus mir unbekannten Gr�nden nicht.
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

		ws = new WebSocket("ws://" + loginURL);
		ws.onopen = function() {
			ws.send(8);
		};
		ws.onmessage = function(message) {
			var string = message.data;
			if (string == 9) {
				ws.onmessage = function(message) {
					var timeout = message.data;
					ws.onmessage = function(message) {
						var question = message.data;
						ws.onmessage = function(message) {
							var answer1 = message.data;
							ws.onmessage = function(message) {
								var answer2 = message.data;
								ws.onmessage = function(message) {
									var answer3 = message.data;
									ws.onmessage = function(message) {
										var answer4 = message.data;
										showQuestion(question, answer1,
												answer2, answer3, answer4,
												timeout);
									};
								};
							};
						};
					};
				};

			}
		};

		break;
	case 10:
		alert("Es wurde die Frage mit index: " + answered + " gewaehlt!");
		/*
		 * $.ajax({ type : 'POST', url : 'LogicServlet', data : { rID : '10',
		 * value : answered }, dataType : 'json', success : function(data) {
		 * readMessages(data); $.each(data, function(index, element) {
		 * 
		 * }); } });
		 */

		ws = new WebSocket("ws://" + loginURL);
		ws.onopen = function() {
			ws.send("11" + answered);
		};
		ws.onmessage = function(message) {
			var string = message.data;
			if (string == 11) {
				ws.onmessage = function(message) {
					var rightAnswer = message.data;
					if (rightAnswer == answered) {
						alert("Richtige Antwort");
						rightAnswer += 2;
						$("#quizTable tr:nth-child("+rightAnswer+") td input").addClass("green");
					} else {
						alert("Falsche Antwort" + rightAnswer);
						rightAnswer += 2;
						$("#quizTable tr:nth-child("+rightAnswer+") td input").addClass("green");
						answered += 2;
						$("#quizTable tr:nth-child("+rightAnswer+") td input").addClass("red");
					}
				};
			} else if (caseNumber == 11 && string == answered) {
				alert("Richtige Antwort");
			}
		};
		// ws.send(answered);

		break;
	default:
		break;
	}

}