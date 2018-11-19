<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="termCondition/administrator/edit.do" modelAttribute="internationalization">

	<form:hidden path="id" />
	
	<acme:textarea code="term.condition.edit" path="value" cols="20" rows="20"/> 
	<acme:submit name="save" code="term.condition.save" />
	<acme:cancel url="termCondition/administrator/display.do" code="term.condition.cancel"/>
	 
		
</form:form>