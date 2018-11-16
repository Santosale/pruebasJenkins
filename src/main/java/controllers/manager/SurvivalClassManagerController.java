
package controllers.manager;

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
import services.ManagerService;
import services.SurvivalClassService;
import services.TripService;
import controllers.AbstractController;
import domain.Manager;
import domain.SurvivalClass;
import domain.Trip;

@Controller
@RequestMapping("/survivalClass/manager")
public class SurvivalClassManagerController extends AbstractController {

	// Services

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private ManagerService			managerService;


	// Constructor
	public SurvivalClassManagerController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		SurvivalClass survivalClass;
		Trip trip;

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);

		survivalClass = this.survivalClassService.create(trip);

		result = this.createEditModelAndView(survivalClass);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int survivalClassId) {
		ModelAndView result;
		SurvivalClass survivalClass;

		survivalClass = this.survivalClassService.findOne(survivalClassId);
		Assert.notNull(survivalClass);

		result = this.createEditModelAndView(survivalClass);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final SurvivalClass survivalClass, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditModelAndView(survivalClass);
		else
			try {
				this.survivalClassService.save(survivalClass);
				result = new ModelAndView("redirect:/survivalClass/list.do?tripId=" + survivalClass.getTrip().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(survivalClass, "survivalClass.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final SurvivalClass survivalClass, final BindingResult binding) {
		ModelAndView result;

		try {
			this.survivalClassService.delete(survivalClass);
			result = new ModelAndView("redirect:/survivalClass/list.do?tripId=" + survivalClass.getTrip().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(survivalClass, "survivalClass.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final SurvivalClass survivalClass) {
		ModelAndView result;

		result = this.createEditModelAndView(survivalClass, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final SurvivalClass survivalClass, final String messageCode) {
		final ModelAndView result;
		UserAccount userAccount;
		Manager manager;
		boolean isHisSurvivalClass;

		isHisSurvivalClass = false;
		userAccount = LoginService.getPrincipal();
		manager = this.managerService.findByUserAccountId(userAccount.getId());

		if (survivalClass.getId() > 0) {
			if (this.survivalClassService.findByManagerId(manager.getId()).contains(survivalClass))
				isHisSurvivalClass = true;
		} else if (this.tripService.findByManagerUserAccountId(manager.getUserAccount().getId()).contains(survivalClass.getTrip()))
			isHisSurvivalClass = true;

		if (survivalClass.getId() > 0)
			result = new ModelAndView("survivalClass/edit");
		else
			result = new ModelAndView("survivalClass/create");

		result.addObject("survivalClass", survivalClass);
		result.addObject("isHisSurvivalClass", isHisSurvivalClass);
		result.addObject("message", messageCode);

		return result;
	}
}
