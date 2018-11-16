
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import services.CurriculumService;
import domain.Curriculum;

@Controller
@RequestMapping("/curriculum")
public class CurriculumController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private CurriculumService	curriculumService;


	//Constructors ---------------------------
	public CurriculumController() {
		super();
	}

	// Display ----------------------------------------------------------------
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam(required = false) final Integer curriculumId) {
		ModelAndView result;
		Curriculum curriculum;
		Authority authority;
		boolean isRangerCurriculum;

		authority = new Authority();
		authority.setAuthority("RANGER");

		if (curriculumId == null) {
			//Si es null, debe ser porque un ranger no tiene e irá a crearlo.
			curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
			isRangerCurriculum = true;
		} else {
			//Si un usuario hace un display, si no se encuentra salta assert
			curriculum = this.curriculumService.findOne(curriculumId);
			Assert.notNull(curriculum);
			isRangerCurriculum = false;
		}

		result = new ModelAndView("curriculum/display");
		result.addObject("curriculum", curriculum);
		result.addObject("isRangerCurriculum", isRangerCurriculum);

		return result;
	}

}
