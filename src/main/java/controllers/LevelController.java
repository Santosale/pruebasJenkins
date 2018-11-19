
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.LevelService;
import domain.Level;

@Controller
@RequestMapping("/level")
public class LevelController extends AbstractController {

	// Services
	@Autowired
	private LevelService			levelService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public LevelController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Level> levels;

		levels = this.levelService.findAllPaginated(page, 5);
		Assert.notNull(levels);

		result = new ModelAndView("level/list");
		result.addObject("pageNumber", levels.getTotalPages());
		result.addObject("page", page);
		result.addObject("levels", levels.getContent());
		result.addObject("requestURI", "level/list.do");

		return result;
	}

	// List
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView list(final int levelId) {
		ModelAndView result;
		Level level;
		String defaultImage;

		defaultImage = this.configurationService.findDefaultImage();
		level = this.levelService.findOne(levelId);
		Assert.notNull(level);

		result = new ModelAndView("level/display");
		result.addObject("level", level);
		result.addObject("linkBroken", super.checkLinkImage(level.getImage()));
		result.addObject("defaultImage", defaultImage);

		return result;
	}
}
