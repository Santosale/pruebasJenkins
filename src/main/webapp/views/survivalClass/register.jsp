

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



<display:table class="table table-striped table-bordered table-hover" name="survivalClasses" id="row"
	requestURI="${requestURI}" pagesize="5" >

	<jstl:if test="${registered.equals(false)}">
		<jstl:if test="${canRegister.equals(true)}">
			<display:column>
				<a
					href="survivalClass/explorer/registerEdit.do?survivalClassId=${row.getId()}">
					<spring:message code="survivalClass.register" />
				</a>
			</display:column>
		</jstl:if>
	</jstl:if>

	<jstl:if test="${registered.equals(true)}">
		<jstl:if test="${canRegister.equals(true)}">

			<display:column>
				<a
					href="survivalClass/explorer/unregisterEdit.do?survivalClassId=${row.getId()}">
					<spring:message code="survivalClass.unregister" />
				</a>
			</display:column>
		</jstl:if>
	</jstl:if>

	<spring:message code="survivalClass.title" var="titleHeader" />
	<display:column property="title" title="${titleHeader}"></display:column>

	<spring:message code="survivalClass.description"
		var="descriptionHeader" />
	<display:column property="description" title="${descriptionHeader}"></display:column>

	<spring:message code="survivalClass.format.moment" var="momentFormat" />
	<spring:message code="survivalClass.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}"
		format="{0,date,${momentFormat }}"></display:column>

	<spring:message code="survivalClass.location.name"
		var="locationNameHeader" />
	<display:column property="location.name" title="${locationNameHeader}"></display:column>

	<spring:message code="survivalClass.location.latitude"
		var="locationLatitudeHeader" />
	<display:column property="location.latitude"
		title="${locationLatitudeHeader}"></display:column>

	<spring:message code="survivalClass.location.longitude"
		var="locationLongitudeHeader" />
	<display:column property="location.longitude"
		title="${locationLongitudeHeader}"></display:column>


</display:table>
