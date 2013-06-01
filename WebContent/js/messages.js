function readMessages(data) {
	switch (data.id) {
	case 2:
		break;
	case 4:
		break;
	case 5:
		break;
	case 6:
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
		    type: 'POST', 
		    url: 'PlayerServlet', 
		    data: { rID: '1' , name: $("#nameInput").val() }, 
		    dataType: 'json',
		    success: function (data) {
		    	console.log(data);
		    	//alert("Hallo" + data);
		    	alert("Hallo2" + JSON.parse(data));
		     	if(data==255) {
	        		alert("Fehler, Spieler konnte nicht eingeloggt werden");
	        	}else if (data == 2){
	        		loggedIn();
	    			ws  = new WebSocket("ws://"+loginURL);
	    			ws.onopen = function(){
	    	        };
	    	        ws.onmessage = function(message){
	    	        	//MUSS AUSGEBAUT WERDEN !!!!
//	    	        	$(".catList").append("<li>"+message.data+"</li>");
	    	        };
//	        		$("#highscore table tbody").append("<tr><td>"+element+"</td><td>0</td></tr>"); 
	        	}
		    }
		});
		break;
	case 3:
		$.ajax({ 
		    type: 'POST', 
		    url: 'CatalogServlet', 
		    data: { rID: '3' }, 
		    dataType: 'json',
		    success: function (data) { 
		        $.each(data, function(index, element) {
		        	$(".catList").append("<li>"+element.name+"</li>");
		        });
		        initCatalogList();
		    }
		});
		break;
	case 5:
		$.ajax({
			type : 'POST',
			url : 'CatalogServlet',
			data : {
				rID : '5',
				filename : $(this).text()
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