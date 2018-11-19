
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

import security.LoginService;
import services.AdvertisementService;
import services.NewspaperService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Newspaper;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private VolumeService			volumeService;

	@Autowired
	private AdvertisementService	advertisementService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Probamos el findAll estando logeados como user
	 * 2. Probamos el findAll estando logeados como customer
	 * 3. Probamos el findAll estando logeados como admin
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 6, null, null, null
			}, {
				"customer", "customer1", "findAll", false, null, null, 6, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 6, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Listamos los newspapers donde el suscrito el customer2 viendo la primera página con un tamaño de 1
	 * 2. Listamos los newspapers donde el suscrito el customer2 viendo la segunda página con un tamaño de 2
	 * 3. Listamos los newspapers donde el suscrito el customer2 viendo la segunda página con un tamaño de 2
	 * 5. Listamos los newspapers donde el suscrito el customer2 viendo la primera página con un tamaño de 5
	 * 6. Intentamos listar los newspapers a los que está suscrito el customer2 logeados como user1 (salta un IllegalArgumentException)
	 * 7. Intentamos listar los newspapers a los que está suscrito el customer2 logeados como admin (salta un IllegalArgumentException)
	 * 8. Intentamos listar los newspapers a los que está suscrito el customer2 sin estar logeado (salta un IllegalArgumentException)
	 * 9. Intentamos listar los newspapers a los que está suscrito el customer2 usando la id cero (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindForSubscribe() {
		final Object testingData[][] = {
			{
				"customer", "customer2", "findForSubscribe", false, null, 1, 1, 1, 1, null
			}, {
				"customer", "customer2", "findForSubscribe", false, null, 1, 1, 1, 2, null
			}, {
				"customer", "customer2", "findForSubscribe", false, null, 2, 0, 1, 2, null
			}, {
				"customer", "customer2", "findForSubscribe", false, null, 1, 1, 1, 5, null
			}, {
				"user", "user1", "findForSubscribe", false, "customer2", 1, 1, 1, 2, IllegalArgumentException.class
			}, {
				"admin", "admin", "findForSubscribe", false, "customer2", 1, 1, 1, 2, IllegalArgumentException.class
			}, {
				null, null, "findForSubscribe", false, "customer2", 1, 1, 1, 2, IllegalArgumentException.class
			}, {
				"customer", "customer2", "findForSubscribe", true, null, 1, 1, 1, 2, IllegalArgumentException.class
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
	 * 1. Listamos los newspapers del user1 con tamaño 2 y la página 1 (no salta excepción)
	 * 2. Listamos los newspapers del user1 con tamaño 1 y la página 1 (no salta excepción)
	 * 3. Listamos los newspapers del user1 con tamaño 1 y la página 2 (no salta excepción)
	 * 4. Listamos los newspapers del user1 con tamaño 1 y la página 3 (no salta excepción)
	 * 5. Listamos los newspapers del user1 con tamaño 5 y la página 1 (no salta excepción)
	 * 6. Listamos los newspapers del user1 logeados como customer (salta un IllegalArgumentException)
	 * 7. Listamos los newspapers del user1 logeados como admin (salta un IllegalArgumentException)
	 * 8. Listamos los newspapers del user1 sin estar logeado (salta un IllegalArgumentException)
	 * 9. Listamos los newspapers del user1 con id cero (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByUserId() {
		final Object testingData[][] = {
			{
				"user", "user1", "findByUserId", false, null, 1, 2, 1, 2, null
			}, {
				"user", "user1", "findByUserId", false, null, 1, 1, 2, 1, null
			}, {
				"user", "user1", "findByUserId", false, null, 2, 1, 2, 1, null
			}, {
				"user", "user1", "findByUserId", false, null, 3, 0, 2, 1, null
			}, {
				"user", "user1", "findByUserId", false, null, 1, 2, 1, 5, null
			}, {
				"customer", "customer1", "findByUserId", false, "user1", 1, 2, 1, 5, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByUserId", false, "user1", 1, 2, 1, 5, IllegalArgumentException.class
			}, {
				null, null, "findByUserId", false, "user1", 1, 2, 1, 5, IllegalArgumentException.class
			}, {
				"user", "user1", "findByUserId", true, null, 1, 2, 1, 2, IllegalArgumentException.class
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
	 * 1. Listamos los newspapers del customer1 con tamaño 3 y la página 1 (no salta excepción)
	 * 2. Listamos los newspapers del customer1 con tamaño 2 y la página 1 (no salta excepción)
	 * 3. Listamos los newspapers del customer1 con tamaño 2 y la página 2 (no salta excepción)
	 * 4. Listamos los newspapers del customer1 con tamaño 2 y la página 3 (no salta excepción)
	 * 5. Listamos los newspapers del customer1 con tamaño 5 y la página 1 (no salta excepción)
	 * 6. Listamos los newspapers del customer1 logeados como user1 (salta un IllegalArgumentException)
	 * 7. Listamos los newspapers del customer1 logeados como admin (salta un IllegalArgumentException)
	 * 8. Listamos los newspapers del customer1 logeados como sin estar logeado (salta un IllegalArgumentException)
	 * 9. Listamos los newspapers de un customer con id cero logeados como user1 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByCustomerId() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "findByCustomerId", false, null, 1, 3, 1, 3, null
			}, {
				"customer", "customer1", "findByCustomerId", false, null, 1, 2, 2, 2, null
			}, {
				"customer", "customer1", "findByCustomerId", false, null, 2, 1, 2, 2, null
			}, {
				"customer", "customer1", "findByCustomerId", false, null, 3, 0, 2, 2, null
			}, {
				"customer", "customer1", "findByCustomerId", false, null, 1, 3, 1, 5, null
			}, {
				"user", "user1", "findByCustomerId", false, "customer1", 1, 3, 1, 5, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByCustomerId", false, "customer1", 1, 3, 1, 5, IllegalArgumentException.class
			}, {
				null, null, "findByCustomerId", false, "customer1", 1, 3, 1, 5, IllegalArgumentException.class
			}, {
				"customer", "customer1", "findByCustomerId", true, null, 1, 3, 1, 5, IllegalArgumentException.class
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
	 * 1. Hacemos el findPublicsAndPublicated logeados como customer1 viendo la primera página con tamaño 2 (no salta excepción)
	 * 2. Hacemos el findPublicsAndPublicated logeados como customer1 viendo la primera página con tamaño 1 (no salta excepción)
	 * 3. Hacemos el findPublicsAndPublicated logeados como customer1 viendo la segunda página con tamaño 1 (no salta excepción)
	 * 4. Hacemos el findPublicsAndPublicated logeados como customer1 viendo la tercera página con tamaño 1 (no salta excepción)
	 * 5. Hacemos el findPublicsAndPublicated logeados como customer1 viendo la primera página con tamaño 5 (no salta excepción)
	 * 6. Hacemos el findPublicsAndPublicated logeados como user1 viendo la primera página con tamaño 2 (no salta excepción)
	 * 7. Hacemos el findPublicsAndPublicated logeados como admin viendo la primera página con tamaño 2 (no salta excepción)
	 * 8. Hacemos el findPublicsAndPublicated sin estar logeado viendo la primera página con tamaño 2 (no salta excepción)
	 * 
	 * Requisitos:
	 * C.4.2: An actor who is not authenticated must be able to list the newspapers that are published and browse their articles.
	 */
	@Test()
	public void testFindPublicsAndPublicated() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "findPublicsAndPublicated", false, null, 1, 2, 1, 2, null
			}, {
				"customer", "customer1", "findPublicsAndPublicated", false, null, 1, 1, 2, 1, null
			}, {
				"customer", "customer1", "findPublicsAndPublicated", false, null, 2, 1, 2, 1, null
			}, {
				"customer", "customer1", "findPublicsAndPublicated", false, null, 3, 0, 2, 1, null
			}, {
				"customer", "customer1", "findPublicsAndPublicated", false, null, 1, 2, 1, 5, null
			}, {
				"user", "user1", "findPublicsAndPublicated", false, null, 1, 2, 1, 2, null
			}, {
				"admin", "admin", "findPublicsAndPublicated", false, null, 1, 2, 1, 2, null
			}, {
				null, null, "findPublicsAndPublicated", false, null, 1, 2, 1, 2, null
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
	 * 1. Hacemos el findAllPaginated logeados como customer1 viendo la primera página con tamaño 6 (no salta excepción)
	 * 2. Hacemos el findAllPaginated logeados como customer1 viendo la primera página con tamaño 3 (no salta excepción)
	 * 3. Hacemos el findAllPaginated logeados como customer1 viendo la segunda página con tamaño 3 (no salta excepción)
	 * 4. Hacemos el findAllPaginated logeados como customer1 viendo la tercera página con tamaño 3 (no salta excepción)
	 * 5. Hacemos el findAllPaginated logeados como customer1 viendo la primera página con tamaño 7 (no salta excepción)
	 * 6. Hacemos el findAllPaginated logeados como user1 viendo la primera página con tamaño 6 (no salta excepción)
	 * 7. Hacemos el findAllPaginated logeados como admin viendo la primera página con tamaño 6 (no salta excepción)
	 * 8. Hacemos el findAllPaginated sin estar logeadps (salta un IllegalArgumentException)
	 * 
	 * Requisitos
	 * C.4.2: An actor who is not authenticated must be able to list the newspapers that are published and browse their articles.
	 */
	@Test()
	public void testFindAllPaginated() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "findAllPaginated", false, null, 1, 6, 1, 6, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 1, 3, 2, 3, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 2, 3, 2, 3, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 3, 0, 2, 3, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 1, 6, 1, 7, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 1, 6, 1, 6, null
			}, {
				"admin", "admin", "findAllPaginated", false, null, 1, 6, 1, 6, null
			}, {
				null, null, "findAllPaginated", false, null, 1, 6, 1, 6, IllegalArgumentException.class
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
	 * 1. Hacemos el findTaboos logeados como admin viendo la primera página con tamaño 2 (no salta excepción)
	 * 2. Hacemos el findTaboos logeados como admin viendo la primera página con tamaño 1 (no salta excepción)
	 * 3. Hacemos el findTaboos logeados como admin viendo la segunda página con tamaño 1 (no salta excepción)
	 * 4. Hacemos el findTaboos logeados como admin viendo la tercera página con tamaño 1 (no salta excepción)
	 * 5. Hacemos el findTaboos logeados como admin viendo la primera página con tamaño 3 (no salta excepción)
	 * 6. Hacemos el findTaboos logeados como user1 (salta un IllegalArgumentException)
	 * 7. Hacemos el findTaboos logeados como customer1 (salta un IllegalArgumentException)
	 * 8. Hacemos el findTaboos sin estar logeado (salta un IllegalArgumentException)
	 * 
	 * Requisitos
	 * 1.B.17.3: An actor who is authenticated as an administrator must be able to list the newspapers that contain taboo words.
	 */
	@Test()
	public void testFindTaboos() {
		final Object testingData[][] = {
			{
				"admin", "admin", "findTaboos", false, null, 1, 2, 1, 2, null
			}, {
				"admin", "admin", "findTaboos", false, null, 1, 1, 2, 1, null
			}, {
				"admin", "admin", "findTaboos", false, null, 2, 1, 2, 1, null
			}, {
				"admin", "admin", "findTaboos", false, null, 3, 0, 2, 1, null
			}, {
				"admin", "admin", "findTaboos", false, null, 1, 2, 1, 3, null
			}, {
				"user", "user1", "findTaboos", false, null, 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				"customer", "customer1", "findTaboos", false, null, 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				null, null, "findTaboos", false, null, 1, 2, 1, 2, IllegalArgumentException.class
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
	 * 1. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "a" con tamaño 2 la página 1 (no salta excepción)
	 * 2. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "a" con tamaño 1 la página 1 (no salta excepción)
	 * 3. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "a" con tamaño 1 la página 2 (no salta excepción)
	 * 4. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "a" con tamaño 1 la página 3 (no salta excepción)
	 * 5. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "a" con tamaño 3 la página 1 (no salta excepción)
	 * 6. Hacemos el findPublicsPublishedSearch sin logear buscando la keyword "proximo" con tamaño 1 la página 1 (no salta excepción)
	 * 
	 * Requisitos
	 * C.4.5: An actor who is not authenticated must be able to search for a published newspaper using a single keyword that must appear somewhere
	 * in its title or its description
	 */
	@Test()
	public void testFindPublicsPublishedSearch() {
		final Object testingData[][] = {
			{
				null, null, "findPublicsPublishedSearch", false, "a", 1, 2, 1, 2, null
			}, {
				null, null, "findPublicsPublishedSearch", false, "a", 1, 1, 2, 1, null
			}, {
				null, null, "findPublicsPublishedSearch", false, "a", 2, 1, 2, 1, null
			}, {
				null, null, "findPublicsPublishedSearch", false, "a", 3, 0, 2, 1, null
			}, {
				null, null, "findPublicsPublishedSearch", false, "a", 1, 2, 1, 3, null
			}, {
				null, null, "findPublicsPublishedSearch", false, "proximo", 1, 1, 1, 1, null
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
	 * 1. Hacemos el findPublishedSearch logeados como user1 buscando la keyword "a" con tamaño 3 la página 1 (no salta excepción)
	 * 2. Hacemos el findPublishedSearch logeados como user1 buscando la keyword "a" con tamaño 2 la página 1 (no salta excepción)
	 * 3. Hacemos el findPublishedSearch logeados como user1 buscando la keyword "a" con tamaño 2 la página 2 (no salta excepción)
	 * 4. Hacemos el findPublishedSearch logeados como user1 buscando la keyword "a" con tamaño 2 la página 3 (no salta excepción)
	 * 5. Hacemos el findPublishedSearch logeados como user1 buscando la keyword "a" con tamaño 4 la página 1 (no salta excepción)
	 * 6. Hacemos el findPublishedSearch logeados como customer1 buscando la keyword "a" con tamaño 3 la página 1 (no salta excepción)
	 * 7. Hacemos el findPublishedSearch logeados como admin buscando la keyword "a" con tamaño 3 la página 1 (no salta excepción)
	 * 8. Hacemos el findPublishedSearch sin estar logeado buscando la keyword "a" con tamaño 3 la página 1 (salta un IllegalArgumentException)
	 * 
	 * Requisitos
	 * C.4.5: An actor who is not authenticated must be able to search for a published newspaper using a single keyword that must appear somewhere
	 * in its title or its description
	 */
	@Test()
	public void testFindPublishedSearch() {
		final Object testingData[][] = {
			{
				"user", "user1", "findPublishedSearch", false, "a", 1, 3, 1, 3, null
			}, {
				"user", "user1", "findPublishedSearch", false, "a", 1, 2, 2, 2, null
			}, {
				"user", "user1", "findPublishedSearch", false, "a", 2, 1, 2, 2, null
			}, {
				"user", "user1", "findPublishedSearch", false, "a", 3, 0, 2, 2, null
			}, {
				"user", "user1", "findPublishedSearch", false, "a", 1, 3, 1, 4, null
			}, {
				"customer", "customer1", "findPublishedSearch", false, "a", 1, 3, 1, 3, null
			}, {
				"admin", "admin", "findPublishedSearch", false, "a", 1, 3, 1, 3, null
			}, {
				null, null, "findPublishedSearch", false, "a", 1, 3, 1, 3, IllegalArgumentException.class
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
	 * 1. Listamos los newspapers publicados con tamaño 3 y la página 1 (no salta excepción)
	 * 2. Listamos los newspapers publicados con tamaño 2 y la página 1 (no salta excepción)
	 * 3. Listamos los newspapers del customer1 con tamaño 2 y la página 2 (no salta excepción)
	 * 4. Listamos los newspapers publicados con tamaño 2 y la página 3 (no salta excepción)
	 * 5. Listamos los newspapers publicados con tamaño 4 y la página 1 (no salta excepción)
	 * 6. Listamos los newspapers publicados con tamaño 3 y la página 1 logeados como customer1 (salta un IllegalArgumentException)
	 * 7. Listamos los newspapers publicados con tamaño 3 y la página 1 logeados como admin (salta un IllegalArgumentException)
	 * 8. Listamos los newspapers publicados con tamaño 3 y la página 1 sin estar logeado (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindPublished() {
		final Object testingData[][] = {
			{
				"user", "user1", "findPublished", false, null, 1, 3, 1, 3, null
			}, {
				"user", "user1", "findPublished", false, null, 1, 2, 2, 2, null
			}, {
				"user", "user1", "findPublished", false, null, 2, 1, 2, 2, null
			}, {
				"user", "user1", "findPublished", false, null, 3, 0, 2, 2, null
			}, {
				"user", "user1", "findPublished", false, null, 1, 3, 1, 4, null
			}, {
				"customer", "customer1", "findPublished", false, null, 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				"admin", "admin", "findPublished", false, null, 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				null, null, "findPublished", false, null, 1, 3, 1, 3, IllegalArgumentException.class
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
	 * 1. Logeados como user1 listamos los newspapers para añadir al volume1 con tamaño 2 y la página 1 (no salta excepción)
	 * 2. Logeados como user1 listamos los newspapers para añadir al volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 3. Logeados como user1 listamos los newspapers para añadir al volume1 con tamaño 1 y la página 2 (no salta excepción)
	 * 4. Logeados como user1 listamos los newspapers para añadir al volume1 con tamaño 1 y la página 3 (no salta excepción)
	 * 5. Logeados como user1 listamos los newspapers para añadir al volume1 con tamaño 4 y la página 1 (no salta excepción)
	 * 6. Logeados como user2 listamos los newspapers para añadir al volume1 que no es suyo (salta un IllegalArgumentNewspaper)
	 * 7. Logeados como customer1 listamos los newspapers para añadir al volume1 (salta un IllegalArgumentNewspaper)
	 * 8. Logeados como admin listamos los newspapers para añadir al volume1 (salta un IllegalArgumentNewspaper)
	 * 9. Sin estar logeados listamos los newspapers para añadir al volume1 (salta un IllegalArgumentNewspaper)
	 */
	@Test()
	public void testFindAddNewspaper() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAddNewspaper", false, "volume1", 1, 2, 1, 2, null
			}, {
				"user", "user1", "findAddNewspaper", false, "volume1", 1, 1, 2, 1, null
			}, {
				"user", "user1", "findAddNewspaper", false, "volume1", 2, 1, 2, 1, null
			}, {
				"user", "user1", "findAddNewspaper", false, "volume1", 3, 0, 2, 1, null
			}, {
				"user", "user1", "findAddNewspaper", false, "volume1", 1, 2, 1, 4, null
			}, {
				"user", "user2", "findAddNewspaper", false, "volume1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				"customer", "customer1", "findAddNewspaper", false, "volume1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				"admin", "admin", "findAddNewspaper", false, "volume1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				null, null, "findAddNewspaper", false, "volume1", 1, 2, 1, 2, IllegalArgumentException.class
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
	 * 1. Logeados como user1 listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 2. Logeados como user2 listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 3. Logeados como user1 listamos los newspapers públicos del volume1 con tamaño 2 y la página 1 (no salta excepción)
	 * 4. Logeados como user1 listamos los newspapers públicos del volume1 con tamaño 1 y la página 2 (no salta excepción)
	 * 5. Logeados como user1 listamos los newspapers públicos del volume1 con tamaño 4 y la página 1 (no salta excepción)
	 * 6. Logeados como customer1 listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 7. Logeados como admin listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 8. Logeados como agent1 listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 * 9. Sin estar logeados listamos los newspapers públicos del volume1 con tamaño 1 y la página 1 (no salta excepción)
	 */
	@Test()
	public void testFindByVolumeAllPublics() {
		final Object testingData[][] = {
			{
				"user", "user1", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
			}, {
				"user", "user2", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
			}, {
				"user", "user1", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 2, null
			}, {
				"user", "user1", "findByVolumeAllPublics", false, "volume1", 2, 0, 1, 1, null
			}, {
				"user", "user1", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 4, null
			}, {
				"customer", "customer1", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
			}, {
				"admin", "admin", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
			}, {
				"agent", "agent1", "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
			}, {
				null, null, "findByVolumeAllPublics", false, "volume1", 1, 1, 1, 1, null
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
	 * 1. Logeados como agent1 listamos los newspapers donde tiene anuncios con tamaño 2 y la página 1 (no salta excepción)
	 * 2. Logeados como agent1 listamos los newspapers donde tiene anuncios con tamaño 1 y la página 1 (no salta excepción)
	 * 3. Logeados como agent1 listamos los newspapers donde tiene anuncios con tamaño 1 y la página 2 (no salta excepción)
	 * 4. Logeados como agent1 listamos los newspapers donde tiene anuncios con tamaño 1 y la página 3 (no salta excepción)
	 * 5. Logeados como customer1 listamos los newspapers donde tiene anuncios el agent1 con tamaño 2 y la página 1 (salta un IllegalArgumentException)
	 * 6. Logeados como admin listamos los newspapers donde tiene anuncios el agent1 con tamaño 2 y la página 1 (salta un IllegalArgumentException)
	 * 7. Logeados como user1 listamos los newspapers donde tiene anuncios el agent1 con tamaño 2 y la página 1 (salta un IllegalArgumentException)
	 * 8. Sin estar logeados listamos los newspapers donde tiene anuncios el agent1 con tamaño 2 y la página 1 (salta un IllegalArgumentException)
	 * 
	 * Requisito
	 * C.4.3: An actor who is authenticated as an agent must be able to list the newspapers in which they have placed an advertisement
	 */
	@Test()
	public void testFindNewspaperWithAdvertisements() {
		final Object testingData[][] = {
			{
				"agent", "agent1", "findNewspaperWithAdvertisements", false, null, 1, 2, 1, 2, null
			}, {
				"agent", "agent1", "findNewspaperWithAdvertisements", false, null, 1, 1, 2, 1, null
			}, {
				"agent", "agent1", "findNewspaperWithAdvertisements", false, null, 2, 1, 2, 1, null
			}, {
				"agent", "agent1", "findNewspaperWithAdvertisements", false, null, 3, 0, 2, 1, null
			}, {
				"customer", "customer1", "findNewspaperWithAdvertisements", false, "agent1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				"admin", "admin", "findNewspaperWithAdvertisements", false, "agent1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				"user", "user1", "findNewspaperWithAdvertisements", false, "agent1", 1, 2, 1, 2, IllegalArgumentException.class
			}, {
				null, null, "findNewspaperWithAdvertisements", false, "agent1", 1, 2, 1, 2, IllegalArgumentException.class
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
	 * 1. Logeados como agent1 listamos los newspapers donde no tiene anuncios con tamaño 4 y la página 1 (no salta excepción)
	 * 2. Logeados como agent1 listamos los newspapers donde no tiene anuncios con tamaño 2 y la página 1 (no salta excepción)
	 * 3. Logeados como agent1 listamos los newspapers donde no tiene anuncios con tamaño 2 y la página 2 (no salta excepción)
	 * 4. Logeados como agent1 listamos los newspapers donde no tiene anuncios con tamaño 2 y la página 3 (no salta excepción)
	 * 5. Logeados como customer1 listamos los newspapers donde no tiene anuncios el agent1 con tamaño 4 y la página 1 (salta un IllegalArgumentException)
	 * 6. Logeados como admin listamos los newspapers donde no tiene anuncios el agent1 con tamaño 4 y la página 1 (salta un IllegalArgumentException)
	 * 7. Logeados como user1 listamos los newspapers donde no tiene anuncios el agent1 con tamaño 4 y la página 1 (salta un IllegalArgumentException)
	 * 8. Sin estar logeados listamos los newspapers donde no tiene anuncios el agent1 con tamaño 4 y la página 1 (salta un IllegalArgumentException)
	 * 
	 * Requisito
	 * C.4.4: An actor who is authenticated as an agent must be able to list the newspapers in which they have not placed any advertisements.
	 */
	@Test()
	public void testFindNewspaperWithNoAdvertisements() {
		final Object testingData[][] = {
			{
				"agent", "agent1", "findNewspaperWithNoAdvertisements", false, null, 1, 4, 1, 4, null
			}, {
				"agent", "agent1", "findNewspaperWithNoAdvertisements", false, null, 1, 2, 2, 2, null
			}, {
				"agent", "agent1", "findNewspaperWithNoAdvertisements", false, null, 2, 2, 2, 2, null
			}, {
				"agent", "agent1", "findNewspaperWithNoAdvertisements", false, null, 3, 0, 2, 2, null
			}, {
				"customer", "customer1", "findNewspaperWithNoAdvertisements", false, "agent1", 1, 4, 1, 4, IllegalArgumentException.class
			}, {
				"admin", "admin", "findNewspaperWithNoAdvertisements", false, "agent1", 1, 4, 1, 4, IllegalArgumentException.class
			}, {
				"user", "user1", "findNewspaperWithNoAdvertisements", false, "agent1", 1, 4, 1, 4, IllegalArgumentException.class
			}, {
				null, null, "findNewspaperWithAdvertisements", false, "agent1", 1, 4, 1, 4, IllegalArgumentException.class
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
	 * 1. Logeados como agent1 vemos que la colección de newspapers donde está el advertisement1 es de 1
	 * 2. Logeados como agent1 vemos que la colección de newspapers donde está el advertisement2 es de 1
	 * 3. Logeados como agent1 vemos que la colección de newspapers donde está el advertisement3 es de 0
	 * 4. Logeados como admin vemos que la colección de newspapers donde está el advertisement1 es de 1
	 * 5. Logeados como admin vemos que la colección de newspapers donde está el advertisement2 es de 1
	 */
	@Test()
	public void testFindNewspapersToUpdateAdvertisements() {
		final Object testingData[][] = {
			{
				"agent", "agent1", "findNewspapersToUpdateAdvertisements", false, "advertisement1", null, 1, null, null, null
			}, {
				"agent", "agent1", "findNewspapersToUpdateAdvertisements", false, "advertisement2", null, 1, null, null, null
			}, {
				"agent", "agent1", "findNewspapersToUpdateAdvertisements", false, "advertisement3", null, 0, null, null, null
			}, {
				"admin", "admin", "findNewspapersToUpdateAdvertisements", false, "advertisement1", null, 1, null, null, null
			}, {
				"admin", "admin", "findNewspapersToUpdateAdvertisements", false, "advertisement2", null, 1, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	protected void template(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer tam, final Integer numPages, final Class<?> expected) {
		Class<?> caught;
		Collection<Newspaper> newspapers;
		int advertisementIdAux;
		int advertisementId;
		//		final int totalPages;
		//		int customerId;

		caught = null;
		newspapers = null;
		//customerId = 0;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				newspapers = this.newspaperService.findAll(); //Cogemos todos los newspapers usando el findAll
			else if (method.equals("findNewspapersToUpdateAdvertisements")) {
				advertisementIdAux = super.getEntityId(bean);
				advertisementId = 0;
				if (user != null && user.equals("admin")) { //Si eres admin se busca entre todos los anuncios
					for (int i = 1; i <= this.advertisementService.findAllPaginated(1, 5).getTotalPages(); i++)
						for (final Advertisement a : this.advertisementService.findAllPaginated(i, 5))
							if (a.getId() == advertisementIdAux)
								advertisementId = a.getId();
				} else if (user != null && user.equals("agent")) //Si eres agente se busca entre los tuyos
					for (int i = 1; i <= this.advertisementService.findByAgentId(1, 5).getTotalPages(); i++)
						for (final Advertisement a : this.advertisementService.findByAgentId(i, 5))
							if (a.getId() == advertisementIdAux)
								advertisementId = a.getId();

				newspapers = this.newspaperService.findNewspapersToUpdateAdvertisements(advertisementId);
			}

			Assert.isTrue(newspapers.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<Newspaper> newspapers;
		int userId;
		int customerId;
		int volumeId;
		int volumeIdAux;
		int agentId;

		caught = null;
		newspapers = null;
		userId = 0;
		customerId = 0;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findByUserId")) {
				if (user != null && user.equals("user"))
					userId = super.getEntityId(username);
				if (falseId == false) {
					if (user != null && user.equals("user"))
						newspapers = this.newspaperService.findByUserId(userId, page, tam); //Todos los newspapers del user logeado
					else
						newspapers = this.newspaperService.findByUserId(super.getEntityId(bean), page, tam); //Los newspapers de user que paas
				} else
					newspapers = this.newspaperService.findByUserId(0, page, tam); //Los newspapers con id cero
			} else if (method.equals("findByCustomerId")) {
				if (falseId == false) {
					if (user != null && user.equals("customer")) {
						customerId = super.getEntityId(username);
						newspapers = this.newspaperService.findByCustomerId(customerId, page, tam); //Los newspapers del customer logeado
					} else
						newspapers = this.newspaperService.findByUserId(super.getEntityId(bean), page, tam); //LOs newspapers del customer dado
				} else
					newspapers = this.newspaperService.findByUserId(0, page, tam);
			} else if (method.equals("findPublicsAndPublicated"))
				newspapers = this.newspaperService.findPublicsAndPublicated(page, tam); //FindAllPublicated
			else if (method.equals("findAllPaginated"))
				newspapers = this.newspaperService.findAllPaginated(page, tam); //FindAllPaginated
			else if (method.equals("findTaboos"))
				newspapers = this.newspaperService.findTaboos(page, tam); //FindTaboos
			else if (method.equals("findPublicsPublishedSearch"))
				newspapers = this.newspaperService.findPublicsPublishedSearch(bean, page, tam); //FindPublicsPublishedSearch
			else if (method.equals("findPublishedSearch"))
				newspapers = this.newspaperService.findPublishedSearch(bean, page, tam); //FindPublishedSearch
			else if (method.equals("findForSubscribe")) {
				if (user != null && user.equals("customer"))
					customerId = super.getEntityId(username);
				if (falseId == false) {
					if (user != null && user.equals("customer"))
						newspapers = this.newspaperService.findForSubscribe(customerId, page, tam); //Si estamos como customer pillar los del logeado
					else
						newspapers = this.newspaperService.findForSubscribe(super.getEntityId(bean), page, tam); //Si no pillar la del customer que le pasas
				} else
					newspapers = this.newspaperService.findForSubscribe(0, page, tam);
			} else if (method.equals("findPublished"))
				newspapers = this.newspaperService.findPublished(page, tam); //FindPublished
			else if (method.equals("findAddNewspaper")) { //FindAddNewspaper
				if (user != null && user.equals("user")) { //Si eres user se busca entre tus volúmenes
					volumeIdAux = super.getEntityId(bean);
					volumeId = 0;
					for (int i = 1; i <= this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5).getTotalPages(); i++)
						for (final Volume v : this.volumeService.findByUserAccountId(LoginService.getPrincipal().getId(), i, 5))
							if (v.getId() == volumeIdAux)
								volumeId = v.getId();
				} else
					//Si no se coge la id directamente
					volumeId = super.getEntityId(bean);
				if (falseId == false)
					newspapers = this.newspaperService.findAddNewspaper(volumeId, page, tam);
				else
					newspapers = this.newspaperService.findAddNewspaper(0, page, tam);
			} else if (method.equals("findByVolumeAllPublics")) { //FindByVolumeAllPublics
				volumeIdAux = super.getEntityId(bean);
				volumeId = 0;
				for (int i = 1; i <= this.volumeService.findAllPaginated(1, 5).getTotalPages(); i++)
					//Se mira entre todos los volúmenes
					for (final Volume v : this.volumeService.findAllPaginated(i, 5))
						if (v.getId() == volumeIdAux)
							volumeId = v.getId();

				newspapers = this.newspaperService.findByVolumeAllPublics(volumeId, page, tam);
			} else if (method.equals("findNewspaperWithAdvertisements")) { //FindNewspaperWithAdvetisements
				if (user != null && user.equals("agent"))
					agentId = super.getEntityId(username);
				else
					agentId = super.getEntityId(bean);
				newspapers = this.newspaperService.findNewspaperWithAdvertisements(agentId, page, tam);
			} else if (method.equals("findNewspaperWithNoAdvertisements")) { //FindNewspaperWithNoAdvetisements
				if (user != null && user.equals("agent"))
					agentId = super.getEntityId(username);
				else
					agentId = super.getEntityId(bean);
				newspapers = this.newspaperService.findNewspaperWithNoAdvertisements(agentId, page, tam);
			}

			Assert.isTrue(newspapers.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(newspapers.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
