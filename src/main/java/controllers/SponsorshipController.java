
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.SponsorService;
import services.SponsorshipService;
import domain.Sponsorship;

@Controller
@RequestMapping("/sponsorship")
public class SponsorshipController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private SponsorService		sponsorService;


	// Constructors -----------------------------------------------------------

	public SponsorshipController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<Sponsorship> sponsorships;
		int sponsorId = 0;
		int userAccountId;

		if (LoginService.isAuthenticated() == true) {
			userAccountId = LoginService.getPrincipal().getId();
			if (this.sponsorService.findByUserAccountId(userAccountId) != null)
				sponsorId = this.sponsorService.findByUserAccountId(userAccountId).getId();
		}

		sponsorships = this.sponsorshipService.findByTripId(tripId);

		result = new ModelAndView("sponsorship/list");
		result.addObject("sponsorships", sponsorships);
		result.addObject("requestURI", "sponsorship/list.do");
		result.addObject("sponsorId", sponsorId);

		return result;
	}

	// Display ----------------------------------------------------------------

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int sponsorshipId) {
		ModelAndView result;
		Sponsorship sponsorship;

		sponsorship = this.sponsorshipService.findOne(sponsorshipId);

		result = new ModelAndView("sponsorship/display");
		result.addObject("sponsorship", sponsorship);
		result.addObject("requestURI", "sponsorship/display.do");

		return result;
	}

}
