<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<%@ page import = "java.util.Date" %>
<%@ page import = "java.util.Calendar" %>

<jstl:choose>
		<jstl:when
			test="${application.getStatus().equals('PENDING') && application.getTrip().getStartDate().after(dateMonth)}">
			<jstl:set var="colorStyle" value="background-color: white;" />
		</jstl:when>
		<jstl:when test="${application.getStatus().equals('PENDING')}">
			<jstl:set var="colorStyle" value="background-color: red;" />
		</jstl:when>
		<jstl:when test="${application.getStatus().equals('REJECTED')}">
			<jstl:set var="colorStyle" value="background-color: #BDBDBD;" />
		</jstl:when>
		<jstl:when test="${application.getStatus().equals('DUE')}">
			<jstl:set var="colorStyle" value="background-color: yellow;" />
		</jstl:when>
		<jstl:when test="${application.getStatus().equals('ACCEPTED')}">
			<jstl:set var="colorStyle" value="background-color: #8BC34A;" />
		</jstl:when>
		<jstl:when test="${application.getStatus().equals('CANCELLED')}">
			<jstl:set var="colorStyle" value="background-color: cyan;" />
		</jstl:when>
</jstl:choose>
	
<div style="${colorStyle}">

	<spring:message code="application.format.moment" var="momentFormat" />
	
	<strong><spring:message code="application.moment"/></strong>: <fmt:formatDate pattern="${momentFormat}" value="${application.getMoment()}" />
	<br/>
	<strong><spring:message code="application.comments"/></strong>: <jstl:out value="  ${application.getComments()}" />
	<br/>
	<strong><spring:message code="application.status"/></strong>: <jstl:out value="  ${application.getStatus()}" />
	<br/>
	<strong><spring:message code="application.creditCard"/></strong>: 
	
	<jstl:if test="${application.getCreditCard() == null}">
		<spring:message code ="application.nocard" />		
	</jstl:if>
	
	
	<jstl:if test="${application.getCreditCard() != null}">
		<jstl:out value="${application.getCreditCard().getBrandName()}" /> - <jstl:out value="${application.getCreditCard().getNumber()}" />
	</jstl:if>
	
	<br/>
	<strong><spring:message code="application.applicant"/></strong>: <jstl:out value="${application.getApplicant().getName()}" /> <jstl:out value="${application.getApplicant().getSurname()}" />
	<br/>
	<strong><spring:message code="application.trip"/></strong>: <a href="trip/display.do?tripId=${application.getTrip().getId()}"><jstl:out value="${application.getTrip().getTitle()}" /></a>
	<br/>
	<strong><spring:message code="application.survivalClasses"/></strong>: 
	
	<jstl:forEach var="sc" items="${application.getSurvivalClasses()}">
		<a href="survivalClass/display.do?survivalClassId=${sc.getId()}"><jstl:out value="${sc.getTitle()}" /></a>
	</jstl:forEach>
	
	<br/>

</div>