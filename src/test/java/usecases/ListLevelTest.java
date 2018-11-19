
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

import services.LevelService;
import utilities.AbstractTest;
import domain.Level;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListLevelTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private LevelService	levelService;


	/*
	 * Pruebas:
	 * 1. Probamos el findAll estando logeados como user
	 * 2. Probamos el findAll estando logeados como moderator
	 * 3. Probamos el findAll estando logeados como admin
	 * 4. Probamos el findAll estando logeados como company
	 * 5. Probamos el findAll estando logeados como sponsor
	 * 6. Probamos el findAll sin estar logeados
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 5, null, null, null
			}, {
				"moderator", "moderator1", "findAll", false, null, null, 5, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 5, null, null, null
			}, {
				"company", "company1", "findAll", false, null, null, 5, null, null, null
			}, {
				"sponsor", "sponsor1", "findAll", false, null, null, 5, null, null, null
			}, {
				null, null, "findAll", false, null, null, 5, null, null, null
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
	 * 1. Hacemos el findAllPaginated logueados como user1 viendo la primera página con tamaño 5 (no salta excepción)
	 * 2. Hacemos el findAllPaginated logueados como user1 viendo la primera página con tamaño 3 (no salta excepción)
	 * 3. Hacemos el findAllPaginated logueados como user1 viendo la segunda página con tamaño 3 (no salta excepción)
	 * 4. Hacemos el findAllPaginated logueados como user1 viendo la tercera página con tamaño 3 (no salta excepción)
	 * 5. Hacemos el findAllPaginated logueados como user1 viendo la primera página con tamaño 6 (no salta excepción)
	 * 6. Hacemos el findAllPaginated logueados como sponsor viendo la primera página con tamaño 5 (no salta excepción)
	 * 7. Hacemos el findAllPaginated logueados como moderator viendo la primera página con tamaño 5 (no salta excepción)
	 * 8. Hacemos el findAllPaginated logueados como company viendo la primera página con tamaño 5 (no salta excepción)
	 * 9. Hacemos el findAllPaginated logueados como admin viendo la primera página con tamaño 5 (no salta excepción)
	 * 10.Hacemos el findAllPaginated sin estar logueado viendo la primera página con tamaño 5 (no salta excepción)
	 * 
	 * Requisito 21.12: Un usuario que no está autenticado puede ver los niveles
	 */
	@Test()
	public void testFindAllPaginated() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAllPaginated", false, null, 1, 5, 1, 5, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 1, 3, 2, 3, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 2, 2, 2, 3, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 3, 0, 2, 3, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 1, 5, 1, 6, null
			}, {
				"sponsor", "sponsor1", "findAllPaginated", false, null, 1, 5, 1, 5, null
			}, {
				"moderator", "moderator1", "findAllPaginated", false, null, 1, 5, 1, 5, null
			}, {
				"company", "company1", "findAllPaginated", false, null, 1, 5, 1, 5, null
			}, {
				"admin", "admin", "findAllPaginated", false, null, 1, 5, 1, 5, null
			}, {
				null, null, "findAllPaginated", false, null, 1, 5, 1, 5, null
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
		Collection<Level> levels;

		caught = null;
		levels = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				levels = this.levelService.findAll(); //Cogemos todos las suscripciones usando el findAll

			Assert.isTrue(levels.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<Level> levels;

		caught = null;
		levels = null;

		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAllPaginated"))
				levels = this.levelService.findAllPaginated(page, tam); //FindAllPaginated

			Assert.isTrue(levels.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(levels.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
