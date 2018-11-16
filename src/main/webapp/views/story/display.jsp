<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div class="container">

	
	<span class="display"><spring:message code="story.title"/></span><jstl:out value="  ${story.getTitle()}" />
	<br/>
	<span class="display"><spring:message code="story.text"/></span><jstl:out value="  ${story.getText()}" />
	<br/>
	<span class="display"><spring:message code="story.attachments"/></span><jstl:out value="  ${story.getAttachments()}" />
	<br/>
	
	<spring:url var="urlExplorer" value="explorer/display.do">
		<spring:param name="explorerId" value="${story.getWriter().getId()}" />
	</spring:url>
	
	<spring:url var="urlTrip" value="trip/display.do">
		<spring:param name="tripId" value="${story.getTrip().getId()}" />
	</spring:url>
	
	
	<a href="${urlTrip}" ><spring:message code="story.trip"/></a>
	<br/>
	

</div>

	

