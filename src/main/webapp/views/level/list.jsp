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

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="levels" id="row">

<security:authorize access="hasRole('ADMIN')">
	<acme:columnLink action="edit" domain="level" id="${row.getId()}" actor="administrator" />
</security:authorize>	
	<acme:column domain="level" property="name"/>
	
	<acme:column domain="level" property="minPoints"/>
	
	<acme:column domain="level" property="maxPoints"/>
	
	<acme:columnLink action="display" domain="level" id="${row.getId()}"/>
	
	
</display:table> 
	
<acme:paginate pageNumber="${pageNumber}" url="level/list.do" objects="${levels}" page="${page}"/>

<security:authorize access="hasRole('ADMIN')">
	<acme:displayLink code="level.create" action="level/administrator/create.do" css="btn btn-primary"/>		
</security:authorize>	

