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

<form:form action="groupon/user/edit.do" modelAttribute="groupon">

	<form:hidden path="id"/>
	
	<acme:textbox code="groupon.title" path="title"/>
	<acme:textbox code="groupon.description" path="description"/>
	<acme:textbox code="groupon.productName" path="productName"/>
	<acme:textbox code="groupon.productUrl" path="productUrl"/>
	<acme:textbox code="groupon.minAmountProduct" path="minAmountProduct"/>
	<acme:textbox code="groupon.maxDate" path="maxDate" placeholder="dd/mm/yyyy HH:mm"/>
	<acme:textbox code="groupon.originalPrice" path="originalPrice"/>
	<acme:textbox code="groupon.price" path="price"/>
	
	<jstl:if test="${canEditDiscountCode }">
		<acme:textbox code="groupon.discountCode" path="discountCode"/>
	</jstl:if>
	
	
	<acme:submit name="save" code="groupon.save" />
		<jstl:if test="${groupon.getId() != 0}">
			<acme:submit name="delete" code="groupon.delete" />
		</jstl:if>	
	<acme:cancel url="groupon/user/list.do" code="groupon.cancel"/>

</form:form>