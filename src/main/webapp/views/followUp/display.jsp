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

	<spring:message code="follow.up.alt" var="followUpAlt"/>
	
	<acme:display code="follow.up.publicationMoment" value="${followUp.getPublicationMoment()}" codeMoment="follow.up.format.moment"/>
	
	<acme:display code="follow.up.title" value="${followUp.getTitle()}"/>
	
	<acme:display code="follow.up.summary" value="${followUp.getSummary()}"/>

	<acme:display code="follow.up.text" value="${followUp.getText()}"/>
	
	<acme:displayLink parametre="userId" code="follow.up.user" action="actor/user/display.do" parametreValue="${followUp.getUser().getId()}" css="btn btn-primary"></acme:displayLink>		
	<acme:displayLink parametre="articleId" code="follow.up.article" action="article/display.do" parametreValue="${followUp.getArticle().getId()}" css="btn btn-primary"></acme:displayLink>		
	
</div>


<jstl:if test="${followUp.getPictures().size()>0}">
	
	<jstl:forEach var="row" items="${followUp.getPictures()}">
		
		<div class="container-square2">
		
			<acme:image value="${row}" alt="${followUpAlt}"/>
			
		</div>
		
	</jstl:forEach>
	
	
</jstl:if> 
	
	

	