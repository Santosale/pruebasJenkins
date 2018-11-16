package services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Internationalization;

import repositories.InternationalizationRepository;


@Service
@Transactional
public class InternationalizationService {

	// Managed repository
	@Autowired
	private InternationalizationRepository internationalizationRepository;
	
	// Supporting services
	
	// Constructor
	public InternationalizationService() {
		super();
	}
	
	// Simple CRUD methods
	public Internationalization create(){
		Internationalization result;
		
		result = new Internationalization();

		return result;
	}
	
	public Internationalization save(final Internationalization internationalization) {
		Internationalization result;
		
		Assert.notNull(internationalization);
		
		result = this.internationalizationRepository.save(internationalization);
		
		return result;
	}
	
	// Other methods
	public Internationalization findByCountryCodeAndMessageCode(final String countryCode, final String messageCode) {
		Internationalization result;
		
		Assert.notNull(countryCode);
		Assert.notNull(messageCode);
		
		result = this.internationalizationRepository.findByCountryCodeMessageCode(countryCode, messageCode);
		
		return result;
	}
}
