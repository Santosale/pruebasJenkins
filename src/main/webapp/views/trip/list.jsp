<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<style>

#searchTitle, #returnTitle {
	text-align: center; 
	font-size: 50px; 
	cursor: pointer;
}

#searchForm {
	opacity:0; 
	display: none; 
	justify-content: center; 
    text-align: center;
	align-items: center; 
	flex-flow: column; 
	margin-bottom:50px
}

#search {
	height: 50px; 
	width: 90%; 
	border-radius: 5px; 
	border: 1px solid black; 
	padding: 10px; 
	font-size: 30px;
}

#resultTable {
	display: none; 
	margin: auto!important; 
	margin-bottom: 50px!important;
}

#initialTable {
	display: flex; 
	justify-content: center; 
	align-items: center; 
	flex-flow: column;
}

#lupa_ico {
	width: 50px; 
	transform: translateY(10px);
}

</style>

<jstl:if test="${requestURI.equals('trip/list.do')}">
	<div>
		<p id="searchTitle">
			<spring:message code="trip.search.title"/>		
			<img id="lupa_ico" src="images/search_icon.png"/>
		</p>
	</div>
</jstl:if>

<div id="searchForm">
	<input name="keyword" id="search" type="text" placeholder="<spring:message code="trip.search.placeholder"/>">
</div>

<table class="table table-striped table-bordered table-hover" id="resultTable">

	<thead>
		<tr>
			<th><spring:message code="trip.title" /></th>
			<th><spring:message code="trip.price" /></th>
			<th><spring:message code="trip.description" /></th>
			<th><spring:message code="trip.explorerRequirements" /></th>		
			<th></th>	
		</tr>
	</thead>
	
	<tbody id="body">

	</tbody>

</table>

<div id="initialTable">

	<display:table class="table table-striped table-bordered table-hover" name="trips" id="row" requestURI="${requestURI}" pagesize="5">
		
		
			
		<!-- Si es su manager, puede editar el trip -->
		<security:authorize access="isAuthenticated()">
			<security:authentication var="principal" property="principal.username"/>
			
			<display:column>
				<!-- Vemos que sea su manager para mostrar las columnas -->
				<jstl:if test="${row.getManager().getUserAccount().getUsername().equals(principal)}">
					
					<!-- Si no se ha publicado, puede editar el trip -->
					<jstl:if test="${row.getPublicationDate().compareTo(currentMoment) > 0}">
							
							<spring:url var="urlEdit" value="trip/manager/edit.do">
								<spring:param name="tripId" value="${row.getId()}" />
							</spring:url>
							<a href="${urlEdit}"> <spring:message code="trip.edit.trip" /></a>
						
					</jstl:if>
				</jstl:if>
			</display:column>
				
			<!-- Si no ha empezado, no se ha cancelado y se ha publicado, puede cancelar el trip -->
			<display:column>
				<jstl:if test="${row.getManager().getUserAccount().getUsername().equals(principal)}">
					<jstl:if test="${row.getCancellationReason() == null && row.getPublicationDate().compareTo(currentMoment) < 0 && row.getStartDate().compareTo(currentMoment) > 0}">
						<br />
						<spring:url var="urlCancel" value="trip/manager/cancel.do">
							<spring:param name="tripId" value="${row.getId()}" />
						</spring:url>
						<a href="${urlCancel}"> <spring:message code="trip.cancel" /></a>
					</jstl:if>
				
				</jstl:if>
			</display:column>
			
			<!-- Si no se ha publicado y tiene stages se puede publicar -->
			<display:column>
				<jstl:if test="${row.getManager().getUserAccount().getUsername().equals(principal)}">
					<jstl:if test="${row.getPublicationDate().compareTo(currentMoment) > 0 && row.getStages().size()>0}">
						<br />
						<spring:url var="urlPublish" value="trip/manager/publish.do">
							<spring:param name="tripId" value="${row.getId()}" />
						</spring:url>
						<a href="${urlPublish}"> <spring:message code="trip.publish" /></a>
					</jstl:if>
				
				</jstl:if>
			</display:column>
		</security:authorize>
		<br />
					
		
		<spring:message code="trip.title" var="titleHeader" />
		<display:column property="title" title="${titleHeader}"></display:column>
		
		<spring:message code="trip.description" var="descriptionHeader" />
		<display:column property="description" title="${descriptionHeader}"></display:column>
		
		<spring:message code="trip.format.date" var="dateFormat"/>
		<spring:message code="trip.startDate" var="startDateHeader"/>
		<display:column property="startDate" title="${startDateHeader}" format="{0,date,${dateFormat}}"></display:column>
		
		<spring:message code="trip.endDate" var="endDateHeader"/>
		<display:column property="endDate" title="${endDateHeader}" format="{0,date,${dateFormat}}"></display:column>
		
		<spring:message code="trip.explorerRequirements" var="explorerRequirementsHeader" />
		<display:column property="explorerRequirements" title="${explorerRequirementsHeader}"></display:column>
		
		<spring:message code="trip.price" var="priceHeader"/>
		<display:column title="${priceHeader}">
			<spring:message code="trip.var1" /><fmt:formatNumber value="${row.getPrice()}" currencySymbol="" type="currency"/><spring:message code="trip.var2"/>
		</display:column>
	
	
		<spring:url var="urlDisplay" value="trip/display.do">
			<spring:param name="tripId" value="${row.getId() }" />
		</spring:url>
		<display:column>
			<a href="${urlDisplay }"><spring:message code="trip.display"/></a>
		</display:column>
		
		
	</display:table>
	
	<!-- Si es un manager le permitimos crear un trip-->
	<security:authorize access="hasRole('MANAGER')">
		<spring:url var="urlcreateTrip" value="trip/manager/create.do"></spring:url>
		<a href="${urlcreateTrip}" ><spring:message code="trip.create.trip"/></a>
	</security:authorize>

</div>