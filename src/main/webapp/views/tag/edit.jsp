<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="tag/administrator/edit.do" modelAttribute="tag">

	<form:hidden path="id" />
	<form:hidden path="version" />
	
	<jstl:if test="${tagSomeTrip != true}">
		<div class="form-group"> 
			<form:label path="name">
				<spring:message code="tag.name" />
			</form:label>
			<form:input class="form-control" path="name" />
			<form:errors class="text-danger" path="name" />
		</div>
	</jstl:if>
	
	<jstl:if test="${tagSomeTrip == true}">
		<div class="form-group"> 
			<form:label path="name">
				<spring:message code="tag.name" />
			</form:label>
			<form:input class="form-control" path="name" readOnly="readOnly"/>
			<form:errors class="text-danger" path="name" />
		</div>
	</jstl:if>
	
	<jstl:if test="${tagSomeTrip != true}">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="tag.save" />">
	</jstl:if>
	
	<jstl:if test="${tag.getId() != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="tag.delete" />">
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="tag.cancel" />" onclick="javascript: relativeRedir('tag/administrator/list.do');">

</form:form>