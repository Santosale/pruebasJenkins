<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="subscriptionNewspaper/customer/edit.do" modelAttribute="subscriptionNewspaper">

	<form:hidden path="id" />
	
	<jstl:if test="${subscriptionNewspaper.getId() == 0}">
		<form:hidden path="customer" />
		<form:hidden path="newspaper"/>
	</jstl:if>
	
	<jstl:if test="${lastCreditCard==null}">
		<acme:textbox code="subscription.creditCard.holderName" path="creditCard.holderName"/>
		
		<acme:textbox code="subscription.creditCard.brandName" path="creditCard.brandName"/>
	
		<acme:textbox code="subscription.creditCard.number" path="creditCard.number"/>
		 
		<acme:textbox code="subscription.creditCard.expirationMonth" path="creditCard.expirationMonth"/>
		 
		<acme:textbox code="subscription.creditCard.expirationYear" path="creditCard.expirationYear"/>
		 
		<acme:textbox code="subscription.creditCard.cvvcode" path="creditCard.cvvcode"/>	
	</jstl:if>
	
	<jstl:if test="${lastCreditCard!=null && subscriptionNewspaper.getId()==0}">
		<acme:textbox code="subscription.creditCard.holderName" path="creditCard.holderName" value="${lastCreditCard.getHolderName()}"/>
	
		<acme:textbox code="subscription.creditCard.brandName" path="creditCard.brandName" value="${lastCreditCard.getBrandName()}"/>
	
		<acme:textbox code="subscription.creditCard.number" path="creditCard.number" value="${lastCreditCard.getNumber()}"/>
		
		<acme:textbox code="subscription.creditCard.expirationMonth" path="creditCard.expirationMonth" value="${lastCreditCard.getExpirationMonth()}"/>
		
		<acme:textbox code="subscription.creditCard.expirationYear" path="creditCard.expirationYear" value="${lastCreditCard.getExpirationYear()}"/>
		
		<acme:textbox code="subscription.creditCard.cvvcode" path="creditCard.cvvcode" value="${lastCreditCard.getCvvcode()}"/>
	</jstl:if>
	
	<acme:submit name="save" code="subscription.save"/>

	<jstl:if test="${subscriptionNewspaper.getId() != 0 && !isAdded}">
		<acme:submit name="delete" code="subscription.delete"/>
 	</jstl:if>
	
	<acme:cancel url="subscriptionNewspaper/customer/list.do" code="subscription.cancel"/>
			
</form:form>