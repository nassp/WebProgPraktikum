var loginPhase = true;
var DatenVomServerVearbeiten = function() {
	var textfeld= $("#login");
	switch (request.readyState){
		case 2:
			console.log("ready State = Anfrage wurde gesendet\n status Server"+request.status);
		break;
		case 3:
			console.log("ready State = ein Teil der Antwort vom Server erhalten\n status Server"+request.status);
		break;
		case 4:
			console.log("ready State = Antwort vom Server vollstaendig erhalten\n Server Antwort: "+request.responseText+"\n status Server"+request.status);
			loggedIn();
			$("#highscore table tbody").append("<tr><td>"+request.responseText+"</td><td>0</td></tr>");
			break;
		default: console.log("noch kein open fuer XMLHttpRequest-Objekt erfolgt");
		}	
};
var loggedIn = function (e) {
	if(loginPhase==true){
		if(document.getElementById("nameInput").value.length <= 0){
			alert("Es wurde kein Loginname eingegeben. Bitte versuche es erneut.");
		}
		else {
			$("#login").hide();
			$("#content").wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");		
		}
		loginPhase=false;
	}
};
var send = function (event,ElemDesc,ElemVal) {
	//var button = event.target;
	request = new XMLHttpRequest();
	request.onreadystatechange = DatenVomServerVearbeiten;
	request.open("POST","CatalogServlet",true);
	request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	//console.log(getElem.val());
	request.send(""+ElemDesc+"="+ElemVal+"");
};
//var showCatalogNames = function () {
//	var catArr = new Array();
//	catArr[0] = "one.cat";
//	catArr[1] = "simple.cat";
//	catArr[2] = "Systemprogrammierung.cat";
//	for (var i = catArr.length-1;i>=0 ;i--){
//		$(".catList").append("<li>"+catArr[i]+"</li>");
//	}
//	console.log(catArr);
//};
var loginURL = "localhost:8080/WebQuiz/LogicServlet";
var ws = new WebSocket("ws://"+loginURL);

$(document).ready(function() {
	
	console.log("Content geladen");
	//showCatalogNames();

	$("#loginButton").click(function(event){
		send(event,"name",$("#nameInput").val());
		
		$.ajax({ 
		    type: 'POST', 
		    url: 'CatalogServlet', 
		    data: { rID: '3' }, 
		    dataType: 'json',
		    success: function (data) { 
		        $.each(data, function(index, element) {
		        	$(".catList").append("<li>"+element.name+"</li>");
		        });
		    }
		});
		var catElements = $(".catList");
		catElements.children().each(function() {   
			$(this).click(function(event){
				send(event,"catalog",$(this).text()); 
			});
		});
		
		ws.onopen = function(){
        };
        ws.onmessage = function(message){
        	$(".catList").append("<li>"+message.data+"</li>");
        };
        function postToServer(){
            ws.send("LoginServlet(WebSocket): "+$("#nameInput").val());
        }
        function closeConnection(){
            ws.close();
        }
        postToServer();
        closeConnection();
		
	});
	$("#nameInput").bind("keypress", {}, function(e){
		var code = (e.keyCode ? e.keyCode : e.which);
	    if (code == 13) { //Enter keycode                        
	        e.preventDefault();
	        send(event,"name",$("#nameInput").val());
	    }
	});
	
});
