<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="survivalClass/manager/edit.do" modelAttribute="survivalClass">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="trip" />
	
			
	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="survivalClass.title" />
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title" />
	</div>
	
	<div class="form-group"> 
		<form:label path="description">
			<spring:message code="survivalClass.description" />
		</form:label>
		<form:input class="form-control" path="description"/>
		<form:errors class="text-danger" path="description" />
	</div>
	
	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="survivalClass.moment" />
		</form:label>
		<form:input class="form-control" path="moment" placeholder="dd/MM/yyyy HH:mm"/>
		<form:errors class="text-danger" path="moment" />
	</div>
	
	<div class="form-group"> 
		<form:label path="location.name">
			<spring:message code="survivalClass.location.name" />
		</form:label>
		<form:input class="form-control" path="location.name"/>
		<form:errors class="text-danger" path="location.name" />
	</div>
	
	<div class="form-group"> 
		<form:label path="location.latitude">
			<spring:message code="survivalClass.location.latitude" />
		</form:label>
		<form:input class="form-control" path="location.latitude"/>
		<form:errors class="text-danger" path="location.latitude" />
	</div>
	
	<div class="form-group"> 
		<form:label path="location.longitude">
			<spring:message code="survivalClass.location.longitude" />
		</form:label>
		<form:input class="form-control" path="location.longitude"/>
		<form:errors class="text-danger" path="location.longitude" />
	</div>
	
	<jstl:if test="${isHisSurvivalClass==true}">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="survivalClass.save" />">
	</jstl:if>
	
	<jstl:if test="${survivalClass.getId() != 0}">
		<jstl:if test="${isHisSurvivalClass==true }">
			<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="survivalClass.delete" />">
		</jstl:if>
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="survivalClass.cancel" />" onclick="javascript: relativeRedir('survivalClass/list.do?tripId=${survivalClass.getTrip().getId()}');">
	
</form:form>