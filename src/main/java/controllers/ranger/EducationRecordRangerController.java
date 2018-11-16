
package controllers.ranger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.CurriculumService;
import services.EducationRecordService;
import controllers.AbstractController;
import domain.Curriculum;
import domain.EducationRecord;

@Controller
@RequestMapping(value = "/educationrecord/ranger")
public class EducationRecordRangerController extends AbstractController {

	// Service
	@Autowired
	private EducationRecordService	educationRecordService;

	@Autowired
	private CurriculumService		curriculumService;


	// Constructor
	public EducationRecordRangerController() {
		super();
	}

	// Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int curriculumId) {
		ModelAndView result;
		EducationRecord educationRecord;
		Curriculum curriculum;

		curriculum = this.curriculumService.findOne(curriculumId);
		Assert.notNull(curriculum);

		educationRecord = this.educationRecordService.create(curriculum);
		Assert.notNull(educationRecord);

		result = this.createEditModelAndView(educationRecord);
		result.addObject("curriculumId", curriculumId);

		return result;
	}

	// Edit
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int recordId) {
		ModelAndView result;
		EducationRecord educationRecord;
		Curriculum curriculum;

		educationRecord = this.educationRecordService.findOne(recordId);
		Assert.notNull(educationRecord);

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(curriculum);

		result = this.createEditModelAndView(educationRecord);
		result.addObject("curriculumId", curriculum.getId());
		return result;
	}

	// Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final EducationRecord educationRecord, final BindingResult binding) {
		ModelAndView result;
		Curriculum curriculum;

		curriculum = this.curriculumService.findByRangerUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(curriculum);

		if (binding.hasErrors())
			result = this.createEditModelAndView(educationRecord);
		else
			try {
				this.educationRecordService.save(educationRecord);
				result = new ModelAndView("redirect:/curriculum/display.do?curriculumId=" + educationRecord.getCurriculum().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(educationRecord, "educationrecord.commit.error");
			}

		result.addObject("curriculumId", curriculum.getId());

		return result;
	}

	// Delete
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final EducationRecord educationRecord, final BindingResult binding) {
		ModelAndView result;

		try {
			this.educationRecordService.delete(educationRecord);
			result = new ModelAndView("redirect:/curriculum/display.do?curriculumId=" + educationRecord.getCurriculum().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(educationRecord, "educationrecord.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final EducationRecord educationRecord) {
		ModelAndView result;

		result = this.createEditModelAndView(educationRecord, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final EducationRecord educationRecord, final String messageCode) {
		ModelAndView result;

		if (educationRecord.getId() > 0)
			result = new ModelAndView("educationrecord/edit");
		else
			result = new ModelAndView("educationrecord/create");

		result.addObject("educationRecord", educationRecord);
		result.addObject("message", messageCode);

		return result;
	}
}
