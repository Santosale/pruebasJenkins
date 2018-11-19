package services;

import java.util.ArrayList;
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
import domain.Identifier;
import domain.Plan;
import domain.Raffle;
import domain.Ticket;
import domain.User;
import forms.TicketForm;

import repositories.TicketRepository;
import security.Authority;
import security.LoginService;

@Service
@Transactional
public class TicketService {

	// Managed repository
	@Autowired
	private TicketRepository ticketRepository;
	
	// Supporting services
	@Autowired
	private IdentifierService	identifierService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private Validator			validator;
	
	@Autowired
	private UserService			userService;
	
	@Autowired
	private CreditCardService creditCardService;
	
	// Constructor
	public TicketService() {
		super();
	}
	
	// Simple CRUD methods
	public Ticket create(final Raffle raffle, final User user, final CreditCard creditCard) {
		Ticket result;
		
		result = new Ticket();
		
		result.setRaffle(raffle);
		result.setUser(user);
		result.setCreditCard(creditCard);
		result.setIsGift(false);
		
		return result;
	}
	
	public Ticket create(final Raffle raffle, final User user) {
		Ticket result;
		
		result = new Ticket();
		
		result.setRaffle(raffle);
		result.setUser(user);		
		result.setIsGift(false);

		return result;
	}
	
	public Collection<Ticket> findAll() {
		Collection<Ticket> result;
		
		result = this.ticketRepository.findAll();
		
		return result;
	}
	
	public Ticket findOne(final int ticketId) {
		Ticket result;
		
		Assert.isTrue(ticketId != 0);
		
		result = this.ticketRepository.findOne(ticketId);
		
		return result;
	}
	
	public Ticket save(final Ticket ticket) {
		Ticket result;
		Authority authority;
		Plan plan;
		Integer countTickets;
		Calendar calendar;
		
		Assert.notNull(ticket);
		
		authority = new Authority();
		authority.setAuthority("COMPANY");
		
		// El usuario debe estar logeado
		Assert.isTrue(LoginService.isAuthenticated());
		// El usuario que ha comprado el ticket debe ser el que está autenticado
		plan = this.planService.findByUserId(ticket.getUser().getId());
		Assert.notNull(plan);
		if(LoginService.getPrincipal().getAuthorities().contains(authority) && plan.getName().equals("Gold Premium")) {
			Assert.isTrue(ticket.getRaffle().getCompany().getUserAccount().equals(LoginService.getPrincipal()));
		} else {
			Assert.isTrue(ticket.getUser().getUserAccount().equals(LoginService.getPrincipal()));
		}
		
		// Un ticket no puede ser creado si ya ha sido sorteado
		Assert.isNull(ticket.getRaffle().getWinner());
						
		// No puedes comprar dos tickets en una rifa gratis
		if(ticket.getRaffle().getPrice() == 0) {
			countTickets = this.ticketRepository.countByRaffleIdAndUserId(ticket.getRaffle().getId(), ticket.getUser().getId());
			Assert.isTrue(countTickets == 0);
		}
		
		// No se puede utilizar una credit card caducada
		if(ticket.getCreditCard() != null) {
			Assert.isTrue(this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId()).contains(ticket.getCreditCard()));
			calendar = Calendar.getInstance();
			if (calendar.get(Calendar.YEAR) % 100 == ticket.getCreditCard().getExpirationYear())
				Assert.isTrue(((ticket.getCreditCard().getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);
			else
				Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < ticket.getCreditCard().getExpirationYear());
		}

		
		// Asignar código único
		ticket.setCode(this.generateUniqueCode(ticket.getRaffle()));
		
		// El código único code no puede ser nulo
		Assert.notNull(ticket.getCode());
				
		// Guardar
		result = this.ticketRepository.save(ticket);
		
		// Añadir los puntos a la cuenta del usuario
		this.userService.addPoints(ticket.getUser(), 5);
		
		return result;
	}
	
	// Delete
	public void delete(final Ticket ticket) {
		Authority authority;
		
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		this.ticketRepository.delete(ticket);
	}
	
	public void flush() {
		this.ticketRepository.flush();
	}
	
	// Other business methods 
	public void save(final Collection<Ticket> tickets, final boolean isPaypal) {
		for(Ticket t: tickets) {
	        if(t.getRaffle().getPrice() != 0 && !isPaypal) Assert.notNull(t.getCreditCard());
			this.save(t);
		}
	}
	
	public Integer countByCreditCardId(final int creditCardId) {
		Integer result;
		
		Assert.isTrue(creditCardId != 0);
		
		result = this.ticketRepository.countByCreditCardId(creditCardId);
		
		return result;
	}
	
	public Integer countByRaffleId(final int raffleId) {
		Integer result;
		
		Assert.isTrue(raffleId != 0);

		result = this.ticketRepository.countByRaffleId(raffleId);
		
		return result;
	}
	
