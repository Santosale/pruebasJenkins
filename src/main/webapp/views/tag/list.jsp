<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="tags" id="row" requestURI="tag/list.do">
	<spring:url value="bargain/bytag.do" var="url"><spring:param name="tagId" value="${row.getId()}" /></spring:url>
	
	
	<acme:column domain="tag" property="name"/>
	<display:column><a href="${url}"><spring:message code="tag.bargains" /></a></display:column>

	
</display:table>

<acme:paginate pageNumber="${pageNumber}" url="tag/list.do" objects="${tags}" page="${page}" />
