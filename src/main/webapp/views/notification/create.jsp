<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="notification/administrator/edit.do" modelAttribute="notification">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="sender" />
	<form:hidden path="recipient" />
	<form:hidden path="folder" />
	
	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="notification.moment" />
		</form:label>
		<form:input class="form-control" path="moment" readOnly="readOnly" />
		<form:errors class="text-danger" path="moment"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="priority">
			<spring:message code="notification.priority" />
		</form:label>
	 	<form:select class="form-control" path="priority">
			<form:option label="----" value="0" />
			<form:option label="HIGH" value="HIGH" />
			<form:option label="NEUTRAL" value="NEUTRAL" />
			<form:option label="LOW" value="LOW" />
		</form:select>
		<form:errors class="text-danger" path="priority"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="subject">
			<spring:message code="notification.subject" />
		</form:label>
		<form:input class="form-control" path="subject"/>
		<form:errors class="text-danger" path="subject"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="body">
			<spring:message code="notification.body" />
		</form:label>
		<form:input class="form-control" path="body"/>
		<form:errors class="text-danger" path="body"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="notification.save" />">
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="notification.cancel" />" onclick="javascript: relativeRedir('message/list.do');" >
	
	
</form:form>