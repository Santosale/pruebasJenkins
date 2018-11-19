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

<%@ attribute name="message" required="true"%>
<%@ attribute name="url" required="true"%>

<%-- Definition --%>

<script type="text/javascript" async src="https://platform.twitter.com/widgets.js"></script>
<a class="twitter-share-button" href="https://twitter.com/intent/tweet?text=${message}"> Tweet</a>
<a style="background-image: url(https://smk.co/dyn/image.png?type=o&id=1908&thumbnail=1&width=800&bgcolor=ffffff); display:inline-block; background-size: cover; width: 75px; height: 35px" href="https://www.facebook.com/dialog/share?app_id=145634995501895&display=page&href=${url}"> </a>

