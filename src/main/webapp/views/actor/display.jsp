<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="acme"	tagdir="/WEB-INF/tags"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container">

	<acme:display code="actor.name" value="${actor.getName()}"/>
	
	<acme:display code="actor.surname" value="${actor.getSurname()}"/>
	
	<acme:display code="actor.email" value="${actor.getEmailAddress()}"/>
	
	<acme:display code="actor.phone" value="${actor.getPhoneNumber()}"/>
	
	<acme:display code="actor.address" value="${actor.getPostalAddress()}"/>
	
	<jstl:if test="${role.equals('USER')}">
		<a href="actor/user/followers.do?userId=${actor.getId()}"><spring:message code="actor.followers" /></a>
		<a href="actor/user/followeds.do?userId=${actor.getId()}"><spring:message code="actor.followeds" /></a>
		
		<jstl:if test="${articles != null}">
			<h2><spring:message code="actor.articles" /></h2>
			<jstl:forEach var="a" items="${articles}">
				<a href="article/display.do?articleId=${a.getId()}"><jstl:out value="${a.getTitle()}" /></a>
			</jstl:forEach>
			<a class="btn btn-primary" href="article/list.do?userId=${actor.getId()}"><spring:message code="actor.user.readMoreArticles" /></a>
		</jstl:if>
	</jstl:if>
		
</div>
