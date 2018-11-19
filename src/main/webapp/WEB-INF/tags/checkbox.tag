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

<%@ attribute name="path" required="true" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="style" required="false" %>

<jstl:if test="${style == null}">
	<jstl:set var="style" value="" />
</jstl:if>

<%-- Definition --%>

<div class="form-group" style="${style}">
	<form:label path="${path}">
		<spring:message code="${code}" />
	</form:label>
	<jstl:if test="${id == null}">
		<form:checkbox path="${path}"/>
	</jstl:if>
	<jstl:if test="${id != null}">
		<form:checkbox id="${id}" path="${path}"/>
	</jstl:if>
	<form:errors path="${path}" class="text-danger" />
</div>