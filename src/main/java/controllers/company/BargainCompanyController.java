
package controllers.company;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BargainService;
import services.CategoryService;
import services.TagService;
import controllers.AbstractController;
import domain.Bargain;
import domain.Category;
import forms.BargainForm;

@Controller
@RequestMapping(value = "/bargain/company")
public class BargainCompanyController extends AbstractController {

	// Services
	@Autowired
	private BargainService	bargainService;

	@Autowired
	private TagService		tagService;

	@Autowired
	private CategoryService	categoryService;


	// Constructor
	public BargainCompanyController() {
		super();
	}

	@RequestMapping(value = "/addCategory", method = RequestMethod.GET)
	public ModelAndView addCategory(@RequestParam final int bargainId, @RequestParam final int categoryId) {
		ModelAndView result;
		Bargain bargain;
		Category category;

		Assert.isTrue(bargainId != 0);
		Assert.isTrue(categoryId != 0);

		bargain = this.bargainService.findOne(bargainId);
		category = this.categoryService.findOne(categoryId);

		this.categoryService.addBargain(bargain, category);

		result = new ModelAndView("redirect:/bargain/display.do?bargainId=" + bargainId);

		return result;

	}

	@RequestMapping(value = "/removeCategory", method = RequestMethod.GET)
	public ModelAndView removeCategory(@RequestParam final int bargainId, @RequestParam final int categoryId) {
		ModelAndView result;
		Bargain bargain;
		Category category;

		Assert.isTrue(bargainId != 0);
		Assert.isTrue(categoryId != 0);

		bargain = this.bargainService.findOne(bargainId);
		category = this.categoryService.findOne(categoryId);

		this.categoryService.removeBargain(bargain, category);

		result = new ModelAndView("redirect:/bargain/display.do?bargainId=" + bargainId);

		return result;

	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Bargain> bargainPage;

		bargainPage = this.bargainService.findByCompanyId(page, 5);
		Assert.notNull(bargainPage);

		result = new ModelAndView("bargain/list");
		result.addObject("bargains", bargainPage.getContent());
		result.addObject("pageNumber", bargainPage.getTotalPages());
		result.addObject("page", page);

		result.addObject("requestURI", "bargain/company/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int categoryId) {
		ModelAndView result;
		Bargain bargain;
		BargainForm bargainForm;

		bargain = this.bargainService.create();
		Assert.notNull(bargain);

		bargainForm = new BargainForm();
		bargainForm.setCategoryId(categoryId);
		bargainForm.setBargain(bargain);
		bargainForm.setTagsName(new ArrayList<String>());

		result = this.createEditModelAndView(bargainForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int bargainId) {
		ModelAndView result;
		Bargain bargain;
		BargainForm bargainForm;

		bargain = this.bargainService.findOneToEdit(bargainId);
		Assert.notNull(bargain);

		bargainForm = new BargainForm();
		bargainForm.setBargain(bargain);
		bargainForm.setTagsName(new ArrayList<String>(this.tagService.findNames(bargain.getId())));

		result = this.createEditModelAndView(bargainForm);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final BargainForm bargainForm, final BindingResult binding) {
		ModelAndView result;
		BargainForm bargainFormReconstruct;

		result = null;

		bargainFormReconstruct = this.bargainService.reconstruct(bargainForm, binding);

		if (binding.hasErrors())
			result = this.createEditModelAndView(bargainForm);
		else
			try {
				this.bargainService.save(bargainFormReconstruct.getBargain(), new ArrayList<String>(bargainForm.getTagsName()), bargainForm.getCategoryId());

				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {

				result = this.createEditModelAndView(bargainForm, "bargain.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final BargainForm bargainForm, final BindingResult binding) {
		ModelAndView result;

		result = null;

		try {
			this.bargainService.delete(bargainForm.getBargain());

			result = new ModelAndView("redirect:list.do");
		} catch (final Throwable oops) {

			result = this.createEditModelAndView(bargainForm, "bargain.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final BargainForm bargainForm) {
		ModelAndView result;

		result = this.createEditModelAndView(bargainForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final BargainForm bargainForm, final String messageCode) {
		ModelAndView result;
		Boolean notPublish;
		Bargain bargain;

		if (bargainForm.getBargain().getId() == 0) {
			result = new ModelAndView("bargain/create");
			notPublish = false;
		} else {
			result = new ModelAndView("bargain/edit");
			bargain = this.bargainService.findOne(bargainForm.getBargain().getId());
			Assert.notNull(bargain);
			notPublish = bargain.getIsPublished();
		}

		result.addObject("bargainForm", bargainForm);
		result.addObject("message", messageCode);
		result.addObject("notPublish", notPublish);

		return result;
	}

}
