
package controllers.moderator;

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
@RequestMapping(value = "/bargain/moderator")
public class BargainModeratorController extends AbstractController {

	@Autowired
	private BargainService	bargainService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;
		Boolean isSponsor;

		bargainPage = this.bargainService.findBargains(page, 5, "all", 0);
		Assert.notNull(bargainPage);

		//Vemos si es un sponsor
		isSponsor = false;

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", isSponsor);
		result.addObject("requestURI", "bargain/moderator/list.do");

		return result;
	}

	// Delete
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int bargainId) {
		ModelAndView result;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);

		this.bargainService.delete(bargain);

		result = new ModelAndView("redirect:list.do");

		return result;
	}
}
