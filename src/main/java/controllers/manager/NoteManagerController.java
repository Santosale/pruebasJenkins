
package controllers.manager;

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
import services.ActorService;
import services.ManagerService;
import services.NoteService;
import services.TripService;
import controllers.AbstractController;
import domain.Note;

@Controller
@RequestMapping("/note/manager")
public class NoteManagerController extends AbstractController {

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
	public NoteManagerController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int noteId) {
		ModelAndView result;
		Note note;

		note = this.noteService.findOne(noteId);
		Assert.notNull(note);

		result = this.createEditModelAndView(note);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Note note, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditModelAndView(note);
		else
			try {
				this.noteService.save(note);
				result = new ModelAndView("redirect:/note/managerAuditor/list.do?tripId=" + note.getTrip().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(note, "note.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public ModelAndView welcome() {
		ModelAndView result;

		result = new ModelAndView("welcome/index");

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Note note) {
		ModelAndView result;

		result = this.createEditModelAndView(note, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Note note, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		UserAccount userAccount;

		canEdit = false;
		userAccount = LoginService.getPrincipal();
		if (note.getTrip().getManager().getUserAccount().getId() == userAccount.getId() && note.getManagerReply() == null)
			canEdit = true;

		result = new ModelAndView("note/edit");

		result.addObject("note", note);
		result.addObject("canEdit", canEdit);
		result.addObject("message", messageCode);

		return result;
	}
}
