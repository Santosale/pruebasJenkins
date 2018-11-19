package controllers.moderator;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Level;
import domain.User;

import services.LevelService;
import services.UserService;

@Controller
@RequestMapping(value="/user/moderator")
public class UserModeratorController {

	// Supporting services
	@Autowired
	private UserService userService;
	
	@Autowired
	private LevelService levelService;
	
	// Constructor
	public UserModeratorController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list.do", method=RequestMethod.GET)
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
		result.addObject("requestURI", "user/moderator/list.do");		
		
		return result;
	}
	
	// Ban
	@RequestMapping(value="/ban.do", method=RequestMethod.GET)
	public ModelAndView ban(@RequestParam final int userId) {
		ModelAndView result;
		User user;
		
		user = this.userService.findOne(userId);
		Assert.notNull(user);
		
		this.userService.ban(user);
		
		result = new ModelAndView("redirect:list.do");
		
		return result;
	}
	
}
