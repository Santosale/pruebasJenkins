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

<display:table class="table table-striped table-bordered table-hover" name="subscriptionVolumes" id="row">
	
	<acme:columnLink domain="subscriptionVolume" actor="customer" id="${row.getId()}" action="edit" />

	<acme:column domain="subscriptionVolume" property="creditCard.holderName"/>
	
	<acme:column domain="subscriptionVolume" property="creditCard.brandName"/>
	
	<acme:column domain="subscriptionVolume" property="creditCard.number"/>
	
	<acme:column domain="subscriptionVolume" property="creditCard.expirationMonth"/>
	
	<acme:column domain="subscriptionVolume" property="creditCard.expirationYear"/>

	<acme:column domain="subscriptionVolume" property="creditCard.cvvcode"/>

	<acme:column domain="subscriptionVolume" property="volume.title"/>

</display:table> 
	
<acme:paginate pageNumber="${pageNumber}" url="subscriptionVolume/customer/list.do" objects="${subscriptionVolumes}" page="${page}"/>