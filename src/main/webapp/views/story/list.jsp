<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

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

<spring:url var="urlURI" value="story/explorer/list.do" />
<display:table class="table table-striped table-bordered table-hover" name="stories" id="row" requestURI="${urlURI }"
	pagesize="5" >

	<display:column>
		<security:authorize access="isAuthenticated()">
			<security:authentication var="principal" property="principal.username"/>
			
			<jstl:if test="${row.getWriter().getUserAccount().getUsername().equals(principal)}">
				<a href="story/explorer/edit.do?storyId=${row.getId()}"> <spring:message
					code="story.edit" />
				</a>
				<br/>
			</jstl:if>
		</security:authorize>
	</display:column>

	<spring:message code="story.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}"></display:column>

	<spring:message code="story.text" var="textHeader" />
	<display:column property="text" title="${textHeader}"></display:column>

	<spring:message code="story.attachments" var="attachmentsHeader" />
	<display:column property="attachments" title="${attachmentsHeader}">
	</display:column>

	<spring:url var="urlDisplay" value="story/explorer/display.do">
		<spring:param name="storyId" value="${row.getId()}" />
	</spring:url>
	<display:column>
		<a href="${urlDisplay }"> <spring:message code="story.display" /></a>
	</display:column>

</display:table>

<br />
