<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div class="container">

	<acme:display code="configuration.name" value="${configurationForm.getWelcomeMessage()}" />
	<acme:display code="configuration.slogan" value="${configurationForm.getConfiguration().getSlogan()}" />
	<acme:display code="configuration.email" value="${configurationForm.getConfiguration().getEmail()}" />
	<acme:display code="configuration.banner" value="${configurationForm.getConfiguration().getBanner()}" />
	<acme:display code="configuration.default.avatar" value="${configurationForm.getConfiguration().getDefaultAvatar()}" />
	<acme:display code="configuration.default.image" value="${configurationForm.getConfiguration().getDefaultImage()}" />
	
	<acme:displayLink code="configuration.edit" action="configuration/administrator/edit.do" css="btn btn-primary"/>
	
</div>
