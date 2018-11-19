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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<jstl:if test="${canSeeGroupon }">
	<div class="container">
		
		<acme:display code="groupon.title" value="${groupon.getTitle()}"/>
		
		<acme:display code="groupon.description" value="${groupon.getDescription()}"/>
		
		<acme:display code="groupon.productName" value="${groupon.getProductName()}"/>
		
		<acme:displayLink code="groupon.productUrl" action="${groupon.getProductUrl()}" newTab="true"/>
		
		<acme:display code="groupon.minAmountProduct" value="${groupon.getMinAmountProduct()}"/>
		
		<acme:display code="groupon.maxDate" value="${groupon.getMaxDate()}" codeMoment="groupon.format.moment"/>
		
		<acme:display code="groupon.originalPrice" value="${groupon.getOriginalPrice()}" formatNumber="true" domain="groupon"/>
		
		<acme:display code="groupon.price" value="${groupon.getPrice()}" formatNumber="true" domain="groupon"/>
		
		<jstl:if test="${groupon.getDiscountCode()!=null && canSeeDiscountCode }">
			<acme:display code="groupon.discountCode" value="${groupon.getDiscountCode()}"/>
		</jstl:if>
		
		<security:authorize access="hasRole('USER')">
					<jstl:if test="${canCreateParticipation==true }">
						<div class="container">	
						<span style="font-size:20px"><spring:message code="groupon.user.actions"/></span>	
						<acme:displayLink parameter="grouponId" code="groupon.createParticipation" action="participation/user/create.do" parameterValue="${groupon.getId()}" css="btn btn-primary"></acme:displayLink>		
						</div>
					</jstl:if>	
		</security:authorize>
		
		<spring:message code="groupon.share.message" var="messageToShare"/>
		<acme:share url="${currentUrl}" message="${messageToShare }" />
		
		
		
		
														
	</div>
</jstl:if>

<jstl:if test="${!canSeeGroupon }">
	<h2><spring:message code="groupon.notVisible.message"/></h2>
	<br>
	<acme:displayLink code="groupon.welcomeBack" action="welcome/index.do" css="btn btn-primary"></acme:displayLink>			
</jstl:if>
		
			