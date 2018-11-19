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

<form:form action="article/user/edit.do" modelAttribute="article">

	<form:hidden path="id" />
	
	<jstl:if test="${article.getId() == 0}">
		<form:hidden path="newspaper" />
	</jstl:if>
	
	<acme:textbox code="article.moment" path="moment" readonly="readonly"/>

	<acme:textbox code="article.title" path="title"/>

	<acme:textbox code="article.summary" path="summary"/>
	
	<acme:textbox code="article.body" path="body"/>
	
	<acme:textbox code="article.pictures" path="pictures"/>
	
	<br>
	<form:checkbox class="form-check-input" path="isFinalMode" id="check" value="" checked=''/>
	<form:label path="isFinalMode">
		<div onclick="activar()" ><spring:message code="article.isFinalMode" /></div>
	</form:label>
	<form:errors class="text-danger" path="isFinalMode"/>
	<br>
	
	<jstl:if test="${article.getId() == 0 || !article.getIsFinalMode()}">
		<acme:submit name="save" code="article.save" />
	</jstl:if>
	
	<jstl:if test="${article.getId() != 0 && !article.getIsFinalMode()}">
		<acme:submit name="delete" code="article.delete" codeDelete="article.confirm.delete"/>
	</jstl:if>
	
	<acme:cancel url="article/user/list.do" code="article.cancel"/>

</form:form>

<script type="text/javascript">

	function activar() {
		if  (document.getElementById("check").checked)
			document.getElementById("check").checked = false;
		else if (!document.getElementById("check").checked)
			document.getElementById("check").checked = true;
	}
</script>