
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.PlanService;
import services.SubscriptionService;
import services.UserService;
import domain.Plan;

@Controller
@RequestMapping("/plan")
public class PlanController extends AbstractController {

	// Services
	@Autowired
	private PlanService			planService;

	@Autowired
	private UserService			userService;

	@Autowired
	private SubscriptionService	subscriptionService;


	// Constructor
	public PlanController() {
		super();
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Collection<Plan> plans;
		Authority authority;
		Boolean canCreateSubscription;

		canCreateSubscription = false;
		authority = new Authority();
		authority.setAuthority("USER");
		plans = this.planService.findAll();

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority))
			if (this.subscriptionService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null)
				canCreateSubscription = true;

		result = new ModelAndView("plan/display");
		result.addObject("plans", plans);
		result.addObject("canCreateSubscription", canCreateSubscription);

		return result;
	}

}
