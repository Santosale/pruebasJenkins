<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="followUp/user/edit.do" modelAttribute="followUp">

	<form:hidden path="id"/>
	
	<jstl:if test="${followUp.getId()==0}">
	 	<form:hidden path="article"/>
 	</jstl:if>

	<acme:textbox code="follow.up.title" path="title"/>
	
	<acme:textbox code="follow.up.summary" path="summary"/>
	
	<acme:textbox code="follow.up.text" path="text"/>
	
	<acme:textbox code="follow.up.publicationMoment" path="publicationMoment" readonly="readonly"/>
	
	<acme:textarea code="follow.up.pictures" path="pictures"/>
	
	
	
	<security:authorize access="hasRole('USER')">
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${principal.equals(followUp.getUser().getUserAccount().getUsername()) && followUp.getId()==0}">
			<acme:submit name="save" code="follow.up.save" />
		</jstl:if>
	
		
	</security:authorize>
	
	<jstl:if test="${followUp.getArticle()!=null}">
		<acme:cancel url="article/display.do?articleId=${followUp.getArticle().getId()}" code="follow.up.cancel"/>
	</jstl:if>
	
	<jstl:if test="${followUp.getArticle()==null}">
		<acme:cancel url="followUp/list.do?userId=${followUp.getUser().getId()}" code="follow.up.cancel"/>
	</jstl:if>
	

</form:form>