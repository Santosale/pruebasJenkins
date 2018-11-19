
package controllers.sponsor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BargainService;
import services.ConfigurationService;
import services.SponsorshipService;
import controllers.AbstractController;
import domain.Bargain;
import domain.Sponsorship;

@Controller
@RequestMapping("/sponsorship/sponsor")
public class SponsorshipSponsorController extends AbstractController {

	// Services
	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private BargainService			bargainService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public SponsorshipSponsorController() {
		super();
	}

	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int sponsorshipId) {
		ModelAndView result;
		Sponsorship sponsorship;
		Boolean canDisplay;

		sponsorship = this.sponsorshipService.findOneToDisplayAndEdit(sponsorshipId);
		Assert.notNull(sponsorship);

		canDisplay = this.bargainService.canDisplay(sponsorship.getBargain());

		result = new ModelAndView("sponsorship/display");
		result.addObject("sponsorship", sponsorship);
		result.addObject("canDisplay", canDisplay);
		result.addObject("linkBroken", super.checkLinkImage(sponsorship.getImage()));
		result.addObject("imageBroken", this.configurationService.findDefaultImage());

		return result;
	}
	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Sponsorship> sponsorships;

		sponsorships = this.sponsorshipService.findBySponsorId(page, 5);
		Assert.notNull(sponsorships);

		result = new ModelAndView("sponsorship/list");
		result.addObject("pageNumber", sponsorships.getTotalPages());
		result.addObject("page", page);
		result.addObject("sponsorships", sponsorships.getContent());
		result.addObject("requestURI", "sponsorship/sponsor/list.do");

		return result;
	}

	//Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int bargainId) {
		ModelAndView result;
		Sponsorship sponsorship;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);

		Assert.isNull(this.sponsorshipService.findBySponsorIdAndBargainId(bargainId));

		sponsorship = this.sponsorshipService.create(bargain);

		result = this.createEditModelAndView(sponsorship);

		return result;

	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int sponsorshipId) {
		ModelAndView result;
		Sponsorship sponsorship;

		sponsorship = this.sponsorshipService.findOneToDisplayAndEdit(sponsorshipId);
		Assert.notNull(sponsorship);

		result = this.createEditModelAndView(sponsorship);

		return result;
	}

	//Post Methods
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Sponsorship sponsorship, final BindingResult binding) {
		ModelAndView result;

		sponsorship = this.sponsorshipService.reconstruct(sponsorship, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(sponsorship);
		else
			try {
				this.sponsorshipService.save(sponsorship);

				result = new ModelAndView("redirect:list.do");

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(sponsorship, "sponsorship.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Sponsorship sponsorship, final BindingResult binding) {
		ModelAndView result;

		try {
			this.sponsorshipService.delete(sponsorship);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(sponsorship, "sponsorship.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Sponsorship sponsorship) {
		ModelAndView result;

		result = this.createEditModelAndView(sponsorship, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Sponsorship sponsorship, final String messageCode) {
		ModelAndView result;

		if (sponsorship.getId() == 0)
			result = new ModelAndView("sponsorship/create");
		else
			result = new ModelAndView("sponsorship/edit");

		result.addObject("sponsorship", sponsorship);
		result.addObject("message", messageCode);

		return result;
	}

}
