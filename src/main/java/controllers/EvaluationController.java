package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.EvaluationService;

import controllers.AbstractController;
import domain.Evaluation;

@Controller
@RequestMapping("/evaluation")
public class EvaluationController extends AbstractController {

	// Services
	@Autowired
	private EvaluationService evaluationService;
	
	// Constructor
	public EvaluationController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page, @RequestParam int companyId) {
		ModelAndView result;
		Page<Evaluation> evaluations;
				
		evaluations = this.evaluationService.findByCompanyId(companyId, page, 5);
		Assert.notNull(evaluations);
		
		result = new ModelAndView("evaluation/list");
		result.addObject("requestURI", "evaluation/list.do?companyId=" + companyId);
		result.addObject("page", page);
		result.addObject("pageNumber", evaluations.getTotalPages());
		result.addObject("evaluations", evaluations.getContent());
		
		return result;
	}
	
	// Display
		@RequestMapping(value="/display", method = RequestMethod.GET)
		public ModelAndView display(@RequestParam int evaluationId) {
			ModelAndView result;
			Evaluation evaluation;
					
			evaluation = this.evaluationService.findOne(evaluationId);
			Assert.notNull(evaluation);
			
			result = new ModelAndView("evaluation/display");
			result.addObject("requestURI", "evaluation/list.do");
			result.addObject("evaluation", evaluation);
			
			return result;
		}
	
}
