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
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>


<jstl:if test="${canSeeArticles==true }">
	<jstl:if test="${newspaper.getPicture()!=null && newspaper.getPicture()!='' }">
		<img src="${newspaper.getPicture()}" alt="Picture" width="400px" height="200px" style="margin-left:15px;" />
	</jstl:if>
</jstl:if>	

	<br>
	
	<div>
	
		<jstl:if test="${canSeeArticles==true }">
		
			<div class="container">
				
				<acme:display code="newspaper.title" value="${newspaper.getTitle()}"/>
				
				<acme:display code="newspaper.description" value="${newspaper.getDescription()}"/>
						
				<acme:display code="newspaper.publicationDate" value="${newspaper.getPublicationDate()}" codeMoment="newspaper.format.moment"/>
							
			</div>
			
		</jstl:if>
	
		<security:authorize access="hasRole('ADMIN')">
			<div class="container">	
			<span style="font-size:20px"><spring:message code="newspaper.admin.actions"/></span>	
			<acme:displayLink parametre="newspaperId" code="newspaper.delete" action="newspaper/administrator/delete.do" parametreValue="${newspaper.getId()}"/>
			</div>
		</security:authorize>
		
	
			<jstl:set var="num" value="0"/>													
			<jstl:if test="${!articles.isEmpty()}">
		
			<div>
				<span style="font-size:20px"><spring:message code="newspaper.articles"></spring:message></span>
				<br>
				<br>
				<jstl:forEach var="row" items="${articles}">
				
					<div class="container-square2" style="border:2px solid black; margin-left:25px; margin-bottom:20px; padding:10px;">
					<jstl:if test="${canSeeArticles==true }">
						<span class="display"><spring:message code="newspaper.article.title"/></span><a href="article/display.do?articleId=${row.getId()}"> <jstl:out value="${row.getTitle()}"/> </a>
						<br>
						<span class="display"><spring:message code="newspaper.article.writer"/></span><a href="actor/user/display.do?userId=${row.getWriter().getId()}"><jstl:out value="${row.getWriter().getName()}"/> <jstl:out value="${row.getWriter().getSurname()}"/></a>
						<br>
					</jstl:if>
					<jstl:if test="${canSeeArticles==false }">
					<span class="display"><spring:message code="newspaper.article.title"/></span><jstl:out value="${row.getTitle()}" />
						<br>
					<span class="display"><spring:message code="newspaper.article.writer"/></span><jstl:out value="${row.getWriter().getName()}"/>
						<br>
					</jstl:if>
					<jstl:if test="${row.getSummary().length()<10}">
						<p><span class="display"><spring:message code="newspaper.article.summary"/></span><jstl:out value="${row.getSummary()}"/></p>
						</jstl:if>
						<jstl:if test="${row.getSummary().length()>=10 }">
						<p>
							<span class="display"><spring:message code="newspaper.article.summary"/></span>
							<span id="shortText" onclick="showHideText(${num});"><jstl:out value="${row.getSummary().substring(0, 10)}"/><span id="puntosuspensivos${num}">...</span><span id="fullText${num }" style="display: none">${row.getSummary().substring(10)}</span></span>
						</p>
						<script>
						function showHideText (num) {
						    var x = document.getElementById("fullText"+num);
						    var puntosuspensivos = document.getElementById("puntosuspensivos"+num);

						    if (x.style.display === "none") {
						    	puntosuspensivos.style.display = "none";
						        x.style.display = "inline";
						    } else {
						        x.style.display = "none";
						    	puntosuspensivos.style.display = "inline";
						    }
						}
						</script>
						</jstl:if>
						</div>
					<br>
					<jstl:set var="num" value="${num+1 }"/>													
				</jstl:forEach>
				
			<acme:paginate pageNumber="${pageNumber }" url="newspaper/display.do" objects="${articles}" page="${page }" parameter="newspaperId" parameterValue="${newspaper.getId()}"/>
			
			</div>
		</jstl:if>
			

		
	</div>