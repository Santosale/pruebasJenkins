<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div class="container">

	
	<span class="display"><spring:message code="actor.name"/></span><jstl:out value="  ${actor.getName()}" />
	<br/>
	<span class="display"><spring:message code="actor.surname"/></span><jstl:out value="  ${actor.getSurname()}" />
	<br/>
	<span class="display"><spring:message code="actor.email"/></span><jstl:out value="  ${actor.getEmail()}" />
	<br/>
	<span class="display"><spring:message code="actor.phone"/></span><jstl:out value="  ${actor.getPhone()}" />
	<br/>
	<span class="display"><spring:message code="actor.address"/></span><jstl:out value="  ${actor.getAddress()}" />
	<br/>
	<span class="display"><spring:message code="actor.suspicious"/></span><jstl:out value="  ${actor.getSuspicious()}" />
	<br/>
	
	
	
	
	<spring:url var="urlSponsorships" value="sponsorship/sponsor/list.do">
	</spring:url>
	
	
	
	<jstl:if test="${actor.getId()==idOfPrincipal }">
	
	<spring:url var="urlFolders" value="folder/list.do">
	</spring:url>
	
	<spring:url var="urlMessages" value="message/list.do">
	</spring:url>
	
	<spring:url var="urlSocialIdentities" value="socialidentity/list.do">
	</spring:url>
	
	
	
	<a href="${urlFolders}" ><spring:message code="sponsor.folders"/></a>
	<br/>
	<a href="${urlMessages}" ><spring:message code="sponsor.messages"/></a>
	<br/>
	<a href="${urlSocialIdentities}" ><spring:message code="sponsor.socialIdentities"/></a>
	<br/>
	<jstl:if test="${actorType.equals('sponsor')}">
	<security:authorize access="hasRole('SPONSOR')">
	<a href="${urlSponsorships}" ><spring:message code="sponsor.sponsorships"/></a>
	</security:authorize>
	<br/>
	</jstl:if>
	<jstl:if test="${actorType.equals('manager')}">		
			<security:authorize access="hasRole('MANAGER')">
				
			<spring:url var="urlTripsManager" value="trip/manager/list.do">
			</spring:url>
			<a href="${urlTripsManager}" ><spring:message code="actor.trips"/></a>
			<br/>
			</security:authorize>
			</jstl:if>	
			
			<jstl:if test="${actorType.equals('auditor')}">		
			<security:authorize access="hasRole('AUDITOR')">
				
			<spring:url var="urlAuditsAuditor" value="auditRecord/auditor/list.do">
			</spring:url>
			<a href="${urlAuditsAuditor}" ><spring:message code="actor.audits"/></a>
			<br/>
			
			<spring:url var="urlNotesAuditor" value="note/auditor/list.do">
			</spring:url>
			<a href="${urlNotesAuditor}" ><spring:message code="actor.notes"/></a>
			<br/>
			</security:authorize>
			</jstl:if>	
	</jstl:if>
	
	
		
			<jstl:if test="${actorType.equals('ranger')}">
			<jstl:if test="${hasCurriculum==true }">
			<spring:url var="urlCurriculum" value="curriculum/display.do">
					<spring:param name="curriculumId" value="${curriculumId}" />
			</spring:url>
			<a href="${urlCurriculum}" ><spring:message code="actor.curriculum"/></a>
			<br/>
			</jstl:if>
			</jstl:if>		
			

</div>
