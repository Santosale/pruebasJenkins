<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="note/manager/edit.do" modelAttribute="note">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="auditor" />	
	
	<jstl:if test="${note.getMomentReply()== null} ">
		<form:hidden path="momentReply"/>
	</jstl:if>
	
	<form:hidden path="trip" />	
	
	<spring:message code="note.format.moment" var="momentFormat"/>
	
	<div class="form-group"> 
		<form:label path="remark">
			<spring:message code="note.remark" />:
		</form:label>
		<form:input class="form-control" path="remark" readOnly="readOnly"/>
		<form:errors class="text-danger" path="remark" />
	</div>
	
	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="note.moment" />:
		</form:label>
		<form:input class="form-control" path="moment" readOnly="readOnly"/>
		<form:errors class="text-danger" path="moment" />
	</div>
	
	<div class="form-group"> 
		<form:label path="managerReply">
			<spring:message code="note.managerReply" />:
		</form:label>
		<form:textarea path="managerReply" />
		<form:errors class="text-danger" path="managerReply" />
	</div>
	
	<jstl:if test="${note.getMomentReply()!= null}">
		<div class="form-group"> 
			<form:label path="momentReply">
				<spring:message code="note.momentReply" />:
			</form:label>
			<form:input class="form-control" path="momentReply" readOnly="readOnly" />
			<form:errors class="text-danger" path="momentReply" />
		</div>
	</jstl:if>
	
	<security:authorize access="hasRole('MANAGER')">
	
		<jstl:if test="${canEdit==true }">
			<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="note.save" />">
		</jstl:if>
		
		<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="note.cancel" />" onclick="javascript: relativeRedir('note/managerAuditor/list.do?tripId=${note.getTrip().getId()}')">

	</security:authorize>
	
	<security:authorize access="hasRole('AUDITOR')">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="note.save" />">
		<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="note.cancel" />" onclick="javascript: relativeRedir('note/managerAuditor/list.do?tripId=${note.getTrip().getId()}')" >
	</security:authorize>

</form:form>