
package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CurriculumRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.EducationRecord;
import domain.EndorserRecord;
import domain.MiscellaneousRecord;
import domain.ProfessionalRecord;
import domain.Ranger;

@Service
@Transactional
public class CurriculumService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private CurriculumRepository		curriculumRepository;

	// Supporting services
	@Autowired
	private ConfigurationService		configurationService;

	@Autowired
	private RangerService				rangerService;

	@Autowired
	private EducationRecordService		educationRecordService;

	@Autowired
	private EndorserRecordService		endorserRecordService;

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;

	@Autowired
	private ProfessionalRecordService	professionalRecordService;


	// Constructors -----------------------------------------------------------
	public CurriculumService() {
		super();
	}

	// Simple CRUD methods
	// -----------------------------------------------------------

	public Curriculum create(final Ranger ranger) {
		Curriculum result;

		result = new Curriculum();

		result.setTicker("000000-AAAA");
		result.setRanger(ranger);

		return result;
	}

	public Collection<Curriculum> findAll() {
		Collection<Curriculum> result;

		result = this.curriculumRepository.findAll();

		return result;
	}

	public Curriculum findOne(final int curriculumId) {
		Curriculum result;

		Assert.isTrue(curriculumId != 0);

		result = this.curriculumRepository.findOne(curriculumId);

		return result;
	}

	public Curriculum save(final Curriculum curriculum) {
		Curriculum result;
		Authority authority;
		Ranger ranger;
		UserAccount userAccount;
		Curriculum saved;

		Assert.notNull(curriculum, "curriculum.not.null");

		userAccount = LoginService.getPrincipal();

		Assert.notNull(userAccount, "curriculum.user.log.in");

		ranger = this.rangerService.findByUserAccountId(userAccount.getId());

		Assert.notNull(ranger, "curriculum.is.ranger");

		Assert.isTrue(ranger.equals(curriculum.getRanger()), "curriculum.ranger.curriculum");

		// Comprobar que solo pueda ser creado por un Explorer y aÃ±adirlo
		// con un set al curriculum.
		// Mirar en Ranger si la curriculum es la misma que se crea
		// Mirar en Save que sea el ranger
		// Un ranger solo puede tener una curriculum
		// Si modifica la curriculum, debe ser el ranger concreto

		authority = new Authority();
		authority.setAuthority("RANGER");
		Assert.isTrue(userAccount.getAuthorities().contains(authority), "curriculum.is.ranger");

		// No puede repetirse el ticker
		if (curriculum.getId() == 0)
			curriculum.setTicker("000000-AAAA");

		if (curriculum.getId() != 0) {
			saved = this.findOne(curriculum.getId());

			Assert.isTrue(curriculum.getRanger().equals(saved.getRanger()), "curriculum.ranger.saved");
			Assert.isTrue(curriculum.getTicker().equals(saved.getTicker()), "curriculum.same.ticker");
		} else
			Assert.isNull(this.findByRangerUserAccountId(LoginService.getPrincipal().getId()), "curriculum.create.not.ranger.curriculum");

		result = this.curriculumRepository.save(curriculum);

		if (curriculum.getId() == 0)
			result.setTicker(this.createTicker(result.getId()));

		return result;
	}

	public void delete(final Curriculum curriculum) {
		UserAccount userAccount;
		Ranger ranger;
		List<EducationRecord> educationRecords;
		List<EndorserRecord> endorserRecords;
		List<MiscellaneousRecord> miscellaneousRecords;
		List<ProfessionalRecord> professionalRecords;

		Assert.notNull(curriculum, "curriculum.not.null");

		userAccount = LoginService.getPrincipal();

		Assert.notNull(userAccount, "curriculum.user.log.in");

		ranger = this.rangerService.findByUserAccountId(userAccount.getId());

		Assert.notNull(ranger, "curriculum.is.ranger");

		educationRecords = new ArrayList<EducationRecord>(this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId())));
		endorserRecords = new ArrayList<EndorserRecord>(this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId())));
		miscellaneousRecords = new ArrayList<MiscellaneousRecord>(this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId())));
		professionalRecords = new ArrayList<ProfessionalRecord>(this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId())));

		if (educationRecords != null && !educationRecords.isEmpty())
			for (final EducationRecord eduR : educationRecords)
				this.educationRecordService.delete(eduR);

		if (endorserRecords != null && !endorserRecords.isEmpty())
			for (final EndorserRecord endR : endorserRecords)
				this.endorserRecordService.delete(endR);

		if (miscellaneousRecords != null && !miscellaneousRecords.isEmpty())
			for (final MiscellaneousRecord misR : miscellaneousRecords)
				this.miscellaneousRecordService.delete(misR);

		if (professionalRecords != null && !professionalRecords.isEmpty())
			for (final ProfessionalRecord proR : professionalRecords)
				this.professionalRecordService.delete(proR);

		this.curriculumRepository.delete(curriculum);

	}

	// Other business methods
	public Curriculum findByRangerUserAccountId(final int userAccountId) {
		Curriculum result;

		Assert.isTrue(userAccountId != 0);

		result = this.curriculumRepository.findByRangerUserAccountId(userAccountId);

		return result;
	}

	public boolean checkSpamWords(final Curriculum curriculum) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = curriculum.getEmailPR() != null && curriculum.getEmailPR().contains(spamWord) || curriculum.getFullNamePR() != null && curriculum.getFullNamePR().contains(spamWord) || curriculum.getLinkedinPR() != null
				&& curriculum.getLinkedinPR().contains(spamWord) || curriculum.getPhotoPR() != null && curriculum.getPhotoPR().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

	private String createTicker(int curriculumId) {
		String result = "";
		Calendar calendar;
		String day;
		String month;
		String year;

		calendar = Calendar.getInstance();
		// Calculamos año
		year = Integer.toString(calendar.get(Calendar.YEAR));
		year = year.substring(2, 4);

		// Calculamos mes
		month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;

		// Calculamos el dia
		day = Integer.toString(calendar.get(Calendar.DATE));
		if (day.length() == 1)
			day = "0" + day;

		result += year + month + day;
		result += "-";
		final char[] chr = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'Ñ', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
		};

		curriculumId %= Math.pow(chr.length, 4);

		for (int i = 0; i <= 3; i++) {
			result += chr[curriculumId % chr.length];
			curriculumId = (curriculumId - curriculumId % chr.length) / chr.length;
		}

		return result;
	}
}
