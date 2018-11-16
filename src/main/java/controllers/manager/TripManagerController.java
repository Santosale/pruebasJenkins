
package controllers.manager;

import java.util.ArrayList;
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

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ApplicationService;
import services.CategoryService;
import services.ExplorerService;
import services.LegalTextService;
import services.ManagerService;
import services.RangerService;
import services.StageService;
import services.TripService;
import controllers.AbstractController;
import domain.Application;
import domain.Category;
import domain.LegalText;
import domain.Manager;
import domain.Ranger;
import domain.Stage;
import domain.Trip;

@Controller
@RequestMapping("/trip/manager")
public class TripManagerController extends AbstractController {

	// Services ------------------------------

	@Autowired
	private TripService			tripService;

	@Autowired
	private StageService		stageService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private RangerService		rangerService;

	@Autowired
	private LegalTextService	legalTextService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private ManagerService		managerService;


	// Constructors ---------------------------
	public TripManagerController() {
		super();
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int tripId) {
		ModelAndView result;
		Trip trip;

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);
		result = this.createEditModelAndViewDisplay(trip);

		return result;
	}

	// Listing --------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) final String keyword) {
		ModelAndView result;
		Collection<Trip> tripsManager;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();

		tripsManager = this.tripService.findByManagerUserAccountId(userAccount.getId());

		result = new ModelAndView("trip/list");

		result.addObject("trips", tripsManager);
		result.addObject("currentMoment", new Date());

		return result;
	}

	// Listing --------------------------------
	@RequestMapping(value = "/publish", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<Trip> tripsManager;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();

		tripsManager = this.tripService.findByManagerUserAccountId(userAccount.getId());

		this.tripService.publishTrip(tripId);

		result = new ModelAndView("trip/list");

		result.addObject("trips", tripsManager);
		result.addObject("currentMoment", new Date());

		return result;
	}

	// Creation ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Trip trip;
		UserAccount user;
		Manager manager;

		user = LoginService.getPrincipal();

		manager = this.managerService.findByUserAccountId(user.getId());

		trip = this.tripService.create(manager);
		result = this.createEditModelAndView(trip);

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int tripId) {
		ModelAndView result;
		Trip trip;

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);
		result = this.createEditModelAndView(trip);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Trip trip, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(trip);
		else
			try {
				this.tripService.save(trip);
				result = new ModelAndView("redirect:list.do");

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(trip, "trip.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Trip trip, final BindingResult binding) {
		ModelAndView result;

		try {
			this.tripService.delete(trip);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(trip, "trip.commit.error");
		}

		return result;
	}

	// Cancel ------------------------------------

	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView editCancel(@RequestParam final int tripId) {
		ModelAndView result;
		Trip trip;

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);
		result = this.createEditModelAndViewCancel(trip);

		return result;
	}

	@RequestMapping(value = "/cancel", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCancel(@Valid final Trip trip, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndViewCancel(trip);
		else
			try {
				this.tripService.cancellTrip(trip.getId(), trip.getCancellationReason());
				result = new ModelAndView("redirect:list.do");

			} catch (final Throwable oops) {
				result = this.createEditModelAndViewCancel(trip, "trip.commit.error");
			}

		return result;
	}

	// Ancillary methods -----------------------

	protected ModelAndView createEditModelAndViewDisplay(final Trip trip) {
		ModelAndView result;
		Collection<Stage> stages;
		Boolean isApplicant;
		Boolean applicationAccepted;
		Application application;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("EXPLORER");

		// Evitamos que devuelvan un valor nulo
		stages = this.stageService.findByTripIdOrderByNumStage(trip.getId());
		if (stages == null)
			stages = new ArrayList<Stage>();

		// Miramos si es un explorer el que está autenticado, si tiene
		// applicación para el viaje y en ese caso si esta aceptada
		isApplicant = false;
		applicationAccepted = false;
		// Con el try catch podemos ver si hay algun usuario autenticado
		try {
			if (LoginService.getPrincipal().getAuthorities().contains(authority)) {
				application = this.applicationService.findByTripIdAndExplorerId(trip.getId(), this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId());

				if (application != null) {
					isApplicant = true;
					applicationAccepted = application.getStatus().equals("ACCEPTED");
				}
			}
		} catch (final Exception e) {

		}

		result = new ModelAndView("trip/display");

		result.addObject("trip", trip);

		result.addObject("stages", stages);

		result.addObject("isApplicant", isApplicant);

		result.addObject("currentDate", new Date());

		result.addObject("applicationAccepted", applicationAccepted);

		return result;

	}

	protected ModelAndView createEditModelAndView(final Trip trip) {
		ModelAndView result;

		result = this.createEditModelAndView(trip, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Trip trip, final String message) {
		ModelAndView result;
		Collection<Ranger> rangers;
		Collection<LegalText> legalTexts;
		Collection<Category> categories;

		rangers = this.rangerService.findAll();

		legalTexts = this.legalTextService.findFinalMode();

		categories = this.categoryService.findAll();

		result = new ModelAndView("trip/edit");

		result.addObject("trip", trip);

		result.addObject("rangers", rangers);

		result.addObject("categories", categories);

		result.addObject("legalTexts", legalTexts);

		result.addObject("currentDate", new Date());

		result.addObject("message", message);

		return result;

	}

	protected ModelAndView createEditModelAndViewCancel(final Trip trip) {
		ModelAndView result;

		result = this.createEditModelAndViewCancel(trip, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewCancel(final Trip trip, final String message) {
		ModelAndView result;
		String cancellationReason;

		if (trip.getCancellationReason() == null)
			cancellationReason = null;
		else
			cancellationReason = trip.getCancellationReason();

		result = new ModelAndView("trip/cancel");

		result.addObject("trip", trip);

		result.addObject("cancellationReason", cancellationReason);

		result.addObject("currentDate", new Date());

		result.addObject("message", message);

		return result;

	}
}
