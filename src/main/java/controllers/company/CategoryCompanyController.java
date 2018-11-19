
package controllers.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.BargainService;
import services.CategoryService;
import controllers.AbstractController;
import domain.Bargain;
import domain.Category;

@Controller
@RequestMapping("/category/company")
public class CategoryCompanyController extends AbstractController {

	//Services

	@Autowired
	private CategoryService	categoryService;

	@Autowired
	private BargainService	bargainService;


	// Constructor
	public CategoryCompanyController() {
		super();
	}

	@RequestMapping(value = "/addCategory", method = RequestMethod.GET)
	public ModelAndView addCategory(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam final int bargainId) {
		ModelAndView result;
		Page<Category> categories;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);

		Assert.isTrue(bargain.getCompany().getUserAccount().getId() == LoginService.getPrincipal().getId());

		categories = this.categoryService.findByNotBargainId(bargain, page, 5);
		Assert.notNull(categories);

		result = new ModelAndView("category/list");
		result.addObject("pageNumber", categories.getTotalPages());
		result.addObject("page", page);
		result.addObject("bargainId", bargainId);
		result.addObject("action", "add");
		result.addObject("categories", categories.getContent());
		result.addObject("bargainId", bargainId);
		result.addObject("requestURI", "category/company/addCategory.do");

		return result;
	}

	@RequestMapping(value = "/createBargain", method = RequestMethod.GET)
	public ModelAndView createBargain(@RequestParam(required = false, defaultValue = "1") final Integer page) {
		ModelAndView result;
		Page<Category> categories;

		categories = this.categoryService.findAllPaginated(page, 5);
		Assert.notNull(categories);

		result = new ModelAndView("category/list");
		result.addObject("pageNumber", categories.getTotalPages());
		result.addObject("page", page);
		result.addObject("bargainId", 0);
		result.addObject("action", "create");
		result.addObject("categories", categories.getContent());
		result.addObject("requestURI", "category/company/createBargain.do");

		return result;
	}

	@RequestMapping(value = "/removeCategory", method = RequestMethod.GET)
	public ModelAndView removeCategory(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam final int bargainId) {
		ModelAndView result;
		Page<Category> categories;
		Bargain bargain;

		bargain = this.bargainService.findOne(bargainId);
		Assert.notNull(bargain);

		Assert.isTrue(bargain.getCompany().getUserAccount().getId() == LoginService.getPrincipal().getId());

		categories = this.categoryService.findByBargainId(bargainId, page, 5);
		Assert.notNull(categories);

		result = new ModelAndView("category/list");
		result.addObject("pageNumber", categories.getTotalPages());
		result.addObject("page", page);
		result.addObject("bargainId", bargainId);
		result.addObject("action", "remove");
		result.addObject("bargainId", bargainId);
		result.addObject("categories", categories.getContent());
		result.addObject("requestURI", "category/company/removeCategory.do");

		return result;
	}

}
