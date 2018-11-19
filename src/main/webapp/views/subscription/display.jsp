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

<jstl:if test="${subscription !=null }">
	<div class="container-square2" style="border:2px solid black; margin-left:25px; margin-bottom:20px; padding:10px;">

		
		<div class="container">
				
				<acme:display code="subscripton.plan" value="${subscription.getPlan().getName()}"/>
				
				<acme:display code="subscription.payFrecuency" value="${subscription.getPayFrecuency()}"/>
												
			</div>
		
			
			<security:authorize access="hasRole('USER')">
					<div class="container">	
					<acme:displayLink parameter="subscriptionId" code="subscription.edit" action="subscription/user/edit.do" parameterValue="${subscription.getId()}" css="btn btn-primary"></acme:displayLink>		
					</div>
			</security:authorize>
		
	</div>
</jstl:if>

<jstl:if test="${subscription ==null }">
	<h2><spring:message code="subscription.notSubscription.message"/></h2>
	<br>
	<acme:displayLink code="subscription.see.plan" action="plan/display.do" css="btn btn-primary"></acme:displayLink>			
</jstl:if>