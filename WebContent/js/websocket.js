/**
 * processes the messages from the websocket
 * 
 */
function processWS() {

	ws = new WebSocket("ws://" + loginURL);
	// handles the messages from the websocket
	ws.onmessage = function(message) {
		var obj = jQuery.parseJSON(message.data);

		if (obj.id == "9") { // Question
			// if it is the last question tell the player to wait for the others
			// else show the question
			if (obj.question == "0" && obj.timeout == "0") {
				content.empty();
				content
						.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte warte bis alle Spieler fertig sind.</td></table>");
			} else {
				showQuestion(obj.question, obj.answer1, obj.answer2,
						obj.answer3, obj.answer4, obj.timeout);
			}
		} else if (obj.id == "11" && acceptAnswer) { // QuestionResult
			// color the right answer and the chosen one if given.
			acceptAnswer = false;
			var rightAnswer = obj.answer;
			if (rightAnswer == answered) {
				$("#answer" + rightAnswer).addClass("green");
			} else if (rightAnswer >= 10) {
				$(".answer").prop("disabled", true);
				$("#answer" + (rightAnswer - 10)).addClass("red");

			} else {
				$("#answer" + answered).addClass("red");
				$("#answer" + rightAnswer).addClass("green");

			}
			// send QuestionRequest after Timeout
			setTimeout(function() {
				var case8 = "{\"id\": \"8\"}";
				ws.send(case8);
				acceptAnswer = true;
			}, 3500);
		} else if (obj.id == "12") { // GameOver
			// show the achieved rank	
			alert(unescape("Herzlichen Gl%FCckwunsch!\nSie sind Rang "
					+ obj.ranking));
			location.reload();
		}else if (obj.id == "11") { // QuestionResult
  			// unrequested QuestionResult (Ignore)
  		} else if (obj.id == "255") {
			// show an errormessage and reload the site
			alert(obj.message);
			location.reload();
		} else {
			// show an errormessage and reload the site
			alert("Es ist etwas schief gegangen!");
			location.reload();
		}

	};

}