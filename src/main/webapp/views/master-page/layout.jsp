<%--
 * layout.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<!DOCTYPE html>
<html>
	<head>
	
		<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/" />
		
		<meta charset="ISO-8859-1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
		<link rel="shortcut icon" href="favicon.ico"/> 
		
		<title><tiles:insertAttribute name="title" ignore="true" /></title>
		
		<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
		<link href="https://fonts.googleapis.com/css?family=Pacifico" rel="stylesheet">
		
		<!--[if lt IE 9]>
		    <script src="scripts/html5shiv.min.js"></script>
		<![endif]-->
		
		<!-- Bootstrap 3.3.7 -->
		<link type="text/css" rel="stylesheet" href="styles/bootstrap.min.css">	
		
		<link type="text/css" rel="stylesheet" href="styles/style.css">	
		
		<!--[if lt IE 9]>
			<script src="scripts/respond.min.js"></script>
		<![endif]-->
		
		<link rel="stylesheet" type="text/css" href="styles/cookieconsent.min.css" />
				
		<!-- jQuery 1.12.4 -->
		<script src="scripts/jquery.min.js"></script>
		
		<!-- Boostrap 3.3.7 -->
		<script src="scripts/bootstrap.min.js"></script>
	
	</head>
	<body>
	
		<tiles:insertAttribute name="header" />

		<section class="container">
			<h1>
				<tiles:insertAttribute name="title" />
			</h1>
			<br>
			<article>
				<tiles:insertAttribute name="body" />	
				<jstl:if test="${message != null}">
					<br>
					<span class="text-danger"><spring:message code="${message}" /></span>
				</jstl:if>	
			</article>
		</section>
	
		<tiles:insertAttribute name="footer" />
		
		<!-- Compatibilidad ES5 con IE8 -->
		<script src="scripts/polyfills.js"></script>
		
		<!-- Funciones comunes -->
		<script src="scripts/helpers.js"></script>
		
		<script src="scripts/cookieconsent.js"></script>
		<script>
		function a() {
			window.cookieconsent.initialise({
			  "palette": {
				  "popup": {
				      "background": "#252e39"
				    },
				    "button": {
				      "background": "#14a7d0"
				    }
				  },
				  "theme": "edgeless",
				  "position": "bottom-right",
				  "content": {
				    "message": "<spring:message code="master_page.cookies.message" />",
				    "dismiss": "<spring:message code="master_page.cookies.dismiss" />",
				    "link": "<spring:message code="master_page.cookies.link" />"
				  }
			});
		}
		
		if (window.addEventListener) {
			window.addEventListener('load', a);
		} else if (window.attachEvent) {
			window.attachEvent('onload', a);
		}
		
		$('img').on("error", function () {
	        $(this).attr('src', '${defaultImage}');
		});

		</script>
		
	</body>
</html>