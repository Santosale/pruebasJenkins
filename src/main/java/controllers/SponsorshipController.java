
package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BargainService;
import services.ConfigurationService;
import services.SponsorshipService;
import domain.Bargain;
import domain.Sponsorship;

@Controller
@RequestMapping("/sponsorship")
public class SponsorshipController extends AbstractController {

	// Services
	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private BargainService			bargainService;


	// Constructor
	public SponsorshipController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam final int bargainId) {
		ModelAndView result;
		Page<Sponsorship> sponsorships;
		Map<Sponsorship, Boolean> linkBroken;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);
		Assert.isTrue(this.bargainService.canDisplay(bargain));

		sponsorships = this.sponsorshipService.findByBargainIdPageable(bargainId, page, 5);
		Assert.notNull(sponsorships);

		//Miramos url rotas
		linkBroken = new HashMap<Sponsorship, Boolean>();
		for (final Sponsorship sponsorship : sponsorships.getContent())
			linkBroken.put(sponsorship, super.checkLinkImage(sponsorship.getImage()));

		result = new ModelAndView("sponsorship/list");
		result.addObject("pageNumber", sponsorships.getTotalPages());
		result.addObject("page", page);
		result.addObject("sponsorships", sponsorships.getContent());
		result.addObject("requestURI", "sponsorship/list.do");
		result.addObject("linkBroken", linkBroken);
		result.addObject("bargainId", bargainId);
		result.addObject("imageBroken", this.configurationService.findDefaultImage());

		return result;
	}
}
