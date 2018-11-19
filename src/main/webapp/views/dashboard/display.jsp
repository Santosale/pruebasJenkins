<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>


	<div>
	
		
		<div class="container">
			<acme:display code="dashboard.average.banner.per.sponsor" value="${avgMinMaxStandarDesviationBannersPerSponsor[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.min.banner.per.sponsor" value="${avgMinMaxStandarDesviationBannersPerSponsor[1]}" formatNumber="true"/>
			
			<acme:display code="dashboard.max.banner.per.sponsor" value="${avgMinMaxStandarDesviationBannersPerSponsor[2]}" formatNumber="true"/>
			
			<acme:display code="dashboard.standar.banner.per.sponsor" value="${avgMinMaxStandarDesviationBannersPerSponsor[3]}" formatNumber="true"/>
		 
		</div>
		<br> 
		
		<div class="container">
			
			<acme:display code="dashboard.avg.ratio.tag.per.bargain" value="${avgRatioTagsPerBargain}" formatNumber="true"/>
					 
		</div>
		
		<br>
		
		
		<div class="container">
		
			<spring:url var="urlBargainWithMoreSponsorships" value="bargain/administrator/listWithMoreSponsorships.do"/>
			<a href="${urlBargainWithMoreSponsorships}" ><spring:message code="dashboard.bargain.moreSponsorships"/></a>
			
			<br>
			
			<spring:url var="urlBargainWithLessSponsorships" value="bargain/administrator/listWithLessSponsorships.do"/>
			<a href="${urlBargainWithLessSponsorships}" ><spring:message code="dashboard.bargain.lessSponsorships"/></a>
			
			
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlBargainInMoreWishList" value="bargain/administrator/isInMoreWishList.do"/>
			<a href="${urlBargainInMoreWishList}" ><spring:message code="dashboard.bargain.inMoreWishList"/></a>
			
		
		</div>
		
		<br>
		
		
		<div class="container">
		
			<spring:url var="urlSurveysMorePopular" value="survey/administrator/morePopular.do"/>
			<a href="${urlSurveysMorePopular}" ><spring:message code="dashboard.survey.morePopular"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.ratio.notification.per.total" value="${ratioNotificationsPerTotal}" formatNumber="true"/>
					 
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.avg.usersWithParticipation.per.total" value="${avgUsersWithParticipationsPerTotal}" formatNumber="true"/>
					 
		</div>
		
		<br>
		
		<div class="container">
			<jstl:set var="zeroFifteen" value="<%= Double.valueOf(0.15)%>"/>
		
			<spring:url var="urlCompaniesWriterMorePercentage15" value="company/administrator/writerOfMorePercentage15.do"/>
			<a href="${urlCompaniesWriterMorePercentage15}" ><spring:message code="dashboard.survey.CompaniesWriterMorePercentage15"/></a>
			
			<br>
			
			<spring:url var="urlCompaniesWriterMorePercentage10" value="company/administrator/writerOfMorePercentage10.do"/>
			<a href="${urlCompaniesWriterMorePercentage10}" ><spring:message code="dashboard.survey.CompaniesWriterMorePercentage10"/></a>
			
			<br> 
			
			<spring:url var="urlCompaniesWriterMorePercentage5" value="company/administrator/writerOfMorePercentage5.do"/>
			<a href="${urlCompaniesWriterMorePercentage5}" ><spring:message code="dashboard.survey.CompaniesWriterMorePercentage5"/></a>
		
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlTopFive" value="user/administrator/topFiveUsersMoreValorations.do"/>
			<a href="${urlTopFive}" ><spring:message code="dashboard.survey.topFive"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.ratio.usersHaveComments" value="${ratioUsersWithComments}" formatNumber="true"/>
					 
		</div>
		
		<br> 
		
		<div class="container">
		
			<spring:url var="urlMore10PercentageInteractions" value="user/administrator/more10PercentageInteractions.do"/>
			<a href="${urlMore10PercentageInteractions}" ><spring:message code="dashboard.users.more10PercentageInteractions"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlCategoriesMoreBargainsThanAverage" value="category/administrator/moreBargainThanAverage.do"/>
			<a href="${urlCategoriesMoreBargainsThanAverage}" ><spring:message code="dashboard.categories.more.bargainsThanAverage"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlCompanyWithMoreTag" value="company/administrator/companiesWithMoreTags.do"/>
			<a href="${urlCompanyWithMoreTag}" ><spring:message code="dashboard.company.moreTag"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.avg.ratio.bargainPerCategory" value="${avgRatioBargainPerCategory}" formatNumber="true"/>
					 
		</div>
		
		<br> 
		
		<div class="container">
		
			<spring:url var="urlUserMoreAverageCharacter" value="user/administrator/moreAverageCharacterLenght.do"/>
			<a href="${urlUserMoreAverageCharacter}" ><spring:message code="dashboard.user.moreAverageCharacter"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlGrouponsMoreParticipationsThanAverage" value="groupon/administrator/tenPercentageMoreParticipationsThanAverage.do"/>
			<a href="${urlGrouponsMoreParticipationsThanAverage}" ><spring:message code="dashboard.groupon.moreParticipationsThanAverage"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.min.discount.per.bargain" value="${minMaxAvgStandarDesviationDiscountPerBargain[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.max.discount.per.bargain" value="${minMaxAvgStandarDesviationDiscountPerBargain[1]}" formatNumber="true"/>
			
			<acme:display code="dashboard.average.discount.per.bargain" value="${minMaxAvgStandarDesviationDiscountPerBargain[2]}" formatNumber="true"/>
			
			<acme:display code="dashboard.standar.discount.per.bargain" value="${minMaxAvgStandarDesviationDiscountPerBargain[3]}" formatNumber="true"/>
		 
		</div>
		
		<br> 
		
		<div class="container">
			
			<acme:display code="dashboard.min.discount.per.groupon" value="${minMaxAvgStandarDesviationDiscountPerGroupon[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.max.discount.per.groupon" value="${minMaxAvgStandarDesviationDiscountPerGroupon[1]}" formatNumber="true"/>
			
			<acme:display code="dashboard.average.discount.per.groupon" value="${minMaxAvgStandarDesviationDiscountPerGroupon[2]}" formatNumber="true"/>
			
			<acme:display code="dashboard.standar.discount.per.groupon" value="${minMaxAvgStandarDesviationDiscountPerGroupon[3]}" formatNumber="true"/>
		 
		</div>
		
		<br> 
		
		<div class="container">
		
			<spring:url var="urlUsersMoreWonRaffles" value="user/administrator/moreWonRaffles.do"/>
			<a href="${urlUsersMoreWonRaffles}" ><spring:message code="dashboard.user.wonRaffles"/></a>
			
		
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlUsersPurchaseMoreTickets" value="user/administrator/purchaseMoreTickets.do"/>
			<a href="${urlUsersPurchaseMoreTickets}" ><spring:message code="dashboard.user.purchaseMoreTickets"/></a>
			
			<br>
			
			<spring:url var="urlUsersPurchaseLessTickets" value="user/administrator/purchaseLessTickets.do"/>
			<a href="${urlUsersPurchaseLessTickets}" ><spring:message code="dashboard.user.purchaseLessTickets"/></a>
			
			
		</div>
		
		<br>
		
		<div class="container">
			
			<acme:display code="dashboard.avg.ticketsPurchaseUserRaffle" value="${avgTicketsPurchaseByUsersPerRaffle}" formatNumber="true"/>
					 
		</div>
		
		<br>
		
		<div class="container">
		
			<spring:url var="urlUsers25Percentage" value="user/administrator/purchase25PercentageMoreTotalForAllRaffles.do"/>
			<a href="${urlUsers25Percentage}" ><spring:message code="dashboard.user.25PercentageMoreTotalForAllRaffles"/></a>
			
		
		</div>
		
		<!--  -->
		
		
	</div>
	
	
