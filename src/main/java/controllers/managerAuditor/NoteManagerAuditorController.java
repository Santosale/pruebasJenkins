
package controllers.managerAuditor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.ManagerService;
import services.NoteService;
import services.TripService;
import controllers.AbstractController;
import domain.Actor;
import domain.Note;
import domain.Trip;

@Controller
@RequestMapping("/note/managerAuditor")
public class NoteManagerAuditorController extends AbstractController {

	// Services
	@Autowired
	ActorService	actorService;

	@Autowired
	ManagerService	managerService;

	@Autowired
	NoteService		noteService;

	@Autowired
	TripService		tripService;


	// Constructor
	public NoteManagerAuditorController() {
		super();
	}
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int noteId) {
		ModelAndView result;
		Note note;

		note = this.noteService.findOne(noteId);
		Assert.notNull(note);
		result = new ModelAndView("note/display");

		result.addObject("note", note);

		return result;

	}
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Actor actor;
		UserAccount userAccount;
		Collection<Note> notes;
		Trip trip;
		boolean canEditNote;

		canEditNote = false;
		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);

		userAccount = LoginService.getPrincipal();

		actor = this.actorService.findByUserAccountId(userAccount.getId());
		if (trip.getManager().getUserAccount().getId() == actor.getUserAccount().getId())
			canEditNote = true;

		notes = this.noteService.findByTripId(tripId);

		result = new ModelAndView("note/list");
		result.addObject("notes", notes);
		result.addObject("canEditNote", canEditNote);
		result.addObject("tripId", tripId);

		return result;
	}
}
