
package usecases;

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
public class EditTermConditionTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private InternationalizationService	internationalizationService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Editamos términos y condiciones en español
	 * 2. Editamos términos y condiciones en inglés
	 */

	//CU: 35.	Los términos y condiciones deben indicar explícitamente las razones por las que el comportamiento de un usuario se considera inapropiado y por qué puede ser borrado del sistema por un moderador.
	@Test
	public void positiveFindByCountryCodeAndMessageCodeTest() {
		final Object testingData[][] = {
			{
				"admin", "es", "term.condition", "Cambio de Términos", true, "es", null
			}, {
				"admin", "en", "term.condition", "Change of Terms", true, "en", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Boolean) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	/*
	 * Pruebas:
	 * 
	 * 1. Editar el messageCode. IllegalArgumentException
	 * 2. Editar el countryCode. IllegalArgumentException
	 */

	////CU: 35.	Los términos y condiciones deben indicar explícitamente las razones por las que el comportamiento de un usuario se considera inapropiado y por qué puede ser borrado del sistema por un moderador.
	@Test
	public void negativeFindByCountryCodeAndMessageCodeTest() {
		final Object testingData[][] = {
			{
				"admin", "es", "term.condition", "Cambio de Términos", true, "it", IllegalArgumentException.class
			}, {
				"admin", "en", "term.condition", "Change of Terms", false, "term", IllegalArgumentException.class
			},

		};
		for (int i = 0; i < testingData.length; i++)

			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Boolean) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}
	// Ancillary methods ------------------------------------------------------

	/*
	 * Pasos:
	 * 
	 * 1. Nos autentificamos como admin
	 * 2. Listar terminos y condiciones
	 * 3. Editar terminos y condiciones
	 */
	protected void template(final String username, final String countryCode, final String messageCode, final String value, final Boolean editCountryCode, final String newParam, final Class<?> expected) {
		Class<?> caught;
		Internationalization termCondition, termConditionEntity;

		caught = null;
		try {
			super.startTransaction();
			super.authenticate(username);

			termCondition = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, messageCode);

			termConditionEntity = this.copyTermCondition(termCondition);

			if (editCountryCode)
				termConditionEntity.setCountryCode(newParam);
			else
				termConditionEntity.setMessageCode(newParam);

			termConditionEntity.setValue(value);

			this.internationalizationService.save(termConditionEntity);

			Assert.isTrue(termCondition.getValue().equals(value));

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}
		super.checkExceptions(expected, caught);
	}

	private Internationalization copyTermCondition(final Internationalization internationalization) {
		Internationalization result;

		result = new Internationalization();

		result.setId(internationalization.getId());
		result.setVersion(internationalization.getVersion());
		result.setCountryCode(internationalization.getCountryCode());
		result.setMessageCode(internationalization.getMessageCode());
		result.setValue(internationalization.getValue());

		return result;
	}

}
