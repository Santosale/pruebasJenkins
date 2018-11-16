<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="professionalRecord/ranger/edit.do" modelAttribute="professionalRecord">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="curriculum"/>
	
	<div class="form-group"> 
		<form:label path="companyName">
			<spring:message code="professionalRecord.companyName"/>
		</form:label>
		<form:input class="form-control" path="companyName"/>
		<form:errors class="text-danger" path="companyName"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="role">
			<spring:message code="professionalRecord.role"/>
		</form:label>
		<form:input class="form-control" path="role"/>
		<form:errors class="text-danger" path="role"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="link">
			<spring:message code="professionalRecord.link"/>
		</form:label>
		<form:input class="form-control" path="link"/>
		<form:errors class="text-danger" path="link"/>
	</div>
	
	<spring:message code="professionalRecord.format.date" var="dateFormat"/>
	<div class="form-group"> 
		<form:label path="startedWorkDate">
			<spring:message code="professionalRecord.startedWorkDate"/>
		</form:label>
		<form:input class="form-control" path="startedWorkDate" placeholder="dd/MM/yyyy"/>
		<form:errors class="text-danger" path="startedWorkDate"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="finishedWorkDate" placeholder="dd/MM/yyyy">
			<spring:message code="professionalRecord.finishedWorkDate"/>
		</form:label>
		<form:input class="form-control" path="finishedWorkDate" placeholder="${dateFormat}"/>
		<form:errors class="text-danger" path="finishedWorkDate"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="comments">
			<spring:message code="professionalRecord.comments"/>
		</form:label>
		<form:textarea class="form-control" path="comments"/>
		<form:errors class="text-danger" path="comments"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="professionalRecord.save" />">
	
	<jstl:if test="${professionalRecord.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="professionalRecord.delete" />" onclick="return confirm('<spring:message code="professionalRecord.confirm.delete" />')">
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="professionalRecord.cancel" />" onclick="javascript: relativeRedir('curriculum/display.do?curriculumId=${professionalRecord.getCurriculum().getId()}');" >

</form:form>