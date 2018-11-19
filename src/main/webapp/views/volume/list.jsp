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


<display:table class="table table-striped table-bordered table-hover" name="volumes" id="row">
	
<jstl:if test="${requestURI.equals('volume/user/list.do')}">
	
	<security:authorize access="hasRole('USER')">
		<display:column>
			<a href="volume/user/edit.do?volumeId=${row.getId()}"> <spring:message
					code="volume.edit" />
			</a>
		
		</display:column>
		
				
	</security:authorize>	
</jstl:if>

	
			
	<acme:column property="title" domain="volume" />
	
	<acme:column property="description" domain="volume" />
	
	<acme:column property="year" domain="volume" />
	
	
			
	<spring:url var="urlDisplay" value="volume/display.do">
		<spring:param name="volumeId" value="${row.getId()}" />
		
	</spring:url>
	
	<display:column>
			<a href="${urlDisplay }"> <spring:message code="volume.display" /></a>
	</display:column>
		
</display:table>

<jstl:if test="${requestURI.equals('volume/user/list.do') || requestURI.equals('volume/list.do') }">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${volumes}" page="${page}"/>
</jstl:if>




<security:authorize access="hasRole('USER')">
<jstl:if test="${requestURI.equals('volume/user/list.do')}">
	<div>
	<br>
		<a href="newspaper/user/listPublished.do">
			<spring:message code="volume.create" />
		</a>
	</div>
</jstl:if>	
</security:authorize>