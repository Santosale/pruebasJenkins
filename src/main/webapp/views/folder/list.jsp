<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="folders" id="row" requestURI="${requestURI }" pagesize="5">

	<spring:url value="folder/edit.do" var="urlEdit">
		<spring:param name="folderId" value="${row.getId()}" />
	</spring:url>
	
	
	<display:column>
	<jstl:if test="${row.getSystem()==false }">
		<jstl:if test="${isChildren==false }">
		<a href="${urlEdit}"><spring:message code="folder.edit"/></a>
		</jstl:if>
		</jstl:if>
	</display:column>
	
	
	<spring:message code="folder.name" var="nameHeader" />
	<display:column property="name" title="${nameHeader}" />
	
	<spring:message code="folder.system" var="systemHeader" />
	<display:column property="system" title="${systemHeader}" />
 
	<spring:url value="folder/display.do" var="urlDisplay">
		<spring:param name="folderId" value="${row.getId()}" />
	</spring:url>
	
	

	<display:column>
		<a href="${urlDisplay }"><spring:message code="folder.display"/></a>
	</display:column>
	
	
		
		
</display:table> 
	
<br />
	<jstl:if test="${isChildren==false }">

<a href="folder/create.do"><spring:message code="folder.create" /></a>

		</jstl:if>
