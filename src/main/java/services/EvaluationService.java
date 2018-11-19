
package services;

import java.util.Collection;

import org.springframework.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

import repositories.EvaluationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Company;
import domain.Evaluation;
import domain.User;

@Service
@Transactional
public class EvaluationService {

	// Managed repository -----------------------------------------------------
	
	@Autowired
	private EvaluationRepository			evaluationRepository;

	//Supporting services -----------------------------------------------------------
	
	@Autowired
	private UserService				userService;
	
	@Autowired
	private Validator				validator;

	// Constructors -----------------------------------------------------------
	
	public EvaluationService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	
	public Evaluation create(final Company company, final User user) {
		Evaluation result;
		
		result = new Evaluation();
		result.setCompany(company);
		result.setUser(user);

		return result;
	}

	public Evaluation findOne(final int evaluationId) {
		Evaluation result;

		Assert.isTrue(evaluationId != 0);
		result = this.evaluationRepository.findOne(evaluationId);

		return result;
	}

	public Collection<Evaluation> findAll() {
		Collection<Evaluation> result;

		result = this.evaluationRepository.findAll();

		return result;
	}

	public Evaluation save(final Evaluation evaluation) {
		Evaluation result;
		Authority authority;
		UserAccount userAccount;

		Assert.notNull(evaluation);

		userAccount = LoginService.getPrincipal();
		
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(userAccount.getAuthorities().contains(authority));
		
		Assert.isTrue(evaluation.getUser().getUserAccount().equals(userAccount));
		
		if (evaluation.getId() == 0) { 
			this.userService.addPoints(evaluation.getUser(), 10);
		}
		
		result = this.evaluationRepository.save(evaluation);

		return result;
	}

	public void delete(final Evaluation evaluation) {
		Authority authority;
		Evaluation saved;

		Assert.notNull(evaluation);
		
		saved = this.findOne(evaluation.getId());
		Assert.notNull(saved);

		// Solo su usuario puede borrarlas
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		Assert.isTrue(saved.getUser().getUserAccount().equals(LoginService.getPrincipal()));

		this.evaluationRepository.delete(saved);
	}
	
	public void deleteModerator(final Evaluation evaluation) {
		Authority authority;
		Evaluation saved;

		Assert.notNull(evaluation);
		
		saved = this.findOne(evaluation.getId());
		Assert.notNull(saved);
		
		this.userService.addPoints(evaluation.getUser(), -10);

		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		this.evaluationRepository.delete(saved);
	}

	//Other business methods -------------------------------------------------

	public Page<Evaluation> findAllEvaluations(final int page, final int size) {
		Page<Evaluation> result;
		Authority authority;
		
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.evaluationRepository.findAllEvaluations(this.getPageable(page, size));
		
		return result;
	}
	
	public Evaluation reconstruct(final Evaluation evaluation, final BindingResult binding) {
		Evaluation saved;
		User user;

		if (evaluation.getId() == 0) {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			evaluation.setUser(user);
		} else {
			saved = this.evaluationRepository.findOne(evaluation.getId());
			Assert.notNull(saved);
			evaluation.setVersion(saved.getVersion());
			evaluation.setUser(saved.getUser());
			evaluation.setCompany(saved.getCompany());
		}

		this.validator.validate(evaluation, binding);

		return evaluation;
	}
	
	private Pageable getPageable(final int page, final int size) {
		Pageable result;
		
		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);
		
		return result;
	}
	
	public void flush(){
		this.evaluationRepository.flush();
	}
	
	public Page<Evaluation> findByCompanyId(int companyId, final int page, final int size){
		Page<Evaluation> result;
		
		Assert.isTrue(companyId != 0);
		
		result = this.evaluationRepository.findByCompanyId(companyId, this.getPageable(page, size));
		
		return result;
	}

	public Integer countByCompanyId(int companyId){
		Integer result;
		
		Assert.isTrue(companyId != 0);
		
		result = this.evaluationRepository.countByCompanyId(companyId);
		
		return result;
	}
	
	public Page<Evaluation> findByCreatorUserAccountId(int userAccountId, final int page, final int size){
		Page<Evaluation> result;
		Authority authority;
		
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		Assert.isTrue(userAccountId != 0);
		
		result = this.evaluationRepository.findByCreatorUserAccountId(userAccountId, this.getPageable(page, size));
		
		return result;
	}

	public Integer countByCreatorUserAccountId(int userAccountId){
		Integer result;
		
		Assert.isTrue(userAccountId != 0);
		
		result = this.evaluationRepository.countByCreatorUserAccountId(userAccountId);
		
		return result;
	}
	
}
