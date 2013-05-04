/* Quizfrage anzeigen */
var showQuestion = function(question, answer1,answer2,answer3,answer4,timeout) {
	content.empty();
	content.append('<table id="quizTable"></table>');
	var contentTable = $("#content table");
	contentTable.append("<tr><th>"+question+"</th></tr>");
	contentTable.append("<tr><td>"+answer1+"</td></tr>");
	contentTable.append("<tr><td>"+answer2+"</td></tr>");
	contentTable.append("<tr><td>"+answer3+"</td></tr>");
	contentTable.append("<tr><td>"+answer4+"</td></tr>");
	content.append('<div id="countdownWrap"><div id="countdown"></div></div>');
	jQuery("#content #countdown").countDown({
		startNumber: timeout,
		callBack: function(me) {
			jQuery(me).text("");
		}
	});
};
/* Startet das Spiel und l‰dt erste Frage*/
var startGame = function() {
	var catElements = $(".catList");
	catElements.children().each(function() {   
		$(this).removeClass("active");
	});
	gamePhase = true;
	loginPhase = false;
	// Spielstart Paket senden
	$.ajax({ 
	    type: 'POST', 
	    url: 'CatalogServlet', 
	    data: { rID: '7' , filename: $(".catList .selected").text() }, 
	    dataType: 'json',
	    success: function (data) { 
	        $.each(data, function(index, element) {
	        	
	        });
	    }
	});
	// Quizfrage zum testen
	var question ="Ein Thread soll auf ein durch einen anderen Thread ausgel√∂stes Ereignis warten. Welcher Mechanismus ist geeignet?";
	var answer1 = '<input class="answer" type="button" name="Antwort 1" value="Nur Semaphore"></input>';
	var answer2 = '<input class="answer" type="button" name="Antwort 2" value="Nur Mutexe"></input>';
	var answer3 = '<input class="answer" type="button" name="Antwort 3" value="Weder Semaphore noch Mutexe"></input>';
	var answer4 = '<input class="answer" type="button" name="Antwort 4" value="Sowohl Semaphore als auch Mutexe"></input>';
	var timeout = 30;

	showQuestion(question, answer1,answer2,answer3,answer4,timeout);
};
/* Spielstart Button anzeigen*/
var initGameStartButton = function () {
	var button = '<input class="startButton" type="button" name="Text 2" value="Spiel starten"></input>';
	content.wrapInner(button);
	startButtonVisible = true;
	$("#content .startButton").click(function(e) {
		startGame();
	});
};