<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="legalText/administrator/edit.do" modelAttribute="legalText">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="legalText.title"/>
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="body">
			<spring:message code="legalText.body"/>
		</form:label>
		<form:textarea path="body"/>
		<form:errors class="text-danger" path="body"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="moment">
			<spring:message code="legalText.moment"/>
		</form:label>
		<form:input class="form-control" path="moment" readonly="true"/>
		<form:errors class="text-danger" path="moment"/>
	</div>
	
	<spring:message code="legalText.format.laws" var="lawsFormat"/>
	<div class="form-group"> 
		<form:label path="laws">
			<spring:message code="legalText.law"/>
		</form:label>
		<form:textarea class="form-control" path="laws" placeholder="${lawsFormat}"/>
		<form:errors class="text-danger" path="laws"/>
	</div>
	
	<div > 
		<form:label path="draft">
			<spring:message code="legalText.draft"/>
		</form:label>
		<form:checkbox path="draft"/>
		<form:errors class="text-danger" path="draft"/>
	</div>
	
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="legalText.save" />">
	
	<jstl:if test="${legalText.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="sponsorship.delete" />" onclick="return confirm('<spring:message code="legalText.confirm.delete" />')">
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="legalText.cancel" />" onclick="javascript: relativeRedir('legalText/administrator/list.do');">
	
</form:form>