$(document).ready(function() {
	$("#submitButton").click(function(event){
		if(document.getElementById("nameInput").value.length <= 0)
			{
			alert("Es wurde kein Loginname eingegeben. Bitte versuch es erneut.");
			}
		else
			{
				$("#login").hide();
				$("#content").wrapInner('<table class="center" id="loginEingabe"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>');
			}
	});
});
