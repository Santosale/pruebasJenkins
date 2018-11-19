
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CompanyService;
import controllers.AbstractController;
import domain.Company;

@Controller
@RequestMapping(value = "/company/administrator")
public class CompanyAdministratorController extends AbstractController {

	// Supporting services
	@Autowired
	private CompanyService	companyService;


	// Constructor
	public CompanyAdministratorController() {
		super();
	}

	@RequestMapping(value = "/companiesWithMoreTags", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Company> actors;

		actors = this.companyService.companiesWithMoreTags(page, 5);
		Assert.notNull(actors);

		result = new ModelAndView("company/list");
		result.addObject("actors", actors.getContent());
		result.addObject("pageNumber", actors.getTotalPages());
		result.addObject("page", page);
		result.addObject("model", "company");
		result.addObject("requestURI", "company/administrator/companiesWithMoreTags.do");
		
		return result;
	}

	@RequestMapping(value = "/writerOfMorePercentage15", method = RequestMethod.GET)
	public ModelAndView writerOfMorePercentage15(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Company> actors;

		actors = this.companyService.findWithMoreAvgPercentageSurveys15(page, 5);
		Assert.notNull(actors);

		result = new ModelAndView("company/list");
		result.addObject("actors", actors.getContent());
		result.addObject("pageNumber", actors.getTotalPages());
		result.addObject("page", page);
		result.addObject("model", "company");
		result.addObject("requestURI", "company/administrator/writerOfMorePercentage15.do");

		return result;
	}

	@RequestMapping(value = "/writerOfMorePercentage10", method = RequestMethod.GET)
	public ModelAndView writerOfMorePercentage10(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Company> actors;

		actors = this.companyService.findWithMoreAvgPercentageSurveys10(page, 5);
		Assert.notNull(actors);

		result = new ModelAndView("company/list");
		result.addObject("actors", actors.getContent());
		result.addObject("pageNumber", actors.getTotalPages());
		result.addObject("page", page);
		result.addObject("model", "company");
		result.addObject("requestURI", "company/administrator/writerOfMorePercentage10.do");

		return result;
	}

	@RequestMapping(value = "/writerOfMorePercentage5", method = RequestMethod.GET)
	public ModelAndView writerOfMorePercentage5(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Company> actors;

		actors = this.companyService.findWithMoreAvgPercentageSurveys5(page, 5);
		Assert.notNull(actors);

		result = new ModelAndView("company/list");
		result.addObject("actors", actors.getContent());
		result.addObject("pageNumber", actors.getTotalPages());
		result.addObject("page", page);
		result.addObject("model", "company");
		result.addObject("requestURI", "company/administrator/writerOfMorePercentage5.do");

		return result;
	}

}
