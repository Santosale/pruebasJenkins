
package controllers.ranger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ConfigurationService;
import services.CurriculumService;
import services.RangerService;
import controllers.AbstractController;
import domain.Curriculum;

@Controller
@RequestMapping("/curriculum/ranger")
public class CurriculumRangerController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private CurriculumService		curriculumService;

	@Autowired
	private RangerService			rangerService;

	@Autowired
	private ConfigurationService	configurationService;


	//Constructors ---------------------------
	public CurriculumRangerController() {
		super();
	}

	//Creation --------------------------------
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Curriculum curriculum;

		curriculum = this.curriculumService.create(this.rangerService.findByUserAccountId(LoginService.getPrincipal().getId()));

		result = this.createEditModelAndView(curriculum);

		return result;
	}
	//Edition ---------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int curriculumId) {
		ModelAndView result;
		Curriculum curriculum;

		curriculum = this.curriculumService.findOne(curriculumId);
		Assert.notNull(curriculum);
		result = this.createEditModelAndView(curriculum);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Curriculum curriculum, final BindingResult binding) {
		ModelAndView result;
		Curriculum saved;

		if (binding.hasErrors())
			result = this.createEditModelAndView(curriculum);
		else
			try {
				saved = this.curriculumService.save(curriculum);
				result = new ModelAndView("redirect:/curriculum/display.do?curriculumId=" + String.valueOf(saved.getId()));
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(curriculum, "curriculum.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Curriculum curriculum, final BindingResult binding) {
		ModelAndView result;

		try {
			this.curriculumService.delete(curriculum);
			result = new ModelAndView("redirect:/");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(curriculum, "curriculum.commit.error");
		}

		return result;
	}

	//Ancillary methods -----------------------
	protected ModelAndView createEditModelAndView(final Curriculum curriculum) {
		ModelAndView result;

		result = this.createEditModelAndView(curriculum, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Curriculum curriculum, final String messageCode) {
		ModelAndView result;
		String countryCode;

		if (curriculum.getId() > 0)
			result = new ModelAndView("curriculum/edit");
		else
			result = new ModelAndView("curriculum/create");

		countryCode = this.configurationService.findCountryCode();

		result.addObject("curriculum", curriculum);
		result.addObject("countryCode", countryCode);
		result.addObject("message", messageCode);

		return result;

	}
}
