<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div class="container">

	
	<span class="display"><spring:message code="stage.title"/></span><jstl:out value="${stage.getTitle()}" />
	<br/>
	<span class="display"><spring:message code="stage.description"/></span><jstl:out value="${stage.getDescription()}" />
	<br/>
	<span class="display"><spring:message code="stage.price"/></span><jstl:out value="${stage.getPrice()}" />
	<br/>
	<span class="display"><spring:message code="stage.numStage"/></span><jstl:out value="${stage.getNumStage()}" />
	<br/>
	
	<spring:url var="urlTrip" value="trip/display.do">
		<spring:param name="tripId" value="${stage.getTrip().getId()}" />
	</spring:url>
	
	
	<a href="${urlTrip}" ><spring:message code="stage.trip"/></a>
	<br/>
	

</div>

	

