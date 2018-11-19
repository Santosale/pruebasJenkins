
package controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ContactService;
import forms.ContactForm;

@Controller
@RequestMapping("/contact")
public class ContactController extends AbstractController {

	@Autowired
	private ContactService	mailService;


	// Constructors -----------------------------------------------------------

	public ContactController() {
		super();
	}

	// Index ------------------------------------------------------------------		

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView result;
		ContactForm contactForm;

		result = new ModelAndView("contact/edit");

		contactForm = new ContactForm();
		result.addObject("contactForm", contactForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final ContactForm contactForm, final BindingResult binding) {
		ModelAndView result;

		result = null;

		if (binding.hasErrors())
			result = this.createEditModelAndView(contactForm);
		else
			try {
				this.mailService.send("aisscreamacas@gmail.com", contactForm.getSubject(), contactForm.getBody());

				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {

				result = this.createEditModelAndView(contactForm, "contact.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final ContactForm contactForm) {
		ModelAndView result;

		result = this.createEditModelAndView(contactForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final ContactForm contactForm, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("contact/edit");

		result.addObject("contactForm", contactForm);
		result.addObject("message", messageCode);

		return result;
	}

}
