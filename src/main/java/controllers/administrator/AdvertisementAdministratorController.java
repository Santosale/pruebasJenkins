
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("/advertisement/administrator")
public class AdvertisementAdministratorController extends AbstractController {

	// Services
	@Autowired
	private AdvertisementService	advertisementService;


	// Constructor
	public AdvertisementAdministratorController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Advertisement> advertisements;

		advertisements = this.advertisementService.findAllPaginated(page, 5);
		Assert.notNull(advertisements);

		result = new ModelAndView("advertisement/list");
		result.addObject("advertisements", advertisements.getContent());
		result.addObject("pageNumber", advertisements.getTotalPages());
		result.addObject("page", page);
		result.addObject("requestURI", "advertisement/administrator/list.do");

		return result;
	}

	// Delete
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int advertisementId) {
		ModelAndView result;
		Advertisement advertisement;

		advertisement = this.advertisementService.findOne(advertisementId);
		Assert.notNull(advertisement);

		this.advertisementService.deleteInappropiate(advertisement);

		result = new ModelAndView("redirect:list.do");

		return result;
	}

}
