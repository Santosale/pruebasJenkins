<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="educationrecord/ranger/edit.do" modelAttribute="educationRecord">

	<jstl:if test="${educationRecord.getId() == 0}">
		<h2><spring:message code="educationrecord.create" /></h2>
 	</jstl:if>
 	
	<jstl:if test="${educationRecord.getId() != 0}">
		<h2><spring:message code="educationrecord.edit" /></h2>
 	</jstl:if>

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="curriculum" />
	
	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="educationrecord.title" />
		</form:label>
		<form:input class="form-control" path="title" />
		<form:errors class="text-danger" path="title"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="startedStudyDate">
			<spring:message code="educationrecord.startedStudyDate" />
		</form:label>
		<form:input class="form-control" placeholder="dd/MM/yyyy" path="startedStudyDate" />
		<form:errors class="text-danger" path="startedStudyDate"/>
	</div>

	<div class="form-group"> 
		<form:label path="finishedStudyDate">
			<spring:message code="educationrecord.finishedStudyDate" />
		</form:label>
		<form:input class="form-control" placeholder="dd/MM/yyyy" path="finishedStudyDate" />
		<form:errors class="text-danger" path="finishedStudyDate"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="institution">
			<spring:message code="educationrecord.institution" />
		</form:label>
		<form:input class="form-control" path="institution" />
		<form:errors class="text-danger" path="institution"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="link">
			<spring:message code="educationrecord.link" />
		</form:label>
		<form:input class="form-control" path="link" />
		<form:errors class="text-danger" path="link"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="comments">
			<spring:message code="educationrecord.comments" />
		</form:label>
		<form:textarea path="comments"/>
		<form:errors class="text-danger" path="comments"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="educationrecord.save" />">

	<jstl:if test="${educationRecord.getId() != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="educationrecord.delete" />">
 	</jstl:if>
	
	<input type="button" name="cancel" class="btn btn-danger" value="<spring:message code="educationrecord.cancel" />" onclick="javascript: relativeRedir('curriculum/display.do?curriculumId=${curriculumId}')">
	
</form:form>