	public Page<Ticket> findByUserAccountId(final int userAccountId, int page, int size) {
		Page<Ticket> result;
		
		Assert.isTrue(userAccountId != 0);
		
		result = this.ticketRepository.findByUserAccountId(userAccountId, this.getPageable(page, size));
		
		return result;
	}
	
	public Page<Ticket> findByRaffleIdAndUserAccountId(final int raffleId, final int userAccountId, int page, int size) {
		Page<Ticket> result;
		
		Assert.isTrue(userAccountId != 0 && raffleId != 0);
		
		result = this.ticketRepository.findByRaffleIdAndUserAccountId(raffleId, userAccountId, this.getPageable(page, size));
		
		return result;
	}
	
	public Collection<Ticket> findByRaffleId(final int raffleId) {
		Collection<Ticket> result;
		
		Assert.isTrue(raffleId != 0);
		
		result = this.ticketRepository.findByRaffleId(raffleId);
		
		return result;
	}
	
	public Double avgTicketsPurchaseByUsersPerRaffle() {
		Double result;
		Authority authority;
		
		// Solo accesible por el administrador
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.ticketRepository.avgTicketsPurchaseByUsersPerRaffle();

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
	
	public Collection<Ticket> reconstruct(final TicketForm ticketForm, final BindingResult binding) {
		Collection<Ticket> result;
		Ticket ticket;
		User user;
		Authority authority;
		
		Assert.notNull(ticketForm);
		
		// Solo puede comprarlo el usuario
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		
		result = new ArrayList<Ticket>();
		
		user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(user);
				
		if(ticketForm.getRaffle().getPrice() == 0) {
			ticketForm.setAmount(1);
			Assert.isNull(ticketForm.getCreditCard());
		}

		if(binding != null) this.validator.validate(ticketForm, binding);
		
		for(int i = 0; i < ticketForm.getAmount(); i++) {
			ticket = this.create(ticketForm.getRaffle(), user, ticketForm.getCreditCard());			
			result.add(ticket);
		}
		
		return result;
	}
	
	private String generateUniqueCode(final Raffle raffle) {
		String result;
		final String[] wordcharacters = {
			"A", "b", "C", "d", "E", "F", "G", "z", "I", "J", "K", "8", "M", "N", "k", "3", "Q", "W", "S", "s", "U", "u", "W", "0", "Z",
			"a", "B", "c", "D", "e", "f", "g", "h", "i", "j", "O", "l", "L", "n", "o", "p", "q", "r", "T", "t", "V", "v", "w", "y", "H",
			"Y", "1", "2", "P", "4", "5", "6", "7", "m", "9"
		};
		Identifier identifier;
		Calendar calendar;
		String day;
		String month;
		String year;

		calendar = Calendar.getInstance();

		year = Integer.toString(calendar.get(Calendar.YEAR));
		year = year.substring(2, 4);

		month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
		if (month.length() == 1) month = "0" + month;

		day = Integer.toString(calendar.get(Calendar.DATE));
		if (day.length() == 1) day = "0" + day;

		result = raffle.getId() + year + month + day;
		
		identifier = this.identifierService.findIdentifier();

		result += wordcharacters[identifier.getFivethCounter()] + wordcharacters[identifier.getFourthCounter()] + wordcharacters[identifier.getThirdCounter()] + wordcharacters[identifier.getSecondCounter()] + wordcharacters[identifier.getFirstCounter()];

		if (identifier.getFirstCounter() < wordcharacters.length) {
			identifier.setFirstCounter(identifier.getFirstCounter() + 1);
			if (identifier.getFirstCounter() >= wordcharacters.length) {
				identifier.setFirstCounter(0);
				if (identifier.getSecondCounter() < wordcharacters.length) {
					identifier.setSecondCounter(identifier.getSecondCounter() + 1);
					if (identifier.getSecondCounter() >= wordcharacters.length) {
						identifier.setSecondCounter(0);
						if (identifier.getThirdCounter() < wordcharacters.length) {
							identifier.setThirdCounter(identifier.getThirdCounter() + 1);
							if (identifier.getThirdCounter() >= wordcharacters.length) {
								identifier.setThirdCounter(0);
								if (identifier.getFourthCounter() < wordcharacters.length) {
									identifier.setFourthCounter(identifier.getFourthCounter() + 1);
									if (identifier.getFourthCounter() >= wordcharacters.length){
										identifier.setFourthCounter(0);
										if (identifier.getFivethCounter() < wordcharacters.length) {
											identifier.setFivethCounter(identifier.getFivethCounter() + 1);
											if (identifier.getFivethCounter() >= wordcharacters.length)
												identifier.setFivethCounter(0);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		this.identifierService.save(identifier);

		return result;
	}
	
	
}
