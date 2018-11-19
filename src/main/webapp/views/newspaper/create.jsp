<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<form:form action="newspaper/user/edit.do" modelAttribute="newspaper">

	<form:hidden path="id" />
	<jstl:if test="${newspaper.getId()==0 }">
	<form:hidden path="publisher" />
	<form:hidden path="isPublished" />
	<form:hidden path="articles" />
	<form:hidden path="advertisements" />
	
	
	
		
	<acme:textbox code="newspaper.title" path="title"/>

	<acme:textbox code="newspaper.description" path="description"/>
	
	<acme:textbox path="publicationDate" code="newspaper.publicationDate" placeholder="dd/MM/yyyy" />
	
	<acme:textbox code="newspaper.picture" path="picture"/>
	
	<form:checkbox class="form-check-input" path="isPrivate" id="check" value="" checked=''/>
	<form:label path="isPrivate">
	<div onclick="activar()" ><spring:message code="newspaper.isPrivate" /></div>
	</form:label>
	<form:errors class="text-danger" path="isPrivate"/>
	<br>
	</jstl:if>
	
	<jstl:if test="${newspaper.getId()!=0 }">
	<acme:textbox path="publicationDate" code="newspaper.publicationDate" placeholder="dd/MM/yyyy" />
	</jstl:if>
	
	
	 
	<acme:submit name="save" code="newspaper.save"/>
	
	<acme:cancel url="newspaper/user/list.do" code="newspaper.cancel"/>
			
</form:form>

<script type="text/javascript">

	function activar() {
		if  (document.getElementById("check").checked)
			document.getElementById("check").checked = false;
		else if (!document.getElementById("check").checked)
			document.getElementById("check").checked = true;
	}
</script>

<script type="text/javascript">
	if(${newspaper.getId()!=0}){

        $("#newspaper").submit(function() {
        	var result =true;
        	var myDate= $("#publicationDate").val();
    		var d = new Date();
            var month =myDate.split("/")[1];
            var day = myDate.split("/")[0];
            var year = myDate.split("/")[2];
            var currentMonth = (d.getMonth() + 1)
            var currentDay = d.getDate();
            var currentYear = d.getFullYear();
			
       		if(${newspaper.getIsPublished()==false}){
        		if(month==currentMonth && day==currentDay && year==currentYear){
					var confirmation = confirm("<spring:message code="newspaper.messageEditDate" />");
					if (!confirmation) {
					 	result = false;
					}
        		}
        	}
        	 return result;
        	});
	}

</script>