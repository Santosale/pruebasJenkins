package controllers.user;

import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import controllers.AbstractController;

import domain.Bargain;
import domain.User;
import domain.Level;
import forms.UserForm;

import security.LoginService;
import services.BargainService;
import services.LevelService;
import services.UserService;

@Controller
@RequestMapping(value="/actor/user")
public class UserController extends AbstractController {

	// Supporting services
	@Autowired
	private UserService userService;
	
	@Autowired
	private LevelService levelService;
	
	@Autowired
	private BargainService bargainService;
	
	// Constructor
	public UserController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;
		
		userPage = this.userService.findOrderByPoints(page, 5);
		Assert.notNull(userPage);
		
		userLevel = new LinkedHashMap<User, Level>();
		for(User u: userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}
		
		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/actor/user/list.do");
		return result;
	}
	
	// Display
	@RequestMapping(value="/display", method=RequestMethod.GET)
	public ModelAndView display(@RequestParam final int userId) {
		ModelAndView result;
		User user;
		Level level;
		
		user = this.userService.findOne(userId);
		Assert.notNull(user);
		
		level = this.levelService.findByPoints(user.getPoints());
		Assert.notNull(level);
		
		result = new ModelAndView("actor/display");
		result.addObject("actor", user);
		result.addObject("model", "user");
		result.addObject("level", level);
		result.addObject("isPublic", true);
		
		return result;
	}
	
	@RequestMapping(value="/wishlist", method=RequestMethod.GET)
	public ModelAndView wishList(@RequestParam final int actorId, @RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		User user;
		Page<Bargain> bargainPage;
		
		user = this.userService.findOne(actorId);
		Assert.notNull(user);
		
		if(LoginService.isAuthenticated()) {
			if(!LoginService.getPrincipal().equals(user.getUserAccount()))
				Assert.isTrue(user.getIsPublicWishList());
		} else
			Assert.isTrue(user.getIsPublicWishList());
		
		bargainPage = this.bargainService.findBargainByActorId(actorId, page, 5);
		
		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("requestURI", "actor/user/wishlist.do");
		result.addObject("actorId", actorId);
		
		return result;
	}
	
	// Change the status of WishList
	@RequestMapping(value="/changewishlist", method=RequestMethod.GET)
	public ModelAndView changeWishList() {
		ModelAndView result;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
				
		this.userService.changeWishList(user);
		
		result = new ModelAndView("redirect:profile.do");
		
		return result;
	}
	
	@RequestMapping(value="/addremovewishlist", method=RequestMethod.GET)
	public ModelAndView addRemoveWishList(@RequestParam final int bargainId) {
		ModelAndView result;
				
		this.userService.addRemoveBargainToWishList(bargainId);
		
		result = new ModelAndView("redirect:/bargain/display.do?bargainId="+bargainId);
		
		return result;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		UserForm userForm;
		
		userForm = new UserForm();
		
		result = new ModelAndView("user/edit");
		result.addObject("requestURI", "actor/user/edit.do");
		result.addObject("userForm", userForm);
		result.addObject("model", "user");
		
		return result;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final UserForm userForm, final BindingResult binding, @RequestParam final String model) {
		ModelAndView result;
		User user;
		boolean next;

		next = true;
		result = null;
		user = null;
		try {
			user = this.userService.reconstruct(userForm, binding);
		} catch (final Throwable e) {

			if (binding.hasErrors())
				result = this.createEditModelAndView(userForm);
			else
				result = this.createEditModelAndView(userForm, "actor.commit.error");

			next = false;
		}

		if (next)
			if (binding.hasErrors())
				result = this.createEditModelAndView(userForm);
			else
				try {
					this.userService.save(user);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(userForm, "actor.commit.error");
				}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final UserForm userForm) {
		ModelAndView result;

		result = this.createEditModelAndView(userForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final UserForm userForm, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("user/edit");

		result.addObject("model", "user");
		result.addObject("userForm", userForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", "actor/user/edit.do");

		return result;
	}
	
}
