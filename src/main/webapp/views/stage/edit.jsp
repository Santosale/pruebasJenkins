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

<form:form action="stage/manager/edit.do" modelAttribute="stage">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="trip" />
	<form:hidden path="numStage" />

	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="stage.title" />:
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title" />
	</div>

	<div class="form-group"> 
		<form:label path="description">
			<spring:message code="stage.description" />:
		</form:label>
		<form:textarea class="form-control" path="description"/>
		<form:errors class="text-danger" path="description" />
	</div>

	<div class="form-group"> 
		<form:label path="price">
			<spring:message code="stage.price" />:
		</form:label>
		<form:input class="form-control" path="price" />
		<form:errors class="text-danger" path="price" />
	</div>

	<%-- <div class="form-group"> 
			<form:label path="numStage">
				<spring:message code="stage.numStage" />:
			</form:label>
			<form:input class="form-control" path="numStage" />
			<form:errors class="text-danger" path="numStage" />
		</div> --%>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="stage.save" />"> 
	
	<jstl:if test="${stage.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="stage.delete" />" onclick="return confirm('<spring:message code="stage.confirm.delete" />')">
	</jstl:if>

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="stage.cancel" />" onclick="javascript: relativeRedir('trip/display.do?tripId=' + ${tripId});">

</form:form>