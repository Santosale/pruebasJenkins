<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>



<spring:message code="sponsorship.alt" var="sponsorshipAlt"/>


<div class="container">
	<acme:display code="sponsorship.amount" value="${sponsorship.getAmount()}"/>
</div>

<jstl:if test="${!linkBroken}">			
	<acme:image value="${sponsorship.getImage()}" alt="${sponsorshipAlt}" url="${sponsorship.getUrl()}"/>
</jstl:if>

<jstl:if test="${linkBroken}">			
	<acme:image value="${imageBroken}" alt="${sponsorshipAlt}" url="${sponsorship.getUrl()}"/>
</jstl:if>
<br><br>
<jstl:if test="${canDisplay}">
	<acme:displayLink parameter="bargainId" code="sponsorship.bargain" action="bargain/display.do" parameterValue="${sponsorship.getBargain().getId()}" css="btn btn-primary"></acme:displayLink>
</jstl:if>

<acme:displayLink parameter="sponsorId" code="sponsorship.sponsor" action="actor/sponsor/profile.do" parameterValue="${sponsorship.getSponsor().getId()}" css="btn btn-primary"></acme:displayLink>

	
	

	