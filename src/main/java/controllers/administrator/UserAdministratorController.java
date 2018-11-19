
package controllers.administrator;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.LevelService;
import services.UserService;
import controllers.AbstractController;
import domain.Level;
import domain.User;

@Controller
@RequestMapping(value = "/user/administrator")
public class UserAdministratorController extends AbstractController {

	// Supporting services
	@Autowired
	private UserService		userService;

	@Autowired
	private LevelService	levelService;


	// Constructor
	public UserAdministratorController() {
		super();
	}

	// List
	@RequestMapping(value = "/topFiveUsersMoreValorations", method = RequestMethod.GET)
	public ModelAndView topFiveUsersMoreValorations() {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.topFiveUsersMoreValorations(1, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/topFiveUsersMoreValorations.do");
		
		return result;
	}

	// List
	@RequestMapping(value = "/purchaseMoreTickets", method = RequestMethod.GET)
	public ModelAndView purchaseMoreTickets(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.purchaseMoreTickets(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/purchaseMoreTickets.do");
		
		return result;
	}

	// List
	@RequestMapping(value = "/purchaseLessTickets", method = RequestMethod.GET)
	public ModelAndView purchaseLessTickets(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.purchaseLessTickets(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/purchaseLessTickets.do");
		
		return result;
	}

	@RequestMapping(value = "/more10PercentageInteractions", method = RequestMethod.GET)
	public ModelAndView more10PercentageInteractions(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.more10PercentageInteractions(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/more10PercentageInteractions.do");
		
		return result;
	}

	@RequestMapping(value = "/moreAverageCharacterLenght", method = RequestMethod.GET)
	public ModelAndView moreAverageCharacterLenght(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.moreAverageCharacterLenght(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/moreAverageCharacterLenght.do");
		
		return result;
	}

	@RequestMapping(value = "/moreWonRaffles", method = RequestMethod.GET)
	public ModelAndView moreWonRaffles(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.moreWonRaffles(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/moreWonRaffles.do");
		return result;
	}

	@RequestMapping(value = "/purchase25PercentageMoreTotalForAllRaffles", method = RequestMethod.GET)
	public ModelAndView purchase25PercentageMoreTotalForAllRaffles(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<User> userPage;
		LinkedHashMap<User, Level> userLevel;
		Level level;

		userPage = this.userService.purchase25PercentageMoreTotalForAllRaffles(page, 5);
		Assert.notNull(userPage);

		userLevel = new LinkedHashMap<User, Level>();
		for (final User u : userPage.getContent()) {
			level = this.levelService.findByPoints(u.getPoints());
			Assert.notNull(level);
			userLevel.put(u, level);
		}

		result = new ModelAndView("user/list");
		result.addObject("page", page);
		result.addObject("pageNumber", userPage.getTotalPages());
		result.addObject("users", userLevel);
		result.addObject("requestURI", "/user/administrator/purchase25PercentageMoreTotalForAllRaffles.do");
		return result;
	}

}
