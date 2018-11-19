<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<jstl:if test="${requestURI.equals('newspaper/list.do') || requestURI.equals('newspaper/listSearch.do')}">

	<form class="navbar-form navbar-right" action="newspaper/listSearch.do" method="GET">
        <div class="form-group">
          	<input type="text" name="keyword" class="form-control" placeholder="<spring:message code="newspaper.search" />">
        </div>
        <button type="submit" class="btn btn-default"><spring:message code="master.page.submit"/></button>
     </form>
</jstl:if>
<display:table class="table table-striped table-bordered table-hover" name="newspapers" id="row">
	<jsp:useBean id="currentMomentVar" class="java.util.Date"/>
	
<jstl:if test="${requestURI.equals('newspaper/user/list.do')}">
	
	<security:authorize access="hasRole('USER')">
		<display:column>
		<jstl:if test="${row.getPublicationDate()>currentMomentVar}">
			<a href="newspaper/user/editDate.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.editDate" />
			</a>
				</jstl:if>
			<jstl:if test="${row.getPublicationDate()<=currentMomentVar}">
			<spring:message code="newspaper.noEdit" />  
			</jstl:if>			
		
		</display:column>
		
		<display:column>
		<jstl:if test="${row.getIsPrivate()==true}">
			<a href="newspaper/user/putPublic.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.public" />
			</a>
		</jstl:if>
		
		<jstl:if test="${row.getIsPrivate()==false}">
			<a href="newspaper/user/putPrivate.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.private" />
			</a>
		</jstl:if>	
		
		</display:column>
				
	</security:authorize>	
</jstl:if>

	<jstl:if test="${requestURI.equals('newspaper/user/listPublished.do')}">
		<display:column>
				<a href="volume/user/create.do?newspaperId=${row.getId()}"> <spring:message
						code="newspaper.volume.create" />
				</a>
		
		</display:column>
	</jstl:if>


	<security:authorize access="hasRole('USER')">
	<jstl:if test="${requestURI.equals('newspaper/user/addNewspaper.do') || requestURI.equals('newspaper/user/deleteNewspaper.do')}">
		<display:column>
			<jstl:if test="${requestURI.equals('newspaper/user/addNewspaper.do')}">
			<spring:url var="urlAddNewspaper" value="volume/user/addNewspaper.do">
						<spring:param name="newspaperId" value="${row.getId()}" />
						<spring:param name="volumeId" value="${volumeId}" />
					</spring:url>
		<a href="${urlAddNewspaper}"> <spring:message
					code="newspaper.volume.add" />
			</a>
		
			</jstl:if>
			
			<jstl:if test="${requestURI.equals('newspaper/user/deleteNewspaper.do')}">
			<spring:url var="urlDeleteNewspaper" value="volume/user/deleteNewspaper.do">
						<spring:param name="newspaperId" value="${row.getId()}" />
						<spring:param name="volumeId" value="${volumeId}" />
					</spring:url>
		<a href="${urlDeleteNewspaper}"> <spring:message
					code="newspaper.volume.delete" />
			</a>
		
			</jstl:if>
		</display:column>
	</jstl:if>	
<jstl:if test="${requestURI.equals('newspaper/user/list.do') || requestURI.equals('newspaper/list.do') }">	
	<display:column>
		<jstl:if test="${ row.getIsPublished()==false || row.getPublicationDate()> currentMomentVar}">
			<a href="article/user/create.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.create.article" />
			</a>
				</jstl:if>	
		<jstl:if test="${ row.getIsPublished()==true && row.getPublicationDate()<= currentMomentVar}">
			<spring:message code="newspaper.noCreateArticle" />  
		</jstl:if>	
		
		</display:column>
</jstl:if>	
		
	</security:authorize>
	
<security:authorize access="hasRole('CUSTOMER')">
	<jstl:if test="${requestURI.equals('newspaper/customer/listForSubscribe.do')}">	
		<display:column>
			<a href="subscriptionNewspaper/customer/create.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.create.subscription" />
			</a>
		</display:column>
	</jstl:if>	
