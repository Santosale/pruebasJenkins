<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="messages" id="row" requestURI="${requestURI}">

	<jstl:if test="${isChildren == false}">
		<acme:columnLink domain="message" actor="actor" id="${row.getId()}" action="edit" />
		<acme:columnLink domain="message" actor="actor" id="${row.getId()}" action="move" />
	</jstl:if>
	
	<acme:column domain="message" property="moment" formatDate="true"/>
	
	<acme:column domain="message" property="priority" />
	
	<acme:column domain="message" property="subject" />
	
	<acme:column domain="message" property="body" />
	
	<acme:column domain="message" property="folder.name" />
	
	<acme:column domain="message" property="sender.name" />
	
	<acme:column domain="message" property="recipient.name" />
	
	<acme:columnLink domain="message" actor="actor" id="${row.getId()}" action="display" />
		
</display:table> 

<br>

<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${messages}" page="${page}"/>

<br>

<jstl:if test="${isChildren == false}">
	<p><acme:displayLink code="message.create" action="message/actor/create.do" css="btn btn-primary" /></p>
</jstl:if>