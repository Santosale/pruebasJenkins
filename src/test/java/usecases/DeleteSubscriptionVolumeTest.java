
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.SubscriptionVolumeService;
import utilities.AbstractTest;
import domain.SubscriptionVolume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteSubscriptionVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;


	/*
	 * Test
	 * 1. Nos logeamos como customer1 y borramos el subscriptionVolume1 (no salta excepción)
	 * 2. Nos logeamos como customer1 y borramos el subscriptionVolume2 (no salta excepción)
	 * 3. Nos logeamos como customer1 y borramos el subscriptionVolume3 (no salta excepción)
	 * 4. Nos logeamos como customer2 y borramos el subscriptionVolume3 (salta un IllegalArgumentException)
	 * 5. Nos logeamos como user1 y borramos el subscriptionVolume3 (salta un IllegalArgumentException)
	 * 6. Nos logeamos como agent1 y borramos el subscriptionVolume3 (salta un IllegalArgumentException)
	 * 7. Nos logeamos como admin y borramos el subscriptionVolume3 (salta un IllegalArgumentException)
	 * 8. No nos logeamos y borramos el subscriptionVolume3 (salta un IllegalArgumentException)
	 */
	@Test
	public void deleteTest() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "subscriptionVolume1", true, null
			}, {
				"customer", "customer1", "subscriptionVolume2", true, null
			}, {
				"customer", "customer1", "subscriptionVolume3", true, null
			}, {
				"customer", "customer2", "subscriptionVolume3", false, IllegalArgumentException.class
			}, {
				"user", "user1", "subscriptionVolume3", false, IllegalArgumentException.class
			}, {
				"agent", "agent1", "subscriptionVolume3", false, IllegalArgumentException.class
			}, {
				"admin", "admin", "subscriptionVolume3", false, IllegalArgumentException.class
			}, {
				null, null, "subscriptionVolume3", false, IllegalArgumentException.class
			}

		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateDelete((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void templateDelete(final String user, final String username, final String subscriptionVolumeBean, final boolean mySubscription, final Class<?> expected) {
		Class<?> caught;
		int subscriptionVolumeId;
		SubscriptionVolume subscriptionVolume;
		int subscriptionVolumeIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			subscriptionVolumeId = 0;
			if (user != null && mySubscription == true) {
				subscriptionVolumeIdAux = super.getEntityId(subscriptionVolumeBean);
				for (int i = 1; i <= this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), 1, 5).getTotalPages(); i++)
					for (final SubscriptionVolume s : this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), i, 5).getContent())
						//Si estás logeado lo buscas entre todas las suscripciones
						if (subscriptionVolumeIdAux == s.getId())
							subscriptionVolumeId = s.getId();
			} else
				subscriptionVolumeId = super.getEntityId(subscriptionVolumeBean); //Si no lo cogemos directamente

			subscriptionVolume = this.subscriptionVolumeService.findOneToEdit(subscriptionVolumeId);
			this.subscriptionVolumeService.delete(subscriptionVolume); //Borramos la suscripción

			Assert.isTrue(!this.subscriptionVolumeService.findAll().contains(subscriptionVolume)); //Miramos que la suscripción borrada no esté entre las suscripciones del sistema

			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
