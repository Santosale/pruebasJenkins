<%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<!-- Atributes -->
<%@ attribute name="action" required="true" %>
<%@ attribute name="parameter" required="false" %> 
<%@ attribute name="parameterValue" required="false" type="java.lang.Object" %>   
<%@ attribute name="parameter2" required="false" %> 
<%@ attribute name="parameterValue2" required="false" type="java.lang.Object" %> 
<%@ attribute name="parameter3" required="false" %> 
<%@ attribute name="parameterValue3" required="false" type="java.lang.Object" %> 
<%@ attribute name="code" required="true" %>
<%@ attribute name="css" required="false" %>
<%@ attribute name="newTab" required="false" %>   

<%-- Definition --%>

<spring:url var="urlDisplayLink" value="${action}">
	<jstl:if test="${parameter != null && parameterValue != null}">
		<spring:param name="${parameter}" value="${parameterValue}" />
	</jstl:if>
	<jstl:if test="${parameter2 != null && parameterValue2 != null}">
		<spring:param name="${parameter2}" value="${parameterValue2}" />
	</jstl:if>
	<jstl:if test="${parameter3 != null && parameterValue3 != null}">
		<spring:param name="${parameter3}" value="${parameterValue3}" />
	</jstl:if>
</spring:url>

<jstl:if test="${newTab!=null}">
	<p><a href="${urlDisplayLink}" target="_blank" class="${css}"><spring:message code="${code}"/></a></p>
</jstl:if>

<jstl:if test="${newTab==null}">
	<p><a href="${urlDisplayLink}" class="${css}"><spring:message code="${code}"/></a></p>
</jstl:if>