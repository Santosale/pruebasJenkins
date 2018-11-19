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



<display:table class="table table-striped table-bordered table-hover" name="followUps" id="row">
	
	
	<!-- Para editar follow Up -->
	<security:authorize access="hasRole('USER')">
	
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${row.getUser().getUserAccount().getUsername().equals(principal) && canDelete!=null}">
			<acme:columnLink id="${row.getId()}" domain="followUp" actor="user" action="delete"/>
		</jstl:if>	
		
	</security:authorize>	
		
	<acme:column property="title" domain="follow.up" />
	
	<acme:column property="summary" domain="follow.up" />
	
	<acme:column property="publicationMoment" domain="follow.up" formatDate="true"/>
	
	
	<acme:columnLink id="${row.getId()}" domain="followUp" action="display"/>
	

</display:table>

<jstl:if test="${requestURI.equals('followUp/user/list.do')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${followUps}" page="${page}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('followUp/list.do')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${followUps}" page="${page}" parameter="articleId" parameterValue="${articleId}"/>
</jstl:if>

