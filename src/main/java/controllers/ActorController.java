
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.ActorService;
import domain.Actor;
import forms.AgentForm;
import forms.CustomerForm;
import forms.UserForm;

@Controller
@RequestMapping("/actor")
public class ActorController extends AbstractController {

	// Services
	@Autowired
	private ActorService	actorService;


	// Constructors
	public ActorController() {
		super();
	}

	// Profile
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView result;
		Actor actor;

		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		result = this.createEditModelAndView(actor);

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Actor actor) {
		ModelAndView result;

		result = this.createEditModelAndView(actor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Actor actor, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		String requestURI;
		String tipoActor;
		UserForm userForm;
		CustomerForm customerForm;
		AgentForm agentForm;
		Authority authorityUser, authorityCustomer, authorityAgent;

		authorityUser = new Authority();
		authorityUser.setAuthority("USER");

		authorityCustomer = new Authority();
		authorityCustomer.setAuthority("CUSTOMER");
		
		authorityAgent = new Authority();
		authorityAgent.setAuthority("AGENT");

		//Creamos la URI
		tipoActor = actor.getClass().getSimpleName().toLowerCase();
		requestURI = "actor/" + tipoActor + "/edit.do";

		canEdit = false;
		result = new ModelAndView(tipoActor + "/edit");

		if (actor.getUserAccount().getId() == LoginService.getPrincipal().getId())
			canEdit = true;

		//Añadimos los parámetros
		if (actor.getUserAccount().getAuthorities().contains(authorityUser)) {

			userForm = new UserForm();

			userForm.setPostalAddress(actor.getPostalAddress());
			userForm.setEmailAddress(actor.getEmailAddress());
			userForm.setId(actor.getId());
			userForm.setName(actor.getName());
			userForm.setPhoneNumber(actor.getPhoneNumber());
			userForm.setSurname(actor.getSurname());
			userForm.setUsername(actor.getUserAccount().getUsername());

			result.addObject("userForm", userForm);

		} else if (actor.getUserAccount().getAuthorities().contains(authorityCustomer)) {

			customerForm = new CustomerForm();
			customerForm.setPostalAddress(actor.getPostalAddress());
			customerForm.setEmailAddress(actor.getEmailAddress());
			customerForm.setId(actor.getId());
			customerForm.setName(actor.getName());
			customerForm.setPhoneNumber(actor.getPhoneNumber());
			customerForm.setSurname(actor.getSurname());
			customerForm.setUsername(actor.getUserAccount().getUsername());

			result.addObject("customerForm", customerForm);

		} else if (actor.getUserAccount().getAuthorities().contains(authorityAgent)) {

			agentForm = new AgentForm();
			agentForm.setPostalAddress(actor.getPostalAddress());
			agentForm.setEmailAddress(actor.getEmailAddress());
			agentForm.setId(actor.getId());
			agentForm.setName(actor.getName());
			agentForm.setPhoneNumber(actor.getPhoneNumber());
			agentForm.setSurname(actor.getSurname());
			agentForm.setUsername(actor.getUserAccount().getUsername());

			result.addObject("agentForm", agentForm);

		}else
			result.addObject("administrator", actor);

		//Añadimos objetos comunes
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
