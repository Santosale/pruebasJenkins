
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.Authority;
import security.LoginService;
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
	 * 1. Probamos listar los términos y condiciones en español
	 * 2. Probamos listar los términos y condiciones en inglés
	 * 
	 * Requisitos:
	 * 
	 * Se prueba la edición de términos y condiciones.
	 */
	@Test
	public void positiveFindByCountryCodeAndMessageCodeTest() {
		final Object testingData[][] = {
			{
				"admin", "admin", "es", "term.condition", "Cambio de Términos", null
			}, {
				"admin", "admin", "en", "term.condition", "Change of Terms", null
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
	 * 
	 * 1. Sólo un administrador puede listar los términos y condiciones
	 * 2. Sólo un administrador puede listar los términos y condiciones
	 * 3. Debe estar logueado como un administrador
	 * 
	 * Requisitos:
	 * 
	 * Se prueba la edición de términos y condiciones.
	 */
	@Test
	public void negativeFindByCountryCodeAndMessageCodeTest() {
		final Object testingData[][] = {
			{
				"user1", "user1", "es", "term.condition", "Cambio de Términos", IllegalArgumentException.class
			}, {
				"manager1", "manager1", "en", "term.condition", "Change of Terms", IllegalArgumentException.class
			}, {
				null, "manager1", "en", "term.condition", "Change of Terms", IllegalArgumentException.class
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

	/*
	 * Pasos:
	 * 
	 * 1. Nos autentificamos como admin
	 * 2. Tomamos el id y la entidad de admin
	 * 3. Creamos una copia del termCondition para que no se guarde solo con un set
	 * 4. Obtenemos el termCondition correspondiente al countryCode y messageCode dado
	 * 5. Cambiamos su value
	 * 6. Guardamos el termCondition copiado con el nuevo value
	 * 7. Comprobamos que se ha cambiado
	 * 8. Nos desautentificamos
	 */
	protected void template(final String user, final String username, final String countryCode, final String messageCode, final String value, final Class<?> expected) {
		Class<?> caught;
		Internationalization termCondition, termConditionEntity;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("ADMIN");

		caught = null;
		try {
			super.authenticate(user);

			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

			termCondition = this.internationalizationService.findByCountryCodeAndMessageCode(countryCode, messageCode);

			termConditionEntity = this.copyTermCondition(termCondition);
			termConditionEntity.setValue(value);

			this.internationalizationService.save(termConditionEntity);

			Assert.isTrue(termCondition.getValue().equals(value));

			super.flushTransaction();
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
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
