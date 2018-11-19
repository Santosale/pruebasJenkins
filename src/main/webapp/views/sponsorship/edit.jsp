<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="sponsorship/sponsor/edit.do" modelAttribute="sponsorship">

	<form:hidden path="id"/>
	
	<jstl:if test="${sponsorship.getId()==0}">
		<form:hidden path="bargain"/>
	</jstl:if>
	

	<acme:textbox code="sponsorship.amount" path="amount"/>
	
	<acme:textbox code="sponsorship.url" path="url"/>
	
	<acme:textbox code="sponsorship.image" path="image"/>
	
	
	<acme:submit name="save" code="sponsorship.save" />
		
	
	<jstl:if test="${sponsorship.getId() != 0}">
		<acme:submit name="delete" code="sponsorship.delete" codeDelete="sponsorship.confirm.delete"/>
	</jstl:if>
	
	
	<acme:cancel url="sponsorship/sponsor/list.do" code="sponsorship.cancel"/>
	
	
</form:form>