
package controllers.explorer;

import java.util.Collection;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ApplicationService;
import services.ExplorerService;
import services.FinderService;
import controllers.AbstractController;
import domain.Application;
import domain.Explorer;
import domain.Finder;

@Controller
@RequestMapping(value = "/finder/explorer")
public class FinderExplorerController extends AbstractController {

	// Service
	@Autowired
	private FinderService		finderService;

	@Autowired
	private ExplorerService		explorerService;

	@Autowired
	private ApplicationService	applicationService;


	// Constructor
	public FinderExplorerController() {
		super();
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;
		Explorer explorer;
		Finder finder;
		Date currentMoment;
		Collection<Application> applications;

		currentMoment = new Date();

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		applications = this.applicationService.findByExplorerId(explorer.getId());
		Assert.notNull(explorer);

		finder = this.finderService.findByExplorerId(explorer.getId());
		Assert.notNull(finder);

		result = new ModelAndView("finder/display");

		result.addObject("finder", finder);
		result.addObject("currentMoment", currentMoment);
		result.addObject("applications", applications);

		return result;
	}

	// List trips
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView listTrips() {
		ModelAndView result;
		Explorer explorer;
		Finder finder;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		finder = this.finderService.findByExplorerId(explorer.getId());
		Assert.notNull(finder);

		result = new ModelAndView("trip/list");

		result.addObject("trips", finder.getTrips());
		result.addObject("requestURI", "finder/explorer/list.do");
		result.addObject("currentMoment", new Date());
		return result;
	}

	// Search
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search() {
		ModelAndView result;
		Explorer explorer;
		Finder finder, oldFinder;

		explorer = this.explorerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(explorer);

		oldFinder = this.finderService.findByExplorerId(explorer.getId());
		Assert.notNull(oldFinder);
		
		//		finder.setKeyWord(null);
		//		finder.setMaxPrice(null);
		//		finder.setMinPrice(null);
		//		finder.setStartedDate(null);
		//		finder.setFinishedDate(null);
		finder = this.finderService.create(explorer);
		finder.setId(oldFinder.getId());
		finder.setVersion(oldFinder.getVersion());

		result = this.createEditModelAndView(finder);

		return result;
	}

	// Save
	@RequestMapping(value = "/search", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Finder finder, final BindingResult binding) {
		ModelAndView result;

		if (finder.getKeyWord().equals(""))
			finder.setKeyWord(null);

		if (binding.hasErrors()) {
			System.out.println(binding.toString());
			result = this.createEditModelAndView(finder);
		} else
			try {
				this.finderService.save(finder);
				result = new ModelAndView("redirect:display.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(finder, "finder.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Finder finder) {
		ModelAndView result;

		result = this.createEditModelAndView(finder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Finder finder, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("finder/search");

		result.addObject("finder", finder);
		result.addObject("message", messageCode);

		return result;
	}

}
