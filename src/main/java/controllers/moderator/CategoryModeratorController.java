
package controllers.moderator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CategoryService;
import controllers.AbstractController;
import domain.Category;

@Controller
@RequestMapping("/category/moderator")
public class CategoryModeratorController extends AbstractController {

	// Services
	@Autowired
	private CategoryService	categoryService;


	//Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam(required = false) final Integer fatherCategoryId) {
		ModelAndView result;
		Category category;
		Category fatherCategory;

		fatherCategory = null;

		if (fatherCategoryId != null)
			fatherCategory = this.categoryService.findOne(fatherCategoryId);

		category = this.categoryService.create(fatherCategory);

		result = this.createEditModelAndView(category);

		return result;

	}

	//Re-organising
	@RequestMapping(value = "/reorganising", method = RequestMethod.GET)
	public ModelAndView reorganising(@RequestParam final int categoryNewFatherId, @RequestParam final int categoryToMoveId) {
		ModelAndView result;
		Category categoryNewFather;
		Category categoryToMove;

		if (categoryNewFatherId == 0)
			categoryNewFather = null;
		else {
			categoryNewFather = this.categoryService.findOne(categoryNewFatherId);
			Assert.notNull(categoryNewFather);
		}

		categoryToMove = this.categoryService.findOne(categoryToMoveId);
		Assert.notNull(categoryToMove);

		try {
			this.categoryService.reorganising(categoryToMove, categoryNewFather);

			result = new ModelAndView("redirect:/category/display.do?categoryId=" + categoryToMove.getId());

		} catch (final Throwable oops) {
			result = new ModelAndView("redirect:/category/display.do?error=true&categoryId=" + categoryToMove.getId());

		}

		return result;
	}

	//Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int categoryId) {
		ModelAndView result;
		Category category;

		category = this.categoryService.findOne(categoryId);
		Assert.notNull(category);

		result = this.createEditModelAndView(category);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Category category, final BindingResult binding) {
		ModelAndView result;
		Category saved;

		category = this.categoryService.reconstruct(category, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(category);
		else
			try {
				saved = this.categoryService.save(category);

				result = new ModelAndView("redirect:/category/display.do?categoryId=" + saved.getId());

			} catch (final Throwable oops) {
				result = this.createEditModelAndView(category, "category.commit.error");
			}

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final Category category, final BindingResult binding) {
		ModelAndView result;
		Category categoryToDelete;

		categoryToDelete = category;

		try {
			this.categoryService.delete(category);

			//Redirect a la raiz o al padre dependiendo de donde estaba situada la categoría que ha sido borrada
			if (categoryToDelete != null && categoryToDelete.getFatherCategory() != null)
				result = new ModelAndView("redirect:/category/display.do?categoryId=" + categoryToDelete.getFatherCategory().getId());
			else
				result = new ModelAndView("redirect:/category/display.do?");

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

		if (category.getId() == 0)
			result = new ModelAndView("category/create");
		else
			result = new ModelAndView("category/edit");

		result.addObject("category", category);
		result.addObject("message", messageCode);

		return result;
	}

}
