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

<form:form action="survey/${model}/edit.do" id="surveyForm" modelAttribute="surveyForm">

	<form:hidden path="id"/>

	<acme:textbox code="survey.title" path="title" />
	
	<jstl:if test="${surveyForm.getId() == 0}">

		<br>
		<h4><spring:message code="survey.chooseActor" /></h4>
		<form:radiobutton path="toActor" value="USER" /><spring:message code="survey.users" />
		<jstl:if test="${model.equals('company')}">
			&nbsp;&nbsp;&nbsp;<form:radiobutton path="toActor" value="SPONSOR" /><spring:message code="survey.sponsors" />
		</jstl:if>
		<form:errors path="toActor" class="text-danger" />
		
		<br>
		
		<jstl:if test="${model.equals('company')}">
			<acme:checkbox id="hasAds" code="survey.hasAds" path="hasAds" style="display:none"/>
		</jstl:if>
		
		<acme:textbox code="survey.minimumPoints" path="minimumPoints" style="display: none"/>
		
		<script>
			$(document).ready(function() {
				
				if(${surveyForm.getToActor() != null && surveyForm.getToActor().equals("USER")}) {
					$("input[name='minimumPoints']").parent().fadeIn();
				} else if(${surveyForm.getToActor() != null && surveyForm.getToActor().equals("SPONSOR")}) {
					$("#hasAds").parent().fadeIn();
				}
				$("input[type=radio][name='toActor']").change(function() {
					if(this.value == "USER") {
						$("input[name='minimumPoints']").parent().fadeIn();
						if(${model.equals('company')}) {
							$("#hasAds").parent().fadeOut();
						}
					} else {
						$("input[name='minimumPoints']").parent().fadeOut();
						if(${model.equals('company')}) {
							$("#hasAds").parent().fadeIn();
						}
					}
				});
			});
		</script>
		
		<br>
		
	</jstl:if>

	<jstl:if test="${surveyForm.getId() != 0}">
		<form:hidden path="toActor" />
		<acme:textbox code="survey.minimumPoints" path="minimumPoints" style="display: none"/>
		<jstl:if test="${model.equals('company')}">
			<acme:checkbox id="hasAds" code="survey.hasAds" path="hasAds" style="display:none"/>
		</jstl:if>
		<input id="toActor1" name="toActor" type="radio" checked="checked" value="EDIT" style="display:none">
	</jstl:if>
	
	<acme:surveyInput codeQuestion="survey.question" codeAnswer="survey.answer" object="${surveyForm}" />
	
	<script>
		$("#surveyForm").submit(function(e) {
			$("input[name^='question']").each(function(i) {
				if(this.value == "") {
					$("#cannotBeEmpty").fadeIn();
					e.preventDefault();
				}
			});
		});
	</script>
		
	<p id="cannotBeEmpty" style="color: red; display:none"><spring:message code="survey.cannotBeEmpty" /></p>
	
	<acme:submit name="save" code="survey.save" />

	<jstl:if test="${surveyForm.getId() != 0}">
		<acme:submit name="delete" code="survey.delete" />
	</jstl:if>
	
	<acme:cancel url="survey/${model}/list.do" code="survey.cancel"/>

</form:form>