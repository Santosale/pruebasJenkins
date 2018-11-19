
package usecases;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.NewspaperService;
import services.UserService;
import utilities.AbstractTest;
import domain.Newspaper;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private UserService			userService;


	/*
	 * Test
	 * 1. Creamos un newspaper inicializando todos sus parámetros (no salta excepción)
	 * 2. Creamos un newspaper inicializando todos sus parámetros menos la imagen que es nulo (no salta excepción)
	 * 3. Creamos un newspaper inicializando todos sus parámetros poniéndolo a público (no salta excepción)
	 * 4. Creamos un newspaper inicializando todos sus parámetros poniéndolo a público y con la imagen a nulo (no salta excepción)
	 * 5. Creamos un newspaper inicializando todos sus parámetros sobreescribiendo el creador por el propio logeado (no salta excepción)
	 * 6. Creamos un newspaper usando palabras taboo en el título (no salta excepción)
	 * 7. Creamos un newspaper usando palabras taboo en la descripción (no salta excepción)
	 * 
	 * Requisitos
	 * C.6.1: An actor who is authenticated as a user must be able to create a newspaper. A user who has created a newspaper is commonly referred to
	 * as a publisher
	 */
	@Test()
	public void testCreatePositive() {
		final Object testingData[][] = {
			{
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", "http://www.imagenes.com/imagen1", true, true, null, false, null
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", null, true, true, null, false, null
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", "http://www.imagenes.com/imagen1", false, true, null, false, null
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", null, false, true, null, false, null
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", "http://www.imagenes.com/imagen1", true, true, "user1", false, null
			}, {
				"user", "user1", "sexo", "12/05/2020", "Descripción 1", "http://www.imagenes.com/imagen1", true, true, null, true, null
			}, {
				"user", "user1", "Título", "12/05/2020", "sexo", "http://www.imagenes.com/imagen1", true, true, null, true, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Boolean) testingData[i][6], (Boolean) testingData[i][7],
					(String) testingData[i][8], (Boolean) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Intentamos crear un newspaper poniendo la fecha a nulo (salta un IllegalArgumentException)
	 * 2. Intentamos crear un newspaper poniendo el título a vacío (salta un ConstraintViolationException)
	 * 3. Intentamos crear un newspaper poniendo la descripción a vacío (salta un ConstraintViolationException)
	 * 4. Intentamos crear un newspaper poniendo un email con formato erroneo (salta un ConstraintViolationException)
	 * 5. Intentamos crear un newspaper poniendo la descripción y el título a vacío (salta un ConstraintViolationException)
	 * 6. Intentamos crear un newspaper poniendo el isPublished a false (salta un IllegalArgumentException)
	 * 7. Intentamos crear un newspaper logeados como customer (salta un IllegalArgumentException)
	 * 8. Intentamos crear un newspaper logeados como admin (salta un IllegalArgumentException)
	 * 9. Intentamos crear un newspaper sin estar logeado (salta un IllegalArgumentException)
	 * 10. Intentamos crear un newspaper con el user1 y asignárselo al user2 (salta un IllegalArgumentException)
	 * 11. Intentamos crear un newspaper poniendo una fecha en el pasado (salta un IllegalArgumentException)
	 */
	@Test()
	public void testCreateNegative() {
		final Object testingData[][] = {
			{
				"user", "user1", "Titulo 1", null, "Descripción", "http://www.imagenes.com/imagen1", true, true, null, false, IllegalArgumentException.class
			}, {
				"user", "user1", "", "12/05/2020", "Descripción", "http://www.imagenes.com/imagen1", true, true, null, false, ConstraintViolationException.class
			}, {
				"user", "user1", "Título", "12/05/2020", "", "http://www.imagenes.com/imagen1", true, true, null, false, ConstraintViolationException.class
			}, {
				"user", "user1", "Título", "12/05/2020", "Descripción", "yeynyew", true, true, null, false, ConstraintViolationException.class
			}, {
				"user", "user1", "", "12/05/2020", "", null, true, true, null, false, ConstraintViolationException.class
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción", "http://www.imagenes.com/imagen1", true, false, null, false, IllegalArgumentException.class
			}, {
				"customer", "customer1", "Titulo 1", "12/05/2020", "Descripción", "http://www.imagenes.com/imagen1", true, true, null, false, IllegalArgumentException.class
			}, {
				"admin", "admin", "Titulo 1", "12/05/2020", "Descripción", "http://www.imagenes.com/imagen1", true, true, null, false, IllegalArgumentException.class
			}, {
				null, null, "Titulo 1", "12/05/2020", "Descripción", "http://www.imagenes.com/imagen1", true, true, null, false, IllegalArgumentException.class
			}, {
				"user", "user1", "Titulo 1", "12/05/2020", "Descripción 1", "http://www.imagenes.com/imagen1", true, true, "user2", false, IllegalArgumentException.class
			}, {
				"user", "user1", "Titulo 1", "12/05/2017", "Descripción 1", "http://www.imagenes.com/imagen1", true, true, null, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Boolean) testingData[i][6], (Boolean) testingData[i][7],
					(String) testingData[i][8], (Boolean) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String title, final String publicationDate, final String description, final String picture, final Boolean isPrivate, final Boolean isPublished, final String userBean,
		final Boolean hasTabooWords, final Class<?> expected) {
		Class<?> caught;
		Newspaper newspaper;
		Newspaper saved;
		final int userId;
		final User userEntity;
		DataBinder binder;
		final Newspaper newspaperReconstruct;
		SimpleDateFormat format;
		Date date;

		date = null;
		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			newspaper = this.newspaperService.create(); //Creamos el newspaper
			newspaper.setTitle(title); //Editamos el título
			if (publicationDate != null) {
				format = new SimpleDateFormat("dd/MM/yyyy");
				date = format.parse(publicationDate); //Si el momento no es nulo creamos el momento
			}
			newspaper.setPublicationDate(date); //Le modificamos el momento
			newspaper.setDescription(description);//Modificamos la descripción
			newspaper.setPicture(picture); //Modificamos el dibujo
			newspaper.setIsPrivate(isPrivate); //Modificamos si es privado
			newspaper.setIsPublished(isPublished); //Le modificamos el isPublished para probar hackeos

			if (userBean != null) {
				userId = super.getEntityId(userBean);
				userEntity = this.userService.findOne(userId);
				newspaper.setPublisher(userEntity); //Le modificamos manualmente el user para probar hackeos

			}

			binder = new DataBinder(newspaper);
			newspaperReconstruct = this.newspaperService.reconstruct(newspaper, binder.getBindingResult());
			saved = this.newspaperService.save(newspaperReconstruct); //Guardamos el newspaper
			super.flushTransaction();

			Assert.isTrue(this.newspaperService.findAll().contains(saved)); //Miramos si están entre todos los newspaper de la BD

			if (hasTabooWords == true)
				Assert.isTrue(saved.getHasTaboo() == true); //Si hemos añadido palabras taboo vemos que este atributo esté a true
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
