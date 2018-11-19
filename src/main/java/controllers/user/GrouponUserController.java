
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
import services.GrouponService;
import services.ParticipationService;
import services.UserService;
import controllers.AbstractController;
import domain.Groupon;

@Controller
@RequestMapping("/groupon/user")
public class GrouponUserController extends AbstractController {

	// Services

	@Autowired
	private UserService				userService;

	@Autowired
	private GrouponService			grouponService;

	@Autowired
	private ParticipationService	participationService;


	// Constructor
	public GrouponUserController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Groupon> groupons;

		groupons = this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(groupons);

		result = new ModelAndView("groupon/list");
		result.addObject("pageNumber", groupons.getTotalPages());
		result.addObject("page", page);
		result.addObject("groupons", groupons.getContent());
		result.addObject("requestURI", "groupon/user/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView request() {
		ModelAndView result;
		Groupon groupon;

		groupon = this.grouponService.create();

		result = this.createEditModelAndView(groupon);

		return result;

	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int grouponId) {
		ModelAndView result;
		Groupon groupon;

		groupon = this.grouponService.findOneToEdit(grouponId);

		result = this.createEditModelAndView(groupon);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Groupon groupon, final BindingResult binding) {
		ModelAndView result;

		groupon = this.grouponService.reconstruct(groupon, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(groupon);
		else
			try {
				this.grouponService.save(groupon);

				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(groupon, "groupon.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Groupon groupon, final BindingResult binding) {
		ModelAndView result;

		try {
			this.grouponService.delete(groupon);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(groupon, "groupon.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Groupon groupon) {
		ModelAndView result;

		result = this.createEditModelAndView(groupon, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Groupon groupon, final String messageCode) {
		ModelAndView result;
		Boolean canEditDiscountCode;

		canEditDiscountCode = false;
		if (groupon.getId() != 0)
			result = new ModelAndView("groupon/edit");
		else
			result = new ModelAndView("groupon/create");

		if (groupon.getId() != 0)
			if (this.participationService.requestedProductsByGrouponId(groupon.getId()) >= groupon.getMinAmountProduct())
				canEditDiscountCode = true;

		result.addObject("groupon", groupon);
		result.addObject("message", messageCode);
		result.addObject("canEditDiscountCode", canEditDiscountCode);

		return result;
	}

}
