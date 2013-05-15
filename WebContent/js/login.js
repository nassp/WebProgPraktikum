/* Katalogliste anzeigen und (für Spielleiter) selektierbar machen */
var initCatalogList = function () {
	var catElements = $(".catList");
	catElements.children().each(function() {   
		$(".catList li").addClass("active");
		$(this).click(function(event){
			if(gamePhase==false) {
				$.ajax({ 
				    type: 'POST', 
				    url: 'CatalogServlet', 
				    data: { rID: '5' , filename: $(this).text() }, 
				    dataType: 'json',
				    success: function (data) { 
				        $.each(data, function(index, element) {
				        	
				        });
				    }
				});
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
	$("#highscore table tbody").empty();
    var source = new EventSource('http://localhost:8080/WebQuiz/PlayerServlet');  
    source.onmessage=function(event){	
    	$("#highscore table tbody").empty();
    	var data = JSON.parse(event.data);
    	if (data.id==6){
	    	$.each(data, function(index, element) {
	    		if(index!="id"){
			    	console.log("SSE: "+index+" "+element);
			        $("#highscore table tbody").append("<tr><td>"+element+"</td><td>0</td></tr>");
	    		}
	    	});
    	}
    };

    /*source.addEventListener('server-time',function (e){
        alert('ea');
    },true);*/
}



