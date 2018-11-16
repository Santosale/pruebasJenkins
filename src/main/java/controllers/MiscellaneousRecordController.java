
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
import services.CurriculumService;
import services.MiscellaneousRecordService;
import domain.Curriculum;
import domain.MiscellaneousRecord;

@RestController
@RequestMapping("/miscellaneousRecord")
public class MiscellaneousRecordController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;

	@Autowired
	private CurriculumService			curriculumService;


	// Constructors -----------------------------------------------------------

	public MiscellaneousRecordController() {
		super();
	}

	// Listing ----------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@RequestParam final int curriculumId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
		final Curriculum curriculum;
		Collection<MiscellaneousRecord> miscellaneousRecords;
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

		recordsNumber = this.miscellaneousRecordService.countByCurriculumId(curriculumId);

		miscellaneousRecords = this.miscellaneousRecordService.findByCurriculumId(curriculumId, page, size);
		Assert.notNull(miscellaneousRecords);
		response = new HashMap<String, Object>();

		//Vemos si esta autenticado el ranger del curriculum para mostrar botones de crear y modificar en la vista
		isRangerCurriculum = false;

		if (LoginService.isAuthenticated() && LoginService.getPrincipal().equals(curriculum.getRanger().getUserAccount()))
			isRangerCurriculum = true;

		response.put("miscellaneousRecords", miscellaneousRecords);
		response.put("isRangerCurriculum", isRangerCurriculum);
		response.put("miscellaneousNumber", recordsNumber);
		response.put("sizeMiscellaneous", size);
		response.put("pageMiscellaneous", page);

		result = new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

		return result;
	}

}
