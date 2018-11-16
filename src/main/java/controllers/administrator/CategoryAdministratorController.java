
package controllers.administrator;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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

	// Services
	@Autowired
	private CategoryService	categoryService;


	// Constructor
	public CategoryAdministratorController() {
		super();
	}

	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int categoryId) {
		ModelAndView result;
		Category category;

		category = this.categoryService.create();
		category.setFatherCategory(this.categoryService.findOne(categoryId));

		result = this.createEditModelAndView(category);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int categoryId) {
		ModelAndView result;
		Category category;

		category = this.categoryService.findOne(categoryId);

		result = this.createEditModelAndView(category);

		return result;
	}

	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Category category, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(category);
		else
			try {
				this.categoryService.save(category);
				result = new ModelAndView("redirect:/category/navigate.do?categoryId=" + category.getFatherCategory().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(category, "category.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Category category, final BindingResult binding) {
		ModelAndView result;
		int fatherId;

		try {
			fatherId = category.getFatherCategory().getId();
			this.categoryService.delete(category);
			result = new ModelAndView("redirect:/category/navigate.do?categoryId=" + fatherId);
		} catch (final Throwable oops) {

			result = this.createEditModelAndView(category, "category.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Category category) {
		ModelAndView result;

		result = this.createEditModelAndView(category, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Category category, final String messageCode) {
		ModelAndView result;
		int categoryId;

		result = new ModelAndView("category/edit");
		categoryId = category.getId();
		if (categoryId == 0)
			categoryId = this.categoryService.findCategoryWithoutFather().getId();
		result.addObject("category", category);
		result.addObject("message", messageCode);
		result.addObject("categoryId", categoryId);

		return result;
	}

}
