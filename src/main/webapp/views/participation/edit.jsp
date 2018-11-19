<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="participation/user/edit.do" modelAttribute="participation">

	<form:hidden path="id"/>
	
	<jstl:if test="${participation.getId()==0 }">
		<form:hidden path="groupon"/>
	</jstl:if>
	
	<acme:textbox code="participation.amountProduct" path="amountProduct"/>
	
	
	<acme:submit name="save" code="participation.save" />
		<jstl:if test="${participation.getId() != 0}">
			<acme:submit name="delete" code="participation.delete" />
		</jstl:if>	
	<acme:cancel url="participation/user/list.do" code="participation.cancel"/>

</form:form>