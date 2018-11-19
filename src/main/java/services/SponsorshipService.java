
package services;

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

import repositories.SponsorshipRepository;
import security.Authority;
import security.LoginService;
import domain.Bargain;
import domain.Sponsor;
import domain.Sponsorship;

@Service
@Transactional
public class SponsorshipService {

	// Managed repository
	@Autowired
	private SponsorshipRepository	sponsorshipRepository;

	// Services
	@Autowired
	private BargainService			bargainService;

	@Autowired
	private SponsorService			sponsorService;

	@Autowired
	private Validator				validator;


	// Constructor
	public SponsorshipService() {
		super();
	}

	// Simple CRUD methods----------
	public Sponsorship create(final Bargain bargain) {
		Sponsorship result;
		Sponsor sponsor;

		Assert.notNull(bargain);
		result = new Sponsorship();

		//Inicializamos Sponsor
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);

		result.setSponsor(sponsor);
		result.setBargain(bargain);

		return result;
	}

	public Sponsorship findOne(final int sponsorshipId) {
		Sponsorship result;

		Assert.isTrue(sponsorshipId != 0);

		result = this.sponsorshipRepository.findOne(sponsorshipId);

		return result;
	}

	public Sponsorship save(final Sponsorship sponsorship) {
		Sponsor sponsor;
		Sponsorship result;
		Sponsorship saved;
		Double amount;
		Bargain updateBargain;

		//Comprobamos que el Sponsor sea el que está autenticado
		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);
		Assert.isTrue(sponsorship.getSponsor() != null && sponsorship.getSponsor().equals(sponsor));

		//Comprobamos que solo exista un sponsorship dado un sponsor y un bargain
		saved = this.findBySponsorIdAndBargainId(sponsorship.getBargain().getId());
		Assert.isTrue(saved == null || saved.getId() == sponsorship.getId());

		amount = this.sumAmountByBargainIdAndNotSponsorshipId(sponsorship.getBargain().getId(), sponsorship.getId());
		updateBargain = sponsorship.getBargain();

		//Si hay algun sponsorship aún, miro si es mayor el minimo o la resta
		if (amount != null)
			updateBargain.setPrice(Math.max((updateBargain.getOriginalPrice() - amount - sponsorship.getAmount()), updateBargain.getMinimumPrice()));

		//Si no, solamente le resto el valor de este sponsorship
		else
			updateBargain.setPrice(Math.max((updateBargain.getOriginalPrice() - sponsorship.getAmount()), updateBargain.getMinimumPrice()));

		//Guardo el bargain
		this.bargainService.saveFromSponsorship(updateBargain);

		result = this.sponsorshipRepository.save(sponsorship);

		return result;
	}

	public void delete(final Sponsorship sponsorship) {
		Sponsorship saved;
		Sponsor sponsor;
		Double amount;
		Bargain updateBargain;

		Assert.notNull(sponsorship);

		//Solo borra Sponsorship su Sponsor
		saved = this.findOne(sponsorship.getId());

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);
		Assert.isTrue(sponsor.equals(saved.getSponsor()));

		//Actualizamos el precio del bargain, quitando dicho Sponsorship
		//Cambio el precio del bargain
		amount = this.sumAmountByBargainIdAndNotSponsorshipId(saved.getBargain().getId(), saved.getId());
		updateBargain = saved.getBargain();

		//Si hay algun sponsorship aún, miro si es mayor el minimo o la resta
		if (amount != null)
			updateBargain.setPrice(Math.max((updateBargain.getOriginalPrice() - amount), updateBargain.getMinimumPrice()));
		else
			updateBargain.setPrice(updateBargain.getOriginalPrice());

		//Guardo el bargain
		this.bargainService.saveFromSponsorship(updateBargain);

		this.sponsorshipRepository.delete(saved);

	}

	//Borrar desde el bargain
	public void deleteFromBargain(final int bargainId) {
		Collection<Sponsorship> sponsorships;

		sponsorships = this.findByBargainId(bargainId);

		for (final Sponsorship sponsorship : sponsorships)
			this.sponsorshipRepository.delete(sponsorship);
	}

	public void flush() {
		this.sponsorshipRepository.flush();
	}

	public Sponsorship findBySponsorIdAndBargainId(final int bargainId) {
		Sponsorship result;
		Sponsor sponsor;

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);
		result = this.sponsorshipRepository.findBySponsorIdAndBargainId(sponsor.getId(), bargainId);

		return result;

	}
	public Page<Sponsorship> findRandomSponsorships(final int bargainId, final int page, final int size) {
		Page<Sponsorship> result;

		result = this.sponsorshipRepository.findRandomSponsorships(bargainId, this.getPageable(page, size));

		return result;

	}

	public Collection<Sponsorship> findByBargainId(final int bargainId) {
		Collection<Sponsorship> result;

		result = this.sponsorshipRepository.findByBargainId(bargainId);

		return result;

	}

	public Double sumAmountByBargainIdAndNotSponsorshipId(final int bargainId, final int sponsorshipId) {
		Double result;

		result = this.sponsorshipRepository.sumAmountByBargainIdAndNotSponsorshipId(bargainId, sponsorshipId);

		return result;

	}
	public Page<Sponsorship> findBySponsorId(final int page, final int size) {
		Page<Sponsorship> result;
		Sponsor sponsor;

		sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(sponsor);

		result = this.sponsorshipRepository.findBySponsorId(sponsor.getId(), this.getPageable(page, size));

		return result;

	}

	public Page<Sponsorship> findByBargainIdPageable(final int bargainId, final int page, final int size) {
		Page<Sponsorship> result;

		result = this.sponsorshipRepository.findByBargainIdPageable(bargainId, this.getPageable(page, size));

		return result;

	}

	public Double[] avgMinMaxStandarDesviationBannersPerSponsor() {
		Double[] result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.sponsorshipRepository.avgMinMaxStandarDesviationBannersPerSponsor();

		return result;
	}

	// Pruned object domain
	public Sponsorship reconstruct(final Sponsorship sponsorship, final BindingResult binding) {
		Sponsorship aux;
		Sponsor sponsor;

		Assert.notNull(sponsorship);

		//Estamos creando
		if (sponsorship.getId() == 0) {
			//Metemos el user
			sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(sponsor);
			sponsorship.setSponsor(sponsor);

			aux = this.create(sponsorship.getBargain());
			sponsorship.setVersion(aux.getVersion());

			//Estamos actualizando
		} else {
			//Metemos el user
			aux = this.findOne(sponsorship.getId());
			Assert.notNull(aux);

			sponsorship.setVersion(aux.getVersion());
			sponsorship.setSponsor(aux.getSponsor());
			sponsorship.setBargain(aux.getBargain());
		}

		this.validator.validate(sponsorship, binding);

		return sponsorship;
	}

	//Solamente para su sponsor
	public Sponsorship findOneToDisplayAndEdit(final int sponsorshipId) {
		Sponsorship result;

		result = this.findOne(sponsorshipId);

		Assert.isTrue(result.getSponsor().getUserAccount().equals(LoginService.getPrincipal()));

		return result;

	}
	//Auxilliary
	private Pageable getPageable(final int page, final int size) {
		Pageable result;

		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);

		return result;

	}
}
