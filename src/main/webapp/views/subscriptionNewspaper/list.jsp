<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="subscriptionNewspapers" id="row" requestURI="subscriptionNewspaper/customer/list.do">
	
	<acme:columnLink url="subscriptionNewspaper/customer/edit.do?subscriptionNewspaperId=${row.getId()}" action="edit" domain="subscription" id="${row.getId()}" />

	<acme:column domain="subscription" property="creditCard.holderName"/>
	
	<acme:column domain="subscription" property="creditCard.brandName"/>
	
	<acme:column domain="subscription" property="creditCard.number"/>
	
	<acme:column domain="subscription" property="creditCard.expirationMonth"/>
	
	<acme:column domain="subscription" property="creditCard.expirationYear"/>

	<acme:column domain="subscription" property="creditCard.cvvcode"/>

	<acme:column domain="subscription" property="newspaper.title"/>

</display:table> 
	
<acme:paginate pageNumber="${pageNumber}" url="subscriptionNewspaper/customer/list.do" objects="${subscriptionNewspapers}" page="${page}"/>