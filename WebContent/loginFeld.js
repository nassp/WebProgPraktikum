$(document).ready(function() {
	$("#test").click(function(event){
		if(document.getElementById("Eingabefeld").value.length <= 0)
			{
			alert("Es wurde kein Loginname eingegeben. Bitte versuch es erneut.");
			}
		else
			{
				$("#loginEingabe").hide();
				$("#content").wrapInner('<table class="loginForm" id="loginEingabe"><td>Bitte w&aumlhle einen Fragekatalog aus.</td></table>');
			}
	});
});
