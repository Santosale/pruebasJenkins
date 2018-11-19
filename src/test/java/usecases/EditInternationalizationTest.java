
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.InternationalizationService;
import utilities.AbstractTest;
import domain.Internationalization;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditInternationalizationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private InternationalizationService	internationalizationService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Un administrador puede editar un internationalization
	 */
	@Test
	public void driverPositiveTest() {
		final Object testingData[][] = {
			{
				"admin", "es", null, "term.condition", null, "asdf", null
			}
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. No se puede modificar el countryCode
	 * 2. No puede ser modificado por un customer
	 * 3. No puede ser modificado por un user
	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
			{
				"admin", "es", "en", "term.condition", null, "Tu sitio para organizar quedadas de aventura", IllegalArgumentException.class
			}, {
				"customer1", "es", null, "term.condition", null, "asdf", IllegalArgumentException.class
			}, {
				"user1", "es", null, "term.condition", null, "asdf", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Editar un internationalization. Pasos:
	 * 1. Autenticar usuario
	 * 2. Editar valores
	 */
	protected void template(final String user, final String oldCountryCode, final String countryCode, final String oldMessageCode, final String messageCode, final String value, final Class<?> expected) {
		Class<?> caught;
		Internationalization oldInternationalization, newInternationalization;
		DataBinder binder;

		caught = null;
		try {

			// 1. Autenticar usuario
			super.authenticate(user);

			oldInternationalization = this.internationalizationService.findByCountryCodeAndMessageCode(oldCountryCode, oldMessageCode);
			newInternationalization = this.copyInternationalization(oldInternationalization);

			binder = new DataBinder(newInternationalization);
			newInternationalization = this.internationalizationService.reconstruct(newInternationalization, binder.getBindingResult());

			// 2. Editar valores
			if (countryCode != null)
				newInternationalization.setCountryCode(countryCode);
			if (messageCode != null)
				newInternationalization.setMessageCode(messageCode);
			if (value != null)
				newInternationalization.setValue(value);

			this.internationalizationService.save(newInternationalization);
			this.internationalizationService.flush();

			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

	private Internationalization copyInternationalization(final Internationalization internationalization) {
		Internationalization result;

		Assert.notNull(internationalization);

		result = new Internationalization();
		result.setId(internationalization.getId());
		result.setValue(internationalization.getValue());

		return result;
	}

}
