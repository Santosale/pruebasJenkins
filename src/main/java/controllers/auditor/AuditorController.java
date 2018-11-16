
package controllers.auditor;

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
import services.AuditorService;
import services.ConfigurationService;
import controllers.AbstractController;
import domain.Actor;
import domain.Auditor;

@Controller
@RequestMapping(value = "/actor/auditor")
public class AuditorController extends AbstractController {

	// Services
	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	// Constructor
	public AuditorController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Auditor auditor;

		auditor = this.auditorService.create();

		result = this.createEditModelAndView(auditor);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int auditorId) {
		ModelAndView result;
		final Auditor auditor;
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

		auditor = this.auditorService.findOne(auditorId);
		Assert.notNull(auditor);
		result = new ModelAndView("actor/display");

		result.addObject("actor", auditor);
		result.addObject("actorType", "auditor");
		result.addObject("idOfPrincipal", idOfPrincipal);

		return result;
	}

	// Edition
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView edit(@RequestParam final int auditorId) {
	//		ModelAndView result;
	//		Auditor auditor;
	//
	//		auditor = this.auditorService.findOne(auditorId);
	//		Assert.notNull(auditor);
	//
	//		result = this.createEditModelAndView(auditor);
	//
	//		return result;
	//	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Auditor auditor, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(auditor);
		else
			try {
				this.auditorService.save(auditor);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(auditor, "actor.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Auditor auditor) {
		ModelAndView result;

		result = this.createEditModelAndView(auditor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Auditor auditor, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		String countryCode;
		String requestURI;

		requestURI = "actor/auditor/edit.do";

		countryCode = this.configurationService.findCountryCode();

		canEdit = false;

		if (auditor.getId() == 0)
			canEdit = true;
		else if (auditor.getUserAccount().getId() == LoginService.getPrincipal().getId())
			canEdit = true;

		if (auditor.getId() > 0)
			result = new ModelAndView("auditor/edit");
		else
			result = new ModelAndView("auditor/create");

		result.addObject("modelo", "auditor");
		result.addObject("auditor", auditor);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}
}
