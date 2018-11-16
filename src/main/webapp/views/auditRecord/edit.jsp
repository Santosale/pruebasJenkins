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

<form:form action="auditRecord/auditor/edit.do" modelAttribute="audit">
	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="trip" />
	<form:hidden path="auditor" />

	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="audit.title" />
		</form:label>
		<form:input class="form-control" path="title" />
		<form:errors class="text-danger" path="title" />
	</div>

	<div class="form-group"> 
		<form:label path="description">
			<spring:message code="audit.description" />
		</form:label>
		<form:input class="form-control" path="description" />
		<form:errors class="text-danger" path="description" />
	</div>

	<spring:message code="audit.format.moment" var="dateFormat" />
	<div class="form-group"> 
		<form:label path="moment" placeholder="${dateFormat}">
			<spring:message code="audit.moment" />
		</form:label>
		<form:input class="form-control" path="moment" readonly="readonly" placeholder="${dateFormat}"  />
		<form:errors class="text-danger" path="moment" />
	</div>

	<spring:message code="audit.attachments.placeholder" var="placeholderAttachments" />
	<div class="form-group"> 
		<form:label path="attachments">
			<spring:message code="audit.attachments" />:
		</form:label>
		<form:textarea class="form-control" path="attachments" placeholder="${placeholderAttachments }" />
		<form:errors class="text-danger" path="attachments" />
	</div>

	<div class="form-group"> 
		<form:label path="draft">
			<spring:message code="audit.draft" />:
		</form:label>
		<form:checkbox path="draft" />
	</div>

	<jstl:if test="${audit.isDraft()}">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="audit.save" />" >
		<jstl:if test="${audit.id != 0}">
			<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="audit.delete" />" onclick="return confirm('<spring:message code="audit.confirm.delete" />')" >
		</jstl:if>
	</jstl:if>
	
	<input type="button" name="cancel" class="btn btn-danger" value="<spring:message code="audit.cancel" />" onclick="javascript: relativeRedir('auditRecord/auditor/list.do');" >
	
</form:form>