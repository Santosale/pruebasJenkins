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

<form:form action="comment/${actor}/edit.do" modelAttribute="comment">

	<form:hidden path="id" />
	<form:hidden path="bargain" />
	
	<jstl:if test="${comment.getId() == 0}">
	 	<form:hidden path="repliedComment"/>
 	</jstl:if>

	<security:authorize access="hasRole('USER')">
		<jstl:if test="${comment.getId()==0}">
		<jstl:if test="${canImage}">
			<acme:textbox code="comment.image" path="images"/>
		</jstl:if>
		<acme:textarea code="comment.text" path="text"/>
		</jstl:if>
		<jstl:if test="${comment.getId()!=0}">
		<jstl:if test="${canImage}">
			<acme:textbox code="comment.image" path="images" readonly="readonly" />
		</jstl:if>
		<acme:textbox code="comment.text" path="text" readonly="readonly" />
		</jstl:if>
	</security:authorize>
	
	<security:authorize access="hasRole('MODERATOR')">
		<acme:textbox code="comment.image" path="images" readonly="readonly" />
		
		<acme:textbox code="comment.text" path="text" readonly="readonly" />
	</security:authorize>
	
	<acme:textbox code="comment.moment" path="moment" readonly="readonly"/>
	
	<security:authorize access="hasRole('USER')">
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${canEdit && comment.getId()==0}">
			<acme:submit name="save" code="comment.save" />
		</jstl:if>
	</security:authorize>
	
	<jstl:if test="${comment.getId()!= 0}">
		<acme:submit name="delete" code="comment.delete" codeDelete="comment.confirm.delete"/>
	</jstl:if>
		
	<jstl:if test="${requestURI=='comment/user/edit.do'}">
		<acme:cancel url="comment/user/list.do" code="comment.cancel"/>
	</jstl:if>
	
	<jstl:if test="${requestURI=='comment/user/create.do' && comment.getRepliedComment() == null}">
		<acme:cancel url="comment/user/list.do" code="comment.cancel"/>
	</jstl:if>
	
	<jstl:if test="${requestURI=='comment/user/create.do' && comment.getRepliedComment() != null}">
		<acme:cancel url="comment/display.do?commentId=${comment.getRepliedComment().getId()}" code="comment.cancel"/>
	</jstl:if>
	
	<jstl:if test="${requestURI=='comment/moderator/edit.do'}">
		<acme:cancel url="comment/moderator/list.do" code="comment.cancel"/>
	</jstl:if>
	

</form:form>