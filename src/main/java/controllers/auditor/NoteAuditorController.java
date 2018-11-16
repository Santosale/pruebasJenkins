
package controllers.auditor;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.AuditorService;
import services.NoteService;
import services.TripService;
import controllers.AbstractController;
import domain.Auditor;
import domain.Note;
import domain.Trip;

@Controller
@RequestMapping("/note/auditor")
public class NoteAuditorController extends AbstractController {

	// Services ---------------------------------------------------------------
	@Autowired
	private NoteService		noteService;

	@Autowired
	private TripService		tripService;

	@Autowired
	private AuditorService	auditorService;


	// Constructors -----------------------------------------------------------
	public NoteAuditorController() {
		super();
	}

	// Listing ----------------------------------------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Note> notes;
		UserAccount user;
		Auditor auditor;

		user = LoginService.getPrincipal();

		auditor = this.auditorService.findByUserAccountId(user.getId());

		notes = this.noteService.findByAuditorId(auditor.getId());

		result = new ModelAndView("note/list");
		result.addObject("notes", notes);
		result.addObject("requestURI", "note/auditor/list.do");

		return result;
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int noteId) {
		ModelAndView result;
		Note note;
		note = this.noteService.findOne(noteId);
		result = new ModelAndView("note/display");
		result.addObject("note", note);
		result.addObject("requestURI", "note/auditor/display.do");
		return result;
	}

	// Creation ---------------------------------------------------------------
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		Note note = null;
		Auditor auditor;
		Trip trip;

		auditor = this.auditorService.findByUserAccountId(LoginService.getPrincipal().getId());
		trip = this.tripService.findOne(tripId);
		note = this.noteService.create(auditor, trip);
		result = this.createEditModelAndView(note);
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Note note, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors()) {
			result = this.createEditModelAndView(note);
			System.out.println(binding);
		} else
			try {
				this.noteService.save(note);
				result = new ModelAndView("redirect:list.do?tripId=" + note.getTrip().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(note, "note.commit.error");
			}
		return result;
	}

	// Ancillary methods ------------------------------------------------------
	protected ModelAndView createEditModelAndView(final Note note) {
		ModelAndView result;
		result = this.createEditModelAndView(note, null);
		return result;
	}

	protected ModelAndView createEditModelAndView(final Note note, final String message) {
		ModelAndView result;
		Date date;

		date = new Date();

		result = new ModelAndView("note/create");
		result.addObject("note", note);
		result.addObject("moment", date);

		result.addObject("message", message);
		return result;
	}
}
