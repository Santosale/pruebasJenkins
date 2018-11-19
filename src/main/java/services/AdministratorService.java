
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

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

	@Autowired
	private Validator				validator;


	//Supporting service-------------

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
		Md5PasswordEncoder encoder;
		Administrator saved;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.notNull(administrator);
		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);

		/* Si el administrador ya existe, debe ser el que este logueado */

		if (administrator.getId() != 0) {
			Assert.isTrue(userAccount.equals(administrator.getUserAccount()));
			saved = this.administratorRepository.findOne(administrator.getId());
			Assert.notNull(saved);
			Assert.isTrue(saved.getUserAccount().getUsername().equals(administrator.getUserAccount().getUsername()));
			Assert.isTrue(administrator.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		} else {

			/* Si no existe, debe tratarse de un administrator */
			Assert.isTrue(userAccount.getAuthorities().contains(authority));
			administrator.getUserAccount().setPassword(encoder.encodePassword(administrator.getUserAccount().getPassword(), null));
		}

		result = this.administratorRepository.save(administrator);

		return result;
	}

	// Other business methods

	public Administrator findByUserAccountId(final int userAccountId) {
		Administrator result;

		Assert.isTrue(userAccountId != 0);

		result = this.administratorRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public Administrator reconstruct(final Administrator administrator, final BindingResult binding) {
		Administrator result;
		Administrator aux;

		result = administrator;
		aux = this.findOne(administrator.getId());
		Assert.notNull(result);

		result.setUserAccount(aux.getUserAccount());
		result.setVersion(aux.getVersion());

		this.validator.validate(result, binding);

		return result;
	}

}
