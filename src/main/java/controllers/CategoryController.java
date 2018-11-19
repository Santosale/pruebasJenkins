
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import services.ConfigurationService;
import domain.Category;

@Controller
@RequestMapping("/category")
public class CategoryController extends AbstractController {

	// Services
	@Autowired
	private CategoryService			categoryService;

	@Autowired
	private ConfigurationService	configurationService;


	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam(required = false) final Integer categoryId, @RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam(required = false) final Integer categoryToMoveId, @RequestParam(
		required = false) final boolean error) {
		final ModelAndView result;
		Category category;
		Page<Category> pageCategory;

		//Si la categoría NO es una de la raíz
		if (categoryId != null) {
			category = this.categoryService.findOne(categoryId);
			Assert.notNull(category);
			pageCategory = this.categoryService.findByFatherCategoryId(category.getId(), page, 5);

			//Si la categoría es una de la raíz
		} else {
			pageCategory = this.categoryService.findWithoutFather(page, 5);
			category = null;
		}

		result = new ModelAndView("category/display");

		//Vemos que la categoría a mover solo lo use el administrador
		if (categoryToMoveId != null)
			result.addObject("categoryToMoveId", categoryToMoveId);

		if (error)
			result.addObject("message", "category.commit.error");

		//Imagen rota
		if (category != null) {
			result.addObject("linkBroken", super.checkLinkImage(category.getImage()));
			result.addObject("imageBroken", this.configurationService.findDefaultImage());
		}

		result.addObject("childrenCategories", pageCategory.getContent());
		result.addObject("category", category);
		result.addObject("pageNumber", pageCategory.getTotalPages());
		result.addObject("page", page);

		return result;
	}

}
