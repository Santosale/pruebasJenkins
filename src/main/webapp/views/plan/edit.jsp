<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="plan/administrator/edit.do" modelAttribute="plan">

	<form:hidden path="id" />
	
	<acme:textbox code="plan.name" path="name" readonly="readonly"/>
	<acme:textarea code="plan.description" path="description" cols="20" rows="20"/> 
	<acme:textbox code="plan.cost" path="cost"/>
	<acme:submit name="save" code="plan.save" />
	<acme:cancel url="plan/display.do" code="plan.cancel"/>
	 
		
</form:form>