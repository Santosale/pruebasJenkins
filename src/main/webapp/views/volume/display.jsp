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



	<div>
	
		<div class="container">
			
			<acme:display code="volume.title" value="${volume.getTitle()}"/>
			
			<acme:display code="volume.description" value="${volume.getDescription()}"/>
					
			<acme:display code="volume.year" value="${volume.getYear()}" />
					
						
		</div>
	
	
	<security:authorize access="hasRole('USER')">
	<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${principal.equals(volume.getUser().getUserAccount().getUsername()) }">
			<div class="container">	
			<span style="font-size:20px"><spring:message code="volume.user.actions"/></span>	
			<acme:displayLink parametre="volumeId" code="volume.addNewspaper" action="newspaper/user/addNewspaper.do" parametreValue="${volume.getId()}"/>
			<jstl:if test="${volume.getNewspapers().size()>1 }">
				<acme:displayLink parametre="volumeId" code="volume.deleteNewspaper" action="newspaper/user/deleteNewspaper.do" parametreValue="${volume.getId()}"/>
			</jstl:if>
			</div>
		</jstl:if>	
	</security:authorize>
	
	<security:authorize access="hasRole('CUSTOMER')">
		<jstl:if test="${canCreateVolumeSubscription }">
			<div class="container">	
			<span style="font-size:20px"><spring:message code="volume.customer.actions"/></span>	
			<acme:displayLink parametre="volumeId" code="volume.subscription.create" action="subscriptionVolume/customer/create.do" parametreValue="${volume.getId()}"/>
			</div>
		</jstl:if>	
	</security:authorize>
	
	<jstl:if test="${newspapers.size()>0 }">
	<display:table class="table table-striped table-bordered table-hover" name="newspapers" id="row">
	
	<acme:column property="title" domain="newspaper" />
	
	<acme:column property="description" domain="newspaper" />
	
	<acme:column property="publicationDate" domain="newspaper" formatDate="true" />
	
	<spring:message code="newspaper.isPublished" var="newspaperIsPublished"/>
	<display:column title="${newspaperIsPublished}">
		<jstl:if test="${ row.getIsPublished()==true && row.getPublicationDate()<=currentMomentVar}">
			<jstl:out value="X"/>
		</jstl:if>	
	</display:column>
	
	<spring:message code="newspaper.isPrivateTheNewspaper" var="newspaperIsPrivate"/>
	<display:column title="${newspaperIsPrivate}">
		<jstl:if test="${ row.getIsPrivate()==true}">
			<jstl:out value="X"/>
		</jstl:if>	
	</display:column>
		
		<spring:url var="urlDisplay" value="newspaper/display.do">
		<spring:param name="newspaperId" value="${row.getId()}" />
		
	</spring:url>
		<display:column>
			<a href="${urlDisplay }"> <spring:message code="newspaper.display" /></a>
	</display:column>
	
	</display:table>
	
	<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${newspapers}" page="${page}" parameter="volumeId" parameterValue="${volume.getId()}"/>
	
	</jstl:if>
																			
	</div>