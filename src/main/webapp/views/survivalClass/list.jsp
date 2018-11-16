<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<spring:url var="requestURI" value="survivalClass/list.do">
	<spring:param name="tripId" value="${tripId }"></spring:param>
</spring:url>


<display:table class="table table-striped table-bordered table-hover" name="survivalClasses" id="row"
	requestURI="${requestURI}" pagesize="5"
	>

	<security:authorize access="hasRole('MANAGER')">
		<display:column>
		<jstl:if test="${row.getTrip().getManager().getId()==managerForCompareId }">
			<a href="survivalClass/manager/edit.do?survivalClassId=${row.getId()}"> <spring:message
					code="survivalClass.edit" />
			</a>
			</jstl:if>
		</display:column>
	</security:authorize>

	<spring:message code="survivalClass.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}"></display:column>

	<spring:message code="survivalClass.description" var="descriptionHeader" />
	<display:column property="description" title="${descriptionHeader}"></display:column>

	<spring:message code="survivalClass.format.moment" var="momentFormat"/>
	<spring:message code="survivalClass.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}" format="{0,date,${momentFormat}}"></display:column>

	<spring:message code="survivalClass.location.name" var="locationNameHeader" />
	<display:column property="location.name" title="${locationNameHeader}"></display:column>
		
	<spring:message code="survivalClass.location.latitude" var="locationLatitudeHeader" />
	<display:column property="location.latitude" title="${locationLatitudeHeader}"></display:column>
		
	<spring:message code="survivalClass.location.longitude" var="locationLongitudeHeader" />
	<display:column property="location.longitude" title="${locationLongitudeHeader}"></display:column>

	<spring:url var="urlDisplay" value="survivalClass/display.do">
		<spring:param name="survivalClassId" value="${row.getId()}" />
	</spring:url>
	
	<display:column>
		<a href="${urlDisplay }"> <spring:message code="survivalClass.display" /></a>
	</display:column>

</display:table>

<br />
<security:authorize access="hasRole('MANAGER')">
<jstl:if test="${canCreate==true }">
	<div>
		<a href="survivalClass/manager/create.do?tripId=${tripId }">
			<spring:message code="survivalClass.create" />
		</a>
	</div>
	</jstl:if>
</security:authorize>
