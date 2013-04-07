$(document).ready(function() {
	$("#loginButton").click(function(event){
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
	}
});
