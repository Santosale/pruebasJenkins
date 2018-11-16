<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<spring:message code="application.view" var="view" />


<display:table class="table table-striped table-bordered table-hover" name="applications" id="row"
	requestURI="application/${actortype}/list.do" pagesize="5"
	 defaultsort="${sortableNumber }">
	
	

	<jstl:choose>
		<jstl:when
			test="${row.getStatus().equals('PENDING') && row.getTrip().getStartDate().after(dateMonth)}">
			<jstl:set var="colorStyle" value="background-color: white;" />
		</jstl:when>
		<jstl:when test="${row.getStatus().equals('PENDING')}">
			<jstl:set var="colorStyle" value="background-color: red;" />
		</jstl:when>
		<jstl:when test="${row.getStatus().equals('REJECTED')}">
			<jstl:set var="colorStyle" value="background-color: #BDBDBD;" />
		</jstl:when>
		<jstl:when test="${row.getStatus().equals('DUE')}">
			<jstl:set var="colorStyle" value="background-color: yellow;" />
		</jstl:when>
		<jstl:when test="${row.getStatus().equals('ACCEPTED')}">
			<jstl:set var="colorStyle" value="background-color: #8BC34A;" />
		</jstl:when>
		<jstl:when test="${row.getStatus().equals('CANCELLED')}">
			<jstl:set var="colorStyle" value="background-color: cyan;" />
		</jstl:when>
	</jstl:choose>


	<spring:url var="urlEditRejected" value="application/manager/editRejected.do">
		<spring:param name="applicationId" value="${row.getId()}" />
	</spring:url>
	
	<spring:url var="urlEditDue" value="application/manager/editDue.do">
		<spring:param name="applicationId" value="${row.getId()}" />
	</spring:url>

	<spring:url var="urlAddCreditCard"
		value="application/explorer/addcreditcard.do">
		<spring:param name="applicationId" value="${row.getId()}" />
	</spring:url>

	<spring:url var="urlCancel" value="application/explorer/cancel.do">
		<spring:param name="applicationId" value="${row.getId()}" />
	</spring:url>

	<spring:url var="urlDisplay"
		value="application/${actorType}/display.do">
		<spring:param name="applicationId" value="${row.getId()}" />
	</spring:url>

	<security:authorize access="hasRole('EXPLORER')">

		<display:column style="${colorStyle}">
			<jstl:if test="${row.getStatus().equals('DUE')}">
				<spring:message code="application.addCreditCard" var="creditHeader" />
				<a href="${urlAddCreditCard}"> <spring:message
						code="application.addCreditCard" />
				</a>
			</jstl:if>
		</display:column>

		<display:column style="${colorStyle}">
			<jstl:if
				test="${row.getStatus().equals('ACCEPTED') && row.getTrip().getStartDate().compareTo(currentMoment) > 0}">
				<a href="${urlCancel}"> <spring:message
						code="application.cancelApplication" /></a>
			</jstl:if>
		</display:column>

	</security:authorize>

	<security:authorize access="hasRole('MANAGER')">
		<display:column style="${colorStyle}">

			<jstl:if test="${row.getStatus().equals('PENDING')}">
				<a href="${urlEditDue}"> <spring:message code="application.editDue" /></a>
				<br />
				<a href="${urlEditRejected}"> <spring:message code="application.editRejected" /></a>
			</jstl:if>
		</display:column>
	</security:authorize>


	<spring:message code="application.format.moment" var="momentFormat" />
	<spring:message code="application.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}"
		format="{0,date,${momentFormat}}" style="${colorStyle}" />

	<spring:message code="application.comments" var="commentsHeader" />
	<display:column property="comments" title="${commentsHeader}"
		style="${colorStyle}" />

	<spring:message code="application.status" var="statusHeader" />
	<display:column property="status" title="${statusHeader}"
		style="${colorStyle}" />

	<spring:message code="application.deniedReason"
		var="deniedReasonHeader" />
	<display:column property="deniedReason" title="${deniedReasonHeader}"
		style="${colorStyle}" />

	<spring:message code="application.creditCard" var="creditCardHeader" />
	<display:column property="creditCard.number"
		title="${creditCardHeader}" style="${colorStyle}" />

	<display:column style="${colorStyle}">
		<a href="${urlDisplay }"><spring:message
				code="application.display" /></a>
	</display:column>

</display:table>

<br />