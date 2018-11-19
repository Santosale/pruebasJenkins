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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div>

	<jstl:if test="${termCondition==null}">
		<spring:url var="urlEditSpanish" value="termCondition/administrator/edit.do">
			<spring:param name="code" value="es" />
		</spring:url>
		
		<spring:url var="urlEditEnglish" value="termCondition/administrator/edit.do">
			<spring:param name="code" value="en" />
		</spring:url>
			
		<span><a href="${urlEditSpanish}" class="btn btn-primary"><spring:message code="term.condition.edit.spanish" /></a></span>
		<span><a href="${urlEditEnglish}"  class="btn btn-primary"><spring:message code="term.condition.edit.english" /></a></span>
		
		<br/>
		<br/>
		<br/>
		
		<p class="display"><spring:message code="term.condition.display.spanish" /></p>
		
		<p><jstl:out value="${termConditionSpanish}"></jstl:out><p>
		
		<br/>
		
		<p class="display"><spring:message code="term.condition.display.english" /></p>
		
		<p><jstl:out value="${termConditionEnglish}"></jstl:out></p>
		
	</jstl:if>
	
		<jstl:if test="${termConditionSpanish==null && termConditionEnglish==null}">
			<jstl:out value="${termCondition}"></jstl:out>
		</jstl:if>
	

</div>