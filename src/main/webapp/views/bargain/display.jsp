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

<div class="container">

	<spring:message code="sponsorship.alt" var="sponsorshipAlt"/>
	<jstl:if test="${bargain.getIsPublished()}">
		<acme:share url="${urlShare}" message="Share it" />
	</jstl:if>
	
	<div style="display:flex">
		<jstl:if test="${sponsorships!=null && sponsorships.size() >=1}">
			<jstl:if test="${!mapSponsorshipBoolean.get(sponsorships.get(0))}">
				<acme:image  value="${sponsorships.get(0).getImage()}" alt="${sponsorshipAlt}" url="${sponsorships.get(0).getUrl()}"/>
			</jstl:if>
			<jstl:if test="${mapSponsorshipBoolean.get(sponsorships.get(0))}">
				<acme:image  value="${imageBroken}" alt="${sponsorshipAlt}" url="${sponsorships.get(0).getUrl()}"/>
			</jstl:if>
		</jstl:if>
		<jstl:if test="${sponsorships!=null && sponsorships.size() >=2}">
			
			<jstl:if test="${!mapSponsorshipBoolean.get(sponsorships.get(1))}">
				<acme:image value="${sponsorships.get(1).getImage()}" alt="${sponsorshipAlt}" url="${sponsorships.get(1).getUrl()}"/>
			</jstl:if>
			<jstl:if test="${mapSponsorshipBoolean.get(sponsorships.get(1))}">
				<acme:image  value="${imageBroken}" alt="${sponsorshipAlt}" url="${sponsorships.get(1).getUrl()}"/>
			</jstl:if>
		</jstl:if>
	</div>
	
	<h3><a href="${bargain.getProductUrl()}" target="_blank" style="text-decoration: none;"><jstl:out value="${bargain.getProductName()}" /></a></h3>
	
	<acme:display code="bargain.description" value="${bargain.getDescription()}"/>
	
	
	<acme:display code="bargain.estimated.sells" value="${bargain.getEstimatedSells()}"/>
	
	
	<jstl:if test="${bargain.getDiscountCode()!=null}">
		<acme:display code="bargain.discount.code" value="${bargain.getDiscountCode()}"/>
	</jstl:if>
	
	<span style="text-decoration:line-through;"><acme:display value="${bargain.getOriginalPrice()}" code="bargain.original.price" formatNumber="true" domain="bargain"/></span>
	
	<security:authorize access="hasRole('COMPANY')">
		<security:authentication property="principal.username" var="principal"/>
		
		<jstl:if test="${principal.equals(bargain.getCompany().getUserAccount().getUsername())}">
			<acme:display value="${bargain.getMinimumPrice()}" code="bargain.minimum.price" formatNumber="true" domain="bargain"/>
		</jstl:if>
	</security:authorize>
	
	<jstl:if test="${plan==null}">
		<acme:display value="${bargain.getPrice()}" code="bargain.price" formatNumber="true" domain="bargain"/>
	</jstl:if>
	
	<jstl:if test="${plan!=null && plan.getName().equals('Gold Premium')}">
		<acme:display value="${bargain.getPrice() - (bargain.getPrice() * 0.075)}" code="bargain.price" formatNumber="true" domain="bargain"/>
	</jstl:if>
	
	<jstl:if test="${plan!=null && plan.getName().equals('Basic Premium')}">
		<acme:display value="${bargain.getPrice() - (bargain.getPrice() * 0.025)}" code="bargain.price" formatNumber="true" domain="bargain"/>
	</jstl:if>
	
	<span class="display">
		<spring:message code="bargain.is.published" var="isPublished"/>
		<strong><jstl:out value="${isPublished}: "></jstl:out></strong>
		<jstl:if test="${bargain.getIsPublished()}">
			<img src="images/yes.png" alt="${isPublished}" width="20"/>
		</jstl:if>
		<jstl:if test="${!bargain.getIsPublished()}">
			<img src="images/no.png" alt="${isPublished}" width="20"/>
		</jstl:if>
	</span>
	
	
	<div style="display:flex">
		<c:forEach items="${bargain.getProductImages()}" var="image">
			<jstl:if test="${mapLinkBooleanBargain.get(image)}">
				<div style="display: inline-block; background-position: center; width:calc(100%/${bargain.getProductImages().size()}); height: 300px; background-image: url(${imageBroken}); background-size: cover;"></div>
			</jstl:if>
			<jstl:if test="${!mapLinkBooleanBargain.get(image)}">
				<div style="display: inline-block; background-position: center; width:calc(100%/${bargain.getProductImages().size()}); height: 300px; background-image: url(${image}); background-size: cover;"></div>
			</jstl:if>
		</c:forEach>
	</div>
	
	<br>
	<acme:displayLink parameter="actorId" code="bargain.company" action="actor/company/profile.do" parameterValue="${bargain.getCompany().getId()}" css="btn btn-primary"></acme:displayLink>
	<acme:displayLink parameter="bargainId" code="bargain.sponsorships" action="sponsorship/list.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
	<security:authorize access="hasRole('COMPANY')">
		<security:authentication property="principal.username" var="principal"/>
		<jstl:if test="${bargain.getCompany().getUserAccount().getUsername().equals(principal)}">
			<acme:displayLink parameter="bargainId" code="category.add" action="category/company/addCategory.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
			<acme:displayLink parameter="bargainId" code="category.remove" action="category/company/removeCategory.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
		</jstl:if>
	</security:authorize>
	
	<security:authorize access="hasRole('USER')">
		<jstl:if test="${canAddWishList}">
			<acme:displayLink parameter="bargainId" code="bargain.addWishList" action="actor/user/addremovewishlist.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
		</jstl:if>
		<jstl:if test="${!canAddWishList}">
			<acme:displayLink parameter="bargainId" code="bargain.removeWishList" action="actor/user/addremovewishlist.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
		</jstl:if>
	</security:authorize>
	
	<!-- Tags -->
	<jstl:if test="${tags.size()>0}">
		<br>
		<br>
		<span class="display"><spring:message code="bargain.tags"/></span>
		<br>
		<c:forEach var="i" begin="0" end="${tags.size()-1}">
		
			<jstl:out value="${tags.get(i).getName()}"></jstl:out>
			<jstl:if test="${tags.size()-1!=i}">
				<span> ,</span>
			</jstl:if>
			
		</c:forEach>
	</jstl:if>
	<!-- /Tags -->
	
	<!-- Comments -->
	<security:authorize access="hasRole('USER')">
		<br>
		<br>
		<acme:displayLink parameter="bargainId" code="comment.create" action="comment/user/create.do" parameterValue="${bargain.getId()}" css="btn btn-primary"></acme:displayLink>
	</security:authorize>
	
	<jstl:if test="${mapCommentBoolean.keySet().size()>0}">
		<br>
		<br>
		<br>
		<span class="display"><spring:message code="bargain.comments"/></span>
		<br>
		
		<jstl:forEach var="row" items="${mapCommentBoolean.keySet()}">
		
			<jstl:if test="${mapCommentBoolean.get(row)}">
				<jstl:set value='background-color: #ec6354;' var="style"/>
			</jstl:if>
			
			<div class="container-square2" style="${style}">
					
				<acme:display code="comment.moment" value="${row.getMoment()}" codeMoment="comment.format.moment"/>
		
				<acme:display code="comment.text" value="${row.getText()}"/>
				
				<acme:displayLink parameter="commentId" code="comment.more" action="comment/display.do" parameterValue="${row.getId()}" css="btn btn-primary"></acme:displayLink>		
				
			</div>
			
		</jstl:forEach>
	
		<acme:paginate url="bargain/display.do" objects="${mapCommentBoolean.keySet()}" parameter="bargainId" parameterValue="${bargain.getId()}" page="${page}" pageNumber="${pageNumber}"/>
			
	</jstl:if> 
		<!-- /Comments -->
	
	
	
	
	<div style="display:flex">
		<jstl:if test="${sponsorships!=null && sponsorships.size() >=3}">
			
			<jstl:if test="${!mapSponsorshipBoolean.get(sponsorships.get(2))}">
				<acme:image value="${sponsorships.get(2).getImage()}" alt="${sponsorshipAlt}" url="${sponsorships.get(2).getUrl()}"/>
			</jstl:if>
			<jstl:if test="${mapSponsorshipBoolean.get(sponsorships.get(2))}">
				<acme:image  value="${imageBroken}" alt="${sponsorshipAlt}" url="${sponsorships.get(2).getUrl()}"/>
			</jstl:if>
			
		</jstl:if>
		
		<jstl:if test="${sponsorships!=null && sponsorships.size() ==4}">
			<jstl:if test="${!mapSponsorshipBoolean.get(sponsorships.get(3))}">
				<acme:image value="${sponsorships.get(3).getImage()}" alt="${sponsorshipAlt}" url="${sponsorships.get(3).getUrl()}"/>
			</jstl:if>
			
			<jstl:if test="${mapSponsorshipBoolean.get(sponsorships.get(3))}">
				<acme:image  value="${imageBroken}" alt="${sponsorshipAlt}" url="${sponsorships.get(3).getUrl()}"/>
			</jstl:if>
		</jstl:if>
	</div>
		
</div>