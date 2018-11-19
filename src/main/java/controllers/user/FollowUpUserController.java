
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
import services.FollowUpService;
import services.UserService;
import controllers.AbstractController;
import domain.Article;
import domain.FollowUp;
import domain.User;

@Controller
@RequestMapping("/followUp/user")
public class FollowUpUserController extends AbstractController {

	// Services
	@Autowired
	private FollowUpService	followUpService;

	@Autowired
	private UserService		userService;

	@Autowired
	private ArticleService	articleService;


	// Constructor
	public FollowUpUserController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<FollowUp> followUps;
		User user;

		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);

		followUps = this.followUpService.findByUserIdPaginated(user.getId(), page, 5);
		Assert.notNull(followUps);

		result = new ModelAndView("followUp/list");
		result.addObject("pageNumber", followUps.getTotalPages());
		result.addObject("page", page);
		result.addObject("canDelete", true);
		result.addObject("followUps", followUps.getContent());
		result.addObject("requestURI", "followUp/user/list.do");

		return result;
	}

	//Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int articleId) {
		ModelAndView result;
		FollowUp followUp;
		Article article;

		article = this.articleService.findOne(articleId);
		Assert.notNull(article);

		followUp = this.followUpService.create(article);

		result = this.createEditModelAndView(followUp);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(FollowUp followUp, final BindingResult binding) {
		ModelAndView result;
		FollowUp saved;

		followUp = this.followUpService.reconstruct(followUp, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(followUp);
		else
			try {
				saved = this.followUpService.save(followUp);

				result = new ModelAndView("redirect:/followUp/display.do?followUpId=" + saved.getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(followUp, "follow.up.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int followUpId) {
		ModelAndView result;
		FollowUp followUp;

		followUp = this.followUpService.findOne(followUpId);
		Assert.notNull(followUp);

		this.followUpService.delete(followUp);

		//Si el objeto borrado traía el articulo volvemos a él si no volvemos al listado de follow-up del usuario
		result = new ModelAndView("redirect:/followUp/user/list.do");

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final FollowUp followUp) {
		ModelAndView result;

		result = this.createEditModelAndView(followUp, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final FollowUp followUp, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("followUp/create");

		result.addObject("followUp", followUp);
		result.addObject("message", messageCode);

		return result;
	}

}
