
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
import services.ArticleService;
import services.UserService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Article;
import domain.Newspaper;
import domain.User;

@Controller
@RequestMapping("/article/user")
public class ArticleUserController extends AbstractController {

	// Services
	@Autowired
	private ArticleService			articleService;

	@Autowired
	private NewspaperService			newspaperService;
	
	@Autowired
	private UserService					userService;
	
	// Constructor
	
	public ArticleUserController() {
		super();
	}
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1") final int page) {
		ModelAndView result;
		Page<Article> articles;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		articles = this.articleService.findByWritterId(user.getId(), page, 5);
		Assert.notNull(articles);
		
		result = new ModelAndView("article/list");

		result.addObject("articles", articles.getContent());
		result.addObject("pageNumber", articles.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "article/user/list.do");
		
		return result;
	}
	
	@RequestMapping(value = "/listSearch", method = RequestMethod.GET)
	public ModelAndView listSearch(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam(required = false, defaultValue = "") final String keyword) {
		ModelAndView result;
		Page<Article> articles;
		User user;

		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		articles = this.articleService.findPublishedSearch(user.getId(), keyword, page, 5);
		Assert.notNull(articles);

		result = new ModelAndView("article/list");
		result.addObject("pageNumber", articles.getTotalPages());
		result.addObject("page", page);
		result.addObject("articles", articles.getContent());

		result.addObject("requestURI", "article/user/listSearch.do");
			
		result.addObject("keyword", keyword);

		return result;
	}
	
	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int newspaperId) {
		ModelAndView result;
		Article article;
		User writer;
		Newspaper newspaper;

		writer = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(writer);

		newspaper = this.newspaperService.findOne(newspaperId);
		Assert.notNull(newspaper);
		
		article = this.articleService.create(writer, newspaper);
		Assert.notNull(article);
		
		result = this.createEditModelAndView(article);

		return result;
	}
	
	// Request -------------------------------------------------------------------------------------------

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int articleId) {
		ModelAndView result;
		Article article;

		article = this.articleService.findOne(articleId);
		Assert.notNull(article);
		
		Assert.isTrue(article.getIsFinalMode() == false);

		result = this.createEditModelAndView(article);

		return result;
	}
	
	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Article article, final BindingResult binding) {
		ModelAndView result;

		article = this.articleService.reconstruct(article, binding);
		
		if (binding.hasErrors()){
			result = this.createEditModelAndView(article);
		}else
			try {
				this.articleService.save(article);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(article, "article.commit.error");
			}

		return result;
	}
	
	// Delete
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Article article, final BindingResult binding) {
		ModelAndView result;
		
		try {
			this.articleService.delete(article);
			result = new ModelAndView("redirect:list.do");
		} catch(final Throwable oops) {
			result = this.createEditModelAndView(article, "article.commit.error");
		}
				
		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Article article) {
		ModelAndView result;

		result = this.createEditModelAndView(article, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Article article, final String messageCode) {
		ModelAndView result;		

		if (article.getId() > 0)
			result = new ModelAndView("article/edit");
		else
			result = new ModelAndView("article/create");
		
		result.addObject("article", article);
		result.addObject("message", messageCode);
		return result;
	}
	
}
