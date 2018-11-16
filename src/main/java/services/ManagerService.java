
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ManagerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Manager;
import domain.Note;
import domain.Stage;
import domain.SurvivalClass;
import domain.TagValue;
import domain.Trip;

@Service
@Transactional
public class ManagerService {

	// Managed repository
	@Autowired
	private ManagerRepository		managerRepository;

	// Supporting services
	@Autowired
	private FolderService			folderService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private NoteService				noteService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private SurvivalClassService	survivalClassService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private StageService			stageService;

	@Autowired
	private TagValueService			tagValueService;

	@Autowired
	private ApplicationService		applicationService;


	// Constructor
	public ManagerService() {
		super();
	}

	// Simple CRUD methods
	public Manager create() {
		Manager result;
		UserAccount userAccount;
		Authority authority;

		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("MANAGER");
		userAccount.getAuthorities().add(authority);
		userAccount.setEnabled(true);

		result = new Manager();

		result.setSuspicious(false);
		result.setUserAccount(userAccount);

		return result;
	}

	public Collection<Manager> findAll() {
		Collection<Manager> result;

		result = this.managerRepository.findAll();

		return result;
	}

	public Manager findOne(final int managerId) {
		Manager result;

		Assert.isTrue(managerId != 0);

		result = this.managerRepository.findOne(managerId);

		return result;
	}

	public Manager save(final Manager manager) {
		Manager result;
		Authority authority;
		UserAccount userAccount;
		//		Collection<Folder> folderSaved;
		Md5PasswordEncoder encoder;
		String plainPassword;
		Manager saved;

		//		folderSaved = new ArrayList<Folder>();
		encoder = new Md5PasswordEncoder();

		// El parámetro manager no puede ser nulo
		Assert.notNull(manager, "manager.not.null");

		// Guardamos el userAccount del usuario que está actualmente logeado.
		userAccount = LoginService.getPrincipal();

		// El administrador crea al manager, pero es solo ese manager quien puede modificarse
		if (manager.getId() == 0) {
			authority = new Authority();
			authority.setAuthority("ADMIN");
			Assert.isTrue(userAccount.getAuthorities().contains(authority), "manager.authority.administrator");
			Assert.isTrue(manager.getSuspicious() == false, "manager.notSuspicious.false");

			plainPassword = manager.getUserAccount().getPassword();
			manager.getUserAccount().setPassword(encoder.encodePassword(plainPassword, null));
			manager.getUserAccount().setEnabled(true);

		} else {
			saved = this.managerRepository.findOne(manager.getId());
			Assert.notNull(saved, "manager.not.null");
			Assert.isTrue(userAccount.equals(manager.getUserAccount()), "manager.notEqual.userAccount");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(manager.getUserAccount().getUsername()), "manager.notEqual.username");
			Assert.isTrue(manager.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "manager.notEqual.password");
			Assert.isTrue(manager.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && manager.getSuspicious() == saved.getSuspicious(), "manager.notEqual.accountOrSuspicious");
		}

		// Persistimos al manager
		result = this.managerRepository.save(manager);

		//Guardamos los folders por defecto cuando creamos el actor
		if (manager.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	// Other business method
	public Double ratioSuspicious() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "manager.authority.administrator");

		result = this.managerRepository.ratioSuspicious();

		return result;
	}

	public Manager findByUserAccountId(final int userAccountId) {
		Manager result;

		Assert.isTrue(userAccountId != 0);

		result = this.managerRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public void searchSuspicious() {
		Collection<Manager> managers;
		Collection<String> spamWords;
		List<String> campos;
		boolean check;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "manager.authority.administrator");

		campos = new ArrayList<String>();
		check = false;
		managers = this.findAll();

		spamWords = this.configurationService.findSpamWords();

		for (final Manager manager : managers) {

			for (final Note n : this.noteService.findByManagerId(manager.getId()))
				campos.add(n.getManagerReply());

			for (final Trip t : this.tripService.findByManagerUserAccountId(manager.getUserAccount().getId())) {
				check = check || this.tripService.checkSpamWords(t);

				for (final Stage s : this.stageService.findByTripId(t.getId()))
					check = check || this.stageService.checkSpamWords(s);

				for (final TagValue tv : this.tagValueService.findByTripId(t.getId()))
					check = check || this.tagValueService.checkSpamWords(tv);
			}

			for (final Application a : this.applicationService.findByManagerId(manager.getId()))
				campos.add(a.getDeniedReason());

			for (final SurvivalClass sc : this.survivalClassService.findByManagerId(manager.getId())) {
				check = check || this.survivalClassService.checkSpamWords(sc);
				campos.add(sc.getLocation().getName());
			}

			check = check || this.actorService.checkSpamWords(manager);

			for (final String spamWord : spamWords)
				for (final String campo : campos) {
					check = check || campo != null && campo.contains(spamWord);
					if (check)
						break;
				}

			manager.setSuspicious(check);
			this.managerRepository.save(manager);

			check = false;
		}

	}

}
