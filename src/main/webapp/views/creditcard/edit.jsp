<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="creditcard/${actorType}/edit.do" modelAttribute="creditCard">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="actor" />
	
	<div class="form-group"> 
		<form:label path="holderName">
			<spring:message code="creditcard.holderName" />:
		</form:label>
		<form:input class="form-control" path="holderName" />
		<form:errors class="text-danger" path="holderName"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="brandName">
			<spring:message code="creditcard.brandName" />:
		</form:label>
		<form:input class="form-control" path="brandName" />
		<form:errors class="text-danger" path="brandName"/>
	</div>

	<div class="form-group"> 
		<form:label path="number">
			<spring:message code="creditcard.number" />:
		</form:label>
		<form:input class="form-control" path="number" />
		<form:errors class="text-danger" path="number"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="expirationMonth">
			<spring:message code="creditcard.expirationMonth" />:
		</form:label>
		<form:input class="form-control" path="expirationMonth" />
		<form:errors class="text-danger" path="expirationMonth"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="expirationYear">
			<spring:message code="creditcard.expirationYear" />:
		</form:label>
		<form:input class="form-control" path="expirationYear" />
		<form:errors class="text-danger" path="expirationYear"/>
	</div>
	 
	<div class="form-group"> 
		<form:label path="cvvcode">
			<spring:message code="creditcard.cvvcode" />:
		</form:label>
		<form:input class="form-control" path="cvvcode" />
		<form:errors class="text-danger" path="cvvcode"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="creditcard.save" />">

	<jstl:if test="${creditCard.getId() != 0 && !isAdded}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="creditcard.delete" />">
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="creditcard.cancel" />" onclick="javascript: relativeRedir('creditcard/${actorType}/list.do')">
		
</form:form>