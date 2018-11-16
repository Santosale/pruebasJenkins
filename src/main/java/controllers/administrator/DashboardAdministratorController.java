
package controllers.administrator;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ApplicationService;
import services.AuditService;
import services.ManagerService;
import services.NoteService;
import services.RangerService;
import services.TripService;
import controllers.AbstractController;
import domain.LegalText;
import domain.Trip;

@Controller
@RequestMapping("/dashboard/administrator")
public class DashboardAdministratorController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private TripService			tripService;

	@Autowired
	private AuditService		auditService;

	@Autowired
	private NoteService			noteService;

	@Autowired
	private RangerService		rangerService;

	@Autowired
	private ManagerService		managerService;


	//Constructors ---------------------------
	public DashboardAdministratorController() {
		super();
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Double[] applicationsPerTrips;
		Double[] tripsPerManager;
		Double[] pricePerTrips;
		Double[] tripsPerRanger;
		Double ratioPending;
		Double ratioDue;
		Double ratioAccepted;
		Double ratioCancelled;
		Double ratioTripsCancelled;
		Double[] notesPerTrip;
		Double[] auditsPerTrip;
		Double ratioTripAudit;
		Double ratioRangerCurricula;
		Double ratioRangerCurriculaEndorser;
		Double ratioManagerSuspicious;
		Double ratioRangerSuspicious;

		applicationsPerTrips = this.applicationService.avgMinMaxStandardDNumberApplications();
		tripsPerManager = this.tripService.avgMinMaxStandardDTripsPerManager();
		pricePerTrips = this.tripService.avgMinMaxStandardDPriceOfTrips();
		tripsPerRanger = this.tripService.avgMinMaxStandardDTripsPerRanger();
		ratioPending = this.applicationService.ratioApplicantionsPending();
		ratioDue = this.applicationService.ratioApplicantionsDue();
		ratioAccepted = this.applicationService.ratioApplicantionsAccepted();
		ratioCancelled = this.applicationService.ratioApplicantionsCancelled();
		ratioTripsCancelled = this.tripService.ratioTripCancelledVsTotal();
		notesPerTrip = this.noteService.minMaxAvgStandardDerivationNotePerTrip();
		auditsPerTrip = this.auditService.minMaxAvgStandardDAuditsPerTrip();
		ratioTripAudit = this.tripService.ratioTripOneAuditRecordVsTotal();
		ratioRangerCurricula = this.rangerService.ratioRangersRegisteredCurriculum();
		ratioRangerCurriculaEndorser = this.rangerService.ratioEndorsedCurriculum();
		ratioManagerSuspicious = this.managerService.ratioSuspicious();
		ratioRangerSuspicious = this.rangerService.ratioSuspicious();

		result = new ModelAndView("dashboard/display");

		result.addObject("applicationsPerTrips", applicationsPerTrips);
		result.addObject("tripsPerManager", tripsPerManager);
		result.addObject("pricePerTrips", pricePerTrips);
		result.addObject("tripsPerRanger", tripsPerRanger);
		result.addObject("ratioPending", ratioPending);
		result.addObject("ratioDue", ratioDue);
		result.addObject("ratioAccepted", ratioAccepted);
		result.addObject("ratioCancelled", ratioCancelled);
		result.addObject("ratioTripsCancelled", ratioTripsCancelled);
		result.addObject("notesPerTrip", notesPerTrip);
		result.addObject("auditsPerTrip", auditsPerTrip);
		result.addObject("ratioTripAudit", ratioTripAudit);
		result.addObject("ratioRangerCurricula", ratioRangerCurricula);
		result.addObject("ratioRangerCurriculaEndorser", ratioRangerCurriculaEndorser);
		result.addObject("ratioManagerSuspicious", ratioManagerSuspicious);
		result.addObject("ratioRangerSuspicious", ratioRangerSuspicious);

		return result;
	}

	//Listing --------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Trip> trips;

		trips = this.tripService.findTenPerCentMoreApplicationThanAverage();

		result = new ModelAndView("trip/list");

		result.addObject("trips", trips);
		result.addObject("currentMoment", new Date());
		result.addObject("requestURI", "dashboard/administrator/list.do");

		return result;
	}

	@RequestMapping(value = "/table", method = RequestMethod.GET)
	public ModelAndView table() {
		ModelAndView result;
		Map<LegalText, Long> table;

		table = this.tripService.countLegalTextReferences();

		result = new ModelAndView("legalText/list");

		result.addObject("table", table);
		result.addObject("legalTexts", table.keySet());
		result.addObject("requestURI", "dashboard/administrator/table.do");

		return result;
	}
}
