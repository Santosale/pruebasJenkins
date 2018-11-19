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

<display:table class="table table-striped table-bordered table-hover" name="creditCards" id="row" requestURI="creditcard/user/list.do">
	
	<acme:columnLink action="edit" actor="user" domain="creditcard" id="${row.getId()}" />
	
	<jstl:if test="${row.getExpirationYear() - year > 0 }">
		<jstl:if test="${row.getId() != primaryCreditCard}">
			<acme:columnLink action="primary" domain="creditcard" id="${row.getId()}" actor="user" />
		</jstl:if>
		<jstl:if test="${row.getId() == primaryCreditCard}">
			<display:column>
				<p><spring:message code="creditcard.default" /></p>
			</display:column>
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${row.getExpirationYear() - year == 0 }">
		<jstl:if test="${row.getExpirationMonth() - month > 0}">
			<jstl:if test="${row.getId() != primaryCreditCard}">
				<acme:columnLink action="primary" domain="creditcard" id="${row.getId()}" actor="user" />
			</jstl:if>
			<jstl:if test="${row.getId() == primaryCreditCard}">
				<display:column>
					<p><spring:message code="creditcard.default" /></p>
				</display:column>
			</jstl:if>
		</jstl:if>
		<jstl:if test="${row.getExpirationMonth() - month <= 0}">
			<display:column>
				<p><spring:message code="creditcard.invalid" /></p>
			</display:column>
		</jstl:if>
	</jstl:if>
	
	<jstl:if test="${row.getExpirationYear() - year < 0}">
		<display:column>
			<spring:message code="creditcard.invalid" />
		</display:column>
	</jstl:if>
	
	<acme:column domain="creditcard" property="holderName"/>
	
	<acme:column domain="creditcard" property="brandName"/>
	
	<acme:column domain="creditcard" property="number"/>
	
	<acme:column domain="creditcard" property="expirationMonth"/>
	
	<acme:column domain="creditcard" property="expirationYear"/>

	<acme:column domain="creditcard" property="cvvcode"/>
	
	<acme:columnLink domain="creditcard" action="display" actor="user" url="creditcard/user/display.do?creditCardId=${row.getId()}"/>

</display:table> 
	
<acme:paginate pageNumber="${pageNumber}" url="creditcard/user/list.do" objects="${creditCards}" page="${page}"/>
<br><br>
<a class="btn btn-primary" href="creditcard/user/create.do"><spring:message code="creditcard.create" /></a>