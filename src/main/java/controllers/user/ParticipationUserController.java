
package controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ParticipationService;
import services.UserService;
import controllers.AbstractController;
import domain.Participation;

@Controller
@RequestMapping("/participation/user")
public class ParticipationUserController extends AbstractController {

	// Services

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private UserService				userService;


	// Constructor
	public ParticipationUserController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Participation> participations;

		participations = this.participationService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(participations);

		result = new ModelAndView("participation/list");
		result.addObject("pageNumber", participations.getTotalPages());
		result.addObject("page", page);
		result.addObject("participations", participations.getContent());
		result.addObject("requestURI", "participation/user/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int grouponId) {
		ModelAndView result;
		Participation participation;

		participation = this.participationService.create(grouponId);

		result = this.createEditModelAndView(participation);

		return result;

	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int participationId) {
		ModelAndView result;
		Participation participation;

		participation = this.participationService.findOneToEdit(participationId);

		result = this.createEditModelAndView(participation);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Participation participation, final BindingResult binding) {
		ModelAndView result;

		participation = this.participationService.reconstruct(participation, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(participation);
		else
			try {
				this.participationService.save(participation);

				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(participation, "participation.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Participation participation, final BindingResult binding) {
		ModelAndView result;

		try {
			this.participationService.delete(participation);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(participation, "participation.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Participation participation) {
		ModelAndView result;

		result = this.createEditModelAndView(participation, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Participation participation, final String messageCode) {
		ModelAndView result;

		if (participation.getId() != 0)
			result = new ModelAndView("participation/edit");
		else
			result = new ModelAndView("participation/create");

		result.addObject("participation", participation);
		result.addObject("message", messageCode);

		return result;
	}

}
