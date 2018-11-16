
package controllers.explorer;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ConfigurationService;
import services.ContactService;
import services.ExplorerService;
import controllers.AbstractController;
import domain.Contact;
import domain.Explorer;

@Controller
@RequestMapping(value = "/contact/explorer")
public class ContactExplorerController extends AbstractController {

	// Service
	@Autowired
	private ContactService			contactService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor
	public ContactExplorerController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;
		Collection<Contact> contacts;
		Explorer explorer;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		contacts = this.contactService.findByExplorerId(explorer.getId());
		Assert.notNull(contacts);

		result = new ModelAndView("contact/list");
		result.addObject("contacts", contacts);

		return result;
	}

	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		Contact contact;
		Explorer explorer;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		contact = this.contactService.create(explorer);
		Assert.notNull(contact);

		result = this.createEditModelAndView(contact);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int contactId) {
		ModelAndView result;
		Contact contact;

		contact = this.contactService.findOne(contactId);
		Assert.notNull(contact);

		result = this.createEditModelAndView(contact);

		return result;
	}

	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Contact contact, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(contact);
		else
			try {
				this.contactService.save(contact);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(contact, "contact.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Contact contact, final BindingResult binding) {
		ModelAndView result;

		try {
			this.contactService.delete(contact);
			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(contact, "contact.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Contact contact) {
		ModelAndView result;

		result = this.createEditModelAndView(contact, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Contact contact, final String messageCode) {
		ModelAndView result;
		String countryCode;

		countryCode = this.configurationService.findCountryCode();
		result = new ModelAndView("contact/edit");

		result.addObject("countryCode", countryCode);
		result.addObject("contact", contact);
		result.addObject("message", messageCode);

		return result;
	}

}
