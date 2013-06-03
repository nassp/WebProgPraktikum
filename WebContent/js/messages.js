function readMessages(data) {

	switch (data.id) {
	case 2:
		//alert(data.userID);
		loggedIn();
		ws = new WebSocket("ws://" + loginURL);
		ws.onopen = function() {
		};
		ws.onmessage = function(message) {
			// MUSS AUSGEBAUT WERDEN !!!!
			// $(".catList").append("<li>"+message.data+"</li>");
		};
		// $("#highscore table
		// tbody").append("<tr><td>"+element+"</td><td>0</td></tr>");
		break;
	case 4:
		break;
	case 5:
		var catElements = $(".catList");
		$(".catList .selected").removeClass("selected");
		catElements.children().each(function() {   			
	    	if($(this).text()==data.filename){
	    		$(this).addClass("selected");
	    	}
		});
		break;
	case 6:
		$("#highscore table tbody").empty();
		$.each(data, function(index, element) {
			if (index != "id") {
				console.log("SSE: " + index + " " + element);
				$("#highscore table tbody").append(
						"<tr><td>" + element + "</td><td>0</td></tr>");
			}
		});
		break;
	case 7:
		break;
	case 9:
		break;
	case 11:
		break;
	case 12:
		break;
	case 255:
		break;

	default:
		break;
	}
}

function sendMessages(id) {

	switch (id) {
	case 1:
		$.ajax({
			type : 'POST',
			url : 'PlayerServlet',
			data : {
				rID : '1',
				name : $("#nameInput").val()
			},
			dataType : 'json',
			success : function(data) {
				console.log(data);
				readMessages(data);
				
			}
		});
		break;
	case 3:
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '3'
			},
			dataType : 'json',
			success : function(data) {
				$.each(data, function(index, element) {
					$(".catList").append("<li>" + element.name + "</li>");
				});
				initCatalogList();
			}
		});
		break;
	case 5:		
		var selectedElem = $(".catList .selected");
		
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '5',
				filename : selectedElem.text()
			},
			dataType : 'json',
			success : function(data) {

				$.each(data, function(index, element) {

				});
			}
		});
		break;
	case 7:
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '7',
				filename : $(".catList .selected").text()
			},
			dataType : 'json',
			success : function(data) {
				$.each(data, function(index, element) {

				});
			}
		});
		break;
	case 8:
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '8',
			},
			dataType : 'json',
			success : function(data) {
				$.each(data, function(index, element) {

				});
			}
		});
		break;
	case 10:
		$.ajax({
			type : 'POST',
			url : 'LogicServlet',
			data : {
				rID : '10',
				value : $(".answer").index(this)
			},
			dataType : 'json',
			success : function(data) {
				$.each(data, function(index, element) {

				});
			}
		});
		break;
	default:
		break;
	}

}