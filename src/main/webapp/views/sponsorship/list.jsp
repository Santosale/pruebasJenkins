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

<spring:url var="urlURI" value="sponsorship/list.do" />
<display:table class="table table-striped table-bordered table-hover" name="sponsorships" id="row" requestURI="${urlURI }"
	pagesize="5" >

	<display:column>

		<jstl:if test="${row.getSponsor().getId() == sponsorId}">
	
				<a href="sponsorship/sponsor/edit.do?sponsorshipId=${row.getId()}">
					<spring:message code="sponsorship.edit" />
				</a>
	
		</jstl:if>
	</display:column>
	

	<spring:message code="sponsorship.banner" var="bannerHeader" />
	<display:column property="banner" title="${bannerHeader}"></display:column>

	<spring:message code="sponsorship.linkInfoPage"
		var="linkInfoPageHeader" />
	<display:column property="linkInfoPage" title="${linkInfoPageHeader}"></display:column>
	
	<spring:message code="sponsorship.trip" var="tripHeader" />
	<display:column property="trip.title" title="${tripHeader}"></display:column>

	<spring:url var="urlDisplay" value="sponsorship/display.do">
		<spring:param name="sponsorshipId" value="${row.getId()}" />
	</spring:url>
	<display:column>
		<a href="${urlDisplay }"> <spring:message
				code="sponsorship.display" /></a>
	</display:column>

</display:table>

<br />
