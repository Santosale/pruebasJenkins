<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
	<div class="container">	
	
	<span class="display"><spring:message code="creditcard.holderName"/>:</span> <jstl:out value=": ${creditCard.getHolderName()}" />
	<br/>
	<span class="display"><spring:message code="creditcard.brandName"/>:</span> <jstl:out value=": ${creditCard.getBrandName()}" />
	<br/>
	<span class="display"><spring:message code="creditcard.number"/>:</span> <jstl:out value=": ${creditCard.getNumber()}" />
	<br/>
	<span class="display"><spring:message code="creditcard.expirationMonth"/>:</span> <jstl:out value=": ${creditCard.getExpirationMonth()}" />
	<br/>
	<span class="display"><spring:message code="creditcard.expirationYear"/>:</span> <jstl:out value=": ${creditCard.getExpirationYear()}" />
	<br/>
	<span class="display"><spring:message code="creditcard.cvvcode"/>:</span> <jstl:out value=": ${creditCard.getCvvcode()}" />
	<br/>	

</div>
