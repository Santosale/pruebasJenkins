<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<a href="finder/explorer/search.do"><spring:message code="finder.search" /></a>

<display:table class="table table-striped table-bordered table-hover" name="finder" id="row" requestURI="finder/explorer/display.do">

	<spring:message code="finder.format.date" var="dateFormat"/>
	<spring:message code="finder.format.moment" var="momenFormat" />

	<spring:message code="finder.moment" var="momentHeader" />
	<display:column property="moment" title="${momentHeader}" format="{0,date,${momentFormat}}"/>
	
	<spring:message code="finder.keyWord" var="keyWordHeader" />
	<display:column property="keyWord" title="${keyWordHeader}" />
	
	<spring:message code="finder.minPrice" var="minPriceHeader" />
	<display:column title="${minPriceHeader}" >
	<jstl:if test="${finder.getMinPrice()!=null }">
	<spring:message code="finder.var1"/><fmt:formatNumber value="${finder.getMinPrice()}" currencySymbol="" type="currency"/><spring:message code="finder.var2"/>
	</jstl:if>
	</display:column>
	
	<spring:message code="finder.maxPrice" var="maxPriceHeader" />
	<display:column title="${maxPriceHeader}">
	<jstl:if test="${finder.getMaxPrice()!=null }">
	<spring:message code="finder.var1"/><fmt:formatNumber value="${finder.getMaxPrice()}" currencySymbol="" type="currency"/><spring:message code="finder.var2"/>
	</jstl:if>
	</display:column>
	
	<spring:message code="finder.startedDate" var="startedDateHeader" />
	<display:column property="startedDate" title="${startedDateHeader}" format="{0,date,${dateFormat}}"/>

	<spring:message code="finder.finishedDate" var="finishedDateHeader" />
	<display:column property="finishedDate" title="${finishedDateHeader}" format="{0,date,${dateFormat}}" />

</display:table>

<br />

<h2><a href="finder/explorer/list.do"><spring:message code="finder.results" /></a></h2>

<br />

