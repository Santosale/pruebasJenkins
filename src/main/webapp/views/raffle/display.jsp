<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="today" class="java.util.Date" />

<div class="container">

	<security:authorize access="hasRole('MODERATOR')">
		<jstl:if test="${raffle.getWinner() == null && raffle.getMaxDate() < (today) && hasTicketsSelled}">
			<acme:displayLink code="raffle.toraffle" action="raffle/moderator/toraffle.do" parameter="raffleId" parameterValue="${raffle.getId()}" css="btn btn-primary" />
		</jstl:if>
		<jstl:if test="${raffle.getWinner() != null && raffle.getMaxDate()>=(today)}">
			<p><spring:message code="raffle.alreadyRaffled" /></p>
		</jstl:if>
	</security:authorize>

	<acme:share url="${url}" message="Share it" />

	<h3><jstl:out value="${raffle.getTitle()}" /></h3>
	
	<p><jstl:out value="${raffle.getDescription()}" /></p>
	
	<jstl:if test="${raffle.getProductUrl() != null && !raffle.getProductUrl().equals('')}">
		<p><b><spring:message code="raffle.productName" />:</b> <a href="${raffle.getProductUrl()}"><jstl:out value="${raffle.getProductName()}" /></a></p>
	</jstl:if>
	
	<jstl:if test="${raffle.getProductUrl() == null || raffle.getProductUrl().equals('')}">
		<p><b><spring:message code="raffle.productName" />:</b> <jstl:out value="${raffle.getProductName()}" /></p>
	</jstl:if>
	
	<acme:display code="raffle.maxDate" value="${raffle.getMaxDate()}" codeMoment="raffle.format.moment" />
	
	<p style="font-size: 25px; text-align:center"><spring:message code="welcome.currency.english" /><fmt:formatNumber value="${raffle.getPrice()}" currencySymbol="" type="number" minFractionDigits="2" maxFractionDigits="2"/><spring:message code="welcome.currency.spanish" /></p>

	<div style="display:flex">
		<c:forEach items="${raffle.getProductImages()}" var="image">
			<div style="display: inline-block; background-position: center; width:calc(100%/${raffle.getProductImages().size()}); height: 300px; background-image: url(${image}); background-size: cover;"></div>
		</c:forEach>
	</div>
	
	<security:authorize access="hasRole('USER')">
		<br>
		<div style="text-align: center">
			<jstl:if test="${raffle.getWinner() == null}">
				<jstl:if test="${raffle.getPrice() != 0.0}">
					<a class="btn btn-primary" href="#" data-toggle="modal" data-target="#myModal"><spring:message code="raffle.buy.paypal" /></a>
					<a class="btn btn-primary" href="ticket/user/buy.do?raffleId=${raffle.getId()}"><spring:message code="raffle.buy.creditcard" /></a>
				</jstl:if>
				<jstl:if test="${raffle.getPrice() == 0.0 && notBuyedYet}">
					<a class="btn btn-primary" href="ticket/user/buy.do?raffleId=${raffle.getId()}"><spring:message code="welcome.buy"/></a>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${raffle.getWinner() != null}">
				<p><spring:message code="raffle.isHasBeenRaffled" /></p>
				<p><b><jstl:out value="${raffle.getWinner().getName()}" /> <jstl:out value="${raffle.getWinner().getSurname()}" /></b></p>
			</jstl:if>
		</div>
		
		<jstl:if test="${raffle.getPrice() != 0.0}">
			<!-- Modal -->
			<div class="modal fade" id="myModal" tabindex="-1">
			  <div class="modal-dialog">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
			        <h4 class="modal-title" id="myModalLabel"><spring:message code="welcome.amountTicketToBuy" /></h4>
			      </div>
		          <form id="buyTicket" action="ticket/user/buy.do" method="GET">
				      <div class="modal-body">
				        	<input type="hidden" name="raffleId" value="${raffle.getId()}" />
				        	<input type="hidden" name="method" value="PAYPAL" />
				        	<div class="from-group">
				        		<input class="form-control" id="amount" type="number" name="amount" min="1" />
				        	</div>
				 			<br>
				        	<p id="amountZero" style="display:none; color: red; font-weight: bold"><spring:message code="raffle.amountZero" /></p>
				        	<script>
				        		$("#buyTicket").submit(function(e) {
				        			res = true;
				        			if($("input[name='amount']").val() == 0 || $("input[name='amount']").val() == "" || $("input[name='amount']").val() == null){
										res = false;
										$("#amountZero").fadeIn();
				        			}
				        			return res;
				        		});
				        	</script>
				      </div>
				      <div class="modal-footer">
				        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="welcome.cancel" /></button>
				        <button type="submit" class="btn btn-primary"><spring:message code="welcome.buy" /></button>
				      </div>
		          </form>
			    </div>
			  </div>
			</div>
		</jstl:if>
	</security:authorize>
	
	<small><spring:message code="welcome.offeredBy" />: <a href="actor/company/profile.do?actorId=${raffle.getCompany().getId()}"><jstl:out value="${raffle.getCompany().getCompanyName()}" /></a></small><br><br>

</div>