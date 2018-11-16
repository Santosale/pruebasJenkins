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

<display:table class="table table-striped table-bordered table-hover" name="messages" id="row" requestURI="${requestURI }" pagesize="5">

	<jstl:if test="${isChildren==false }">
	<spring:url value="message/edit.do" var="urlEdit">
		<spring:param name="messageId" value="${row.getId()}" />
	</spring:url>
	
	<spring:url value="message/move.do" var="urlMove">
		<spring:param name="messageId" value="${row.getId()}" />
	</spring:url>
	
	
	<display:column>
		<a href="${urlEdit}"><spring:message code="message.edit"/></a>
	</display:column>
	
	<display:column>
		<a href="${urlMove}"><spring:message code="message.move"/></a>
	</display:column>
	</jstl:if>

	<spring:message code="message.format.moment" var="momentFormat"/>

	<spring:message code="message.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}" format="{0,date,${momentFormat }}" />
	
	<spring:message code="message.priority" var="priorityHeader" />
	<display:column property="priority" title="${priorityHeader}" />
	
	<spring:message code="message.subject" var="subjectHeader" />
	<display:column property="subject" title="${subjectHeader}" />
	
	<spring:message code="message.body" var="bodyHeader" />
	<display:column property="body" title="${bodyHeader}" />
	
	<spring:message code="message.folder.name" var="folderNameHeader" />
	<display:column property="folder.name" title="${folderNameHeader}" />

	<spring:message code="message.sender.name" var="senderNameHeader" />
	<display:column property="sender.name" title="${senderNameHeader}" />
	
	<spring:message code="message.recipient.name" var="recipientNameHeader" />
	<display:column property="recipient.name" title="${recipientNameHeader}" />
 
	<spring:url value="message/display.do" var="urlDisplay">
		<spring:param name="messageId" value="${row.getId()}" />
	</spring:url>

	<display:column>
		<a href="${urlDisplay }"><spring:message code="message.display"/></a>
	</display:column>
		
</display:table> 
	
<br />
	<jstl:if test="${isChildren==false }">

<a href="message/create.do"><spring:message code="message.create" /></a>

	</jstl:if>
