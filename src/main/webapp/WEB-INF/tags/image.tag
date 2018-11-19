<%--
 * submit.tag
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
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<%-- Attributes --%> 

<%@ attribute name="alt" required="false" %> 
<%@ attribute name="value" required="true"%>
<%@ attribute name="url" required="false"%>

<%-- Definition --%>

<jstl:if test="${alt==null}">
	<img src="${value}" width="400px" height="200px" style="text-align: center; margin:auto;" />
</jstl:if>

<jstl:if test="${alt!=null}">

	<jstl:if test="${url==null}">
		<img src="${value}" alt="${alt}" width="400px" height="200px" style="text-align: center; margin:auto;" />
	</jstl:if>
	
	<jstl:if test="${url!=null}">
		<a href="${url}" target="_blank" ><img src="${value}" alt="${alt}" width="400px" height="200px" style="text-align: center; margin:auto;" /></a>
	</jstl:if>
	
</jstl:if>


