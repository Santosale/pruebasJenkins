
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
import services.ArticleService;
import services.CustomerService;
import services.NewspaperService;
import services.SubscriptionNewspaperService;
import services.VolumeService;
import domain.Article;
import domain.Newspaper;

@Controller
@RequestMapping("/newspaper")
public class NewspaperController extends AbstractController {

	// Services
	@Autowired
	private NewspaperService				newspaperService;

	@Autowired
	private ArticleService					articleService;

	@Autowired
	private SubscriptionNewspaperService	subscriptionNewspaperService;

	@Autowired
	private CustomerService					customerService;

	@Autowired
	private VolumeService					volumeService;


	// Constructor
	public NewspaperController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int newspaperId, @RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Newspaper newspaper;

		newspaper = this.newspaperService.findOneToDisplay(newspaperId);

		Assert.notNull(newspaper);
		result = this.createDisplayModelAndView(newspaper, page);

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		if (LoginService.isAuthenticated())
			newspapers = this.newspaperService.findAllPaginated(page, 5);
		else
			newspapers = this.newspaperService.findPublicsAndPublicated(page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/list.do");

		return result;
	}

	@RequestMapping(value = "/listSearch", method = RequestMethod.GET)
	public ModelAndView listSearch(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam(required = false, defaultValue = "") final String keyword) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		if (LoginService.isAuthenticated())
			newspapers = this.newspaperService.findPublishedSearch(keyword, page, 5);
		else
			newspapers = this.newspaperService.findPublicsPublishedSearch(keyword, page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/listSearch.do");
		result.addObject("keyword", keyword);

		return result;
	}

	//Ancillary methods -----------------------
	protected ModelAndView createDisplayModelAndView(final Newspaper newspaper, final int page) {
		ModelAndView result;
		Page<Article> articles;
		Boolean canSeeArticles;
		Authority authority;
		Authority authority2;

		articles = this.articleService.findByNewspaperIdPaginated(newspaper.getId(), page, 5);

		canSeeArticles = false;
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		authority2 = new Authority();
		authority2.setAuthority("ADMIN");
		if (newspaper.getIsPrivate() == false)
			canSeeArticles = true;
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)
			&& (this.subscriptionNewspaperService.findByCustomerAndNewspaperId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), newspaper.getId()).size() == 1))
			canSeeArticles = true;
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)
			&& this.volumeService.findByCustomerIdAndNewspaperId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), newspaper.getId()).size() > 0)
			canSeeArticles = true;
		else if (LoginService.isAuthenticated() && newspaper.getPublisher().getUserAccount().getId() == LoginService.getPrincipal().getId())
			canSeeArticles = true;
		else if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2))
			canSeeArticles = true;

		result = new ModelAndView("newspaper/display");

		result.addObject("pageNumber", articles.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspaper", newspaper);
		result.addObject("articles", articles.getContent());
		result.addObject("canSeeArticles", canSeeArticles);

		return result;

	}
}
