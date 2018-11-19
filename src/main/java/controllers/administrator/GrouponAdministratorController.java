
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.GrouponService;
import controllers.AbstractController;
import domain.Groupon;

@Controller
@RequestMapping("/groupon/administrator")
public class GrouponAdministratorController extends AbstractController {

	// Services
	@Autowired
	private GrouponService	grouponService;


	// Constructor
	public GrouponAdministratorController() {
		super();
	}

	@RequestMapping(value = "/tenPercentageMoreParticipationsThanAverage", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Groupon> groupons;

		groupons = this.grouponService.tenPercentageMoreParticipationsThanAverage(page, 5);
		Assert.notNull(groupons);

		result = new ModelAndView("groupon/list");
		result.addObject("pageNumber", groupons.getTotalPages());
		result.addObject("page", page);
		result.addObject("groupons", groupons.getContent());
		result.addObject("requestURI", "groupon/administrator/tenPercentageMoreParticipationsThanAverage.do");

		return result;
	}

}
