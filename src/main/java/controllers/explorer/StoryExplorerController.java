
package controllers.explorer;

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

import security.LoginService;
import security.UserAccount;
import services.ExplorerService;
import services.StoryService;
import services.TripService;
import controllers.AbstractController;
import domain.Explorer;
import domain.Story;
import domain.Trip;

@Controller
@RequestMapping("/story/explorer")
public class StoryExplorerController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private StoryService	storyService;

	@Autowired
	private TripService		tripService;

	@Autowired
	private ExplorerService	explorerService;


	// Constructors -----------------------------------------------------------

	public StoryExplorerController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Story> stories;
		UserAccount user;
		Explorer explorer;

		user = LoginService.getPrincipal();

		explorer = this.explorerService.findByUserAccountId(user.getId());
		Assert.notNull(explorer);

		stories = this.storyService.findByExplorerId(explorer.getId());

		result = new ModelAndView("story/list");
		result.addObject("stories", stories);
		result.addObject("requestURI", "story/list.do");
		result.addObject("editar", true);

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

	// Creation ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		Story story;
		Trip trip;
		UserAccount user;
		Explorer explorer;

		user = LoginService.getPrincipal();

		explorer = this.explorerService.findByUserAccountId(user.getId());

		trip = this.tripService.findOne(tripId);

		story = this.storyService.create(trip, explorer);

		result = this.createEditModelAndView(story);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCreate(@Valid final Story story, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(story);
		else
			try {
				this.storyService.save(story);
				result = new ModelAndView("redirect:list.do?tripId=" + story.getTrip().getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(story, "story.commit.error");
			}

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int storyId) {
		ModelAndView result;
		Story story;

		story = this.storyService.findOne(storyId);
		Assert.notNull(story);

		result = this.createEditModelAndView(story);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Story story, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(story);
		else
			try {
				this.storyService.save(story);
				result = new ModelAndView("redirect:list.do?tripId=" + story.getTrip().getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(story, "story.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Story story, final BindingResult binding) {
		ModelAndView result;

		try {
			this.storyService.delete(story);
			result = new ModelAndView("redirect:list.do?tripId=" + story.getTrip().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(story, "story.commit.error");
		}
		return result;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Story story) {
		ModelAndView result;

		result = this.createEditModelAndView(story, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Story story, final String message) {
		ModelAndView result;
		Trip trip;
		Explorer explorer;
		int tripId;

		if (story.getTrip() == null)
			trip = null;
		else
			trip = story.getTrip();

		if (story.getWriter() == null)
			explorer = null;
		else
			explorer = story.getWriter();

		tripId = story.getTrip().getId();

		result = new ModelAndView("story/edit");
		result.addObject("story", story);
		result.addObject("writer", explorer);
		result.addObject("trip", trip);
		result.addObject("tripId", tripId);
		result.addObject("message", message);

		return result;
	}

}
