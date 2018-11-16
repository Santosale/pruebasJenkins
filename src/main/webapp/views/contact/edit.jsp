<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="contact/explorer/edit.do" modelAttribute="contact">

	<jstl:if test="${contact.getId() == 0}">
		<h2><spring:message code="contact.create" /></h2>
 	</jstl:if>
 	
	<jstl:if test="${contact.getId() != 0}">
		<h2><spring:message code="contact.edit" /></h2>
 	</jstl:if>

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="explorer" />
	
	<div class="form-group"> 
		<form:label path="name">
			<spring:message code="contact.name" />:
		</form:label>
		<form:input class="form-control" path="name" />
		<form:errors class="text-danger" path="name"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="email">
			<spring:message code="contact.email" />:
		</form:label>
		<form:input class="form-control" path="email" />
		<form:errors class="text-danger" path="email"/>
	</div>

	<div class="form-group"> 
		<form:label path="phone">
			<spring:message code="contact.phone" />:
		</form:label>
		<form:input class="form-control" path="phone"/>
		<form:errors class="text-danger" path="phone"/> 
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="contact.save" />">

	<jstl:if test="${contact.getId() != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="contact.delete" />">
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="contact.cancel" />" onclick="javascript: relativeRedir('contact/explorer/list.do')">
	
</form:form>