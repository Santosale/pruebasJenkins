
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.AdvertisementService;
import services.ArticleService;
import services.CustomerService;
import services.UserService;
import controllers.AbstractController;
import domain.Advertisement;
import domain.Article;
import domain.Customer;
import domain.User;

@Controller
@RequestMapping("/article")
public class ArticleController extends AbstractController {

	// Services
	@Autowired
	private ArticleService			articleService;
	
	@Autowired
	private AdvertisementService 		advertisementService;
	
	@Autowired
	private UserService				userService;
	
	@Autowired
	private CustomerService	customerService;
	
	// Constructor
	public ArticleController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int userId, @RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Article> articles;
		Authority authority2;
		Authority authority3;
		Customer customer;
		User user;
		
		authority2 = new Authority();
		authority3 = new Authority();
		authority2.setAuthority("CUSTOMER");
		authority3.setAuthority("ADMIN");

		user = null;
		if (LoginService.isAuthenticated())
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			customer = this.customerService.findByUserAccountId(LoginService.getPrincipal().getId());
			articles = this.articleService.findAllUserPaginatedByCustomer(userId, customer.getId(), 0, 5);
		} else if (user != null && LoginService.isAuthenticated() && user.getId() == userId)
			articles = this.articleService.findByWritterId(userId, 0, 5);
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority3))
			articles = this.articleService.findAllUserPaginatedByAdmin(userId, 0, 5);
		else
			articles = this.articleService.findAllUserPaginated(userId, 0, 5);
		
		Assert.notNull(articles);
		
		result = new ModelAndView("article/list");

		result.addObject("articles", articles.getContent());
		result.addObject("pageNumber", articles.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "article/list.do");
		result.addObject("userId", userId);
		
		return result;
	}
	
	@RequestMapping(value = "/listSearch", method = RequestMethod.GET)
	public ModelAndView listSearch(@RequestParam(required=false) Integer userId, @RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam(required = false, defaultValue = "") final String keyword) {
		ModelAndView result;
		Page<Article> articles;
		
		articles = this.articleService.findPublicsPublishedSearch(userId, keyword, page, 5);
		Assert.notNull(articles);

		result = new ModelAndView("article/list");
		result.addObject("pageNumber", articles.getTotalPages());
		result.addObject("page", page);
		result.addObject("articles", articles.getContent());

		result.addObject("requestURI", "article/listSearch.do");

		result.addObject("keyword", keyword);
		result.addObject("userId", userId);

		return result;
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int articleId) {
		ModelAndView result;
		Article article;
		Advertisement advertisement;

		article = this.articleService.findOneToDisplay(articleId);
		Assert.notNull(article);

		advertisement = this.advertisementService.findRandomAdvertisement(article.getNewspaper().getId());
		
		result = new ModelAndView("article/display");
		result.addObject("article", article);
		result.addObject("advertisement", advertisement);

		return result;
	}
	
}
