<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


	<div class="container">
		<spring:message code="legalText.format.moment" var="momentFormat"/>
		
		<span class="display"><spring:message code="legalText.title"/></span><jstl:out value="  ${legalText.getTitle()}" />
		<br/>
		
		<span class="display"><spring:message code="legalText.body"/></span><jstl:out value="  ${legalText.getBody()}" />
		<br/>
		
		<span class="display"><spring:message code="legalText.moment"/></span><fmt:formatDate value="${legalText.getMoment()}" pattern="${momentFormat}"/>
		<br/>
		
		<jstl:if test="${!legalText.getLaws().isEmpty() }">
			<span class="display"><spring:message code="legalText.law"/></span>
			<jstl:forEach var="row" items="${legalText.getLaws()}">
				<jstl:out value="${row}" />
				<br/>
			</jstl:forEach>
		</jstl:if>
		
		<jstl:if test="${legalText.getDraft()}">
			<spring:message code="legalText.draft"/>
		</jstl:if>
		
		<jstl:if test="${!legalText.getDraft()}">
			<spring:message code="legalText.noDraft"/>
		</jstl:if>
		
		<br/>
		
	
	</div>
	
	
