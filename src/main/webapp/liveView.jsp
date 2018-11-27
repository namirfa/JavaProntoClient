<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
    <title>VANTIQ LiveView</title>
    <body>
    	<h1>Live feed of events on ${eventName}</h1>
    	<form>
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