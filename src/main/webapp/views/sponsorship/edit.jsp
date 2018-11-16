<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="sponsorship/sponsor/edit.do" modelAttribute="sponsorship">
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="trip" />
	<form:hidden path="sponsor" />
	
	<div class="form-group"> 
	<form:label path="banner">
		<spring:message code="sponsorship.banner"/>
	</form:label>
	<form:input class="form-control" path="banner"/>
	<form:errors class="text-danger" path="banner"/>
	</div>
	
<%-- 	
	<div class="form-group"> 
		<form:label path="trip">
			<spring:message code="sponsorship.trip" />:
		</form:label>
		<form:select path="trip">
			<form:option label="-----" value="0"/>
			<form:options items="${trips}" itemLabel="title" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="trip"/>
	</div> --%>
	
	
	<div class="form-group"> 
		<form:label path="linkInfoPage">
			<spring:message code="sponsorship.linkInfoPage"/>
		</form:label>
		<form:input class="form-control" path="linkInfoPage"/>
		<form:errors class="text-danger" path="linkInfoPage"/>
	</div>
	

	<div class="form-group"> 
		<form:label path="creditCard">
			<spring:message code="sponsorship.creditCard" />
		</form:label>
		<form:select class="form-control" path="creditCard">
			<form:option label="-----" value="0"/>
			<form:options items="${creditCards}" itemLabel="number" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="creditCard"/>
	</div>
	
<%--<div class="form-group"> 
		<form:label path="sponsor">
			<spring:message code="sponsorship.sponsor" />
		</form:label>
		<form:select class="form-control" path="sponsor">
			<form:option label="-----" value="0"/>
			<form:options items="${sponsors}" itemLabel="name" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="sponsor"/>
	</div> --%>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="sponsorship.save" />">
	
	<jstl:if test="${sponsorship.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="sponsorship.delete" />" onclick="return confirm('<spring:message code="sponsorship.confirm.delete" />')">
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="sponsorship.cancel" />" onclick="javascript: relativeRedir('sponsorship/sponsor/list.do');">

</form:form>