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

<display:table class="table table-striped table-bordered table-hover" name="raffles" id="row" requestURI="${requestURI}">

	<jstl:if test="${requestURI.equals('raffle/moderator/list.do')}">
		<acme:columnLink domain="raffle" action="delete" id="${row.getId()}" actor="moderator" />
	</jstl:if>
	
	<jstl:if test="${requestURI.equals('raffle/company/list.do') && row.getWinner() == null}">
		<acme:columnLink domain="raffle" action="edit" id="${row.getId()}" actor="company" />
	</jstl:if>
	<jstl:if test="${requestURI.equals('raffle/company/list.do') && row.getWinner() != null}">
		<display:column><p>Cannot be edited</p></display:column>
	</jstl:if>

	<acme:column domain="raffle" property="title"/>
	
	<acme:column domain="raffle" property="description"/>
	
	<acme:column domain="raffle" property="productName" />

	<acme:column domain="raffle" property="maxDate" formatDate="true" sortable="true"/>
	
	<acme:column domain="raffle" property="${row.getPrice()}" code="price" formatPrice="true" codeSymbol1="raffle.currency.english" codeSymbol2="raffle.currency.spanish" sortable="true" />

	<jstl:if test="${!requestURI.equals('raffle/list.do')}">
		<jstl:if test="${row.getWinner() != null}">
			<acme:columnLink domain="raffle" code="winner" content="${row.getWinner().getName()} ${row.getWinner().getSurname()}" url="actor/user/display.do?userId=${row.getWinner().getId()}" />
		</jstl:if>
		<jstl:if test="${row.getWinner() == null}">
			<spring:message code='raffle.winner' var="winnerHeader"/>
			<display:column title="${winnerHeader}"><spring:message code="raffle.notRaffledYet" /></display:column>
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${!requestURI.equals('raffle/company/list.do')}">
		<acme:columnLink domain="raffle" code="company" content="${row.getCompany().getName()} ${row.getCompany().getSurname()}" url="actor/company/profile.do?actorId=${row.getCompany().getId()}" />
	</jstl:if>
	
	<acme:columnLink domain="raffle" action="display" id="${row.getId()}" />

</display:table>

<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${raffles}" page="${page}"/>

<br><br>

<jstl:if test="${requestURI.equals('raffle/company/list.do')}">
	<a href="raffle/company/create.do" class="btn btn-primary"><spring:message code="raffle.create" /></a>
</jstl:if>