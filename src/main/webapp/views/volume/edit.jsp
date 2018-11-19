<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="volume/user/edit.do" modelAttribute="volume">

	<form:hidden path="id" />
	<jstl:if test="${volume.getId()==0 }">
		<form:hidden path="user" />
		<form:hidden path="newspapers" />
	</jstl:if>
	
	
		
	<acme:textbox code="volume.title" path="title"/>

	<acme:textbox code="volume.description" path="description"/>
	
	<acme:textbox path="year" code="volume.year"/>
	
		 
	<acme:submit name="save" code="volume.save"/>
	
	<acme:cancel url="volume/user/list.do" code="volume.cancel"/>
			
</form:form>