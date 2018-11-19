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

<%@ attribute name="action" required="false" %>
<%@ attribute name="domain" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="url" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ attribute name="actor" required="false" %>
<%@ attribute name="content" required="false" %>
<%@ attribute name="code" required="false" %>

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
	</spring:url>
</jstl:if>

<jstl:if test="${content == null}">
	<display:column style="${style}">
		<a href="${url}"><spring:message code="${domain}.${action}" /></a>
	</display:column>
</jstl:if>

<jstl:if test="${content != null}">
	<spring:message code="${domain}.${code}" var="titleHeader"/>
	<display:column title="${titleHeader}" style="${style}">
		<a href="<jstl:out value='${url}'/>"><jstl:out value="${content}" /></a>
	</display:column>
</jstl:if>
