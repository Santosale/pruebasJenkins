package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import domain.Company;
import forms.CompanyForm;

import repositories.CompanyRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class CompanyService {

	// Managed repository
	@Autowired
	private CompanyRepository companyRepository;
	
	// Supporting services
	@Autowired
	private Validator		validator;
	
	// Constructor
	public CompanyService() {
		super();
	}
	
	// Simple CRUD methods
	public Company create() {
		Company result;
		UserAccount userAccount;
		Authority authority;

		result = new Company();
		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("COMPANY");
		userAccount.addAuthority(authority);

		result.setUserAccount(userAccount);
		
		return result;
	}
	
	public Collection<Company> findAll() {
		Collection<Company> result;
		
		result = this.companyRepository.findAll();
		
		return result;
	}
	
	public Company findOne(final int companyId) {
		Company result;
		
		Assert.isTrue(companyId != 0);
		
		result = this.companyRepository.findOne(companyId);
		
		return result;
	}
	
	public Company save(final Company company) {
		Company result, saved;
		Md5PasswordEncoder encoder;
		
		Assert.notNull(company);

		encoder = new Md5PasswordEncoder();
		
		if(company.getId() == 0) {
			// Para crear un company debes estar sin autenticar
			Assert.isTrue(!LoginService.isAuthenticated());
			company.getUserAccount().setPassword(encoder.encodePassword(company.getUserAccount().getPassword(), null));			
		} else {
			// Solo puede ser editado por él mismo
			Assert.isTrue(company.getUserAccount().equals(LoginService.getPrincipal()));
			
			// No se puede cambiar usuario ni contraseña
			saved = this.findOne(company.getId());
			Assert.isTrue(saved.getUserAccount().getUsername().equals(company.getUserAccount().getUsername()));
			Assert.isTrue(company.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		}
		
		result = this.companyRepository.save(company);
		
		return result;
	}
		
	// Other business methods
	public void flush() {
		this.companyRepository.flush();
	}
	
	public Page<Company> findAllPaginated(final int page, final int size) {
		Page<Company> result;
		
		Assert.isTrue(page >= 0);
		
		result = this.companyRepository.findAll(this.getPageable(page, size));
		
		return result;
	}

	public Company findByUserAccountId(final int userAccountId) {
		Company result;
		
		Assert.isTrue(userAccountId != 0);
		
		result = this.companyRepository.findByUserAccountId(userAccountId);
		
		return result;
	}
	
	public Page<Company> companiesWithMoreTags(final int page, final int size) {
		Page<Company> result;
		Authority authority;
		
		Assert.isTrue(page >= 0);
		
		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.companyRepository.companiesWithMoreTags(this.getPageable(page, size));

		return result;
	}
	
	public Page<Company> findWithMoreAvgPercentageSurveys15(final int page, final int size) {
		Page<Company> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.companyRepository.findWithMoreAvgPercentageSurveis15(this.getPageable(page, size));

		return result;
	}

	public Page<Company> findWithMoreAvgPercentageSurveys10(final int page, final int size) {
		Page<Company> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.companyRepository.findWithMoreAvgPercentageSurveis10(this.getPageable(page, size));

		return result;
	}

	public Page<Company> findWithMoreAvgPercentageSurveys5(final int page, final int size) {
		Page<Company> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.companyRepository.findWithMoreAvgPercentageSurveis5(this.getPageable(page, size));

		return result;
	}
	
	// Auxiliary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}
	
	// Reconstruct
	public Company reconstruct(CompanyForm companyForm, final BindingResult binding) {
		Company result;
		
		if (companyForm.getId() == 0) {
			result = this.create();

			Assert.notNull(result);
			Assert.isTrue(companyForm.getCheckPassword().equals(companyForm.getPassword()));
			Assert.isTrue(companyForm.isCheck());

			result.getUserAccount().setUsername(companyForm.getUsername());
			result.getUserAccount().setPassword(companyForm.getPassword());
			
			result.getUserAccount().setEnabled(true);
		} else {
			result = this.findOne(companyForm.getId());
			Assert.notNull(result);
		}
		
		result.setName(companyForm.getName());
		result.setSurname(companyForm.getSurname());
		result.setAddress(companyForm.getAddress());
		result.setEmail(companyForm.getEmail());
		result.setPhone(companyForm.getPhone());
		result.setIdentifier(companyForm.getIdentifier());		
		
		result.setCompanyName(companyForm.getCompanyName());
		result.setType(companyForm.getType());
		
		this.validator.validate(companyForm, binding);

		return result;
	}
	
}
