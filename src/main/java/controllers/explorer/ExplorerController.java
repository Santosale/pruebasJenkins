
package controllers.explorer;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ConfigurationService;
import services.ExplorerService;
import controllers.AbstractController;
import domain.Explorer;

@Controller
@RequestMapping(value = "/actor/explorer")
public class ExplorerController extends AbstractController {

	// Services
	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public ExplorerController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Explorer explorer;

		explorer = this.explorerService.create();

		result = this.createEditModelAndView(explorer);

		return result;
	}

	// Edition
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView edit(@RequestParam final int explorerId) {
	//		ModelAndView result;
	//		Explorer explorer;
	//
	//		explorer = this.explorerService.findOne(explorerId);
	//		Assert.notNull(explorer);
	//
	//		result = this.createEditModelAndView(explorer);
	//
	//		return result;
	//	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Explorer explorer, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(explorer);
		else
			try {
				this.explorerService.save(explorer);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(explorer, "actor.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Explorer explorer) {
		ModelAndView result;

		result = this.createEditModelAndView(explorer, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Explorer explorer, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		UserAccount userAccount;
		int userAccountId;
		String countryCode;
		String requestURI;

		requestURI = "actor/explorer/edit.do";

		countryCode = this.configurationService.findCountryCode();

		userAccountId = 0;
		canEdit = false;
		if (explorer.getId() == 0)
			canEdit = true;
		else {
			//			try {
			//				userAccount = LoginService.getPrincipal();
			//				userAccountId = userAccount.getId();
			//			} catch (final IllegalArgumentException e) {
			//
			//			}
			if (LoginService.isAuthenticated() == true) {
				userAccount = LoginService.getPrincipal();
				userAccountId = userAccount.getId();
			}
			if (explorer.getUserAccount().getId() == userAccountId)
				canEdit = true;

		}

		if (explorer.getId() > 0)
			result = new ModelAndView("explorer/edit");
		else
			result = new ModelAndView("explorer/create");

		result.addObject("modelo", "explorer");
		result.addObject("explorer", explorer);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
