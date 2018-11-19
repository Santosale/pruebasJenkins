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
<%@ attribute name="row" required="false" type="java.lang.Object" %>

<%-- Definition --%>
<spring:message code="${domain}.${property}" var="headerTitle" />

<jstl:if test="${property.equals('draft') }">

		<display:column title="${headerTitle}">
			<jstl:if test="${row.getDraft()==true }">
				<jstl:out value="X"/>
			</jstl:if>
		</display:column>
	</jstl:if> 	
	
	<jstl:if test="${property.equals('adultOnly') }">

		<display:column title="${headerTitle}">
			<jstl:if test="${row.getAdultOnly()==true }">
				<jstl:out value="X"/>
			</jstl:if>
		</display:column>
	</jstl:if> 	
		
	<jstl:if test="${property.equals('isDeleted') }">

		<display:column title="${headerTitle}">
			<jstl:if test="${row.getIsDeleted()==true }">
				<jstl:out value="X"/>
			</jstl:if>
		</display:column>
	</jstl:if> 		
	
	<jstl:if test="${property.equals('hasTaboo') }">
		<display:column title="${headerTitle}">
			<jstl:if test="${row.getHasTaboo()==true}">
				<jstl:out value="X"/>
			</jstl:if>
		</display:column>
	</jstl:if> 	
	
	<jstl:if test="${property.equals('system') }">
		<display:column title="${headerTitle}">
			<jstl:if test="${row.getSystem()==true}">
				<jstl:out value="X"/>
			</jstl:if>
		</display:column>
	</jstl:if> 	
