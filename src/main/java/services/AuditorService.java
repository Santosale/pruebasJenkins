
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.AuditorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Audit;
import domain.Auditor;
import domain.Note;

@Service
@Transactional
public class AuditorService {

	// Managed repository----------------
	@Autowired
	private AuditorRepository		auditorRepository;

	// Supporting Services-----------------
	@Autowired
	private FolderService			folderService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private TripService				tripService;

	@Autowired
	private AuditService			auditService;

	@Autowired
	private NoteService				noteService;

	@Autowired
	private ActorService			actorService;


	// Constructor ------------
	public AuditorService() {
		super();
	}

	// Simple CRUD Methods
	public Auditor create() {
		Auditor result;
		UserAccount userAccount;
		Authority authority;
		Authority authorityOfAdmin;
		Collection<Authority> authorities;

		authorityOfAdmin = new Authority();
		authorityOfAdmin.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authorityOfAdmin));

		result = new Auditor();
		authority = new Authority();
		authority.setAuthority("AUDITOR");
		authorities = new ArrayList<Authority>();
		authorities.add(authority);
		userAccount = new UserAccount();
		userAccount.setAuthorities(authorities);
		userAccount.setEnabled(true);

		result.setUserAccount(userAccount);
		result.setSuspicious(false);

		return result;
	}

	public Collection<Auditor> findAll() {
		Collection<Auditor> result;

		result = this.auditorRepository.findAll();

		return result;
	}

	public Auditor findOne(final int auditorId) {
		Auditor result;

		Assert.isTrue(auditorId != 0);

		result = this.auditorRepository.findOne(auditorId);

		return result;
	}

	public Auditor save(final Auditor auditor) {
		Auditor result;
		Md5PasswordEncoder encoder;
		Auditor saved;
		UserAccount userAccount;
		Authority authority;

		encoder = new Md5PasswordEncoder();

		Assert.notNull(auditor, "auditor.not.null");

		userAccount = LoginService.getPrincipal();

		if (auditor.getId() != 0) {

			Assert.isTrue(LoginService.getPrincipal().equals(auditor.getUserAccount()), "auditor.notEqual.userAccount"); //Solo el propio auditor lo puede modificar
			saved = this.auditorRepository.findOne(auditor.getId());
			Assert.notNull(saved, "auditor.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(auditor.getUserAccount().getUsername()), "auditor.notEqual.username");
			Assert.isTrue(auditor.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "auditor.notEqual.password");
			Assert.isTrue(auditor.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && auditor.getSuspicious() == saved.getSuspicious(), "auditor.notEqual.accountOrSuspicious");
		} else {
			authority = new Authority();
			authority.setAuthority("ADMIN");
			Assert.isTrue(userAccount.getAuthorities().contains(authority), "auditor.authority.administrator");
			Assert.isTrue(auditor.getSuspicious() == false, "auditor.notSuspicious.false");
			auditor.getUserAccount().setPassword(encoder.encodePassword(auditor.getUserAccount().getPassword(), null));
			auditor.getUserAccount().setEnabled(true);

		}

		result = this.auditorRepository.save(auditor);

		//Guardamos los folders por defecto cuando creamos el actor
		if (auditor.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	//Other business methods---------------
	public Auditor findByNoteId(final int noteId) {
		Auditor result;
		UserAccount userAccount;

		Assert.isTrue(noteId != 0);
		userAccount = LoginService.getPrincipal();
		result = this.auditorRepository.findByNoteId(noteId);
		if (result != null)
			Assert.isTrue(userAccount.equals(result.getUserAccount())); //Solo el auditor de la nota puede hacer el metodo
		return result;
	}

	public void searchSuspicious() {
		Collection<Auditor> auditors;
		Collection<String> spamWords;
		List<String> campos;
		boolean check;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "auditor.authority.administrator");

		campos = new ArrayList<String>();
		check = false;
		auditors = this.findAll();

		spamWords = this.configurationService.findSpamWords();

		for (final Auditor auditor : auditors) {

			for (final Note n : this.noteService.findByAuditorId(auditor.getId()))
				campos.add(n.getRemark());

			for (final Audit a : this.auditService.findByAuditorId(auditor.getId()))
				check = check || this.auditService.checkSpamWords(a);

			check = check || this.actorService.checkSpamWords(auditor);

			for (final String spamWord : spamWords)
				for (final String campo : campos) {
					check = check || campo != null && campo.contains(spamWord);
					if (check)
						break;
				}

			auditor.setSuspicious(check);
			this.auditorRepository.save(auditor);
			check = false;

		}

	}

	public Collection<Auditor> findByTripId(final int tripId) {
		final Collection<Auditor> result;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(tripId != 0);
		if (this.tripService.findOne(tripId) != null)
			Assert.isTrue(this.tripService.findOne(tripId).getManager().getUserAccount().equals(userAccount)); //El manager puede coger los auditors de un trip
		result = this.auditorRepository.findByTripId(tripId);

		return result;

	}
	public Auditor findByUserAccountId(final int userAccountId) {
		Auditor result;

		Assert.isTrue(userAccountId != 0);

		result = this.auditorRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public boolean checkSpamWords(final Auditor auditor) {
		Collection<String> spamWords;
		boolean result;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (auditor.getAddress() != null && auditor.getAddress().toLowerCase().contains(spamWord)) || auditor.getEmail() != null && auditor.getEmail().toLowerCase().contains(spamWord) || auditor.getName() != null
				&& auditor.getName().toLowerCase().contains(spamWord) || auditor.getSurname() != null && auditor.getSurname().toLowerCase().contains(spamWord);
			if (result == true)
				break;
		}
		return result;
	}

}
