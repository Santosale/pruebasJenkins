<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<form:form action="${requestURI}" modelAttribute="${modelo }">

	<form:hidden path="id" />
	<form:hidden path="version" />
	
	<jstl:if test="${auditor.getId()!=0 }">
		<form:hidden path="userAccount.password"/>
	</jstl:if>
	
	<form:hidden path="userAccount.authorities" />
	<form:hidden path="userAccount.version" />
	<form:hidden path="userAccount.enabled" />
	<form:hidden path="userAccount.id" />
	<form:hidden path="suspicious" />

	<!-- Username -->
	<jstl:if test="${auditor.getId()!=0 }">
	
	<div class="form-group"> 
		<form:label path="userAccount.username">
			<spring:message code="actor.username" />
		</form:label>
		
		<form:input class="form-control" path="userAccount.username" readOnly="readOnly" />
		<form:errors class="text-danger" path="userAccount.username" />
	</div>
	
	</jstl:if>
	
	<jstl:if test="${auditor.getId()==0 }">
		<div class="form-group"> 
			<form:label path="userAccount.username">
				<spring:message code="actor.username" />
			</form:label>
			<form:input class="form-control" path="userAccount.username" />
			<form:errors class="text-danger" path="userAccount.username" />
		</div>

		<!--  Password -->
		<div class="form-group"> 
			<form:label path="userAccount.password">
				<spring:message code="actor.password" />
			</form:label>
			<form:password class="form-control" path="userAccount.password" />
			<form:errors class="text-danger" path="userAccount.password" />
		</div>
	</jstl:if>
	
	<!-- Name -->
	<div class="form-group"> 
		<form:label path="name">
			<spring:message code="actor.name" />
		</form:label>
		<form:input class="form-control" path="name" />
		<form:errors class="text-danger" path="name" />
	</div>

	<!-- Surname -->
	<div class="form-group"> 
		<form:label path="surname">
			<spring:message code="actor.surname" />
		</form:label>
		<form:input class="form-control" path="surname" />
		<form:errors class="text-danger" path="surname" />
	</div>

	<!-- Email -->
	<div class="form-group"> 
		<form:label path="email">
			<spring:message code="actor.email" />
		</form:label>
		<form:input class="form-control" path="email" />
		<form:errors class="text-danger" path="email" />
	</div>

	<!-- Phone -->
	<div class="form-group"> 
		<form:label path="phone">
			<spring:message code="actor.phone" />
		</form:label>
		<form:input class="form-control" path="phone" />
		<form:errors class="text-danger" path="phone" />
	</div>

	<!-- Address -->
	<div class="form-group"> 
		<form:label path="address">
			<spring:message code="actor.address" />
		</form:label>
		<form:input class="form-control" path="address" />
		<form:errors class="text-danger" path="address" />
	</div>
	
	<jstl:if test="${canEdit==true }">	
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="actor.save" />">
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="actor.cancel" />" onclick="javascript: relativeRedir('welcome/index.do');" >
	
</form:form>
