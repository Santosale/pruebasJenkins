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

<form:form action="survey/${model}/answer.do" modelAttribute="answerSurveyForm">	
		
	<jstl:forEach items="${answerSurveyForm.mapQuestionAnswers}" var="mapEntry">

	<h2><jstl:out value="${mapEntry.key.text}" /></h2>
		
	<acme:select code="survey.answers" path="answers" items="${mapEntry.value}" itemLabel="text" hideFirstOption="true"/> 
	
	</jstl:forEach>
	
	<p id="cannotBeEmpty" style="color: red; display:none"><spring:message code="survey.answerCannotBeEmpty" /></p>
	
	<script>
		$("form").submit(function(e) {
			$("select").each(function(item) { 
				if(this.value == "") {
					$("#cannotBeEmpty").fadeIn();
					e.preventDefault();
				}
			});
		});
	</script>
	
	<acme:submit name="save" code="survey.save" />

	<acme:cancel url="/notification/actor/list.do" code="survey.cancel"/>

</form:form>