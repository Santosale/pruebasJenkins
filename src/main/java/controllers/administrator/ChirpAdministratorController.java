package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ChirpService;
import controllers.AbstractController;
import domain.Chirp;

@Controller
@RequestMapping("/chirp/administrator")
public class ChirpAdministratorController extends AbstractController {

	@Autowired
	private ChirpService chirpService;

	// Constructor
	public ChirpAdministratorController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Chirp> pageChirps;
		
		pageChirps = this.chirpService.findAllPaginated(page, 5);
		Assert.notNull(pageChirps);
		
		result = new ModelAndView("chirp/list");
		result.addObject("chirps", pageChirps.getContent());
		result.addObject("pageNumber", pageChirps.getTotalPages());
		result.addObject("page", page);
		result.addObject("role", "administrator");
		
		return result;
	}
	
	// Delete
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public ModelAndView create(@RequestParam final int chirpId) {
		ModelAndView result;
		Chirp chirp;
		
		chirp = this.chirpService.findOneToEdit(chirpId);
		Assert.notNull(chirp);
		
		this.chirpService.delete(chirp);
		
		result = new ModelAndView("redirect:list.do");
		
		return result;
	}
	
}
