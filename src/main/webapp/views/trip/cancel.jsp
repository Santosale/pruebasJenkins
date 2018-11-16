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

<form:form action="trip/manager/cancel.do" modelAttribute="trip">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="price" />
	<form:hidden path="manager" />
	<form:hidden path="stages" />
	<form:hidden path="title" />
	<form:hidden path="description" />
	<form:hidden path="ticker" />
	<form:hidden path="explorerRequirements" />
	<form:hidden path="startDate" />
	<form:hidden path="endDate" />
	<form:hidden path="publicationDate" />
	<form:hidden path="ranger" />
	<form:hidden path="category" />
	<form:hidden path="legalText" />

	<div class="form-group"> 
		<form:label path="cancellationReason">
			<spring:message code="trip.cancellationReason" />:
		</form:label>
		<form:textarea class="form-control" path="cancellationReason" />
		<form:errors class="text-danger" path="cancellationReason" />
	</div>

	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="trip.save" />">

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="trip.cancel" />" onclick="javascript: relativeRedir('trip/manager/list.do');">

</form:form>