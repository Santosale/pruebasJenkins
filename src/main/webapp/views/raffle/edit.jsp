<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="raffle/company/edit.do" modelAttribute="raffle">

 	<form:hidden path="id" />
	
	<acme:textbox code="raffle.title" path="title" />
	
	<acme:textbox code="raffle.description" path="description" />
	
	<acme:textbox code="raffle.productName" path="productName" />
	
	<acme:textbox code="raffle.productUrl" path="productUrl" />
	
	<acme:textbox code="raffle.productImages" path="productImages" />
	
	<acme:textbox code="raffle.maxDate" path="maxDate" placeholder="dd/MM/YYYY" />
	
	<acme:textbox code="raffle.price" path="price" />
	
	<acme:submit name="save" code="raffle.save"/>
	
	<jstl:if test="${raffle.getId() != 0 && countTickets == 0}">
		<acme:submit name="delete" code="raffle.delete"/>
 	</jstl:if>
	
	<acme:cancel url="raffle/company/list.do" code="raffle.cancel"/>
			
</form:form>