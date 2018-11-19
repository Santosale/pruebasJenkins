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



<jstl:forEach var="row" items="${plans}">
	<div class="container-square2" style="border:2px solid black; margin-left:25px; margin-bottom:20px; padding:10px;">
	
		<div>
				
				<acme:display code="plan.name" value="${row.getName()}"/>
				
				<acme:display code="plan.description" value="${row.getDescription()}"/>
						
				<acme:display code="plan.cost" value="${row.getCost()}" formatNumber="true" domain="plan" />
						
							
			</div>
		
			<security:authorize access="hasRole('ADMIN')">
				<div class="container">	
				<span style="font-size:20px"><spring:message code="plan.admin.actions"/></span>	
				<acme:displayLink parameter="planId" code="plan.edit" action="plan/administrator/edit.do" parameterValue="${row.getId()}" css="btn btn-primary"></acme:displayLink>		
				</div>
			</security:authorize>
			
			<security:authorize access="hasRole('USER')">
				<jstl:if test="${canCreateSubscription==true }">
					<div class="container">	
					<span style="font-size:20px"><spring:message code="plan.user.actions"/></span>	
					<acme:displayLink parameter="planId" code="plan.createSubscription" action="subscription/user/create.do" parameterValue="${row.getId()}" css="btn btn-primary"></acme:displayLink>		
					</div>
				</jstl:if>	
			</security:authorize>
	</div>
			
	</jstl:forEach>	

