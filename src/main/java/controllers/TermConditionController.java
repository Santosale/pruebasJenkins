
package controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.InternationalizationService;

@Controller
@RequestMapping("/termCondition")
public class TermConditionController extends AbstractController {

	// Services
	@Autowired
	private InternationalizationService	internationalizationService;


	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		String termCondition;
		final Locale locale = LocaleContextHolder.getLocale();
		String code;

		code = locale.getLanguage();

		if (code.equals("es"))
			termCondition = this.internationalizationService.findByCountryCodeAndMessageCode("es", "term.condition").getValue();
		else
			termCondition = this.internationalizationService.findByCountryCodeAndMessageCode("en", "term.condition").getValue();

		result = new ModelAndView("termCondition/display");

		result.addObject("termCondition", termCondition);

		return result;

	}

}
