
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.UserService;
import utilities.AbstractTest;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListUserTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private UserService	userService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario entra en una página que llama al método findAll
	 * 		2. Un usuario anónimo entra en una página que llama al método findAll
	 * 		3. Un customer entra en una página que llama al método findAll
	 * 		4. Un usuario entra en una página que llama al método findAllPaginated en la página 2
	 * 		5. Un usuario anónimo entra en una página que llama al método findAllPaginated en la página 2
	 * 		6. Un usuario entra en una página que llama al método findFollowersByUserId
	 * 		7. Un usuario entra en una página que llama al método findFollowedsByUserId
	 * 
	 * Requisitos:
	 * 		4. An actor who is not authenticated must be able to: 
	 * 			3. List the users of the system and display their profiles, which must include their personal
	 *			   data and the list of articles that they have written as long as they are published
	 *			   in a newspaper. 
	 * 		16. An actor who is authenticated as a user must be able to:
	 * 			3. List the users who he or she follows. 
	 * 			4. List the users who follow him or her. 
	 */
	@Test
	public void driverTest() {
		final Object testingData[][] = {
			{
				"user1", "findAll", 6, 0, 0, null
			}, {
				null, "findAll", 6, 0, 0, null
			}, {
				"customer1", "findAll", 6, 0, 5, null
			}, {
				"user1", "findAllPaginated", 5, 0, 5, null
			}, {
				null, "findAllPaginated", 1, 2, 5, null
			}, {
				"user1", "findFollowersByUserId", 0, 0, 5, null
			}, {
				"user1", "findFollowedsByUserId", 2, 1, 5, null
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
	 * 
	 * 1. Autenticar usuario
	 * 2. Listar
	 */
	protected void template(final String userBean, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<User> users;
		Integer userId;

		caught = null;
		users = null;
		userId = null;
		try {
			
			if(userBean != null) {
				// 1. Autenticar usuario
				super.authenticate(userBean);
				userId = super.getEntityId(userBean);
			}

			// 2. Listar
			if (method.equals("findAll")) {
				users = this.userService.findAll();
			} else if (method.equals("findAllPaginated")) {
				users = this.userService.findAllPaginated(page, size).getContent();
			} else if (method.equals("findFollowersByUserId")) {
				users = this.userService.findFollowersByUserId(userId, page, size).getContent();
			} else if (method.equals("findFollowedsByUserId")) {
				users = this.userService.findFollowedsByUserId(userId, page, size).getContent();
			}
			
			// Comprobación
			Assert.isTrue(users.size() == tamano);
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}
}
