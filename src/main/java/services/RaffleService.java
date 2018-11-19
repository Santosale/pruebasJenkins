
package services;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.RaffleRepository;
import security.Authority;
import security.LoginService;
import domain.Actor;
import domain.Company;
import domain.Raffle;
import domain.Ticket;
import domain.User;

@Service
@Transactional
public class RaffleService {

	// Managed repository
	@Autowired
	private RaffleRepository	raffleRepository;

	// Supporting services
	@Autowired
	private CompanyService		companyService;

	@Autowired
	private TicketService		ticketService;

	@Autowired
	private NotificationService	notificationService;

	@Autowired
	private UserService			userService;

	@Autowired
	private Validator			validator;


	// Constructor
	public RaffleService() {
		super();
	}

	// Simple CRUD methods
	public Raffle create(final Company company) {
		Raffle result;

		result = new Raffle();
		result.setCompany(company);

		return result;
	}

	public Collection<Raffle> findAll() {
		Collection<Raffle> result;

		result = this.raffleRepository.findAll();

		return result;
	}

	public Raffle findOne(final int raffleId) {
		Raffle result;

		Assert.isTrue(raffleId != 0);

		result = this.raffleRepository.findOne(raffleId);

		return result;
	}

	public Raffle save(final Raffle raffle) {
		Raffle result, saved;
		Authority authorityCompany, authorityModerator;
		User user;
		Collection<Actor> actors;
		Ticket ticket;

		Assert.notNull(raffle);

		authorityCompany = new Authority();
		authorityCompany.setAuthority("COMPANY");

		authorityModerator = new Authority();
		authorityModerator.setAuthority("MODERATOR");

		// El actor está logeado
		Assert.isTrue(LoginService.isAuthenticated());

		if (raffle.getId() == 0) {
			// Solo puede ser creado por un actor autenticado como compañía
			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authorityCompany));

			// La compañía autenticada debe ser la que esté en la rifa
			Assert.isTrue(raffle.getCompany().getUserAccount().equals(LoginService.getPrincipal()));

			// La fecha máxima no puede ser hoy o pasada
			Assert.isTrue(raffle.getMaxDate().compareTo(new Date()) >= 0);

