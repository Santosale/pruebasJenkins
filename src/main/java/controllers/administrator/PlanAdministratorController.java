
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.PlanService;
import controllers.AbstractController;
import domain.Plan;

@Controller
@RequestMapping("/plan/administrator")
public class PlanAdministratorController extends AbstractController {

	// Services
	@Autowired
	private PlanService	planService;


	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int planId) {
		ModelAndView result;
		Plan plan;

		plan = this.planService.findOneToEdit(planId);

		result = this.editModelAndView(plan);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Plan plan, final BindingResult binding) {
		ModelAndView result;

		plan = this.planService.reconstruct(plan, binding);

		if (binding.hasErrors())
			result = this.editModelAndView(plan);
		else
			try {
				this.planService.save(plan);
				result = new ModelAndView("redirect:/plan/display.do");
			} catch (final Throwable oops) {
				result = this.editModelAndView(plan, "plan.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView editModelAndView(final Plan plan) {
		ModelAndView result;

		result = this.editModelAndView(plan, null);

		return result;
	}

	protected ModelAndView editModelAndView(final Plan plan, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("plan/edit");

		result.addObject("plan", plan);
		result.addObject("message", messageCode);

		return result;
	}

}
