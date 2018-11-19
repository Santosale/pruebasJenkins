
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

import repositories.AdvertisementRepository;
import security.Authority;
import security.LoginService;
import domain.Advertisement;
import domain.Agent;

@Service
@Transactional
public class AdvertisementService {

	// Managed repository
	@Autowired
	private AdvertisementRepository	advertisementRepository;

	// Services
	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private AgentService			agentService;

	@Autowired
	private Validator				validator;


	// Constructor
	public AdvertisementService() {
		super();
	}

	// Simple CRUD methods----------
	public Advertisement create() {
		Advertisement result;
		Agent agent;

		result = new Advertisement();

		//Inicializamos agent
		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);

		result.setAgent(agent);
		result.setHasTaboo(false);

		return result;
	}

	public Collection<Advertisement> findAll() {
		Collection<Advertisement> result;

		result = this.advertisementRepository.findAll();

		return result;
	}

	public Advertisement findOne(final int advertisementId) {
		Advertisement result;

		Assert.isTrue(advertisementId != 0);

		result = this.advertisementRepository.findOne(advertisementId);

		return result;
	}

	public Advertisement save(final Advertisement advertisement) {
		Agent agent;
		Advertisement result;
		Calendar calendar;

		calendar = Calendar.getInstance();

		//CreditCard no caducada
		Assert.notNull(advertisement.getCreditCard());
		//Caduca este año
		if (calendar.get(Calendar.YEAR) % 100 == advertisement.getCreditCard().getExpirationYear())
			Assert.isTrue(((advertisement.getCreditCard().getExpirationMonth()) - (calendar.get(Calendar.MONTH) + 1)) >= 1);

		//Caduca año próximo
		//		else if ((calendar.get(Calendar.YEAR) % 100) + 1 == advertisement.getCreditCard().getExpirationYear())
		//			Assert.isTrue(advertisement.getCreditCard().getExpirationMonth() >= 2 || calendar.get(Calendar.MONTH) + 1 <= 11);

		//Caduca más tarde
		else
			Assert.isTrue(calendar.get(Calendar.YEAR) % 100 < advertisement.getCreditCard().getExpirationYear());

		//Comprobamos que el agent sea el que está autenticado
		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);

		Assert.isTrue(advertisement.getAgent() != null && advertisement.getAgent().equals(agent));

		//Actualizamos las tabooWors
		advertisement.setHasTaboo(this.checkTabooWords(advertisement));

		result = this.advertisementRepository.save(advertisement);

		return result;
	}

	public void delete(final Advertisement advertisement) {
		Advertisement saved;
		Agent agent;

		Assert.notNull(advertisement);

		//Solo borra advertisement su agent
		saved = this.findOne(advertisement.getId());

		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);
		Assert.isTrue(agent.equals(saved.getAgent()));

		//Actualizamos los newspaper, quitando dicho advertisement
		this.newspaperService.saveFromAdvertisement(saved);

		this.advertisementRepository.delete(saved);

	}

	//Metodo para borrar el admin
	public void deleteInappropiate(final Advertisement advertisement) {
		Authority authority;

		Assert.notNull(advertisement);

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		//Actualizamos los newspaper, quitando dicho advertisement
		this.newspaperService.saveFromAdvertisement(advertisement);

		this.advertisementRepository.delete(advertisement);

	}

	public void flush() {
		this.advertisementRepository.flush();
	}

	public Advertisement findRandomAdvertisement(final int newspaperId) {
		Advertisement result;
		Page<Advertisement> advertisements;

		Assert.isTrue(newspaperId != 0);

		advertisements = this.advertisementRepository.findRandomAdvertisement(newspaperId, new PageRequest(0, 1));

		result = null;
		if (!advertisements.getContent().isEmpty())
			result = advertisements.getContent().get(0);

		return result;
	}

	public boolean checkTabooWords(final Advertisement advertisement) {
		Collection<String> tabooWords;
		boolean result;

		result = false;
		tabooWords = this.configurationService.findTabooWords();

		for (final String tabooWord : tabooWords) {
			result = advertisement.getTitle() != null && advertisement.getTitle().toLowerCase().contains(tabooWord.toLowerCase());
			if (result == true)
				break;
		}

		return result;
	}

	public Double ratioHaveTabooWords() {
		Double result;

		result = this.advertisementRepository.ratioHaveTabooWords();

		return result;
	}

	public Double ratioNewspaperHaveAtLeastOneAdvertisementVSNoAdvertisement() {
		Double result;

		result = this.advertisementRepository.ratioNewspaperHaveAtLeastOneAdvertisementVSNoAdvertisement();

		return result;
	}

	public Page<Advertisement> findTaboos(final int page, final int size) {
		Page<Advertisement> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.advertisementRepository.findTaboos(this.getPageable(page, size));

		return result;

	}

	public Page<Advertisement> findAllPaginated(final int page, final int size) {
		Page<Advertisement> result;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.isAuthenticated());
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
		result = this.advertisementRepository.findAllPaginated(this.getPageable(page, size));

		return result;

	}

	public Page<Advertisement> findByAgentId(final int page, final int size) {
		Page<Advertisement> result;
		Agent agent;

		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);

		result = this.advertisementRepository.findByAgentId(agent.getId(), this.getPageable(page, size));

		return result;

	}

	public Page<Advertisement> findByAgentIdUnlinkToNewspaper(final int newspaperId, final int page, final int size) {
		Page<Advertisement> result;
		Agent agent;

		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);

		result = this.advertisementRepository.findByAgentIdUnLinkToNewspaper(agent.getId(), newspaperId, this.getPageable(page, size));

		return result;

	}

	public Page<Advertisement> findByAgentIdLinkToNewspaper(final int newspaperId, final int page, final int size) {
		Page<Advertisement> result;
		Agent agent;

		agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(agent);

		result = this.advertisementRepository.findByAgentIdLinkToNewspaper(agent.getId(), newspaperId, this.getPageable(page, size));

		return result;

	}
	//Actualizar la búsqueda de forma manual
	public void findTaboos() {
		final Collection<Advertisement> advertisements;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		advertisements = this.findAll();

		for (final Advertisement advertisement : advertisements)
			if (advertisement.getHasTaboo() == false && this.checkTabooWords(advertisement) == true) {
				advertisement.setHasTaboo(true);
				this.advertisementRepository.save(advertisement);
			} else if (advertisement.getHasTaboo() == true && this.checkTabooWords(advertisement) == false) {
				advertisement.setHasTaboo(false);
				this.advertisementRepository.save(advertisement);
			}

	}

	// Pruned object domain
	public Advertisement reconstruct(final Advertisement advertisement, final BindingResult binding) {
		Advertisement aux;
		Agent agent;

		Assert.notNull(advertisement);

		//Estamos creando
		if (advertisement.getId() == 0) {
			//Metemos el user
			agent = this.agentService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(agent);
			advertisement.setAgent(agent);

			aux = this.create();
			advertisement.setVersion(aux.getVersion());

			//Estamos actualizando
		} else {
			//Metemos el user
			aux = this.findOne(advertisement.getId());

			advertisement.setVersion(aux.getVersion());
			advertisement.setAgent(aux.getAgent());
			advertisement.setHasTaboo(aux.getHasTaboo());
		}

		this.validator.validate(advertisement, binding);

		return advertisement;
	}

	public Advertisement findOneToDisplay(final int advertisementId) {
		Advertisement result;
		Authority authority;

		result = this.findOne(advertisementId);

		authority = new Authority();
		authority.setAuthority("ADMIN");

		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority) || result.getAgent().getUserAccount().equals(LoginService.getPrincipal()));

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
