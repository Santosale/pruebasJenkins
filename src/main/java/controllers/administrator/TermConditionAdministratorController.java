
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.InternationalizationService;
import controllers.AbstractController;
import domain.Internationalization;

@Controller
@RequestMapping("/termCondition/administrator")
public class TermConditionAdministratorController extends AbstractController {

	// Services
	@Autowired
	private InternationalizationService	internationalizationService;


	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;

		String termConditionEs;
		String termConditionEn;

		termConditionEs = this.internationalizationService.findByCountryCodeAndMessageCode("es", "term.condition").getValue();
		termConditionEn = this.internationalizationService.findByCountryCodeAndMessageCode("en", "term.condition").getValue();

		result = new ModelAndView("termCondition/display");

		result.addObject("termConditionSpanish", termConditionEs);
		result.addObject("termConditionEnglish", termConditionEn);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final String code) {
		ModelAndView result;
		Internationalization internationalization;

		Assert.isTrue(code != null && (code.equals("en") || code.equals("es")));

		if (code.equals("en"))
			internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(code, "term.condition");
		else
			internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(code, "term.condition");

		result = this.createEditModelAndView(internationalization);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Internationalization internationalization, final BindingResult binding) {
		ModelAndView result;

		internationalization = this.internationalizationService.reconstruct(internationalization, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(internationalization);
		else
			try {
				this.internationalizationService.save(internationalization);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(internationalization, "internationalization.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Internationalization internationalization) {
		ModelAndView result;

		result = this.createEditModelAndView(internationalization, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Internationalization internationalization, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("termCondition/edit");

		result.addObject("internationalization", internationalization);
		result.addObject("message", messageCode);

		return result;
	}
}
