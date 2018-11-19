<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div class="container">

	<jstl:if test="${hasPlanComment}">
		<jstl:set value='background-color: #ec6354;color: white;' var="styleComment"/>
	</jstl:if>
	
	<spring:message code="comment.alt" var="commentAlt"/>
	
	<jstl:forEach items="${mapLinkBoolean}" var="mapEntry">
     
		<jstl:if test="${mapEntry.key!=null && mapEntry.key!='' && mapEntry.value==false}">
			<acme:image value="${mapEntry.key}" alt="${commentAlt}"/>
			<br/><br/>
		</jstl:if>
		
		<jstl:if test="${mapEntry.key!=null && mapEntry.key!='' && mapEntry.value==true}">
			<acme:image value="${defaultImage}" alt="${commentAlt}"/>		
			<br/><br/>
		</jstl:if>
	
	</jstl:forEach>
	
	<div style="${styleComment}">
	<acme:display code="comment.moment" value="${comment.getMoment()}" codeMoment="comment.format.moment"/>
	
	<acme:display code="comment.text" value="${comment.getText()}"/>
	</div>
	
	<security:authorize access="hasRole('USER')"> 
		<jstl:if test="${canComment}">
			<spring:url var="urlCreateComment" value="comment/user/create.do">
				<spring:param name="repliedCommentId" value="${comment.getId()}" />
				<spring:param name="bargainId" value="${comment.getBargain().getId()}" />
			</spring:url>
			
			<p><span  style='margin-right:10px;'><a href="${urlCreateComment}" class='btn btn-primary'><spring:message code="comment.create"/></a></span></p>
		</jstl:if>
	</security:authorize>
	
	<security:authorize access="hasRole('MODERATOR')">
		<acme:displayLink parameter="commentId" code="comment.delete" action="comment/moderator/edit.do" parameterValue="${comment.getId()}" css="btn btn-warning"></acme:displayLink>
	</security:authorize>
	
	<acme:displayLink parameter="bargainId" code="comment.bargain" action="bargain/display.do" parameterValue="${comment.getBargain().getId()}" css="btn btn-primary"></acme:displayLink>		
	<acme:displayLink parameter="userId" code="comment.user" action="actor/user/display.do" parameterValue="${comment.getUser().getId()}" css="btn btn-primary"></acme:displayLink>		
	
<jstl:if test="${comments.size()>0}">
	<span class="display"><spring:message code="comment.replied.comment"/></span>
	
	<jstl:forEach var="row" items="${comments}">
		
		<jstl:if test="${mapCommentBoolean[row]}">
			<jstl:set value='background-color: #ec6354;color: white;' var="style"/>
		</jstl:if>
	
		<div class="container-square2" style="${style}">
				
			<acme:display code="comment.moment" value="${row.getMoment()}" codeMoment="comment.format.moment"/>
	
			<acme:display code="comment.text" value="${row.getText()}"/>
			
			<acme:displayLink parameter="commentId" code="comment.more" action="comment/display.do" parameterValue="${row.getId()}" css="btn btn-primary"></acme:displayLink>		
			
		</div>
		
	</jstl:forEach>
	
	<acme:paginate url="comment/display.do" objects="${comments}" parameter="commentId" parameterValue="${comment.getId()}" page="${page}" pageNumber="${pageNumber}"/>
		
</jstl:if> 
</div>