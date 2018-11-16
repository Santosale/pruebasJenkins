<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="configuration/administrator/editWelcomeMessage.do" modelAttribute="internationalization">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="countryCode" />
	<form:hidden path="messageCode"/>
	
	<div class="form-group"> 
		<form:label path="value">
			<spring:message code="internationalization.value" />
		</form:label>
		<form:input class="form-control" path="value"/>
		<form:errors css="text-danger" path="value"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="configuration.save" />" >

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="configuration.cancel" />" onclick="javascript: relativeRedir('configuration/administrator/display.do');" >
		
</form:form>