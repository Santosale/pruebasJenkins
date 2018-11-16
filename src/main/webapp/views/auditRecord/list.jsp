<%--
 * list.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

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

<spring:url var="urlURI" value="audit/auditor/list.do" />
<display:table class="table table-striped table-bordered table-hover" name="audits" id="row" requestURI="${urlURI }"
	pagesize="5" >



	<security:authorize access="hasRole('AUDITOR')">

		<display:column>
		
			<jstl:if test="${row.isDraft() && actor.equals(row.getAuditor().getUserAccount())}">

				<a href="auditRecord/auditor/edit.do?auditId=${row.getId()}"> <spring:message
						code="audit.edit" />
				</a>
			</jstl:if>

		</display:column>
	</security:authorize>

	<!-- 	private Collection<String>	attachments;
 -->
	<spring:message code="audit.format.moment" var="momentFormat" />
	<spring:message code="audit.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}"
		format="{0,date,${momentFormat}}">
	</display:column>

	<spring:message code="audit.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}"></display:column>

	<spring:message code="audit.description" var="descriptionHeader" />
	<display:column property="description" title="${descriptionHeader}"></display:column>

	<spring:url var="urlDisplay" value="auditRecord/display.do">
		<spring:param name="auditId" value="${row.getId()}" />
	</spring:url>
	<display:column>
		<a href="${urlDisplay }"> <spring:message code="audit.display" /></a>
	</display:column>

</display:table>

<br />
<%-- <security:authorize access="hasRole('AUDITOR')">
	<div>
		<a href="auditRecord/auditor/create.do"> <spring:message
				code="audit.create" />
		</a>
	</div>
</security:authorize> --%>