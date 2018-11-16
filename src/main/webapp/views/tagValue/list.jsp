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

<display:table class="table table-striped table-bordered table-hover" name="tagValues" id="row" requestURI="${requestURI }" pagesize="5">



	<spring:url value="tagValue/manager/edit.do" var="urlEdit">
		<spring:param name="tagValueId" value="${row.getId()}" />
	</spring:url>
	
	
	<display:column>
	<jstl:if test="${canEditOrCreate==true }">
		<a href="${urlEdit}"><spring:message code="tagValue.edit"/></a>
		</jstl:if>
	</display:column>
	
		
	<spring:message code="tagValue.value" var="valueHeader" />
	<display:column property="value" title="${valueHeader}" />
	
	<spring:message code="tagValue.tag" var="tagHeader" />
	<display:column property="tag.name" title="${tagHeader}" />
	
		
</display:table> 
	
<br />
	<jstl:if test="${canEditOrCreate==true }">

<a href="tagValue/manager/create.do?tripId=${tripId}"><spring:message code="tagValue.create" /></a>
</jstl:if>


