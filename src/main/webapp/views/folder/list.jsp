<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="folders" id="row" requestURI="${requestURI}">

	
	<display:column>
		<jstl:if test="${row.getSystem()==false}">
			<jstl:if test="${isChildren==false}">
				<acme:displayLink code="folder.edit" action="folder/actor/edit.do" parametre="folderId" parametreValue="${row.getId()}"></acme:displayLink>
			</jstl:if>
		</jstl:if>
		<jstl:if test="${row.getSystem()==true}">
			<spring:message code="folder.cannotBeEdited" />
		</jstl:if>
	</display:column>
	
	<acme:column domain="folder" property="name"/>
	
	<acme:columnBoolean domain="folder" property="system" row="${row}"/>
	
	<acme:columnLink domain="folder" actor="actor" action="display" id="${row.getId()}"/>
		
</display:table> 

<br>

<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${folders}" page="${page}"/>

<br>
	
<jstl:if test="${isChildren == false}">
	<p><acme:displayLink code="folder.create" action="folder/actor/create.do" css="btn btn-primary" /></p>
</jstl:if>