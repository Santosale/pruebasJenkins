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

<form:form action="bargain/company/edit.do" modelAttribute="bargainForm">

 	<form:hidden path="bargain.id" />
 	
 	<jstl:if test="${bargainForm.getBargain().getId()==0}">
 		<form:hidden path="categoryId" />
 	</jstl:if>
	
	<acme:textbox code="bargain.product.name" path="bargain.productName" />
	
	<acme:textbox code="bargain.description" path="bargain.description" />
	
	<acme:textbox code="bargain.productUrl" path="bargain.productUrl" />
	
	<acme:textbox code="bargain.product.images" path="bargain.productImages"/>
	
	<acme:textbox code="bargain.original.price" path="bargain.originalPrice" />
	
	<acme:textbox code="bargain.minimum.price" path="bargain.minimumPrice" />
	
	<acme:textbox code="bargain.estimated.sells" path="bargain.estimatedSells" />
	
	<acme:textbox code="bargain.discount.code" path="bargain.discountCode" />
	
	<spring:message code="bargain.examples.tags" var="tagsExamples"/>
	<acme:textbox code="bargain.tags" path="tagsName" placeholder="${tagsExamples}"/>
	
	<jstl:if test="${!notPublish}">
		<div class="form-check">
			<form:checkbox class="form-check-input" path="bargain.isPublished" id="check" checked=''/>
			<form:label path="bargain.isPublished"><div onclick="activar()" ><spring:message code="bargain.is.published" /></div></form:label> 
			<form:errors class="text-danger" path="bargain.isPublished"/>
		</div>
	</jstl:if>
	
	<br><br>
	
	<acme:submit name="save" code="raffle.save"/>
	
	<jstl:if test="${bargainForm.getBargain().getId() != 0}">
		<acme:submit name="delete" code="bargain.delete"/>
 	</jstl:if>
	
	<acme:cancel url="bargain/company/list.do" code="bargain.cancel"/>
			
</form:form>

<script type="text/javascript">

	function activar() {
		if  (document.getElementById("check").checked)
			document.getElementById("check").checked = false;
		else if (!document.getElementById("check").checked)
			document.getElementById("check").checked = true;
	}
</script>
