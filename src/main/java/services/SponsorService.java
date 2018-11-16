
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SponsorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Sponsor;
import domain.Sponsorship;

@Service
@Transactional
public class SponsorService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SponsorRepository		sponsorRepository;

	// Supporting services ----------------------------------------------------
	@Autowired
	private FolderService			folderService;

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private CreditCardService		creditCardService;


	// Constructors -----------------------------------------------------------
	public SponsorService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Sponsor create() {
		Sponsor result;
		List<Authority> authorities;
		UserAccount userAccount;
		Authority authority;

		userAccount = new UserAccount();
		authority = new Authority();
		authority.setAuthority("SPONSOR");
		authorities = new ArrayList<Authority>();
		authorities.add(authority);
		userAccount.setAuthorities(authorities);
		userAccount.setEnabled(true);

		result = new Sponsor();
		result.setSuspicious(false);
		result.setUserAccount(userAccount);

		return result;
	}

	public Sponsor findOne(final int sponsorId) {
		Sponsor result;

		Assert.isTrue(sponsorId != 0);
		result = this.sponsorRepository.findOne(sponsorId);

		return result;
	}

	public Collection<Sponsor> findAll() {
		Collection<Sponsor> result;

		result = this.sponsorRepository.findAll();

		return result;
	}

	public Sponsor save(final Sponsor sponsor) {
		Sponsor result;
		UserAccount userAccount;
		Sponsor saved;
		//		Collection<Folder> folderSaved;
		Authority authority;
		Md5PasswordEncoder encoder;

		//		folderSaved = new ArrayList<Folder>();
		encoder = new Md5PasswordEncoder();

		Assert.notNull(sponsor, "sponsor.not.null");

		//Vemos si se esta modificando
		if (sponsor.getId() != 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount, "sponsor.notLogged");
			//Solamente puede hacerlo el propio sponsor
			Assert.isTrue(sponsor.getUserAccount().equals(userAccount), "sponsor.notEqual.userAccount");
			//No puede cambiar las propiedades ban, suspicious
			saved = this.sponsorRepository.findOne(sponsor.getId());
			Assert.notNull(saved, "sponsor.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(sponsor.getUserAccount().getUsername()), "sponsor.notEqual.username");
			Assert.isTrue(sponsor.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "sponsor.notEqual.password");
			Assert.isTrue(sponsor.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && sponsor.getSuspicious() == saved.getSuspicious(), "sponsor.notEqual.accountOrSuspicious");
		} else {
			//Solo lo puede crear un administrator
			userAccount = LoginService.getPrincipal();
			authority = new Authority();
			authority.setAuthority("ADMIN");
			Assert.isTrue(userAccount.getAuthorities().contains(authority), "sponsor.authority.administrator");
			Assert.isTrue(sponsor.getUserAccount().isAccountNonLocked() && !sponsor.getSuspicious(), "sponsor.notSuspiciousAndLocked.false");
			sponsor.getUserAccount().setPassword(encoder.encodePassword(sponsor.getUserAccount().getPassword(), null));
			sponsor.getUserAccount().setEnabled(true);

		}

		result = this.sponsorRepository.save(sponsor);

		//Guardamos los folders por defecto cuando creamos el actor
		if (sponsor.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	//Other business methods -------------------------------------------------
	public void searchSuspicious() {
		final Collection<Sponsor> sponsors;
		boolean check;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "sponsor.authority.administrator");

		check = false;
		sponsors = this.findAll();

		for (final Sponsor sponsor : sponsors) {

			for (final Sponsorship s : this.sponsorshipService.findBySponsorId(sponsor.getId()))
				check = check || this.sponsorshipService.checkSpamWords(s) || this.creditCardService.checkSpamWords(s.getCreditCard());

			check = check || this.actorService.checkSpamWords(sponsor);

			sponsor.setSuspicious(check);
			this.sponsorRepository.save(sponsor);

			check = false;
		}

	}

	public Sponsor findByUserAccountId(final int userAccountId) {
		Sponsor result;

		Assert.isTrue(userAccountId != 0);
		result = this.sponsorRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public boolean checkSpamWords(final Sponsor sponsor) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (sponsor.getAddress() != null && sponsor.getAddress().toLowerCase().contains(spamWord)) || (sponsor.getEmail() != null && sponsor.getEmail().toLowerCase().contains(spamWord))
				|| (sponsor.getName() != null && sponsor.getName().toLowerCase().contains(spamWord)) || (sponsor.getPhone() != null && sponsor.getPhone().toLowerCase().contains(spamWord))
				|| (sponsor.getSurname() != null && sponsor.getSurname().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}
}
