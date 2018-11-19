<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div>
	
	<br/>
	<spring:message code="about.us.alt" var="aboutAlt"/>
	<acme:image alt="${aboutAlt}" value="http://acme-world.com/wp-content/themes/immersivegarden/images/logo-acme-grey.png"/>
	<br/>
	<br/>
	
	<spring:message code="about.us.name.value" var="nameValue"/>
	<spring:message code="about.us.vat.number.value" var="vatValue"/>
	<spring:message code="about.us.email.value" var="emailValue"/>
	<spring:message code="about.us.group.value" var="groupValue"/>
	<spring:message code="about.us.address.value" var="addressValue"/>
	
	<acme:display code="about.us.name" value="${nameValue}"/>
	<acme:display code="about.us.vat.number" value="${vatValue}"/>
	<acme:display code="about.us.email" value="${emailValue}"/>
	<acme:display code="about.us.group" value="${groupValue}"/>
	<acme:display code="about.us.address" value="${addressValue}"/>
	
	

</div>