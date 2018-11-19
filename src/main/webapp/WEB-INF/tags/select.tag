<%--
 * select.tag
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@ tag language="java" body-content="empty" %>

<%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%-- Attributes --%> 

<%@ attribute name="path" required="true" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="items" required="false" type="java.util.Collection" %>
<%@ attribute name="itemLabel" required="false" %>
<%@ attribute name="hideFirstOption" required="false" type="java.lang.Boolean"%>
<%@ attribute name="object" required="false" type="java.lang.Object"%>

<%@ attribute name="option" required="false" %>
<%@ attribute name="option2" required="false" %>
<%@ attribute name="option3" required="false" %>
<%@ attribute name="option4" required="false" %>
<%@ attribute name="selectPattern" required="false" type="java.lang.Boolean" %>


<%@ attribute name="selected" required="false" %>

<%@ attribute name="id" required="false" %>
<%@ attribute name="onchange" required="false" %>

<jstl:if test="${id == null}">
	<jstl:set var="id" value="${UUID.randomUUID().toString()}" />
</jstl:if>

<jstl:if test="${onchange == null}">
	<jstl:set var="onchange" value="javascript: return true;" />
</jstl:if>

<%-- Definition --%>

<jstl:if test="${hideFirstOption == null || hideFirstOption == false}">

<jstl:if test="${path == 'creditCard'}">


	<div class="form-group"> 
		<form:label path="${path}">
			<spring:message code="${code}" />:
		</form:label>
		<form:select id="selectCreditCardId" path="${path}" class="form-control">
      		<jstl:if test="${object.getId()==0 }">
      			<option value="0">----</option>
      		</jstl:if>
      		<jstl:if test="${object.getId()!=0 }">
      			<option value="${object.getCreditCard().getId() }">${object.getCreditCard().getNumber() }</option>
      		</jstl:if>	
      		<jstl:forEach items="${items}" var="cc">
				<option value="${cc.getId()}" ${cc.getId() == selected ? 'selected' : ''} >${cc.number}</option>
        	</jstl:forEach>
		</form:select>
		<form:errors class="text-danger" path="${path}" />
	</div>

</jstl:if>

<jstl:if test="${path != 'creditCard' && itemLabel != null && selectPattern == null}">
	<div class="form-group">
		<form:label path="${path}">
			<spring:message code="${code}" />
		</form:label>	
		<form:select  class="form-control" id="${id}" path="${path}" onchange="${onchange}">
			<form:option value="0" label="----" />		
			<form:options items="${items}" itemValue="id" itemLabel="${itemLabel}" />
		</form:select>
		<form:errors path="${path}" class="text-danger" />
	</div>
</jstl:if>

<jstl:if test="${selectPattern == null }">
	<jstl:if test="${option != null || option2 != null || option3 != null || option4 != null}">
		<div class="form-group">
			<form:label path="${path}">
				<spring:message code="${code}" />
			</form:label>	
			<form:select id="${id}" path="${path}" onchange="${onchange}" class="form-control">
				<form:option value="0" label="----" />		
				<form:option label="${option}" value="${option}" />
				<form:option label="${option2}" value="${option2}" />
				<form:option label="${option3}" value="${option3}" />
				<form:option label="${option4}" value="${option4}" />
			</form:select>
			<form:errors path="${path}" class="text-danger" />
		</div>
	</jstl:if>
</jstl:if>	

<jstl:if test="${selectPattern == true }">
		<div class="form-group">
			<form:label path="${path}">
				<spring:message code="${code}" />
			</form:label>	
			<form:select path="${path}" class="form-control">
				<form:option value="0" label="----" />		
				<form:option label="${option}" value="${option}" />
				<jstl:if test="${option2 != null }">
					<form:option label="${option2}" value="${option2}" />
				</jstl:if>
				<jstl:if test="${option3 != null }">
					<form:option label="${option3}" value="${option3}" />
				</jstl:if>				
				<jstl:if test="${option4 != null }">
					<form:option label="${option4}" value="${option4}" />
				</jstl:if>			
				</form:select>
			<form:errors path="${path}" class="text-danger" />
		</div>
</jstl:if>	

</jstl:if>

<jstl:if test="${hideFirstOption == true}">
	<div class="form-group"> 
		<form:label path="${path}">
			<spring:message code="${code}" />:
		</form:label>
		<form:select path="${path}" class="form-control">
      		<jstl:forEach items="${items}" var="cc">
				<option value="${cc.getId()}" ${cc.getId() == selected ? 'selected' : ''} ><jstl:out value="${cc.text}"/></option>
        	</jstl:forEach>
		</form:select>
		<form:errors class="text-danger" path="${path}" />
	</div>
</jstl:if>
