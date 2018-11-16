
package services;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Configuration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ConfigurationServiceTest extends AbstractTest {

	//Service under test----------------

	@Autowired
	private ConfigurationService	configurationService;


	//Test-------------------------

	@Test
	public void testFindAll() {
		Collection<Configuration> allConfigurations;

		super.authenticate("admin");

		allConfigurations = this.configurationService.findAll();

		Assert.isTrue(allConfigurations.size() == 1);

		super.authenticate(null);

	}

	@Test
	public void testFindOne() {
		Collection<Configuration> allConfigurations;
		Configuration configuration;
		Configuration saved;

		super.authenticate("admin");
		configuration = null;
		allConfigurations = this.configurationService.findAll();
		for (final Configuration conf : allConfigurations) {
			configuration = conf;
			break;
		}

		saved = this.configurationService.findOne(configuration.getId());
		Assert.isTrue(saved.equals(configuration));

		super.authenticate(null);

	}
	//Modificamos una configuration
	@Test
	public void testSave() {
		Collection<Configuration> allConfigurations;
		Configuration configuration;
		Configuration saved;
		Configuration configurationSaved;

		super.authenticate("admin");
		configuration = null;
		allConfigurations = this.configurationService.findAll();
		for (final Configuration conf : allConfigurations) {
			configuration = conf;
			break;
		}

		saved = this.configurationService.findOne(configuration.getId());
		saved.setVat(22);
		configurationSaved = this.configurationService.save(saved);

		Assert.isTrue(this.configurationService.findAll().contains(configurationSaved));
		Assert.isTrue(configurationSaved.getVat() == 22.0);

	}
	//Lo intenta guaradar alguien que no es un administrador
	@Test
	public void testSave2() {
		Collection<Configuration> allConfigurations;
		Configuration configuration;
		Configuration saved;
		Configuration configurationSaved;

		super.authenticate("admin");
		configuration = null;
		allConfigurations = this.configurationService.findAll();
		for (final Configuration conf : allConfigurations) {
			configuration = conf;
			break;
		}

		saved = this.configurationService.findOne(configuration.getId());
		saved.setVat(22);
		super.authenticate(null);
		super.authenticate("manager1");
		try {
			configurationSaved = this.configurationService.save(saved);
			Assert.isNull(configurationSaved);
			Assert.isTrue(this.configurationService.findAll().contains(configurationSaved));
			Assert.isTrue(configurationSaved.getVat() == 22.0);
			super.authenticate(null);
		} catch (final IllegalArgumentException e) {
			super.authenticate(null);
		}

	}

	@Test
	public void testFindSpamWords() {
		Collection<String> spamWords;

		spamWords = this.configurationService.findSpamWords();

		Assert.isTrue(spamWords.size() == 4);
	}

	@Test
	public void testFindVat() {
		Double vat;

		vat = this.configurationService.findVat();

		Assert.isTrue(vat.equals(21.0));
	}
	@Test
	public void testFindCompanyName() {
		String companyName;

		companyName = this.configurationService.findCompanyName();

		Assert.isTrue(companyName.equals("Tanzanika"));
	}

	@Test
	public void testFindBanner() {
		String banner;

		banner = this.configurationService.findBanner();

		Assert.notNull(banner);
	}

	@Test
	public void testFindCountryCode() {
		String countryCode;

		countryCode = this.configurationService.findCountryCode();

		Assert.isTrue(countryCode.equals("+34"));
	}

	@Test
	public void testWelcomeMessage() {
		String message;
		String countryCode;

		countryCode = "es";

		message = this.configurationService.findWelcomeMessage(countryCode);

		Assert.notNull(message);
	}

	@Test
	public void testFindCachedTime() {
		Double time;

		time = this.configurationService.findCachedTime();

		Assert.isTrue(time.equals(1.0));
	}

	@Test
	public void testFindFinderNumber() {
		Integer number;

		number = this.configurationService.findFinderNumber();

		Assert.isTrue(number.equals(10));
	}

}
