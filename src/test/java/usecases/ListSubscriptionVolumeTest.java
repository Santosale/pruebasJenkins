
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

import services.SubscriptionVolumeService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.SubscriptionVolume;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListSubscriptionVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private VolumeService				volumeService;

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;


	/*
	 * Test
	 * 1. Hacemos el findAll logeados como user1
	 * 2. Hacemos el findAll logeados como customer1
	 * 3. Hacemos el findAll logeados como admin
	 * 4. Hacemos el findAll logeados como agent1
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 5, null, null, null
			}, {
				"customer", "customer1", "findAll", false, null, null, 5, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 5, null, null, null
			}, {
				"agent", "agent1", "findAll", false, null, null, 5, null, null, null
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
	 * 1. Logeados como admin vemos que la colección de suscripciones para el volume1 es de 2
	 * 2. Logeados como admin vemos que la colección de suscripciones para el volume2 es de 2
	 * 3. Logeados como admin vemos que la colección de suscripciones para el volume3 es de 1
	 * 4. Sin estar logeados vemos que la colección de suscripciones para el volume1 es de 2 (salta un IllegalArgumentException)
	 * 5. Logeados como admin vemos que la colección de suscripciones para un volumen con id 0 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testFindByVolumeId() {
		final Object testingData[][] = {
			{
				"admin", "admin", "findByVolumeId", false, "volume1", null, 2, null, null, null
			}, {
				"admin", "admin", "findByVolumeId", false, "volume2", null, 2, null, null, null
			}, {
				"admin", "admin", "findByVolumeId", false, "volume3", null, 1, null, null, null
			}, {
				null, null, "findByVolumeId", false, "volume1", null, 2, null, null, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByVolumeId", true, "volume1", null, 2, null, null, IllegalArgumentException.class
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
	 * 1. Logeados como customer1 listamos tus suscripciones con tamaño 3, la página 1 (no salta excepción)
	 * 2. Logeados como customer1 listamos tus suscripciones con tamaño 2, la página 1 (no salta excepción)
	 * 3. Logeados como customer1 listamos tus suscripciones con tamaño 2, la página 2 (no salta excepción)
	 * 4. Logeados como customer1 listamos tus suscripciones con tamaño 2, la página 3 (no salta excepción)
	 * 5. Logeados como customer1 listamos tus suscripciones con tamaño 5, la página 1 (no salta excepción)
	 * 6. Logeados como user1 listamos las suscripciones del customer1 con tamaño 3, la página 1 (salta un IllegalArgumentException)
	 * 7. Logeados como admin listamos las suscripciones del customer1 con tamaño 3, la página 1 (salta un IllegalArgumentException)
	 * 8. Logeados como user2 listamos las suscripciones del customer1 con tamaño 3, la página 1 (salta un IllegalArgumentException)
	 * 9. Logeados como agent1 listamos las suscripciones del customer1 con tamaño 3, la página 1 (salta un IllegalArgumentException)
	 * 10.Sin estar logeados listamos las suscripciones del customer1 con tamaño 3, la página 1 (salta un IllegalArgumentException)
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
				"user", "user1", "findByCustomerId", false, "customer1", 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				"admin", "admin", "findByCustomerId", false, "customer1", 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				"user", "user2", "findByCustomerId", false, "customer1", 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				"agent", "agent1", "findByCustomerId", false, "customer1", 1, 3, 1, 3, IllegalArgumentException.class
			}, {
				null, null, "findByCustomerId", false, "customer1", 1, 3, 1, 3, IllegalArgumentException.class
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
		Collection<SubscriptionVolume> subscriptionVolumes;
		int volumeIdAux;
		int volumeId;

		caught = null;
		subscriptionVolumes = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				subscriptionVolumes = this.subscriptionVolumeService.findAll(); //Cogemos todas las suscripciones usando el findAll
			else if (method.equals("findByVolumeId")) {
				volumeIdAux = super.getEntityId(bean);
				volumeId = 0;
				for (final Volume v : this.volumeService.findAll())
					//Cogemos el volume entre todos los volúmenes
					if (v.getId() == volumeIdAux)
						volumeId = v.getId();

				if (falseId == false)
					subscriptionVolumes = this.subscriptionVolumeService.findByVolumeId(volumeId); //Cogemos todas las suscripciones de ese volumen
				else
					subscriptionVolumes = this.subscriptionVolumeService.findByVolumeId(0); //Cogemos las suscripciones de un volumen con id 0

			}

			Assert.isTrue(subscriptionVolumes.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void template2(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer totalPage, final Integer tam, final Class<?> expected) {
		Class<?> caught;
		Page<SubscriptionVolume> subscriptionVolumes;
		int customerId;

		caught = null;
		subscriptionVolumes = null;
		customerId = 0;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findByCustomerId")) { //FindByCustomerId
				if (user != null && user.equals("customer"))
					customerId = super.getEntityId(username);
				if (falseId == false) {
					if (bean == null)
						subscriptionVolumes = this.subscriptionVolumeService.findByCustomerId(customerId, page, tam); //Suscripciones del customer logeado
					else
						subscriptionVolumes = this.subscriptionVolumeService.findByCustomerId(super.getEntityId(bean), page, tam); //Se coge las suscripciones de un customer en concreto
				} else
					subscriptionVolumes = this.subscriptionVolumeService.findByCustomerId(0, page, tam); //Las suscripciones de un volumen con id 0
			}

			Assert.isTrue(subscriptionVolumes.getContent().size() == size); //Se compara el tamaño con el esperado
			Assert.isTrue(subscriptionVolumes.getTotalPages() == totalPage);//Se compara el total de páginas con las esperadas

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
