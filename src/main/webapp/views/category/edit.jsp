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

<form:form action="category/moderator/edit.do" modelAttribute="category">

	
	<form:hidden path="id"/>
	
	<jstl:if test="${category.getId()==0}">
	 	<form:hidden path="fatherCategory"/>
 	</jstl:if>
 	

	<acme:textbox code="category.name" path="name"/>
	
	<acme:textbox code="category.image" path="image"/>
		
	
	<acme:submit name="save" code="category.save" />
	
	<!-- Si es una category por defecto no se puede borrar -->	
	<jstl:if test="${category.getId()!= 0 && !category.getDefaultCategory()}">
		<acme:submit name="delete" code="category.delete" codeDelete="category.confirm.delete"/>
	</jstl:if>

	<!-- Estamos creando, al cancelar, volvemos a su padre -->
	<jstl:if test="${category.getId()==0}">
		<acme:cancel url="category/display.do?categoryId=${category.getFatherCategory().getId()}" code="category.cancel"/>
	</jstl:if>
	
	<!-- Estamos editando, al cancelar, volvemos a la categoría -->
	<jstl:if test="${category.getId()!=0}">
		<acme:cancel url="category/display.do?categoryId=${category.getId()}" code="category.cancel"/>
	</jstl:if>
	

</form:form>