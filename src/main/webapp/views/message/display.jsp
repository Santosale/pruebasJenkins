<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>
	
<div class="container">
	
	<p><acme:display code="message.moment" value="${messageObject.getMoment()}" codeMoment="message.format.moment" /></p>
		
	<p><acme:display code="message.priority" value="${messageObject.getPriority()}" /></p>
	
	<p><acme:display code="message.subject" value="${messageObject.getSubject()}" /></p>

	<p><acme:display code="message.body" value="${messageObject.getBody()}" /></p>

	<p><acme:displayLink css="btn btn-primary" code="message.folder" action="folder/actor/display.do" parametre="folderId" parametreValue="${messageObject.getFolder().getId()}"></acme:displayLink></p>

</div>
