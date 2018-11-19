
package controllers.moderator;

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
@RequestMapping(value = "/groupon/moderator")
public class GrouponModeratorController extends AbstractController {

	// Supporting services
	@Autowired
	private GrouponService	grouponService;


	public GrouponModeratorController() {
		super();
	}

	// Edit
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int grouponId) {
		ModelAndView result;
		Groupon groupon;

		groupon = this.grouponService.findOne(grouponId);
		Assert.notNull(groupon);

		this.grouponService.deleteFromModerator(groupon);

		result = new ModelAndView("redirect:/groupon/list.do");

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Groupon> groupons;

		groupons = null;

		groupons = this.grouponService.findAllPaginated(page, 5);

		Assert.notNull(groupons);

		result = new ModelAndView("groupon/list");
		result.addObject("pageNumber", groupons.getTotalPages());
		result.addObject("page", page);
		result.addObject("groupons", groupons.getContent());
		result.addObject("requestURI", "groupon/moderator/list.do");

		return result;
	}

}
