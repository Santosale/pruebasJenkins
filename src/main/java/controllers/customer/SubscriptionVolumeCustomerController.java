
package controllers.customer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import security.LoginService;
import services.CustomerService;
import services.SubscriptionVolumeService;
import controllers.AbstractController;
import converters.CreditCardToStringConverter;
import converters.StringToCreditCardConverter;
import domain.Customer;
import domain.SubscriptionVolume;

@Controller
@RequestMapping("/subscriptionVolume/customer")
public class SubscriptionVolumeCustomerController extends AbstractController {

	// Services
	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;

	@Autowired
	private CustomerService				customerService;

	@Autowired
	private CreditCardToStringConverter	creditCardToStringConverter;

	@Autowired
	private StringToCreditCardConverter	stringToCreditCardConverter;


	// Constructor
	public SubscriptionVolumeCustomerController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<SubscriptionVolume> subscriptionVolumes;

		subscriptionVolumes = this.subscriptionVolumeService.findByCustomerId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(subscriptionVolumes);

		result = new ModelAndView("subscriptionVolume/list");
		result.addObject("pageNumber", subscriptionVolumes.getTotalPages());
		result.addObject("page", page);
		result.addObject("subscriptionVolumes", subscriptionVolumes.getContent());

		return result;
	}

	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int volumeId, final HttpServletRequest request) {
		ModelAndView result;
		SubscriptionVolume subscriptionVolume;
		Cookie cookie;
		Customer customer;

		subscriptionVolume = this.subscriptionVolumeService.create(volumeId);
		customer = this.customerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(subscriptionVolume);

		result = this.createEditModelAndView(subscriptionVolume);

		cookie = WebUtils.getCookie(request, "cookiemonster_" + customer.getId());
		if (cookie != null)
			result.addObject("lastCreditCard", this.stringToCreditCardConverter.convert(cookie.getValue()));

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int subscriptionVolumeId) {
		ModelAndView result;
		SubscriptionVolume subscriptionVolume;

		subscriptionVolume = this.subscriptionVolumeService.findOneToEdit(subscriptionVolumeId);
		Assert.notNull(subscriptionVolume);

		result = this.createEditModelAndView(subscriptionVolume);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(SubscriptionVolume subscriptionVolume, final BindingResult binding, final HttpServletResponse response) {
		ModelAndView result;
		Cookie lastCreditCard;

		subscriptionVolume = this.subscriptionVolumeService.reconstruct(subscriptionVolume, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(subscriptionVolume);
		else
			try {
				this.subscriptionVolumeService.save(subscriptionVolume);
				// --- COOKIE --- //
				lastCreditCard = new Cookie("cookiemonster_" + subscriptionVolume.getCustomer().getId(), this.creditCardToStringConverter.convert(subscriptionVolume.getCreditCard()));
				lastCreditCard.setHttpOnly(true);
				lastCreditCard.setMaxAge(3600000);
				response.addCookie(lastCreditCard);
				// --- COOKIE --- //
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(subscriptionVolume, "subscriptionVolume.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final SubscriptionVolume subscriptionVolume, final BindingResult binding) {
		ModelAndView result;

		try {
			this.subscriptionVolumeService.delete(subscriptionVolume);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(subscriptionVolume, "subscriptionVolume.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final SubscriptionVolume subscriptionVolume) {
		ModelAndView result;

		result = this.createEditModelAndView(subscriptionVolume, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final SubscriptionVolume subscriptionVolume, final String messageCode) {
		ModelAndView result;

		if (subscriptionVolume.getId() == 0)
			result = new ModelAndView("subscriptionVolume/create");
		else
			result = new ModelAndView("subscriptionVolume/edit");

		result.addObject("subscriptionVolume", subscriptionVolume);
		result.addObject("message", messageCode);

		return result;
	}

}
