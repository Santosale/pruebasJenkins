
package controllers.explorer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ApplicationService;
import services.ExplorerService;
import services.SurvivalClassService;
import controllers.AbstractController;
import domain.Application;
import domain.Explorer;
import domain.SurvivalClass;

@Controller
@RequestMapping("/survivalClass/explorer")
public class SurvivalClassExplorerController extends AbstractController {

	// Services

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private ExplorerService			explorerService;


	// Constructor
	public SurvivalClassExplorerController() {
		super();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView listRegister(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<SurvivalClass> survivalClasses, results;
		Application application;
		UserAccount user;
		Explorer explorer;
		Boolean canRegister, registered;
		String requestUri;

		canRegister = false;
		registered = false;

		requestUri = "survivalClass/explorer/register.do?tripId=" + tripId;

		user = LoginService.getPrincipal();

		explorer = this.explorerService.findByUserAccountId(user.getId());

		survivalClasses = new ArrayList<SurvivalClass>();
		results = new ArrayList<SurvivalClass>();

		application = this.applicationService.findByTripIdAndExplorerId(tripId, explorer.getId());

		if (application != null && application.getStatus().equals("ACCEPTED")) {
			survivalClasses = this.survivalClassService.findByTripId(tripId);
			survivalClasses.removeAll(application.getSurvivalClasses());
			canRegister = true;
		}

		for (final SurvivalClass s : survivalClasses)
			if (s.getMoment().compareTo(new Date()) > 0)
				results.add(s);

		result = new ModelAndView("survivalClass/register");
		result.addObject("survivalClasses", results);
		result.addObject("tripId", tripId);
		result.addObject("canRegister", canRegister);
		result.addObject("registered", registered);
		result.addObject("requestURI", requestUri);

		return result;
	}

	@RequestMapping(value = "/unregister", method = RequestMethod.GET)
	public ModelAndView listUnregister(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<SurvivalClass> survivalClasses, results;
		Application application;
		UserAccount user;
		Explorer explorer;
		Boolean canRegister, registered;
		String requestUri;

		canRegister = false;
		registered = true;

		requestUri = "survivalClass/explorer/unregister.do?tripId=" + tripId;

		user = LoginService.getPrincipal();

		explorer = this.explorerService.findByUserAccountId(user.getId());

		survivalClasses = new ArrayList<SurvivalClass>();
		results = new ArrayList<SurvivalClass>();

		application = this.applicationService.findByTripIdAndExplorerId(tripId, explorer.getId());

		if (application != null && application.getStatus().equals("ACCEPTED")) {
			survivalClasses = application.getSurvivalClasses();
			canRegister = true;
		}

		for (final SurvivalClass s : survivalClasses)
			if (s.getMoment().compareTo(new Date()) > 0)
				results.add(s);

		result = new ModelAndView("survivalClass/register");
		result.addObject("survivalClasses", results);
		result.addObject("tripId", tripId);
		result.addObject("canRegister", canRegister);
		result.addObject("registered", registered);
		result.addObject("requestURI", requestUri);

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/registerEdit", method = RequestMethod.GET)
	public ModelAndView listRegSur(@RequestParam final int survivalClassId) {
		ModelAndView result;
		SurvivalClass survivalClass;
		Application application;
		UserAccount user;
		Explorer explorer;

		survivalClass = this.survivalClassService.findOne(survivalClassId);
		Assert.notNull(survivalClass);

		user = LoginService.getPrincipal();
		explorer = this.explorerService.findByUserAccountId(user.getId());
		Assert.notNull(explorer);
		application = this.applicationService.findByTripIdAndExplorerId(survivalClass.getTrip().getId(), explorer.getId());
		Assert.notNull(application);
		
		this.applicationService.registerToASurvivalClass(application, survivalClass);
		
		result = new ModelAndView("redirect:register.do?tripId=" + application.getTrip().getId());
		
		return result;

	}

	@RequestMapping(value = "/unregisterEdit", method = RequestMethod.GET)
	public ModelAndView listUnRegSur(@RequestParam final int survivalClassId) {
		ModelAndView result;
		SurvivalClass survivalClass;
		Application application;
		UserAccount user;
		Explorer explorer;

		survivalClass = this.survivalClassService.findOne(survivalClassId);
		Assert.notNull(survivalClass);

		user = LoginService.getPrincipal();
		explorer = this.explorerService.findByUserAccountId(user.getId());
		Assert.notNull(explorer);
		application = this.applicationService.findByTripIdAndExplorerId(survivalClass.getTrip().getId(), explorer.getId());
		Assert.notNull(application);
		
		this.applicationService.unRegisterToASurvivalClass(application, survivalClass);
		result = new ModelAndView("redirect:unregister.do?tripId=" + application.getTrip().getId());
		
		return result;

	}

}
