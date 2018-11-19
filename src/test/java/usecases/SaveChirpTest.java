package usecases;

import java.util.Calendar;
import java.util.Date;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Chirp;
import domain.User;

import security.LoginService;
import services.ChirpService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveChirpTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ChirpService		chirpService;

	@Autowired
	private UserService			userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como USER trata de crear un chirp con normalidad
	 * 		2. Un actor autenticado como USER trata de crear un chirp poniendo una taboo word en el titulo
	 * 		3. Un actor autenticado como USER trata de crear un chirp poniendo una taboo word en la descripción
	 * 		4. Un actor autenticado como USER trata de crear un chirp con taboo word.
	 * 
	 * Requisitos:
	 * 		15. A user may post a chirp. For every chirp, the system must store the moment, a title, and a description. The list or chirps are considered a part of the profile of a user.
	 * 
	 */
	@Test
	public void driverPositiveTest() {
		Calendar calendar;
		Date moment;
		
		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		moment = calendar.getTime();
		
		final Object testingData[][] = {
			{
				"user1", moment, "This is a chirp", "This is a chirp description", false, null
			}, {
				"user2", moment, "This is sex", "This is a a description", true, null
			}, {
				"user3", moment, "This is a chirp", "This is a sex description", true, null
			}, {
				"user3", moment, "This is sex", "This is a sex description", true, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (Date) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un actor autenticado como USER trata de crear un chirp dejando en blanco el campo title
	 * 		2. Un actor autenticado como USER trata de crear un chirp dejando en blanco el campo description
	 * 		3. Un administrador trata de crear un chirp
	 * 		4. Un customer trata de crear un chirp
	 * 		5. Un actor autenticado como USER trata de crear un chirp sin enviar un campo title
	 * 		6. Un actor autenticado como USER trata de crear un chirp sin enviar un campo description
	 * 
	 * Requisitos:
	 * 		16. An actor who is authenticated as a user must be able to:
	 * 			1. Post a chirp. Chirps may not be changed or deleted once they are posted.
	 */
	@Test
	public void driverNegativeTest() {
		Calendar calendar;
		Date moment;
	
		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		moment = calendar.getTime();
		
		
		final Object testingData[][] = {
				{
					"user1", moment, "", "This is a chirp description", false, ConstraintViolationException.class
				}, {
					"user2", moment, "This is a chirp", "", false, ConstraintViolationException.class
				}, {
					"admin", moment, "This is a chirp", "This is a chirp description", false, IllegalArgumentException.class
				}, {
					"customer1", moment, "This is a chirp", "This is a chirp description", false, IllegalArgumentException.class
				}, {
					"user2", moment, null, "This is a chirp description", false, IllegalArgumentException.class
				}, {
					"user2", moment, "This is a chirp", null, false, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (Date) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Crear un chirp
	 * Pasos:
	 * 		1. Autenticar como usuario
	 * 		2. Crear chirp
	 * 		3. Volver al listado de chirp
	 */
	protected void template(final String userBean, final Date moment, final String title, final String description, final boolean hasTaboo, final Class<?> expected) {
		Class<?> caught;
		Page<Chirp> chirps;
		Chirp chirp, saved;
		User user;

		caught = null;
		try {
			
			// 1. Autenticar como usuario
			super.authenticate(userBean);
			user = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(user);
			
			// 2. Crear chirp
			chirp = this.chirpService.create(user);
			chirp.setMoment(moment);
			if(title != null) chirp.setTitle(title); else Assert.notNull(title);
			if(description != null) chirp.setDescription(description); else Assert.notNull(description);
			
			saved = this.chirpService.save(chirp);
			Assert.notNull(saved);
			
			// 3. Volver al listado de chirps
			chirps = this.chirpService.findFollowedsChirpByUserId(user.getId(), this.getPage(user.getId(), saved), 5);
			Assert.notNull(chirps);
			Assert.isTrue(chirps.getContent().contains(saved));
			if(hasTaboo) Assert.isTrue(saved.getHasTaboo());
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
	
	private Integer getPage(final int userId, final Chirp chirp) {
		Integer result;
		Page<Chirp> pageChirp, pageChirpAux;

		pageChirp = this.chirpService.findFollowedsChirpByUserId(userId, 0, 5);

		result = null;
		for (int i = 0; i <= pageChirp.getTotalPages(); i++) {
			pageChirpAux = this.chirpService.findFollowedsChirpByUserId(userId, i, 5);
			if (pageChirpAux.getContent().contains(chirp)) {
				result = i;
				break;
			}
		}

		return result;
	}


}

