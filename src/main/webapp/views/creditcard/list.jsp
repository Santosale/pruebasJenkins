<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="creditCards" id="row" requestURI="creditcard/${actorType}/list.do" pagesize="5">

	<spring:url value="creditcard/${actorType}/edit.do" var="urlEdit">
		<spring:param name="creditCardId" value="${row.getId()}" />
	</spring:url>
	
	<display:column>
		<a href="${urlEdit}"><spring:message code="creditcard.edit"/></a>
	</display:column>

	<spring:message code="creditcard.holderName" var="holderNameHeader" />
	<display:column property="holderName" title="${holderNameHeader}" />
	
	<spring:message code="creditcard.brandName" var="brandNameHeader" />
	<display:column property="brandName" title="${brandNameHeader}" />
	
	<spring:message code="creditcard.number" var="numberHeader" />
	<display:column property="number" title="${numberHeader}" />
	
	<spring:message code="creditcard.expirationMonth" var="expirationMonthHeader" />
	<display:column property="expirationMonth" title="${expirationMonthHeader}" />
	
	<spring:message code="creditcard.expirationYear" var="expirationYearHeader" />
	<display:column property="expirationYear" title="${expirationYearHeader}" />

	<spring:message code="creditcard.cvvcode" var="cvvcodeHeader" />
	<display:column property="cvvcode" title="${cvvcodeHeader}" />
 
	<spring:url value="creditcard/${actorType}/display.do" var="urlDisplay">
		<spring:param name="creditCardId" value="${row.getId()}" />
	</spring:url>

	<display:column>
		<a href="${urlDisplay }"><spring:message code="creditcard.display"/></a>
	</display:column>
		
</display:table> 
	
<br />

<a href="creditcard/${actorType}/create.do"><spring:message code="creditcard.create" /></a>