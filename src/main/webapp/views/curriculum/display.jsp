<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


	
	<style>
		.plegable{
		   max-height:0;
		   overflow:hidden;
		   -webkit-transition: max-height 500ms ease-out;
		   -moz-transition: max-height 500ms ease-out;
		   -o-transition: max-height 500ms ease-out;
		   transition: max-height 500ms ease-out;
		}
		.plegable.display-on{
		   max-height:500;
		   -webkit-transition: max-height 500ms ease-out;
		   -moz-transition: max-height 500ms ease-out;
		   -o-transition: max-height 500ms ease-out;
		   transition: max-height 500ms ease-out;
		}
	
	</style>
	
	
	
	
	<div >	
		<jstl:if test="${curriculum == null}">
				<spring:url var="urlCreate" value="curriculum/ranger/create.do"></spring:url>
				<a class="btn btn-primary" href="${urlCreate}"><span style="font-size:20px"><spring:message code="curriculum.create" /></span></a>
				<br/>
		</jstl:if>
				
	
		
		<jstl:if test="${curriculum != null}">
					
			<div>
				
				<span class="display"><spring:message code="curriculum.ticker"/></span><jstl:out value="  ${curriculum.getTicker()}" />
				<br/>
				
				<span class="display"><spring:message code="curriculum.fullNamePR"/></span><jstl:out value="  ${curriculum.getFullNamePR()}" />
				<br/>
				<spring:message code="curriculum.photo.alt" var="alt"/>
				<jstl:set var="imgCurriculum" value=" ${curriculum.getPhotoPR()}"/>
				<img src="${imgCurriculum}" alt="${alt}" />
				<br/>
				
				<span class="display"><spring:message code="curriculum.emailPR"/></span><jstl:out value="  ${curriculum.getEmailPR()}" />
				<br/>
				
				<span class="display"><spring:message code="curriculum.phonePR"/></span><jstl:out value="  ${curriculum.getPhonePR()}" />
				<br/>
				
				<span class="display"><spring:message code="curriculum.linkedinPR"/></span><jstl:out value="  ${curriculum.getLinkedinPR()}" />
				<br/>
				<br/>	
				
				<p id="miscellaneous" class="btn btn-primary"><spring:message code="curriculum.miscellaneousRecord" /></p>

				<!-- Aquí se meteran los miscellaneousRecords -->
				<div id="miscellaneousdiv">
				</div>
				
				<br/>
				
				<p id="professional" class="btn btn-primary"><spring:message code="curriculum.professionalRecord" /></p>

				<!-- Aquí se meteran los professionalRecords -->
				<div id="professionaldiv">
				</div>
				
				<br/>
				
				<p id="education" class="btn btn-primary"><spring:message code="curriculum.educationRecord" /></p>

				<!-- Aquí se meteran los educationRecords -->
				<div id="educationdiv">
				</div>
				
				<br/>
				
				<p id="endorser" class="btn btn-primary"><spring:message code="curriculum.endorserRecord" /></p>

				<!-- Aquí se meteran los endorserRecords -->
				<div id="endorserdiv">
				</div>
				
				<br/>
			
				
				<security:authorize access="isAuthenticated()">
					<security:authentication var="authenticated" property="principal.username"/>
					<jstl:if test="${authenticated.equals(curriculum.getRanger().getUserAccount().getUsername())}">
						<spring:url var="urlEdit" value="curriculum/ranger/edit.do">
							<spring:param name="curriculumId" value="${curriculum.getId()}" />
						</spring:url>
						<a class="btn btn-danger" href="${urlEdit}"> <spring:message code="curriculum.edit" /></a>
						<br/>
					</jstl:if>
				</security:authorize>
				
			</div>
		</jstl:if>	
	</div>
	
	
	
