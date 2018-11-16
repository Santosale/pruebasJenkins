
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EndorserRecordRepository;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.EndorserRecord;

@Service
@Transactional
public class EndorserRecordService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private EndorserRecordRepository	endorserRecordRepository;

	// Supporting services
	@Autowired
	private CurriculumService			curriculumService;

	@Autowired
	private ConfigurationService		configurationService;


	// Constructors -----------------------------------------------------------
	public EndorserRecordService() {
		super();
	}

	// Simple CRUD methods
	// -----------------------------------------------------------

	public EndorserRecord create(final Curriculum curriculum) {
		EndorserRecord result;

		result = new EndorserRecord();
		result.setCurriculum(curriculum);

		return result;
	}

	public Collection<EndorserRecord> findAll() {
		Collection<EndorserRecord> result;

		result = this.endorserRecordRepository.findAll();

		return result;
	}

	public EndorserRecord findOne(final int id) {
		EndorserRecord result;

		Assert.isTrue(id != 0);

		result = this.endorserRecordRepository.findOne(id);

		return result;
	}

	public EndorserRecord save(final EndorserRecord endorserRecord) {
		EndorserRecord result;
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<EndorserRecord> endorserRecords;

		Assert.notNull(endorserRecord, "endorserRecord.not.null");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum, "endorserRecord.curriculum.not.null");
		Assert.isTrue(curriculum.getRanger().getUserAccount().equals(userAccount), "endorserRecord.is.ranger.curriculum");

		endorserRecords = this.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordRepository.countByCurriculumId(curriculum.getId()));

		if (endorserRecord.getId() != 0)
			Assert.isTrue(endorserRecords.contains(endorserRecord), "endorserRecord.this.curriculum");

		result = this.endorserRecordRepository.save(endorserRecord);

		return result;
	}

	public void delete(final EndorserRecord endorserRecord) {
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<EndorserRecord> endorserRecords;

		Assert.notNull(endorserRecord, "endorserRecord.not.null");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum, "endorserRecord.curriculum.not.null");

		endorserRecords = this.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordRepository.countByCurriculumId(curriculum.getId()));

		Assert.isTrue(endorserRecords.contains(endorserRecord), "endorserRecord.this.curriculum");

		this.endorserRecordRepository.delete(endorserRecord);

	}

	// Other business methods

	public Collection<EndorserRecord> findByCurriculumId(final int curriculumId, final Integer page, final Integer size) {
		Collection<EndorserRecord> result;
		Pageable pageable;

		if (page == 0 || size <= 0)
			pageable = new PageRequest(0, 5);
		else
			pageable = new PageRequest(page - 1, size);

		Assert.isTrue(curriculumId != 0);

		result = this.endorserRecordRepository.findByCurriculumId(curriculumId, pageable).getContent();

		return result;
	}

	public Integer countByCurriculumId(final int curriculumId) {
		Integer result;

		Assert.isTrue(curriculumId != 0);

		result = this.endorserRecordRepository.countByCurriculumId(curriculumId);

		return result;
	}

	public boolean checkSpamWords(final EndorserRecord endorserRecord) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = endorserRecord.getComments() != null && endorserRecord.getComments().contains(spamWord) || endorserRecord.getEmail() != null && endorserRecord.getEmail().contains(spamWord) || endorserRecord.getFullName() != null
				&& endorserRecord.getFullName().contains(spamWord) || endorserRecord.getLinkLinkedin() != null && endorserRecord.getLinkLinkedin().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
