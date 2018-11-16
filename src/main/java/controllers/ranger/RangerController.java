
package controllers.ranger;

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
import services.CurriculumService;
import services.RangerService;
import controllers.AbstractController;
import domain.Actor;
import domain.Ranger;

@Controller
@RequestMapping(value = "/actor/ranger")
public class RangerController extends AbstractController {

	// Services
	@Autowired
	private RangerService			rangerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private CurriculumService		curriculumService;


	// Constructor
	public RangerController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Ranger ranger;

		ranger = this.rangerService.create();

		result = this.createEditModelAndView(ranger);

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int rangerId) {
		ModelAndView result;
		final Ranger ranger;
		UserAccount userAccount;
		Actor actor;
		int idOfPrincipal;
		int curriculumId;
		boolean hasCurriculum;

		hasCurriculum = false;
		idOfPrincipal = 0;
		curriculumId = 0;
		//try {
		if (LoginService.isAuthenticated() == true) {
			userAccount = LoginService.getPrincipal();
			actor = this.actorService.findByUserAccountId(userAccount.getId());
			idOfPrincipal = actor.getId();
		}
		//} catch (final IllegalArgumentException e) {

		//}

		ranger = this.rangerService.findOne(rangerId);
		Assert.notNull(ranger);
		if (this.curriculumService.findByRangerUserAccountId(ranger.getUserAccount().getId()) != null) {
			hasCurriculum = true;
			curriculumId = this.curriculumService.findByRangerUserAccountId(ranger.getUserAccount().getId()).getId();
		}
		result = new ModelAndView("actor/display");

		result.addObject("actor", ranger);
		result.addObject("idOfPrincipal", idOfPrincipal);
		result.addObject("hasCurriculum", hasCurriculum);
		result.addObject("curriculumId", curriculumId);
		result.addObject("actorType", "ranger");

		return result;

	}

	// Edition
	//	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	//	public ModelAndView edit(@RequestParam final int rangerId) {
	//		ModelAndView result;
	//		Ranger ranger;
	//
	//		ranger = this.rangerService.findOne(rangerId);
	//		Assert.notNull(ranger);
	//
	//		result = this.createEditModelAndView(ranger);
	//
	//		return result;
	//	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Ranger ranger, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			System.out.println(binding);
			result = this.createEditModelAndView(ranger);
		} else
			try {
				this.rangerService.save(ranger);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(ranger, "actor.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Ranger ranger) {
		ModelAndView result;

		result = this.createEditModelAndView(ranger, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Ranger ranger, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		UserAccount userAccount;
		int userAccountId;
		String countryCode;
		String requestURI;

		requestURI = "actor/ranger/edit.do";

		countryCode = this.configurationService.findCountryCode();

		userAccountId = 0;
		canEdit = false;

		System.out.println("AAAAA" + messageCode);

		if (ranger.getId() == 0)
			canEdit = true;
		else {
			//try {
			if (LoginService.isAuthenticated() == true) {
				userAccount = LoginService.getPrincipal();
				userAccountId = userAccount.getId();
			}
			//} catch (final IllegalArgumentException e) {

			//}
			if (ranger.getUserAccount().getId() == userAccountId)
				canEdit = true;

		}
		if (ranger.getId() > 0)
			result = new ModelAndView("ranger/edit");
		else
			result = new ModelAndView("ranger/create");

		result.addObject("modelo", "ranger");
		result.addObject("ranger", ranger);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
