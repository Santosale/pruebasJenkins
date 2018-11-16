
package controllers;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import services.TripService;
import domain.Category;
import domain.Trip;

@Controller
@RequestMapping("/category")
public class CategoryController extends AbstractController {

	// Services
	@Autowired
	private CategoryService	categoryService;

	@Autowired
	private TripService		tripService;


	// Constructor
	public CategoryController() {
		super();
	}

	// List / Navigate
	@RequestMapping(value = "/navigate", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) final Integer categoryId) {
		ModelAndView result;
		Category category;
		int categoryIdPadre;

		if (categoryId == null) {
			categoryIdPadre = this.categoryService.findCategoryWithoutFather().getId();
			category = this.categoryService.findOne(categoryIdPadre);
		} else
			category = this.categoryService.findOne(categoryId);

		Assert.notNull(category);

		result = new ModelAndView("category/navigate");
		result.addObject("category", category);

		return result;
	}

	// List trips
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int categoryId) {
		ModelAndView result;
		Collection<Trip> trips;

		trips = this.tripService.findByCategoryId(categoryId);

		result = new ModelAndView("trip/list");

		result.addObject("trips", trips);
		result.addObject("currentMoment", new Date());
		result.addObject("requestURI", "category/list.do");

		return result;
	}

}
