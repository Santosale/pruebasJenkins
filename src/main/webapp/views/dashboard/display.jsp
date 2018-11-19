<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>


	<div>
	
		
		<div class="container">
			<acme:display code="dashboard.newspaper.user.average" value="${newspaperPerUser[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.newspaper.user.standard" value="${newspaperPerUser[1]}" formatNumber="true"/>
		 
		</div>
		<br/> 
		
		<div class="container">
			<acme:display code="dashboard.articles.writter.average" value="${articlesPerWriter[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.articles.writter.standard" value="${articlesPerWriter[1]}" formatNumber="true"/>
		 
		</div>
		<br/> 
		
		<div class="container">
			<acme:display code="dashboard.articles.newspaper.average" value="${articlesPerNewspaper[0]}" formatNumber="true"/>
			
			<acme:display code="dashboard.articles.newspaper.standard" value="${articlesPerNewspaper[1]}" formatNumber="true"/>
		 
		</div>
		<br/> 
		
		
		<div class="container">
		
			<spring:url var="urlMoreNewspaper" value="newspaper/administrator/listFewerAverage.do"/>
			<a href="${urlMoreNewspaper}" ><spring:message code="dashboard.more.articles"/></a>
			
			
		</div>
		<br/>
			
		<div class="container">
		
			<spring:url var="urlFewerNewspaper" value="newspaper/administrator/listMoreAverage.do"/>
			<a href="${urlFewerNewspaper}" ><spring:message code="dashboard.fewer.articles"/></a>
			
			
		</div>
		<br/>
		
		
		<div class="container">
			<acme:display code="dashboard.ratio.newspaper" value="${ratioUserCreateNewspaper}" formatNumber="true"/>
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.ratio.user.writter" value="${ratioUserWrittenArticle}" formatNumber="true"/>
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.followUp.article" value="${followUpPerArticle}" formatNumber="true"/>
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.followUp.one.week" value="${followUpPerArticleOneWeek}" formatNumber="true"/>
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.followUp.two.week" value="${followUpPerArticleTwoWeek}" formatNumber="true"/>
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.chirp.user.average" value="${chirpPerUser[0]}" formatNumber="true"/>
			<acme:display code="dashboard.chirp.user.standard" value="${chirpPerUser[1]}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		
		<div class="container">
			<acme:display code="dashboard.posted" value="${ratioUserPostNumberChirps}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.ratio.private" value="${ratioPublicVsPrivateNewspaper}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.average.private" value="${numberArticlesPerPrivateNewspaper}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		
		<div class="container">
			<acme:display code="dashboard.article.per.newspaper.public" value="${numberArticlesPerPublicNewspaper}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.ratio.public.newspaper" value="${averageRatioPrivateVsPublicNewspaperPerPublisher}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<h4><spring:message code="dashboard.suscribers.newspaper" /></h4>
		<c:forEach items="${ratioSuscribersPrivateVsTotalCustomer}" var="entry">
  			<p><span class="display"><jstl:out value="${entry.key.title}" /></span>: <jstl:out value="${entry.value}" /></p>
		</c:forEach>
		<br>
		
	<acme:paginate pageNumber="${pageNumber }" url="dashboard/administrator/display.do" objects="${ratioSuscribersPrivateVsTotalCustomer.values()}" page="${page }"/>
		
		<br/><br/>
				
		<div class="container">
			<acme:display code="dashboard.ratio.advertisement.newspaper" value="${ratioNewspaperHaveAdvertisementVsHavent}" formatNumber="true"/>
			
			
		</div>
		<br />
		
		<div class="container">
			<acme:display code="dashboard.ratio.advertisement" value="${ratioAdvertisementHaveTaboo}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.avg.volume" value="${avgNewspaperPerVolume}" formatNumber="true"/>
			
			
		</div>
		<br/>
		
		<div class="container">
			<acme:display code="dashboard.ratio.volume.newspaper" value="${ratioSubscriptionVolumeVsNewspaper}" formatNumber="true"/>
			
			
		</div>
		<br/>
	</div>
	
	
