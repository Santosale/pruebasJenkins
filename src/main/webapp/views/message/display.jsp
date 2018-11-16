<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
	<div class="container">

	<spring:message code="message.format.moment" var="momentFormat"/>
	
	<span class="display"><spring:message code="message.moment"/></span><fmt:formatDate value="${messageObject.getMoment()}" pattern="${momentFormat }"/>
	<br/>
	<span class="display"><spring:message code="message.priority"/></span><jstl:out value="  ${messageObject.getPriority()}" />
	<br/>
	<span class="display"><spring:message code="message.subject"/></span><jstl:out value="  ${messageObject.getSubject()}" />
	<br/>
	<span class="display"><spring:message code="message.body"/></span><jstl:out value="  ${messageObject.getBody()}" />
	<br/>
	
	
	<spring:url var="urlFolder" value="folder/display.do">
		<spring:param name="folderId" value="${messageObject.getFolder().getId()}" />
	</spring:url>
	
	
	<a href="${urlFolder}" ><spring:message code="message.folder"/></a>
	

</div>
