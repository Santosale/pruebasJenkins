package controllers.user;

import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import security.LoginService;
import services.CreditCardService;
import services.SubscriptionService;
import services.TicketService;
import services.UserService;

import controllers.AbstractController;
import converters.CreditCardToStringConverter;
import domain.CreditCard;
import domain.User;

@Controller
@RequestMapping("/creditcard/user")
public class CreditCardUserController extends AbstractController {

	// Services
	@Autowired
	private CreditCardService creditCardService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	// Converter
	@Autowired
	private CreditCardToStringConverter creditCardToStringConverter;
	
	// Constructor
	public CreditCardUserController() {
		super();
	}
	
	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") final Integer page, HttpServletRequest request) {
		ModelAndView result;
		Page<CreditCard> pageCreditCard;
		Cookie cookie;
		Calendar calendar;
		
		calendar = Calendar.getInstance();
		
		pageCreditCard = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(pageCreditCard);
		
		result = new ModelAndView("creditcard/list");
		result.addObject("pageNumber", pageCreditCard.getTotalPages());
		result.addObject("page", page);
		result.addObject("creditCards", pageCreditCard.getContent());
		
		result.addObject("month", calendar.get(Calendar.MONTH) + 1);
		result.addObject("year", calendar.get(Calendar.YEAR) % 100);
		
		cookie = WebUtils.getCookie(request, "cookiemonster_"+LoginService.getPrincipal().getId());
		if(cookie != null) result.addObject("primaryCreditCard", cookie.getValue());
		
		return result;
	}
	
	@RequestMapping(value="/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int creditCardId) {
		ModelAndView result;
		CreditCard creditCard;
				
		creditCard = this.creditCardService.findOneToDisplayEdit(creditCardId);
		Assert.notNull(creditCard);
		
		result = new ModelAndView("creditcard/display");
		result.addObject("creditcard", creditCard);
		
		return result;
	}
	
	// Create
	@RequestMapping(value="/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		CreditCard creditCard;		

		creditCard = this.creditCardService.create(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		Assert.notNull(creditCard);

		result = this.createEditModelAndView(creditCard);
		
		return result;
	}
	
	@RequestMapping(value="/primary", method = RequestMethod.GET)
	public ModelAndView primary(@RequestParam int creditcardId, HttpServletResponse response) {
		ModelAndView result;
		CreditCard creditCard;
		
		creditCard = this.creditCardService.findOneToDisplayEdit(creditcardId);
		Assert.notNull(creditCard);
		
		// --- COOKIE --- //
		Cookie primaryCreditCard = new Cookie("cookiemonster_"+creditCard.getUser().getUserAccount().getId(), this.creditCardToStringConverter.convert(creditCard));
		primaryCreditCard.setHttpOnly(true);
		primaryCreditCard.setMaxAge(3600000);
		primaryCreditCard.setPath("/");
		response.addCookie(primaryCreditCard);
		// --- COOKIE --- //
		
		result = new ModelAndView("redirect:list.do");
		
		return result;
	}

	// Edit
	@RequestMapping(value="/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int creditcardId) {
		ModelAndView result;
		CreditCard creditCard;
		boolean isAdded;

		creditCard = this.creditCardService.findOneToDisplayEdit(creditcardId);
		Assert.notNull(creditCard);

		isAdded = this.ticketService.countByCreditCardId(creditcardId) == 0;
		if(!isAdded) {
			isAdded = this.subscriptionService.countByCreditCardId(creditcardId) == 0;
		}
		
		result = this.createEditModelAndView(creditCard);
		result.addObject("isAdded", isAdded);

		return result;
	}
	
	// Edit
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(CreditCard creditCard, BindingResult binding, @ModelAttribute("check") final String check) {
		ModelAndView result;
		User user;
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
				
		if(check.equals("checked")) creditCard.setHolderName(user.getName() + " " + user.getSurname());
		
		creditCard = this.creditCardService.reconstruct(creditCard, binding);
		
		if(binding.hasErrors()){
			result = this.createEditModelAndView(creditCard);
		}else{
			try {
				this.creditCardService.save(creditCard);
				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(creditCard, "creditcard.commit.error");
			}
		}
		
		result.addObject("check", check);
		
		return result;
	}
	
	// Delete
	@RequestMapping(value="/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(CreditCard creditCard, BindingResult binding) {
		ModelAndView result;
		
		try {
			this.creditCardService.delete(creditCard);
			result = new ModelAndView("redirect:list.do");
		} catch (Throwable oops) {
			result = this.createEditModelAndView(creditCard, "creditcard.commit.error");
		}
		
		return result;
	}
	
	// Ancillary methods
	protected ModelAndView createEditModelAndView(final CreditCard creditCard) {
		ModelAndView result;

		result = this.createEditModelAndView(creditCard, null);

		return result;
	}
	
	protected ModelAndView createEditModelAndView(final CreditCard creditCard, final String messageCode) {
		ModelAndView result;
		boolean isAdded;
		
		result = new ModelAndView("creditcard/edit");
		
		if(creditCard.getId() != 0) {
			isAdded = this.ticketService.countByCreditCardId(creditCard.getId()) == 0;
			if(!isAdded) {
				isAdded = this.subscriptionService.countByCreditCardId(creditCard.getId()) == 0;
			}
			result.addObject("isAdded", isAdded);
		}
		
		result.addObject("creditCard", creditCard);
		result.addObject("message", messageCode);

		return result;
	}
	
}
