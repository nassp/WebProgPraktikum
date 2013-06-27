var answered = 0;
/**
 * show question with possible answers and countdown
 * 
 * 
 * @param question
 * @param answer1
 * @param answer2
 * @param answer3
 * @param answer4
 * @param timeout
 */
var showQuestion = function(question, answer1, answer2, answer3, answer4,
		timeout) {
	
	stopCountdown = false;
	
	var a1 = '<input class="answer" id="answer0" type="button" name="Antwort 1" value=';
	var a2 = '<input class="answer" id="answer1" type="button" name="Antwort 2" value=';
	var a3 = '<input class="answer" id="answer2" type="button" name="Antwort 3" value=';
	var a4 = '<input class="answer" id="answer3" type="button" name="Antwort 4" value=';
	var input = '></input>';
	
	timeout = timeout/1000;

	// clear the current content
	content.empty();
	// show question
	content.append('<center><table id="quizTable"></table></center>');
	var contentTable = $("#content table");
	contentTable.append("<tr><th>" + question + "</th></tr>");
	contentTable.append("<tr><td>" + a1 + "\"" + answer1 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a2 + "\"" + answer2 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a3 + "\"" + answer3 + "\"" + input + "</td></tr>");
	contentTable.append("<tr><td>" + a4 + "\"" + answer4 + "\"" + input + "</td></tr>");
	content.append('<div id="countdownWrap"><div id="countdown"></div></div>');
	
	// initialize the countdown
	jQuery("#content #countdown").countDown({
		startNumber : timeout,
		callBack : function(me) {
			jQuery(me).text("");
		}
	});

	// stops the countdown and disables all buttons
	$(".answer").click(function(event) {
		stopCountdown = true;
		answered = $(".answer").index(this);
		$(".answer").prop("disabled", true);
		//sends the question answered message
		sendMessages(10);

	});
};
/**
 * removes the highlight form the selected catalog
 * 
 */
var startGame = function() {
	var catElements = $(".catList");
	catElements.children().each(function() {
		$(this).removeClass("active");
	});
};

/**
 * initializes the gamestart button
 * 
 */
var initGameStartButton = function() {
	moreThan2 = true;
	var button = '<input class="startButton" type="button" name="Text 2" value="Spiel starten"></input>';
	content.wrapInner(button);
	startButtonVisible = true;
	$("#content .startButton").click(function(e) {
		// Sends the start game message.
		sendMessages(7);
	});
};