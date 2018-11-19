
package controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.CustomerService;
import services.NewspaperService;
import services.SubscriptionVolumeService;
import services.VolumeService;
import domain.Newspaper;
import domain.Volume;

@Controller
@RequestMapping("/volume")
public class VolumeController extends AbstractController {

	// Services

	@Autowired
	private VolumeService				volumeService;

	@Autowired
	private CustomerService				customerService;

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;

	@Autowired
	private NewspaperService			newspaperService;


	// Constructor
	public VolumeController() {
		super();
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int volumeId, @RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Volume volume;

		volume = this.volumeService.findOne(volumeId);

		Assert.notNull(volume);
		result = this.createDisplayModelAndView(volume, page);

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Volume> volumes;

		volumes = this.volumeService.findAllPaginated(page, 5);
		Assert.notNull(volumes);

		result = new ModelAndView("volume/list");
		result.addObject("pageNumber", volumes.getTotalPages());
		result.addObject("page", page);
		result.addObject("volumes", volumes.getContent());
		result.addObject("requestURI", "volume/list.do");

		return result;
	}

	//Ancillary methods -----------------------
	protected ModelAndView createDisplayModelAndView(final Volume volume, final int page) {
		ModelAndView result;
		Boolean canCreateVolumeSubscription;
		Authority authority;
		Page<Newspaper> newspapers;
		List<Newspaper> newspapersAux;
		Integer pageNumber, fromId, toId;

		canCreateVolumeSubscription = false;
		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority))
			if (this.subscriptionVolumeService.findByCustomerIdAndVolumeId(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), volume.getId()) == null)
				canCreateVolumeSubscription = true;

		result = new ModelAndView("volume/display");
		if (LoginService.isAuthenticated()) {
			newspapersAux = new ArrayList<Newspaper>(volume.getNewspapers());
			fromId = this.fromIdAndToId(newspapersAux.size(), page)[0];
			toId = this.fromIdAndToId(newspapersAux.size(), page)[1];

			pageNumber = newspapersAux.size();
			pageNumber = (int) Math.floor(((pageNumber / (5 + 0.0)) - 0.1) + 1);
			result.addObject("pageNumber", pageNumber);
			result.addObject("newspapers", newspapersAux.subList(fromId, toId));
		} else {
			newspapers = this.newspaperService.findByVolumeAllPublics(volume.getId(), page, 5);
			Assert.notNull(newspapers);
			result.addObject("pageNumber", newspapers.getTotalPages());
			result.addObject("newspapers", newspapers.getContent());
		}

		result.addObject("page", page);
		result.addObject("requestURI", "volume/display.do");
		result.addObject("volume", volume);
		result.addObject("canCreateVolumeSubscription", canCreateVolumeSubscription);

		return result;

	}

	private Integer[] fromIdAndToId(final Integer tamañoAux, final Integer page) {
		Integer tamaño, pageAux, fromId, toId;
		tamaño = tamañoAux;
		Integer[] result;

		result = new Integer[2];

		pageAux = page;
		if (page <= 0)
			pageAux = 1;

		fromId = (pageAux - 1) * 5;
		if (fromId > tamaño)
			fromId = 0;
		toId = (pageAux * 5);
		if (tamaño > 5) {
			if (toId > tamaño && fromId == 0)
				toId = 5;
			else if (toId > tamaño && fromId != 0)
				toId = tamaño;
		} else
			toId = tamaño;

		result[0] = fromId;
		result[1] = toId;

		return result;
	}
}
