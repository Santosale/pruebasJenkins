
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ExplorerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Contact;
import domain.CreditCard;
import domain.Explorer;
import domain.Finder;
import domain.Story;

@Service
@Transactional
public class ExplorerService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private ExplorerRepository		explorerRepository;

	// Supporting
	// services-----------------------------------------------------------
	@Autowired
	private FolderService			folderService;

	@Autowired
	private FinderService			finderService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private StoryService			storyService;

	@Autowired
	private ContactService			contactService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------
	public ExplorerService() {
		super();
	}

	// Simple CRUD
	// methods-----------------------------------------------------------
	public Explorer create() {
		Explorer result;
		UserAccount userAccount;
		Authority authority;

		result = new Explorer();
		userAccount = new UserAccount();
		authority = new Authority();

		result.setSuspicious(false);

		authority.setAuthority("EXPLORER");
		userAccount.addAuthority(authority);
		userAccount.setEnabled(true);

		result.setUserAccount(userAccount);

		return result;
	}

	public Collection<Explorer> findAll() {
		Collection<Explorer> result;

		result = this.explorerRepository.findAll();

		return result;
	}

	public Explorer findOne(final int explorerId) {
		Explorer result;

		Assert.isTrue(explorerId != 0);

		result = this.explorerRepository.findOne(explorerId);

		return result;
	}

	public Explorer save(final Explorer explorer) {
		Explorer result, saved;
		UserAccount logedUserAccount;
		Finder finder;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("EXPLORER");
		Assert.notNull(explorer, "explorer.not.null");

		if (explorer.getId() != 0) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "explorer.notLogged ");
			Assert.isTrue(logedUserAccount.equals(explorer.getUserAccount()), "explorer.notEqual.userAccount");
			saved = this.explorerRepository.findOne(explorer.getId());
			Assert.notNull(saved, "explorer.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(explorer.getUserAccount().getUsername()), "explorer.notEqual.username");
			Assert.isTrue(explorer.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "explorer.notEqual.password");
			Assert.isTrue(explorer.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && explorer.getSuspicious() == saved.getSuspicious(), "explorer.notEqual.accountOrSuspicious");

		} else {
			Assert.isTrue(explorer.getSuspicious() == false, "explorer.notSuspicious.false");
			explorer.getUserAccount().setPassword(encoder.encodePassword(explorer.getUserAccount().getPassword(), null));
			explorer.getUserAccount().setEnabled(true);

		}

		result = this.explorerRepository.save(explorer);

		if (explorer.getId() == 0) {
			finder = this.finderService.create(result);
			this.finderService.save(finder);
			this.folderService.createDefaultFolders(result);
		}

		return result;
	}

	// Other business methods
	public void searchSuspicious() {
		Collection<Explorer> result;
		Authority authority;
		Collection<Explorer> explorers;
		List<String> campos;
		boolean check;
		Collection<String> spamWords;

		// Metodo usable por administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "explorer.authority.administrator");

		campos = new ArrayList<String>();
		result = new ArrayList<Explorer>();
		check = false;
		explorers = this.explorerRepository.findAll();

		spamWords = this.configurationService.findSpamWords();

		for (final Explorer explorer : explorers) {

			for (final Application application : this.applicationService.findByExplorerId(explorer.getId()))
				check = check || this.applicationService.checkSpamWords(application);

			for (final Contact contact : this.contactService.findByExplorerId(explorer.getId()))
				check = check || this.contactService.checkSpamWords(contact);

			for (final Story story : this.storyService.findByExplorerId(explorer.getId()))
				check = check || this.storyService.checkSpamWords(story);

			for (final CreditCard creditCard : this.creditCardService.findByExplorerId(explorer.getId()))
				check = check || this.creditCardService.checkSpamWords(creditCard);

			final Finder finder = this.finderService.findByExplorerId(explorer.getId());
			check = check || this.finderService.checkSpamWords(finder);

			check = check || this.actorService.checkSpamWords(explorer);

			for (final String spamWord : spamWords)
				for (final String campo : campos) {
					check = check || campo != null && campo.contains(spamWord);
					if (check)
						break;
				}

			if (check) {
				explorer.setSuspicious(check);
				this.explorerRepository.save(explorer);
				result.add(explorer);
			}
			check = false;
		}
	}

	public Explorer findByApplicationId(final int applicationId) {
		Explorer result;

		Assert.isTrue(applicationId != 0);

		result = this.explorerRepository.findByApplicationId(applicationId);

		return result;
	}

	public Explorer findByUserAccountId(final int id) {
		Explorer result;

		Assert.isTrue(id != 0);

		result = this.explorerRepository.findByUserAccountId(id);

		return result;
	}

}
