
package controllers.sponsor;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.ConfigurationService;
import services.SponsorService;
import controllers.AbstractController;
import domain.Actor;
import domain.Sponsor;

@Controller
@RequestMapping(value = "/actor/sponsor")
public class SponsorController extends AbstractController {

	// Services
	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public SponsorController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int sponsorId) {
		ModelAndView result;
		Sponsor sponsor;
		UserAccount userAccount;
		Actor actor;
		int idOfPrincipal;

		idOfPrincipal = 0;
		//try {
		if (LoginService.isAuthenticated() == true) {
			userAccount = LoginService.getPrincipal();
			actor = this.actorService.findByUserAccountId(userAccount.getId());
			idOfPrincipal = actor.getId();
		}
		//} catch (final IllegalArgumentException e) {

		//}

		sponsor = this.sponsorService.findOne(sponsorId);
		Assert.notNull(sponsor);
		result = new ModelAndView("actor/display");

		result.addObject("actor", sponsor);
		result.addObject("actorType", "sponsor");
		result.addObject("idOfPrincipal", idOfPrincipal);

		return result;

	}
	//	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	//	public ModelAndView profile() {
	//		ModelAndView result;
	//		Sponsor sponsor;
	//		UserAccount userAccount;
	//		int idOfPrincipal;
	//
	//		userAccount = LoginService.getPrincipal();
	//		sponsor = this.sponsorService.findByUserAccountId(userAccount.getId());
	//		Assert.notNull(sponsor);
	//		idOfPrincipal = sponsor.getId();
	//
	//		result = new ModelAndView("sponsor/display");
	//
	//		result.addObject("sponsor", sponsor);
	//		result.addObject("idOfPrincipal", idOfPrincipal);
	//
	//		return result;
	//	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Sponsor sponsor, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(sponsor);
		else
			try {
				this.sponsorService.save(sponsor);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(sponsor, "actor.commit.error");
			}

		return result;
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Sponsor sponsor;

		sponsor = this.sponsorService.create();

		result = this.createEditModelAndView(sponsor);

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Sponsor sponsor) {
		ModelAndView result;

		result = this.createEditModelAndView(sponsor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Sponsor sponsor, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		String countryCode;
		String requestURI;

		requestURI = "actor/sponsor/edit.do";

		countryCode = this.configurationService.findCountryCode();
		canEdit = false;
		if (sponsor.getId() == 0)
			canEdit = true;
		else if (sponsor.getUserAccount().getId() == LoginService.getPrincipal().getId())
			canEdit = true;

		if (sponsor.getId() > 0)
			result = new ModelAndView("sponsor/edit");
		else
			result = new ModelAndView("sponsor/create");

		result.addObject("modelo", "sponsor");
		result.addObject("sponsor", sponsor);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}
}
