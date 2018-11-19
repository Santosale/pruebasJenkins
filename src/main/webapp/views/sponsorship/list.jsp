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
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>



<display:table class="table table-striped table-bordered table-hover" name="sponsorships" id="row">
	
	<jstl:if test="${requestURI.equals('sponsorship/sponsor/list.do')}">
		<acme:columnLink id="${row.getId()}" domain="sponsorship" actor="sponsor" action="edit"/>
		
		<acme:column property="amount" domain="sponsorship" />
		
		<spring:message code="sponsorship.url" var="contentURL"/>
		<acme:columnLink url="${row.getUrl()}" domain="sponsorship" code="url" content="${contentURL}"/>
		<spring:message code="sponsorship.image" var="contentImage"/>
		<acme:columnLink url="${row.getImage()}"  domain="sponsorship" code="image" content="${contentImage}"/>
	
		
		<spring:message code="sponsorship.bargain" var="sponsorshipBargain"/>
		<display:column title="${sponsorshipBargain}">
			<jstl:out value="${row.getBargain().getProductName()}"></jstl:out>
		</display:column>
	
		<acme:columnLink id="${row.getId()}" domain="sponsorship" actor="sponsor" action="display"/>
	
	</jstl:if>
	
	
	<jstl:if test="${requestURI.equals('sponsorship/list.do')}">
		<spring:message code="sponsorship.sponsorships" var="sponsorshipSponsorships"/>
		<display:column title="${sponsorshipSponsorships}">
			
			<div style="text-align: center">
			
			<jstl:if test="${!linkBroken.get(row)}">
				<acme:image value="${row.getImage()}" alt="${sponsorshipAlt}" url="${row.getUrl()}"/>
			</jstl:if>
			
			<jstl:if test="${linkBroken.get(row)}">
				<acme:image value="${imageBroken}" alt="${sponsorshipAlt}" url="${row.getUrl()}"/>
			</jstl:if>
			
			<br><br>
			<acme:displayLink parameter="actorId" code="sponsorship.sponsor" action="actor/sponsor/profile.do" parameterValue="${row.getSponsor().getId()}" css="btn btn-primary"></acme:displayLink>
		
			</div>
		</display:column>
	</jstl:if>

</display:table>

<jstl:if test="${requestURI.equals('sponsorship/sponsor/list.do')}">
	<acme:paginate pageNumber="${pageNumber }" url="${requestURI}" objects="${sponsorships}" page="${page}" />
</jstl:if>

<jstl:if test="${requestURI.equals('sponsorship/list.do')}">
	<acme:paginate pageNumber="${pageNumber }" url="${requestURI}" objects="${sponsorships}" page="${page}" parameter="bargainId" parameterValue="${bargainId}"/>
</jstl:if>

