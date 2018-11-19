package controllers.user;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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

import controllers.AbstractController;
import converters.StringToCreditCardConverter;

import domain.CreditCard;
import domain.Raffle;
import domain.Ticket;
import domain.User;
import forms.TicketForm;

import security.LoginService;
import services.CreditCardService;
import services.RaffleService;
import services.TicketService;
import services.UserService;
import services.res.PaypalClient;

@Controller
@RequestMapping(value="/ticket/user")
public class TicketUserController extends AbstractController {

	// Supporint services
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private RaffleService raffleService;
	
	@Autowired
	private UserService	userService;
	
	@Autowired
	private CreditCardService creditCardService;
	
	// Converters
	@Autowired
	private StringToCreditCardConverter stringToCreditCardConverter;
	
    private final PaypalClient paypalClient;
	
    
    // Constructor
    public TicketUserController() {
        this.paypalClient = new PaypalClient();
    }
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public ModelAndView list(@RequestParam(required=false, defaultValue="1")  final int page) {
		ModelAndView result;
		Page<Ticket> ticketPage;
		
		ticketPage = this.ticketService.findByUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(ticketPage);
		
		result = new ModelAndView("ticket/list");
		result.addObject("tickets", ticketPage.getContent());
		result.addObject("pageNumber", ticketPage.getTotalPages());
		result.addObject("page", page);
		
		result.addObject("requestURI", "ticket/user/list.do");

		result.addObject("showRaffle", true);
		
		return result;
	}
	
	@RequestMapping(value="/buy", method = RequestMethod.GET)
	public ModelAndView buy(@RequestParam(defaultValue="CREDITCARD") final String method, @RequestParam(defaultValue="1", required=false) final Integer amount, @RequestParam int raffleId, HttpServletRequest request) {
		ModelAndView result;
		Raffle raffle;
		TicketForm ticketForm;
		Map<String, Object> attributes;
		Collection<CreditCard> creditCards;
		Cookie cookie;

		raffle = this.raffleService.findOneToDisplay(raffleId);
		Assert.notNull(raffle);
		
		result = null;
		if(method.equals("CREDITCARD")) {
			
			ticketForm = new TicketForm();
			ticketForm.setRaffle(raffle);
			
			creditCards = this.creditCardService.findValidByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(creditCards);
			
			result = new ModelAndView("ticket/buy");
			
			cookie = WebUtils.getCookie(request, "cookiemonster_"+LoginService.getPrincipal().getId());
			if(cookie != null) {
				result.addObject("primaryCreditCard", cookie.getValue());
				ticketForm.setCreditCard(this.stringToCreditCardConverter.convert(cookie.getValue()));
			}
			
			result.addObject("ticketForm", ticketForm);
			result.addObject("creditCards", creditCards);
			result.addObject("raffle", raffle);
			
		} else if(method.equals("PAYPAL")) {
			attributes = this.paypalClient.createPayment(String.valueOf(raffle.getPrice()*amount), raffleId, amount, request);
			
			result = new ModelAndView("redirect:"+(String) attributes.get("redirect_url"));
			
			for(String key: attributes.keySet())
				result.addObject(key, attributes.get(key));
		}
		
		return result;
	}
	
	@RequestMapping(value="/completepayment", method = RequestMethod.GET)
	public ModelAndView completePayment(@RequestParam final String paymentId, @RequestParam final String PayerID, @RequestParam final Integer amount, @RequestParam int raffleId, final HttpServletRequest request) {
		ModelAndView result;
		Collection<Ticket> tickets;
		TicketForm ticketForm;
		Raffle raffle;
		User user;
		
		raffle = this.raffleService.findOne(raffleId);
		Assert.notNull(raffle);
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
		
		if(paymentId != null) {
			paypalClient.completePayment(paymentId, PayerID);
		}
		
		ticketForm = new TicketForm();
		ticketForm.setAmount(amount);
		ticketForm.setRaffle(raffle);
		
		tickets = this.ticketService.reconstruct(ticketForm, null);
		Assert.notNull(tickets);

		try {
			this.ticketService.save(tickets, true);
			result = new ModelAndView("redirect:/ticket/user/list.do");
		} catch (Throwable oops) {
			result = buyModelAndView(ticketForm, "ticket.commit.error", request);
		}
		
		return result;
	}
	
	@RequestMapping(value="/buy", method = RequestMethod.POST, params = "save")
	public ModelAndView buy(final TicketForm ticketForm, final BindingResult binding, final HttpServletRequest request) {
		ModelAndView result;
		Collection<Ticket> tickets;
		
        tickets = this.ticketService.reconstruct(ticketForm, binding);
		Assert.notNull(tickets);
	
		if(binding.hasErrors()) {
			result = this.buyModelAndView(ticketForm, request);
		} else {
			try {
				this.ticketService.save(tickets, false);
				result = new ModelAndView("redirect:/ticket/user/list.do");
			} catch (Throwable oops) {
				result = buyModelAndView(ticketForm, "ticket.commit.error", request);
			}
		}
		
		return result;
	}
	
	protected ModelAndView buyModelAndView(final TicketForm ticketForm, final HttpServletRequest request) {
		ModelAndView result;

		result = this.buyModelAndView(ticketForm, null, request);

		return result;
	}

	protected ModelAndView buyModelAndView(final TicketForm ticketForm, final String messageCode, final HttpServletRequest request) {
		ModelAndView result;
		Collection<CreditCard> creditCards;
		Cookie cookie;

		creditCards = this.creditCardService.findValidByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(creditCards);
		
		result = new ModelAndView("ticket/buy");

		if(request != null) {
			cookie = WebUtils.getCookie(request, "cookiemonster_"+LoginService.getPrincipal().getId());
			if(cookie != null) {
				result.addObject("primaryCreditCard", cookie.getValue());
				ticketForm.setCreditCard(this.stringToCreditCardConverter.convert(cookie.getValue()));
			}
		}
		
		result.addObject("ticketForm", ticketForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", "ticket/user/buy.do?raffleId="+ticketForm.getRaffle().getId());
		result.addObject("creditCards", creditCards);

		return result;
	}
	
}
