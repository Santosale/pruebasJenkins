
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.GrouponService;
import utilities.AbstractTest;
import domain.Groupon;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListGrouponTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private GrouponService	grouponService;


	/*
	 * Pruebas:
	 * 1. Probamos el findAll estando logeados como user
	 * 2. Probamos el findAll estando logeados como moderator
	 * 3. Probamos el findAll estando logeados como admin
	 * 4. Probamos el findAll estando logeados como company
	 * 5. Probamos el findAll estando logeados como sponsor
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 7, null, null, null
			}, {
				"moderator", "moderator1", "findAll", false, null, null, 7, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 7, null, null, null
			}, {
				"company", "company1", "findAll", false, null, null, 7, null, null, null
			}, {
				"sponsor", "sponsor1", "findAll", false, null, null, 7, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Hacemos el findAllPaginated como user2 la página 1 con tamaño 7 (no salta excepción)
	 * 2. Hacemos el findAllPaginated como user2 la página 1 con tamaño 4 (no salta excepción)
	 * 3. Hacemos el findAllPaginated como user2 la página 2 con tamaño 4 (no salta excepción)
	 * 4. Hacemos el findAllPaginated como user2 la página 3 con tamaño 4 (no salta excepción)
	 * 5. Hacemos el findAllPaginated como user2 la página 1 con tamaño 8 (no salta excepción)
	 * 6. Hacemos el findAllPaginated como moderator la página 1 con tamaño 4 (no salta excepción)
	 * 7. Hacemos el findAllPaginated logueados como sponsor(salta un IllegalArgumentException)
	 * 8. Hacemos el findAllPaginated logueados como user5 que no tiene plan premium(salta un IllegalArgumentException)
	 * 9. Hacemos el findAllPaginated logueados como company(salta un IllegalArgumentException)
	 * 10.Hacemos el findAllPaginated logueados como admin(salta un IllegalArgumentException)
	 * 11.Hacemos el findAllPaginated sin estar logueados(salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindAllPaginated() {
		final Object testingData[][] = {
			{
				"user", "user2", "findAllPaginated", false, null, 1, 7, 1, 7, null
			}, {
				"user", "user2", "findAllPaginated", false, null, 1, 4, 2, 4, null
			}, {
				"user", "user2", "findAllPaginated", false, null, 2, 3, 2, 4, null
			}, {
				"user", "user2", "findAllPaginated", false, null, 3, 0, 2, 4, null
			}, {
				"user", "user2", "findAllPaginated", false, null, 1, 7, 1, 8, null
			}, {
				"moderator", "moderator1", "findAllPaginated", false, null, 1, 4, 2, 4, null
			}, {
				"sponsor", "sponsor1", "findAllPaginated", false, null, 1, 7, 1, 7, IllegalArgumentException.class
			}, {
				"user", "user5", "findAllPaginated", false, null, 1, 7, 1, 7, IllegalArgumentException.class
			}, {
				"company", "company1", "findAllPaginated", false, null, 1, 7, 1, 7, IllegalArgumentException.class
			}, {
				"admin", "admin", "findAllPaginated", false, null, 1, 7, 1, 7, IllegalArgumentException.class
			}, {
				null, null, "findAllPaginated", false, null, 1, 7, 1, 7, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template2((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Hacemos el findByCreatorId del user1 la página 1 con tamaño 6 (no salta excepción)
	 * 2. Hacemos el findByCreatorId del user1 la página 1 con tamaño 3 (no salta excepción)
	 * 3. Hacemos el findByCreatorId del user1 la página 2 con tamaño 2 (no salta excepción)
	 * 4. Hacemos el findByCreatorId del user1 la página 3 con tamaño 0 (no salta excepción)
	 * 5. Hacemos el findByCreatorId del user1 la página 1 con tamaño 7 (no salta excepción)
	 * 6. Hacemos el findByCreatorId del user1 autenticados como moderator la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 * 7. Hacemos el findByCreatorId del user1 autenticados como sponsor la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 * 8. Hacemos el findByCreatorId del user1 autenticados como user2 la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 * 9. Hacemos el findByCreatorId del user1 autenticados como company la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 * 10.Hacemos el findByCreatorId del user1 autenticados como admin la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 * 11.Hacemos el findByCreatorId del user1 sin estar autenticado la página 1 con tamaño 6 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByCreatorId() {
		final Object testingData[][] = {
			{
				"user", "user1", "findByCreatorId", false, null, 1, 6, 1, 6, null
			}, {
				"user", "user1", "findByCreatorId", false, null, 1, 3, 2, 3, null
			}, {
				"user", "user1", "findByCreatorId", false, null, 2, 3, 2, 3, null
			}, {
				"user", "user1", "findByCreatorId", false, null, 3, 0, 2, 3, null
			}, {
				"user", "user1", "findByCreatorId", false, null, 1, 6, 1, 7, null
			}, {
				"moderator", "moderator1", "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"userAux", "user2", "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"company", "company1", "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}, {
				null, null, "findByCreatorId", false, "user1", 1, 6, 1, 6, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template2((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Hacemos el findWithMaxDateFuture como user5 la página 1 con tamaño 5 (no salta excepción)
	 * 2. Hacemos el findWithMaxDateFuture como user5 la página 1 con tamaño 3 (no salta excepción)
	 * 3. Hacemos el findWithMaxDateFuture como user5 la página 2 con tamaño 3 (no salta excepción)
	 * 4. Hacemos el findWithMaxDateFuture como user5 la página 3 con tamaño 3 (no salta excepción)
	 * 5. Hacemos el findWithMaxDateFuture como user5 la página 1 con tamaño 6 (no salta excepción)
	 * 6. Hacemos el findWithMaxDateFuture como moderator la página 1 con tamaño 5 (no salta excepción)
	 * 7. Hacemos el findWithMaxDateFuture logueados como sponsor la página 1 con tamaño 5 (no salta excepción)
	 * 8. Hacemos el findAllPaginated logueados como user2 la página 1 con tamaño 5 (no salta excepción)
	 * 9. Hacemos el findAllPaginated logueados como company la página 1 con tamaño 5 (no salta excepción)
	 * 10.Hacemos el findAllPaginated logueados como admin la página 1 con tamaño 5 (no salta excepción)
	 * 11.Hacemos el findAllPaginated sin estar logueados la página 1 con tamaño 5 (no salta excepción)
	 * 
	 * Requisito 21.6: Un actor que no está autenticado debe ser capaz de listar las conjuntas cuya fecha máxima no haya pasado
	 */
	@Test()
	public void testFindWithMaxDateFuture() {
		final Object testingData[][] = {
			{
				"user", "user5", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				"user", "user5", "findWithMaxDateFuture", false, null, 1, 3, 2, 3, null
			}, {
				"user", "user5", "findWithMaxDateFuture", false, null, 2, 2, 2, 3, null
			}, {
				"user", "user5", "findWithMaxDateFuture", false, null, 3, 0, 2, 3, null
			}, {
				"user", "user5", "findWithMaxDateFuture", false, null, 1, 5, 1, 6, null
			}, {
				"moderator", "moderator1", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				"sponsor", "sponsor1", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				"user", "user2", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				"company", "company1", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				"admin", "admin", "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}, {
				null, null, "findWithMaxDateFuture", false, null, 1, 5, 1, 5, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template2((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer tam, final Integer numPages, final Class<?> expected) {
		Class<?> caught;
		Collection<Groupon> groupons;

		caught = null;
		groupons = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				groupons = this.grouponService.findAll(); //Cogemos todos las suscripciones usando el findAll 

			Assert.isTrue(groupons.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<Groupon> groupons;
		int userId;

		caught = null;
		groupons = null;

		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAllPaginated"))
				groupons = this.grouponService.findAllPaginated(page, tam);
			else if (method.equals("findByCreatorId")) {
				if (user != null && user.equals("user"))
					userId = super.getEntityId(username);
				else
					userId = super.getEntityId(bean);
				groupons = this.grouponService.findByCreatorId(userId, page, tam);
			} else if (method.equals("findWithMaxDateFuture"))
				groupons = this.grouponService.findWithMaxDateFuture(page, tam);

			Assert.isTrue(groupons.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(groupons.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
