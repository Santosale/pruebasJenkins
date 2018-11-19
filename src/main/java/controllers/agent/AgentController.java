
package controllers.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AgentService;
import controllers.AbstractController;
import domain.Agent;
import forms.AgentForm;

@Controller
@RequestMapping(value = "/actor/agent")
public class AgentController extends AbstractController {

	// Services
	@Autowired
	private AgentService	agentService;


	// Constructor
	public AgentController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		AgentForm agentForm;

		agentForm = new AgentForm();

		result = this.createEditModelAndView(agentForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final AgentForm agentForm, final BindingResult binding) {
		ModelAndView result;
		Agent agent;
		boolean next;

		next = true;
		result = null;
		agent = null;
		if(agentForm.getPhoneNumber() != null && agentForm.getPhoneNumber().equals("")) agentForm.setPhoneNumber(null);
		try {
			agent = this.agentService.reconstruct(agentForm, binding);
		} catch (final Throwable e) {

			if (binding.hasErrors())
				result = this.createEditModelAndView(agentForm);
			else
				result = this.createEditModelAndView(agentForm, "actor.commit.error");

			next = false;
		}

		if (next)
			if (binding.hasErrors())
				result = this.createEditModelAndView(agentForm);
			else
				try {
					this.agentService.save(agent);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(agentForm, "actor.commit.error");
				}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final AgentForm agentForm) {
		ModelAndView result;

		result = this.createEditModelAndView(agentForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final AgentForm agentForm, final String messageCode) {
		ModelAndView result;
		String requestURI;

		requestURI = "actor/agent/edit.do";

		if (agentForm.getId() == 0)
			result = new ModelAndView("agent/create");
		else
			result = new ModelAndView("agent/edit");

		result.addObject("modelo", "agent");
		result.addObject("agentForm", agentForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
