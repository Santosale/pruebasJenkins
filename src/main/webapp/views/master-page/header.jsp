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
	  <a style="background-image: url(${banner}); -ms-behavior: url(styles/backgroundsize.min.htc);" class="navbar-brand" href="#"><span>${companyName}</span></a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">    
      <ul class="nav navbar-nav">
      	
      	<li><a href="welcome/index.do"><spring:message code="master.page.home" /></a></li>
      	
      	<security:authorize access="isAnonymous()">
			<li><a href="trip/list.do"><spring:message code="master.page.all.trips" /></a></li>
      		<li><a href="category/navigate.do"><spring:message code="master.page.category" /></a></li>
      	</security:authorize>
               
        <li class="dropdown">
        
        	<security:authorize access="hasRole('ADMIN')">
        		<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message	code="master.page.administrator" /> <span class="caret"></span></a>
        		<ul class="dropdown-menu">
					<li><a href="legalText/administrator/list.do"><spring:message code="master.page.admin.legal.text" /></a></li>
					<li><a href="tag/administrator/list.do"><spring:message code="master.page.admin.tag" /></a></li>
					<li><a href="configuration/administrator/display.do"><spring:message code="master.page.admin.configuration" /></a></li>
					<li><a href="dashboard/administrator/display.do"><spring:message code="master.page.admin.dashboard" /></a></li>
					<li><a href="notification/administrator/create.do"><spring:message code="master.page.admin.notification" /></a></li>	
					<li><a href="suspicious/administrator/searchSuspicious.do"><spring:message code="master.page.admin.search.suspicious" /></a></li>	
					<li><a href="suspicious/administrator/listSuspicious.do"><spring:message code="master.page.admin.suspicious" /></a></li>	
					<li><a href="actor/ranger/create.do"><spring:message code="master.page.create.ranger" /></a></li>
					<li><a href="actor/manager/create.do"><spring:message code="master.page.admin.create.manager" /></a></li>		
					<li><a href="actor/sponsor/create.do"><spring:message code="master.page.admin.create.sponsor" /></a></li>
					<li><a href="actor/auditor/create.do"><spring:message code="master.page.admin.create.auditor" /></a></li>		
							
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('SPONSOR')">
        		<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message	code="master.page.sponsor" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="sponsorship/sponsor/list.do"><spring:message code="master.page.sponsor.sponsorship" /></a></li>					
					<li><a href="creditcard/sponsor/list.do"><spring:message code="master.page.sponsor.explorer.creaditCard" /></a></li>	
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('AUDITOR')">
        		<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message	code="master.page.auditor" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="note/auditor/list.do"><spring:message code="master.page.auditor.note" /></a></li>					
					<li><a href="auditRecord/auditor/list.do"><spring:message code="master.page.auditor.audit" /></a></li>	
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('RANGER')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.ranger" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="curriculum/display.do"><spring:message code="master.page.ranger.curriculum" /></a></li>
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('EXPLORER')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.explorer" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="creditcard/explorer/list.do"><spring:message code="master.page.sponsor.explorer.creaditCard" /></a></li>
					<li><a href="application/explorer/list.do"><spring:message code="master.page.explorer.applications" /></a></li>						
					<li><a href="story/explorer/list.do"><spring:message code="master.page.explorer.story" /></a></li>
					<li><a href="contact/explorer/list.do"><spring:message code="master.page.explorer.contact" /></a></li>
					<li><a href="finder/explorer/search.do"><spring:message code="master.page.explorer.search" /></a></li>	
					<li><a href="finder/explorer/display.do"><spring:message code="master.page.explorer.search.view" /></a></li>					
				</ul>
			</security:authorize>
			
			<security:authorize access="hasRole('MANAGER')">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.manager" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="trip/manager/list.do"><spring:message code="master.page.manager.my.trips" /></a></li>
				</ul>
			</security:authorize>
			
        </li>
        
        <li class="dropdown">
	        <security:authorize access="isAuthenticated()">		
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.profile" /> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a href="message/create.do"><spring:message code="master.page.message" /></a></li>
					<li><a href="folder/list.do"><spring:message code="master.page.folder" /></a></li>
					<li><a href="socialidentity/list.do"><spring:message code="master.page.social.identity" /></a></li>
					<li><a href="actor/profile.do"><spring:message code="master.page.profile.edit" /></a></li>		
					<li><a href="trip/list.do"><spring:message code="master.page.all.trips" /></a></li>	
					<li><a href="category/navigate.do"><spring:message code="master.page.category" /></a></li>	
				</ul>
			</security:authorize>
        </li>
        
      </ul>
      
      <form class="navbar-form navbar-right" action="trip/list.do" method="GET">
        <div class="form-group">
          <input type="text" name="keyword" class="form-control" placeholder="<spring:message code="master.page.explorer.search" />">
        </div>
        <button type="submit" class="btn btn-default"><spring:message code="master.page.submit"/></button>
      </form>
      
      <div class="navbar-right navbar-btn btn-group">
      
	      <security:authorize access="isAuthenticated()">
	      	<a href="j_spring_security_logout" class="btn btn-primary"><spring:message code="master.page.logout"/></a>
	      </security:authorize>
	      
	      <security:authorize access="isAnonymous()">
	      	<a href="security/login.do" class="btn btn-primary"><spring:message code="master.page.login"/></a>
		   	<a href="actor/explorer/create.do" class="btn btn-primary"><spring:message code="master.page.create.explorer" /></a>
	 		<a href="actor/ranger/create.do" class="btn btn-primary"><spring:message code="master.page.create.ranger" /></a>
	     	
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
