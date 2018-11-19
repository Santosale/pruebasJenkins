<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="acme"	tagdir="/WEB-INF/tags"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container">

	<jstl:if test="${advertisement!=null}">
		<acme:image value="${advertisement.getUrlBanner()}" alt="${advertisementAlt}" url="${advertisement.getUrlTarget()}"/>
		<br>
		<br>
	</jstl:if>

	<acme:display code="article.moment" value="${article.getMoment()}" codeMoment="article.format.moment"/>

	<acme:display code="article.title" value="${article.getTitle()}"/>
	
	<acme:display code="article.summary" value="${article.getSummary()}"/>

	<acme:display code="article.body" value="${article.getBody()}"/>
	
	<jstl:if test="${article.getHasTaboo()}">
		<acme:display code="article.hasTaboo" value="X"/>
	</jstl:if>
	
	<jstl:if test="${article.getIsFinalMode()}">
		<acme:display code="article.isFinalMode" value="X"/>
	</jstl:if>
	
	<spring:url var="urlNewspaper" value="newspaper/display.do">
		<spring:param name="newspaperId" value="${article.getNewspaper().getId()}" />
	</spring:url>
	<p><a href="${urlNewspaper}"> <spring:message code="article.newspaper" /></a></p>
	
	<spring:url var="urlFollowUp" value="followUp/list.do">
		<spring:param name="articleId" value="${article.getId()}" />
	</spring:url>
	<p><a href="${urlFollowUp}"> <spring:message code="article.followup" /></a></p>
	
	<spring:url var="urlUser" value="actor/user/display.do">
		<spring:param name="userId" value="${article.getWriter().getId()}" />
	</spring:url>
	<p><a href="${urlUser}"> <spring:message code="article.writer" /></a></p>
	
	<security:authorize access="hasRole('USER')">
		<jstl:if test="${article.getIsFinalMode() && article.getNewspaper().getIsPublished()}">
			<spring:url var="urlFollow" value="followUp/user/create.do">
				<spring:param name="articleId" value="${article.getId()}" />
			</spring:url>
			<p><a href="${urlFollow}"> <spring:message code="article.follow" /></a></p>
		</jstl:if>
	</security:authorize>
	
	<jstl:if test="${article.getPictures().size() != 0}">
		<acme:display code="article.pictures" value=""/>
		<jstl:forEach items="${article.getPictures()}" var="picture">
	    	<img src="${picture}" alt="Picture" width="400px" height="200px" style="margin-left:15px;" /><br /><br />
		</jstl:forEach>
	</jstl:if>

</div>
