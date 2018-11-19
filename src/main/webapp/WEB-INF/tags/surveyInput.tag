<%--
 * textbox.tag
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@ tag language="java" body-content="empty" %>

<%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%-- Attributes --%> 
 
<%@ attribute name="codeQuestion" required="true" %>
<%@ attribute name="codeAnswer" required="true" %>
<%@ attribute name="object" required="true" type="forms.SurveyForm" %>

<%-- Definition --%>

<style>
label.answer {
    margin-left: 50px;
    margin-top: 15px;
    margin-bottom: 25px;
}
</style>

<spring:message code="${codeQuestion}" var="questionText"/>
<spring:message code="${codeAnswer}" var="answerText"/>
<spring:message code="survey.addAnswer" var="addAnswer" />
<spring:message code="survey.removeAnswer" var="removeAnswer" />
<spring:message code="survey.removeQuestion" var="removeQuestion" />

<div id="questionsContainer">
	<jstl:if test="${object.questions != null && object.questions.size() >= 1}">
		<jstl:forEach items="${object.questions}" var="question" varStatus="loop">
			<br>
			<div>
				<label>${questionText} ${loop.index+1}</label>
				<input type="hidden" name="questions[${loop.index}].id" value="${question.id}">
				<input type="text" name="questions[${loop.index}].text" value="${question.text}">&nbsp;&nbsp;<a class="addAnswer" data-container="${loop.index+1}">${addAnswer}</a> - <a class="removeAnswer">${removeAnswer}</a>
				<br>
				<div class="answersContainer${loop.index+1}" style="display:inline;margin-right:5px;">
					<jstl:forEach items="${question.answers}" var="answer" varStatus="loopAnswer">
						<div style="display:inline;margin-right:5px">
							<label class="answer">${answerText} ${loopAnswer.index+1}</label>
							<input type="text" name="questions[${loop.index}].answers[${loopAnswer.index}].text" value="${answer.text}">
							<input type="hidden" name="questions[${loop.index}].answers[${loopAnswer.index}].id" value="${answer.id}">
						</div>
					</jstl:forEach>
				</div>
			</div>
		</jstl:forEach>
	</jstl:if>
</div>

<button class="btn btn-primary" id="addQuestion"><spring:message code="survey.addQuestion" /></button> 
<button style="display:none" class="btn btn-danger removeQuestion">${removeQuestion}</button>
<br><br>

<script>

var questionText = "${questionText}";
var answerText = "${answerText}";
var addAnswer = "${addAnswer}";
var removeAnswer ="${removeAnswer}";
var removeQuestion ="${removeQuestion}";

if(${object.questions != null && object.questions.size() == 0}) {
	$(document).ready(function() {
		var questionsContainer = $("#questionsContainer");
		var number = questionsContainer.children().length;
		var number2 = questionsContainer.children().length+1;
		$(questionsContainer).append('<div><br><label>'+questionText+ ' ' + number2 +'&nbsp;&nbsp;</label><input type="hidden" name="questions['+number+'].id" value="0"><input type="text" name="questions['+number+'].text">&nbsp;&nbsp;<a class="addAnswer" data-container="1">'+addAnswer+'</a> - <a class="removeAnswer">'+removeAnswer+'</a><br><div class="answersContainer'+number2+'" style="display: inline; margin-right: 5px"><div  style="display: inline; margin-right: 5px"><label class="answer">'+answerText + ' 1&nbsp;&nbsp;</label><input type="text" name="questions['+number+'].answers[0].text" /><input type="hidden" name="questions['+number+'].answers[0].id" value="0" /></div></div></div>');
	});
}

$(document).ready(function() {
	if($("#questionsContainer").children("div").length != 1)
		$(".removeQuestion").fadeIn();
});

$("#addQuestion").click(function(e) {
	e.preventDefault();
	var questionsContainer = $("#questionsContainer");
	var number = questionsContainer.children("div").length+1;
	var number2 = questionsContainer.children("div").length;
	$(questionsContainer).append('<div><br><label>'+questionText+ ' ' + number +'&nbsp;&nbsp;</label><input type="text" name="questions['+number2+'].text">&nbsp;&nbsp;<a class="addAnswer" data-container="'+number+'">'+addAnswer+'</a> - <a class="removeAnswer">'+removeAnswer+'</a><br><div class="answersContainer'+number+'" style="display: inline; margin-right: 5px"><div  style="display: inline; margin-right: 5px"><label class="answer">'+answerText + ' 1&nbsp;&nbsp;</label><input type="text" name="questions['+number2+'].answers[0].text" /><input type="hidden" name="questions['+number2+'].answers[0].id" value="0" /></div></div></div>');
	if(number2 >= 0)
		$(".removeQuestion").fadeIn();
});

$("body").on("click", ".addAnswer", function(e){
	e.preventDefault();

	var numberContainer = $(e.currentTarget).attr("data-container");

	var numberAnswer = $(".answersContainer"+numberContainer).children("div").length;
	var numberAnswer2 = $(".answersContainer"+numberContainer).children("div").length+1;
	var numberQuestion = numberContainer-1;

	$(".answersContainer"+numberContainer).append('<div  style="display: inline; margin-right: 5px"><br><label class="answer">'+answerText + ' ' + numberAnswer2 +'&nbsp;&nbsp;</label><input type="text" name="questions['+numberQuestion+'].answers['+ numberAnswer +'].text" /><input type="hidden" name="questions['+numberQuestion+'].answers['+ numberAnswer +'].id" value="0" /></div>');
});

$("body").on("click", ".removeAnswer", function(e){
	e.preventDefault();
	if($(e.currentTarget.parentNode).children("div").children().length > 1) {
		$(e.currentTarget.parentNode).children("div").children(":last-child").remove();
	}
});

$("body").on("click", ".removeQuestion", function(e){
	e.preventDefault();
	if($("#questionsContainer").children("div").length != 1)
		$("#questionsContainer").children("div").last().remove();
	if($("#questionsContainer").children("div").length == 1)
		$(".removeQuestion").fadeOut();
});

</script>