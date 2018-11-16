
package controllers;

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

import security.Authority;
import security.LoginService;
import services.ApplicationService;
import services.AuditService;
import services.AuditorService;
import services.ExplorerService;
import services.SponsorshipService;
import services.StageService;
import services.TripService;
import domain.Application;
import domain.Auditor;
import domain.Sponsorship;
import domain.Stage;
import domain.Trip;

@Controller
@RequestMapping("/trip")
public class TripController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private TripService			tripService;

	@Autowired
	private StageService		stageService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private AuditService		auditService;

	@Autowired
	private AuditorService		auditorService;


	//Constructors ---------------------------
	public TripController() {
		super();
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int tripId) {
		ModelAndView result;
		Trip trip;

		trip = this.tripService.findOne(tripId);

		Assert.notNull(trip);
		result = this.createEditModelAndView(trip);

		return result;
	}

	//Listing --------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) final String keyword) {
		ModelAndView result;
		Collection<Trip> trips;

		trips = this.tripService.findByKeyWord(keyword);

		result = new ModelAndView("trip/list");

		result.addObject("trips", trips);
		result.addObject("currentMoment", new Date());
		result.addObject("requestURI", "trip/list.do");

		return result;
	}

	//Ancillary methods -----------------------
	protected ModelAndView createEditModelAndView(final Trip trip) {
		ModelAndView result;
		Collection<Stage> stages;
		Boolean isApplicant;
		Boolean applicationAccepted;
		Application application;
		Authority authority;
		Sponsorship sponsorship;
		Boolean createAudit;
		Auditor auditor;

		authority = new Authority();
		authority.setAuthority("EXPLORER");

		//Evitamos que devuelvan un valor nulo
		stages = this.stageService.findByTripIdOrderByNumStage(trip.getId());
		if (stages == null)
			stages = new ArrayList<Stage>();

		//Miramos si es un explorer el que está autenticado, si tiene applicación para el viaje y en ese caso si esta aceptada
		isApplicant = false;
		applicationAccepted = false;
		//Con el try catch podemos ver si hay algun usuario autenticado

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			application = this.applicationService.findByTripIdAndExplorerId(trip.getId(), this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId());

			if (application != null) {
				isApplicant = true;
				applicationAccepted = application.getStatus().equals("ACCEPTED");
			}
		}

		//Vemos si es un auditor el que está autenticado y si ya tiene un audit
		authority.setAuthority("AUDITOR");
		createAudit = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {

			auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());

			if (this.auditService.findByTripIdAndAuditorId(trip.getId(), auditor.getId()) == null)
				createAudit = true;
		}

		sponsorship = this.sponsorshipService.findRandomSponsorship(trip.getId());

		result = new ModelAndView("trip/display");

		result.addObject("sponsorship", sponsorship);

		result.addObject("trip", trip);

		result.addObject("stages", stages);

		result.addObject("isApplicant", isApplicant);

		result.addObject("currentDate", new Date());

		result.addObject("applicationAccepted", applicationAccepted);

		result.addObject("createAudit", createAudit);

		return result;

	}
}
