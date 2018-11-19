
package controllers.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.CustomerService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Newspaper;

@Controller
@RequestMapping("/newspaper/customer")
public class NewspaperCustomerController extends AbstractController {

	// Services
	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private CustomerService		customerService;


	// Constructor
	public NewspaperCustomerController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findByCustomerId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/customer/list.do");

		return result;
	}

	@RequestMapping(value = "/listForSubscribe", method = RequestMethod.GET)
	public ModelAndView listForSubscribe(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findForSubscribe(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/customer/listForSubscribe.do");

		return result;
	}

}
