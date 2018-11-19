
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BargainService;
import controllers.AbstractController;
import domain.Bargain;

@Controller
@RequestMapping("/bargain/administrator")
public class BargainAdministratorController extends AbstractController {

	//Services

	@Autowired
	private BargainService	bargainService;


	@RequestMapping(value = "/listWithMoreSponsorships", method = RequestMethod.GET)
	public ModelAndView listWithMoreSponsorships(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;

		bargainPage = this.bargainService.listWithMoreSponsorships(page, 5);
		Assert.notNull(bargainPage);

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", false);
		result.addObject("plan", null);
		result.addObject("requestURI", "bargain/administrator/listWithMoreSponsorships.do");

		return result;
	}

	@RequestMapping(value = "/listWithLessSponsorships", method = RequestMethod.GET)
	public ModelAndView listWithLessSponsorships(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;

		bargainPage = this.bargainService.listWithLessSponsorships(page, 5);
		Assert.notNull(bargainPage);

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", false);
		result.addObject("plan", null);
		result.addObject("requestURI", "bargain/administrator/listWithLessSponsorships.do");

		return result;
	}

	@RequestMapping(value = "/isInMoreWishList", method = RequestMethod.GET)
	public ModelAndView isInMoreWishList(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;

		bargainPage = this.bargainService.findAreInMoreWishList(page, 5);
		Assert.notNull(bargainPage);

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", false);
		result.addObject("plan", null);
		result.addObject("requestURI", "bargain/administrator/isInMoreWishList.do");

		return result;
	}
}
