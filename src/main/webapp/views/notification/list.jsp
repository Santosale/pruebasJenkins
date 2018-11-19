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

<display:table class="table table-striped table-bordered table-hover" name="notifications" id="row" requestURI="notification/actor/list.do">

	
	<jstl:if test="${row.isVisited()}">
		<acme:columnLink style="background:#E8EAF6;" domain="notification" action="delete" id="${row.getId()}" actor="actor" />
		<acme:column style="background:#E8EAF6;" domain="notification" property="subject"/>
		<acme:columnLink style="background:#E8EAF6;" domain="notification" action="display" id="${row.getId()}" actor="actor" />
	</jstl:if>
	
	<jstl:if test="${!row.isVisited()}">
		<acme:columnLink  style="background:#fff;" domain="notification" action="delete" id="${row.getId()}" actor="actor" />
		<acme:column style="background:#fff;" domain="notification" property="subject"/>
		<acme:columnLink style="background:#fff;" domain="notification" action="display" id="${row.getId()}" actor="actor" />
	</jstl:if>
	
	
	
</display:table>

<acme:paginate pageNumber="${pageNumber}" url="notification/actor/list.do" objects="${notifications}" page="${page}"/>
