
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ChirpService;
import utilities.AbstractTest;
import domain.Chirp;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListChirpTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ChirpService	chirpService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario autenticado como USER entra en una página que llama al método findByUserId
	 * 		2. Un usuario autenticado como USER entra en una página que llama al método findAllPaginated
	 * 		3. Un usuario autenticado como USER entra en una página que llama al método findAllPaginated
	 * 
	 * Requisitos:
	 * 		15. A user may post a chirp. For every chirp, the system must store the moment, a title, and a
	 *			description. The list or chirps are considered a part of the profile of a user. 
	 *
	 *		16. An actor who is authenticated as a user must be able to: 
	 *			5. Display a stream with the chirps posted by all of the users that he or she follows. 
	 *
	 *		17. An actor who is authenticated as an administrator must be able to:
	 *			4. List the chirps that contain taboo words. 
	 */
	@Test
	public void driverTest() {
		final Object testingData[][] = {
			{
				"user1", "findByUserId", 1, 0, 5, null
			}, {
				"admin", "findAllPaginated", 5, 0, 5, null
			}, {
				"user1", "findFollowedsChirpByUserId", 5, 0, 5, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 		1. Autenticar usuario
	 * 		2. Listar
	 */
	protected void template(final String userBean, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<Chirp> chirps;
		Integer userId;

		caught = null;
		chirps = null;
		userId = null;
		try {
			
			if(userBean != null) {
				// 1. Autenticar usuario
				super.authenticate(userBean);
				if(userBean.equals("admin"))
					userId = super.getEntityId("administrator");
				else
					userId = super.getEntityId(userBean);
			}

			// 2. Listar
			if (method.equals("findByUserId")) {
				chirps = this.chirpService.findByUserId(userId, page, size).getContent();
			} else if (method.equals("findAllPaginated")) {
				chirps = this.chirpService.findAllPaginated(page, size).getContent();
			} else if (method.equals("findFollowedsChirpByUserId")) {
				chirps = this.chirpService.findFollowedsChirpByUserId(userId, page, size).getContent();
			}
			
			// Comprobación
			Assert.isTrue(chirps.size() == tamano);
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
}
