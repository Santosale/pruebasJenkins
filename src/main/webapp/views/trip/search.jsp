<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="trip/explorer/search.do" modelAttribute="finder">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="explorer" />	
	<form:hidden path="trips" />
	<form:hidden path="moment" />		

	<div class="form-group"> 
		<form:label path="keyWord">
			<spring:message code="trip.keyWord" />
		</form:label>
		<form:input class="form-control" path="keyWord" />
		<form:errors class="text-danger" path="keyWord" />
	</div>

	<div class="form-group"> 
		<form:label path="minPrice">
			<spring:message code="trip.minPrice" />
		</form:label>
		<form:input class="form-control" path="minPrice"/>
		<form:errors class="text-danger" path="minPrice" />
	</div>
	
	<div class="form-group"> 
		<form:label path="maxPrice">
			<spring:message code="trip.maxPrice" />
		</form:label>
		<form:input class="form-control" path="maxPrice"/>
		<form:errors class="text-danger" path="maxPrice" />
	</div>
	
	<spring:message code="trip.format.date" var="dateFormat"/>
	<div class="form-group"> 
		<form:label path="startedDate">
			<spring:message code="trip.startedDate" />
		</form:label>
		<form:input class="form-control" path="startedDate" placeholder="${dateFormat }" />
		<form:errors class="text-danger" path="startedDate" />
	</div>
	
	<div class="form-group"> 
		<form:label path="finishedDate">
			<spring:message code="trip.finishedDate" />
		</form:label>
		<form:input class="form-control" path="finishedDate" placeholder="${dateFormat }" />
		<form:errors class="text-danger" path="finishedDate" />
	</div>

	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="trip.search" />">
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="trip.cancel" />" onclick="javascript: window.location.replace('trip/explorer/list.do')">
	<br />

</form:form>