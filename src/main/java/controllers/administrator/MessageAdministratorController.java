package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.AdministratorService;
import services.MessageService;

import controllers.AbstractController;
import domain.Administrator;
import domain.Message;

@Controller
@RequestMapping(value = "/message/administrator")
public class MessageAdministratorController extends AbstractController {

	// Service
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private AdministratorService administratorService;
	
	// Constructor
	public MessageAdministratorController() {
		super();
	}
	
	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(){
		ModelAndView result;
		Administrator administrator;
		Message message;
		
		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(administrator);
		
		message = this.messageService.create(administrator);
		Assert.notNull(message);
		
		result = this.createEditModelAndView(message);
		
		return result;
	}
	
	// Send
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveNotification(@ModelAttribute(value = "notification") Message message, final BindingResult binding) {
		ModelAndView result;
		
		message = this.messageService.reconstruct(message, binding);
		
		if (binding.hasErrors()) {
			result = this.createEditModelAndView(message);
		} else
			try {
				this.messageService.broadcastNotification(message);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(message, "message.commit.error");
			}

		return result;
	}
	
	
	// Ancillary methods
	protected ModelAndView createEditModelAndView(Message message) {
		ModelAndView result;

		result = this.createEditModelAndView(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message message, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("message/broadcast");

		result.addObject("notification", message);
		result.addObject("message", messageCode);
		result.addObject("showRecipients", false);

		return result;
	}
	
	
}
