<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="message/edit.do" modelAttribute="myMessage">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="sender" />
	
	<jstl:if test="${myMessage.getId()!=0 }">
		<form:hidden path="recipient" />
	</jstl:if>
	
	<form:hidden path="folder" />
	
	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="message.moment" />
		</form:label>
		<form:input class="form-control" path="moment" readOnly="readOnly"/>
		<form:errors class="text-danger" path="moment"/>
	</div>
		
	<jstl:if test="${myMessage.getId()!=0 }">
	
		<div class="form-group"> 
			<form:label path="priority">
				<spring:message code="message.priority" />
			</form:label>
			<form:input class="form-control" path="priority" readOnly="readOnly"/>
			<form:errors class="text-danger" path="priority"/>
		</div>
		
		<div class="form-group"> 
			<form:label path="subject">
				<spring:message code="message.subject" />
			</form:label>
			<form:input class="form-control" path="subject" readOnly="readOnly"/>
			<form:errors class="text-danger" path="subject"/>
		</div>
		
		<div class="form-group"> 
			<form:label path="body">
				<spring:message code="message.body" />
			</form:label>
			<form:input class="form-control" path="body" readOnly="readOnly" />
			<form:errors class="text-danger" path="body"/>
		</div>
	
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() == 0}">
		
		<div class="form-group"> 
			<form:label path="priority">
				<spring:message code="message.priority" />
			</form:label>
			<form:input class="form-control" path="priority" placeholder="HIGH,NEUTRAL,LOW"/>
			<form:errors class="text-danger" path="priority"/>
		</div>
		
		<div class="form-group"> 
			<form:label path="subject">
				<spring:message code="message.subject" />
			</form:label>
			<form:input class="form-control" path="subject"/>
			<form:errors class="text-danger" path="subject"/>
		</div>
		
		<div class="form-group"> 
			<form:label path="body">
				<spring:message code="message.body" />
			</form:label>
			<form:input class="form-control" path="body"/>
			<form:errors class="text-danger" path="body"/>
		</div>
		
		<div class="form-group"> 
			<form:label path="recipient">
				<spring:message code="message.recipient.name" />:
			</form:label>
			<form:select class="form-control" path="recipient">
				<form:option value="${defaultActor.getId() }" label="${defaultActor.getUserAccount().getUsername() }" />		
				<form:options items="${actors}" itemValue="id" itemLabel="userAccount.username" />
			</form:select>
			<form:errors class="text-danger" path="recipient"/>
		</div>
		
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() == 0}">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="message.save" />">
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() != 0}">
		<jstl:if test="${canEdit==true }">
			<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="message.delete" />">
		</jstl:if>
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="message.cancel" />" onclick="javascript: relativeRedir('message/list.do');">
		
</form:form>