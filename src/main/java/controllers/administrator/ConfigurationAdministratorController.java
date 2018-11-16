
package controllers.administrator;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.InternationalizationService;
import controllers.AbstractController;
import domain.Configuration;
import domain.Internationalization;

@Controller
@RequestMapping("/configuration/administrator")
public class ConfigurationAdministratorController extends AbstractController {

	// Services
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private InternationalizationService internationalizationService;


	// Constructor
	public ConfigurationAdministratorController() {
		super();
	}
	
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Configuration configuration;
		Collection<Configuration> configurations;
		String welcomeMessageEs;
		String welcomeMessageEn;

		configuration = null;
		configurations = this.configurationService.findAll();

		for (final Configuration c : configurations) {
			configuration = c;
			break;
		}
		
		welcomeMessageEs = this.internationalizationService.findByCountryCodeAndMessageCode("es", configuration.getWelcomeMessage()).getValue();
		welcomeMessageEn = this.internationalizationService.findByCountryCodeAndMessageCode("en", configuration.getWelcomeMessage()).getValue();

		Assert.notNull(configuration);
		result = new ModelAndView("configuration/display");

		result.addObject("configuration", configuration);
		result.addObject("welcomeMessageEs", welcomeMessageEs);
		result.addObject("welcomeMessageEn", welcomeMessageEn);

		return result;

	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int configurationId) {
		ModelAndView result;
		Configuration configuration;

		configuration = this.configurationService.findOne(configurationId);
		Assert.notNull(configuration);

		result = this.createEditModelAndView(configuration);

		return result;
	}
	
	@RequestMapping(value = "/editWelcomeMessage", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int configurationId, @RequestParam final String countryCode) {
		ModelAndView result;
		Configuration configuration;
		Internationalization internationalization;
		
		configuration = this.configurationService.findOne(configurationId);
		Assert.notNull(configuration);
		
		internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, configuration.getWelcomeMessage());

		result = this.createEditInternationalizationModelAndView(internationalization);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Configuration configuration, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditModelAndView(configuration);
		else
			try {
				this.configurationService.save(configuration);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(configuration, "configuration.commit.error");
			}

		return result;
	}
	
	@RequestMapping(value = "/editWelcomeMessage", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Internationalization internationalization, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditInternationalizationModelAndView(internationalization);
		else
			try {
				this.internationalizationService.save(internationalization);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditInternationalizationModelAndView(internationalization, "internationalization.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditInternationalizationModelAndView(final Internationalization internationalization) {
		ModelAndView result;

		result = this.createEditInternationalizationModelAndView(internationalization, null);

		return result;
	}
	
	protected ModelAndView createEditInternationalizationModelAndView(final Internationalization internationalization, final String messageCode) {
		ModelAndView result;
		
		result = new ModelAndView("configuration/editWelcomeMessage");
		
		result.addObject("internationalization", internationalization);
		result.addObject("message", messageCode);
		
		return result;
	}

	protected ModelAndView createEditModelAndView(final Configuration configuration) {
		ModelAndView result;

		result = this.createEditModelAndView(configuration, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Configuration configuration, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("configuration/edit");

		result.addObject("configuration", configuration);
		result.addObject("message", messageCode);

		return result;
	}
}
