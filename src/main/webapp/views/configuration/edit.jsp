<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="configuration/administrator/edit.do" modelAttribute="configurationForm">

	<form:hidden path="configuration.id" />
	
	<acme:textbox code="configuration.name" path="welcomeMessage" />
		
	<acme:textbox code="configuration.slogan" path="configuration.slogan" />
	
	<acme:textbox code="configuration.email" path="configuration.email" />
		
	<acme:textbox code="configuration.banner" path="configuration.banner" />
	
	<acme:textbox code="configuration.default.avatar" path="configuration.defaultAvatar" />
	
	<acme:textbox code="configuration.default.image" path="configuration.defaultImage" />

	<acme:submit name="save" code="configuration.save" />

	<acme:cancel url="configuration/administrator/display.do" code="configuration.cancel"/>
		
</form:form>