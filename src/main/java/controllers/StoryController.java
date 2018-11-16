
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ActorService;
import services.ApplicationService;
import services.StoryService;
import domain.Actor;
import domain.Story;

@Controller
@RequestMapping("/story")
public class StoryController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private StoryService		storyService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ActorService		actorService;


	// Constructors -----------------------------------------------------------

	public StoryController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<Story> stories;
		boolean editar;
		Actor actor;

		editar = false;
		if (LoginService.isAuthenticated()) {
			actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			if (this.applicationService.findByTripIdAndExplorerId(tripId, actor.getId()) != null)
				editar = true;
		}

		stories = this.storyService.findByTripId(tripId);

		result = new ModelAndView("story/list");
		result.addObject("stories", stories);
		result.addObject("editar", editar);
		result.addObject("requestURI", "story/list.do");

		return result;
	}
	// Display ----------------------------------------------------------------

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int storyId) {
		ModelAndView result;
		Story story;

		story = this.storyService.findOne(storyId);

		result = new ModelAndView("story/display");
		result.addObject("story", story);
		result.addObject("requestURI", "story/display.do");

		return result;
	}
}
