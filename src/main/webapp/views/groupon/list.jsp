<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table class="table table-striped table-bordered table-hover" name="groupons" id="row">

<jstl:if test="${requestURI.equals('groupon/user/list.do')}">
<acme:columnLink action="edit" domain="groupon" id="${row.getId()}" actor="user" />
</jstl:if>

<jstl:if test="${requestURI.equals('groupon/moderator/list.do')}">
		<security:authorize access="hasRole('MODERATOR')">
			<acme:columnLink action="delete" domain="groupon" id="${row.getId()}" actor="moderator" />
		</security:authorize>
</jstl:if>
	<acme:column domain="groupon" property="title"/>
	
	<acme:column domain="groupon" property="description"/>
	
	<acme:column domain="groupon" property="productName"/>
	
	<acme:columnLink domain="groupon" action="productUrl" url="${row.getProductUrl()}"/>
	
	<acme:column domain="groupon" property="minAmountProduct"/>
	
	<acme:column property="maxDate" domain="groupon" formatDate="true" />
	
	<acme:column domain="groupon" property="${row.getOriginalPrice()}" code="originalPrice" formatPrice="true" codeSymbol1="groupon.currency.english" codeSymbol2="groupon.currency.spanish" />
			
	<acme:column domain="groupon" property="${row.getPrice()}" code="price" formatPrice="true" codeSymbol1="groupon.currency.english" codeSymbol2="groupon.currency.spanish" />
			
	<acme:columnLink action="display" domain="groupon" id="${row.getId()}"/>
	
	
</display:table> 
	
<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${groupons}" page="${page}"/>

<jstl:if test="${requestURI.equals('groupon/user/list.do')}">
	<acme:displayLink code="groupon.create" action="groupon/user/create.do" css="btn btn-primary"/>		
</jstl:if>	

