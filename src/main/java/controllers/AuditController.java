
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.AuditService;
import services.AuditorService;
import domain.Audit;

@Controller
@RequestMapping("/auditRecord")
public class AuditController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private AuditService	auditService;

	@Autowired
	private AuditorService	auditorService;


	// Constructors -----------------------------------------------------------

	public AuditController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<Audit> audits;
		UserAccount auditor = null;
		int userAccountId;

		if (LoginService.isAuthenticated() == true) {
			userAccountId = LoginService.getPrincipal().getId();
			if (this.auditorService.findByUserAccountId(userAccountId) != null)
				auditor = this.auditorService.findByUserAccountId(userAccountId).getUserAccount();
		}

		audits = this.auditService.findByTripId(tripId);

		result = new ModelAndView("auditRecord/list");
		result.addObject("audits", audits);
		result.addObject("requestURI", "auditRecord/list.do");
		result.addObject("actor", auditor);

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

}
