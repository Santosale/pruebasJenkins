<%--
 * submit.tag
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@ tag language="java" body-content="empty" %>

<%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%-- Attributes --%> 

<%@ attribute name="property" required="true" %> 
<%@ attribute name="domain" required="true" %>
<%@ attribute name="formatDate" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="sortable" required="false" %>

<%@ attribute name="formatPrice" required="false" %>
<%@ attribute name="code" required="false" %>
<%@ attribute name="codeSymbol1" required="false" %>
<%@ attribute name="codeSymbol2" required="false" %>

<jstl:if test="${formatDate == null}">
	<jstl:set var="formatDate" value="false" />
</jstl:if>

<jstl:if test="${formatPrice == null}">
	<jstl:set var="formatPrice" value="false" />
</jstl:if>

<jstl:if test="${sortable == null}">
	<jstl:set var="sortable" value="false" />
</jstl:if>

<jstl:if test="${style == null}">
	<jstl:set var="style" value="background: inherit;" />
</jstl:if>

<%-- Definition --%>
<jstl:if test="${!property.equals('user')}">
	<jstl:if test="${formatDate == true}">
		<spring:message code="${domain}.${property}" var="headerTitle" />
		<spring:message code="${domain}.format.moment" var="format"/>
		<display:column style="${style}" property="${property}" title="${headerTitle}" format="{0,date,${format}}" sortable="${sortable}"/>
	</jstl:if>
	
	<jstl:if test="${formatPrice == true}">
		<spring:message code="${domain}.${code}" var="headerTitle" />
		<display:column style="${style}" title="${headerTitle}" sortable="${sortable}">
			<spring:message code="${codeSymbol1}"/><fmt:formatNumber value="${property}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="${codeSymbol2}" />
		</display:column>
	</jstl:if>
	
	<jstl:if test="${formatDate == false && formatPrice == false}">
		<spring:message code="${domain}.${property}" var="headerTitle" />
		<display:column style="${style}" escapeXml="true" property="${property}" title="${headerTitle}" sortable="${sortable}" />
	</jstl:if>
</jstl:if>

