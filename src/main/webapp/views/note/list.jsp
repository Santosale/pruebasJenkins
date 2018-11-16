<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

	<spring:url var="requestURI" value="note/managerAuditor/list.do">
		<spring:param name="tripId" value="${tripId }" />
	</spring:url>
	
<display:table class="table table-striped table-bordered table-hover" name="notes" id="row" requestURI="${requestURI }" pagesize="5">

	<security:authorize access="hasRole('MANAGER')">
		<display:column>
		<jstl:if test="${canEditNote==true }">
		<jstl:if test="${row.getManagerReply()==null }">
			<a href="note/manager/edit.do?noteId=${row.getId()}"> <spring:message
					code="note.edit" />
			</a>
			</jstl:if>
			</jstl:if>
		</display:column>
	</security:authorize>

	<spring:message code="note.format.moment" var="momentFormat"/>
	<spring:message code="note.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}" format="{0,date,${momentFormat}}"></display:column>

	<spring:message code="note.remark" var="remarkHeader" />
	<display:column property="remark" title="${remarkHeader}"></display:column>

	<spring:message code="note.managerReply" var="managerReplyHeader" />
	<display:column property="managerReply" title="${managerReplyHeader}"></display:column>

	<spring:message code="note.momentReply" var="momentReplyHeader" />
	<display:column property="momentReply" title="${momentReplyHeader}"
		format="{0,date,${momentFormat}}"></display:column>

	<spring:url var="urlDisplay" value="note/managerAuditor/display.do">
		<spring:param name="noteId" value="${row.getId()}" />
	</spring:url>
	<display:column>
	<a href="${urlDisplay }"> <spring:message code="note.display" /></a>
	</display:column>

</display:table>

<br />

