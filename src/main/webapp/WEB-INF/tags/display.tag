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

<%@ attribute name="code" required="true" %> 
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@ attribute name="codeMoment" required="false" %>
<%@ attribute name="formatNumber" required="false" type="java.lang.Boolean" %>  


<%-- Definition --%>
<jstl:if test="${codeMoment == null && (formatNumber==null || formatNumber==false)}">
<p>
	<span class="display">
		<spring:message code="${code}" />:
	</span> 
	<jstl:out value="${value}" />
</p>
</jstl:if>

<jstl:if test="${codeMoment != null && (formatNumber==null || formatNumber==false)}">
<spring:message code="${codeMoment }" var="momentFormat"/>
<p>
	<span class="display">
		<spring:message code="${code}" />
	</span>: 
<fmt:formatDate value="${value}" pattern="${momentFormat }"/>
</p>
</jstl:if>

<jstl:if test="${formatNumber == true}">
<p>
	<span class="display">
		<spring:message code="${code}" />
	</span>: 
<fmt:formatNumber value="${value}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
</p>
</jstl:if>

