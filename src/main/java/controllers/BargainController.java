
package controllers;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import services.BargainService;
import services.CommentService;
import services.ConfigurationService;
import services.PlanService;
import services.SponsorshipService;
import services.TagService;
import services.UserService;
import domain.Bargain;
import domain.Comment;
import domain.Plan;
import domain.Sponsorship;
import domain.Tag;
import domain.User;

@Controller
@RequestMapping(value = "/bargain")
public class BargainController extends AbstractController {

	@Autowired
	private BargainService			bargainService;

	@Autowired
	private CommentService			commentService;

	@Autowired
	private TagService				tagService;

	@Autowired
	private PlanService				planService;

	@Autowired
	private UserService				userService;

	@Autowired
	private SponsorshipService		sponsoshipService;

	@Autowired
	private ConfigurationService	configurationService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;
		Authority authority;
		Boolean isSponsor;
		Plan plan;
		Authority authority2;
		User user;

		authority2 = new Authority();
		authority2.setAuthority("USER");

		authority = new Authority();
		authority.setAuthority("SPONSOR");

		bargainPage = this.bargainService.findBargains(page, 5, "all", 0);
		Assert.notNull(bargainPage);

		//Vemos si es un sponsor
		isSponsor = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority))
			isSponsor = true;

		//Vemos si es user qué plan tiene
		plan = null;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			plan = this.planService.findByUserId(user.getId());
		}

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", isSponsor);
		result.addObject("plan", plan);
		result.addObject("requestURI", "bargain/list.do");

		return result;
	}

	@RequestMapping(value = "/bycategory", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page, @RequestParam final int categoryId) {
		ModelAndView result;
		Page<Bargain> bargainPage;
		Authority authority;
		Boolean isSponsor;
		Plan plan;
		Authority authority2;
		User user;

		authority2 = new Authority();
		authority2.setAuthority("USER");

		authority = new Authority();
		authority.setAuthority("SPONSOR");

		bargainPage = this.bargainService.findBargains(page, 5, "category", categoryId);
		Assert.notNull(bargainPage);
		//

		//Vemos si es un sponsor
		isSponsor = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority))
			isSponsor = true;

		//Vemos si es user qué plan tiene
		plan = null;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			plan = this.planService.findByUserId(user.getId());
		}

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", isSponsor);
		result.addObject("plan", plan);
		result.addObject("categoryId", categoryId);
		result.addObject("requestURI", "bargain/bycategory.do");

		return result;
	}

	@RequestMapping(value = "/bytag", method = RequestMethod.GET)
	public ModelAndView bytag(@RequestParam(required = false, defaultValue = "1") final int page, @RequestParam final int tagId) {
		ModelAndView result;
		Page<Bargain> bargainPage;
		Authority authority;
		Boolean isSponsor;
		Plan plan;
		Authority authority2;
		User user;

		authority2 = new Authority();
		authority2.setAuthority("USER");

		authority = new Authority();
		authority.setAuthority("SPONSOR");

		bargainPage = this.bargainService.findBargains(page, 5, "tag", tagId);
		Assert.notNull(bargainPage);
		//

		//Vemos si es un sponsor
		isSponsor = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority))
			isSponsor = true;

		//Vemos si es user qué plan tiene
		plan = null;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			plan = this.planService.findByUserId(user.getId());
		}

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", isSponsor);
		result.addObject("plan", plan);
		result.addObject("tagId", tagId);
		result.addObject("requestURI", "bargain/bytag.do");

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int bargainId, @RequestParam(required = false, defaultValue = "1") final int page, final HttpServletRequest request) {
		ModelAndView result;
		Bargain bargain;
		Page<Comment> commentPage;
		Page<Sponsorship> sponsorshipPage;
		Collection<Tag> tags;
		Plan plan;
		Authority authority;
		User user;
		Map<Comment, Boolean> mapCommentBoolean;
		Boolean hasPlan;
		Map<Sponsorship, Boolean> mapSponsorshipBoolean;
		Boolean canAddWishList;

		authority = new Authority();
		authority.setAuthority("USER");

		bargain = this.bargainService.findOneToDisplay(bargainId);
		Assert.notNull(bargain);

		sponsorshipPage = this.sponsoshipService.findRandomSponsorships(bargain.getId(), 1, 4);

		//Url sponsorship rotas
		mapSponsorshipBoolean = new LinkedHashMap<Sponsorship, Boolean>();
		for (final Sponsorship sponsorship : sponsorshipPage.getContent())
			mapSponsorshipBoolean.put(sponsorship, super.checkLinkImage(sponsorship.getImage()));

		tags = this.tagService.findByBargainId(bargainId);

		//Vemos si es user qué plan tiene
		plan = null;
		canAddWishList = false;
		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority)) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			plan = this.planService.findByUserId(user.getId());

			//Ver si la puede agregar a la wishLIst
			canAddWishList = !user.getWishList().contains(bargain);
		}

		//Vemos los comentarios
		commentPage = this.commentService.findByBargainIdAndNoRepliedComment(bargainId, page, 5);
		mapCommentBoolean = new LinkedHashMap<Comment, Boolean>();
		for (final Comment comment : commentPage.getContent()) {
			hasPlan = false;
			if (this.planService.findByUserId(comment.getUser().getId()) != null)
				hasPlan = true;
			mapCommentBoolean.put(comment, hasPlan);
		}

		result = new ModelAndView("bargain/display");
		result.addObject("bargain", bargain);
		result.addObject("mapCommentBoolean", mapCommentBoolean);
		result.addObject("tags", tags);
		result.addObject("page", page);
		result.addObject("pageNumber", commentPage.getTotalPages());
		result.addObject("plan", plan);
		result.addObject("canAddWishList", canAddWishList);
		result.addObject("mapLinkBooleanBargain", super.checkLinkImages(bargain.getProductImages()));
		result.addObject("imageBroken", this.configurationService.findDefaultImage());
		result.addObject("mapSponsorshipBoolean", mapSponsorshipBoolean);
		result.addObject("sponsorships", sponsorshipPage.getContent());
		result.addObject("urlShare", super.makeUrl(request));

		return result;
	}

}
