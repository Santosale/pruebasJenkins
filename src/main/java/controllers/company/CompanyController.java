
package controllers.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CompanyService;
import controllers.AbstractController;
import domain.Company;
import forms.CompanyForm;

@Controller
@RequestMapping(value = "/actor/company")
public class CompanyController extends AbstractController {

	// Services
	@Autowired
	private CompanyService	companyService;

	// Constructor
	public CompanyController() {
		super();
	}
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Company> actors;
		
		actors = this.companyService.findAllPaginated(page, 5);
		Assert.notNull(actors);
		
		result = new ModelAndView("company/list");
		result.addObject("actors", actors.getContent());
		result.addObject("pageNumber", actors.getTotalPages());
		result.addObject("page", page);
		result.addObject("model", "company");
		result.addObject("requestURI", "actor/company/list.do");
		
		return result;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		CompanyForm companyForm;
		
		companyForm = new CompanyForm();
		
		result = new ModelAndView("company/edit");
		result.addObject("requestURI", "actor/company/edit.do");
		result.addObject("companyForm", companyForm);
		result.addObject("model", "company");
		
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final CompanyForm companyForm, final BindingResult binding, @RequestParam final String model) {
		ModelAndView result;
		Company company;
		boolean next;

		next = true;
		result = null;
		company = null;
		try {
			company = this.companyService.reconstruct(companyForm, binding);
		} catch (final Throwable e) {

			if (binding.hasErrors())
				result = this.createEditModelAndView(companyForm);
			else {
				result = this.createEditModelAndView(companyForm, "actor.commit.error");
			}


			next = false;
		}

		if (next)
			if (binding.hasErrors())
				result = this.createEditModelAndView((CompanyForm) companyForm);
			else
				try {
					this.companyService.save(company);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(companyForm, "actor.commit.error");
				}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final CompanyForm companyForm) {
		ModelAndView result;

		result = this.createEditModelAndView(companyForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final CompanyForm companyForm, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("company/edit");

		result.addObject("model", "company");
		result.addObject("companyForm", companyForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", "actor/company/edit.do");

		return result;
	}

}
