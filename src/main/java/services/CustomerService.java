
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CustomerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.Newspaper;
import forms.CustomerForm;

@Service
@Transactional
public class CustomerService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private CustomerRepository	customerRepository;

	// Supporting services-----------------------------------------------------------
	@Autowired
	private Validator			validator;

	@Autowired
	private FolderService		folderService;


	// Constructors -----------------------------------------------------------
	public CustomerService() {
		super();
	}

	// Simple CRUD
	// methods-----------------------------------------------------------
	public Customer create() {
		Customer result;
		UserAccount userAccount;
		Authority authority;

		result = new Customer();
		userAccount = new UserAccount();
		authority = new Authority();

		authority.setAuthority("CUSTOMER");
		userAccount.addAuthority(authority);
		result.setUserAccount(userAccount);

		return result;
	}

	public Collection<Customer> findAll() {
		Collection<Customer> result;

		result = this.customerRepository.findAll();

		return result;
	}

	public Customer findOne(final int customerId) {
		Customer result;

		Assert.isTrue(customerId != 0);

		result = this.customerRepository.findOne(customerId);

		return result;
	}

	public Customer save(final Customer customer) {
		Customer result, saved;
		UserAccount userAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		Assert.notNull(customer);
		authority.setAuthority("CUSTOMER");

		/* Si el customer ya existe, debe ser el que este logueado */
		if (customer.getId() != 0) {
			userAccount = LoginService.getPrincipal();
			Assert.notNull(userAccount);
			Assert.isTrue(userAccount.getAuthorities().contains(authority));
			Assert.isTrue(userAccount.equals(customer.getUserAccount()));
			saved = this.customerRepository.findOne(customer.getId());
			Assert.notNull(saved);
			Assert.isTrue(saved.getUserAccount().getUsername().equals(customer.getUserAccount().getUsername()));
			Assert.isTrue(customer.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()));
		} else {
			/* Si no existe, debe tratarse de anonimo */
			Assert.isTrue(!LoginService.isAuthenticated());
			customer.getUserAccount().setPassword(encoder.encodePassword(customer.getUserAccount().getPassword(), null));
		}

		result = this.customerRepository.save(customer);

		//Guardamos los folders por defecto cuando creamos el actor
		if (customer.getId() == 0)
			this.folderService.createDefaultFolders(result);

		return result;
	}

	public Map<Newspaper, Double> ratioSuscribersPerPrivateNewspaperVersusNumberCustomers(final int page) {
		Map<Newspaper, Double> result;
		List<Object[]> res;
		int pageAux, fromId, tamaño, toId;

		result = new HashMap<Newspaper, Double>();

		res = new ArrayList<Object[]>(this.customerRepository.ratioSuscribersPerPrivateNewspaperVersusNumberCustomers());

		tamaño = res.size();

		pageAux = page;
		if (page <= 0)
			pageAux = 1;

		fromId = (pageAux - 1) * 5;
		if (fromId > tamaño)
			fromId = 0;
		toId = (pageAux * 5);
		if (tamaño > 5) {
			if (toId > tamaño && fromId == 0)
				toId = 5;
			else if (toId > tamaño && fromId != 0)
				toId = tamaño;
		} else
			toId = tamaño;

		for (final Object[] o : res.subList(fromId, toId))
			result.put((Newspaper) o[0], (Double) o[1]);

		return result;
	}

	public Integer countRatioSuscribersPerPrivateNewspaperVersusNumberCustomers() {
		Collection<Object[]> res;
		Integer result;

		res = this.customerRepository.ratioSuscribersPerPrivateNewspaperVersusNumberCustomers();
		result = res.size();

		return result;

	}
	// Other business methods
	public Customer findByUserAccountId(final int id) {
		Customer result;

		Assert.isTrue(id != 0);

		result = this.customerRepository.findByUserAccountId(id);

		return result;
	}

	public Customer reconstruct(final CustomerForm customerForm, final BindingResult binding) {
		Customer result;

		if (customerForm.getId() == 0) {
			result = this.create();

			Assert.notNull(result);
			Assert.isTrue(customerForm.getCheckPassword().equals(customerForm.getPassword()));
			Assert.isTrue(customerForm.isCheck());

			result.getUserAccount().setUsername(customerForm.getUsername());
			result.getUserAccount().setPassword(customerForm.getPassword());
		} else {
			result = this.findOne(customerForm.getId());
			Assert.notNull(result);
			Assert.isTrue(result.getUserAccount().getUsername().equals(customerForm.getUsername()));
		}

		result.setName(customerForm.getName());
		result.setSurname(customerForm.getSurname());
		result.setPostalAddress(customerForm.getPostalAddress());
		result.setEmailAddress(customerForm.getEmailAddress());
		result.setPhoneNumber(customerForm.getPhoneNumber());

		this.validator.validate(customerForm, binding);

		return result;
	}

}