</security:authorize>
	
	<security:authorize access="hasRole('AGENT')">
		<jstl:if test="${requestURI.equals('newspaper/list.do')||requestURI.equals('newspaper/listSearch.do')}">
		<display:column>
			<a href="advertisement/agent/listLink.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.advertisement.unLink" />
			</a>
		</display:column>
		
		<display:column>
			<a href="advertisement/agent/listUnlink.do?newspaperId=${row.getId()}"> <spring:message
					code="newspaper.advertisement.link" />
			</a>
		</display:column>
		</jstl:if>	
	</security:authorize>
	
			
	<acme:column property="title" domain="newspaper" />
	
	<acme:column property="description" domain="newspaper" />
	
	<acme:column property="publicationDate" domain="newspaper" formatDate="true" />
	
	<spring:message code="newspaper.isPublished" var="newspaperIsPublished"/>
	<display:column title="${newspaperIsPublished}">
		<jstl:if test="${ row.getIsPublished()==true && row.getPublicationDate()<=currentMomentVar}">
			<jstl:out value="X"/>
		</jstl:if>	
	</display:column>
	
	<spring:message code="newspaper.isPrivateTheNewspaper" var="newspaperIsPrivate"/>
	<display:column title="${newspaperIsPrivate}">
		<jstl:if test="${ row.getIsPrivate()==true}">
			<jstl:out value="X"/>
		</jstl:if>	
	</display:column>
	
			
	<jstl:set var="canPermit" value="false"/>
	
	
	<security:authorize access="hasRole('USER')">
	<security:authentication var="principal" property="principal.username"/>
	<jstl:if test="${row.getPublisher().getUserAccount().getUsername().equals(principal) || (row.getPublicationDate()<= currentMomentVar && row.getIsPublished()==true)}">
		<jstl:set var="canPermit" value="true"/>
	</jstl:if>
	</security:authorize>
	
	<security:authorize access="hasRole('CUSTOMER')">
	<security:authentication var="principal" property="principal.username"/>
	<jstl:if test="${row.getPublicationDate()<= currentMomentVar && row.getIsPublished()==true}">
		<jstl:set var="canPermit" value="true"/>
	</jstl:if>
	
	</security:authorize>
	
	<security:authorize access="hasRole('AGENT')">
	<security:authentication var="principal" property="principal.username"/>
	<jstl:if test="${row.getPublicationDate()<= currentMomentVar && row.getIsPublished()==true}">
		<jstl:set var="canPermit" value="true"/>
	</jstl:if>
	
	</security:authorize>
	
	<security:authorize access="hasRole('ADMIN')">
		<jstl:set var="canPermit" value="true"/>
	</security:authorize>
	
	<security:authorize access="isAnonymous()">
		<jstl:if test="${row.getPublicationDate()<=currentMomentVar && row.getIsPublished() == true && row.getIsPrivate() == false}">
			<jstl:set var="canPermit" value="true"/>
		</jstl:if>
	</security:authorize>
	
	
			
	<spring:url var="urlDisplay" value="newspaper/display.do">
		<spring:param name="newspaperId" value="${row.getId()}" />
		
	</spring:url>
	
	<display:column>
		<jstl:if test="${canPermit==true}">	
			<a href="${urlDisplay }"> <spring:message code="newspaper.display" /></a>
		</jstl:if>
		<jstl:if test="${ canPermit==false}">
			<spring:message code="newspaper.noDisplay" />  
		</jstl:if>	
	</display:column>
		
</display:table>

<jstl:if test="${requestURI.equals('newspaper/user/list.do') || requestURI.equals('newspaper/user/listPublished.do') || requestURI.equals('newspaper/customer/list.do') || requestURI.equals('newspaper/list.do') || requestURI.equals('newspaper/customer/listForSubscribe.do') || requestURI.equals('newspaper/administrator/findTaboos.do') || requestURI.equals('newspaper/administrator/listMoreAverage.do') || requestURI.equals('newspaper/administrator/listFewerAverage.do') || requestURI.equals('newspaper/agent/listWithAdvertisements.do') || requestURI.equals('newspaper/agent/listWithNoAdvertisements.do')   }">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${newspapers}" page="${page}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('newspaper/listSearch.do')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${newspapers}" page="${page}" parameter="volumeId" parameterValue="${volumeId}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('newspaper/user/addNewspaper.do') || requestURI.equals('newspaper/user/deleteNewspaper.do')}">
		<acme:paginate pageNumber="${pageNumber }" url="${requestURI }" objects="${newspapers}" page="${page}" parameter="volumeId" parameterValue="${volumeId}"/>
</jstl:if>


<security:authorize access="hasRole('USER')">
<jstl:if test="${requestURI.equals('newspaper/user/list.do')}">
	<div>
	<br>
		<a href="newspaper/user/create.do">
			<spring:message code="newspaper.create" />
		</a>
	</div>
</jstl:if>	
</security:authorize>