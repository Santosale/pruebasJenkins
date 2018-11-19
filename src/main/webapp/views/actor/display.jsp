<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="acme"	tagdir="/WEB-INF/tags"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container" style="display: flex;">

	<jstl:if test="${model.equals('user')}">
		<img style="margin-right: 50px;" src="${actor.getAvatar()}" alt="Avatar" width="300" height="300">
	</jstl:if>
	
	<div style="display: inline-block">
	
		<acme:display code="actor.name" value="${actor.getName()}"/>
		
		<acme:display code="actor.surname" value="${actor.getSurname()}"/>
		
		<acme:display code="actor.email" value="${actor.getEmail()}"/>
		
		<acme:display code="actor.phone" value="${actor.getPhone()}"/>
		
		<acme:display code="actor.address" value="${actor.getAddress()}"/>
		
		<jstl:if test="${model.equals('user')}">
			<jstl:if test="${!isPublic}">
				<acme:display code="actor.identifier" value="${actor.getIdentifier()}"/>
				<jstl:if test="${actor.getIsPublicWishList()}">
					<p><span class="display"><spring:message code="actor.isPublicWishListTrue" /></span>&nbsp;-&nbsp;<a href="actor/user/changewishlist.do"><spring:message code="actor.changewishlist" /></a></p>
				</jstl:if>
				<jstl:if test="${!actor.getIsPublicWishList()}">
					<p><span class="display"><spring:message code="actor.isPublicWishListFalse" /></span>&nbsp;-&nbsp;<a href="actor/user/changewishlist.do"><spring:message code="actor.changewishlist" /></a></p>
				</jstl:if>
			</jstl:if>
		</jstl:if>
				
		<jstl:if test="${model.equals('user')}">
			<acme:display code="actor.points" value="${actor.getPoints()}" />
			
			<br>
			<h4><spring:message code="actor.level" /></h4>
			<br>
			<acme:display code="level.name" value="${level.getName()}" /><img width="50" height="50" src="${level.getImage()}" />
			<jstl:if test="${actor.getIsPublicWishList() || !isPublic}">
				<br><br>
				<a href="actor/user/wishlist.do?actorId=${actor.getId()}" class="btn btn-primary"><spring:message code="actor.wishlist" /></a>
				<br>
			</jstl:if>
		</jstl:if>
		
		<jstl:if test="${model.equals('company')}">
			<acme:display code="actor.companyName" value="${actor.getCompanyName()}" />
			<acme:display code="actor.type" value="${actor.getType()}" />
			<security:authorize access="hasRole('USER')">
				<a class="btn btn-primary" href="evaluation/user/create.do?companyId=${actor.getId()}"><spring:message code="actor.addEvaluation" /></a>
			</security:authorize>
			<a class="btn btn-primary" href="evaluation/list.do?companyId=${actor.getId()}"><spring:message code="actor.seeEvaluations" /></a>
		</jstl:if>
		
	</div>
		
</div>
