<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="message/actor/edit.do" modelAttribute="myMessage">

	<form:hidden path="id" />
		
	<acme:textbox code="message.moment" path="moment" readonly="true"/>
		
	<jstl:if test="${myMessage.getId()!= 0}">
		
		<acme:textbox code="message.priority" path="priority" readonly="true"/>
		
		<acme:textbox code="message.subject" path="subject" readonly="true"/>

		<acme:textbox code="message.body" path="body" readonly="true"/>
		
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() == 0}">

		<acme:select code="message.priority" path="priority" option="HIGH" option2="NEUTRAL" option3="LOW" />
				
		<acme:textbox code="message.subject" path="subject" />

		<acme:textarea code="message.body" path="body" />
				
	</jstl:if>
	
	<jstl:if test="${showRecipients}">
		<acme:select items="${actors}" itemLabel="userAccount.username" code="message.recipient.name" path="recipient"/>
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() == 0}">
		<acme:submit name="save" code="message.save"/>
	</jstl:if>
	
	<jstl:if test="${myMessage.getId() != 0 && canEdit}">
		<acme:submit name="delete" code="message.delete" />
 	</jstl:if>

	<acme:cancel url="message/actor/list.do" code="message.cancel" />	
		
</form:form>