<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container">

	<span class="display"><spring:message code="folder.name" /></span>
	<jstl:out value="  ${folder.getName()}" />
	<br> <span class="display"><spring:message
			code="folder.system" /></span>
	<jstl:out value="  ${folder.getSystem()}" />

	<jstl:if test="${folder.getFatherFolder()!=null }">
		<br>
		<spring:url var="urlFatherFolder" value="folder/display.do">
			<spring:param name="folderId"
				value="${folder.getFatherFolder().getId()}" />
		</spring:url>
		<a href="${urlFatherFolder}"><spring:message
				code="folder.fatherFolder" /></a>
	</jstl:if>

</div>

<jstl:if test="${anyFolder==false}">

	<br>
	<h2>
		<spring:message code="folder.childrenFolders" />
	</h2>
	<display:table class="table table-striped table-bordered table-hover"
		name="folders" id="row" requestURI="${requestURIf }" pagesize="5">

		<spring:url value="folder/edit.do" var="urlEdit">
			<spring:param name="folderId" value="${row.getId()}" />
		</spring:url>


		<display:column>
			<jstl:if test="${row.getSystem()==false }">
				<jstl:if test="${isChildren==false }">
					<a href="${urlEdit}"><spring:message code="folder.edit" /></a>
				</jstl:if>
			</jstl:if>
		</display:column>


		<spring:message code="folder.name" var="nameHeader" />
		<display:column property="name" title="${nameHeader}" />

		<spring:message code="folder.system" var="systemHeader" />
		<display:column property="system" title="${systemHeader}" />

		<spring:url value="folder/display.do" var="urlDisplay">
			<spring:param name="folderId" value="${row.getId()}" />
		</spring:url>

		<display:column>
			<a href="${urlDisplay }"><spring:message code="folder.display" /></a>
		</display:column>


	</display:table>

	<br>
	<jstl:if test="${isChildren==false }">

		<a href="folder/create.do"><spring:message code="folder.create" /></a>

	</jstl:if>

</jstl:if>

<jstl:if test="${anyMessage==false}">

	<br>
	<h2>
		<spring:message code="folder.messages" />
	</h2>
	<display:table class="table table-striped table-bordered table-hover"
		name="messages" id="row" requestURI="${requestURIm }" pagesize="5">

		<jstl:if test="${isChildren==false }">
			<spring:url value="message/edit.do" var="urlEdit">
				<spring:param name="messageId" value="${row.getId()}" />
			</spring:url>

			<spring:url value="message/move.do" var="urlMove">
				<spring:param name="messageId" value="${row.getId()}" />
			</spring:url>


			<display:column>
				<a href="${urlEdit}"><spring:message code="message.edit" /></a>
			</display:column>

			<display:column>
				<a href="${urlMove}"><spring:message code="message.move" /></a>
			</display:column>
		</jstl:if>

		<spring:message code="message.format.moment" var="momentFormat" />

		<spring:message code="message.moment" var="momentHeader" />
		<display:column property="moment" title="${momentHeader}"
			format="{0,date,${momentFormat }}" />

		<spring:message code="message.priority" var="priorityHeader" />
		<display:column property="priority" title="${priorityHeader}" />

		<spring:message code="message.subject" var="subjectHeader" />
		<display:column property="subject" title="${subjectHeader}" />

		<spring:message code="message.body" var="bodyHeader" />
		<display:column property="body" title="${bodyHeader}" />

		<spring:message code="message.folder.name" var="folderNameHeader" />
		<display:column property="folder.name" title="${folderNameHeader}" />

		<spring:message code="message.sender.name" var="senderNameHeader" />
		<display:column property="sender.name" title="${senderNameHeader}" />

		<spring:message code="message.recipient.name"
			var="recipientNameHeader" />
		<display:column property="recipient.name"
			title="${recipientNameHeader}" />

		<spring:url value="message/display.do" var="urlDisplay">
			<spring:param name="messageId" value="${row.getId()}" />
		</spring:url>

		<display:column>
			<a href="${urlDisplay }"><spring:message code="message.display" /></a>
		</display:column>

	</display:table>

	<br>
	<jstl:if test="${isChildren==false }">

		<a href="message/create.do"><spring:message code="message.create" /></a>

	</jstl:if>

</jstl:if>



