
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

import repositories.SubscriptionVolumeRepository;
import security.Authority;
import security.LoginService;
import domain.Customer;
import domain.SubscriptionVolume;
import domain.Volume;

@Service
@Transactional
public class SubscriptionVolumeService {

	// Managed repository
	@Autowired
	private SubscriptionVolumeRepository	subscriptionVolumeRepository;

	// Supporting service
	@Autowired
	private CustomerService					customerService;

	@Autowired
	private VolumeService					volumeService;

	@Autowired
	private Validator						validator;


	// Constructor
	public SubscriptionVolumeService() {
		super();
	}

	// Simple CRUD methods
	public SubscriptionVolume create(final int volumeId) {
		SubscriptionVolume result;
		Customer customer;
		Authority authority;
		Volume volume;

		Assert.isTrue(volumeId != 0);
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		volume = this.volumeService.findOne(volumeId);
		Assert.notNull(volume);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		customer = this.customerService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(this.findByCustomerIdAndVolumeId(customer.getId(), volumeId) == null);
		result = new SubscriptionVolume();
		result.setCustomer(customer);
		result.setVolume(volume);

		return result;
	}

	public Collection<SubscriptionVolume> findAll() {
		Collection<SubscriptionVolume> result;

		result = this.subscriptionVolumeRepository.findAll();

		return result;
	}

	public SubscriptionVolume findOne(final int subscriptionVolumeId) {
		SubscriptionVolume result;

		Assert.isTrue(subscriptionVolumeId != 0);

		result = this.subscriptionVolumeRepository.findOne(subscriptionVolumeId);

		return result;
	}

	public SubscriptionVolume findOneToEdit(final int subscriptionVolumeId) {
		SubscriptionVolume result;

		Assert.isTrue(subscriptionVolumeId != 0);
		result = this.findOne(subscriptionVolumeId);
		Assert.notNull(result);

		Assert.isTrue(result.getCustomer().getUserAccount().getId() == LoginService.getPrincipal().getId());

		return result;
	}

	public SubscriptionVolume save(final SubscriptionVolume subscriptionVolume) {
		SubscriptionVolume result;

		Assert.notNull(subscriptionVolume);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(subscriptionVolume.getCustomer().getUserAccount().getId() == LoginService.getPrincipal().getId());

		Calendar calendar;

		calendar = Calendar.getInstance();

		//CreditCard no caducada
		//Caduca este año
		if (calendar.get(Calendar.YEAR) % 100 == subscriptionVolume.getCreditCard().getExpirationYear())
			Assert.isTrue(((subscriptionVolume.getCreditCard().getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);

		//		//Caduca año próximo
		//		else if ((calendar.get(Calendar.YEAR) % 100) + 1 == subscriptionVolume.getCreditCard().getExpirationYear())
		//			Assert.isTrue(subscriptionVolume.getCreditCard().getExpirationMonth() >= 2 || calendar.get(Calendar.MONTH) + 1 <= 11);

		//Caduca más tarde
		else
			Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < subscriptionVolume.getCreditCard().getExpirationYear());

		if (subscriptionVolume.getId() == 0)
			Assert.isTrue(this.subscriptionVolumeRepository.findByCustomerIdAndVolumeId(subscriptionVolume.getCustomer().getId(), subscriptionVolume.getVolume().getId()) == null);
		result = this.subscriptionVolumeRepository.save(subscriptionVolume);

		return result;
	}
	public void delete(final SubscriptionVolume subscriptionVolume) {
		SubscriptionVolume subscriptionVolumeToDelete;

		subscriptionVolumeToDelete = this.findOne(subscriptionVolume.getId());
		Assert.notNull(subscriptionVolumeToDelete);
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(subscriptionVolumeToDelete.getCustomer().getUserAccount().getId() == LoginService.getPrincipal().getId());

		this.subscriptionVolumeRepository.delete(subscriptionVolumeToDelete);

	}

	public Page<SubscriptionVolume> findByCustomerId(final int customerId, final int page, final int size) {
		Page<SubscriptionVolume> result;
		Authority authority;

		Assert.isTrue(customerId != 0);
		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(this.customerService.findByUserAccountId(LoginService.getPrincipal().getId()).getId() == customerId);

		result = this.subscriptionVolumeRepository.findByCustomerId(customerId, this.getPageable(page, size));

		return result;

	}

	public Collection<SubscriptionVolume> findByVolumeId(final int volumeId) {
		Collection<SubscriptionVolume> result;

		Assert.isTrue(volumeId != 0);
		Assert.isTrue(LoginService.isAuthenticated());

		result = this.subscriptionVolumeRepository.findByVolumeId(volumeId);

		return result;

	}

	public SubscriptionVolume findByCustomerIdAndVolumeId(final int customerId, final int volumeId) {
		SubscriptionVolume result;

		Assert.isTrue(customerId != 0);
		Assert.isTrue(volumeId != 0);

		result = this.subscriptionVolumeRepository.findByCustomerIdAndVolumeId(customerId, volumeId);

		return result;

	}

	public void deleteFromVolume(final SubscriptionVolume subscriptionVolume) {
		SubscriptionVolume subscriptionVolumeToDelete;

		Assert.isTrue(LoginService.isAuthenticated());

		subscriptionVolumeToDelete = this.findOne(subscriptionVolume.getId());

		this.subscriptionVolumeRepository.delete(subscriptionVolumeToDelete);
	}

	public Double ratioSubscritionVolumeVsSubscriptionNewspaper() {
		Double result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.subscriptionVolumeRepository.ratioSubscritionVolumeVsSubscriptionNewspaper();

		return result;
	}

	public SubscriptionVolume reconstruct(final SubscriptionVolume subscriptionVolume, final BindingResult binding) {
		SubscriptionVolume result;
		SubscriptionVolume aux;

		if (subscriptionVolume.getId() == 0)
			result = subscriptionVolume;
		else {
			result = subscriptionVolume;
			aux = this.subscriptionVolumeRepository.findOne(subscriptionVolume.getId());
			result.setVersion(aux.getVersion());
			result.setCustomer(aux.getCustomer());
			result.setVolume(aux.getVolume());
			result.setCreditCard(subscriptionVolume.getCreditCard());
		}

		this.validator.validate(result, binding);

		return result;
	}

	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}

}
