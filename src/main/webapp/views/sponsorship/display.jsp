<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


	<div class="container">
		<a title="Banner" href="${sponsorship.getLinkInfoPage()}"><img src="${sponsorship.getBanner()}" alt="Banner" width="400px" height="200px" style="margin-left:15px;" /></a>
		
		<br />
		<span class="display"><spring:message code="sponsorship.linkInfoPage"/></span><jstl:out value="  ${sponsorship.getLinkInfoPage()}" />
		<br />
		
		<spring:url var="urlSponsor" value="actor/sponsor/display.do">
			<spring:param name="sponsorId" value="${sponsorship.getSponsor().getId()}" />
		</spring:url>
		
		<spring:url var="urlCreditCard" value="creditcard/sponsor/display.do">
			<spring:param name="creditCardId" value="${sponsorship.getCreditCard().getId()}" />
		</spring:url>
		
		<spring:url var="urlTrip" value="trip/display.do">
			<spring:param name="tripId" value="${sponsorship.getTrip().getId()}" />
		</spring:url>
			
		<a href="${urlTrip}" ><spring:message code="sponsorship.trip"/></a>
		<br/>	
		<a href="${urlSponsor}" ><spring:message code="sponsorship.sponsor"/></a>
		<br/>
		<security:authorize access="isAuthenticated()">
			<security:authentication var="principal" property="principal.username"/>
			
			<jstl:if test="${sponsorship.getSponsor().getUserAccount().getUsername().equals(principal)}">
				<a href="${urlCreditCard}" ><spring:message code="sponsorship.creditCard"/></a>
				<br/>
			</jstl:if>
		</security:authorize>
		
		
		
	
	</div>
	
	
