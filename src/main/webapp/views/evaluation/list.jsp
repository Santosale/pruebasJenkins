<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="evaluations" id="row" requestURI="${requestURI}">
	
	<jstl:if test="${requestURI == 'evaluation/user/list.do'}">
		<acme:columnLink action="edit" actor="user" domain="evaluation" id="${row.getId()}" />
	</jstl:if>
	<jstl:if test="${requestURI == 'evaluation/moderator/list.do'}">
		<acme:columnLink action="edit" actor="moderator" domain="evaluation" id="${row.getId()}" />
	</jstl:if>
	
	<spring:message code="evaluation.puntuation" var="puntu" />
	<display:column style="background: inherit;" title="${puntu}">
	<jstl:if test="${row.getPuntuation() == 1}">
		<img src="images/1stars.png" alt="One star" width="100" style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	<jstl:if test="${row.getPuntuation() == 2}">
		<img src="images/2stars.png" alt="Two stars" width="100" style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	<jstl:if test="${row.getPuntuation() == 3}">
		<img src="images/3stars.png" alt="Three stars" width="100"  style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	<jstl:if test="${row.getPuntuation() == 4}">
		<img src="images/4stars.png" alt="Four stars" width="100"  style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	<jstl:if test="${row.getPuntuation() == 5}">
		<img src="images/5stars.png" alt="Five stars" width="100" style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	</display:column>
	
	<acme:column property="content" domain="evaluation" />
	
	<spring:message code="evaluation.isAnonymous" var="anon" />
	<spring:message code="evaluation.yes" var="yes" />
	<spring:message code="evaluation.no" var="no" />
	
	<display:column style="background: inherit;" title="${anon}">
	<jstl:if test="${row.getIsAnonymous()}">
		<img src="images/yes.png" alt="${yes}" width="25" style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	<jstl:if test="${!row.getIsAnonymous()}">
		<img src="images/no.png" alt="${no}" width="25" style="display: block; margin-left: auto; margin-right: auto;" />
	</jstl:if>
	</display:column>
	
	<acme:columnLink action="display" domain="evaluation" id="${row.getId()}" />

</display:table>

<acme:paginate url="${requestURI}" objects="${evaluations}" pageNumber="${pageNumber}" page="${page}" />
