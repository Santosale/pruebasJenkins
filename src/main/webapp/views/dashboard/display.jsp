<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


	<div>
	
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.applicationstrip"/></span><fmt:formatNumber value="${applicationsPerTrips[0]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.applicationstrip"/></span><jstl:out value="${applicationsPerTrips[1].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.applicationstrip"/></span><jstl:out value="${applicationsPerTrips[2].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.standard.applicationstrip"/></span><fmt:formatNumber value="${applicationsPerTrips[3]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.tripsmanager"/></span><fmt:formatNumber value="${tripsPerManager[0]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.tripsmanager"/></span><jstl:out value="${tripsPerManager[1].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.tripsmanager"/></span><jstl:out value="${tripsPerManager[2].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.standard.tripsmanager"/></span><fmt:formatNumber value="${tripsPerManager[3]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.pricetrips"/></span><spring:message code="dashboard.var1" /><fmt:formatNumber value="${pricePerTrips[0]}" currencySymbol="" type="currency"/><spring:message code="dashboard.var2" />
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.pricetrips"/></span><spring:message code="dashboard.var1" /><fmt:formatNumber value="${pricePerTrips[1]}" currencySymbol="" type="currency"/><spring:message code="dashboard.var2" />
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.pricetrips"/></span><spring:message code="dashboard.var1" /><fmt:formatNumber value="${pricePerTrips[2]}" currencySymbol="" type="currency"/><spring:message code="dashboard.var2" />
			<br/>
			<span class="display"><spring:message code="dashboard.standard.pricetrips"/></span><spring:message code="dashboard.var1" /><fmt:formatNumber value="${pricePerTrips[3]}" currencySymbol="" type="currency"/><spring:message code="dashboard.var2" />
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.tripsranger"/></span><fmt:formatNumber value="${tripsPerRanger[0]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.tripsranger"/></span><jstl:out value="${tripsPerRanger[1].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.tripsranger"/></span><jstl:out value="${tripsPerRanger[2].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.standard.tripsranger"/></span><fmt:formatNumber value="${tripsPerRanger[3]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.ratio.applicationspendig"/></span><fmt:formatNumber value="${ratioPending}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.ratio.applicationsdue"/></span><fmt:formatNumber value="${ratioDue}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.ratio.applicationsaccepted"/></span><fmt:formatNumber value="${ratioAccepted}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.ratio.applicationscancelled"/></span><fmt:formatNumber value="${ratioCancelled}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.ratio.tripscancelled"/></span><fmt:formatNumber value="${ratioTripsCancelled}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<spring:url var="urlListMoreApplications" value="dashboard/administrator/list.do"></spring:url>
			<a href="${urlListMoreApplications}"> <spring:message code="dashboard.list.moreapplications" /></a>
			<br/>
			<br/>
			
			<spring:url var="urlTableLegalText" value="dashboard/administrator/table.do"></spring:url>
			<a href="${urlTableLegalText}"> <spring:message code="dashboard.table.legaltextreferences" /></a>
			<br/>
			<br/>
		</div>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.notestrip"/></span><fmt:formatNumber value="${notesPerTrip[2]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.notestrip"/></span><jstl:out value="${notesPerTrip[0].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.notestrip"/></span><jstl:out value="${notesPerTrip[1].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.standard.notestrip"/></span><fmt:formatNumber value="${notesPerTrip[3]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.average.auditstrip"/></span><fmt:formatNumber value="${auditsPerTrip[2]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.minimum.auditstrip"/></span><jstl:out value="${auditsPerTrip[0].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.maximum.auditstrip"/></span><jstl:out value="${auditsPerTrip[1].intValue()}"/>
			<br/>
			<span class="display"><spring:message code="dashboard.standard.auditstrip"/></span><fmt:formatNumber value="${auditsPerTrip[3]}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.ratio.tripaudit"/></span><fmt:formatNumber value="${ratioTripAudit}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.ratio.rangercurricula"/></span><fmt:formatNumber value="${ratioRangerCurricula}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.ratio.rangercurriculaendorser"/></span><fmt:formatNumber value="${ratioRangerCurriculaEndorser}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
		
		<div class="container">
			<span class="display"><spring:message code="dashboard.ratio.managersuspicious"/></span><fmt:formatNumber value="${ratioManagerSuspicious}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			<span class="display"><spring:message code="dashboard.ratio.rangersuspicious"/></span><fmt:formatNumber value="${ratioRangerSuspicious}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/>
			<br/>
			
		</div>
		<br/>
	
	</div>
	
	
