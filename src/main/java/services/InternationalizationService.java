
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.InternationalizationRepository;
import security.Authority;
import security.LoginService;
import domain.Internationalization;

@Service
@Transactional
public class InternationalizationService {

	// Managed repository
	@Autowired
	private InternationalizationRepository	internationalizationRepository;

	// Supporting services
	@Autowired
	private Validator						validator;


	// Constructor
	public InternationalizationService() {
		super();
	}

	// Simple CRUD methods
	public Internationalization create() {
		Internationalization result;

		result = new Internationalization();

		return result;
	}

	public Internationalization save(final Internationalization internationalization) {
		Internationalization result, saved;
		Authority authority;
		
		Assert.notNull(internationalization);
		
		// No puede cambiar ni el countryCode ni el messageCode
		if(internationalization.getId() != 0){
			saved = this.internationalizationRepository.findOne(internationalization.getId());
			Assert.isTrue(internationalization.getMessageCode().equals(saved.getMessageCode()));
			Assert.isTrue(internationalization.getCountryCode().equals(saved.getCountryCode()));
		}
		
		// Debe ser un admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));
	
		result = this.internationalizationRepository.save(internationalization);

		return result;
	}

	// Other methods
	public Internationalization findByCountryCodeAndMessageCode(final String countryCode, final String messageCode) {
		Internationalization result;
		Collection<String> availableLanguages;
		
		Assert.notNull(countryCode);
		Assert.notNull(messageCode);
		
		availableLanguages = this.findAvailableLanguagesByMessageCode(messageCode);
		Assert.notNull(availableLanguages);
		Assert.isTrue(availableLanguages.contains(countryCode));

		result = this.internationalizationRepository.findByCountryCodeMessageCode(countryCode, messageCode);

		return result;
	}
	
	public Collection<String> findAvailableLanguagesByMessageCode(final String messageCode) {
		Collection<String> result;
		
		Assert.notNull(messageCode);
		
		result = this.internationalizationRepository.findAvailableLanguagesByMessageCode(messageCode);
		
		return result;
	}
	
	public void flush(){
		this.internationalizationRepository.flush();
	}
	

	public Internationalization reconstruct(final Internationalization internationalization, final BindingResult binding) {
		Internationalization result;

		result = this.internationalizationRepository.findOne(internationalization.getId());
		Assert.notNull(result);

		internationalization.setVersion(result.getVersion());
		internationalization.setCountryCode(result.getCountryCode());
		internationalization.setMessageCode(result.getMessageCode());

		this.validator.validate(internationalization, binding);

		return internationalization;
	}
}
