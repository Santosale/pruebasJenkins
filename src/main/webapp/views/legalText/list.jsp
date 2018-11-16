<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="legalTexts" id="row" requestURI="${requestURI}" pagesize="5">
	
	<display:column>
		<span>
			<!-- Si es draft podemos editarlo -->
			<jstl:if test="${row.getDraft()}">
				<spring:url var="urlEdit" value="legalText/administrator/edit.do">
					<spring:param name="legalTextId" value="${row.getId()}" />
				</spring:url>
				<a href="${urlEdit }"> <spring:message code="legalText.edit" /></a>
			</jstl:if>
		</span>
	</display:column>
	
	<spring:message code="legalText.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}" ></display:column>
	
	<spring:message code="legalText.body" var="bodyHeader" />
	<display:column property="body" title="${bodyHeader}" ></display:column>
	
	<spring:message code="legalText.format.moment" var="momentFormat"/>
	<spring:message code="legalText.moment" var="momentHeader" />
	<display:column title="${momentHeader}" >
		<fmt:formatDate value="${row.getMoment()}" pattern="${momentFormat}"/>
	</display:column>
	
	<jstl:if test="${table!=null}">
		<spring:message code="legalText.references" var="referencesHeader" />
		<display:column title="${referencesHeader}">
			<jstl:out value="${table.get(row)}"/>
		</display:column>
	</jstl:if>
	
	<display:column>
		<spring:url var="urlDisplay" value="legalText/display.do">
			<spring:param name="legalTextId" value="${row.getId()}" />
		</spring:url>
		<a href="${urlDisplay }"> <spring:message code="legalText.display" /></a>
	</display:column>
	
	
</display:table>


<spring:url var="urlCreate" value="legalText/administrator/create.do">
</spring:url>
		
<a href="${urlCreate }"> <spring:message code="legalText.create" /></a>