<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div class="container">

	<spring:message code="survivalClass.format.moment" var="momentFormat"/>
	
	<span class="display"><spring:message code="survivalClass.title"/></span><jstl:out value="${survivalClass.getTitle()}"/>
	<br/>
	<span class="display"><spring:message code="survivalClass.description"/></span><jstl:out value="  ${survivalClass.getDescription()}" />
	<br/>
	<span class="display"><spring:message code="survivalClass.moment"/></span><fmt:formatDate value="${survivalClass.getMoment()}" pattern="${momentFormat }"/>
	<br/>
	<span class="display"><spring:message code="survivalClass.location.name"/></span><jstl:out value="  ${survivalClass.getLocation().getName()}" />
	<br/>
	<span class="display"><spring:message code="survivalClass.location.latitude"/></span><jstl:out value="  ${survivalClass.getLocation().getLatitude()}" />
	<br/>
	<span class="display"><spring:message code="survivalClass.location.longitude"/></span><jstl:out value="  ${survivalClass.getLocation().getLongitude()}" />
	<br/>
	
	
	<spring:url var="urlTrip" value="trip/display.do">
		<spring:param name="tripId" value="${survivalClass.getTrip().getId()}" />
	</spring:url>
	
	
	<a href="${urlTrip}" ><spring:message code="survivalClass.trip"/></a>
	

</div>
