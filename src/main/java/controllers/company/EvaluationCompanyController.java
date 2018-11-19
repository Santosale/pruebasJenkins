package controllers.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.CompanyService;
import services.EvaluationService;

import controllers.AbstractController;
import domain.Evaluation;

@Controller
@RequestMapping("/evaluation/company")
public class EvaluationCompanyController extends AbstractController {

	// Services
	@Autowired
	private EvaluationService evaluationService;
	
	@Autowired
	private CompanyService companyService;
	
	// Constructor
	public EvaluationCompanyController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page) {
		ModelAndView result;
		Page<Evaluation> evaluations;
				
		evaluations = this.evaluationService.findByCompanyId(this.companyService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(evaluations);
		
		result = new ModelAndView("evaluation/list");
		result.addObject("requestURI", "evaluation/company/list.do");
		result.addObject("page", page);
		result.addObject("pageNumber", evaluations.getTotalPages());
		result.addObject("evaluations", evaluations.getContent());
		
		return result;
	}
	
}
