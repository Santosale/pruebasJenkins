<%--
 * index.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<h2><jstl:out value="${slogan}"></jstl:out></h2>
<br>
<jstl:if test="${raffles.size()>0}">
	<div id="raffle">
		<h3><spring:message code="welcome.raffle" /></h3>
		<div class="row">
			
				<jstl:forEach var="item" items="${raffles}">
					<div class="thumbnail col-sm-6 col-md-4" style="padding: 0px;">
						<img src="${item.getProductImages().toArray()[0]}" alt='<jstl:out value="${item.getTitle()}"/>' style="padding: 0px; margin: 0px; width: 100%; height: 300px!important;">
						<div class="caption">
						
							<h4><jstl:out value="${item.getTitle()}" /></h4>
							<%-- <p>${item.getDescription()}</p> --%>	
							
							<jstl:if test="${item.getPrice() != 0.0}">				
								<p style="text-align: center; font-size: 20px; font-weight: bold;"><spring:message code="welcome.currency.english" /><fmt:formatNumber value="${item.getPrice()}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="welcome.currency.spanish" /></p>
							</jstl:if>
							
							<jstl:if test="${item.getPrice() == 0.0}">				
								<p style="text-align: center; font-size: 20px; font-weight: bold;"><spring:message code="raffle.free" /></p>
							</jstl:if>
							
							<small><spring:message code="welcome.offeredBy" />: <a href="actor/company/profile.do?actorId=${item.getCompany().getId()}"><jstl:out value="${item.getCompany().getCompanyName()}" /></a></small><br><br>
							
							<a href="raffle/display.do?raffleId=${item.getId()}" class="btn btn-default"><spring:message code="welcome.details" /></a>
							
						</div>
					</div>
				</jstl:forEach>
		</div>
		<a href="raffle/list.do" class="btn btn-primary" style="width: 100%; padding: 10px;"><spring:message code="welcome.moreRaffles" /></a>
	</div>
</jstl:if>

<jstl:if test="${bargains.size()>0}">
	<br><br><br>
	<div id="bargain">
		<h3><spring:message code="welcome.bargain" /></h3>
		<div class="row">
			
				<jstl:forEach var="item" items="${bargains}">
					<div class="thumbnail col-sm-6 col-md-4" style="padding: 0px;">
						<img src="${item.getProductImages().toArray()[0]}" alt='<jstl:out value="${item.getProductName()}" />' style="padding: 0px; margin: 0px; width: 100%; height: 300px!important;">
						<div class="caption">
						
							<h4>${item.getProductName()}</h4>
								
							<jstl:if test="${plan==null}">
								<p style="text-align: center; font-size: 20px; font-weight: bold;"><spring:message code="welcome.currency.english" /><fmt:formatNumber value="${item.getPrice()}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="welcome.currency.spanish" /></p>
							</jstl:if>
							
							<jstl:if test="${plan!=null && plan.getName().equals('Gold Premium')}">
								<p style="text-align: center; font-size: 20px; font-weight: bold;"><spring:message code="welcome.currency.english" /><fmt:formatNumber value="${item.getPrice() - (item.getPrice() * 0.075)}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="welcome.currency.spanish" /></p>
							</jstl:if>
							
							<jstl:if test="${plan!=null && plan.getName().equals('Basic Premium')}">
								<p style="text-align: center; font-size: 20px; font-weight: bold;"><spring:message code="welcome.currency.english" /><fmt:formatNumber value="${item.getPrice() - (item.getPrice() * 0.025)}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="welcome.currency.spanish" /></p>
							</jstl:if>
							
							<small><spring:message code="welcome.offeredBy" />: <a href="actor/company/profile.do?actorId=${item.getCompany().getId()}"><jstl:out value="${item.getCompany().getCompanyName()}" /></a></small><br><br>
							
							<jstl:if test="${item.getIsPublished() || !isSponsor }">
								<a href="bargain/display.do?bargainId=${item.getId()}" class="btn btn-default"><spring:message code="welcome.details" /></a>
							</jstl:if>
							
							
						</div>
					</div>
				</jstl:forEach>
		</div>
		<a href="bargain/list.do" class="btn btn-primary" style="width: 100%; padding: 10px;"><spring:message code="welcome.moreBargains" /></a>
	</div>
</jstl:if>
