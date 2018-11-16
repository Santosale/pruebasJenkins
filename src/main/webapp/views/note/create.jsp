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

<form:form action="note/auditor/create.do" modelAttribute="note">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="auditor" />
	<form:hidden path="trip" />

	<spring:message code="note.format.moment" var="momentFormat" />

	<div class="form-group"> 
		<form:label path="remark">
			<spring:message code="note.remark" />:
		</form:label>
		<form:input class="form-control" path="remark" />
		<form:errors class="text-danger" path="remark" />
	</div>

	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="note.moment" />:
		</form:label>
		<form:input class="form-control" path="moment" readOnly="readOnly"/>
		<form:errors class="text-danger" path="moment" />
	</div>

	<security:authorize access="hasRole('AUDITOR')">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="note.save" />">
		<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="note.cancel" />" onclick="javascript: relativeRedir('note/auditor/list.do');">
	</security:authorize>

</form:form>