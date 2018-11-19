/*
 * WelcomeController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.BargainService;
import services.ConfigurationService;
import services.PlanService;
import services.RaffleService;
import services.UserService;
import domain.Bargain;
import domain.Plan;
import domain.Raffle;
import domain.User;

@Controller
@RequestMapping("/welcome")
public class WelcomeController extends AbstractController {

	@Autowired
	private RaffleService			raffleService;

	@Autowired
	private BargainService			bargainService;

	@Autowired
	private PlanService				planService;

	@Autowired
	private UserService				userService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------

	public WelcomeController() {
		super();
	}

	// Index ------------------------------------------------------------------		

	@RequestMapping(value = "/index")
	public ModelAndView index() {
		ModelAndView result;
		Page<Raffle> raffles;
		Page<Bargain> bargains;
		Plan plan;
		Authority authority;
		User user;
		Boolean isSponsor;
		Authority authority2;

		authority = new Authority();
		authority.setAuthority("USER");

		authority2 = new Authority();
		authority2.setAuthority("SPONSOR");

		raffles = this.raffleService.findOrderedByMaxDate(1, 3);
		Assert.notNull(raffles);

		bargains = this.bargainService.findBargains(1, 3, "sponsorship", 0);
		Assert.notNull(bargains);
		//Vemos si es user qué plan tiene
		plan = null;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			plan = this.planService.findByUserId(user.getId());
		}

		//Vemos si es un sponsor
		isSponsor = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2))
			isSponsor = true;

		result = new ModelAndView("welcome/index");
		result.addObject("raffles", raffles.getContent());
		result.addObject("bargains", bargains.getContent());
		result.addObject("plan", plan);
		result.addObject("isSponsor", isSponsor);
		result.addObject("slogan", this.configurationService.findSlogan());

		return result;
	}
}
