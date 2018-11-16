
package controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import security.LoginService;
import services.ActorService;
import services.ConfigurationService;
import services.CurriculumService;
import services.EndorserRecordService;
import services.RangerService;
import domain.Curriculum;
import domain.EndorserRecord;

@RestController
@RequestMapping("/endorserRecord")
public class EndorserRecordController extends AbstractController {

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
	public EndorserRecordController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@RequestParam final int curriculumId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
		final Curriculum curriculum;
		final Collection<EndorserRecord> endorserRecords;
		Boolean isRangerCurriculum;
		final ResponseEntity<?> result;
		final Map<String, Object> response;
		Integer recordsNumber;

		if (page == null)
			page = 0;

		if (size == null)
			size = 0;

		curriculum = this.curriculumService.findOne(curriculumId);
		Assert.notNull(curriculum);

		recordsNumber = this.endorserRecordService.countByCurriculumId(curriculumId);

		endorserRecords = this.endorserRecordService.findByCurriculumId(curriculumId, page, size);
		Assert.notNull(endorserRecords);
		response = new HashMap<String, Object>();

		//Vemos si esta autenticado el ranger del curriculum para mostrar botones de crear y modificar en la vista
		isRangerCurriculum = false;

		if (LoginService.isAuthenticated() && curriculum.getRanger().getUserAccount().getId() == LoginService.getPrincipal().getId())
			isRangerCurriculum = true;

		response.put("endorserRecords", endorserRecords);
		response.put("isRangerCurriculum", isRangerCurriculum);
		response.put("endorserNumber", recordsNumber);
		response.put("sizeEndorser", size);
		response.put("pageEndorser", page);

		result = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		return result;
	}

}
