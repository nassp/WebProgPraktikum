function readMessages(data) {

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
		var case11 = false;
		var timeout = 0;
		var question = "";
		var answer1 = "";
		var answer2 = "";
		var answer3 = "";
		var answer4 = "";
		var count = 0;

		ws.onmessage = function(message) {
			var string = message.data;
			if (string == 9) {
				count = 1;
			} else if (count == 1) {
				count = 2;
				timeout = message.data;
			} else if (count == 2) {
				count = 3;
				question = message.data;
			} else if (count == 3) {
				count = 4;
				answer1 = message.data;
			} else if (count == 4) {
				count = 5;
				answer2 = message.data;
			} else if (count == 5) {
				count = 6;
				answer3 = message.data;
			} else if (count == 6) {
				count = 0;
				answer4 = message.data;
				showQuestion(question, answer1, answer2, answer3, answer4,
						timeout);
			} else if (string == 11) {
				case11 = true;
			} else if (case11) {
				var rightAnswer = string;
				if (rightAnswer == answered) {
					alert("Richtige Antwort");
					$("#answer"+rightAnswer).addClass("green");
//					rightAnswer += 2;
//					$("#quizTable tr:nth-child(" + rightAnswer + ") td input")
//							.addClass("green");
				} else {
					alert("Falsche Antwort" + rightAnswer);
					$("#answer"+answered).addClass("red");
					$("#answer"+rightAnswer).addClass("green");
//					rightAnswer += 2;
//					$("#quizTable tr:nth-child(" + rightAnswer + ") td input")
//							.addClass("green");
//					answered += 2;
//					$("#quizTable tr:nth-child(" + rightAnswer + ") td input")
//							.addClass("red");
				}
				case11 = false;
			    setTimeout(function(){
			        ws.send(8);
			       },5000);

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


		ws.send("11" + answered);


		// ws.send(answered);

		break;
	default:
		break;
	}

}