
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class ListDisplayUserTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private UserService	userService;

	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Listar y desplegar un usuario sin autenticar
	 * 		2. Listar y desplegar un usuario autenticado como usuario
	 * 		3. Listar y desplegar un usuario autenticado como compañía
	 * 		4. Listar y desplegar un usuario autenticado como patrocinador
	 * 		5. Listar y desplegar un usuario autenticado como moderador
	 * 
	 * Requisitos:
	 * 		21.4 Un actor que no está autenticado debe ser capaz de listar los usuarios ordenados según su puntuación y las empresas.
	 * 
	 */
	@Test
	public void driverPositiveTests() {
		final Object testingData[][] = {
			{
				null, "user1", null
			}, {
				"user1", "user1", null
			}, {
				"company1", "user2", null
			}, {
				"sponsor1", "user3", null
			}, {
				"moderator2", "user4", null
			},
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	/*
	 * Pruebas:
	 * 		1. Autenticar (o no).
	 * 		2. Listar usuarios
	 * 		3. Desplegar usuario
	 * 
	 * Requisitos:
	 * 		21.4 Un actor que no está autenticado debe ser capaz de listar los usuarios ordenados según su puntuación y las empresas.
	 * 
	 */

	protected void templateListDisplay(final String actorBean, final String userBean, final Class<?> expected) {
		Class<?> caught;
		Page<User> users;
		Integer countUsers;
		User userChoosen, user;

		caught = null;
		try {
			
			super.startTransaction();
			
			this.authenticate(actorBean);

			//Inicializamos
			userChoosen = null;

			user = this.userService.findOne(super.getEntityId(userBean));
			Assert.notNull(user);

			//Obtenemos los usuarios
			users = this.userService.findAllPaginated(1, 1);
			Assert.notNull(users);
			
			countUsers = users.getTotalPages();
			Assert.notNull(countUsers);
	
			//Buscamos el que queremos desplegar
			for (int i = 0; i < countUsers; i++) {
				users = this.userService.findAllPaginated(1+i, 5);

				//Si estamos pidiendo una página mayor
				if (users.getContent().size() == 0)
					break;

				// Navegar hasta el usuario que queremos.
				for (final User newUser : users.getContent())
					if (newUser.equals(user)) {
						userChoosen = newUser;
						break;
					}

				if (userChoosen != null) break;
			}

			Assert.notNull(userChoosen);

			this.userService.findOne(userChoosen.getId());

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
