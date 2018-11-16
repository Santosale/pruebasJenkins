
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.MiscellaneousRecordRepository;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.MiscellaneousRecord;

@Service
@Transactional
public class MiscellaneousRecordService {

	// Managed repository
	@Autowired
	private MiscellaneousRecordRepository	miscellaneousRecordRepository;

	// Supporting services
	@Autowired
	private ConfigurationService			configurationService;

	@Autowired
	private CurriculumService				curriculumService;


	// Constructor
	public MiscellaneousRecordService() {
		super();
	}

	// Simple CRUD methods
	public MiscellaneousRecord create(final Curriculum curriculum) {
		MiscellaneousRecord result;

		result = new MiscellaneousRecord();
		result.setCurriculum(curriculum);

		return result;
	}

	public Collection<MiscellaneousRecord> findAll() {
		Collection<MiscellaneousRecord> result;

		result = this.miscellaneousRecordRepository.findAll();

		return result;
	}

	public MiscellaneousRecord findOne(final int miscellaneousRecordId) {
		MiscellaneousRecord result;

		Assert.isTrue(miscellaneousRecordId != 0);

		result = this.miscellaneousRecordRepository.findOne(miscellaneousRecordId);

		return result;
	}

	public MiscellaneousRecord save(final MiscellaneousRecord miscellaneousRecord) {
		MiscellaneousRecord result;
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<MiscellaneousRecord> miscellaneousRecords;

		Assert.notNull(miscellaneousRecord, "miscellaneousRecord.not.null");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		Assert.notNull(curriculum, "miscellaneousRecord.curriculum.not.null");
		Assert.isTrue(curriculum.getRanger().getUserAccount().equals(userAccount), "miscellaneousRecord.curriculum.ranger");

		miscellaneousRecords = this.findByCurriculumId(curriculum.getId(), 1, this.countByCurriculumId(curriculum.getId()));
		Assert.notNull(miscellaneousRecords, "miscellaneousRecord.count.not.null");
		if (miscellaneousRecord.getId() != 0)
			Assert.isTrue(miscellaneousRecords.contains(miscellaneousRecord), "miscellaneousRecord.contains");

		result = this.miscellaneousRecordRepository.save(miscellaneousRecord);

		return result;
	}

	public void delete(final MiscellaneousRecord miscellaneousRecord) {
		Curriculum curriculum;
		UserAccount userAccount;
		Collection<MiscellaneousRecord> miscellaneousRecords;

		Assert.notNull(miscellaneousRecord, "miscellaneousRecord.not.null");

		userAccount = LoginService.getPrincipal();
		curriculum = this.curriculumService.findByRangerUserAccountId(userAccount.getId());

		// Si es nulo, entonces es que no hay ningun ranger con la sesión iniciada
		Assert.notNull(curriculum, "miscellaneousRecord.curriculum.not.null");

		miscellaneousRecords = this.findByCurriculumId(curriculum.getId(), 1, this.countByCurriculumId(curriculum.getId()));

		Assert.notNull(miscellaneousRecords, "miscellaneousRecord.count.not.null");
		// Un Ranger no puede borrar un record que no es de su curriculum.
		Assert.isTrue(miscellaneousRecords.contains(miscellaneousRecord), "miscellaneousRecord.contains");

		this.miscellaneousRecordRepository.delete(miscellaneousRecord);
	}

	// Other business methods
	//Una curriculum lo pueden ver los no autenticados
	public Collection<MiscellaneousRecord> findByCurriculumId(final int curriculumId, final Integer page, final Integer size) {
		Collection<MiscellaneousRecord> result;
		Pageable pageable;

		Assert.isTrue(curriculumId != 0);

		if (page == 0 || size <= 0)
			pageable = new PageRequest(0, 5);
		else
			pageable = new PageRequest(page - 1, size);

		Assert.isTrue(curriculumId != 0);

		result = this.miscellaneousRecordRepository.findByCurriculumId(curriculumId, pageable).getContent();

		return result;
	}

	public Integer countByCurriculumId(final int curriculumId) {
		Integer result;

		Assert.isTrue(curriculumId != 0);

		result = this.miscellaneousRecordRepository.countByCurriculumId(curriculumId);

		return result;
	}

	public boolean checkSpamWords(final MiscellaneousRecord miscellaneousRecord) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = miscellaneousRecord.getComments() != null && miscellaneousRecord.getComments().toLowerCase().contains(spamWord) || miscellaneousRecord.getLink() != null && miscellaneousRecord.getLink().toLowerCase().contains(spamWord)
				|| miscellaneousRecord.getTitle().toLowerCase().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
