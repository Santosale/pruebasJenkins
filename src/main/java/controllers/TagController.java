
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.TagService;
import domain.Tag;

@Controller
@RequestMapping("/tag")
public class TagController extends AbstractController {

	// Services
	@Autowired
	TagService	tagService;


	// Constructor
	public TagController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Tag> tags;

		tags = this.tagService.findAllPaginated(page, 5);
		Assert.notNull(tags);

		result = new ModelAndView("tag/list");
		result.addObject("tags", tags.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", tags.getTotalPages());

		return result;
	}

}
