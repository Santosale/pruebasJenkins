<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<jstl:if test="${sponsorship!=null}">
	<a title="Banner" href="${sponsorship.getLinkInfoPage()}"><img src="${sponsorship.getBanner()}" alt="Banner" width="400px" height="200px" style="margin-left:15px;" /></a>	
</jstl:if>
	<div>
		<div class="container">
			<spring:message code="trip.format.date" var="dateFormat"/>
			
			<span class="display"><spring:message code="trip.ticker"/></span><jstl:out value="  ${trip.getTicker()}" />
			<br/>
		
			<span class="display"><spring:message code="trip.title"/></span><jstl:out value="  ${trip.getTitle()}" />
			<br/>
			
			<span class="display"><spring:message code="trip.description"/></span><jstl:out value="  ${trip.getDescription()}" />
			<br/>
			
			<span class="display"><spring:message code="trip.publicationDate"/></span><fmt:formatDate value="${trip.getPublicationDate()}" pattern="${dateFormat }"/>
			<br/>
			
			<span class="display"><spring:message code="trip.startDate"/></span><fmt:formatDate value="${trip.getStartDate()}" pattern="${dateFormat }"/>
			<br/>
			
			<span class="display"><spring:message code="trip.endDate"/></span><fmt:formatDate value="${trip.getEndDate()}" pattern="${dateFormat }"/>
			<br/>
			
			<span class="display"><spring:message code="trip.explorerRequirements"/></span><jstl:out value="  ${trip.getExplorerRequirements()}" />
			<br/>
			
			<jstl:if test="${trip.getCancellationReason()!=null}">
				<span class="display"><spring:message code="trip.cancellationReason"/></span><jstl:out value="  ${trip.getCancellationReason()}" />
				<br/>
			</jstl:if>
			
			<span class="display"><spring:message code="trip.price"/></span>
			<spring:message code="trip.var1" /><fmt:formatNumber value="${trip.getPrice()}" currencySymbol="" type="currency"/><spring:message code="trip.var2"/>
			<br/>
		</div>
		
		<!-- Mostramos las stages si las hay-->
		<jstl:if test="${!stages.isEmpty()}">
		
			<div class="container">
				<span style="font-size:20px"><spring:message code="trip.stages"></spring:message></span>
				<br/>
				<br/>
				<jstl:forEach var="row" items="${stages}">
					<div style="border:2px solid black; margin-left:25px; margin-bottom:20px; padding:10px;">
						<span class="display"><spring:message code="trip.stage.title"/></span><jstl:out value="  ${row.getTitle()}" />
						<br/>
						<span class="display"><spring:message code="trip.stage.description"/></span><jstl:out value="  ${row.getDescription()}" />
						<br/>
						<span class="display"><spring:message code="trip.stage.price"/></span><spring:message code="trip.var1" /><fmt:formatNumber value="${row.getPrice()}" currencySymbol="" type="currency"/><spring:message code="trip.var2"/>
						<br/>
					</div>
					<br/>
				</jstl:forEach>
			</div>
		</jstl:if>
		
		<spring:url var="urlManager" value="actor/manager/display.do">
			<spring:param name="managerId" value="${trip.getManager().getId()}" />
		</spring:url>
		
		<spring:url var="urlRanger" value="actor/ranger/display.do">
			<spring:param name="rangerId" value="${trip.getRanger().getId()}" />
		</spring:url>
		
		<spring:url var="urlLegalText" value="legalText/display.do">
			<spring:param name="legalTextId" value="${trip.getLegalText().getId()}" />
		</spring:url>
		
		<spring:url var="urlCategory" value="category/navigate.do">
			<spring:param name="categoryId" value="${trip.getCategory().getId()}" />
		</spring:url>
		
		<spring:url var="urlSurvivalClasses" value="survivalClass/list.do">
			<spring:param name="tripId" value="${trip.getId()}" />
		</spring:url>
		
		<spring:url var="urlTagValues" value="tagValue/list.do">
			<spring:param name="tripId" value="${trip.getId()}" />
		</spring:url>
		
		<spring:url var="urlSponsorships" value="sponsorship/list.do">
			<spring:param name="tripId" value="${trip.getId()}" />
		</spring:url>
		
		<spring:url var="urlAudits" value="auditRecord/list.do">
			<spring:param name="tripId" value="${trip.getId()}" />
		</spring:url>
		
		<spring:url var="urlStories" value="story/list.do">
			<spring:param name="tripId" value="${trip.getId()}" />
		</spring:url>
		
		
		<div class="container">
			
				<span style="font-size:20px"><spring:message code="trip.other.actions"/></span>
				<br/>
				<br/>
				<a href="${urlManager}" ><spring:message code="trip.manager.display"/></a>
				<br/>	
				<a href="${urlRanger}" ><spring:message code="trip.ranger.display"/></a>
				<br/>
				<a href="${urlLegalText}" ><spring:message code="trip.legalText.display"/></a>
				<br/>
				<a href="${urlCategory}" ><spring:message code="trip.category.display"/></a>
				<br/>
				<a href="${urlSurvivalClasses}" ><spring:message code="trip.survivalClasses.display"/></a>
				<br/>
				<a href="${urlTagValues}" ><spring:message code="trip.tagValues.display"/></a>
				<br/>
				<a href="${urlSponsorships}" ><spring:message code="trip.sponsorships.display"/></a>
				<br/>	
				<a href="${urlAudits}" ><spring:message code="trip.audits.display"/></a>
				<br/>
				<a href="${urlStories}" ><spring:message code="trip.stories.display"/></a>
				<br/>
		</div>
		
		
	
		<!-- Si es un auditor le permitimos crear una note y un audit-->
		<security:authorize access="hasRole('AUDITOR')">
			
			<div class="container">
			
				<span style="font-size:20px"><spring:message code="trip.auditor.actions"/></span>
				<br/>
				<br/>
				<!-- Enlace para crear la note -->
				<spring:url var="urlcreateNote" value="note/auditor/create.do">
					<spring:param name="tripId" value="${trip.getId()}" />
				</spring:url>
				<a href="${urlcreateNote}" ><spring:message code="trip.createNote.display"/></a>
				<br/>
				
				<!-- Enlace para crear el audit si ese auditor no ha auditado para este trip aun-->
				<jstl:if test="${createAudit}">
					<spring:url var="urlcreateAudit" value="auditRecord/auditor/create.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					<a href="${urlcreateAudit}" ><spring:message code="trip.createAudit.display"/></a>
					<br/>
				</jstl:if>
			</div>
				
		</security:authorize>
		
		<!-- Si es un sponsor le permitimos crear un sponsorship-->
		<security:authorize access="hasRole('SPONSOR')">
			<div class="container">
			
				<span style="font-size:20px"><spring:message code="trip.sponsor.actions"/></span>
				<br/>
				<br/>
				<!-- Enlace para crear el sponsorship -->
				<spring:url var="urlcreateSponsorship" value="sponsorship/sponsor/create.do">
					<spring:param name="tripId" value="${trip.getId()}" />
				</spring:url>
				
				<a href="${urlcreateSponsorship}" ><spring:message code="trip.createSponsorship.display"/></a>
				<br/>
			</div>
		</security:authorize>
		
		<!-- Si es un explorer le permitimos crear Stories -->
		<security:authorize access="hasRole('EXPLORER')">
		
			<!-- Si es applicant, se ha aceptado la application y  no se ha acabado el viaje-->
			<jstl:if test="${isApplicant && applicationAccepted && trip.getEndDate().compareTo(currentDate)>0 }">
				<div class="container">
					
					<span style="font-size:20px"><spring:message code="trip.explorer.actions"/></span>
					<br/>
					<br/>
					<spring:url var="urlSurvivalsToRegister" value="survivalClass/explorer/register.do">
								<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
							
					<a href="${urlSurvivalsToRegister}" ><spring:message code="trip.survivalsToRegister"/></a>
					<br/>
					
					<spring:url var="urlSurvivalsToUnregister" value="survivalClass/explorer/unregister.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlSurvivalsToUnregister}" ><spring:message code="trip.survivalsToUnregister"/></a>
					<br/>
				</div>
			</jstl:if>
			
			<!-- Si es applicant y el trip se ha acabado, puede escribir una historia -->
			<jstl:if test="${isApplicant && applicationAccepted && trip.getEndDate().compareTo(currentDate)<0 }">
				<div class="container">
					
					<span style="font-size:20px"><spring:message code="trip.explorer.actions"/></span>
					<br/>
					<br/>
					<!-- Enlace para crear la story -->
					<spring:url var="urlcreateStory" value="story/explorer/create.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlcreateStory}" ><spring:message code="trip.createStory.display"/></a>
					<br/>
				</div>
			</jstl:if>
			
			<!-- Si NO es applicant-->
			<jstl:if test="${!isApplicant && trip.getStartDate().compareTo(currentDate)>0 && trip.getCancellationReason()==null}">
				<div class="container">
					
					<span style="font-size:20px"><spring:message code="trip.explorer.actions"/></span>
					<br/>
					<br/>
					<!-- Enlace para crear la application -->
					<spring:url var="urlcreateApplication" value="application/explorer/apply.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlcreateApplication}" ><spring:message code="trip.createApplication.display"/></a>
					<br/>
				</div>
			</jstl:if>
			
		</security:authorize>
		
		<!-- Si hay un user autenticado vemos si es su manager y le dejamos crear stages, tagValues y survival Y ver notes y applications-->
	
		<security:authorize access="isAuthenticated()">
			<security:authentication var="principal" property="principal.username"/>
			
			<jstl:if test="${trip.getManager().getUserAccount().getUsername().equals(principal)}">
				<div class="container">
					
					<span style="font-size:20px"><spring:message code="trip.manager.actions"/></span>
					<br/>
					<br/>
					<!-- Enlace para ver las applications del trip -->
					<spring:url var="urlApplications" value="application/manager/list.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlApplications}" ><spring:message code="trip.applications.display"/></a>
					<br/>
					
					<!-- Enlace para ver las notes del trip -->
					<spring:url var="urlNotes" value="note/managerAuditor/list.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlNotes}" ><spring:message code="trip.notes.display"/></a>
					<br/>
				
					<!-- Enlace para crear la survival class -->
					<spring:url var="urlcreateSurvivalClass" value="survivalClass/manager/create.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					<a href="${urlcreateSurvivalClass}" ><spring:message code="trip.createSurvivalClass.display"/></a>
					<br/>
					
					<!-- Enlace para crear tag Value -->
					<spring:url var="urlcreateTagValue" value="tagValue/manager/create.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					
					
					<spring:url var="urlListStages" value="stage/manager/list.do">
						<spring:param name="tripId" value="${trip.getId()}" />
					</spring:url>
					<a href="${urlListStages}" ><spring:message code="trip.listStages"/></a>
						<br/>
					
					<!-- Dejamos que el manager cree stages si el viaje aun no se ha publicado -->
					
					<jstl:if test="${trip.getPublicationDate().compareTo(currentDate)>0}">
						<a href="${urlcreateTagValue}" ><spring:message code="trip.createTagValue.display"/></a>
						<br/>
						<!-- Enlace para crear stage-->
						<spring:url var="urlcreateStage" value="stage/manager/create.do">
							<spring:param name="tripId" value="${trip.getId()}" />
						</spring:url>
						
						<a href="${urlcreateStage}" ><spring:message code="trip.createStage.display"/></a>
					</jstl:if>
				</div>
			</jstl:if>
		</security:authorize>
		
			
			
		
		
	</div>
	
	
