<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="contacts" id="row" requestURI="contact/explorer/list.do" pagesize="5">

	<spring:url value="contact/explorer/edit.do" var="urlEdit">
		<spring:param name="contactId" value="${row.getId()}" />
	</spring:url>
	
	<display:column>
		<a href="${urlEdit}"><spring:message code="educationrecord.edit"/></a>
	</display:column>

	<spring:message code="contact.name" var="nameHeader" />
	<display:column property="name" title="${nameHeader}" />
	
	<spring:message code="contact.email" var="emailHeader" />
	<display:column property="email" title="${emailHeader}" />
	
	<spring:message code="contact.phone" var="phoneHeader" />
	<display:column property="phone" title="${phoneHeader}" />
	
</display:table>
	
<br />

<a href="contact/explorer/create.do"><spring:message code="contact.create" /></a>