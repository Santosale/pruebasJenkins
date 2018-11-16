<%--
 * navigate.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<p><strong><spring:message code="category.name" /></strong> <jstl:out value=" ${category.getName()}" /></p>

<jstl:if test="${!category.getName().equals('CATEGORY')}">

	<spring:url var="urlFather" value="category/navigate.do">
		<spring:param name="categoryId" value="${category.getFatherCategory().getId() }" />
	</spring:url>
	
	<p>
		<strong><spring:message code="category.fatherCategory" /></strong>
		<a href="${urlFather}"><jstl:out value="${category.getFatherCategory().getName()}" /></a>
	</p>

</jstl:if>

<spring:url var="urlTrips" value="category/list.do?categoryId=${category.getId()}" />

<a href="${urlTrips}"><spring:message code="category.trips" /></a>

<br><br>

<security:authorize access="hasRole('ADMIN')">

	<jstl:if test="${!category.getName().equals('CATEGORY')}">
	
		<a href="category/administrator/edit.do?categoryId=${category.getId()}">
			<spring:message code="category.edit" />
		</a>
		
	</jstl:if>

</security:authorize>


<jstl:if test="${!category.getChildrenCategories().isEmpty()}">

	<display:table class="table table-striped table-bordered table-hover" name="category.childrenCategories" id="row" requestURI="category/navigate.do" pagesize="5">

		<spring:message code="category.childrenCategories" var="childrenCategoriesHeader" />

		<display:column title="${childrenCategoriesHeader}">

			<spring:url var="urlCategoryChildren" value="category/navigate.do">
				<spring:param name="categoryId" value="${row.getId() }" />
			</spring:url>
			
			<a href="${urlCategoryChildren}"> <jstl:out value="${row.getName()}" /> </a>
			
		</display:column>

	</display:table>

</jstl:if>

<security:authorize access="hasRole('ADMIN')">

	<spring:url var="urlCreate" value="category/administrator/create.do">
		<spring:param name="categoryId" value="${category.getId() }" />
	</spring:url>
	<a href="${urlCreate}"><spring:message code="category.create" /></a>
</security:authorize>



