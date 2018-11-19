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

<div class="container">
	
	<jstl:if test="${level.getImage()!=null && level.getImage()!='' && linkBroken==false}">
		<acme:image value="${level.getImage()}" alt="${levelAlt}"/>
		<br/><br/>
	</jstl:if>
	
	<jstl:if test="${level.getImage()!=null && level.getImage()!='' && linkBroken==true}">
		<acme:image value="${defaultImage}" alt="${levelAlt}"/>		
		<br/><br/>
	</jstl:if>
	
	<acme:display code="level.name" value="${level.getName()}"/>
	
	<acme:display code="level.minPoints" value="${level.getMinPoints()}"/>
	
	<acme:display code="level.maxPoints" value="${level.getMaxPoints()}"/>
												
</div>
		
			