
package controllers.user;

import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import security.LoginService;
import services.CreditCardService;
import services.SubscriptionService;
import services.UserService;
import controllers.AbstractController;
import converters.StringToCreditCardConverter;
import domain.CreditCard;
import domain.Subscription;

@Controller
@RequestMapping(value = "/subscription/user")
public class SubscriptionUserController extends AbstractController {

	// Supporting services
	@Autowired
	private SubscriptionService			subscriptionService;

	@Autowired
	private UserService					userService;

	@Autowired
	private CreditCardService			creditCardService;

	@Autowired
	private StringToCreditCardConverter	stringToCreditCardConverter;


	public SubscriptionUserController() {
		super();
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Subscription subscription;

		subscription = this.subscriptionService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId());

		result = new ModelAndView("subscription/display");
		result.addObject("subscription", subscription);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView request(@RequestParam final int planId, final HttpServletRequest request) {
		ModelAndView result;
		Subscription subscription;
		Cookie cookie;

		subscription = this.subscriptionService.create(planId);

		result = this.createEditModelAndView(subscription);

		cookie = WebUtils.getCookie(request, "cookiemonster_" + subscription.getUser().getUserAccount().getId());
		if (cookie != null) {
			result.addObject("lastCreditCard", cookie.getValue());
			subscription.setCreditCard(this.stringToCreditCardConverter.convert(cookie.getValue()));
		}

		return result;

	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int subscriptionId) {
		ModelAndView result;
		Subscription subscription;

		subscription = this.subscriptionService.findOneToEdit(subscriptionId);

		result = this.createEditModelAndView(subscription);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Subscription subscription, final BindingResult binding, final HttpServletResponse response, final HttpServletRequest request) {
		ModelAndView result;
		Cookie cookie;

		subscription = this.subscriptionService.reconstruct(subscription, binding);

		if (binding.hasErrors()) {
			result = this.createEditModelAndView(subscription);
			cookie = WebUtils.getCookie(request, "cookiemonster_" + subscription.getUser().getUserAccount().getId());
			if (cookie != null && subscription.getId() == 0) {
				result.addObject("lastCreditCard", cookie.getValue());
				subscription.setCreditCard(this.stringToCreditCardConverter.convert(cookie.getValue()));
			}
		} else
			try {
				this.subscriptionService.save(subscription);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(subscription, "subscription.commit.error");
				cookie = WebUtils.getCookie(request, "cookiemonster_" + subscription.getUser().getUserAccount().getId());
				if (cookie != null && subscription.getId() == 0) {
					result.addObject("lastCreditCard", cookie.getValue());
					subscription.setCreditCard(this.stringToCreditCardConverter.convert(cookie.getValue()));
				}
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Subscription subscription, final BindingResult binding) {
		ModelAndView result;

		try {
			this.subscriptionService.delete(subscription);
			result = new ModelAndView("redirect:display.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(subscription, "subscription.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Subscription subscription) {
		ModelAndView result;

		result = this.createEditModelAndView(subscription, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Subscription subscription, final String messageCode) {
		ModelAndView result;
		Collection<CreditCard> creditCards;

		creditCards = this.creditCardService.findValidByUserAccountId(LoginService.getPrincipal().getId());

		if (subscription.getId() != 0) {
			result = new ModelAndView("subscription/edit");
			creditCards.remove(subscription.getCreditCard());
		} else
			result = new ModelAndView("subscription/create");

		result.addObject("creditCards", creditCards);
		result.addObject("subscription", subscription);
		result.addObject("message", messageCode);

		return result;
	}

}
