
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CreditCardRepository;
import security.Authority;
import security.LoginService;
import domain.Actor;
import domain.CreditCard;
import domain.Explorer;
import domain.Sponsor;

@Service
@Transactional
public class CreditCardService {

	// Constructors -----------------------------------------------------------
	public CreditCardService() {
		super();
	}


	// Managed repository -----------------------------------------------------
	@Autowired
	private CreditCardRepository	creditCardRepository;

	// Supporting services
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ExplorerService			explorerService;

	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private ActorService			actorService;


	// Simple CRUD methods
	// -----------------------------------------------------------

	public CreditCard create(final Actor actor) {
		CreditCard result;

		result = new CreditCard();
		result.setActor(actor);

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

	public CreditCard save(final CreditCard creditCard) {
		CreditCard result;
		Authority authorityExplorer, authoritySponsor;

		Assert.notNull(creditCard);

		authorityExplorer = new Authority();
		authoritySponsor = new Authority();

		// Una credit card solo puede ser añadida por un explorer o por un
		// sponsor
		authorityExplorer.setAuthority("EXPLORER");
		authoritySponsor.setAuthority("SPONSOR");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authorityExplorer) || LoginService.getPrincipal().getAuthorities().contains(authoritySponsor));

		result = this.creditCardRepository.save(creditCard);

		return result;
	}

	public void delete(final CreditCard creditCard) {
		Assert.notNull(creditCard);
		this.creditCardRepository.delete(creditCard);
	}

	// Other business methods
	public Collection<CreditCard> findByExplorerId(final int explorerId) {
		Collection<CreditCard> result;
		Explorer explorer;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(explorerId != 0);

		explorer = this.explorerService.findOne(explorerId);

		Assert.notNull(explorer);

		Assert.isTrue(LoginService.getPrincipal().equals(explorer.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.creditCardRepository.findByExplorerId(explorerId);

		return result;
	}

	public Collection<CreditCard> findBySponsorId(final int sponsorId) {
		Collection<CreditCard> result;
		Sponsor sponsor;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(sponsorId != 0);

		sponsor = this.sponsorService.findOne(sponsorId);

		Assert.notNull(sponsor);

		Assert.isTrue(LoginService.getPrincipal().equals(sponsor.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.creditCardRepository.findByExplorerId(sponsorId);

		return result;
	}

	public Collection<CreditCard> findByActorId(final int actorId) {
		Collection<CreditCard> result;
		Actor actor;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(actorId != 0);

		actor = this.actorService.findOne(actorId);
		Assert.notNull(actor);

		Assert.isTrue(LoginService.getPrincipal().equals(actor.getUserAccount()) || LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.creditCardRepository.findByActorId(actorId);

		return result;
	}

	public boolean checkSpamWords(final CreditCard creditCard) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = creditCard.getBrandName() != null && creditCard.getBrandName().contains(spamWord) || creditCard.getHolderName() != null && creditCard.getHolderName().contains(spamWord);
			if (result)
				break;
		}

		return result;
	}

}
