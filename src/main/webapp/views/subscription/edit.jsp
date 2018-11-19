<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="subscription/user/edit.do" modelAttribute="subscription">

	<form:hidden path="id" />
	<jstl:if test="${subscription.getId()==0 }">
		<form:hidden path="plan" />
	</jstl:if>
	
	<acme:select code="subscription.creditCard" path="creditCard" items="${creditCards}" selected="${lastCreditCard}" object="${subscription }" itemLabel="number"/> 
	<acme:select code="subscription.payFrecuency" path="payFrecuency" option="Monthly" option2="Quarterly" option3="Anually" selectPattern="true"/> 
	
	<acme:submit name="save" code="subscription.save" />
	
	<jstl:if test="${subscription.getId() != 0}">
		<acme:submit name="delete" code="subscription.delete"/> 	
		<acme:cancel url="subscription/user/display.do" code="subscription.cancel"/>
	</jstl:if>
	
	<jstl:if test="${subscription.getId() == 0}">
		<acme:cancel url="plan/display.do" code="subscription.cancel"/>
	</jstl:if>
	
		
	 
		
</form:form>