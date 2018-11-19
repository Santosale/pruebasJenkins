
package services;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.SubscriptionNewspaperRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.Newspaper;
import domain.SubscriptionNewspaper;

@Service
@Transactional
public class SubscriptionNewspaperService {

	// Managed repository
	@Autowired
	private SubscriptionNewspaperRepository	subscriptionNewspaperRepository;

	// Supporting service
	@Autowired
	private Validator						validator;


	// Constructor
	public SubscriptionNewspaperService() {
		super();
	}

	// Simple CRUD methods
	public SubscriptionNewspaper create(final Customer customer, final Newspaper newspaper) {
		SubscriptionNewspaper result;

		result = new SubscriptionNewspaper();
		result.setCustomer(customer);
		result.setNewspaper(newspaper);

		return result;
	}

	public Collection<SubscriptionNewspaper> findAll() {
		Collection<SubscriptionNewspaper> result;

		result = this.subscriptionNewspaperRepository.findAll();

		return result;
	}

	public SubscriptionNewspaper findOne(final int subscriptionNewspaperId) {
		SubscriptionNewspaper result;

		Assert.isTrue(subscriptionNewspaperId != 0);

		result = this.subscriptionNewspaperRepository.findOne(subscriptionNewspaperId);

		return result;
	}

	public SubscriptionNewspaper findOneToEdit(final int subscriptionNewspaperId) {
		SubscriptionNewspaper result;

		Assert.isTrue(subscriptionNewspaperId != 0);

		result = this.subscriptionNewspaperRepository.findOne(subscriptionNewspaperId);

		// Solo puede ser editado por el usuario adjunto
		Assert.isTrue(result.getCustomer().getUserAccount().equals(LoginService.getPrincipal()));

		return result;
	}

	public SubscriptionNewspaper save(final SubscriptionNewspaper subscriptionNewspaper) {
		SubscriptionNewspaper result;
		Authority authority;
		Boolean subscriptionNewspaperNoExists;
		Calendar calendar;

		Assert.notNull(subscriptionNewspaper);
		result = null;

		// Solo puede ser añadido por un usuario
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		// Solo puede ser añadido por el user de SubscriptionNewspaper
		Assert.isTrue(subscriptionNewspaper.getCustomer().getUserAccount().equals(LoginService.getPrincipal()));

		// Un customer solo puede tener una subscriptionNewspaper para cada newspaper
		subscriptionNewspaperNoExists = this.findByUserAccountIdNewspaper(LoginService.getPrincipal().getId(), subscriptionNewspaper.getNewspaper().getId());

		if (subscriptionNewspaper.getId() == 0) {
			Assert.isTrue(subscriptionNewspaperNoExists);
			Assert.isTrue(subscriptionNewspaper.getNewspaper().getIsPrivate());
		}

		calendar = Calendar.getInstance();

		//CreditCard no caducada
		//Caduca este año
		if (calendar.get(Calendar.YEAR) % 100 == subscriptionNewspaper.getCreditCard().getExpirationYear())
			Assert.isTrue(((subscriptionNewspaper.getCreditCard().getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);

		//Caduca año próximo
		//		else if ((calendar.get(Calendar.YEAR) % 100) + 1 == subscriptionNewspaper.getCreditCard().getExpirationYear())
		//			Assert.isTrue(subscriptionNewspaper.getCreditCard().getExpirationMonth() >= 2 || calendar.get(Calendar.MONTH) + 1 <= 11);

		//Caduca más tarde
		else
			Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < subscriptionNewspaper.getCreditCard().getExpirationYear());

		result = this.subscriptionNewspaperRepository.save(subscriptionNewspaper);

		return result;
	}

	public void delete(final SubscriptionNewspaper subscriptionNewspaper) {
		SubscriptionNewspaper savedSubscriptionNewspaper;

		Assert.notNull(subscriptionNewspaper);

		savedSubscriptionNewspaper = this.subscriptionNewspaperRepository.findOne(subscriptionNewspaper.getId());

		// Solo puede borrarlo el creador del mismo
		Assert.isTrue(savedSubscriptionNewspaper.getCustomer().getUserAccount().equals(LoginService.getPrincipal()));

		this.subscriptionNewspaperRepository.delete(savedSubscriptionNewspaper);

	}

	public void deleteFromNewspaper(final SubscriptionNewspaper subscriptionNewspaper) {
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.notNull(subscriptionNewspaper);
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		this.subscriptionNewspaperRepository.delete(subscriptionNewspaper);

	}

	// Other business methods
	public Page<SubscriptionNewspaper> findByUserAccountId(final int userAccountId, final int page, final int size) {
		Page<SubscriptionNewspaper> result;
		Authority authority;
		UserAccount userAccount;

		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		if (LoginService.isAuthenticated()) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			Assert.isTrue(userAccount.getAuthorities().contains(authority));
		}

		Assert.isTrue(userAccountId != 0);

		result = this.subscriptionNewspaperRepository.findByUserAccountId(userAccountId, this.getPageable(page, size));

		return result;
	}

	public Collection<SubscriptionNewspaper> findByUserAccountId(final int userAccountId) {
		Collection<SubscriptionNewspaper> result;

		Assert.isTrue(userAccountId != 0);

		result = this.subscriptionNewspaperRepository.findByUserAccountId(userAccountId);

		return result;
	}

	public Collection<SubscriptionNewspaper> findByCustomerAndNewspaperId(final int customerId, final int newspaperId) {
		Collection<SubscriptionNewspaper> result;

		Assert.isTrue(customerId != 0);
		Assert.isTrue(newspaperId != 0);

		result = this.subscriptionNewspaperRepository.findByCustomerAndNewspaperId(customerId, newspaperId);

		return result;
	}

	public Collection<SubscriptionNewspaper> findByNewspaperId(final int newspaperId) {
		Collection<SubscriptionNewspaper> result;

		Assert.isTrue(newspaperId != 0);

		result = this.subscriptionNewspaperRepository.findByNewspaperId(newspaperId);

		return result;
	}

	public Boolean findByUserAccountIdNewspaper(final int userAccountId, final int newspaperId) {
		Boolean result;
		Integer repo;

		Assert.isTrue(userAccountId != 0);
		Assert.isTrue(newspaperId != 0);

		repo = this.subscriptionNewspaperRepository.findByUserAccountIdNewspaper(userAccountId, newspaperId);

		if (repo == null || repo == 0)
			result = true;
		else
			result = false;

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

	// Pruned object domain
	public SubscriptionNewspaper reconstruct(final SubscriptionNewspaper subscriptionNewspaper, final BindingResult binding) {
		SubscriptionNewspaper result;
		SubscriptionNewspaper aux;

		if (subscriptionNewspaper.getId() == 0)
			result = subscriptionNewspaper;
		else {
			result = subscriptionNewspaper;
			aux = this.subscriptionNewspaperRepository.findOne(subscriptionNewspaper.getId());
			result.setVersion(aux.getVersion());
			result.setCustomer(aux.getCustomer());
			result.setNewspaper(aux.getNewspaper());
			result.setCreditCard(subscriptionNewspaper.getCreditCard());
		}

		this.validator.validate(result, binding);

		return result;
	}

}
