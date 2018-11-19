
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

import security.Authority;
import security.LoginService;
import services.ArticleService;
import services.ChirpService;
import services.CustomerService;
import services.UserService;
import controllers.AbstractController;
import domain.Article;
import domain.Chirp;
import domain.Customer;
import domain.User;
import forms.UserForm;

@Controller
@RequestMapping(value = "/actor/user")
public class UserController extends AbstractController {

	// Services
	@Autowired
	private UserService	userService;

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ChirpService chirpService;
	
	@Autowired
	private CustomerService customerService;
	
	// Constructor
	public UserController() {
		super();
	}

	// Follow
	@RequestMapping(value="/follow", method = RequestMethod.GET) 
	public ModelAndView follow(@RequestParam int userId) {
		ModelAndView result;		
		
		this.userService.addFollower(userId);
		
		result = new ModelAndView("redirect:display.do?userId="+userId);
		
		return result;
	}
	
	// Unfollow
	@RequestMapping(value="/unfollow", method = RequestMethod.GET) 
	public ModelAndView unfollow(@RequestParam int userId) {
		ModelAndView result;		
		
		this.userService.removeFollower(userId);
		
		result = new ModelAndView("redirect:display.do?userId="+userId);
		
		return result;
	}
	
	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		UserForm userForm;

		userForm = new UserForm();

		result = this.createEditModelAndView(userForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final UserForm userForm, final BindingResult binding) {
		ModelAndView result;
		User user;
		boolean next;

		next = true;
		result = null;
		user = null;
		if(userForm.getPhoneNumber() != null && userForm.getPhoneNumber().equals("")) userForm.setPhoneNumber(null);
		try {
			user = this.userService.reconstruct(userForm, binding);
		} catch (final Throwable e) {
			
			if (binding.hasErrors()) {
				result = this.createEditModelAndView(userForm);
			} else
				result = this.createEditModelAndView(userForm, "actor.commit.error");

			next = false;
		}

		if (next)
			if (binding.hasErrors()) {
				result = this.createEditModelAndView(userForm);
			}else
				try {
					this.userService.save(user);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(userForm, "actor.commit.error");
				}

		return result;
	}
	
	// List
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<User> pageUser;
		
		pageUser = this.userService.findAllPaginated(page, 5);
		Assert.notNull(pageUser);
		
		result = new ModelAndView("user/list");
		result.addObject("users", pageUser.getContent());
		result.addObject("pageNumber", pageUser.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "actor/user/list.do");
		
		return result;
	}
	
	// Display
	@RequestMapping(value="/display", method=RequestMethod.GET)
	public ModelAndView display(@RequestParam(required=false) Integer userId, @RequestParam(defaultValue="1", required=false) final int page) {
		ModelAndView result;
		User user, userAuthenticated;
		Page<Article> articles;
		Page<Chirp> chirpsPage;
		boolean isFollowing;
		boolean isSamePerson;
		Authority authority;
		Authority authority2;
		Authority authority3;
		Customer customer;
		
		authority = new Authority();
		authority.setAuthority("USER");
		
		if(userId == null) {
			if(LoginService.isAuthenticated()) {
				user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
				Assert.notNull(user);
				userId = user.getId();
			} else {
				user = null;
				Assert.notNull(userId);
			}
		} else {
			user = this.userService.findOneToDisplay(userId);
			Assert.notNull(user);
		}
		
		authority2 = new Authority();
		authority3 = new Authority();
		authority2.setAuthority("CUSTOMER");
		authority3.setAuthority("ADMIN");

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			customer = this.customerService.findByUserAccountId(LoginService.getPrincipal().getId());
			articles = this.articleService.findAllUserPaginatedByCustomer(userId, customer.getId(), 0, 5);
		} else if (user != null && LoginService.isAuthenticated() && user.getUserAccount().equals(LoginService.getPrincipal()))
			articles = this.articleService.findByWritterId(userId, 0, 5);
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority3))
			articles = this.articleService.findAllUserPaginatedByAdmin(userId, 0, 5);
		else
			articles = this.articleService.findAllUserPaginated(userId, 0, 5);

		Assert.notNull(articles);
		
		chirpsPage = this.chirpService.findByUserId(userId, page, 5);
		Assert.notNull(chirpsPage);
		
		result = new ModelAndView("user/display");
		result.addObject("user", user);
		result.addObject("articles", articles.getContent());
		result.addObject("chirps", chirpsPage.getContent());
		result.addObject("pageNumber", chirpsPage.getTotalPages());
		result.addObject("page", page);
		
		if(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			isFollowing = true;
			isSamePerson = false;
			
			userAuthenticated = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(userAuthenticated);
			
			if(!user.equals(userAuthenticated))
				isFollowing = user.getFollowers().contains(userAuthenticated);
			else 
				isSamePerson = true;
			
			result.addObject("isFollowing", isFollowing);	
			result.addObject("isSamePerson", isSamePerson);
		}
				
		return result;
	}
	
	// Followers
	@RequestMapping(value="/followers", method=RequestMethod.GET)
	public ModelAndView followers(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<User> pageFollowers;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		pageFollowers = this.userService.findFollowersByUserId(user.getId(), page, 5);
		Assert.notNull(pageFollowers);

		result = new ModelAndView("user/list");
		result.addObject("users", pageFollowers.getContent());
		result.addObject("pageNumber", pageFollowers.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "actor/user/followers.do");
		
		return result;
	}
	
	// Followeds
	@RequestMapping(value="/followeds", method=RequestMethod.GET)
	public ModelAndView followeds(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<User> pageFolloweds;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		pageFolloweds = this.userService.findFollowedsByUserId(user.getId(), page, 5);
		Assert.notNull(pageFolloweds);

		result = new ModelAndView("user/list");
		result.addObject("users", pageFolloweds.getContent());
		result.addObject("pageNumber", pageFolloweds.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "actor/user/followeds.do");

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
		String requestURI;

		requestURI = "actor/user/edit.do";

		if (userForm.getId() == 0)
			result = new ModelAndView("user/create");
		else
			result = new ModelAndView("user/edit");

		result.addObject("modelo", "user");
		result.addObject("userForm", userForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
