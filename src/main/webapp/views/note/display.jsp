<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div class="container">

	<spring:message code="note.format.moment" var="momentFormat"/>
	
	<span class="display"><spring:message code="note.moment"/></span> <fmt:formatDate value="${note.getMoment()}" pattern="${momentFormat }"/>
	<br/>
	<span class="display"><spring:message code="note.remark"/></span> <jstl:out value="  ${note.getRemark()}" />
	<br/>
	<span class="display"><spring:message code="note.managerReply"/></span> <jstl:out value="  ${note.getManagerReply()}" />
	<br/>
	<span class="display"><spring:message code="note.momentReply"/></span>
	<jstl:if test="${note.getMomentReply()==null }">
	<jstl:out value="  ${note.getMomentReply()}" />
	</jstl:if>
	<jstl:if test="${note.getMomentReply()!=null }">
	<fmt:formatDate value="${note.getMomentReply()}" pattern="${momentFormat }"/>
	</jstl:if>
	<br/>
	
	<spring:url var="urlAuditor" value="actor/auditor/display.do">
		<spring:param name="auditorId" value="${note.getAuditor().getId()}" />
	</spring:url>

	<spring:url var="urlTrip" value="trip/display.do">
		<spring:param name="tripId" value="${note.getTrip().getId()}" />
	</spring:url>
	
	
	<a href="${urlTrip}" ><spring:message code="note.trip"/></a>
	<br/>
	<a href="${urlAuditor}" ><spring:message code="note.auditor"/></a>
	
	
	

</div>
