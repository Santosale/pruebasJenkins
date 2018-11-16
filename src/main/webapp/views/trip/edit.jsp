<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="trip/manager/edit.do" modelAttribute="trip">

	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="price"/>
 	<form:hidden path="manager"/>
	<form:hidden path="stages"/>
	
	<jstl:if test="${trip.getId()==0 }">
		<form:hidden path="ticker"/>
	</jstl:if>
	
	<div class="form-group"> 
		<form:label path="title">
			<spring:message code="trip.title"/>
		</form:label>
		<form:input class="form-control" path="title"/>
		<form:errors class="text-danger" path="title"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="description">
			<spring:message code="trip.description" />:
		</form:label>
		<form:textarea path="description"/>
		<form:errors class="text-danger" path="description"/>
	</div>
	
	<jstl:if test="${trip.getId()>0 }">
		<div class="form-group"> 
			<form:label path="ticker">
				<spring:message code="trip.ticker"/>
			</form:label>
			<form:input class="form-control" path="ticker" readOnly="readOnly"/>
			<form:errors class="text-danger" path="ticker"/>
		</div>
	</jstl:if>
	
	
	<div class="form-group"> 
		<form:label path="explorerRequirements">
			<spring:message code="trip.explorerRequirements" />:
		</form:label>
		<form:textarea class="form-control" path="explorerRequirements"/>
		<form:errors class="text-danger" path="explorerRequirements"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="startDate">
			<spring:message code="trip.startDate" />:
		</form:label>
		<form:input class="form-control" path="startDate" placeholder="dd/MM/yyyy"/>
		<form:errors class="text-danger" path="startDate"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="endDate">
			<spring:message code="trip.endDate" />:
		</form:label>
		<form:input class="form-control" path="endDate" placeholder="dd/MM/yyyy"/>
		<form:errors class="text-danger" path="endDate"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="publicationDate">
			<spring:message code="trip.publicationDate" />:
		</form:label>
		<form:input class="form-control" path="publicationDate" placeholder="${dateFormat }"/>
		<form:errors class="text-danger" path="publicationDate"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="ranger">
			<spring:message code="trip.ranger" />:
		</form:label>
		<form:select class="form-control" path="ranger">
			<form:option label="-----" value="0"/>
			<form:options items="${rangers}" itemLabel="name" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="ranger"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="category">
			<spring:message code="trip.category" />:
		</form:label>
		<form:select class="form-control" path="category">
			<form:option label="-----" value="0"/>
			<form:options items="${categories}" itemLabel="name" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="category"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="legalText">
			<spring:message code="trip.legalText" />:
		</form:label>
		<form:select class="form-control" path="legalText">
			<form:option label="-----" value="0"/>
			<form:options items="${legalTexts}" itemLabel="title" itemValue="id"/>
		</form:select>
		<form:errors class="text-danger" path="legalText"/>
	</div>
		
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="trip.save" />">
	
	<jstl:if test="${trip.id != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="trip.delete" />" onclick="return confirm('<spring:message code="trip.confirm.delete" />')">
	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="trip.cancel" />" onclick="javascript: relativeRedir('trip/manager/list.do');" >

</form:form>