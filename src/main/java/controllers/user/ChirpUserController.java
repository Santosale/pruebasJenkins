package controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ChirpService;
import services.UserService;

import controllers.AbstractController;
import domain.Chirp;
import domain.User;

@Controller
@RequestMapping("/chirp/user")
public class ChirpUserController extends AbstractController {

	@Autowired
	private ChirpService chirpService;
	
	@Autowired
	private UserService userService;
	
	// Constructor
	public ChirpUserController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Chirp> pageChirps;
		Integer countFolloweds, countFollowers;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		pageChirps = this.chirpService.findFollowedsChirpByUserId(user.getId(), page, 5);
		Assert.notNull(pageChirps);
		
		countFollowers = this.userService.countFollowersByUserId(user.getId());
		Assert.notNull(countFollowers);
		
		countFolloweds = this.userService.countFollowedsByUserId(user.getId());
		Assert.notNull(countFolloweds);
		
		result = new ModelAndView("chirp/list");
		result.addObject("chirps", pageChirps.getContent());
		result.addObject("pageNumber", pageChirps.getTotalPages());
		result.addObject("page", page);
		result.addObject("countFollowers", countFollowers);
		result.addObject("countFolloweds", countFolloweds);
		result.addObject("userId", user.getId());
		result.addObject("role", "user");
		
		return result;
	}
	
	// Create
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Chirp chirp;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		chirp = this.chirpService.create(user);
		Assert.notNull(chirp);
		
		result = createEditModelAndView(chirp);
		
		return result;
	}
	
	// Save
	@RequestMapping(value="/create", method=RequestMethod.POST, params="save")
	public ModelAndView save(Chirp chirp, final BindingResult binding) {
		ModelAndView result;
		
		chirp = this.chirpService.reconstruct(chirp, binding);
		
		if(binding.hasErrors()) {
			result = createEditModelAndView(chirp);
		} else {
			try {
				this.chirpService.save(chirp);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = createEditModelAndView(chirp, "chirp.commit.error");
			}
		}
		
		return result;
	}
	
	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Chirp chirp) {
		ModelAndView result;

		result = this.createEditModelAndView(chirp, null);

		return result;
	}
	
	protected ModelAndView createEditModelAndView(final Chirp chirp, final String messageCode) {
		ModelAndView result;
		
		result = new ModelAndView("chirp/create");
		
		result.addObject("chirp", chirp);
		result.addObject("message", messageCode);

		return result;
	}
	
}
