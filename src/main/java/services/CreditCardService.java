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

import domain.CreditCard;
import domain.User;

import repositories.CreditCardRepository;
import security.Authority;
import security.LoginService;

@Service
@Transactional
public class CreditCardService {

	// Managed repository
	@Autowired
	private CreditCardRepository creditCardRepository;
	
	// Supporting service
	@Autowired
	private Validator			validator;
	
	@Autowired
	private UserService 		userService;
	
	@Autowired
	private TicketService		ticketService;
	
	@Autowired
	private SubscriptionService	subscriptionService;
	
	// Constructor
	public CreditCardService() {
		super();
	}
	
	// Simple CRUD methods
	public CreditCard create(final User user) {
		CreditCard result;
		
		result = new CreditCard();
		result.setUser(user);
		
		return result;
	}
	
	public Collection<CreditCard> findAll() {
		Collection<CreditCard> result;
		
		result = this.creditCardRepository.findAll();
		
		return result;
	}
	
	public CreditCard findOne(final int creditCardId) {
		CreditCard result;
		
		Assert.isTrue(creditCardId != 0);
		
		result = this.creditCardRepository.findOne(creditCardId);
		
		return result;
	}
	
	public CreditCard findOneToDisplayEdit(final int creditCardId) {
		CreditCard result;
		
		Assert.isTrue(creditCardId != 0);
		
		result = this.creditCardRepository.findOne(creditCardId);
		Assert.notNull(result);
		
		// Solo puede ser editado por el usuario
		Assert.isTrue(result.getUser().getUserAccount().equals(LoginService.getPrincipal()));
		
		return result;
	}
	
	public CreditCard save(final CreditCard creditCard) {
		CreditCard result;
		Authority authority;
		Calendar calendar;
		
		Assert.notNull(creditCard);
		
		calendar = Calendar.getInstance();
		
		// Solo puede ser añadido por un usuario
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		// Solo puede ser añadido por el user de creditCard
		Assert.isTrue(creditCard.getUser().getUserAccount().equals(LoginService.getPrincipal()));
		
		// El año y el mes no puede ser anterior al actual
		if (calendar.get(Calendar.YEAR) % 100 == creditCard.getExpirationYear())
			Assert.isTrue(((creditCard.getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);
		else
			Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < creditCard.getExpirationYear());
		
		result = this.creditCardRepository.save(creditCard);
		
		return result;
	}
	
	public void delete(final CreditCard creditCard) {
		CreditCard savedCreditCard;
		boolean notAdded;
		
		Assert.notNull(creditCard);
		
		savedCreditCard = this.creditCardRepository.findOne(creditCard.getId());
		Assert.notNull(savedCreditCard);
		
		// Solo puede borrarlo el creador del mismo
		Assert.isTrue(savedCreditCard.getUser().getUserAccount().equals(LoginService.getPrincipal()));
		
		// No puede borrar una tarjeta de crédido si está siendo usada
		notAdded = this.ticketService.countByCreditCardId(creditCard.getId()) == 0;
		if(!notAdded) {
			notAdded = this.subscriptionService.countByCreditCardId(creditCard.getId()) == 0;
		}		
		Assert.isTrue(notAdded);
		
		this.creditCardRepository.delete(savedCreditCard);
		
	}
	
	// Other business methods
	public void flush() {
		this.creditCardRepository.flush();
	}
	
	public Page<CreditCard> findByUserAccountId(final int userAccountId, final int page, final int size) {
		Page<CreditCard> result;
		Authority authority;
		
		Assert.isTrue(userAccountId != 0);

		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(LoginService.getPrincipal().getId() == userAccountId);
		
		result = this.creditCardRepository.findByUserAccountId(userAccountId, this.getPageable(page, size));
		
		return result;
	}
	
	public Collection<CreditCard> findByUserAccountId(final int userAccountId) {
		Collection<CreditCard> result;
		Authority authority;
		
		Assert.isTrue(userAccountId != 0);

		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(LoginService.getPrincipal().getId() == userAccountId);
		
		result = this.creditCardRepository.findByUserAccountId(userAccountId);
		
		return result;
	}
	
	public Collection<CreditCard> findValidByUserAccountId(final int userAccountId) {
		Collection<CreditCard> result;
		Authority authority;
		
		Assert.isTrue(userAccountId != 0);

		authority = new Authority();
		authority.setAuthority("USER");

		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		Assert.isTrue(LoginService.getPrincipal().getId() == userAccountId);
		
		Calendar calendar;
		
		calendar = Calendar.getInstance();

		result = this.creditCardRepository.findValidByUserAccountId(userAccountId, calendar.get(Calendar.YEAR) % 100, (calendar.get(Calendar.MONTH) + 1));
				
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
	public CreditCard reconstruct(final CreditCard creditCard, final BindingResult binding) {
		CreditCard aux;
		User user;

		if(creditCard.getId() != 0) {
			aux = this.creditCardRepository.findOne(creditCard.getId());
			
			creditCard.setVersion(aux.getVersion());
			creditCard.setUser(aux.getUser());
		} else {
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			creditCard.setUser(user);
		}
		
		this.validator.validate(creditCard, binding);

		return creditCard;
	}
	
}
