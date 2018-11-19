
package controllers.administrator;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.InternationalizationService;
import controllers.AbstractController;
import domain.Configuration;
import forms.ConfigurationForm;

@Controller
@RequestMapping("/configuration/administrator")
public class ConfigurationAdministratorController extends AbstractController {

	// Services
	@Autowired
	private ConfigurationService		configurationService;

	@Autowired
	private InternationalizationService	internationalizationService;


	// Constructor
	public ConfigurationAdministratorController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Configuration configuration;
		ConfigurationForm configurationForm;
		final Locale locale;
		String code;

		configuration = this.configurationService.findUnique();
		Assert.notNull(configuration);

		locale = LocaleContextHolder.getLocale();
		code = locale.getLanguage();

		configurationForm = new ConfigurationForm();
		configurationForm.setConfiguration(configuration);
		configurationForm.setWelcomeMessage(this.internationalizationService.findByCountryCodeAndMessageCode(code, configuration.getName()).getValue());

		result = new ModelAndView("configuration/display");

		result.addObject("configurationForm", configurationForm);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView result;
		Configuration configuration;
		ConfigurationForm configurationForm;
		final Locale locale;
		String code;

		locale = LocaleContextHolder.getLocale();
		code = locale.getLanguage();

		configuration = this.configurationService.findUnique();
		Assert.notNull(configuration);

		configurationForm = new ConfigurationForm();
		configurationForm.setConfiguration(configuration);
		configurationForm.setWelcomeMessage(this.internationalizationService.findByCountryCodeAndMessageCode(code, configuration.getName()).getValue());

		result = this.createEditModelAndView(configurationForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(ConfigurationForm configurationForm, final BindingResult binding) {
		ModelAndView result;

		configurationForm = this.configurationService.reconstruct(configurationForm, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(configurationForm);
		else
			try {
				this.configurationService.save(configurationForm);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(configurationForm, "configuration.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final ConfigurationForm configurationForm) {
		ModelAndView result;

		result = this.createEditModelAndView(configurationForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final ConfigurationForm configurationForm, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("configuration/edit");

		result.addObject("configurationForm", configurationForm);
		result.addObject("message", messageCode);

		return result;
	}
}
