
package services;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.SubscriptionRepository;
import security.Authority;
import security.LoginService;
import domain.Plan;
import domain.Subscription;
import domain.User;

@Service
@Transactional
public class SubscriptionService {

	// Managed repository
	@Autowired
	private SubscriptionRepository	subscriptionRepository;

	// Supporting services
	@Autowired
	private PlanService				planService;

	@Autowired
	private UserService				userService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private Validator				validator;


	// Simple CRUD methods
	public Subscription create(final int planId) {
		Subscription result;
		Plan plan;
		Authority authority;
		User user;

		result = new Subscription();
		plan = this.planService.findOne(planId);
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.notNull(plan);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null);
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		result.setUser(user);
		result.setPlan(plan);

		return result;
	}

	public Collection<Subscription> findAll() {
		Collection<Subscription> result;

		result = this.subscriptionRepository.findAll();

		return result;
	}

	public Subscription findOne(final int subscriptionId) {
		Subscription result;

		Assert.isTrue(subscriptionId != 0);

		result = this.subscriptionRepository.findOne(subscriptionId);

		return result;
	}

	public Subscription findOneToEdit(final int subscriptionId) {
		Subscription result;

		result = this.findOne(subscriptionId);
		Assert.notNull(result);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == result.getUser().getUserAccount().getId());

		return result;
	}

	public Subscription save(final Subscription subscription) {
		Subscription result;
		Calendar calendar;

		Assert.notNull(subscription);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == subscription.getUser().getUserAccount().getId());
		Assert.isTrue(this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId()).contains(subscription.getCreditCard()));

		calendar = Calendar.getInstance();

		if (calendar.get(Calendar.YEAR) % 100 == subscription.getCreditCard().getExpirationYear())
			Assert.isTrue(((subscription.getCreditCard().getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);
		else
			Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < subscription.getCreditCard().getExpirationYear());

		if (subscription.getId() == 0)
			Assert.isTrue(this.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()) == null);

		result = this.subscriptionRepository.save(subscription);

		return result;
	}

	public void delete(final Subscription subscription) {
		Subscription subscriptionToDelete;

		Assert.notNull(subscription);
		subscriptionToDelete = this.findOne(subscription.getId());
		Assert.notNull(subscriptionToDelete);
		Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getId() == subscriptionToDelete.getUser().getUserAccount().getId());

		this.subscriptionRepository.delete(subscriptionToDelete);

	}
	public Subscription findByUserId(final int userId) {
		Subscription result;

		result = this.subscriptionRepository.findByUserId(userId);

		return result;
	}

	public Integer countByCreditCardId(final int creditCardId) {
		Integer result;

		Assert.isTrue(creditCardId != 0);

		result = this.subscriptionRepository.countByCreditCardId(creditCardId);

		return result;
	}

	public Subscription reconstruct(final Subscription subscription, final BindingResult binding) {
		Subscription aux;
		Subscription result;

		result = subscription;

		if (subscription.getId() == 0)
			result.setUser(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()));
		else {
			aux = this.findOne(subscription.getId());
			result.setVersion(aux.getVersion());
			result.setPlan(aux.getPlan());
			result.setUser(aux.getUser());
		}

		this.validator.validate(result, binding);

		return result;
	}
}
