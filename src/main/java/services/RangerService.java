
package services;

// import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.RangerRepository;
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
public class RangerService {

	// Managed repository
	@Autowired
	private RangerRepository			rangerRepository;

	// Supporting services
	@Autowired
	private FolderService				folderService;

	@Autowired
	private CurriculumService			curriculumService;

	@Autowired
	private ActorService				actorService;

	@Autowired
	private EducationRecordService		educationRecordService;

	@Autowired
	private EndorserRecordService		endorserRecordService;

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;

	@Autowired
	private ProfessionalRecordService	professionalRecordService;


	// Constructor
	public RangerService() {
		super();
	}

	// Simple CRUD methods
	public Ranger create() {
		Ranger result;
		UserAccount userAccount;
		Authority authority;

		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("RANGER");
		userAccount.getAuthorities().add(authority);
		userAccount.setEnabled(true);

		result = new Ranger();

		result.setSuspicious(false);
		result.setUserAccount(userAccount);

		return result;
	}

	public Collection<Ranger> findAll() {
		Collection<Ranger> result;

		result = this.rangerRepository.findAll();

		return result;
	}

	public Ranger findOne(final int rangerId) {
		Ranger result;

		Assert.isTrue(rangerId != 0);

		result = this.rangerRepository.findOne(rangerId);

		return result;
	}

	public Ranger save(final Ranger ranger) {
		Ranger result;
		UserAccount userAccount;
		Md5PasswordEncoder encoder;
		String plainPassword;
		Ranger saved;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		encoder = new Md5PasswordEncoder();
		Assert.notNull(ranger, "ranger.not.null");

		// El administrador o un usuario no logeado crea el ranger
		if (ranger.getId() == 0) {
			if (LoginService.isAuthenticated() == true)
				Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "ranger.authority.administratorOrAnonimous");
			plainPassword = ranger.getUserAccount().getPassword();
			ranger.getUserAccount().setPassword(encoder.encodePassword(plainPassword, null));
			ranger.getUserAccount().setEnabled(true);
			Assert.isTrue(ranger.getSuspicious() == false, "ranger.notSuspicious.false");

		} else {
			userAccount = LoginService.getPrincipal();
			// Aquí no solo se comprueba que el ranger solo se puede modificar así mismo, sino que el userAccount
			// no puede ser modificado, es decir, el usuario y la contraseña no se pueden cambiar.
			saved = this.rangerRepository.findOne(ranger.getId());
			Assert.notNull(saved, "ranger.not.null");
			Assert.isTrue(userAccount.equals(ranger.getUserAccount()), "ranger.notEqual.userAccount");
			Assert.isTrue(ranger.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && ranger.getSuspicious() == saved.getSuspicious(), "ranger.notEqual.accountOrSuspicious");
		}
		result = this.rangerRepository.save(ranger);
		//Guardamos los folders por defecto cuando creamos el actor
		if (ranger.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	// Other business method

	public Double ratioSuspicious() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "ranger.authority.administrator");

		result = this.rangerRepository.ratioSuspicious();

		return result;
	}

	public Double ratioRangersRegisteredCurriculum() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "ranger.authority.administrator");

		result = this.rangerRepository.ratioRangersRegisteredCurriculum();

		return result;
	}

	public Double ratioEndorsedCurriculum() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "ranger.authority.administrator");

		result = this.rangerRepository.ratioEndorsedCurriculum();

		return result;
	}

	public Ranger findByCurriculumId(final int curriculumId) {
		Ranger result;

		Assert.isTrue(curriculumId != 0);

		result = this.rangerRepository.findByCurriculumId(curriculumId);

		return result;
	}

	public Ranger findByUserAccountId(final int userAccountId) {
		Ranger result;

		Assert.isTrue(userAccountId != 0);

		result = this.rangerRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public Ranger findByTripId(final int tripId) {
		Ranger result;

		Assert.isTrue(tripId != 0);

		result = this.rangerRepository.findByTripId(tripId);

		return result;
	}

	public void searchSuspicious() {
		Collection<Ranger> rangers;
		Curriculum curriculum;
		boolean check;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "ranger.authority.administrator");

		check = false;
		rangers = this.findAll();

		for (final Ranger ranger : rangers) {

			curriculum = this.curriculumService.findByRangerUserAccountId(ranger.getUserAccount().getId());
			check = curriculum != null && this.curriculumService.checkSpamWords(curriculum);

			if (curriculum != null) {
				for (final EducationRecord er : this.educationRecordService.findByCurriculumId(curriculum.getId(), 1, this.educationRecordService.countByCurriculumId(curriculum.getId())))

					check = check || this.educationRecordService.checkSpamWords(er);

				for (final EndorserRecord er : this.endorserRecordService.findByCurriculumId(curriculum.getId(), 1, this.endorserRecordService.countByCurriculumId(curriculum.getId())))
					check = check || this.endorserRecordService.checkSpamWords(er);

				for (final MiscellaneousRecord mr : this.miscellaneousRecordService.findByCurriculumId(curriculum.getId(), 1, this.miscellaneousRecordService.countByCurriculumId(curriculum.getId())))
					check = check || this.miscellaneousRecordService.checkSpamWords(mr);

				for (final ProfessionalRecord pr : this.professionalRecordService.findByCurriculumId(curriculum.getId(), 1, this.professionalRecordService.countByCurriculumId(curriculum.getId())))
					check = check || this.professionalRecordService.checkSpamWords(pr);
			}

			check = check || this.actorService.checkSpamWords(ranger);

			ranger.setSuspicious(check);
			this.rangerRepository.save(ranger);

			check = false;

		}

	}

}
