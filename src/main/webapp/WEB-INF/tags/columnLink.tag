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

<%@ attribute name="action" required="true" %>
<%@ attribute name="domain" required="true" %>
<%@ attribute name="domain2" required="false" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="id2" required="false" %>
<%@ attribute name="url" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="actor" required="false" %>

<jstl:if test="${style == null}">
	<jstl:set var="style" value="background: inherit;" />
</jstl:if>

<%-- Definition --%>

<jstl:if test="${actor == null && url == null}">
	<spring:url value="${domain}/${action}.do" var="url">
		<spring:param name="${domain}Id" value="${id}" />
	</spring:url>
</jstl:if>

<jstl:if test="${actor != null && url == null}">
	<spring:url value="${domain}/${actor}/${action}.do" var="url">
		<spring:param name="${domain}Id" value="${id}" />
		<jstl:if test="${domain2!=null && id2!=null}">
			<spring:param name="${domain2}Id" value="${id2}" />
		</jstl:if>
	</spring:url>
</jstl:if>

<display:column style="${style}">
	<a href="${url}"><spring:message code="${domain}.${action}" /></a>
</display:column>
