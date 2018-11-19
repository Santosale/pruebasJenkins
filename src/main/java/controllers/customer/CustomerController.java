
package controllers.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.CustomerService;
import controllers.AbstractController;
import domain.Customer;
import forms.CustomerForm;

@Controller
@RequestMapping(value = "/actor/customer")
public class CustomerController extends AbstractController {

	// Services
	@Autowired
	private CustomerService	customerService;


	// Constructor
	public CustomerController() {
		super();
	}

	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;
		CustomerForm customerForm;

		customerForm = new CustomerForm();

		result = this.createEditModelAndView(customerForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final CustomerForm customerForm, final BindingResult binding) {
		ModelAndView result;
		Customer customer;
		boolean next;

		next = true;
		result = null;
		customer = null;
		if(customerForm.getPhoneNumber() != null && customerForm.getPhoneNumber().equals("")) customerForm.setPhoneNumber(null);
		try {
			customer = this.customerService.reconstruct(customerForm, binding);
		} catch (final Throwable e) {

			if (binding.hasErrors())
				result = this.createEditModelAndView(customerForm);
			else
				result = this.createEditModelAndView(customerForm, "actor.commit.error");

			next = false;
		}

		if (next)
			if (binding.hasErrors())
				result = this.createEditModelAndView(customerForm);
			else
				try {
					this.customerService.save(customer);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(customerForm, "actor.commit.error");
				}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final CustomerForm customerForm) {
		ModelAndView result;

		result = this.createEditModelAndView(customerForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final CustomerForm customerForm, final String messageCode) {
		ModelAndView result;
		String requestURI;

		requestURI = "actor/customer/edit.do";

		if (customerForm.getId() == 0)
			result = new ModelAndView("customer/create");
		else
			result = new ModelAndView("customer/edit");

		result.addObject("modelo", "customer");
		result.addObject("customerForm", customerForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
