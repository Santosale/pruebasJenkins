
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
import security.UserAccount;
import services.ActorService;
import services.ConfigurationService;
import services.CurriculumService;
import services.EndorserRecordService;
import services.RangerService;
import controllers.AbstractController;
import domain.Curriculum;
import domain.EndorserRecord;

@Controller
@RequestMapping("/endorserRecord/ranger")
public class EndorserRecordRangerController extends AbstractController {

	// Services
	@Autowired
	RangerService			rangerService;

	@Autowired
	CurriculumService		curriculumService;

	@Autowired
	EndorserRecordService	endorserRecordService;

	@Autowired
	ActorService			actorService;

	@Autowired
	ConfigurationService	configurationService;


	// Constructor
	public EndorserRecordRangerController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int curriculumId) {
		ModelAndView result;
		EndorserRecord endorserRecord;
		Curriculum curriculum;

		curriculum = this.curriculumService.findOne(curriculumId);
		Assert.notNull(curriculum);

		endorserRecord = this.endorserRecordService.create(curriculum);

		result = this.createEditModelAndView(endorserRecord);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int endorserRecordId) {
		ModelAndView result;
		EndorserRecord endorserRecord;

		endorserRecord = this.endorserRecordService.findOne(endorserRecordId);
		Assert.notNull(endorserRecord);

		result = this.createEditModelAndView(endorserRecord);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final EndorserRecord endorserRecord, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(endorserRecord);
		else
			try {
				this.endorserRecordService.save(endorserRecord);
				result = new ModelAndView("redirect:/curriculum/display.do?curriculumId=" + endorserRecord.getCurriculum().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(endorserRecord, "endorserRecord.commit.error");
			}

		return result;
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final EndorserRecord endorserRecord, final BindingResult binding) {
		ModelAndView result;
		try {
			this.endorserRecordService.delete(endorserRecord);
			result = new ModelAndView("redirect:/curriculum/display.do?curriculumId=" + endorserRecord.getCurriculum().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(endorserRecord, "endorserRecord.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final EndorserRecord endorserRecord) {
		ModelAndView result;

		result = this.createEditModelAndView(endorserRecord, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final EndorserRecord endorserRecord, final String messageCode) {
		final ModelAndView result;
		UserAccount userAccount;
		boolean isHisEndorserRecord;
		String countryCode;

		countryCode = this.configurationService.findCountryCode();

		isHisEndorserRecord = false;
		userAccount = LoginService.getPrincipal();

		if (endorserRecord.getId() > 0) {
			if (this.curriculumService.findByRangerUserAccountId(userAccount.getId()) != null)
				if (this.endorserRecordService.findByCurriculumId(this.curriculumService.findByRangerUserAccountId(userAccount.getId()).getId(), 1,
					this.endorserRecordService.countByCurriculumId(this.curriculumService.findByRangerUserAccountId(userAccount.getId()).getId())).contains(endorserRecord))
					isHisEndorserRecord = true;
		} else if (this.curriculumService.findByRangerUserAccountId(userAccount.getId()) != null)
			isHisEndorserRecord = true;

		System.out.println(this.curriculumService.findByRangerUserAccountId(userAccount.getId()));
		System.out.println(isHisEndorserRecord);

		if (endorserRecord.getId() > 0)
			result = new ModelAndView("endorserRecord/edit");
		else
			result = new ModelAndView("endorserRecord/create");

		result.addObject("endorserRecord", endorserRecord);
		result.addObject("isHisEndorserRecord", isHisEndorserRecord);
		result.addObject("message", messageCode);
		result.addObject("countryCode", countryCode);

		return result;
	}
}
