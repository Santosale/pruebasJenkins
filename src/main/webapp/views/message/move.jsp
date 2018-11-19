<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="message/actor/move.do" modelAttribute="myMessage">

	<form:hidden path="id" />
	
	<acme:select items="${folders}" itemLabel="name" code="message.folder.choose" path="folder"/>
	
	<jstl:if test="${canEdit==true}">
		<acme:submit name="save" code="message.save"/>
	</jstl:if>
	
	<acme:cancel url="message/actor/list.do" code="message.cancel" />	
		
</form:form>