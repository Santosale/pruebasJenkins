
package usecases;

import java.util.Locale;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ConfigurationService;
import services.InternationalizationService;
import utilities.AbstractTest;
import domain.Configuration;
import domain.Internationalization;
import forms.ConfigurationForm;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ConfigurationTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private ConfigurationService		configurationService;

	@Autowired
	private InternationalizationService	internationalizationService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Actualizar configuracion por admin.
	 */
	//CU:30.  2.Cambiar la configuración del sistema.

	@Test
	public void driverPositive() {

		//rol, banner, defaultAvatar, defaultImage, email, name, slogan, expected
		final Object testingData[][] = {
			{
				"admin", "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(Class<?>) testingData[i][7]);

	}

	//Pruebas
	/*
	 * 1. Actualizar configuracion por moderador. IllegalArgumentException
	 * 2. Actualizar configuracion por user. IllegalArgumentException
	 * 3. Actualizar configuracion por company. IllegalArgumentException
	 * 4. Actualizar configuracion por no autenticado. IllegalArgumentException
	 * 5. Actualizar configuracion por sponsor. IllegalArgumentException
	 */
	//CU:30.  2.Cambiar la configuración del sistema.

	@Test
	public void driverStatementConstraint() {

		//rol, banner, defaultAvatar, defaultImage, email, name, slogan, expected
		final Object testingData[][] = {
			{
				"moderator1", "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", IllegalArgumentException.class
			}, {
				"user1", "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", IllegalArgumentException.class
			}, {
				"company1", "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", IllegalArgumentException.class
			}, {
				null, "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", IllegalArgumentException.class
			}, {
				"sponsor1", "http://web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(Class<?>) testingData[i][7]);

	}

	/*
	 * 1. Actualizar configuracion banner no url. ConstraintViolationException
	 * 2. Actualizar configuracion avatar no url. ConstraintViolationException
	 * 3. Actualizar configuracion image no url. ConstraintViolationException
	 * 4. Actualizar configuracion email no válido. ConstraintViolationException
	 * 5. Actualizar configuracion name blanco. ConstraintViolationException
	 * 6. Actualizar configuracion slogan null. ConstraintViolationException
	 */

	//CU:30.  2.Cambiar la configuración del sistema.

	@Test
	public void driverDataConstraint() {

		//rol, banner, defaultAvatar, defaultImage, email, name, slogan, expected
		final Object testingData[][] = {
			{
				"admin", "web.com/banner.jpg", "http://web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", ConstraintViolationException.class
			}, {
				"admin", "web.com/banner.jpg", "web.com/avatar.jpg", "http://web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", ConstraintViolationException.class
			}, {
				"admin", "web.com/banner.jpg", "http://web.com/avatar.jpg", "web.com/image.jpg", "hola@aisscream.com", "Acme Chollos y Rifas", "La mejor web", ConstraintViolationException.class
			}, {
				"admin", "web.com/banner.jpg", "http://web.com/avatar.jpg", "web.com/image.jpg", "holaaisscream.com", "Acme Chollos y Rifas", "La mejor web", ConstraintViolationException.class
			}, {
				"admin", "web.com/banner.jpg", "http://web.com/avatar.jpg", "web.com/image.jpg", "holaa@isscream.com", " ", "La mejor web", ConstraintViolationException.class
			}, {
				"admin", "web.com/banner.jpg", "http://web.com/avatar.jpg", "web.com/image.jpg", "holaaisscream.com", "Acme Chollos y Rifas", null, ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(Class<?>) testingData[i][7]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Acceder a la configuración.
	 * 3. Editar configuración
	 */
	protected void templateEditConfiguration(final String username, final String banner, final String defaultAvatar, final String defaultImage, final String email, final String name, final String slogan, final Class<?> expected) {
		Class<?> caught;

		Configuration configuration;
		ConfigurationForm configurationForm;
		Configuration saved;
		final Locale locale;
		String code;
		Internationalization internationalization;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(username);

			//Creamos el form
			configurationForm = new ConfigurationForm();

			//Sacamos la configuration
			locale = LocaleContextHolder.getLocale();
			code = locale.getLanguage();
			configuration = this.configurationService.findUnique();
			configurationForm.setConfiguration(configuration);
			configurationForm.setWelcomeMessage(this.internationalizationService.findByCountryCodeAndMessageCode(code, "welcome.message").getValue());

			configurationForm.getConfiguration().setBanner(banner);
			configurationForm.getConfiguration().setDefaultAvatar(defaultAvatar);
			configurationForm.getConfiguration().setDefaultImage(defaultImage);
			configurationForm.getConfiguration().setEmail(email);
			configurationForm.getConfiguration().setSlogan(slogan);
			configurationForm.setWelcomeMessage(name);

			saved = this.configurationService.save(configurationForm);
			this.configurationService.flush();

			//Vemos que se haya actualizado
			Assert.isTrue(saved.getBanner().equals(banner));
			Assert.isTrue(configuration.getDefaultAvatar().equals(defaultAvatar));
			Assert.isTrue(saved.getDefaultImage().equals(defaultImage));
			Assert.isTrue(saved.getEmail().equals(email));
			Assert.isTrue(saved.getName().equals("welcome.message"));
			internationalization = this.internationalizationService.findByCountryCodeAndMessageCode(code, "welcome.message");
			Assert.isTrue(internationalization.getValue().equals(name));
			Assert.isTrue(saved.getSlogan().equals(slogan));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}
}