			// No se puede asignar el ganador cuando se crea
			Assert.isNull(raffle.getWinner());
		} else {

			saved = this.findOne(raffle.getId());

			// Si lo está editando una compañía...
			if (LoginService.getPrincipal().getAuthorities().contains(authorityCompany)) {
				// La compañía autenticada debe ser la que esté en la rifa
				Assert.isTrue(raffle.getCompany().getUserAccount().equals(LoginService.getPrincipal()));

				// La fecha máxima no puede ser hoy o pasada
				Assert.isTrue(raffle.getMaxDate().compareTo(new Date()) >= 0);

				// No se puede asignar el ganador cuando la edita la compañía
				Assert.isNull(raffle.getWinner());
			} else if (LoginService.getPrincipal().getAuthorities().contains(authorityModerator)) {
				// Si estamos añadiendo el ganador el actor autenticado debe ser el moderador
				if (saved.getWinner() == null && raffle.getWinner() != null) {
					// Comprueba que no esté intentando cambiar la fecha máxima cuando cambia el winner
					Assert.isTrue(saved.getMaxDate().compareTo(raffle.getMaxDate()) == 0);
					// Comprobar que solo lo cambie el moderador
					Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authorityModerator));
				}

				// Si el ganador ya se ha puesto, la rifa no se puede modificar
				if (saved.getWinner() != null)
					Assert.isNull(saved.getWinner());
			}

		}

		// Guardar
		result = this.raffleRepository.save(raffle);

		if (raffle.getId() == 0) {

			actors = this.userService.findWithGoldPremium();
			Assert.notNull(actors);

			for (final Actor a : actors) {
				user = (User) a;
				ticket = this.ticketService.create(result, user);
				ticket.setIsGift(true);
				this.ticketService.save(ticket);
			}
		}

		return result;
	}

	public void delete(final Raffle raffle) {
		Raffle saved;
		Authority authority;
		Collection<Ticket> tickets;
		int countTickets;

		Assert.notNull(raffle);

		saved = this.raffleRepository.findOne(raffle.getId());

		countTickets = this.ticketService.countByRaffleId(saved.getId());
		Assert.notNull(countTickets);
		
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		if(LoginService.getPrincipal().getAuthorities().contains(authority)) {
			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		} else {
			Assert.isTrue(countTickets == 0);
			Assert.isTrue(saved.getCompany().getUserAccount().equals(LoginService.getPrincipal()));
		}
		
		tickets = this.ticketService.findByRaffleId(raffle.getId());
		Assert.notNull(tickets);
		for (final Ticket t : tickets)
			this.ticketService.delete(t);

		this.raffleRepository.delete(saved);
	}

	public void flush() {
		this.raffleRepository.flush();
	}

	// Other business methods 
	public Page<Raffle> findAvailables(final int page, final int size) {
		Page<Raffle> result;

		result = this.raffleRepository.findAvailables(this.getPageable(page, size));

		return result;
	}

	public Page<Raffle> findByUserAccountId(final int userAccountId, final int page, final int size) {
		Page<Raffle> result;
		Authority authority;

		Assert.isTrue(userAccountId != 0);

		// Rifas de un usuario
		authority = new Authority();
		authority.setAuthority("USER");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.raffleRepository.findByUserAccountId(userAccountId, this.getPageable(page, size));

		return result;
	}

	public Page<Raffle> findByCompanyAccountId(final int userAccountId, final int page, final int size) {
		Page<Raffle> result;
		Authority authority;

		Assert.isTrue(userAccountId != 0);

		// Rifas de un usuario
		authority = new Authority();
		authority.setAuthority("COMPANY");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.raffleRepository.findByCompanyAccountId(userAccountId, this.getPageable(page, size));

		return result;
	}

	public Page<Raffle> findOrderedByMaxDate(final int page, final int size) {
		Page<Raffle> result;

		result = this.raffleRepository.findOrderedByMaxDate(this.getPageable(page, size));

		return result;
	}

	public Page<Raffle> findAllPaginated(final int page, final int size) {
		Page<Raffle> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.raffleRepository.findAllPaginated(this.getPageable(page, size));

		return result;
	}

	public Raffle findOneToEdit(final int raffleId) {
		Raffle result;

		Assert.isTrue(raffleId != 0);

		result = this.findOne(raffleId);
		Assert.notNull(result);

		// El que edite tiene que ser el creador del raffle
		Assert.isTrue(result.getCompany().getUserAccount().equals(LoginService.getPrincipal()));

		// No se puede editar si ya se ha sorteado
		Assert.isNull(result.getWinner());

		return result;
	}

	public Raffle findOneToDisplay(final int raffleId) {
		Raffle result;

		Assert.isTrue(raffleId != 0);

		result = this.findOne(raffleId);
		Assert.notNull(result);

		return result;
	}

	public Raffle findOneToDelete(final int raffleId) {
		Raffle result;
		Authority authority;

		Assert.isTrue(raffleId != 0);

		// El que borre tiene que ser el moderador
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.findOne(raffleId);
		Assert.notNull(result);

		return result;
	}

	public void toRaffle(final int raffleId) {
		List<Actor> winner;
		Raffle raffle, copyRaffle;
		Authority authority;

		Assert.isTrue(raffleId != 0);

		// Solo lo puede sortear el moderador
		authority = new Authority();
		authority.setAuthority("MODERATOR");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		raffle = this.findOne(raffleId);
		Assert.notNull(raffle);

		// La fecha del sorteo debe haber pasado
		Assert.isTrue(raffle.getMaxDate().compareTo(new Date()) < 0);

		// El ganado del sorteo no puede estar relleno
		Assert.isTrue(raffle.getWinner() == null);

		winner = this.raffleRepository.toRaffle(raffle.getId(), this.getPageable(1, 1)).getContent();
		Assert.notNull(winner);

		copyRaffle = this.copy(raffle);

		copyRaffle.setWinner((User) winner.get(0));

		this.save(copyRaffle);

		this.notificationService.send(winner, "¡Has ganado un sorteo! Este sorteo es: " + raffle.getTitle(), null);
	}

	@Scheduled(fixedDelay = 300000, initialDelay = 180000)
	public void toRaffle() {
		Collection<Raffle> listToRaffle;
		Raffle copyRaffle;
		List<Actor> winner;

		listToRaffle = this.raffleRepository.findCanBeRaffled();

		for (final Raffle r : listToRaffle) {
			winner = this.raffleRepository.toRaffle(r.getId(), this.getPageable(1, 1)).getContent();
			if (winner != null) {
				copyRaffle = this.copy(r);

				copyRaffle.setWinner((User) winner.get(0));

				this.raffleRepository.save(copyRaffle);

				this.notificationService.send(winner, "¡Has ganado un sorteo! Este sorteo es: " + r.getTitle(), null);
			}
		}

	}

	// Auxiliary methods
	private Raffle copy(final Raffle raffle) {
		Raffle result;

		result = new Raffle();
		result.setId(raffle.getId());
		result.setVersion(raffle.getVersion());
		result.setTitle(raffle.getTitle());
		result.setDescription(raffle.getDescription());
		result.setMaxDate(raffle.getMaxDate());
		result.setProductName(raffle.getProductName());
		result.setProductUrl(raffle.getProductUrl());
		result.setProductImages(raffle.getProductImages());
		result.setWinner(raffle.getWinner());
		result.setCompany(raffle.getCompany());

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

	public Raffle reconstruct(final Raffle raffle, final BindingResult binding) {
		Raffle result;
		Company company;

		if (raffle.getId() == 0) {
			company = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(company);
		} else {
			result = this.raffleRepository.findOne(raffle.getId());
			company = result.getCompany();
			raffle.setVersion(result.getVersion());
		}

		raffle.setCompany(company);

		this.validator.validate(raffle, binding);

		return raffle;
	}

}
