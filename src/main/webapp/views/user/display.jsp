<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container">

	<security:authorize access="hasRole('USER')">
		<jstl:if test="${!isSamePerson}">
			<jstl:if test="${!isFollowing}">
				<a class="btn btn-primary"
					href="actor/user/follow.do?userId=${user.getId()}"><spring:message
						code="user.follow" /></a>
				<br>
				<br>
			</jstl:if>
			<jstl:if test="${isFollowing}">
				<a class="btn btn-primary"
					href="actor/user/unfollow.do?userId=${user.getId()}"><spring:message
						code="user.unfollow" /></a>
				<br>
				<br>
			</jstl:if>
		</jstl:if>
	</security:authorize>

	<acme:display code="actor.name" value="${user.getName()}" />

	<acme:display code="actor.surname" value="${user.getSurname()}" />

	<acme:display code="actor.emailAddress"
		value="${user.getEmailAddress()}" />

	<acme:display code="actor.phoneNumber" value="${user.getPhoneNumber()}" />

	<acme:display code="actor.postalAddress"
		value="${user.getPostalAddress()}" />

	<jstl:if test="${isSamePerson}">
		<a class="btn btn-primary" href="actor/user/followers.do"><spring:message code="user.followers" /></a> 
		<a class="btn btn-primary" href="actor/user/followeds.do"><spring:message code="user.followeds" /></a>
	</jstl:if>

	<h2>
		<spring:message code="user.articles" />
	</h2>
	<jstl:if test="${articles != null && articles.size() != 0}">
		<jstl:forEach var="a" items="${articles}">
			<p><a href="article/display.do?articleId=${a.getId()}"><jstl:out
					value="${a.getTitle()}" /></a></p>
		</jstl:forEach>
		<a class="btn btn-primary"
			href="article/list.do?userId=${user.getId()}"><spring:message
				code="user.readMoreArticles" /></a>
	</jstl:if>
	<jstl:if test="${articles == null || articles.size() == 0}">
		<p>
			<spring:message code="user.noArticles" />
		</p>
	</jstl:if>

	<h2>
		<spring:message code="user.chirps" />
	</h2>
	<jstl:if test="${chirps != null && chirps.size() != 0}">
		<jstl:forEach var="row" items="${chirps}">
			<div class="bg-primary" style="padding: 15px;">
				<h3 style="margin-top: 0px;">${row.getTitle()}</h3>
				<p>${row.getDescription()}</p>
				<small><a style="color: white" href="actor/user/display.do?userId=${row.getUser().getId()}">${row.getUser().getName()} ${row.getUser().getSurname()}</a></small>
			</div>
			<br>
		</jstl:forEach>
		<acme:paginate pageNumber="${pageNumber}" url="actor/user/display.do?userId=${user.getId()}" objects="${chirps}" page="${page}"/>
	</jstl:if>
	<jstl:if test="${chirps == null || chirps.size() == 0}">
		<p>
			<spring:message code="user.noChirps" />
		</p>
	</jstl:if>

</div>
