<%--
 * layout.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<!DOCTYPE html>
<html>
	<head>
	
		<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/" />
		
		<meta charset="ISO-8859-1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
		<link rel="shortcut icon" href="favicon.ico"/> 
		
		<title><tiles:insertAttribute name="title" ignore="true" /></title>
		
		<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
		<link href="https://fonts.googleapis.com/css?family=Pacifico" rel="stylesheet">
		
		<!--[if lt IE 9]>
		    <script src="scripts/html5shiv.min.js"></script>
		<![endif]-->
		
		<!-- Bootstrap 3.3.7 -->
		<link type="text/css" rel="stylesheet" href="styles/bootstrap.min.css">	
		
		<link type="text/css" rel="stylesheet" href="styles/style.css">	
		
		<!--[if lt IE 9]>
			<script src="scripts/respond.min.js"></script>
		<![endif]-->

	</head>
	<body>
	
		<tiles:insertAttribute name="header" />

		<section class="container">
			<h1>
				<tiles:insertAttribute name="title" />
			</h1>
			<br>
			<article>
				<tiles:insertAttribute name="body" />	
				<jstl:if test="${message != null}">
					<br>
					<span class="text-danger"><spring:message code="${message}" /></span>
				</jstl:if>	
			</article>
		</section>
	
		<tiles:insertAttribute name="footer" />
		
		<!-- jQuery 1.12.4 -->
		<script src="scripts/jquery.min.js"></script>
		
		<!-- Boostrap 3.3.7 -->
		<script src="scripts/bootstrap.min.js"></script>
		
		<!-- forEach compatibilidad IE8 -->
		<script src="scripts/polyfills.js"></script>
		
		<!-- Funciones comunes -->
		<script src="scripts/helpers.js"></script>
		
		<!-- AJAX Buscador de Trip -->
		<script>
		
			$("#searchTitle").click(function(e){
				if($(e.currentTarget).attr("id") == "returnTitle"){
					$("#initialTable").css("display","block");
					$("#resultTable").fadeOut();
					$("#searchForm").animate({opacity:0},1000).css("display","none");
					$("#initialTable").animate({"padding-top": "0px", opacity: 1}, 1000);
					$(e.currentTarget).attr("id", "searchTitle");
					$("#search").val("");
				}else{
					$("#initialTable").animate({"padding-top": "50px", opacity: 0}, 1000, function(){
						$("#initialTable").css("display","none");
						$("#searchForm").css("display", "block");
						$("#searchForm").css("opacity", 1);
					});	
					$(e.currentTarget).attr("id", "returnTitle");
				}
			});
				
			$('#search').keyup(function() {
				$.get("finder/search.do", {keyword: $('#search').val()}, requestDone);			
			});
			
			function requestDone(data) {
						
				$("#body").html("");
				
				$("#resultTable").fadeIn();
								
				data.forEach(function(item) {
				
					var tr = document.createElement("tr");
					
					for (var i in item) {
						
						if(item[i] != item.id){
							if(item[i] != item.locale){
								var text;
			
								if(item[i] == item.price){
									console.log(data.locale);
									var price = internacionalizationPrice(item[i], item.locale);
									text = document.createTextNode(escapeHtml("<spring:message code="trip.var1" />") +  
																   price + 
																   escapeHtml("<spring:message code="trip.var2"/>"));
								}else{
									text = document.createTextNode(item[i]);
								}
								
								var td = document.createElement("td");
								
								td.appendChild(text);
								
								tr.appendChild(td);
							}
						}
					}
					
					var td = document.createElement("td");
					var a = document.createElement("a");
					a.setAttribute("href", "trip/display.do?tripId="+item.id);
					a.innerHTML = "<spring:message code="trip.display" />";
					td.appendChild(a);
					tr.appendChild(td);
					
					$("#body").append(tr);
		
				});
			}
			
			function escapeHtml(text) {
			  var map = {
			    '&euro;': '8364',
			  };
		
			  return String.fromCharCode(map[text]);
			}

		</script>
		
		<!-- Validar teléfono -->
		<script>
		if($("input[name*='phone']").length != 0){
			
			$("form:eq(1)").submit(function(){	
								
				var result = true;
				var phone = $("input[name*='phone']").val();
				var pattern = new RegExp("^(\\+(([1-9][0-9][0-9])|([1-9][0-9])|([1-9])).\\((([1-9][0-9][0-9])|([1-9][0-9])|([1-9]))\\).\\d{4,})|(\\+(([1-9][0-9][0-9])|([1-9][0-9])|([1-9])).\\d{4,})|(\\d{4,})$", "m");
				var patternFourOrMoreNumber = new RegExp("^(\\d{4,})$", "m");
				
				if(phone != ""){
				
					var aux = patternFourOrMoreNumber.test(phone);
					
					console.log(aux);
					
					if(aux){
						$("input[name*='phone']").val("${countryCode}" + phone);
					}
					
					aux = pattern.test(phone);
					
					if (!aux) {
					
						var confirmation = confirm("<spring:message code="actor.phone.message" />");
						
						if (!confirmation) {
						 	result = false;
						}
						
					}
				
				}
					
				return result;
			      
			});
		
		}		
		</script>
		
		<!-- AJAX Vista de Curriculum -->
		<script>
			var curriculumId = "${curriculum.getId()}";

			// Respuesta professional Records
			function requestProfessional(data) {
				$("#professionaldiv").empty();
				var pageNumber = Math.floor(((data.recordsNumber / data.size)-0.1) + 1);
				var isRangerCurriculum = data.isRangerCurriculum;
				var page = data.page;

				data.professionalRecords.forEach(function(item) {

					$("#professionaldiv").append("<div id='provisional'></div>").addClass("container-square2");
					$("#provisional").attr('id', item.id).addClass("container-square");

					$("#" + item.id).append("<span class='display'><spring:message code='professionalRecord.companyName' /></span>");
					$("#" + item.id).append("<span>" + item.companyName + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='professionalRecord.role' /></span>");
					$("#" + item.id).append("<span>" + item.role + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='professionalRecord.startedWorkDate' /></span>");
					$("#" + item.id).append("<span>" + internacionalizationDate(item.startedWorkDate, data.locale) + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='professionalRecord.finishedWorkDate' /></span>");
					$("#" + item.id).append("<span>" + internacionalizationDate(item.finishedWorkDate, data.locale) + "</span></br>");
					
					if(item.link!="")
						$("#" + item.id).append("<a href='" + item.link + "'><spring:message code='professionalRecord.link' /></a></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='professionalRecord.comments' /></span>");
					$("#" + item.id).append("<span>" + item.comments + "</span></br>");

					if (isRangerCurriculum) {
						$("#" + item.id).append(
								"<a class='btn btn-primary' href='professionalRecord/ranger/edit.do?professionalRecordId=" + item.id
										+ "'><spring:message code='professionalRecord.edit' /></a></br>");
					}

				});
				

				for ( var i = 1; i < pageNumber + 1; i++) {
					if (i == page) {
						$("#professionaldiv").append("<span class='btn btn-danger' id='page' style='margin-right:10px;'>" + i + "</span>");
					} else {
						$("#professionaldiv").append("<span class='btn btn-primary' id='page' style='margin-right:10px;'>" + i + "</span>");
					}

					$("#page").attr('id', "paginationprofessional" + i);

				}
				
				if (isRangerCurriculum) {
					$("#professionaldiv").append("<br/><br/><a class='btn btn-warning' href='professionalRecord/ranger/create.do'><spring:message code='professionalRecord.create' /></a><br/></br>");
				}

				$('#professional').next().addClass('display-on');

			}

			// Respuesta miscellaneous Records
			function requestMiscellaneous(data) {
				$("#miscellaneousdiv").empty();
				var pageNumber = Math.floor(((data.miscellaneousNumber / data.sizeMiscellaneous)-0.1) + 1);
				var isRangerCurriculum = data.isRangerCurriculum;
				var page = data.pageMiscellaneous;

				data.miscellaneousRecords.forEach(function(item) {

					$("#miscellaneousdiv").append("<div id='provisionalM'></div>").addClass("container-square2");
					$("#provisionalM").attr('id', item.id).addClass("container-square");

					$("#" + item.id).append("<span class='display'><spring:message code='miscellaneousRecord.title' /></span>");
					$("#" + item.id).append("<span>" + item.title + "</span></br>");

					if(item.link!="")
						$("#" + item.id).append("<a href='" + item.link + "'><spring:message code='miscellaneousRecord.link' /></a></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='miscellaneousRecord.comments' /></span>");
					$("#" + item.id).append("<span>" + item.comments + "</span></br>");

					if (isRangerCurriculum) {
						$("#" + item.id).append(
								"<a class='btn btn-primary' href='miscellaneousRecord/ranger/edit.do?miscellaneousRecordId=" + item.id
										+ "'><spring:message code='miscellaneousRecord.edit' /></a></br>");
					}

				});
				
				for ( var i = 1; i < pageNumber + 1; i++) {
					if (i == page) {
						$("#miscellaneousdiv").append("<span class='btn btn-danger' id='pagem' style='margin-right:10px;'>" + i + "</span>");
					} else {
						$("#miscellaneousdiv").append("<span class='btn btn-primary' id='pagem' style='margin-right:10px;'>" + i + "</span>");
					}

					$("#pagem").attr('id', "paginationmiscellaneous" + i);

				}
				
				if (isRangerCurriculum) {
					$("#miscellaneousdiv").append("<br/><br/><a class='btn btn-warning' href='miscellaneousRecord/ranger/create.do'><spring:message code='miscellaneousRecord.create' /></a></br></br>");
				}

				$('#miscellaneous').next().addClass('display-on');

			}

			// Respuesta education Records
			function requestEducation(data) {
				$("#educationdiv").empty();
				var pageNumber = Math.floor(((data.educationNumber / data.sizeEducation)-0.1) + 1);
				var isRangerCurriculum = data.isRangerCurriculum;
				var page = data.pageEducation;

				data.educationrecords.forEach(function(item) {

					$("#educationdiv").append("<div id='provisionalEd'></div>").addClass("container-square2");
					$("#provisionalEd").attr('id', item.id).addClass("container-square");

					$("#" + item.id).append("<span class='display'><spring:message code='educationrecord.title' /></span>");
					$("#" + item.id).append("<span>" + item.title + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='educationrecord.institution' /></span>");
					$("#" + item.id).append("<span>" + item.institution + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='educationrecord.startedStudyDate' /></span>");
					$("#" + item.id).append("<span>" + internacionalizationDate(item.startedStudyDate, data.locale) + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='educationrecord.finishedStudyDate' /></span>");
					$("#" + item.id).append("<span>" + internacionalizationDate(item.finishedStudyDate, data.locale) + "</span></br>");

					if(item.link!="")
						$("#" + item.id).append("<a href='" + item.link + "'><spring:message code='educationrecord.link' /></a></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='educationrecord.comments' /></span>");
					$("#" + item.id).append("<span>" + item.comments + "</span></br>");

					if (isRangerCurriculum) {
						$("#" + item.id)
								.append(
										"<a class='btn btn-primary' href='educationrecord/ranger/edit.do?recordId=" + item.id
												+ "'><spring:message code='educationrecord.edit' /></a></br>");
					}

				});
				
				for ( var i = 1; i < pageNumber + 1; i++) {
					if (i == page) {
						$("#educationdiv").append("<span class='btn btn-danger' id='pageed' style='margin-right:10px;'>" + i + "</span>");
					} else {
						$("#educationdiv").append("<span class='btn btn-primary' id='pageed' style='margin-right:10px;'>" + i + "</span>");
					}

					$("#pageed").attr('id', "paginationeducation" + i);

				}
				
				if (isRangerCurriculum) {
					$("#educationdiv").append("<br/><br/><a class='btn btn-warning' href='educationrecord/ranger/create.do?curriculumId="+curriculumId+"'><spring:message code='educationrecord.create' /></a></br></br>");
				}

				$('#education').next().addClass('display-on');

			}

			// Respuesta endorser Records
			function requestEndorser(data) {
				$("#endorserdiv").empty();
				var pageNumber = Math.floor(((data.endorserNumber / data.sizeEndorser)-0.1) + 1);
				var isRangerCurriculum = data.isRangerCurriculum;
				var page = data.pageEndorser;

				data.endorserRecords.forEach(function(item) {

					$("#endorserdiv").append("<div id='provisionalEn'></div>").addClass("container-square2");
					$("#provisionalEn").attr('id', item.id).addClass("container-square");

					$("#" + item.id).append("<span class='display'><spring:message code='endorserRecord.fullName' /></span>");
					$("#" + item.id).append("<span>" + item.fullName + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='endorserRecord.email' /></span>");
					$("#" + item.id).append("<span>" + item.email + "</span></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='endorserRecord.phone' /></span>");
					$("#" + item.id).append("<span>" + item.phone + "</span></br>");

					$("#" + item.id).append("<a href='" + item.linkLinkedin + "'><spring:message code='endorserRecord.linkLinkedin' /></a></br>");

					$("#" + item.id).append("<span class='display'><spring:message code='endorserRecord.comments' /></span>");
					$("#" + item.id).append("<span>" + item.comments + "</span></br>");

					if (isRangerCurriculum) {
						$("#" + item.id).append("<a class='btn btn-primary' href='endorserRecord/ranger/edit.do?endorserRecordId=" + item.id+ "'><spring:message code='endorserRecord.edit' /></a></br>");
					}
				
				});
				
				for ( var i = 1; i < pageNumber + 1; i++) {
					if (i == page) {
						$("#endorserdiv").append("<span class='btn btn-danger' id='pageen' style='margin-right:10px;'>" + i + "</span>");
					} else {
						$("#endorserdiv").append("<span class='btn btn-primary' id='pageen' style='margin-right:10px;'>" + i + "</span>");
					}

					$("#pageen").attr('id', "paginationendorser" + i);

				}
				
				if (isRangerCurriculum) {
					$("#endorserdiv").append("<br/><br/><a class='btn btn-warning' href='endorserRecord/ranger/create.do?curriculumId="+curriculumId+"'><spring:message code='endorserRecord.create' /></a></br></br>");
				}

				$('#endorser').next().addClass('display-on');

			}

			// Declaramos los eventos para los enlaces de paginacion
			$("#professionaldiv").on('click', "[id^=paginationprofessional]", function(e) {
				$.get("professionalRecord/list.do", {
					curriculumId : curriculumId,
					page : $(e.currentTarget).html(),
					size : 5
				}, requestProfessional);
			});

			$("#endorserdiv").on('click', "[id^=paginationendorser]", function(e) {
				$.get("endorserRecord/list.do", {
					curriculumId : curriculumId,
					page : $(e.currentTarget).html(),
					size : 5
				}, requestEndorser);
			});

			$("#educationdiv").on('click', "[id^=paginationeducation]", function(e) {
				$.get("educationrecord/list.do", {
					curriculumId : curriculumId,
					page : $(e.currentTarget).html(),
					size : 5
				}, requestEducation);
			});

			$("#miscellaneousdiv").on('click', "[id^=paginationmiscellaneous]", function(e) {
				$.get("miscellaneousRecord/list.do", {
					curriculumId : curriculumId,
					page : $(e.currentTarget).html(),
					size : 5
				}, requestMiscellaneous);
			});

			// Generamos los eventos para la petición inicial
			getRecords("#professional", "#professionaldiv", "professionalRecord/list.do", curriculumId);
			getRecords("#endorser", "#endorserdiv", "endorserRecord/list.do", curriculumId);
			getRecords("#miscellaneous", "#miscellaneousdiv", "miscellaneousRecord/list.do", curriculumId);
			getRecords("#education", "#educationdiv", "educationrecord/list.do", curriculumId);		
		</script>
		
		<!-- Rellenar formulario con datos aleatorios -->
		
	</body>
</html>