<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div  class="container">

	<spring:message code="audit.format.moment" var="momentFormat"/>
	
	<span class="display"><spring:message code="audit.title"/></span><jstl:out value="  ${audit.getTitle()}" />
	<br/>
	<span class="display"><spring:message code="audit.description"/></span><jstl:out value="  ${audit.getDescription()}" />
	<br/>
	<span class="display"><spring:message code="audit.attachments"/></span><jstl:out value="  ${audit.getAttachments()}" />
	<br/>
	<span class="display"><spring:message code="audit.draft"/></span><jstl:out value="  ${audit.isDraft()}" />
	<br/>
	<span class="display"><spring:message code="audit.moment"/></span><fmt:formatDate value="${audit.getMoment()}" pattern="${momentFormat }"/>
	<br/>
	
	
	
	<spring:url var="urlAuditor" value="actor/auditor/display.do">
		<spring:param name="auditorId" value="${audit.getAuditor().getId()}" />
	</spring:url>

	<spring:url var="urlTrip" value="trip/display.do">
		<spring:param name="tripId" value="${audit.getTrip().getId()}" />
	</spring:url>
	
	
	<a href="${urlTrip}" ><spring:message code="audit.trip"/></a>
	<br/>
	<a href="${urlAuditor}" ><spring:message code="audit.auditor"/></a>
	

</div>

	

