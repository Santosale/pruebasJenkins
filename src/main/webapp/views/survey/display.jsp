<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Date"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<script src="scripts/Chart.js"></script>

<div>
            
    <jstl:forEach items="${questionsAnswerRatio}" var="mapEntry">
        
    <h2><jstl:out value="${mapEntry.key.text}" /></h2>
        
    <canvas id="<jstl:out value="${mapEntry.key.id}" />"></canvas>
    <spring:message code="survey.avg.answer" var="mediaRespuestas"/>
    
    <script>    
    var ctx = document.getElementById('<jstl:out value="${mapEntry.key.id}" />');
    var media = new Chart.Bar(ctx, {
    data: {
    labels: [<jstl:forEach items="${mapEntry.value}" var="mapValue"><jstl:out value="\'" escapeXml="false"/><jstl:out value="${mapValue.key.text}"/><jstl:out value="\', " escapeXml="false"/></jstl:forEach>],
    datasets: [{
                label: '${mediaRespuestas}',
                fill: true,
                lineTension: 0.5,
                backgroundColor: "rgba(75,192,192,0.4)",
                borderColor: "rgba(75,192,192,1)",
                borderCapStyle: 'butt',
                borderDash: [],
                borderDashOffset: 0.0,
                borderJoinStyle: 'miter',
                pointBorderColor: "rgba(75,192,192,1)",
                pointBackgroundColor: "#fff",
                pointBorderWidth: 1,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: "rgba(75,192,192,1)",
                pointHoverBorderColor: "rgba(220,220,220,1)",
                pointHoverBorderWidth: 1,
                pointRadius: 5,
                pointHitRadius: 10,
                data: [<jstl:forEach items="${mapEntry.value}" var="mapValue"><jstl:out value="\'" escapeXml="false"/><jstl:out value="${mapValue.value}"/><jstl:out value="\', " escapeXml="false"/></jstl:forEach>],
                spanGaps: true,
            }]
        }
    });

    </script>
    
    </jstl:forEach>

</div>