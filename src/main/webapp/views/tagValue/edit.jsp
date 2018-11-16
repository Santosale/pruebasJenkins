<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="tagValue/manager/edit.do" modelAttribute="tagValue">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="trip" />
	
	<jstl:if test="${tagValue.getId()>0 }">
		<form:hidden path="tag" />
	</jstl:if>
	
	<div class="form-group"> 
		<form:label path="value">
			<spring:message code="tagValue.value" />
		</form:label>
		<form:input class="form-control" path="value" />
		<form:errors class="text-danger" path="value" />
	</div>
	
	<jstl:if test="${tagValue.getId()==0 }">
		<div class="form-group"> 
			<form:label path="tag">
				<spring:message code="tagValue.tag" />:
			</form:label>
			<form:select class="form-control" path="tag">
			 	<form:option value="0" label="----" />	
				<form:options items="${tags}" itemValue="id"
					itemLabel="name" />
			</form:select>
			<form:errors class="text-danger" path="tag" />
		</div>
	</jstl:if>
	
	<jstl:if test="${tagValue.getId()==0}">
		<jstl:if test="${canEdit==true}">
			<jstl:if test="${tags.size()>0}">
				<input type="submit" name="save" class="btn btn-primary" value="<spring:message code="tagValue.save" />">
			</jstl:if>
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${tagValue.getId()>0}">
		<jstl:if test="${canEdit==true}">
			<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="tagValue.save" />">
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${tagValue.getId()!= 0}">
		<jstl:if test="${canEdit==true}">
			<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="tagValue.delete" />">
			</jstl:if>
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="tagValue.cancel" />" onclick="javascript: relativeRedir('tagValue/list.do?tripId=${tagValue.getTrip().getId()}');">
			
</form:form>