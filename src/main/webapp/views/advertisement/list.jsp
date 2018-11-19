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
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>



<display:table class="table table-striped table-bordered table-hover" name="advertisements" id="row">
	
	
	<!-- Para editar Advertisement -->
	<security:authorize access="hasRole('AGENT')">
	
		<security:authentication var="principal" property="principal.username"/>
		<jstl:if test="${row.getAgent().getUserAccount().getUsername().equals(principal)}">
			
			<jstl:if test="${action=='agent-edit'}">
				<acme:columnLink id="${row.getId()}" domain="advertisement" actor="agent" action="edit"/>
			</jstl:if>
			
			<jstl:if test="${action=='agent-link'}">
				<acme:columnLink id="${row.getId()}" domain="advertisement" id2="${newspaperId}" domain2="newspaper" actor="agent" action="link"/>
			</jstl:if>
			
			<jstl:if test="${action=='agent-unlink'}">
				<acme:columnLink id="${row.getId()}" domain="advertisement" id2="${newspaperId}" domain2="newspaper" actor="agent" action="unlink"/>
			</jstl:if>
		
		</jstl:if>	
		
	</security:authorize>	
		
	
	
	<security:authorize access="hasRole('AGENT')">
		<acme:column property="title" domain="advertisement" />
		<acme:column property="creditCard.number" domain="advertisement" />
		<acme:columnLink id="${row.getId()}" domain="advertisement" actor="agentAdministrator" action="display"/>
	</security:authorize>
	
	
	<security:authorize access="hasRole('ADMIN')">
		<jstl:if test="${row.getHasTaboo()}">
			<acme:columnLink style="background:red; color:white" id="${row.getId()}" domain="advertisement" actor="administrator" action="delete"/>
			<acme:column style="background:red; color:white" property="title" domain="advertisement" />
			<acme:columnLink style="background:red; color:white" id="${row.getId()}" domain="advertisement" actor="agentAdministrator" action="display"/>
		</jstl:if>
		
		<jstl:if test="${!row.getHasTaboo()}">
			<acme:columnLink id="${row.getId()}" domain="advertisement" actor="administrator" action="delete"/>
			<acme:column property="title" domain="advertisement" />
			<acme:columnLink id="${row.getId()}" domain="advertisement" actor="agentAdministrator" action="display"/>
		</jstl:if>
	</security:authorize>
	
	
	
	

</display:table>


	
<jstl:if test="${!action.equals('agent-link') && !action.equals('agent-unlink')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${advertisements}" page="${page}"/>
</jstl:if>
	

<jstl:if test="${action.equals('agent-link') || action.equals('agent-unlink')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${advertisements}" page="${page}" parameter="newspaperId" parameterValue="${newspaperId}"/>
</jstl:if>

<security:authorize access="hasRole('AGENT')">
	<br>
	<br>
	<jstl:if test="${action.equals('agent-edit')}">
		<acme:displayLink code="advertisement.create" action="advertisement/agent/create.do" css="btn btn-primary"></acme:displayLink>
	</jstl:if>
</security:authorize>