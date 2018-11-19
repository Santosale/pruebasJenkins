
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.InternationalizationService;
import utilities.AbstractTest;
import domain.Internationalization;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListInternationalizationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private InternationalizationService	internationalizationService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. El administrador llama al método testFindBy
	 * 2.
	 * 3.
	 * 4.
	 * 5.
	 */
	@Test
	public void driverTestFindByCountryAndMessageCodeTest() {
		final Object testingData[][] = {
			{
				"admin", "findByCountryAndMessageCode", "es", "term.condition", "INFORMACIÓN", null
			}, {
				"admin", "findByCountryAndMessageCode", "en", "term.condition", "RELEVANT", null
			}, {
				"admin", "findByCountryAndMessageCode", "test", "term.condition", null, IllegalArgumentException.class
			}, {
				"admin", "findByCountryAndMessageCode", "en", "test", null, IllegalArgumentException.class
			}, {
				"admin", "findByCountryAndMessageCode", null, null, null, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1.
	 * 2.
	 * 3.
	 */
	@Test
	public void driverTestFindAvailableLanguagesByMessageCodeTest() {
		final Object testingData[][] = {
			{
				"admin", "findAvailableLanguagesByMessageCode", null, "term.condition", null, null
			}, {
				"admin", "findAvailableLanguagesByMessageCode", null, "term.condition", null, null
			}, {
				"admin", "findAvailableLanguagesByMessageCode", null, null, null, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	protected void template(final String user, final String method, final String countryCode, final String messageCode, final String value, final Class<?> expected) {
		Class<?> caught;
		Internationalization internationalization;
		Collection<String> availableLanguage;

		caught = null;
		try {

			super.authenticate(user);

			if (method.equals("findByCountryAndMessageCode")) {
				internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, messageCode);
				if (value != null)
					Assert.isTrue(internationalization.getValue().contains(value));
			} else {
				availableLanguage = this.internationalizationService.findAvailableLanguagesByMessageCode(messageCode);
				Assert.isTrue(availableLanguage.size() == 2);
			}

			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}
