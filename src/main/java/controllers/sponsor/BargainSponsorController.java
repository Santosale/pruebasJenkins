
package controllers.sponsor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.BargainService;
import services.SponsorService;
import controllers.AbstractController;
import domain.Bargain;
import domain.Sponsor;

@Controller
@RequestMapping(value = "/bargain/sponsor")
public class BargainSponsorController extends AbstractController {

	@Autowired
	private BargainService	bargainService;

	@Autowired
	private SponsorService	sponsorService;


	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;
		Boolean isSponsor;
		Sponsor sponsor;

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);

		bargainPage = this.bargainService.findBySponsorIdWithNoSponsorship(sponsor.getId(), page, 5);
		Assert.notNull(bargainPage);

		//Vemos si es un sponsor
		isSponsor = true;

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);
		result.addObject("isSponsor", isSponsor);
		result.addObject("requestURI", "bargain/sponsor/list.do");

		return result;
	}
}
