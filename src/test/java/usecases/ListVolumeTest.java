
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

import services.NewspaperService;
import services.UserService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.Newspaper;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private VolumeService		volumeService;

	@Autowired
	private UserService			userService;


	/*
	 * Test
	 * 1. Logueados como user1 hacemos el findAll
	 * 2. Logueados como customer1 hacemos el findAll
	 * 3. Logueados como admin hacemos el findAll
	 * 4. Logueados como agent1 hacemos el findAll
	 * 5. Sin loguearse hacemos el findAll
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 3, null, null, null
			}, {
				"customer", "customer1", "findAll", false, null, null, 3, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 3, null, null, null
			}, {
				"agent", "agent1", "findAll", false, null, null, 3, null, null, null
			}, {
				null, null, "findAll", false, null, null, 3, null, null, null
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
	 * 1.Logueados como admin miramos que la colección de volúmenes que tienen el newspaper1 es de 2 (no salta excepción)
	 * 2.Logueados como admin miramos que la colección de volúmenes que tienen el newspaper2 es de 1 (no salta excepción)
	 * 3.Logueados como admin miramos que la colección de volúmenes que tienen el newspaper3 es de 1 (no salta excepción)
	 * 4.Logueados como user1 miramos que la colección de volúmenes que tienen el newspaper1 es de 2 (salta un IllegalArgumentException)
	 * 5.Logueados como customer1 miramos que la colección de volúmenes que tienen el newspaper1 es de 2 (salta un IllegalArgumentException)
	 * 6.Logueados como agent1 miramos que la colección de volúmenes que tienen el newspaper1 es de 2 (salta un IllegalArgumentException)
	 * 7.Sin estar logueado miramos que la colección de volúmenes que tienen el newspaper1 es de 2 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByNewspaperId() {
		final Object testingData[][] = {
			{
				"admin", "admin", "findByNewspaperId", false, "newspaper1", null, 2, null, null, null
			}, {
				"admin", "admin", "findByNewspaperId", false, "newspaper2", null, 1, null, null, null
			}, {
				"admin", "admin", "findByNewspaperId", false, "newspaper3", null, 1, null, null, null
			}, {
				"user", "user1", "findByNewspaperId", false, "newspaper1", null, 2, null, null, IllegalArgumentException.class
			}, {
				"customer", "customer1", "findByNewspaperId", false, "newspaper1", null, 2, null, null, IllegalArgumentException.class
			}, {
				"agent", "agent1", "findByNewspaperId", false, "newspaper1", null, 2, null, null, IllegalArgumentException.class
			}, {
				null, null, "findByNewspaperId", false, "newspaper1", null, 2, null, null, IllegalArgumentException.class
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
	 * 1.Logeados como customer1 vemos que hay dos volúmenes a los que estás suscrito que tienen el newspaper1
	 * 2.Logeados como customer1 vemos que hay un volúmen a los que estás suscrito que tienen el newspaper2
	 * 3.Logeados como customer1 vemos que hay un volúmen a los que estás suscrito que tienen el newspaper3
	 * 4.Logeados como customer2 vemos que hay un volúmen a los que estás suscrito que tienen el newspaper1
	 * 5.Logeados como customer2 vemos que hay un volúmen a los que estás suscrito que tienen el newspaper2
	 * 6.Logeados como customer2 vemos que no hay volúmenes a los que estás suscrito que tienen el newspaper1
	 * 7.Logeados como customer3 vemos que no hay volúmenes a los que estás suscrito que tienen el newspaper1
	 */
	@Test()
	public void testFindByCustomerIdAndNewspaperId() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "findByCustomerIdAndNewspaperId", false, "newspaper1", null, 2, null, null, null
			}, {
				"customer", "customer1", "findByCustomerIdAndNewspaperId", false, "newspaper2", null, 1, null, null, null
			}, {
				"customer", "customer1", "findByCustomerIdAndNewspaperId", false, "newspaper3", null, 1, null, null, null
			}, {
				"customer", "customer2", "findByCustomerIdAndNewspaperId", false, "newspaper1", null, 1, null, null, null
			}, {
				"customer", "customer2", "findByCustomerIdAndNewspaperId", false, "newspaper2", null, 1, null, null, null
			}, {
				"customer", "customer2", "findByCustomerIdAndNewspaperId", false, "newspaper3", null, 0, null, null, null
			}, {
				"customer", "customer3", "findByCustomerIdAndNewspaperId", false, "newspaper1", null, 0, null, null, null
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
	 * 1. Logeados como user1 listamos los volúmenes tuyos con tamaño 1 y página 1 (no salta excepción)
	 * 2. Logeados como user1 listamos los volúmenes tuyos con tamaño 2 y página 1 (no salta excepción)
	 * 3. Logeados como user1 listamos los volúmenes tuyos con tamaño 2 y página 2 (no salta excepción)
	 * 4. Logeados como user1 listamos los volúmenes tuyos con tamaño 5 y página 1 (no salta excepción)
	 * 5. Logeados como customer1 listamos los volúmenes del user1 con tamaño 1 y página 1 (salta un IllegalArgumentException)
	 * 6. Logeados como admin listamos los volúmenes del user1 con tamaño 1 y página 1 (salta un IllegalArgumentException)
	 * 7. Logeados como user2 listamos los volúmenes del user1 con tamaño 1 y página 1 (salta un IllegalArgumentException)
	 * 8. Logeados como agent1 listamos los volúmenes del user1 con tamaño 1 y página 1 (salta un IllegalArgumentException)
	 * 9. Sin estar logueado listamos los volúmenes del user1 con tamaño 1 y página 1 (salta un IllegalArgumentExceptionn)
	 */
	@Test()
	public void testFindByUserAccountId() {
		final Object testingData[][] = {
			{
				"user", "user1", "findByUserAccountId", false, null, 1, 1, 1, 1, null
			}, {
				"user", "user1", "findByUserAccountId", false, null, 1, 1, 1, 2, null
			}, {
				"user", "user1", "findByUserAccountId", false, null, 2, 0, 1, 2, null
			}, {
				"user", "user1", "findByUserAccountId", false, null, 1, 1, 1, 5, null
			}, {
				"customer", "customer1", "findByUserAccountId", false, "user1", 1, 1, 1, 1, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByUserAccountId", false, "user1", 1, 1, 1, 1, IllegalArgumentException.class
			}, {
				"user", "user2", "findByUserAccountId", false, "user1", 1, 1, 1, 1, IllegalArgumentException.class
			}, {
				"agent", "agent1", "findByUserAccountId", false, "user1", 1, 1, 1, 1, IllegalArgumentException.class
			}, {
				null, null, "findByUserAccountId", false, "user1", 1, 1, 1, 1, IllegalArgumentException.class
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
	 * 1. Logeados como customer1 listamos todos los volúmenes con tamaño 3 y página 1 (no salta excepción)
	 * 2. Logeados como customer1 listamos todos los volúmenes con tamaño 2 y página 1 (no salta excepción)
	 * 3. Logeados como customer1 listamos todos los volúmenes con tamaño 2 y página 2 (no salta excepción)
	 * 4. Logeados como customer1 listamos todos los volúmenes con tamaño 2 y página 3 (no salta excepción)
	 * 5. Logeados como customer1 listamos todos los volúmenes con tamaño 7 y página 1 (no salta excepción)
	 * 6. Logeados como user1 listamos todos los volúmenes con tamaño 3 y página 1 (no salta excepción)
	 * 7. Logeados como admin listamos todos los volúmenes con tamaño 3 y página 1 (no salta excepción)
	 * 8. Logeados como agent1 listamos todos los volúmenes con tamaño 3 y página 1 (no salta excepción)
	 * 9. Sin estar logueado listamos todos los volúmenes con tamaño 3 y página 1 (no salta excepción)
	 * 
	 * Requisito:
	 * B.8.1: An actor who is not authenticated must be able to list the volumes in the system and browse their newspapers as long as they are
	 * authorised (for instance, a private newspaper cannot be fully displayed to
	 * unauthenticated actors)
	 */
	@Test()
	public void testFindAllPaginated() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "findAllPaginated", false, null, 1, 3, 1, 3, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 1, 2, 2, 2, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 2, 1, 2, 2, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 3, 0, 2, 2, null
			}, {
				"customer", "customer1", "findAllPaginated", false, null, 1, 3, 1, 7, null
			}, {
				"user", "user1", "findAllPaginated", false, null, 1, 3, 1, 3, null
			}, {
				"admin", "admin", "findAllPaginated", false, null, 1, 3, 1, 3, null
			}, {
				"agent", "agent1", "findAllPaginated", false, null, 1, 3, 1, 3, null
			}, {
				null, null, "findAllPaginated", false, null, 1, 3, 1, 3, null
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
		Collection<Volume> volumes;
		int newspaperIdAux;
		int newspaperId;
		int customerId;

		caught = null;
		volumes = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				volumes = this.volumeService.findAll(); //Cogemos todos los volúmenes usando el findAll
			else if (method.equals("findByNewspaperId")) {
				newspaperIdAux = super.getEntityId(bean);
				newspaperId = 0;
				if (user != null && user.equals("admin")) {
					for (int i = 1; i <= this.newspaperService.findAllPaginated(1, 5).getTotalPages(); i++)
						//Cogemos el newspaper entre todos
						for (final Newspaper n : this.newspaperService.findAllPaginated(i, 5))
							if (n.getId() == newspaperIdAux)
								newspaperId = n.getId();
				} else
					newspaperId = super.getEntityId(bean); //Si no lo cogemos directamente

				if (falseId == false)
					volumes = this.volumeService.findByNewspaperId(newspaperId);
				else
					volumes = this.volumeService.findByNewspaperId(0);
			} else if (method.equals("findByCustomerIdAndNewspaperId")) {
				if (user != null) {
					newspaperIdAux = super.getEntityId(bean);
					newspaperId = 0;
					for (int i = 1; i <= this.newspaperService.findAllPaginated(1, 5).getTotalPages(); i++)
						//Cogemos el newspaper entre todos los newspaper
						for (final Newspaper n : this.newspaperService.findAllPaginated(i, 5))
							if (n.getId() == newspaperIdAux)
								newspaperId = n.getId();
				} else
					newspaperId = super.getEntityId(bean); //Si no se coge directamente
				if (user != null && user.equals("customer"))
					customerId = super.getEntityId(username);
				else
					customerId = super.getEntityId(bean);

				volumes = this.volumeService.findByCustomerIdAndNewspaperId(customerId, newspaperId); //Volúmenes donde el cliente está suscrito y tiene ese newspaper
			}

			Assert.isTrue(volumes.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<Volume> volumes;
		int userId;

		caught = null;
		volumes = null;
		userId = 0;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findByUserAccountId")) {
				if (user != null && user.equals("user"))
					userId = super.getEntityId(username);
				if (falseId == false) {
					if (bean == null)
						volumes = this.volumeService.findByUserAccountId(this.userService.findOne(userId).getUserAccount().getId(), page, tam); //Todos los volúmenes del user logeado
					else
						volumes = this.volumeService.findByUserAccountId(this.userService.findOne(super.getEntityId(bean)).getUserAccount().getId(), page, tam); //Los volúmenes de user que pasas
				} else
					volumes = this.volumeService.findByUserAccountId(0, page, tam); //Los volúmenes con id cero
			} else if (method.equals("findAllPaginated"))
				volumes = this.volumeService.findAllPaginated(page, tam); //FindAllPaginated

			Assert.isTrue(volumes.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(volumes.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
