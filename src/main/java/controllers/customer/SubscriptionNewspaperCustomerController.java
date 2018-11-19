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
import services.SubscriptionNewspaperService;
import services.CustomerService;
import services.NewspaperService;

import controllers.AbstractController;
import converters.CreditCardToStringConverter;
import converters.StringToCreditCardConverter;
import domain.Customer;
import domain.SubscriptionNewspaper;

@Controller
@RequestMapping("/subscriptionNewspaper/customer")
public class SubscriptionNewspaperCustomerController extends AbstractController {

	// Services
	@Autowired
	private SubscriptionNewspaperService subscriptionNewspaperService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private NewspaperService newspaperService;
	
	@Autowired
	private CreditCardToStringConverter	creditCardToStringConverter;
	
	@Autowired
	private StringToCreditCardConverter	stringToCreditCardConverter;
	
	// Constructor
	public SubscriptionNewspaperCustomerController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") final Integer page) {
		ModelAndView result;
		Page<SubscriptionNewspaper> pageSubscriptionNewspaper;
		
		pageSubscriptionNewspaper = this.subscriptionNewspaperService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(pageSubscriptionNewspaper);
		
		result = new ModelAndView("subscriptionNewspaper/list");
		result.addObject("pageNumber", pageSubscriptionNewspaper.getTotalPages());
		result.addObject("page", page);
		result.addObject("subscriptionNewspapers", pageSubscriptionNewspaper.getContent());
		
		return result;
	}
	
	// Create
	@RequestMapping(value="/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int newspaperId, final HttpServletRequest request) {
		ModelAndView result;
		SubscriptionNewspaper subscriptionNewspaper;
		Cookie cookie;
		Customer customer;

		subscriptionNewspaper = this.subscriptionNewspaperService.create(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()), this.newspaperService.findOne(newspaperId));
		customer = this.customerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(subscriptionNewspaper);

		result = this.createEditModelAndView(subscriptionNewspaper);
		
		cookie = WebUtils.getCookie(request, "cookiemonster_" + customer.getId());
		if (cookie != null)
			result.addObject("lastCreditCard", this.stringToCreditCardConverter.convert(cookie.getValue()));

		return result;
	}

	// Edit
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int subscriptionNewspaperId) {
		ModelAndView result;
		SubscriptionNewspaper subscriptionNewspaper;

		subscriptionNewspaper = this.subscriptionNewspaperService.findOneToEdit(subscriptionNewspaperId);
		Assert.notNull(subscriptionNewspaper);

		result = this.createEditModelAndView(subscriptionNewspaper);

		return result;
	}
	
	// Edit
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(SubscriptionNewspaper subscriptionNewspaper, BindingResult binding, final HttpServletResponse response) {
		ModelAndView result;
		Cookie lastCreditCard;
		
		subscriptionNewspaper = this.subscriptionNewspaperService.reconstruct(subscriptionNewspaper, binding);
		
		if(binding.hasErrors()){
			result = this.createEditModelAndView(subscriptionNewspaper);
		}else{
			try {
				this.subscriptionNewspaperService.save(subscriptionNewspaper);
				// --- COOKIE --- //
				lastCreditCard = new Cookie("cookiemonster_" + subscriptionNewspaper.getCustomer().getId(), this.creditCardToStringConverter.convert(subscriptionNewspaper.getCreditCard()));
				lastCreditCard.setHttpOnly(true);
				lastCreditCard.setMaxAge(3600000);
				response.addCookie(lastCreditCard);
				// --- COOKIE --- //
				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(subscriptionNewspaper, "subscriptionNewspaper.commit.error");
			}
		}
		
		return result;
	}
	
	// Delete
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(SubscriptionNewspaper subscriptionNewspaper, BindingResult binding) {
		ModelAndView result;
		
		try {
			this.subscriptionNewspaperService.delete(subscriptionNewspaper);
			result = new ModelAndView("redirect:list.do");
		} catch (Throwable oops) {
			result = this.createEditModelAndView(subscriptionNewspaper, "subscriptionNewspaper.commit.error");
		}
		
		return result;
	}
	
	// Ancillary methods
	protected ModelAndView createEditModelAndView(final SubscriptionNewspaper subscriptionNewspaper) {
		ModelAndView result;

		result = this.createEditModelAndView(subscriptionNewspaper, null);

		return result;
	}
	
	protected ModelAndView createEditModelAndView(final SubscriptionNewspaper subscriptionNewspaper, final String messageCode) {
		ModelAndView result;
		
		result = new ModelAndView("subscriptionNewspaper/edit");
		
		result.addObject("subscriptionNewspaper", subscriptionNewspaper);
		result.addObject("message", messageCode);

		return result;
	}
	
}
