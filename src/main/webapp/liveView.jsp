<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
	
	<script type="text/javascript">
		if ('WebSocket' in window) {
			var webSocket = new WebSocket("ws://localhost:8000/JavaProntoClient/websocket");
	    	webSocket.onopen = function(message) {
	    		console.log("Connected!");
	    	}
	    	webSocket.onmessage = function(message) {
	    		console.log(message.data);
	    	}
	    	webSocket.onclose = function(message) {
	    		console.log("Closed!");
	    		console.log(message.code);
	    		console.log(message.reason);
	    	}
	    	webSocket.onerror = function(message) {
	    		console.log("Error!");
	    	}	
		} else {
			alert("WebSocket isn't supported for this browser.")
		}
    </script>

    <title>VANTIQ LiveView</title>
    <body>
    	<h1>Live feed of events on ${eventName}</h1>
    	<form action="${pageContext.request.contextPath}/Catalog" method="post">
   			<input type="submit" name="viewCatalog" value="Return to Catalog">
   			<input type="hidden" name="catalogName" value="${catalogName}">
    	</form>
    </body>
</html>

<style type="text/css">
	h1,h3{
        text-align: center;
        font-family: arial, sans-serif;
    }
    form{
    	text-align: center;
    }
</style>