
package services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EducationRecordRepository;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.EducationRecord;

@Service
@Transactional
public class EducationRecordService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private EducationRecordRepository	educationRecordRepository;

	// Supporting services
	@Autowired
	private CurriculumService			curriculumService;

	@Autowired
	private ConfigurationService		configurationService;


	// Constructors -----------------------------------------------------------
	public EducationRecordService() {
		super();
	}

	// Simple CRUD methods
	// -----------------------------------------------------------

	public EducationRecord create(final Curriculum curriculum) {
		EducationRecord result;

		result = new EducationRecord();
		result.setCurriculum(curriculum);

		return result;
	}

	public Collection<EducationRecord> findAll() {
		Collection<EducationRecord> result;

		result = this.educationRecordRepository.findAll();

		return result;
	}

	public EducationRecord findOne(final int id) {
		EducationRecord result;

		Assert.isTrue(id != 0);

		result = this.educationRecordRepository.findOne(id);

		return result;
	}

	public EducationRecord save(final EducationRecord educationRecord) {
		EducationRecord result;
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<EducationRecord> educationRecords;
		Date currentMoment;

		Assert.notNull(educationRecord);

		currentMoment = new Date(System.currentTimeMillis() + 1);

		if (educationRecord.getFinishedStudyDate() != null)
			Assert.isTrue(educationRecord.getStartedStudyDate().compareTo(educationRecord.getFinishedStudyDate()) < 0);

		Assert.isTrue(educationRecord.getStartedStudyDate().compareTo(currentMoment) < 0);

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum);
		Assert.isTrue(curriculum.getRanger().getUserAccount().equals(userAccount));

		educationRecords = this.findByCurriculumId(curriculum.getId(), 1, this.educationRecordRepository.countByCurriculumId(curriculum.getId()));

		if (educationRecord.getId() != 0)
			Assert.isTrue(educationRecords.contains(educationRecord));

		result = this.educationRecordRepository.save(educationRecord);

		return result;
	}

	public void delete(final EducationRecord educationRecord) {
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<EducationRecord> educationRecords;

		Assert.notNull(educationRecord);

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum);

		educationRecords = this.findByCurriculumId(curriculum.getId(), 1, this.educationRecordRepository.countByCurriculumId(curriculum.getId()));

		Assert.isTrue(educationRecords.contains(educationRecord));

		this.educationRecordRepository.delete(educationRecord);

	}

	// Other business methods

	public Collection<EducationRecord> findByCurriculumId(final int curriculumId, final Integer page, final Integer size) {
		Collection<EducationRecord> result;
		Pageable pageable;

		if (page == 0 || size <= 0)
			pageable = new PageRequest(0, 5);
		else
			pageable = new PageRequest(page - 1, size);

		Assert.isTrue(curriculumId != 0);

		result = this.educationRecordRepository.findByCurriculumId(curriculumId, pageable).getContent();

		return result;
	}

	public Integer countByCurriculumId(final int curriculumId) {
		Integer result;

		Assert.isTrue(curriculumId != 0);

		result = this.educationRecordRepository.countByCurriculumId(curriculumId);

		return result;
	}

	public boolean checkSpamWords(final EducationRecord educationRecord) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = educationRecord.getComments() != null && educationRecord.getComments().contains(spamWord) || educationRecord.getInstitution() != null && educationRecord.getInstitution().contains(spamWord) || educationRecord.getLink() != null
				&& educationRecord.getLink().contains(spamWord) || educationRecord.getTitle() != null && educationRecord.getTitle().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
