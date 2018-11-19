
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.LevelService;
import controllers.AbstractController;
import domain.Level;

@Controller
@RequestMapping("/level/administrator")
public class LevelAdministratorController extends AbstractController {

	// Services
	@Autowired
	private LevelService	levelService;


	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView request() {
		ModelAndView result;
		Level level;

		level = this.levelService.create();

		result = this.createEditModelAndView(level);

		return result;

	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int levelId) {
		ModelAndView result;
		Level level;

		level = this.levelService.findOneToEdit(levelId);

		result = this.createEditModelAndView(level);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Level level, final BindingResult binding) {
		ModelAndView result;

		level = this.levelService.reconstruct(level, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(level);
		else
			try {
				this.levelService.save(level);

				result = new ModelAndView("redirect:/level/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(level, "level.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Level level, final BindingResult binding) {
		ModelAndView result;

		try {
			this.levelService.delete(level);
			result = new ModelAndView("redirect:/level/list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(level, "level.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Level level) {
		ModelAndView result;

		result = this.createEditModelAndView(level, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Level level, final String messageCode) {
		ModelAndView result;
		Boolean canDeleteLevel;
		Boolean isMinimumLevel;
		Boolean isMaximumLevel;

		isMinimumLevel = false;
		isMaximumLevel = false;
		canDeleteLevel = false;
		if (level.getId() != 0) {
			result = new ModelAndView("level/edit");
			result.addObject("previousMinPoints", this.levelService.findOne(level.getId()).getMinPoints());
			result.addObject("previousMaxPoints", this.levelService.findOne(level.getId()).getMaxPoints());
			if (this.levelService.findOne(level.getId()).getId() == this.levelService.minLevel().getId())
				isMinimumLevel = true;
			else if (this.levelService.findOne(level.getId()).getId() == this.levelService.maxLevel().getId())
				isMaximumLevel = true;
		} else
			result = new ModelAndView("level/create");

		if (this.levelService.findAll().size() > 2)
			canDeleteLevel = true;
		result.addObject("level", level);
		result.addObject("message", messageCode);
		result.addObject("canDeleteLevel", canDeleteLevel);
		result.addObject("isMinimumLevel", isMinimumLevel);
		result.addObject("isMaximumLevel", isMaximumLevel);
		result.addObject("minimumPoints", this.levelService.minPoints());
		result.addObject("maximumPoints", this.levelService.maxPoints());

		return result;
	}
}
