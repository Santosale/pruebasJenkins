
package services;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ConfigurationRepository;
import security.Authority;
import security.LoginService;
import domain.Configuration;
import domain.Internationalization;
import forms.ConfigurationForm;

@Service
@Transactional
public class ConfigurationService {

	// Managed repository
	@Autowired
	private ConfigurationRepository		configurationRepository;

	// Other Services
	@Autowired
	private Validator					validator;

	@Autowired
	private InternationalizationService	internationalizationService;


	// Constructor
	public ConfigurationService() {
		super();
	}

	// Simple CRUD methods
	public Configuration save(final ConfigurationForm configurationForm) {
		Configuration result;
		Authority authority;
		Internationalization internationalization;
		final Locale locale;
		String code;

		locale = LocaleContextHolder.getLocale();
		code = locale.getLanguage();

		//Si no está vacío no te deja persistir otra, por lo que comprueba que la que pasas es la que está persistida y no es otra
		if (!this.configurationRepository.findAll().isEmpty())
			Assert.notNull(this.configurationRepository.findOne(configurationForm.getConfiguration().getId()));

		Assert.notNull(configurationForm.getConfiguration(), "configuration.not.null");

		// Solo puede ser modificado por el admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		//Guardamos el name internacionalizado
		internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(code, "welcome.message");
		internationalization.setValue(configurationForm.getWelcomeMessage());
		this.internationalizationService.save(internationalization);

		result = this.configurationRepository.save(configurationForm.getConfiguration());

		return result;
	}

	//Other business methods
	public Configuration findUnique() {
		Configuration result;
		Authority authority;

		// Solo puede ser modificado por el admin
		authority = new Authority();
		authority.setAuthority("ADMIN");
		if (LoginService.isAuthenticated())
			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

		result = this.configurationRepository.findUnique();

		return result;
	}

	public String findName() {
		String result;

		result = this.configurationRepository.findName();

		return result;
	}

	public String findSlogan() {
		String result;

		result = this.configurationRepository.findSlogan();

		return result;
	}

	public String findEmail() {
		String result;

		result = this.configurationRepository.findEmail();

		return result;
	}

	public String findBanner() {
		String result;

		result = this.configurationRepository.findBanner();

		return result;
	}

	public String findDefaultAvatar() {
		String result;

		result = this.configurationRepository.findDefaultAvatar();

		return result;
	}

	public String findDefaultImage() {
		String result;

		result = this.configurationRepository.findDefaultImage();

		return result;
	}

	public void flush() {
		this.configurationRepository.flush();
	}

	// Reconstruct pruned object
	public ConfigurationForm reconstruct(final ConfigurationForm configurationForm, final BindingResult binding) {
		Configuration aux;

		aux = this.configurationRepository.findOne(configurationForm.getConfiguration().getId());
		Assert.notNull(aux);

		configurationForm.getConfiguration().setVersion(aux.getVersion());
		configurationForm.getConfiguration().setName(aux.getName());

		this.validator.validate(configurationForm, binding);

		return configurationForm;
	}

}
