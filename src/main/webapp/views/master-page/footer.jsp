<%--
 * footer.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>


<jsp:useBean id="date" class="java.util.Date" />

<footer>

	<hr>
	
	<spring:url var="urlAboutUsFooter" value="aboutUs/display.do"></spring:url>
	<span class="display"><a href="${urlAboutUsFooter}" > <spring:message code="master.page.about.us" /></a></span>
	
	<spring:url var="urlTermConditionFooter" value="termCondition/display.do"></spring:url>
	<span class="display"><a href="${urlTermConditionFooter}"> <spring:message code="master.page.term.condition" /></a></span>
	
		<br/>
	<small><jstl:out value="${mail }"/></small>	
	
	<br/>
	
	<small>Copyright &copy; <fmt:formatDate value="${date}" pattern="yyyy" /> Acme, Inc.</small>
	
	
</footer>