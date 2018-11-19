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

<display:table class="table table-striped table-bordered table-hover" name="tickets" id="row" requestURI="${requestURI}">

	<acme:column domain="ticket" property="code"/>

	<jstl:if test="${showRaffle == true}">
		<acme:columnLink domain="ticket" 
						code="raffle" 
						content="${row.getRaffle().getTitle()}" 
						url="raffle/display.do?raffleId=${row.getRaffle().getId()}" />
	</jstl:if>
	
	<spring:message code="ticket.payMethod" var="payMethodTitle" />
	<jstl:if test="${row.getRaffle().getPrice() != 0}">
		<display:column title="${payMethodTitle}">
			<jstl:if test="${row.getCreditCard() != null}">
				<spring:message code="ticket.creditcard" />
			</jstl:if>
			<jstl:if test="${row.getCreditCard() == null}">
				<spring:message code="ticket.paypal" />
			</jstl:if>
		</display:column>
		
		<jstl:if test="${row.getCreditCard() != null}">
			<acme:columnLink domain="ticket" 
							code="creditcard" 
							content="${row.getCreditCard().getNumber()}" 
							url="creditcard/user/display.do?creditCardId=${row.getCreditCard().getId()}" />
		</jstl:if>
		<jstl:if test="${row.getCreditCard() == null}">
			<spring:message code="ticket.creditcard" var="creditCardHeader" />
			<display:column title="${creditCardHeader}">
			</display:column>
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${row.getRaffle().getPrice() == 0}">
		<display:column title="${payMethodTitle}"></display:column>
	</jstl:if>
		
</display:table>

<acme:paginate pageNumber="${pageNumber}" url="${requestURI}" objects="${tickets}" page="${page}"/>