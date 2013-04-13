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
			$("#highscore table tbody").append("<tr><td>"+request.responseText+"</td><td>0</td></tr>");
			break;
		default: console.log("noch kein open fuer XMLHttpRequest-Objekt erfolgt");
		}	
}
var sendLogin = function (event) {
	var button = event.target;
	var nameInput = $("#nameInput");
	request = new XMLHttpRequest();
	request.onreadystatechange = DatenVomServerVearbeiten;
	request.open("POST","CatalogServlet",true);
	request.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	console.log(nameInput.val());
	request.send("name="+nameInput.val()+"");
};
$(document).ready(function() {
	console.log("Content geladen");
	$("#loginButton").click(function(event){
		sendLogin(event);
	});
	/*$("#loginButton").click(function(event){
		if(document.getElementById("nameInput").value.length <= 0)
		{
			alert("Es wurde kein Loginname eingegeben. Bitte versuch es erneut.");
		}
		else
		{
			$("#login").hide();
			$("#content").wrapInner("<table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");		
		}
	});
	
	$("#nameInput").bind("keypress", {}, keypressInBox);
	
	function keypressInBox(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
	    if (code == 13) { //Enter keycode                        
	        e.preventDefault();
	        if(document.getElementById("nameInput").value.length <= 0)
			{
	        	alert("Es wurde kein Loginname eingegeben. Bitte versuch es erneut.");
			}
	        else
			{
				$("#login").hide();
				$("#content").wrapInner("</table><table class=\"center\" id=\"loginEingabe\"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>");
				
			}

	    }
	}*/
});
