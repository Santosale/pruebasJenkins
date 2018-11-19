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
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%-- Attributes --%> 

<%@ attribute name="name" required="true" %> 
<%@ attribute name="code" required="true" %>
<%@ attribute name="codeDelete" required="false" %>
<%@ attribute name="disabled" required="false" %>

<jstl:if test="${disabled == null}">
	<jstl:set var="disabled" value="" />
</jstl:if>

<%-- Definition --%>

<jstl:if test="${codeDelete != null}">
	<button class="btn btn-warning" type="submit" name="${name}" ${disabled} onclick="return confirm('<spring:message code="${codeDelete }" />')">
		<spring:message code="${code}" />
	</button>
</jstl:if>

<jstl:if test="${codeDelete == null && name.equals('delete')}">
	<button class="btn btn-warning" type="submit" name="${name}" ${disabled}>
		<spring:message code="${code}" />
	</button>
</jstl:if>

<jstl:if test="${codeDelete == null && !name.equals('delete')}">
	<button class="btn btn-primary" type="submit" name="${name}" ${disabled}>
		<spring:message code="${code}" />
	</button>
</jstl:if>

