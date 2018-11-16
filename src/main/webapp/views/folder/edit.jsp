<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<form:form action="folder/edit.do" modelAttribute="folder">

	<form:hidden path="id" />
	<form:hidden path="version" />
	<form:hidden path="actor" />
	<form:hidden path="system" />
	<form:hidden path="childrenFolders" />
	
	<jstl:if test="${folder.getId()!=0 }">
		<form:hidden path="fatherFolder" />
	</jstl:if>	
		
	<div class="form-group"> 
		<form:label path="name">
			<spring:message code="folder.name" />
		</form:label>
		<form:input class="form-control" path="name"/>
		<form:errors class="text-danger" path="name" />
	</div>
	
	<jstl:if test="${folder.getId() == 0}">
		<div class="form-group"> 
			<form:label path="fatherFolder">
				<spring:message code="folder.fatherFolder" />:
			</form:label>
			<form:select class="form-control" path="fatherFolder">
				<form:option value="0" label="----" />		
				<form:options items="${folders}" itemValue="id"
					itemLabel="name" />
			</form:select>
			<form:errors class="text-danger" path="fatherFolder" />
		</div>
	</jstl:if>
	
	<jstl:if test="${canCreateAndEdit==true }">
		<input type="submit" class="btn btn-primary" name="save" value="<spring:message code="folder.save" />">
	</jstl:if>
	
	<jstl:if test="${folder.getId() != 0}">
		<jstl:if test="${canCreateAndEdit==true }">
				<input type="submit" class="btn btn-warning" name="delete" value="<spring:message code="folder.delete" />">
		</jstl:if>
 	</jstl:if>
	
	<input type="button" class="btn btn-danger" name="cancel" value="<spring:message code="folder.cancel" />" onclick="javascript: relativeRedir('folder/list.do');">
	
</form:form>