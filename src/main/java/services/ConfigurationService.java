
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ConfigurationRepository;
import security.Authority;
import security.LoginService;
import domain.Configuration;
import domain.Internationalization;

@Service
@Transactional
public class ConfigurationService {

	// Managed repository
	@Autowired
	private ConfigurationRepository		configurationRepository;

	// Other Services
	@Autowired
	private InternationalizationService	internationalizationService;


	// Constructor
	public ConfigurationService() {
		super();
	}

	// Simple CRUD methods

	public Collection<Configuration> findAll() {
		Collection<Configuration> result;

		result = this.configurationRepository.findAll();

		return result;
	}
	public Configuration findOne(final int configurationId) {
		Configuration result;

		Assert.isTrue(configurationId != 0);

		result = this.configurationRepository.findOne(configurationId);

		return result;
	}
	public Configuration save(final Configuration configuration) {
		Configuration result;
		Authority authority;

		if (!this.findAll().isEmpty()) //Si no está vacío no te deja persistir otra, por lo que comprueba que la que pasas es la que está persistida y no es otra
			Assert.notNull(this.findOne(configuration.getId()), "configuration.notCreateAnother");

		Assert.notNull(configuration, "configuration.not.null");

		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority), "configuration.authority.administrator");

		result = this.configurationRepository.save(configuration);

		return result;
	}

	//Other business methods
	public Collection<String> findSpamWords() {
		Collection<String> result;

		result = this.configurationRepository.findSpamWords();

		return result;

	}
	public Double findVat() {
		Double result;

		result = this.configurationRepository.findVat();

		return result;
	}

	public String findCompanyName() {
		String result;

		result = this.configurationRepository.findCompanyName();

		return result;
	}

	public String findBanner() {
		String result;

		result = this.configurationRepository.findBanner();

		return result;
	}

	public String findWelcomeMessage(final String countryCode) {
		String result;
		Internationalization internationalizationWelcomeMessage;
		String welcomeMessageCode;

		welcomeMessageCode = this.configurationRepository.findWelcomeMessage();
		internationalizationWelcomeMessage = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, welcomeMessageCode);
		result = internationalizationWelcomeMessage.getValue();

		return result;
	}

	public String findCountryCode() {
		String result;

		result = this.configurationRepository.findCountryCode();

		return result;
	}

	public Double findCachedTime() {
		Double result;

		result = this.configurationRepository.findCachedTime();

		return result;
	}

	public Integer findFinderNumber() {
		Integer result;

		result = this.configurationRepository.findFinderNumber();

		return result;
	}

	public boolean checkSpamWords(final Configuration configuration) {
		Collection<String> spamWords;
		boolean result;

		result = false;
		spamWords = this.findSpamWords();

		for (final String spamWord : spamWords) {
			result = configuration.getCompanyName() != null && configuration.getCompanyName().toLowerCase().contains(spamWord);
			if (result == true)
				break;
		}
		return result;
	}

}
