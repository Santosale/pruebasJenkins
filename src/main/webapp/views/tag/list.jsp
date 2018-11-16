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

<display:table class="table table-striped table-bordered table-hover" name="tags" id="row" requestURI="tag/administrator/list.do" pagesize="5">



	<spring:url value="tag/administrator/edit.do" var="urlEdit">
		<spring:param name="tagId" value="${row.getId()}" />
	</spring:url>
	
	
	<display:column>
		<a href="${urlEdit}"><spring:message code="tag.edit"/></a>
	</display:column>
	
		
	<spring:message code="tag.name" var="nameHeader" />
	<display:column property="name" title="${nameHeader}" />
	
		
</display:table> 
	
<br />

<a href="tag/administrator/create.do"><spring:message code="tag.create" /></a>

