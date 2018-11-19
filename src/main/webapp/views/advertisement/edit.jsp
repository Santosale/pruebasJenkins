<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="advertisement/agent/edit.do" modelAttribute="advertisement">

	<form:hidden path="id"/>
	

	<acme:textbox code="advertisement.title" path="title"/>
	
	<acme:textbox code="advertisement.url.banner" path="urlBanner"/>
	
	<acme:textbox code="advertisement.url.target" path="urlTarget"/>
	
	
	<!-- CreditCard -->
	<br>
	<br>
	<p class="display"><spring:message code="advertisement.creditCard" /></p> 
	<br>
	
	<jstl:if test="${lastCreditCard==null}">
		<acme:textbox code="advertisement.creditCard.holder.name" path="creditCard.holderName"/>
	
		<acme:textbox code="advertisement.creditCard.brand.name" path="creditCard.brandName"/>
	
		<acme:textbox code="advertisement.creditCard.number" path="creditCard.number"/>
		
		<acme:textbox code="advertisement.creditCard.expiration.month" path="creditCard.expirationMonth"/>
		
		<acme:textbox code="advertisement.creditCard.expiration.year" path="creditCard.expirationYear"/>
		
		<acme:textbox code="advertisement.creditCard.cvv.code" path="creditCard.cvvcode"/>
	</jstl:if>
	
	<jstl:if test="${lastCreditCard!=null && advertisement.getId()==0}">
		<acme:textbox code="advertisement.creditCard.holder.name" path="creditCard.holderName" value="${lastCreditCard.getHolderName()}"/>
	
		<acme:textbox code="advertisement.creditCard.brand.name" path="creditCard.brandName" value="${lastCreditCard.getBrandName()}"/>
	
		<acme:textbox code="advertisement.creditCard.number" path="creditCard.number" value="${lastCreditCard.getNumber()}"/>
		
		<acme:textbox code="advertisement.creditCard.expiration.month" path="creditCard.expirationMonth" value="${lastCreditCard.getExpirationMonth()}"/>
		
		<acme:textbox code="advertisement.creditCard.expiration.year" path="creditCard.expirationYear" value="${lastCreditCard.getExpirationYear()}"/>
		
		<acme:textbox code="advertisement.creditCard.cvv.code" path="creditCard.cvvcode" value="${lastCreditCard.getCvvcode()}"/>
	</jstl:if>
	
	
	
	
	<security:authorize access="hasRole('AGENT')">
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${principal.equals(advertisement.getAgent().getUserAccount().getUsername())}">
			<acme:submit name="save" code="advertisement.save" />
		</jstl:if>
	
		
	</security:authorize>
	<security:authorize access="hasRole('AGENT')">
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${principal.equals(advertisement.getAgent().getUserAccount().getUsername()) && advertisement.getId() != 0}">
			<acme:submit name="delete" code="advertisement.delete" codeDelete="advertisement.confirm.delete"/>
		</jstl:if>
	</security:authorize>
	
	<acme:cancel url="advertisement/agent/list.do" code="advertisement.cancel"/>
	
	
	

</form:form>