
package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ActorService;
import services.ApplicationService;
import services.CreditCardService;
import services.ExplorerService;
import services.SponsorService;
import services.SponsorshipService;
import domain.Actor;
import domain.Application;
import domain.CreditCard;
import domain.Explorer;
import domain.Sponsor;
import domain.Sponsorship;

@Controller
@RequestMapping("/creditcard/{actorType}")
public class CreditCardController extends AbstractController {

	// Services
	@Autowired
	private ActorService		actorService;

	@Autowired
	private CreditCardService	creditCardService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private SponsorService		sponsorService;

	@Autowired
	private SponsorshipService	sponsorshipService;


	// Constructor
	public CreditCardController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@PathVariable(value = "actorType") final String actorType) {
		ModelAndView result;
		Collection<CreditCard> creditCards;
		Actor actor;

		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		creditCards = this.creditCardService.findByActorId(actor.getId());
		Assert.notNull(creditCards);

		result = new ModelAndView("creditcard/list");

		result.addObject("creditCards", creditCards);
		result.addObject("actorType", actorType);
		result.addObject("actorId", actor.getId());

		return result;
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int creditCardId) {
		ModelAndView result;
		CreditCard creditCard;

		creditCard = this.creditCardService.findOne(creditCardId);
		Assert.notNull(creditCard);

		result = new ModelAndView("creditcard/display");

		result.addObject("creditCard", creditCard);

		return result;
	}

	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@PathVariable(value = "actorType") final String actorType) {
		ModelAndView result;
		CreditCard creditCard;
		Actor actor;

		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		creditCard = this.creditCardService.create(actor);
		Assert.notNull(creditCard);

		result = this.createEditModelAndView(creditCard, actorType);

		//result.addObject("actorType", actorType);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable(value = "actorType") final String actorType, @RequestParam final int creditCardId) {
		ModelAndView result;
		CreditCard creditCard;

		creditCard = this.creditCardService.findOne(creditCardId);
		Assert.notNull(creditCard);

		result = this.createEditModelAndView(creditCard, actorType);

		//result.addObject("actorType", actorType);

		return result;
	}

	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final CreditCard creditCard, final BindingResult binding, @PathVariable(value = "actorType") final String actorType) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(creditCard, actorType);
		//	result.addObject("actorType", actorType);
		else
			try {
				this.creditCardService.save(creditCard);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(creditCard, "creditcard.commit.error", actorType);
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final CreditCard creditCard, final BindingResult binding, @PathVariable(value = "actorType") final String actorType) {
		ModelAndView result;
		System.out.println("Petición: " + creditCard);
		try {
			this.creditCardService.delete(creditCard);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(creditCard, "creditcard.commit.error", actorType);
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final CreditCard creditCard, final String actorType) {
		ModelAndView result;

		result = this.createEditModelAndView(creditCard, null, actorType);

		return result;
	}

	protected ModelAndView createEditModelAndView(final CreditCard creditCard, final String messageCode, final String actorType) {
		ModelAndView result;
		Explorer explorer;
		Sponsor sponsor;
		boolean isAdded;
		Collection<Application> applications;

		isAdded = false;

		result = new ModelAndView("creditcard/edit");

		if (creditCard.getId() != 0)
			if (actorType.equals("explorer")) {
				explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
				applications = this.applicationService.findByExplorerId(explorer.getId());
				if (applications != null)
					for (final Application a : this.applicationService.findByExplorerId(explorer.getId()))
						if (a.getCreditCard() != null && a.getCreditCard().equals(creditCard)) {
							isAdded = true;
							break;
						}
			} else if (actorType.equals("sponsor")) {
				sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
				for (final Sponsorship s : this.sponsorshipService.findBySponsorId(sponsor.getId()))
					if (s.getCreditCard().equals(creditCard)) {
						isAdded = true;
						break;
					}
			}
		result.addObject("creditCard", creditCard);
		result.addObject("message", messageCode);
		result.addObject("isAdded", isAdded);
		result.addObject("actorType", actorType);

		return result;
	}

}
