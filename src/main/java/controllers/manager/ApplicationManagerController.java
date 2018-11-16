
package controllers.manager;

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

import services.ApplicationService;
import controllers.AbstractController;
import domain.Application;

@Controller
@RequestMapping("/application/manager")
public class ApplicationManagerController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private ApplicationService	applicationService;


	// Constructors -----------------------------------------------------------

	public ApplicationManagerController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(final int tripId) {
		ModelAndView result;
		Collection<Application> applications;
		//UserAccount user;
		//Manager manager;
		String rol;

		Assert.isTrue(tripId != 0);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		final Date dateOneMonthLater = calendar.getTime();

		//user = LoginService.getPrincipal();
		//manager = this.managerService.findByUserAccountId(user.getId());

		rol = "manager";

		applications = this.applicationService.findByTripId(tripId);

		result = new ModelAndView("application/list");
		result.addObject("applications", applications);
		result.addObject("requestURI", "application/list.do");
		result.addObject("dateMonth", dateOneMonthLater);
		result.addObject("actorType", rol);
		result.addObject("sortableNumber", 4);

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

		result = new ModelAndView("application/display");
		result.addObject("application", application);
		result.addObject("actortype", "manager");
		result.addObject("dateMonth", dateMonth);

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/editDue", method = RequestMethod.GET)
	public ModelAndView editDue(@RequestParam final int applicationId) {
		ModelAndView result;
		Application application;

		application = this.applicationService.findOne(applicationId);
		Assert.notNull(application);
		application.setStatus("DUE");
		this.applicationService.save(application);
		result = new ModelAndView("redirect:list.do?tripId=" + application.getTrip().getId());

		return result;
	}

	@RequestMapping(value = "/editRejected", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int applicationId) {
		ModelAndView result;
		Application application;

		application = this.applicationService.findOne(applicationId);
		Assert.notNull(application);
		result = this.createEditModelAndView(application);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Application application, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(application);
		else
			try {
				application.setStatus("REJECTED");
				this.applicationService.save(application);
				result = new ModelAndView("redirect:list.do?tripId=" + application.getTrip().getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(application, "application.commit.error");
			}
		return result;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Application application) {
		ModelAndView result;

		result = this.createEditModelAndView(application, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Application application, final String message) {
		ModelAndView result;

		result = new ModelAndView("application/edit");
		result.addObject("application", application);
		result.addObject("message", message);

		return result;
	}

}
