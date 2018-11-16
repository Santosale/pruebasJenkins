
package controllers.explorer;

import java.util.Calendar;
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

import security.LoginService;
import services.ApplicationService;
import services.CreditCardService;
import services.ExplorerService;
import services.TripService;
import controllers.AbstractController;
import domain.Application;
import domain.CreditCard;
import domain.Explorer;
import domain.Trip;

@Controller
@RequestMapping(value = "/application/explorer")
public class ApplicationExplorerController extends AbstractController {

	// Services
	@Autowired
	private TripService			tripService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private CreditCardService	creditCardService;


	// Constructor
	public ApplicationExplorerController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Application> applications;
		Explorer explorer;
		Date dateMonth;
		Calendar calendar;
		Date currentMoment;

		currentMoment = new Date();

		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		dateMonth = calendar.getTime();

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		applications = this.applicationService.findByExplorerId(explorer.getId());
		Assert.notNull(applications);

		result = new ModelAndView("application/list");

		result.addObject("applications", applications);
		result.addObject("actorType", "explorer");
		result.addObject("dateMonth", dateMonth);
		result.addObject("currentMoment", currentMoment);
		result.addObject("sortableNumber", 5);

		return result;
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int applicationId) {
		ModelAndView result;
		Application application;
		Calendar calendar;
		Date dateMonth;

		calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		dateMonth = calendar.getTime();

		application = this.applicationService.findOne(applicationId);
		Assert.notNull(application);

		result = new ModelAndView("application/display");
		result.addObject("application", application);
		result.addObject("actorType", "explorer");
		result.addObject("dateMonth", dateMonth);

		return result;
	}

	// Create
	@RequestMapping(value = "/apply", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		Trip trip;
		Application application;
		Explorer explorer;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);

		application = this.applicationService.create(explorer, trip);

		result = this.createEditModelAndView(application);

		return result;
	}

	// Add credit card to an application
	@RequestMapping(value = "/addcreditcard", method = RequestMethod.GET)
	public ModelAndView addCreditCard(@RequestParam final int applicationId) {
		ModelAndView result;
		Application application;

		application = this.applicationService.findOne(applicationId);
		Assert.notNull(application);

		result = this.addCreditCardModelAndView(application);

		return result;
	}

	@RequestMapping(value = "/addcreditcard", method = RequestMethod.POST, params = "save")
	public ModelAndView addCreditCard(@Valid final Application application, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.addCreditCardModelAndView(application);
		else
			try {
				this.applicationService.addCreditCard(application);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.addCreditCardModelAndView(application, "application.commit.error");
			}

		return result;
	}

	// Cancel application
	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView cancel(@RequestParam final int applicationId) {
		ModelAndView result;

		this.applicationService.cancelApplication(applicationId);

		result = new ModelAndView("redirect:list.do");

		return result;
	}

	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Application application, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			System.out.println(binding.toString());
			result = this.createEditModelAndView(application);
		} else
			try {
				this.applicationService.save(application);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(application, "application.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Application application) {
		ModelAndView result;

		result = this.createEditModelAndView(application, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Application application, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("application/create");

		result.addObject("application", application);
		result.addObject("actorType", "explorer");

		return result;
	}

	protected ModelAndView addCreditCardModelAndView(final Application application) {
		ModelAndView result;

		result = this.addCreditCardModelAndView(application, null);

		return result;
	}

	protected ModelAndView addCreditCardModelAndView(final Application application, final String messageCode) {
		ModelAndView result;
		Collection<CreditCard> creditCards;

		result = new ModelAndView("application/addcreditcard");

		creditCards = this.creditCardService.findByActorId(application.getApplicant().getId());

		result.addObject("message", messageCode);
		result.addObject("creditcards", creditCards);
		result.addObject("application", application);

		return result;
	}

}
