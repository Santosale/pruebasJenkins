<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="acme"	tagdir="/WEB-INF/tags"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<security:authorize access="hasRole('ADMIN')">
	<jstl:if test="${requestURI.equals('article/administrator/list.do') || requestURI.equals('article/administrator/listSearch.do')}">
		<spring:url var="searchURL" value="article/administrator/listSearch.do" />
	</jstl:if>
	<jstl:if test="${requestURI.equals('article/administrator/listTaboo.do') || requestURI.equals('article/administrator/listSearchTaboo.do')}">
		<spring:url var="searchURL" value="article/administrator/listSearchTaboo.do" />
	</jstl:if>
</security:authorize>

<jstl:if test="${requestURI.equals('article/user/list.do') || requestURI.equals('article/user/listSearch.do')}">
	<spring:url var="searchURL" value="article/user/listSearch.do" />
</jstl:if>

<jstl:if test="${requestURI.equals('article/list.do') || requestURI.equals('article/listSearch.do')}">
	<spring:url var="searchURL" value="article/listSearch.do" />
</jstl:if>

<form class="navbar-form navbar-right" action="${searchURL}" method="GET">
	<div class="form-group">
		<jstl:if test="${userId != null}">
          	<input name="userId" value="${userId}" hidden="true"/>
		</jstl:if>
		<jstl:if test="${keyword != null}">
	  		<input type="text" name="keyword" value="${keyword}" class="form-control" placeholder="<spring:message code="article.search" />">
	  	</jstl:if>
	  	<jstl:if test="${keyword == null}">
	  		<input type="text" name="keyword" class="form-control" placeholder="<spring:message code="article.search" />">
	  	</jstl:if>
	</div>
	<button type="submit" class="btn btn-default"><spring:message code="master.page.submit"/></button>
</form>

<spring:url var="requestURIView" value="${requestURI}">
	<jstl:if test="${userId != null}">
		<spring:param name="userId" value="${userId}"/>
	</jstl:if>
	<jstl:if test="${keyword != null}">
		<spring:param name="keyword" value="${keyword}"/>
	</jstl:if>
</spring:url>

<display:table class="table table-striped table-bordered table-hover" name="articles" id="row" requestURI="${requestURIView}">

	<jstl:if test="${requestURI.equals('article/user/list.do') || requestURI.equals('article/user/listSearch.do')}">
		<spring:message code="article.edit" var="editHeader" />
		<display:column title="${editHeader}">
		<jstl:if test="${!row.getIsFinalMode()}">
			<spring:url var="urlEdit" value="article/user/edit.do">
				<spring:param name="articleId" value="${row.getId()}" />
			</spring:url>
			<p><a href="${urlEdit}">${editHeader}</a></p>		
		</jstl:if>
		</display:column>
	</jstl:if>
	
	<security:authorize access="hasRole('ADMIN')">
		<jstl:if test="${requestURI.equals('article/administrator/list.do') || requestURI.equals('article/administrator/listSearch.do')}">
			<acme:columnLink action="deleteLis" domain="article" id="${row.getId()}" actor="administrator"/>
		</jstl:if>
		<jstl:if test="${requestURI.equals('article/administrator/listTaboo.do') || requestURI.equals('article/administrator/listSearchTaboo.do')}">
			<acme:columnLink action="deleteTab" domain="article" id="${row.getId()}" actor="administrator"/>
		</jstl:if>
	</security:authorize>
	
	<acme:column property="moment" domain="article"  formatDate="true"/>
	
	<acme:column property="title" domain="article"/>
	
	<acme:column property="summary" domain="article"/>
		
	<acme:column property="body" domain="article"/>
	
	<spring:message code="article.isFinalMode" var="isFinalMode"/>
	<display:column title="${isFinalMode}">
		<jstl:if test="${row.getIsFinalMode()}">
		 	<jstl:out value="X"/>
		</jstl:if>
	</display:column>
		
	<spring:message code="article.hasTaboo" var="hasTaboo"/>
	<display:column title="${hasTaboo}">
		<jstl:if test="${row.getHasTaboo()}">
		 	<jstl:out value="X"/>
		</jstl:if>
	</display:column>
		
	<acme:columnLink domain="article" action="display" id="${row.getId()}"/>
	
</display:table>

<acme:paginate url="${requestURIView}" objects="${articles}" pageNumber="${pageNumber}" page="${page}"/>

