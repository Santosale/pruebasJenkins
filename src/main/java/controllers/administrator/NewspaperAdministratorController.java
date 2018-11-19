
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.NewspaperService;
import controllers.AbstractController;
import domain.Newspaper;

@Controller
@RequestMapping("/newspaper/administrator")
public class NewspaperAdministratorController extends AbstractController {

	// Services
	@Autowired
	private NewspaperService	newspaperService;


	// Constructor
	public NewspaperAdministratorController() {
		super();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView accept(@RequestParam final int newspaperId) {
		ModelAndView result;
		Newspaper newspaper;

		newspaper = this.newspaperService.findOne(newspaperId);
		Assert.notNull(newspaper);

		this.newspaperService.delete(newspaper);

		result = new ModelAndView("redirect:/newspaper/list.do");

		return result;
	}

	@RequestMapping(value = "/findTaboos", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.findTaboos(page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/administrator/findTaboos.do");

		return result;
	}

	@RequestMapping(value = "/listMoreAverage", method = RequestMethod.GET)
	public ModelAndView listTenPercentageMoreAvg(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.find10PercentageMoreAvg(page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/administrator/listMoreAverage.do");

		return result;
	}

	@RequestMapping(value = "/listFewerAverage", method = RequestMethod.GET)
	public ModelAndView listTenPercentageLessAvg(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Newspaper> newspapers;

		newspapers = this.newspaperService.find10PercentageLessAvg(page, 5);
		Assert.notNull(newspapers);

		result = new ModelAndView("newspaper/list");
		result.addObject("pageNumber", newspapers.getTotalPages());
		result.addObject("page", page);
		result.addObject("newspapers", newspapers.getContent());
		result.addObject("requestURI", "newspaper/administrator/listFewerAverage.do");

		return result;
	}

}
