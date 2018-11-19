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

<jstl:if test="${role.equals('user')}">

	<p><a class="btn btn-primary" href="chirp/user/create.do"><spring:message code="chirp.create" /></a></p>
	
	<p><a href="actor/user/followers.do"><spring:message code="chirp.numberFollowers" />: ${countFollowers}</a> | <a href="actor/user/followeds.do"><spring:message code="chirp.numberFolloweds" />: ${countFolloweds}</a></p>
	
	<jstl:forEach var="row" items="${chirps}">
		<div class="bg-primary" style="padding:15px;">
			<h3 style="margin-top: 0px;">${row.getTitle()}</h3>
			<p>${row.getDescription()}</p>
			<small><a style="color: white" href="actor/user/display.do?userId=${row.getUser().getId()}">${row.getUser().getName()} ${row.getUser().getSurname()}</a></small>
		</div>
		<br>
	</jstl:forEach>

</jstl:if>

<jstl:if test="${role.equals('administrator')}">

	<display:table class="table table-striped table-bordered table-hover" defaultorder="descending" defaultsort="5" name="chirps" id="row" requestURI="chirp/administrator/list.do">
	
		<jstl:if test="${row.getHasTaboo() == true}">
			
			<acme:columnLink style="background:red; color:white" action="delete" domain="chirp" id="${row.getId()}" url="chirp/administrator/delete.do?chirpId=${row.getId()}"/>
	
			<acme:column style="background:red; color:white" domain="chirp" property="moment" formatDate="true" />
			
			<acme:column style="background:red; color:white" domain="chirp" property="title"/>
			
			<acme:column style="background:red; color:white" domain="chirp" property="description"/>
			
			<display:column property="hasTaboo" class="hidden" headerClass="hidden" />
			
			<acme:column style="background:red; color:white" domain="chirp" property="user" row="${row}" />
		
		</jstl:if>
		
		<jstl:if test="${row.getHasTaboo() == false}">
			
			<acme:columnLink action="delete" domain="chirp" id="${row.getId()}" url="chirp/administrator/delete.do?chirpId=${row.getId()}"/>
	
			<acme:column domain="chirp" property="moment" formatDate="true" />
			
			<acme:column domain="chirp" property="title"/>
			
			<acme:column domain="chirp" property="description"/>
			
			<display:column property="hasTaboo" class="hidden" headerClass="hidden" />
			
			<acme:column domain="chirp" property="user" row="${row}" />
		
		</jstl:if>
	
	</display:table>
</jstl:if>

<acme:paginate pageNumber="${pageNumber}" url="chirp/${role}/list.do" objects="${chirps}" page="${page}"/>