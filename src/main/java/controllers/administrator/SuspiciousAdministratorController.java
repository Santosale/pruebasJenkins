
package controllers.administrator;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import controllers.AbstractController;
import domain.Actor;

@Controller
@RequestMapping("/suspicious/administrator")
public class SuspiciousAdministratorController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private ActorService	actorService;


	//Constructors ---------------------------
	public SuspiciousAdministratorController() {
		super();
	}

	// searchSuspicious ----------------------------------------------------------------
	@RequestMapping(value = "/searchSuspicious", method = RequestMethod.GET)
	public ModelAndView findSuspicious() {
		ModelAndView result;

		this.actorService.searchAllSuspicious();

		result = new ModelAndView("redirect:/");

		return result;
	}

	//Listing --------------------------------
	@RequestMapping(value = "/listSuspicious", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Actor> actors;

		actors = this.actorService.findAllSuspicious();

		result = new ModelAndView("actor/listSuspicious");

		result.addObject("actors", actors);

		return result;
	}

	//BanUnban --------------------------------
	@RequestMapping(value = "/banUnban", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final boolean ban, @RequestParam final int actorId) {
		ModelAndView result;
		Actor actor;

		actor = this.actorService.findOne(actorId);
		Assert.notNull(actor);
		this.actorService.banUnBanActors(actor, ban);

		result = new ModelAndView("redirect:listSuspicious.do");

		return result;
	}

}
