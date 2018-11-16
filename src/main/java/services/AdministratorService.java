
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.AdministratorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Administrator;

@Service
@Transactional
public class AdministratorService {

	//Managed repository----------
	@Autowired
	private AdministratorRepository	administratorRepository;

	//Supporting service-------------
	@Autowired
	private FolderService			folderService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	//Constructor-----------
	public AdministratorService() {
		super();
	}

	//Simple CRUD methods----------

	public Collection<Administrator> findAll() {
		List<Administrator> result;

		result = new ArrayList<Administrator>();
		result.addAll(this.administratorRepository.findAll());

		return result;
	}

	public Administrator findOne(final int administratorId) {
		Administrator result;

		Assert.isTrue(administratorId != 0);

		result = this.administratorRepository.findOne(administratorId);

		return result;
	}

	public Administrator save(final Administrator administrator) {
		Administrator result;
		UserAccount userAccount;
		Authority authority;
		//		Collection<Folder> folderSaved;
		Md5PasswordEncoder encoder;
		Administrator saved;

		//		folderSaved = new ArrayList<Folder>();
		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.notNull(administrator, "administrator.not.null");
		userAccount = LoginService.getPrincipal();

		//Si el administrador ya persiste vemos que el actor logeado sea el propio administrador que se quiere modificar
		if (administrator.getId() != 0) {
			Assert.isTrue(userAccount.equals(administrator.getUserAccount()), "administrator.notEqual.userAccount");
			saved = this.administratorRepository.findOne(administrator.getId());
			Assert.notNull(saved, "administrator.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(administrator.getUserAccount().getUsername()), "administrator.notEqual.username");
			Assert.isTrue(administrator.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "administrator.notEqual.password");
			Assert.isTrue(administrator.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && administrator.getSuspicious() == saved.getSuspicious(), "administrator.notEqual.accountOrSuspicious");
		} else {
			Assert.isTrue(userAccount.getAuthorities().contains(authority), "administrator.authority.administrator"); //Si no vemos que un administrador va a guardar a otro
			Assert.isTrue(administrator.getSuspicious() == false, "administrator.notSuspicious.false");
			administrator.getUserAccount().setPassword(encoder.encodePassword(administrator.getUserAccount().getPassword(), null));
			administrator.getUserAccount().setEnabled(true);
		}

		result = this.administratorRepository.save(administrator);

		//Guardamos los folders por defecto cuando creamos el actor
		if (administrator.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	// Other business methods
	public void searchSuspicious() {
		Collection<Administrator> administrators;
		Collection<String> spamWords;
		List<String> campos;
		boolean check;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "administrator.authority.administrator");

		campos = new ArrayList<String>();
		check = false;
		administrators = this.findAll();

		spamWords = this.configurationService.findSpamWords();

		for (final Administrator administrator : administrators) {

			check = check || this.actorService.checkSpamWords(administrator);

			for (final String spamWord : spamWords)
				for (final String campo : campos) {
					check = check || campo != null && campo.contains(spamWord);
					if (check)
						break;
				}

			administrator.setSuspicious(check);
			this.administratorRepository.save(administrator);

			check = false;

		}

	}
	public Administrator findByUserAccountId(final int userAccountId) {
		Administrator result;

		Assert.isTrue(userAccountId != 0);

		result = this.administratorRepository.findByUserAccountId(userAccountId);

		return result;
	}

}
