<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="curriculum/ranger/edit.do" modelAttribute="curriculum">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="ranger"/>
	
	<jstl:if test="${curriculum.getId()==0}">	
		<form:hidden path="ticker"/>
	</jstl:if>
	
	<jstl:if test="${curriculum.getId()!=0}">	
		<div class="form-group"> 
			<form:label path="ticker">
				<spring:message code="curriculum.ticker"/>
			</form:label>
			<form:input class="form-control" path="ticker" readonly="true"/>
		</div>
	</jstl:if>
	
	<div class="form-group"> 
		<form:label path="fullNamePR">
			<spring:message code="curriculum.fullNamePR"/>
		</form:label>
		<form:input class="form-control" path="fullNamePR"/>
		<form:errors class="text-danger" path="fullNamePR"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="photoPR">
			<spring:message code="curriculum.photoPR"/>
		</form:label>
		<form:input class="form-control" path="photoPR"/>
		<form:errors class="text-danger" path="photoPR"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="emailPR">
			<spring:message code="curriculum.emailPR"/>
		</form:label>
		<form:input class="form-control" path="emailPR"/>
		<form:errors class="text-danger" path="emailPR"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="phonePR">
			<spring:message code="curriculum.phonePR"/>
		</form:label>
		<form:input class="form-control" path="phonePR"/>
		<form:errors class="text-danger" path="phonePR"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="linkedinPR">
			<spring:message code="curriculum.linkedinPR"/>
		</form:label>
		<form:input class="form-control" path="linkedinPR"/>
		<form:errors class="text-danger" path="linkedinPR"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="curriculum.save" />">
	
	<jstl:if test="${curriculum.id != 0}">
		<input type="submit" name="delete" class="btn btn-warning" value="<spring:message code="curriculum.delete" />" onclick="return confirm('<spring:message code="curriculum.confirm.delete" />')">
		<input type="button" name="cancel" class="btn btn-danger" value="<spring:message code="curriculum.cancel" />" onclick="javascript: relativeRedir('curriculum/display.do?curriculumId=${curriculum.getId()}');">
	</jstl:if>
	
	<jstl:if test="${curriculum.id == 0}">
		<input type="button" name="cancel" class="btn btn-danger" value="<spring:message code="curriculum.cancel" />" onclick="javascript: relativeRedir('/');" >
	</jstl:if>
		
</form:form>