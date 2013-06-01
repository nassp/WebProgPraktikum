/* Katalogliste anzeigen und (für Spielleiter) selektierbar machen */
var initCatalogList = function () {
	var catElements = $(".catList");
	catElements.children().each(function() {   
		$(".catList li").addClass("active");
		$(this).click(function(event){
			if(gamePhase==false) {
				sendMessages(5);
		    	$(".catList .selected").removeClass("selected");
		    	$(this).addClass("selected");
		    	if(startButtonVisible==false){
		    		initGameStartButton();
		    	}
			}
		});
	});
};

/* Fragekatalog Auswahl zuslassen wenn Loginname eingegeben ist */
var loggedIn = function (e) {
	if(loginPhase==true){
		content.empty();
		content.wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");	
		
		loginPhase=false;
	}
};
function testFunc()
{
	var eventSource = new EventSource('http://localhost:8080/WebQuiz/SSEServlet');
	//var eventSource = new EventSource('http://localhost:8080/WebQuiz/SSEServlet');
//	eventSource.addEventListener('simpleEvent', function(event) {
    	//$("#highscore table tbody").empty();
//    	console.log(event);
    	//console.log(event.data);
    	//var data = JSON.parse(event);
    	//$("#highscore table tbody").append("<tr><td>"+data.data+"</td><td>0</td></tr>");
    	//if (data.id==6){
//    	$.each(data, function(index, element) {
//    		if(index!="id"){
//		    	console.log("SSE: "+index+" "+element);
//		        
//    		}
//    	});
    	//}
//	}, false);
	eventSource.addEventListener('simpleEvent', function(simpleEvent) {
		  //console.log(simpleEvent);
	    	$("#highscore table tbody").empty();
	    	var data = JSON.parse(simpleEvent.data);
	    	readMessages(data);
	},false);
	$("#highscore table tbody").empty();
//    var source = new EventSource('http://localhost:8080/WebQuiz/PlayerServlet');  
//    source.onmessage=function(event){	
//
//    };

    /*source.addEventListener('server-time',function (e){
        alert('ea');
    },true);*/
}



