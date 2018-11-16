package controllers.administrator;

import javax.validation.Valid;

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
@RequestMapping(value = "/notification/administrator")
public class NotificationAdministratorController extends AbstractController {

	// Service
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private AdministratorService administratorService;
	
	// Constructor
	public NotificationAdministratorController() {
		super();
	}
	
	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(){
		ModelAndView result;
		Administrator administrator;
		Message notification;
		
		administrator = this.administratorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(administrator);
		
		notification = this.messageService.create(administrator);
		Assert.notNull(notification);
		
		result = this.createEditModelAndView(notification);
		
		return result;
	}
	
	// Send
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveNotification(@Valid @ModelAttribute(value = "notification") final Message notification, final BindingResult binding) {
		ModelAndView result;
		
		if (binding.hasErrors()) {
			result = this.createEditModelAndView(notification);
		} else
			try {
				this.messageService.broadcastNotification(notification);
				result = new ModelAndView("redirect:/message/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(notification, "notification.commit.error");
			}

		return result;
	}
	
	
	// Ancillary methods
	protected ModelAndView createEditModelAndView(Message notification) {
		ModelAndView result;

		result = this.createEditModelAndView(notification, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message notification, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("notification/create");

		result.addObject("notification", notification);
		result.addObject("message", messageCode);

		return result;
	}
	
	
}
