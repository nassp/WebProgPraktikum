var answered = 0;
/* Quizfrage anzeigen */
var showQuestion = function(question, answer1, answer2, answer3, answer4,
		timeout) {
	
	stopCountdown = false;
	
	var a1 = '<input class="answer" id="answer0" type="button" name="Antwort 1" value=';
	var a2 = '<input class="answer" id="answer1" type="button" name="Antwort 2" value=';
	var a3 = '<input class="answer" id="answer2" type="button" name="Antwort 3" value=';
	var a4 = '<input class="answer" id="answer3" type="button" name="Antwort 4" value=';
	var input = '></input>';
	
	timeout = timeout/1000;
	
	console.log("Timeout: " + timeout);

	content.empty();
	content.append('<table id="quizTable"></table>');
	var contentTable = $("#content table");
	contentTable.append("<tr><th>" + question + "</th></tr>");
	contentTable.append("<tr><td>" + a1 + "\"" + answer1 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a2 + "\"" + answer2 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a3 + "\"" + answer3 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a4 + "\"" + answer4 + "\"" + input + "</td></tr>");
	content.append('<div id="countdownWrap"><div id="countdown"></div></div>');
	jQuery("#content #countdown").countDown({
		startNumber : timeout,
		callBack : function(me) {
			jQuery(me).text("");
		}
	});

	$(".answer").click(function(event) {

		stopCountdown = true;

		answered = $(".answer").index(this);
		$(".answer").prop("disabled", true);

		sendMessages(10);

	});
};
/* Startet das Spiel und l‰dt erste Frage */
var startGame = function() {
	var catElements = $(".catList");
	catElements.children().each(function() {
		$(this).removeClass("active");
	});


	// Quizfrage zum testen
	/*var question = "Ein Thread soll auf ein durch einen anderen Thread ausgel√∂stes Ereignis warten. Welcher Mechanismus ist geeignet?";
	var answer1 = '<input class="answer" type="button" name="Antwort 1" value="Nur Semaphore"></input>';
	var answer2 = '<input class="answer" type="button" name="Antwort 2" value="Nur Mutexe"></input>';
	var answer3 = '<input class="answer" type="button" name="Antwort 3" value="Weder Semaphore noch Mutexe"></input>';
	var answer4 = '<input class="answer" type="button" name="Antwort 4" value="Sowohl Semaphore als auch Mutexe"></input>';
	var timeout = 30;*/

	//showQuestion(question, answer1, answer2, answer3, answer4, timeout);
};
/* Spielstart Button anzeigen */
var initGameStartButton = function() {
	var button = '<input class="startButton" type="button" name="Text 2" value="Spiel starten"></input>';
	content.wrapInner(button);
	startButtonVisible = true;
	$("#content .startButton").click(function(e) {
		// Spielstart Paket senden
		sendMessages(7);
	});
};