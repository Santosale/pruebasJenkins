
package controllers.manager;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.StageService;
import services.TripService;
import controllers.AbstractController;
import domain.Stage;
import domain.Trip;

@Controller
@RequestMapping("/stage/manager")
public class StageManagerController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private StageService	stageService;

	@Autowired
	private TripService		tripService;


	// Constructors -----------------------------------------------------------

	public StageManagerController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<Stage> stages;
		Date currentMoment;

		currentMoment = new Date();

		stages = this.stageService.findByTripIdOrderByNumStage(tripId);

		result = new ModelAndView("stage/list");
		result.addObject("stages", stages);
		result.addObject("currentMoment", currentMoment);
		result.addObject("requestURI", "stage/list.do");

		return result;
	}

	// Display ----------------------------------------------------------------

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int stageId) {
		ModelAndView result;
		Stage stage;

		stage = this.stageService.findOne(stageId);

		result = new ModelAndView("stage/display");
		result.addObject("stage", stage);
		result.addObject("requestURI", "stage/display.do");

		return result;
	}

	// Creation ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		Stage stage;
		Trip trip;

		trip = this.tripService.findOne(tripId);

		stage = this.stageService.create(trip);
		result = this.createEditModelAndView(stage);

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int stageId) {
		ModelAndView result;
		Stage stage;

		stage = this.stageService.findOne(stageId);
		Assert.notNull(stage);
		result = this.createEditModelAndView(stage);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Stage stage, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(stage);
		else
			try {
				this.stageService.save(stage);
				result = new ModelAndView("redirect:/trip/display.do?tripId=" + stage.getTrip().getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(stage, "stage.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Stage stage, final BindingResult binding) {
		ModelAndView result;

		try {
			this.stageService.delete(stage);
			result = new ModelAndView("redirect:/trip/display.do?tripId=" + stage.getTrip().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(stage, "stage.commit.error");
		}

		return result;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Stage stage) {
		ModelAndView result;

		result = this.createEditModelAndView(stage, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Stage stage, final String message) {
		ModelAndView result;
		Trip trip;
		int numStage;

		if (stage.getTrip() == null)
			trip = null;
		else
			trip = stage.getTrip();

		numStage = stage.getNumStage();

		result = new ModelAndView("stage/edit");
		result.addObject("stage", stage);
		result.addObject("numStage", numStage);
		result.addObject("trip", trip);
		result.addObject("tripId", stage.getTrip().getId());
		result.addObject("message", message);

		return result;
	}

}
