<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="surveys" id="row" requestURI="${requestURI}">
	
	<jstl:if test="${requestURI != 'survey/administrator/morePopular.do'}">
		<acme:columnLink domain="survey" action="edit" id="${row.getId()}" actor="${model}"/>
	</jstl:if>
	
	<acme:column property="title" domain="survey" />
	
	<jstl:if test="${requestURI != 'survey/administrator/morePopular.do'}">
		<acme:columnLink action="display" actor="${model}" domain="survey" id="${row.getId()}" />
	</jstl:if>
	
</display:table>

<acme:paginate url="${requestURI}" objects="${surveys}" pageNumber="${pageNumber}" page="${page}" />

<jstl:if test="${requestURI != 'survey/administrator/morePopular.do'}">
	<acme:displayLink code="survey.create" action="survey/${model}/create.do"/>
</jstl:if>