
package controllers.auditor;

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
import services.AuditService;
import services.AuditorService;
import services.TripService;
import controllers.AbstractController;
import domain.Audit;
import domain.Auditor;
import domain.Trip;

@Controller
@RequestMapping("/auditRecord/auditor")
public class AuditAuditorController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private AuditService	auditService;

	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private TripService		tripService;


	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Audit> audits;
		UserAccount actor;
		Auditor auditor;

		actor = LoginService.getPrincipal();

		auditor = this.auditorService.findByUserAccountId(actor.getId());

		audits = this.auditService.findByAuditorId(auditor.getId());

		result = new ModelAndView("auditRecord/list");
		result.addObject("audits", audits);
		result.addObject("requestURI", "auditRecord/list.do");
		result.addObject("actor", actor);

		return result;
	}

	// Display ----------------------------------------------------------------

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int auditId) {
		ModelAndView result;
		Audit audit;

		audit = this.auditService.findOne(auditId);

		result = new ModelAndView("auditRecord/display");
		result.addObject("audit", audit);
		result.addObject("requestURI", "auditRecord/display.do");

		return result;
	}

	// Constructors -----------------------------------------------------------

	public AuditAuditorController() {
		super();
	}

	// Creation ---------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		Audit audit = null;
		Auditor auditor;
		Trip trip;

		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		trip = this.tripService.findOne(tripId);

		audit = this.auditService.create(auditor, trip);
		result = this.createEditModelAndView(audit);

		return result;
	}

	// Edition ----------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int auditId) {
		ModelAndView result;
		Audit audit;

		audit = this.auditService.findOne(auditId);
		Assert.notNull(audit);
		Assert.isTrue(audit.isDraft());
		result = this.createEditModelAndView(audit);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Audit audit, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(audit);
		else
			try {
				this.auditService.save(audit);
				result = new ModelAndView("redirect:list.do");

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(audit, "audit.commit.error");
			}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Audit audit, final BindingResult binding) {
		ModelAndView result;

		try {
			this.auditService.delete(audit);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(audit, "audit.commit.error");
		}

		return result;
	}

	// Ancillary methods ------------------------------------------------------

	protected ModelAndView createEditModelAndView(final Audit audit) {
		ModelAndView result;

		result = this.createEditModelAndView(audit, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Audit audit, final String message) {
		ModelAndView result;
		Auditor auditor;
		Trip trip;
		UserAccount actor;

		actor = LoginService.getPrincipal();

		if (audit.getAuditor() == null)
			auditor = null;
		else
			auditor = audit.getAuditor();

		if (audit.getTrip() == null)
			trip = null;
		else
			trip = audit.getTrip();

		result = new ModelAndView("auditRecord/edit");
		result.addObject("audit", audit);
		result.addObject("auditor", auditor);
		result.addObject("trip", trip);
		result.addObject("actor", actor);

		result.addObject("message", message);

		return result;
	}

}
