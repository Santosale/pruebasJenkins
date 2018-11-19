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

<display:table class="table table-striped table-bordered table-hover" name="comments" id="row" requestURI="${requestURI}">
	
	<jstl:if test="${mapCommentBoolean[row]}">
		<jstl:set value='background-color: #ec6354;color: white;' var="style"/>
	</jstl:if>
	<jstl:if test="${!mapCommentBoolean[row]}">
		<jstl:set value='' var="style"/>
	</jstl:if>
	
	<jstl:if test="${requestURI == 'comment/moderator/list.do'}">
		<acme:columnLink style="${style}" action="edit" actor="moderator" domain="comment" id="${row.getId()}" />
	</jstl:if>
	
	<jstl:if test="${requestURI == 'comment/user/list.do'}">
		<acme:columnLink style="${style}" action="edit" actor="user" domain="comment" id="${row.getId()}" />
	</jstl:if>
	
	<acme:column style="${style}" property="moment" domain="comment" formatDate="true"/>
	
	<acme:column style="${style}" property="text" domain="comment" />
	
	<acme:columnLink style="${style}" code="name" url="actor/user/display.do?userId=${row.getUser().getId()}" domain="comment.user" content="${row.getUser().getName()} ${row.getUser().getSurname()}"/>
		
	<acme:columnLink style="${style}" code="productName" url="bargain/display.do?bargainId=${row.getBargain().getId()}" domain="comment.bargain" content="${row.getBargain().getProductName()}"/>
		
	<acme:columnLink style="${style}" action="display" domain="comment" id="${row.getId()}" />

</display:table>

<acme:paginate url="${requestURI}" objects="${comments}" pageNumber="${pageNumber}" page="${page}" />
