<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


	
<div>

	
	<spring:message code="sponsor.name"/><jstl:out value="  ${sponsor.getName()}" />
	<br/>
	<spring:message code="sponsor.surname"/><jstl:out value="  ${sponsor.getSurname()}" />
	<br/>
	<spring:message code="sponsor.email"/><jstl:out value="  ${sponsor.getEmail()}" />
	<br/>
	<spring:message code="sponsor.phone"/><jstl:out value="  ${sponsor.getPhone()}" />
	<br/>
	<spring:message code="sponsor.address"/><jstl:out value="  ${sponsor.getAddress()}" />
	<br/>
	<spring:message code="sponsor.suspicious"/><jstl:out value="  ${sponsor.getSuspicious()}" />
	<br/>
	
	
	
	
	<spring:url var="urlSponsorships" value="sponsorship/sponsor/list.do">
	</spring:url>
	
	<security:authorize access="hasRole('SPONSOR')">
	<jstl:if test="${sponsor.getId()==idOfPrincipal }">
	
	<spring:url var="urlFolders" value="folder/list.do">
	</spring:url>
	
	<spring:url var="urlMessages" value="message/list.do">
	</spring:url>
	
	<spring:url var="urlSocialIdentities" value="socialIdentities/list.do">
	</spring:url>
	
	
	
	<a href="${urlFolders}" ><spring:message code="sponsor.folders"/></a>
	<br/>
	<a href="${urlMessages}" ><spring:message code="sponsor.messages"/></a>
	<br/>
	<a href="${urlSocialIdentities}" ><spring:message code="sponsor.socialIdentities"/></a>
	<br/>
	</jstl:if>
	</security:authorize>
	<a href="${urlSponsorships}" ><spring:message code="sponsor.sponsorships"/></a>
	<br/>
	
	

</div>
