<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div class="container">

	<strong><spring:message code="configuration.taboo.words" /></strong>
	<br>
	<jstl:forEach var="row" items="${configuration.getTabooWords()}">
		<jstl:out value="${row} "></jstl:out>
	</jstl:forEach>
	<br>
	<br>
	<acme:displayLink css="btn btn-primary" code="configuration.edit" action="configuration/administrator/edit.do" />


</div>
