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

<form:form action="story/explorer/edit.do" modelAttribute="story">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="trip" />
	<form:hidden path="writer" />

	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="story.title" />:
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title" />
	</div>

	<div class="form-group"> 
		<form:label path="text">
			<spring:message code="story.text" />:
		</form:label>
		<form:textarea path="text"/>
		<form:errors class="text-danger" path="text" />
	</div>

	<div class="form-group"> 
		<form:label path="attachments">
			<spring:message code="story.attachments" />:
		</form:label>
		<form:input class="form-control" path="attachments" />
		<form:errors class="text-danger" path="attachments" />
	</div>

	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="story.save" />">
	
	<jstl:if test="${story.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="story.delete" />" onclick="return confirm('<spring:message code="story.confirm.delete" />')">
	</jstl:if>

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="story.cancel" />" onclick="javascript: relativeRedir('story/explorer/list.do?tripId=' + ${tripId});">
	
</form:form>