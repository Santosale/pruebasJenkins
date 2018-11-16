<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="miscellaneousRecord/ranger/edit.do" modelAttribute="miscellaneousRecord">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="curriculum" />

	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="miscellaneousRecord.title" />
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title" />
	</div>

	<div class="form-group"> 
		<form:label path="link">
			<spring:message code="miscellaneousRecord.link" />
		</form:label>
		<form:input class="form-control" path="link" />
		<form:errors class="text-danger" path="link" />
	</div>

	<div class="form-group"> 
		<form:label path="comments">
			<spring:message code="miscellaneousRecord.comments" />
		</form:label>
		<form:textarea class="form-control" path="comments" />
		<form:errors class="text-danger" path="comments" />
	</div>

	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="miscellaneousRecord.save" />">
	
	<jstl:if test="${miscellaneousRecord.id != 0}">
		<input type="submit" class="btn btn-danger" name="delete" value="<spring:message code="miscellaneousRecord.delete" />" onclick="return confirm('<spring:message code="miscellaneousRecord.confirm.delete" />')">
	</jstl:if>

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="miscellaneousRecord.cancel" />" onclick="javascript: relativeRedir('curriculum/display.do?curriculumId=${miscellaneousRecord.getCurriculum().getId()}');">
	
</form:form>