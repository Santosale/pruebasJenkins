
package controllers.agentAdministrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdvertisementService;
import controllers.AbstractController;
import domain.Advertisement;

@Controller
@RequestMapping("/advertisement/agentAdministrator")
public class AdvertisementAgentAdministratorController extends AbstractController {

	// Services
	@Autowired
	private AdvertisementService	advertisementService;


	// Constructor
	public AdvertisementAgentAdministratorController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int advertisementId) {
		ModelAndView result;
		Advertisement advertisement;

		advertisement = this.advertisementService.findOneToDisplay(advertisementId);
		Assert.notNull(advertisement);

		result = new ModelAndView("advertisement/display");
		result.addObject("advertisement", advertisement);

		return result;
	}
}
