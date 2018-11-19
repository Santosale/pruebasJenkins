
package controllers.moderator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.EvaluationService;
import controllers.AbstractController;
import domain.Evaluation;

@Controller
@RequestMapping("/evaluation/moderator")
public class EvaluationModeratorController extends AbstractController {

	// Services
	@Autowired
	private EvaluationService	evaluationService;
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page) {
		ModelAndView result;
		Page<Evaluation> evaluations;
				
		evaluations = this.evaluationService.findAllEvaluations(page, 5);
		Assert.notNull(evaluations);
		
		result = new ModelAndView("evaluation/list");
		result.addObject("requestURI", "evaluation/moderator/list.do");
		result.addObject("evaluations", evaluations.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", evaluations.getTotalPages());
				
		return result;
	}
	
	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int evaluationId) {
		ModelAndView result;
		Evaluation evaluation;

		evaluation = this.evaluationService.findOne(evaluationId);
		Assert.notNull(evaluation);

		result = this.createEditModelAndView(evaluation);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Evaluation evaluation, final BindingResult binding) {
		ModelAndView result;
		
		evaluation = this.evaluationService.reconstruct(evaluation, binding);

		try {
			this.evaluationService.deleteModerator(evaluation);
			result = new ModelAndView("redirect:/evaluation/moderator/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(evaluation, "evaluation.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Evaluation evaluation) {
		ModelAndView result;

		result = this.createEditModelAndView(evaluation, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Evaluation evaluation, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("evaluation/edit");

		result.addObject("evaluation", evaluation);
		result.addObject("actor", "moderator");
		result.addObject("message", messageCode);
		result.addObject("requestURI", "evaluation/moderator/edit.do");

		return result;
	}

}
