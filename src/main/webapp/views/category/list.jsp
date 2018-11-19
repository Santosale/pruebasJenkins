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
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>



<display:table class="table table-striped table-bordered table-hover" name="categories" id="row">
	

		
			<jstl:if test="${!action.equals('dashboard')}">
				<display:column>
			
				<jstl:if test="${action.equals('add')}">
						<spring:url var="urlAddCategory" value="bargain/company/addCategory.do">
							<spring:param name="bargainId" value="${bargainId}" />
							<spring:param name="categoryId" value="${row.getId()}" />
						</spring:url>
						<a href="${urlAddCategory }"> <spring:message code="category.add" /></a>
				</jstl:if>
				
				<jstl:if test="${action.equals('remove') && !(row.getDefaultCategory() && categories.size()==1)}">
					<spring:url var="urlRemoveCategory" value="bargain/company/removeCategory.do">
						<spring:param name="bargainId" value="${bargainId}" />
						<spring:param name="categoryId" value="${row.getId()}" />
					</spring:url>
					<a href="${urlRemoveCategory }"> <spring:message code="category.remove" /></a>
				</jstl:if>
				
				
				<jstl:if test="${action.equals('create')}">
					<spring:url var="urlCreateService" value="bargain/company/create.do">
						<spring:param name="categoryId" value="${row.getId()}" />
					</spring:url>
					<a href="${urlCreateService}"> <spring:message code="category.choose.create" /></a>
				</jstl:if>
							
				</display:column>
			</jstl:if>
			<acme:column domain="category" property="name"/>		
		
	
	
</display:table>


<jstl:if test="${bargainId!=0}">
	<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${categories}" page="${page}" parameter="bargainId" parameterValue="${bargainId}"/>
</jstl:if>

<jstl:if test="${bargainId==0}">
	<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${categories}" page="${page}"/>
</jstl:if>





