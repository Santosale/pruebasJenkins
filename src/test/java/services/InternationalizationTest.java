
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Configuration;
import domain.Internationalization;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class InternationalizationTest extends AbstractTest {

	// Service under test ---------------------------------
	@Autowired
	private InternationalizationService	internationalizationService;
	
	// Supporting services
	@Autowired
	private ConfigurationService configurationService;

	@Test
	public void testCreate() {
		Internationalization result;
		
		result = this.internationalizationService.create();
		
		Assert.isNull(result.getCountryCode());
		Assert.isNull(result.getMessageCode());
		Assert.isNull(result.getValue());
	}
	
	@Test
	public void testSave() {
		Internationalization peopleEnglish, peopleSpanish, savedSpanish, savedEnglish;
		String spanishCountryCode, englishCountryCode, messageCode, spanishValue, englishValue;
		
		spanishCountryCode = "es";
		englishCountryCode = "en";
		messageCode = "example.people";
		englishValue = "People";
		spanishValue = "Personas";

		peopleEnglish = this.internationalizationService.create();
		peopleEnglish.setCountryCode(englishCountryCode);
		peopleEnglish.setMessageCode(messageCode);
		peopleEnglish.setValue(englishValue);
		
		peopleSpanish = this.internationalizationService.create();
		peopleSpanish.setCountryCode(spanishCountryCode);
		peopleSpanish.setMessageCode(messageCode);
		peopleSpanish.setValue(spanishValue);
		
		this.internationalizationService.save(peopleEnglish);
		this.internationalizationService.save(peopleSpanish);
		
		savedSpanish = this.internationalizationService.findByCountryCodeAndMessageCode(spanishCountryCode, messageCode);
		Assert.isTrue(savedSpanish.getCountryCode().equals(spanishCountryCode));
		Assert.isTrue(savedSpanish.getMessageCode().equals(messageCode));
		Assert.isTrue(savedSpanish.getValue().equals(spanishValue));
		
		savedEnglish = this.internationalizationService.findByCountryCodeAndMessageCode(englishCountryCode, messageCode);
		Assert.isTrue(savedEnglish.getCountryCode().equals(englishCountryCode));
		Assert.isTrue(savedEnglish.getMessageCode().equals(messageCode));
		Assert.isTrue(savedEnglish.getValue().equals(englishValue));
	}

	@Test
	public void tesfindByCountryCodeAndMessageCode() {
		String countryCode;
		Collection<Configuration> configurations;
		Configuration configuration;
		Internationalization internationalization;

		configuration = null;
		configurations = this.configurationService.findAll();

		for (final Configuration c : configurations) {
			configuration = c;
			break;
		}

		countryCode = "es";

		internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, configuration.getWelcomeMessage());
		Assert.notNull(internationalization);
		
		Assert.notNull(internationalization.getValue());
		Assert.isTrue(internationalization.getCountryCode().equals(countryCode));
		
		countryCode = "en";

		internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, configuration.getWelcomeMessage());
		Assert.notNull(internationalization);
		
		Assert.notNull(internationalization.getValue());
		Assert.isTrue(internationalization.getCountryCode().equals(countryCode));
	}

}
