<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="folder/actor/edit.do" modelAttribute="folder">

	<form:hidden path="id" />
		
	<acme:textbox code="folder.name" path="name"/>
	
	<acme:select items="${folders}" itemLabel="name" code="folder.fatherFolder" path="fatherFolder"/>

	<jstl:if test="${canCreateAndEdit}">
		<acme:submit name="save" code="folder.save"/>
	</jstl:if>
	
	<jstl:if test="${folder.getId() != 0}">
		<jstl:if test="${canCreateAndEdit}">
				<acme:submit name="delete" code="folder.delete" />
		</jstl:if>
 	</jstl:if>
		
	<acme:cancel url="folder/actor/list.do" code="folder.cancel"/>
	
</form:form>