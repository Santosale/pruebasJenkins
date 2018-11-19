
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.PlanRepository;
import security.Authority;
import security.LoginService;
import domain.Plan;

@Service
@Transactional
public class PlanService {

	// Managed repository
	@Autowired
	private PlanRepository	planRepository;

	// Supporting services
	@Autowired
	private Validator		validator;


	// Constructor
	public PlanService() {
		super();
	}

	public Collection<Plan> findAll() {
		Collection<Plan> result;

		result = this.planRepository.findAll();

		return result;
	}

	public Plan findOne(final int planId) {
		Plan result;

		Assert.isTrue(planId != 0);

		result = this.planRepository.findOne(planId);

		return result;
	}

	public Plan findOneToEdit(final int planId) {
		Plan result;
		Authority authority;
		Assert.isTrue(planId != 0);
		result = this.findOne(planId);
		Assert.notNull(result);
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		return result;
	}

	public Plan save(final Plan plan) {
		Plan result, saved;
		Authority authority;

		Assert.notNull(plan);
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

		saved = this.findOne(plan.getId());

		Assert.isTrue(plan.getId() != 0);
		Assert.isTrue(plan.getName().equals(saved.getName()));

		result = this.planRepository.save(plan);

		return result;
	}

	public Plan findByUserId(final int userId) {
		Plan result;

		Assert.isTrue(userId != 0);
		result = this.planRepository.findByUserId(userId);

		return result;
	}

	public Plan reconstruct(final Plan plan, final BindingResult binding) {
		Plan aux;
		Plan result;

		result = plan;
		aux = this.findOne(plan.getId());

		result.setVersion(aux.getVersion());
		result.setName(aux.getName());

		this.validator.validate(result, binding);

		return result;
	}

}
