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

<form:form action="${requestURI}" modelAttribute="evaluation">

	<form:hidden path="id"/>
	
	<jstl:if test="${evaluation.getId() == 0}">
 		<form:hidden path="company"/>
 	</jstl:if>
 	 	
	<jstl:if test="${requestURI != 'evaluation/moderator/edit.do'}">
 	<div class="form-group">
		<form:label path="puntuation">
			<spring:message code="evaluation.puntuation" />
		</form:label>	
		<form:select path="puntuation" class="form-control">
			<form:option label="1" value="1" />
			<form:option label="2" value="2" />
			<form:option label="3" value="3" />
			<form:option label="4" value="4" />
			<form:option label="5" value="5" />
		</form:select>
		<form:errors class="error" path="puntuation" cssClass="error" />
	</div>
	
	<acme:textarea path="content" code="evaluation.content" />
	
	<div class="form-check">
			<form:checkbox class="form-check-input" path="isAnonymous" id="check" checked=''/>
			<form:label path="isAnonymous"><div onclick="activar()" ><spring:message code="evaluation.isAnonymous" /></div></form:label>
			<form:errors class="text-danger" path="isAnonymous"/>
	</div>
	
	<br/>
	</jstl:if>
	
	<jstl:if test="${requestURI == 'evaluation/moderator/edit.do'}">
 	
	<acme:textbox code="evaluation.puntuation" path="puntuation" readonly="readonly"/>
	
	<acme:textbox path="content" code="evaluation.content" readonly="readonly" />
	
	<br/>
	</jstl:if>
	
	<jstl:if test="${requestURI != 'evaluation/moderator/edit.do'}">
		<acme:submit name="save" code="evaluation.save" />
	</jstl:if>

	<jstl:if test="${evaluation.getId()!= 0}">
		<acme:submit name="delete" code="evaluation.delete" />
	</jstl:if>
	
	<jstl:if test="${requestURI != 'evaluation/moderator/edit.do'}">
		<acme:cancel code="evaluation.cancel" url="evaluation/user/list.do" />
	</jstl:if>
	<jstl:if test="${requestURI == 'evaluation/moderator/edit.do'}">
		<acme:cancel code="evaluation.cancel" url="evaluation/moderator/list.do" />
	</jstl:if>
	
</form:form>

<script type="text/javascript">

	function activar() {
		if  (document.getElementById("check").checked)
			document.getElementById("check").checked = false;
		else if (!document.getElementById("check").checked)
			document.getElementById("check").checked = true;
	}
</script>