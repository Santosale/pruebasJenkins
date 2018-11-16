
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.SurvivalClassService;
import services.TripService;
import domain.Actor;
import domain.SurvivalClass;
import domain.Trip;

@Controller
@RequestMapping("/survivalClass")
public class SurvivalClassController extends AbstractController {

	// Services

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ActorService			actorService;


	// Constructor
	public SurvivalClassController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int survivalClassId) {
		ModelAndView result;
		SurvivalClass survivalClass;

		survivalClass = this.survivalClassService.findOne(survivalClassId);
		Assert.notNull(survivalClass);
		result = new ModelAndView("survivalClass/display");

		result.addObject("survivalClass", survivalClass);

		return result;

	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<SurvivalClass> survivalClasses;
		UserAccount userAccount;
		Actor actor;
		Trip trip;
		boolean canCreate;
		Authority authority;
		int managerForCompareId;

		managerForCompareId = 0;
		authority = new Authority();
		authority.setAuthority("MANAGER");

		canCreate = false;
		//		try {
		if (LoginService.isAuthenticated() == true) {
			userAccount = LoginService.getPrincipal();
			actor = this.actorService.findByUserAccountId(userAccount.getId());
		} else
			actor = null;
		//		} catch (final IllegalArgumentException e) {

		//		}
		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);
		if (actor != null) {
			managerForCompareId = actor.getId();
			if (actor.getUserAccount().getAuthorities().contains(authority)) {
				if (this.tripService.findByManagerUserAccountId(actor.getUserAccount().getId()).contains(trip))
					canCreate = true;
			} else
				canCreate = false;
		} else
			canCreate = false;

		survivalClasses = this.survivalClassService.findByTripId(tripId);

		result = new ModelAndView("survivalClass/list");
		result.addObject("survivalClasses", survivalClasses);
		result.addObject("managerForCompareId", managerForCompareId);
		result.addObject("canCreate", canCreate);
		result.addObject("tripId", tripId);

		return result;
	}
}
