<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<div class="container">

	<p><acme:display code="folder.name" value="${folder.getName()}"/></p>
	
	<jstl:if test="${folder.getSystem() == true}">
		<p><spring:message code="folder.isSystem" /></p>
		
	</jstl:if>
		
	<jstl:if test="${folder.getFatherFolder()!=null }">
		<p><acme:displayLink code="folder.fatherFolder" action="folder/actor/display.do" parametre="folderId" parametreValue="${folder.getFatherFolder().getId()}" /></p>
	</jstl:if>

</div>

<jstl:if test="${anyFolder==false}">

	<br>
	
	<h2><spring:message code="folder.childrenFolders" /></h2>
	
	<display:table class="table table-striped table-bordered table-hover" name="folders" id="row" requestURI="${requestURIf}">

		<display:column>
			<jstl:if test="${row.getSystem()==false }">
				<jstl:if test="${isChildren==false }">
					<acme:displayLink code="folder.edit" action="folder/actor/edit.do" parametre="folderId" parametreValue="${row.getId()}" />
				</jstl:if>
			</jstl:if>
		</display:column>

		<acme:column domain="folder" property="name"/>

		<acme:columnBoolean domain="folder" property="system" />

		<acme:columnLink domain="folder" actor="actor" action="display" id="${row.getId()}" />

	</display:table>
	
	<jstl:forEach var="i" begin="1" end="${pageNumberFolders}">

		<spring:url var="urlNextPage" value="folder/actor/display.do">
			<spring:param name="folderId" value="${folder.getId()}" />
			<spring:param name="pageFolders" value="${i}" />
			<spring:param name="pageMessages" value="${pageMessages}" />
		</spring:url>
			
		<jstl:if test="${pageFolders==i}">
			<span  style='margin-right:10px;'><a href="${urlNextPage}" class='btn btn-danger'><jstl:out value="${i}"></jstl:out></a></span>
		</jstl:if>
		<jstl:if test="${pageFolders!=i}">
			<span  style='margin-right:10px;'><a href="${urlNextPage}" class='btn btn-primary'><jstl:out value="${i}"></jstl:out></a></span>
		</jstl:if>
		
	</jstl:forEach>

	<br><br>
	
	<jstl:if test="${isChildren==false }">
		<acme:displayLink css="btn btn-primary" code="folder.create" action="folder/actor/create.do" />
	</jstl:if>

</jstl:if>

<jstl:if test="${anyMessage==false}">

	<br>
	
	<h2> <spring:message code="folder.messages" /> </h2>
	
	<display:table class="table table-striped table-bordered table-hover" name="messages" id="row" requestURI="${requestURIm}">

		<jstl:if test="${isChildren==false }">

			<acme:columnLink domain="message" actor="actor" action="edit" id="${row.getId()}"/>

			<acme:columnLink domain="message" actor="actor" action="move" id="${row.getId()}" />
			
		</jstl:if>

		<acme:column domain="message" property="moment" formatDate="true"/>

		<acme:column domain="message" property="priority"/>
		
		<acme:column domain="message" property="subject"/>
		
		<acme:column domain="message" property="body"/>
		
		<acme:column domain="message" property="folder.name"/>
		
		<acme:column domain="message" property="sender.name"/>
		
		<acme:column domain="message" property="recipient.name"/>
		
		<acme:columnLink domain="message" actor="actor" action="display" id="${row.getId()}"/>

	</display:table>
	
	<jstl:forEach var="m" begin="1" end="${pageNumberMessages}">

		<spring:url var="urlNextPagem" value="folder/actor/display.do">
			<spring:param name="folderId" value="${folder.getId()}" />
			<spring:param name="pageMessages" value="${m}" />
			<spring:param name="pageFolders" value="${pageFolders}" />
		</spring:url>
			
		<jstl:if test="${pageMessages==m}">
			<span  style='margin-right:10px;'><a href="${urlNextPagem}" class='btn btn-danger'><jstl:out value="${m}"></jstl:out></a></span>
		</jstl:if>
		<jstl:if test="${pageMessages!=m}">
			<span  style='margin-right:10px;'><a href="${urlNextPagem}" class='btn btn-primary'><jstl:out value="${m}"></jstl:out></a></span>
		</jstl:if>
		
	</jstl:forEach>

</jstl:if>



