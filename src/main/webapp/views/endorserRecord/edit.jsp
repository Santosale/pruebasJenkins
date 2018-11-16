<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="endorserRecord/ranger/edit.do" modelAttribute="endorserRecord">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="curriculum" />
			
	<div class="form-group"> 
		<form:label path="fullName">
			<spring:message code="endorserRecord.fullName" />
		</form:label>
		<form:input class="form-control" path="fullName"/>
		<form:errors class="text-danger" path="fullName" />
	</div>
	
	<div class="form-group"> 
		<form:label path="email">
			<spring:message code="endorserRecord.email" />
		</form:label>
		<form:input class="form-control" path="email"/>
		<form:errors class="text-danger" path="email" />
	</div>
	
	<div class="form-group"> 
		<form:label path="phone">
			<spring:message code="endorserRecord.phone" />
		</form:label>
		<form:input class="form-control" path="phone"/>
		<form:errors class="text-danger" path="phone" />
	</div>
	
	<div class="form-group"> 
		<form:label path="linkLinkedin">
			<spring:message code="endorserRecord.linkLinkedin" />
		</form:label>
		<form:input class="form-control" path="linkLinkedin"/>
		<form:errors class="text-danger" path="linkLinkedin" />
	</div>
	
	<div class="form-group"> 
		<form:label path="comments">
			<spring:message code="endorserRecord.comments" />
		</form:label>
		<form:textarea path="comments"/>
		<form:errors class="text-danger" path="comments" />
	</div>
	
	<jstl:if test="${isHisEndorserRecord==true }">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="endorserRecord.save" />"> 
	</jstl:if>
	
	<jstl:if test="${endorserRecord.getId() != 0}">
		<jstl:if test="${isHisEndorserRecord==true }">
			<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="endorserRecord.delete" />">		
		</jstl:if>
 	</jstl:if>
	
	<input type="button" name="cancel" class="btn btn-danger" value="<spring:message code="endorserRecord.cancel" />" onclick="javascript: relativeRedir('curriculum/display.do?curriculumId=${endorserRecord.getCurriculum().getId()}');">
	
</form:form>