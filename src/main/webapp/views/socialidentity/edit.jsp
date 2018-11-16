<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="socialidentity/edit.do" modelAttribute="socialIdentity">

	<jstl:if test="${socialIdentity.getId() == 0}">
		<h2><spring:message code="socialidentity.create" /></h2>
 	</jstl:if>
 	
	<jstl:if test="${socialIdentity.getId() != 0}">
		<h2><spring:message code="socialidentity.edit" /></h2>
 	</jstl:if>
 	
	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="actor" />
	
	<div class="form-group"> 
		<form:label path="nick">
			<spring:message code="socialidentity.nick" />:
		</form:label>
		<form:input class="form-control" path="nick" />
		<form:errors class="text-danger" path="nick"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="socialNetwork">
			<spring:message code="socialidentity.socialNetwork" />:
		</form:label>
		<form:input class="form-control" path="socialNetwork" />
		<form:errors class="text-danger" path="socialNetwork"/>
	</div>

	<div class="form-group"> 
		<form:label path="link">
			<spring:message code="socialidentity.link" />:
		</form:label>
		<form:input class="form-control" path="link" />
		<form:errors class="text-danger" path="link"/>
	</div>
	
	<div class="form-group"> 
		<form:label path="photo">
			<spring:message code="socialidentity.photo" />:
		</form:label>
		<form:input class="form-control" path="photo" />
		<form:errors class="text-danger" path="photo"/>
	</div>
	
	<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="socialidentity.save" />"> 

	<jstl:if test="${socialIdentity.getId() != 0}">
		<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="socialidentity.delete" />">
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="socialidentity.cancel" />" onclick="javascript: relativeRedir('socialidentity/list.do')">
		
</form:form>