<%--
 * header.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<nav class="navbar navbar-default">
  <div class="container-fluid">

    <div class="navbar-header">
 		<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
	  <a style="background-image: url(images/newspaper-1.jpg); -ms-behavior: url(styles/backgroundsize.min.htc);" class="navbar-brand" href="#"><span>Acme Newspaper</span></a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">    
      <ul class="nav navbar-nav">
      	
      	<li><a href="welcome/index.do"><spring:message code="master.page.home" /></a></li>
      	
      	<security:authorize access="isAnonymous()">
			<li><a href="newspaper/list.do"><spring:message code="master.page.all.newspapers.isAnonymous" /></a></li>
			<li><a href="actor/user/list.do"><spring:message code="master.page.all.user" /></a></li>
			<li><a href="volume/list.do"><spring:message code="master.page.all.volume" /></a></li>
      	</security:authorize>
               
        <li class="dropdown">
        
        	<security:authorize access="hasRole('ADMIN')">
        		<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message	code="master.page.administrator" /> <span class="caret"></span></a>
        		<ul class="dropdown-menu">
					<li><a href="termCondition/administrator/display.do"><spring:message code="master.page.admin.termCondition" /></a></li>
					<li><a href="dashboard/administrator/display.do"><spring:message code="master.page.administrator.dashboard" /></a></li>
					<li><a href="configuration/administrator/searchTabooWord.do"><spring:message code="master.page.administrator.search.taboo" /></a></li>
					<li><a href="newspaper/administrator/findTaboos.do"><spring:message code="master.page.all.newspapers.taboos" /></a></li>
					<li><a href="chirp/administrator/list.do"><spring:message code="master.page.all.chirps" /></a></li>
					<li><a href="configuration/administrator/display.do"><spring:message code="master.page.administrator.configuration" /></a></li>
					<li><a href="article/administrator/list.do"><spring:message code="master.page.administrator.articles" /></a></li>
					<li><a href="article/administrator/listTaboo.do"><spring:message code="master.page.administrator.articles.taboo" /></a></li>
					<li><a href="message/administrator/create.do"><spring:message code="master.page.administrator.broadcast" /></a></li>
					<li><a href="advertisement/administrator/list.do"><spring:message code="master.page.administrator.advertisement" /></a></li>
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('USER')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.user" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="newspaper/user/list.do"><spring:message code="master.page.user.newspapers" /></a></li>	
					<li><a href="volume/user/list.do"><spring:message code="master.page.user.volume" /></a></li>
					<li><a href="chirp/user/list.do"><spring:message code="master.page.all.chirps" /></a></li>	
					<li><a href="chirp/user/create.do"><spring:message code="master.page.create.chirps" /></a></li>	
					<li><a href="followUp/user/list.do"><spring:message code="master.page.user.followUp" /></a></li>
				    <li><a href="article/user/list.do"><spring:message code="master.page.article.list" /></a></li>
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('CUSTOMER')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.customer" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="newspaper/customer/list.do"><spring:message code="master.page.customer.newspapers" /></a></li>
					<li><a href="newspaper/customer/listForSubscribe.do"><spring:message code="master.page.customer.newspapersForSubscribe" /></a></li>
                    <li><a href="subscriptionNewspaper/customer/list.do"><spring:message code="master.page.subscription.list" /></a></li>	
                    <li><a href="subscriptionVolume/customer/list.do"><spring:message code="master.page.subscriptionVolume.list" /></a></li>														
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('AGENT')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.agent" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="newspaper/agent/listWithAdvertisements.do"><spring:message code="master.page.agent.newspapersLinked" /></a></li>
					<li><a href="newspaper/agent/listWithNoAdvertisements.do"><spring:message code="master.page.agent.newspapersNoLinked" /></a></li>
					<li><a href="advertisement/agent/list.do"><spring:message code="master.page.agent.advertisement" /></a></li>
				</ul>
			</security:authorize>
        </li>
        
        
        <li class="dropdown">
	        <security:authorize access="isAuthenticated()">		
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.profile" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="message/actor/list.do"><spring:message code="master.page.actor.messages" /></a></li>
					<li><a href="folder/actor/list.do"><spring:message code="master.page.actor.folders" /></a></li>
					<li><a href="newspaper/list.do"><spring:message code="master.page.all.newspapers" /></a></li>	
					<li><a href="volume/list.do"><spring:message code="master.page.all.volume" /></a></li>
					<li><a href="actor/user/list.do"><spring:message code="master.page.all.user" /></a></li>
			        <security:authorize access="hasRole('USER')">		
					<li><a href="actor/user/display.do"><spring:message code="master.page.profile" /></a></li>
					</security:authorize>
					<li><a href="actor/profile.do"><spring:message code="master.page.edit.profile" /></a></li>
					
				</ul>
			</security:authorize>
        </li>
       
      </ul>
      
      
      <div class="navbar-right navbar-btn btn-group">
      
	      <security:authorize access="isAuthenticated()">
	      	<a href="j_spring_security_logout" class="btn btn-primary"><spring:message code="master.page.logout"/></a>
	      </security:authorize>
	      
	      <security:authorize access="isAnonymous()">
	      	<a href="security/login.do" class="btn btn-primary"><spring:message code="master.page.login"/></a>
	      	<a href="actor/user/create.do" class="btn btn-primary"><spring:message code="master.page.user.create"/></a>
	      	<a href="actor/customer/create.do" class="btn btn-primary"><spring:message code="master.page.customer.create"/></a>
	      	<a href="actor/agent/create.do" class="btn btn-primary"><spring:message code="master.page.agent.create"/></a>
	      </security:authorize>
      
		  	<div class="btn-group">
		 <button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
		   <spring:message code="master.page.language" />
		   <span class="caret"></span>
		 </button>
		 <ul class="dropdown-menu">
		   <li><a href="?language=en"><spring:message code="master.page.language.english" /></a></li>
		   <li><a href="?language=es"><spring:message code="master.page.language.spanish" /></a></li>
		 </ul>
		</div>
      
		  
	  </div>
		
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
