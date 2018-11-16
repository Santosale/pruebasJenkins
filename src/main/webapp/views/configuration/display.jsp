<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div class="container">
	
	<p><span class="display"><spring:message code="configuration.companyName"/>:</span> <jstl:out value="${configuration.getCompanyName()}" /></p>
	<p><span class="display"><spring:message code="configuration.banner"/>:</span> <jstl:out value="${configuration.getBanner()}" /></p>
	<p><span class="display"><spring:message code="configuration.welcomeMessageEn"/>:</span> <jstl:out value="${welcomeMessageEn}" /></p>
	<p><span class="display"><spring:message code="configuration.welcomeMessageEs"/>:</span> <jstl:out value="${welcomeMessageEs}" /></p>
	<p><span class="display"><spring:message code="configuration.vat" />:</span><fmt:formatNumber value="${configuration.getVat()}" currencySymbol="" type="currency"/>%</p>
	<p><span class="display"><spring:message code="configuration.countryCode"/>:</span> <jstl:out value="${configuration.getCountryCode()}" /></p>
	<p><span class="display"><spring:message code="configuration.spamWords"/>:</span> <jstl:out value="${configuration.getSpamWords()}" /></p>
	<p><span class="display"><spring:message code="configuration.cachedTime"/>:</span> <jstl:out value="${configuration.getCachedTime()}" /></p>
	<p><span class="display"><spring:message code="configuration.finderNumber"/>:</span> <jstl:out value="${configuration.getFinderNumber()}" /></p>
	
	<spring:url var="urlEdit" value="configuration/administrator/edit.do">
		<spring:param name="configurationId" value="${configuration.getId()}" />
	</spring:url>
		
	<a class="btn btn-primary" href="${urlEdit}" ><spring:message code="configuration.edit"/></a>
	
	<spring:url var="urlWelcomeMessageEs" value="configuration/administrator/editWelcomeMessage.do">
		<spring:param name="configurationId" value="${configuration.getId()}" />
		<spring:param name="countryCode" value="es" />
	</spring:url>
	
	<spring:url var="urlWelcomeMessageEn" value="configuration/administrator/editWelcomeMessage.do">
		<spring:param name="configurationId" value="${configuration.getId()}" />
		<spring:param name="countryCode" value="en" />
	</spring:url>
	
	<a class="btn btn-primary" href="${urlWelcomeMessageEs}" ><spring:message code="configuration.editWelcomeMessageEs"/></a>
	
	<a class="btn btn-primary" href="${urlWelcomeMessageEn}" ><spring:message code="configuration.editWelcomeMessageEn"/></a>
	
	
</div>
