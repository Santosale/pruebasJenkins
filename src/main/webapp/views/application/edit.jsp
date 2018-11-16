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

<form:form action="application/manager/edit.do"
	modelAttribute="application">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="applicant" />
	<form:hidden path="comments" />
	<form:hidden path="creditCard" />
	<form:hidden path="moment" />
	<form:hidden path="survivalClasses" />
	<form:hidden path="trip" />
	<form:hidden path="status" />


<!--<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="application.moment" />:
		</form:label>
		<form:input class="form-control" path="moment" readOnly="readOnly"/>
		<form:errors class="text-danger" path="moment" />
	</div>-->

	<div class="form-group"> 
		<form:label path="deniedReason">
			<spring:message code="application.deniedReason" />:
		</form:label>
		<form:textarea path="deniedReason" />
		<form:errors css="text-danger" path="deniedReason" />
	</div>

	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="application.save" />" >
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="application.cancel" />" onclick="javascript: relativeRedir('application/manager/list.do?tripId=${application.getTrip().getId()}')" >

</form:form>