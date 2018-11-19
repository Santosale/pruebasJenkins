
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import controllers.AbstractController;
import domain.Category;

@Controller
@RequestMapping("/category/administrator")
public class CategoryAdministratorController extends AbstractController {

	//Services

	@Autowired
	private CategoryService	categoryService;


	@RequestMapping(value = "/moreBargainThanAverage", method = RequestMethod.GET)
	public ModelAndView createBargain(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Category> categories;

		categories = this.categoryService.moreBargainThanAverage(page, 5);
		Assert.notNull(categories);

		result = new ModelAndView("category/list");
		result.addObject("pageNumber", categories.getTotalPages());
		result.addObject("page", page);
		result.addObject("bargainId", 0);
		result.addObject("action", "dashboard");
		result.addObject("categories", categories.getContent());
		result.addObject("requestURI", "category/administrator/moreBargainThanAverage.do");

		return result;
	}
}
