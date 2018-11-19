
package controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.GrouponService;
import services.ParticipationService;
import services.PlanService;
import services.UserService;
import domain.Groupon;

@Controller
@RequestMapping("/groupon")
public class GrouponController extends AbstractController {

	// Services
	@Autowired
	private GrouponService			grouponService;

	@Autowired
	private PlanService				planService;

	@Autowired
	private UserService				userService;

	@Autowired
	private ParticipationService	participationService;


	// Constructor
	public GrouponController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Groupon> groupons;
		Authority authority;
		Authority authority2;

		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");
		groupons = null;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			if (this.planService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) != null)
				groupons = this.grouponService.findAllPaginated(page, 5);
			else
				groupons = this.grouponService.findWithMaxDateFuture(page, 5);
		} else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2))
			groupons = this.grouponService.findAllPaginated(page, 5);
		else
			groupons = this.grouponService.findWithMaxDateFuture(page, 5);

		Assert.notNull(groupons);

		result = new ModelAndView("groupon/list");
		result.addObject("pageNumber", groupons.getTotalPages());
		result.addObject("page", page);
		result.addObject("groupons", groupons.getContent());
		result.addObject("requestURI", "groupon/list.do");

		return result;
	}

	// List
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(final int grouponId, final HttpServletRequest request) {
		final ModelAndView result;
		Groupon groupon;
		String currentUrl;
		Boolean canSeeGroupon;
		Boolean canSeeDiscountCode;
		Boolean canCreateParticipation;
		Authority authority;
		Authority authority2;

		currentUrl = super.makeUrl(request);
		canSeeGroupon = false;
		canSeeDiscountCode = false;
		canCreateParticipation = false;
		authority = new Authority();
		authority.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");
		groupon = this.grouponService.findOne(grouponId);
		Assert.notNull(groupon);

		if (groupon.getMaxDate().compareTo(new Date()) > 0)
			canSeeGroupon = true;
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			if (groupon.getCreator().getUserAccount().getId() == LoginService.getPrincipal().getId()
				|| this.participationService.findByGrouponIdAndUserId(groupon.getId(), this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) != null
				|| this.planService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) != null)
				canSeeGroupon = true;

		} else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2))
			canSeeGroupon = true;

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			if (groupon.getCreator().getUserAccount().getId() == LoginService.getPrincipal().getId()
				|| this.participationService.findByGrouponIdAndUserId(groupon.getId(), this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) != null)
				canSeeDiscountCode = true;

			if (groupon.getCreator().getUserAccount().getId() != LoginService.getPrincipal().getId()
				&& this.participationService.findByGrouponIdAndUserId(groupon.getId(), this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null)
				canCreateParticipation = true;

		}

		result = new ModelAndView("groupon/display");
		result.addObject("groupon", groupon);
		result.addObject("currentUrl", currentUrl);
		result.addObject("canSeeGroupon", canSeeGroupon);
		result.addObject("canSeeDiscountCode", canSeeDiscountCode);
		result.addObject("canCreateParticipation", canCreateParticipation);

		return result;
	}

}
