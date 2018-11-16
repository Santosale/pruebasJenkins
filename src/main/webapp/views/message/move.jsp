<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="message/move.do" modelAttribute="myMessage">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="sender" />
	<form:hidden path="recipient" />
	<form:hidden path="moment" />
	<form:hidden path="priority" />
	<form:hidden path="subject" />
	<form:hidden path="body" />
		
	<div class="form-group"> 
		<form:label path="folder">
			<spring:message code="message.folder.choose" />:
		</form:label>	
		<form:select class="form-control" path="folder">
			<form:option value="${defaultFolder.getId()}" label="${defaultFolder.getName()}" />		
			<form:options items="${folders}" itemValue="id" itemLabel="name" />
		</form:select>
		<form:errors class="text-danger" path="folder" />
	</div>
	
	<jstl:if test="${canEdit==true}">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="message.save" />"> 
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="message.cancel" />" onclick="javascript: relativeRedir('message/list.do');">
		
</form:form>