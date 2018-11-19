
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
public class ListTermConditionTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private InternationalizationService	internationalizationService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos listar los t�rminos y condiciones en espa�ol
	 * 2. Probamos listar los t�rminos y condiciones en ingl�s
	 * 3. Probamos listar los t�rminos y condiciones en espa�ol, sin autenticar
	 * 4. Probamos listar los t�rminos y condiciones en ingl�s, sin autenticar
	 */

	//CU: 35.	Los t�rminos y condiciones deben indicar expl�citamente las razones por las que el comportamiento de un usuario se considera inapropiado y por qu� puede ser borrado del sistema por un moderador.
	@Test
	public void positiveFindByCountryCodeAndMessageCodeTest() {

		final Object testingData[][] = {
			{
				"admin", "es", "term.condition", null
			}, {
				"admin", "en", "term.condition", null
			}, {
				null, "es", "term.condition", null
			}, {
				null, "en", "term.condition", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * Pruebas:
	 * 
	 * 1. Listamos terminos y condiciones en otro idioma con admin
	 * 2. Listamos terminos y condiciones en otro idioma sin autenticar
	 * 
	 * Requisitos:
	 * 
	 * Se desea probar el listado correcto de los t�rminos y condiciones.
	 */

	//CU: //CU: 35.	Los t�rminos y condiciones deben indicar expl�citamente las razones por las que el comportamiento de un usuario se considera inapropiado y por qu� puede ser borrado del sistema por un moderador.
	@Test
	public void negativeFindByCountryCodeAndMessageCodeTest() {

		final Object testingData[][] = {
			{
				"admin", "it", "term.condition", IllegalArgumentException.class
			}, {
				null, "it", "term.condition", IllegalArgumentException.class
			},

		};

		for (int i = 0; i < testingData.length; i++)

			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}
	// Ancillary methods ------------------------------------------------------

	/*
	 * Pasos:
	 * 
	 * 1. Nos autentificamos
	 * 2. Listamos t�rminos y condiciones
	 */
	protected void template(final String username, final String countryCode, final String messageCode, final Class<?> expected) {
		Class<?> caught;
		Internationalization termCondition;

		caught = null;
		try {
			super.startTransaction();
			super.authenticate(username);

			termCondition = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, messageCode);

			Assert.notNull(termCondition);

			super.authenticate(null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}
		super.checkExceptions(expected, caught);
	}

}
