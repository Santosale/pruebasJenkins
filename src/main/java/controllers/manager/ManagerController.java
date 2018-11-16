
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
import services.ActorService;
import services.ConfigurationService;
import services.ManagerService;
import controllers.AbstractController;
import domain.Actor;
import domain.Manager;

@Controller
@RequestMapping(value = "/actor/manager")
public class ManagerController extends AbstractController {

	// Services
	@Autowired
	private ManagerService			managerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	// Constructor
	public ManagerController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Manager manager;

		manager = this.managerService.create();

		result = this.createEditModelAndView(manager);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int managerId) {
		ModelAndView result;
		Manager manager;
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

		//	}

		manager = this.managerService.findOne(managerId);
		Assert.notNull(manager);
		result = new ModelAndView("actor/display");

		result.addObject("actor", manager);
		result.addObject("actorType", "manager");
		result.addObject("idOfPrincipal", idOfPrincipal);

		return result;

	}

	// Edition
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView edit(@RequestParam final int managerId) {
	//		ModelAndView result;
	//		Manager manager;
	//
	//		manager = this.managerService.findOne(managerId);
	//		Assert.notNull(manager);
	//
	//		result = this.createEditModelAndView(manager);
	//
	//		return result;
	//	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Manager manager, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			System.out.println(binding.getAllErrors());
			result = this.createEditModelAndView(manager);
		} else
			try {
				this.managerService.save(manager);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(manager, "actor.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Manager manager) {
		ModelAndView result;

		result = this.createEditModelAndView(manager, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Manager manager, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		String countryCode;
		String requestURI;

		requestURI = "actor/manager/edit.do";

		countryCode = this.configurationService.findCountryCode();

		canEdit = false;
		if (manager.getId() == 0)
			canEdit = true;
		else if (manager.getUserAccount().getId() == LoginService.getPrincipal().getId())
			canEdit = true;

		if (manager.getId() > 0)
			result = new ModelAndView("manager/edit");
		else
			result = new ModelAndView("manager/create");

		result.addObject("modelo", "manager");
		result.addObject("manager", manager);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
