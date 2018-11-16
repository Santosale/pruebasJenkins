
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<form:form action="category/administrator/edit.do" modelAttribute="category">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="childrenCategories" />
	<form:hidden path="fatherCategory" />

	<div class="form-group"> 
		<form:label path="name">
			<spring:message code="category.name" />
		</form:label>
		<form:input class="form-control" path="name" />
		<form:errors class="text-danger" path="name" />
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="category.save" />" >&nbsp; 
	
	<jstl:if test="${category.id != 0}">
			<input type="submit" class="btn btn-danger" name="delete" value="<spring:message code="category.delete" />" onclick="return confirm('<spring:message code="category.confirm.delete" />')" >
	</jstl:if>

	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="category.cancel" />" onclick="javascript: relativeRedir('category/navigate.do?categoryId=' + ${categoryId});" >
	
</form:form>