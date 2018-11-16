<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="configuration/administrator/edit.do" modelAttribute="configuration">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="welcomeMessage"/>

	<div class="form-group"> 
		<form:label path="companyName">
			<spring:message code="configuration.companyName" />
		</form:label>
		<form:input class="form-control" path="companyName"/>
		<form:errors css="text-danger" path="companyName"/>
	</div>
		
	<div class="form-group"> 
		<form:label path="banner">
			<spring:message code="configuration.banner" />
		</form:label>
		<form:input class="form-control" path="banner"/>
		<form:errors css="text-danger" path="banner"/>
	</div>
		
	<div class="form-group"> 
		<form:label path="vat">
			<spring:message code="configuration.vat" />
		</form:label>
		<form:input class="form-control" path="vat"/>
		<form:errors css="text-danger" path="vat"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="countryCode">
			<spring:message code="configuration.countryCode" />
		</form:label>
		<form:input class="form-control" path="countryCode"/>
		<form:errors css="text-danger" path="countryCode"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="spamWords">
			<spring:message code="configuration.spamWords" />
		</form:label>
		<form:input class="form-control" path="spamWords"/>
		<form:errors css="text-danger" path="spamWords"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="cachedTime">
			<spring:message code="configuration.cachedTime" />
		</form:label>
		<form:input class="form-control" path="cachedTime"/>
		<form:errors css="text-danger" path="cachedTime"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="finderNumber">
			<spring:message code="configuration.finderNumber" />
		</form:label>
		<form:input class="form-control" path="finderNumber"/>
		<form:errors css="text-danger" path="finderNumber"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="configuration.save" />" >

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="configuration.cancel" />" onclick="javascript: relativeRedir('configuration/administrator/display.do');" >
		
</form:form>