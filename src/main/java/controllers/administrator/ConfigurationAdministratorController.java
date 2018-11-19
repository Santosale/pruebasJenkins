
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import controllers.AbstractController;
import domain.Configuration;

@Controller
@RequestMapping("/configuration/administrator")
public class ConfigurationAdministratorController extends AbstractController {

	// Services
	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public ConfigurationAdministratorController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Configuration configuration;

		configuration = this.configurationService.findUnique();

		result = new ModelAndView("configuration/display");

		result.addObject("configuration", configuration);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView result;

		result = this.createEditModelAndView(this.configurationService.findUnique());

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Configuration configuration, final BindingResult binding) {
		ModelAndView result;

		configuration = this.configurationService.reconstruct(configuration, binding);

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

	@RequestMapping(value = "/searchTabooWord", method = RequestMethod.GET)
	public ModelAndView findSuspicious() {
		ModelAndView result;

		this.configurationService.updateTabooWords();

		result = new ModelAndView("redirect:/");

		return result;
	}

	// Ancillary methods
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
