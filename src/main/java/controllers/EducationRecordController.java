
package controllers;

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
import services.EducationRecordService;
import domain.Curriculum;
import domain.EducationRecord;

@RestController
@RequestMapping(value = "/educationrecord")
public class EducationRecordController extends AbstractController {

	// Service
	@Autowired
	private EducationRecordService	educationRecordService;

	@Autowired
	private CurriculumService		curriculumService;


	// Constructor
	public EducationRecordController() {
		super();
	}

	// List
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@RequestParam final int curriculumId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
		Curriculum curriculum;
		Collection<EducationRecord> educationRecords;
		boolean isRangerCurriculum;
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

		recordsNumber = this.educationRecordService.countByCurriculumId(curriculumId);

		educationRecords = this.educationRecordService.findByCurriculumId(curriculumId, page, size);
		Assert.notNull(educationRecords);
		response = new HashMap<String, Object>();

		//Vemos si esta autenticado el ranger del curriculum para mostrar botones de crear y modificar en la vista
		isRangerCurriculum = LoginService.isAuthenticated() && curriculum.getRanger().getUserAccount().getId() == LoginService.getPrincipal().getId();

		response.put("educationrecords", educationRecords);
		response.put("educationNumber", recordsNumber);
		response.put("isRangerCurriculum", isRangerCurriculum);
		response.put("sizeEducation", size);
		response.put("pageEducation", page);
		response.put("locale", locale.toLanguageTag());

		result = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		return result;
	}

}
