
package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import security.LoginService;
import services.CurriculumService;
import services.ProfessionalRecordService;
import domain.Curriculum;
import domain.ProfessionalRecord;

@RestController
@RequestMapping("/professionalRecord")
public class ProfessionalRecordController extends AbstractController {

	//Services ------------------------------

	@Autowired
	private ProfessionalRecordService	professionalRecordService;

	@Autowired
	private CurriculumService			curriculumService;


	//Constructors ---------------------------
	public ProfessionalRecordController() {
		super();
	}

	//Listing --------------------------------
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@RequestParam final int curriculumId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
		final Curriculum curriculum;
		Collection<ProfessionalRecord> professionalRecords;
		Boolean isRangerCurriculum;
		final ResponseEntity<?> result;
		final Map<String, Object> response;
		Integer recordsNumber;
		Locale locale;

		locale = LocaleContextHolder.getLocale();

		if (page == null)
			page = 0;

		if (size == null)
			size = 0;

		curriculum = this.curriculumService.findOne(curriculumId);
		Assert.notNull(curriculum);

		recordsNumber = this.professionalRecordService.countByCurriculumId(curriculumId);

		professionalRecords = this.professionalRecordService.findByCurriculumId(curriculumId, page, size);
		Assert.notNull(professionalRecords);
		response = new HashMap<String, Object>();

		//Vemos si esta autenticado el ranger del curriculum para mostrar botones de crear y modificar en la vista
		isRangerCurriculum = false;

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().equals(curriculum.getRanger().getUserAccount()))
			isRangerCurriculum = true;

		response.put("professionalRecords", new ArrayList<ProfessionalRecord>(professionalRecords));
		response.put("isRangerCurriculum", isRangerCurriculum);
		response.put("recordsNumber", recordsNumber);
		response.put("size", size);
		response.put("page", page);
		response.put("locale", locale.toLanguageTag());

		result = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		return result;
	}
}
