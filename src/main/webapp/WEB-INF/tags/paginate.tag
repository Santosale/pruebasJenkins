<%--
 * submit.tag
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@ tag language="java" body-content="empty"%>

<%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<%-- Attributes --%>

<%@ attribute name="url" required="true"%>
<%@ attribute name="page" required="true"%>
<%@ attribute name="pageNumber" required="true"%>
<%@ attribute name="parameter" required="false"%>
<%@ attribute name="parameterValue" required="false"%>
<%@ attribute name="parameter2" required="false"%>
<%@ attribute name="parameterValue2" required="false"%>
<%@ attribute name="objects" required="false" type="java.lang.Object"%>

<%-- Definition --%>

<jstl:if test="${objects.size() > 0}">

	<nav>
		<ul class="pagination">
		
			<spring:url var="urlFirstPage" value="${url}">
				<jstl:if test="${parameter!=null && parameterValue!=null}">
					<spring:param name="${parameter}" value="${parameterValue}" />
				</jstl:if>
				<jstl:if test="${parameter2!=null && parameterValue2!=null}">
					<spring:param name="${parameter2}" value="${parameterValue2}" />
				</jstl:if>
				<spring:param name="page" value="1" />
			</spring:url>
			
			<jstl:if test="${pageNumber > 1}">
				<li><a href='<jstl:out value="${urlFirstPage}" />'> <span>&laquo;</span></a></li>
			</jstl:if>
			
			<jstl:forEach var="i" begin="1" end="${pageNumber}">

				<spring:url var="urlNextPage" value="${url}">
					<jstl:if test="${parameter!=null && parameterValue!=null}">
						<spring:param name="${parameter}" value="${parameterValue}" />
					</jstl:if>
					<jstl:if test="${parameter2!=null && parameterValue2!=null}">
						<spring:param name="${parameter2}" value="${parameterValue2}" />
					</jstl:if>
					<spring:param name="page" value="${i}" />
				</spring:url>
				
				<spring:url var="urlLastPage" value="${url}">
					<jstl:if test="${parameter!=null && parameterValue!=null}">
						<spring:param name="${parameter}" value="${parameterValue}" />
					</jstl:if>
					<jstl:if test="${parameter2!=null && parameterValue2!=null}">
						<spring:param name="${parameter2}" value="${parameterValue2}" />
					</jstl:if>
					<spring:param name="page" value="${pageNumber}" />
				</spring:url>


				<jstl:if test="${page==i}">
					<li class="active"><a href="${urlNextPage}"><jstl:out value="${i}"></jstl:out></a></li>
				</jstl:if>
				<jstl:if test="${page!=i}">
					<li><a href="${urlNextPage}"><jstl:out value="${i}"></jstl:out></a></li>
				</jstl:if>

			</jstl:forEach>
			<jstl:if test="${pageNumber > 1}">
				<li><a href="${urlLastPage}"><span>&raquo;</span></a></li>
			</jstl:if>
		</ul>
	</nav>


</jstl:if>
