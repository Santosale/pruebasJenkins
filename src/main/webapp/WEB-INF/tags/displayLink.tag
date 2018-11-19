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
<%@ attribute name="parametre" required="false" %> 
<%@ attribute name="parametreValue" required="false" type="java.lang.Object" %>   
<%@ attribute name="parametre2" required="false" %> 
<%@ attribute name="parametreValue2" required="false" type="java.lang.Object" %> 
<%@ attribute name="parametre3" required="false" %> 
<%@ attribute name="parametreValue3" required="false" type="java.lang.Object" %> 
<%@ attribute name="code" required="true" %>
<%@ attribute name="css" required="false" %>  

<%-- Definition --%>


		<spring:url var="urlDisplayLink" value="${action}">
			<jstl:if test="${parametre!=null && parametreValue!=null}">
				<spring:param name="${parametre }" value="${parametreValue}" />
			</jstl:if>
			<jstl:if test="${parametre2!=null && parametreValue2!=null}">
				<spring:param name="${parametre2 }" value="${parametreValue2}" />
			</jstl:if>
			<jstl:if test="${parametre3!=null && parametreValue3!=null}">
				<spring:param name="${parametre3 }" value="${parametreValue3}" />
			</jstl:if>
		</spring:url>

		<p>
			<a href="${urlDisplayLink}" class="${css}"><spring:message code="${code }"/></a>
		</p>