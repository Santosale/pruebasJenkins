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

<spring:url var="urlURI" value="stage/manager/list.do" />
<display:table class="table table-striped table-bordered table-hover" name="stages" id="row" requestURI="${urlURI }"
	pagesize="5" >
	
	

	<security:authorize access="hasRole('MANAGER')">
		<display:column>
			<jstl:if test="${row.getTrip().getPublicationDate() > currentMoment}">
				<a href="stage/manager/edit.do?stageId=${row.getId()}"> <spring:message
						code="stage.edit" />
				</a>
			</jstl:if>
		</display:column>
	</security:authorize>


	<spring:message code="stage.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}"></display:column>

	<spring:message code="stage.description" var="descriptionHeader" />
	<display:column property="description" title="${descriptionHeader}"></display:column>

	<spring:message code="stage.price" var="priceHeader" />
	<display:column title="${priceHeader}">
		<spring:message code="stage.var1" />
		<fmt:formatNumber value="${row.getPrice()}" currencySymbol=""
			type="currency" />
		<spring:message code="stage.var2" />
	</display:column>

	<<%-- spring:message code="stage.price" var="priceHeader" />
	<display:column property="price" title="${priceHeader}"></display:column>
 --%>

	<spring:message code="stage.numStage" var="numStageHeader" />
	<display:column property="numStage" title="${numStageHeader}"></display:column>

	<spring:url var="urlDisplay" value="stage/manager/display.do">
		<spring:param name="stageId" value="${row.getId()}" />
	</spring:url>
	<display:column>
		<a href="${urlDisplay }"> <spring:message code="stage.display" /></a>
	</display:column>

</display:table>

<br />
