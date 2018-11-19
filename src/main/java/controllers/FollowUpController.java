
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ArticleService;
import services.FollowUpService;
import domain.Article;
import domain.FollowUp;

@Controller
@RequestMapping("/followUp")
public class FollowUpController extends AbstractController {

	// Services
	@Autowired
	private FollowUpService	followUpService;

	@Autowired
	private ArticleService	articleService;


	// Constructor
	public FollowUpController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int articleId, @RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<FollowUp> followUps;
		Article article;

		article = this.articleService.findOne(articleId);
		Assert.notNull(article);

		//Vemos que el artículo se pueda ver
		Assert.isTrue(this.articleService.checkVisibleArticle(article));

		followUps = this.followUpService.findByArticleIdPaginated(article.getId(), page, 5);
		Assert.notNull(followUps);

		result = new ModelAndView("followUp/list");
		result.addObject("pageNumber", followUps.getTotalPages());
		result.addObject("page", page);
		result.addObject("articleId", article.getId());
		result.addObject("followUps", followUps.getContent());
		result.addObject("requestURI", "followUp/list.do");

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int followUpId) {
		ModelAndView result;
		FollowUp followUp;

		followUp = this.followUpService.findOneToDisplay(followUpId);
		Assert.notNull(followUp);

		result = new ModelAndView("followUp/display");
		result.addObject("followUp", followUp);

		return result;
	}
}
