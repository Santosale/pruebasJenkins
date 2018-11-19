<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="subscriptionVolume/customer/edit.do" modelAttribute="subscriptionVolume">

	<form:hidden path="id" />
	
	<jstl:if test="${subscriptionVolume.getId() == 0}">
		<form:hidden path="customer" />
		<form:hidden path="volume"/>	
	</jstl:if>
	
	<jstl:if test="${lastCreditCard==null}">
		<acme:textbox code="subscriptionVolume.creditCard.holderName" path="creditCard.holderName"/>
		
		<acme:textbox code="subscriptionVolume.creditCard.brandName" path="creditCard.brandName"/>
	
		<acme:textbox code="subscriptionVolume.creditCard.number" path="creditCard.number"/>
		 
		<acme:textbox code="subscriptionVolume.creditCard.expirationMonth" path="creditCard.expirationMonth"/>
		 
		<acme:textbox code="subscriptionVolume.creditCard.expirationYear" path="creditCard.expirationYear"/>
		 
		<acme:textbox code="subscriptionVolume.creditCard.cvvcode" path="creditCard.cvvcode"/>	
	</jstl:if>
	
	<jstl:if test="${lastCreditCard!=null && subscriptionVolume.getId()==0}">
		<acme:textbox code="subscriptionVolume.creditCard.holderName" path="creditCard.holderName" value="${lastCreditCard.getHolderName()}"/>
	
		<acme:textbox code="subscriptionVolume.creditCard.brandName" path="creditCard.brandName" value="${lastCreditCard.getBrandName()}"/>
	
		<acme:textbox code="subscriptionVolume.creditCard.number" path="creditCard.number" value="${lastCreditCard.getNumber()}"/>
		
		<acme:textbox code="subscriptionVolume.creditCard.expirationMonth" path="creditCard.expirationMonth" value="${lastCreditCard.getExpirationMonth()}"/>
		
		<acme:textbox code="subscriptionVolume.creditCard.expirationYear" path="creditCard.expirationYear" value="${lastCreditCard.getExpirationYear()}"/>
		
		<acme:textbox code="subscriptionVolume.creditCard.cvvcode" path="creditCard.cvvcode" value="${lastCreditCard.getCvvcode()}"/>
	</jstl:if>
	
	<acme:submit name="save" code="subscriptionVolume.save"/>

	<jstl:if test="${subscriptionVolume.getId() != 0}">
		<acme:submit name="delete" code="subscriptionVolume.delete"/>
 	</jstl:if>
	
	<acme:cancel url="subscriptionVolume/customer/list.do" code="subscriptionVolume.cancel"/>
			
</form:form>