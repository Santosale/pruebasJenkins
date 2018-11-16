
package services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ProfessionalRecordRepository;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.ProfessionalRecord;

@Service
@Transactional
public class ProfessionalRecordService {

	// Repository
	@Autowired
	private ProfessionalRecordRepository	professionalRecordRepository;

	// Supporting services
	@Autowired
	private ConfigurationService			configurationService;

	@Autowired
	private CurriculumService				curriculumService;


	// Constructor
	public ProfessionalRecordService() {
		super();
	}

	// Simple CRUD methods
	public ProfessionalRecord create(final Curriculum curriculum) {
		ProfessionalRecord result;

		result = new ProfessionalRecord();
		result.setCurriculum(curriculum);

		return result;
	}

	public Collection<ProfessionalRecord> findAll() {
		Collection<ProfessionalRecord> result;

		result = this.professionalRecordRepository.findAll();

		return result;
	}

	public ProfessionalRecord findOne(final int professionalRecordId) {
		ProfessionalRecord result;

		Assert.isTrue(professionalRecordId != 0);

		result = this.professionalRecordRepository.findOne(professionalRecordId);

		return result;
	}

	public ProfessionalRecord save(final ProfessionalRecord professionalRecord) {
		ProfessionalRecord result;
		UserAccount userAccount;
		Curriculum curriculum;
		Date currentMoment;
		Collection<ProfessionalRecord> professionalRecords;

		Assert.notNull(professionalRecord, "professionalRecord.not.null");

		currentMoment = new Date(System.currentTimeMillis() + 1);

		if (professionalRecord.getFinishedWorkDate() != null)
			Assert.isTrue(professionalRecord.getFinishedWorkDate().compareTo(professionalRecord.getStartedWorkDate()) > 0, "professionalRecord.startDate.before.endDate");

		Assert.isTrue(professionalRecord.getStartedWorkDate().compareTo(currentMoment) < 0, "professionalRecord.startDate.past");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum, "professionalRecord.curriculum.not.null");
		Assert.isTrue(curriculum.getRanger().getUserAccount().equals(userAccount), "professionalRecord.is.ranger.curriculum");

		professionalRecords = this.findByCurriculumId(curriculum.getId(), 1, this.countByCurriculumId(curriculum.getId()));
		Assert.notNull(professionalRecords, "professionalRecord.not.null");
		if (professionalRecord.getId() != 0)
			Assert.isTrue(professionalRecords.contains(professionalRecord), "professionalRecord.this.curriculum");

		result = this.professionalRecordRepository.save(professionalRecord);

		return result;
	}

	public void delete(final ProfessionalRecord professionalRecord) {
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<ProfessionalRecord> professionalRecords;

		Assert.notNull(professionalRecord, "professionalRecord.not.null");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		// Si es nulo, entonces es que no hay ningun ranger con la sesión iniciada
		Assert.notNull(curriculum, "professionalRecord.curriculum.not.null");

		professionalRecords = this.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordRepository.countByCurriculumId(curriculum.getId()));
		Assert.notNull(professionalRecords, "professionalRecord.not.null");
		// Un Ranger no puede borrar un record que no es de su curriculum.
		Assert.isTrue(professionalRecords.contains(professionalRecord), "professionalRecord.this.curriculum");

		this.professionalRecordRepository.delete(professionalRecord);

	}
	// Other business methods
	//Una curriculum lo pueden ver los no autenticados
	public Collection<ProfessionalRecord> findByCurriculumId(final int curriculumId, final Integer page, final Integer size) {
		Collection<ProfessionalRecord> result;
		Pageable pageable;

		if (page == 0 || size <= 0)
			pageable = new PageRequest(0, 5);
		else
			pageable = new PageRequest(page - 1, size);

		Assert.isTrue(curriculumId != 0);

		result = this.professionalRecordRepository.findByCurriculumId(curriculumId, pageable).getContent();

		return result;
	}

	public Integer countByCurriculumId(final int curriculumId) {
		Integer result;

		Assert.isTrue(curriculumId != 0);

		result = this.professionalRecordRepository.countByCurriculumId(curriculumId);

		return result;
	}

	public boolean checkSpamWords(final ProfessionalRecord professionalRecord) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = professionalRecord.getComments() != null && professionalRecord.getComments().toLowerCase().contains(spamWord) || professionalRecord.getCompanyName().toLowerCase().contains(spamWord) || professionalRecord.getLink() != null
				&& professionalRecord.getLink().toLowerCase().contains(spamWord) || professionalRecord.getRole().toLowerCase().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
