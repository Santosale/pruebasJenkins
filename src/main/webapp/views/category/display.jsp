<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div class="container">

	<jstl:if test="${category!=null}">
	
		<!-- Marcamos la categoría como por defecto -->
		<jstl:if test="${category.getDefaultCategory()}">
			<div style="background: #8BC34A; text-align: center; padding:5px; font-weight:bold;">
				<spring:message code="category.default"/>
			</div>
			<br/>
		</jstl:if>
		
		
		<acme:display code="category.name" value="${category.getName()}"/>
		
		<spring:message code="category.name" var="categoryAlt"/>
		
		<jstl:if test="${!linkBroken}">	
			<acme:image  value="${category.getImage()}" alt="${categoryAlt}"/>
		</jstl:if>
		<jstl:if test="${linkBroken}">	
			<acme:image  value="${imageBroken}" alt="${categoryAlt}"/>
		</jstl:if>
		
		<br><br>
		<!-- Navegamos a la categoría padre -->
		<jstl:if test="${category.getFatherCategory()!=null}">
			<acme:displayLink parameter="categoryId" code="category.father.category" action="category/display.do" parameterValue="${category.getFatherCategory().getId()}" parameter2="categoryToMoveId" parameterValue2="${categoryToMoveId}" css="btn btn-primary"></acme:displayLink>		
		</jstl:if>
		
		<jstl:if test="${category.getFatherCategory()==null}">
			<acme:displayLink code="category.father.category" action="category/display.do"  parameter="categoryToMoveId" parameterValue="${categoryToMoveId}" css="btn btn-primary"></acme:displayLink>		
		</jstl:if>
		
		<!-- Si es un moderator -->
		<security:authorize access="hasRole('MODERATOR')">
			
			<!-- Creación y edición de categoria -->
			<acme:displayLink parameter="categoryId" code="category.edit" action="category/moderator/edit.do" parameterValue="${category.getId()}" css="btn btn-primary"></acme:displayLink>
			<acme:displayLink parameter="fatherCategoryId" code="category.create" action="category/moderator/create.do" parameterValue="${category.getId()}" css="btn btn-primary"></acme:displayLink>
			
			<!-- Ponemos enlaces para controlar la reorganización -->
			<jstl:if test="${categoryToMoveId==null && !category.getDefaultCategory()}">
				<acme:displayLink parameter="categoryId" code="category.move.this" action="category/display.do" parameterValue="${category.getId()}" parameter2="page" parameterValue2="${page}" parameter3="categoryToMoveId" parameterValue3="${category.getId()}" css="btn btn-primary"></acme:displayLink>
			</jstl:if>
			
			<jstl:if test="${categoryToMoveId!=null}">
				
				<!-- Si movemos a una categoría distinta, mostramos el botón -->
				<jstl:if test="${categoryToMoveId!= category.getId()}">
					<acme:displayLink parameter="categoryNewFatherId" code="category.move.here" action="category/moderator/reorganising.do" parameterValue="${category.getId()}" parameter2="categoryToMoveId" parameterValue2="${categoryToMoveId}" css="btn btn-warning"></acme:displayLink>
				</jstl:if>
				
				<!-- Si estamos en la categoría que vamos a mover mostramos un mensaje -->
				<jstl:if test="${categoryToMoveId== category.getId()}">
					<spring:message code="category.navigation" var="navigation"></spring:message>
					<p><jstl:out value="${navigation}"></jstl:out></p>
				</jstl:if>
				<acme:displayLink parameter="categoryId" code="category.move.cancel" action="category/display.do" parameterValue="${category.getId()}" parameter2="page" parameterValue2="${page}" parameter3="categoryToMoveId" parameterValue3="" css="btn btn-danger"></acme:displayLink>
			
			</jstl:if>
		</security:authorize>
		
		<!-- Mostramos los rendezvous asociados a una categoría -->
		<acme:displayLink code="category.bargains" action="bargain/bycategory.do"  parameter="categoryId" parameterValue="${category.getId()}" css="btn btn-primary"></acme:displayLink>	
		
	</jstl:if>
	
	<!-- Si estamos en la raíz damos opción a crear categoría o mover una categoría a la raíz -->
	<jstl:if test="${category==null}">
		<security:authorize access="hasRole('MODERATOR')">		
			<acme:displayLink code="category.create" action="category/moderator/create.do" css="btn btn-primary"></acme:displayLink>
		</security:authorize>
		
		<!-- Mover a la raíz -->
		<jstl:if test="${categoryToMoveId!=null}">
			<acme:displayLink parameter="categoryNewFatherId" code="category.move.here" action="category/moderator/reorganising.do" parameterValue="${0}" parameter2="categoryToMoveId" parameterValue2="${categoryToMoveId}" css="btn btn-warning"></acme:displayLink>
		</jstl:if>
	</jstl:if>
	
	
	
</div>

<jstl:if test="${childrenCategories.size()>0}">
	
	<jstl:forEach var="row" items="${childrenCategories}">
		
		<div class="container-square2">
	
			<acme:display code="category.name" value="${row.getName()}"/>
	
			<br><br>
			<acme:displayLink parameter="categoryId" code="category.children" action="category/display.do" parameterValue="${row.getId()}" parameter2="categoryToMoveId" parameterValue2="${categoryToMoveId}" css="btn btn-primary"></acme:displayLink>		
			
			
		</div>
		
	</jstl:forEach>
	
	
	<acme:paginate url="category/display.do" objects="${childrenCategories}" parameter="categoryId" parameterValue="${category.getId()}" parameter2="categoryToMoveId" parameterValue2="${categoryToMoveId}" page="${page}" pageNumber="${pageNumber}"/>
	
</jstl:if> 
	
	

	