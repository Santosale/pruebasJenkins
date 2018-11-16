<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="socialidentities" id="row" requestURI="socialidentity/list.do" pagesize="5">

	<spring:url value="socialidentity/edit.do" var="urlEdit">
		<spring:param name="socialIdentityId" value="${row.getId()}" />
	</spring:url>
	
	<display:column>
		<a href="${urlEdit}"><spring:message code="socialidentity.edit"/></a>
	</display:column>

	<spring:message code="socialidentity.nick" var="nickHeader" />
	<display:column property="nick" title="${nickHeader}" />
	
	<spring:message code="socialidentity.socialNetwork" var="socialNetworkHeader" />
	<display:column property="socialNetwork" title="${socialNetworkHeader}" />
	
	<spring:message code="socialidentity.link" var="linkHeader" />
	<display:column property="link" title="${linkHeader}" />
	
	<spring:message code="socialidentity.photo" var="photoHeader" />
	<display:column title="${photoHeader}">
		<img src="${row.getPhoto()}"/>
	</display:column>
	
</display:table> 
	
<br />

<a href="socialidentity/create.do"><spring:message code="socialidentity.create" /></a>