 <%--
 * login.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="j_spring_security_check" modelAttribute="credentials">

	<jstl:if test="${showError == true}">
	
		<div class="form-group has-error">
			<form:label class="control-label" path="username">
				<spring:message code="security.username" />
			</form:label>
			<form:input class="form-control" path="username" />	
			<form:errors class="text-danger" path="username" />
		</div>
	
		<div class="form-group has-error">
			<form:label class="control-label" path="password">
				<spring:message code="security.password" />
			</form:label>
			<form:password class="form-control" path="password" />	
			<form:errors class="text-danger" path="password" />
			
			<span class="help-block">
			<spring:message code="security.login.failed" />
		</span>
		</div>
	
	</jstl:if>
	
	<jstl:if test="${showError == false}">
	
		<div class="form-group">
			<form:label path="username">
				<spring:message code="security.username" />
			</form:label>
			<form:input class="form-control" path="username" />	
			<form:errors class="text-danger" path="username" />
		</div>
	
		<div class="form-group">
			<form:label path="password">
				<spring:message code="security.password" />
			</form:label>
			<form:password class="form-control" path="password" />	
			<form:errors class="text-danger" path="password" />
		</div>
	
	</jstl:if>
	
	<input type="submit" class="btn btn-primary" value="<spring:message code="security.login" />" />
	
</form:form>