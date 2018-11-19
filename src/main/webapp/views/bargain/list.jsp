<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<display:table class="table table-striped table-bordered table-hover" name="bargains" id="row" requestURI="${requestURI}">

	<jstl:if test="${requestURI.equals('bargain/moderator/list.do')}">
		<acme:columnLink domain="bargain" action="delete" id="${row.getId()}" actor="moderator" />
	</jstl:if>
	<jstl:if test="${requestURI.equals('bargain/company/list.do')}">
		<acme:columnLink domain="bargain" action="edit" id="${row.getId()}" actor="company" />
	</jstl:if>
	
	<jstl:if test="${requestURI.equals('bargain/sponsor/list.do')}">
		<spring:url value="sponsorship/sponsor/create.do" var="url">
			<spring:param name="bargainId" value="${row.getId()}" />
		</spring:url>
		<display:column>
			<a href="${url}"><spring:message code="sponsorship.create" /></a>
		</display:column>
	</jstl:if>

	<acme:columnLink domain="bargain" code="product.name" content="${row.getProductName()}" url="${row.getProductUrl()}"/>
	
	<acme:column domain="bargain" property="description"/>
	
	<!-- Mostramos el precio-->
	<jstl:if test="${plan==null}">
		<acme:column domain="bargain" property="${row.getPrice()}" code="price" formatPrice="true" codeSymbol1="bargain.var1" codeSymbol2="bargain.var2" />
	</jstl:if>
	
	<jstl:if test="${plan!=null && plan.getName().equals('Gold Premium')}">
		<acme:column domain="bargain" property="${row.getPrice() - (row.getPrice() * 0.075)}" code="price" formatPrice="true" codeSymbol1="bargain.var1" codeSymbol2="bargain.var2" />
	</jstl:if>
	
	<jstl:if test="${plan!=null && plan.getName().equals('Basic Premium')}">
		<acme:column domain="bargain" property="${row.getPrice() - (row.getPrice() * 0.025)}" code="price" formatPrice="true" codeSymbol1="bargain.var1" codeSymbol2="bargain.var2" />
	</jstl:if>
	
	<!-- Enlace al display de chollos -->
	<spring:message code="bargain.display" var="titleDisplay"></spring:message>
	
	
		
	<!-- Si no está publicado -->
	<jstl:if test="${requestURI.equals('bargain/list.do') || requestURI.equals('bargain/bytag.do') || requestURI.equals('bargain/bycategory.do')}">
		<jstl:if test="${!row.getIsPublished() && isSponsor}">
				<display:column title="${titleDisplay}"></display:column>
		</jstl:if>
		
		<!-- Si no esta publicado pero no es sponsor -->		
		<jstl:if test="${!row.getIsPublished() && isSponsor==false}">
			<acme:columnLink domain="bargain" action="display" id="${row.getId()}" />
		</jstl:if>
		
		<!-- Si está publicado todos lo ven -->		
		<jstl:if test="${row.getIsPublished()}">
			<acme:columnLink domain="bargain" action="display" id="${row.getId()}" />
		</jstl:if>
	</jstl:if>
	
	<!-- /Enlace al display de chollos -->
	
	<jstl:if test="${requestURI.equals('bargain/moderator/list.do') || requestURI.equals('bargain/company/list.do')}">
		<acme:columnLink domain="bargain" action="display" id="${row.getId()}" />
	</jstl:if>
	
</display:table>

<jstl:if test="${requestURI.equals('bargain/bytag.do')}">
	<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${bargains}" page="${page}" parameter="tagId" parameterValue="${tagId}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('bargain/bycategory.do')}">
	<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${bargains}" page="${page}" parameter="categoryId" parameterValue="${categoryId}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('category/company/addCategory.do') || requestURI.equals('category/company/removeCategory.do')}">
	<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${bargains}" page="${page}" parameter="bargainId" parameterValue="${bargainId}"/>
</jstl:if>


<jstl:if test="${!requestURI.equals('bargain/bytag.do') && !requestURI.equals('bargain/bycategory.do') && !requestURI.equals('category/company/addCategory.do') && !requestURI.equals('category/company/removeCategory.do') && !requestURI.equals('actor/user/wishlist.do')}">
	<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${bargains}" page="${page}" parameter="categoryId" parameterValue="${categoryId}"/>
</jstl:if>

<jstl:if test="${requestURI.equals('actor/user/wishlist.do')}">
	<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${bargains}" page="${page}" parameter="actorId" parameterValue="${actorId}"/>
</jstl:if>

<br><br>

<jstl:if test="${requestURI.equals('bargain/company/list.do')}">
	<acme:displayLink parameter="companyId" code="bargain.create" action="category/company/createBargain.do" parameterValue="${bargain.getCompany().getId()}" css="btn btn-primary"></acme:displayLink>
</jstl:if>

