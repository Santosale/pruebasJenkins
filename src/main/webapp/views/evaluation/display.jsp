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

<div class="container">
	<span class="display">
	<spring:message code="evaluation.puntuation"/>:
	<jstl:if test="${evaluation.getPuntuation() == 1}">
		<img src="images/1stars.png" alt="One star" width="100"  />
	</jstl:if>
	<jstl:if test="${evaluation.getPuntuation() == 2}">
		<img src="images/2stars.png" alt="Two stars" width="100"  />
	</jstl:if>
	<jstl:if test="${evaluation.getPuntuation() == 3}">
		<img src="images/3stars.png" alt="Three stars" width="100"   />
	</jstl:if>
	<jstl:if test="${evaluation.getPuntuation() == 4}">
		<img src="images/4stars.png" alt="Four stars" width="100"   />
	</jstl:if>
	<jstl:if test="${evaluation.getPuntuation() == 5}">
		<img src="images/5stars.png" alt="Five stars" width="100"  />
	</jstl:if>
	</span>
	
	<br/><br/>
	
	<spring:message code="evaluation.yes" var="yes" />
	<spring:message code="evaluation.no" var="no" />
	<acme:display code="evaluation.content" value="${evaluation.getContent()}" />
	<span class="display">
	<spring:message code="evaluation.isAnonymous" />:
	<jstl:if test="${evaluation.getIsAnonymous()}">
		<img src="images/yes.png" alt="${yes}" width="20"/>
	</jstl:if>
	<jstl:if test="${!evaluation.getIsAnonymous()}">
		<img src="images/no.png" alt="${no}" width="20"/>
	</jstl:if>
	</span>
	
	<br/><br/>
	
	<security:authorize access="hasAnyRole('USER', 'COMPANY')">
	<security:authentication var="principal" property="principal.username"/>
	</security:authorize>
	<jstl:if test="${!evaluation.getIsAnonymous() || principal.equals(evaluation.getUser().getUserAccount().getUsername())}">
		<acme:displayLink parameter="userId" code="evaluation.user.name" action="actor/user/display.do" parameterValue="${evaluation.getUser().getId()}" ></acme:displayLink>
	</jstl:if>
	
	<jstl:if test="${!principal.equals(evaluation.getCompany().getUserAccount().getUsername())}">
		<acme:displayLink parameter="actorId" code="evaluation.company.name" action="actor/company/profile.do" parameterValue="${evaluation.getCompany().getId()}" ></acme:displayLink>
	</jstl:if>
</div>	

	