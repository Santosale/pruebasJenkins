
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.SponsorshipRepository;
import security.LoginService;
import security.UserAccount;
import domain.Sponsor;
import domain.Sponsorship;
import domain.Trip;

@Service
@Transactional
public class SponsorshipService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SponsorshipRepository	sponsorshipRepository;

	//Supporting services -----------------------------------------------------------

	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------
	public SponsorshipService() {
		super();
	}

	// Simple CRUD methods -----------------------------------------------------------
	public Sponsorship create(final Sponsor sponsor, final Trip trip) {
		Sponsorship result;

		Assert.notNull(sponsor);
		result = new Sponsorship();
		result.setSponsor(sponsor);
		result.setTrip(trip);
		return result;
	}
	public Collection<Sponsorship> findAll() {
		Collection<Sponsorship> result;

		result = this.sponsorshipRepository.findAll();

		return result;
	}

	public Sponsorship findOne(final int sponsorshipId) {
		Sponsorship result;

		Assert.isTrue(sponsorshipId != 0);
		result = this.sponsorshipRepository.findOne(sponsorshipId);

		return result;
	}

	public Sponsorship save(final Sponsorship sponsorship) {
		Sponsorship result;
		UserAccount userAccount;
		Sponsorship saved;

		Assert.notNull(sponsorship, "sponsorship.not.null");

		userAccount = LoginService.getPrincipal();

		// Solo su sponsor puede crear o actualizar sponsorships
		Assert.isTrue(sponsorship.getSponsor().getUserAccount().equals(userAccount), "sponsorship.sponsor.login");

		//Solo podemos actualizar el trip y la creditCard. Miramos si es necesario
		if (sponsorship.getId() != 0) {
			saved = this.sponsorshipRepository.findOne(sponsorship.getId());
			Assert.notNull(saved, "sponsorship.not.null");
			//No se puede cambiar el sponsor del sponsorship
			Assert.isTrue(saved.getSponsor().equals(sponsorship.getSponsor()), "sponsorship.equals.sponsor");
			//No dejamos que llegue una nueva creditCard, aunque si puede actualizar la existente
			//Assert.isTrue(sponsorship.getCreditCard().equals(saved.getCreditCard()));
		}

		//La creditCard debe ser de ese sponsor
		Assert.isTrue(sponsorship.getSponsor().getId() == sponsorship.getCreditCard().getActor().getId(), "sponsorship.creditcard.sponsor");

		result = this.sponsorshipRepository.save(sponsorship);

		return result;
	}

	public void delete(final Sponsorship sponsorship) {
		UserAccount userAccount;

		Assert.notNull(sponsorship, "sponsorship.not.null");

		//Miramos que lo borre su sponsor
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(sponsorship.getSponsor().getUserAccount().equals(userAccount), "sponsorship.sponsor.login");

		this.sponsorshipRepository.delete(sponsorship);

		//this.creditCardService.delete(sponsorship.getCreditCard());

	}

	//Other business methods -------------------------------------------------

	//Listar los sponsorships de un trip
	public Collection<Sponsorship> findByTripId(final int tripId) {
		Collection<Sponsorship> result;

		Assert.isTrue(tripId != 0);
		result = this.sponsorshipRepository.findByTripId(tripId);

		return result;
	}

	//Listar los sponsorships de un sponsor
	public Collection<Sponsorship> findBySponsorId(final int sponsorId) {
		Collection<Sponsorship> result;

		Assert.isTrue(sponsorId != 0);
		result = this.sponsorshipRepository.findBySponsorId(sponsorId);

		return result;
	}

	public void deleteFromTrip(final int sponsorshipId) {
		Sponsorship sponsorship;
		Assert.isTrue(sponsorshipId != 0);
		sponsorship = this.findOne(sponsorshipId);
		this.sponsorshipRepository.delete(sponsorship);

	}

	public Sponsorship findRandomSponsorship(final int tripId) {
		Sponsorship result;
		Page<Sponsorship> sponsorshipPage;

		Assert.isTrue(tripId != 0);

		sponsorshipPage = this.sponsorshipRepository.findRandomSponsorship(new PageRequest(0, 1), tripId);

		result = null;
		if (!sponsorshipPage.getContent().isEmpty())
			result = sponsorshipPage.getContent().get(0);

		return result;
	}

	public boolean checkSpamWords(final Sponsorship sponsorship) {
		boolean result;
		Collection<String> spamWords;

		result = false;
		spamWords = this.configurationService.findSpamWords();

		for (final String spamWord : spamWords) {
			result = (sponsorship.getBanner() != null && sponsorship.getBanner().toLowerCase().contains(spamWord)) || (sponsorship.getLinkInfoPage() != null && sponsorship.getLinkInfoPage().toLowerCase().contains(spamWord));
			if (result)
				break;
		}

		return result;
	}
}
