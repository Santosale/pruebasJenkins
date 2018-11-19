
package controllers.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.AgentService;
import services.NewspaperService;
import controllers.AbstractController;
import domain.Newspaper;

@Controller
@RequestMapping("/newspaper/agent")
public class NewspaperAgentController extends AbstractController {

	// Services
	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private AgentService		agentService;


	@RequestMapping(value = "/listWithAdvertisements", method = RequestMethod.GET)
	public ModelAndView listWithAdvertisements(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findNewspaperWithAdvertisements(this.agentService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/agent/listWithAdvertisements.do");

		return result;
	}

	@RequestMapping(value = "/listWithNoAdvertisements", method = RequestMethod.GET)
	public ModelAndView listWithnoAdvertisements(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findNewspaperWithNoAdvertisements(this.agentService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/agent/listWithNoAdvertisements.do");

		return result;
	}

}
