<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<display:table class="table table-striped table-bordered table-hover" name="actors" id="row" requestURI="actor/administrator/list.do" pagesize="5"  >
	
	<display:column>
		
			<!-- Ponemos el enlace en funcion de la accion -->
			<jstl:if test="${row.getUserAccount().isEnabled()}">
				<spring:url var="urlBanUnban" value="suspicious/administrator/banUnban.do">
					<spring:param name="ban" value="true" />
					<spring:param name="actorId" value="${row.getId()}" />
				</spring:url>
				<a href="${urlBanUnban }"> <spring:message code="actor.ban" /></a>
			</jstl:if>
			
			<jstl:if test="${!row.getUserAccount().isEnabled()}">
				<spring:url var="urlBanUnban" value="suspicious/administrator/banUnban.do">
					<spring:param name="ban" value="false" />
					<spring:param name="actorId" value="${row.getId()}" />
				</spring:url>
				<a href="${urlBanUnban }"> <spring:message code="actor.unban" /></a>
			</jstl:if>
	
	</display:column>
	
	
	<spring:message code="actor.name" var="nameHeader" />
	<display:column property="name" title="${nameHeader}" ></display:column>
	
	<spring:message code="actor.surname" var="surnameHeader" />
	<display:column property="surname" title="${surnameHeader}" ></display:column>
	
	<spring:message code="actor.email" var="emailHeader" />
	<display:column property="email" title="${emailHeader}" ></display:column>
	
	<spring:message code="actor.phone" var="phoneHeader" />
	<display:column property="phone" title="${phoneHeader}" ></display:column>
	
	<spring:message code="actor.role" var="authorityHeader" />
	<display:column property="userAccount.authorities" title="${authorityHeader}" ></display:column>
	
</display:table>


