
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.LegalTextService;
import domain.LegalText;

@Controller
@RequestMapping("/legalText")
public class LegalTextController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private LegalTextService	legalTextService;


	//Constructors ---------------------------
	public LegalTextController() {
		super();
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int legalTextId) {
		ModelAndView result;
		LegalText legalText;

		legalText = this.legalTextService.findOne(legalTextId);
		Assert.notNull(legalText);
		result = new ModelAndView("legalText/display");
		result.addObject("legalText", legalText);

		return result;
	}

}
