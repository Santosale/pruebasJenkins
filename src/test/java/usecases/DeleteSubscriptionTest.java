
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.SubscriptionService;
import services.UserService;
import utilities.AbstractTest;
import domain.Subscription;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteSubscriptionTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionService	subscriptionService;

	@Autowired
	private UserService			userService;


	/*
	 * Pruebas:
	 * 1. Borramos la suscripcion1 autenticados como user1 (no salta excepción)
	 * 2. Borramos la suscripcion2 autenticados como user2 (no salta excepción)
	 * 3. Borramos la suscripcion3 autenticados como user3 (no salta excepción)
	 * 4. Borramos la suscripcion4 autenticados como user4 (no salta excepción)
	 * 5. Borramos la suscripcion1 autenticados como user2 que no tiene esa suscripción (salta un IllegalArgumentException)
	 * 6. Borramos la suscripcion1 autenticados como sponsor (salta un IllegalArgumentException)
	 * 7. Borramos la suscripcion1 autenticados como company (salta un IllegalArgumentException)
	 * 8. Borramos la suscripcion1 autenticados como moderator (salta un IllegalArgumentException)
	 * 9. Borramos la suscripcion1 autenticados como admin (salta un IllegalArgumentException)
	 * 10.Borramos la suscripcion1 sin estar autenticados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.8: Un actor autenticado como user puede
	 * suscribirse a un plan de pago indicando la tarjeta de crédito a la que se realizará
	 * el cargo y si quiere que el pago se realice de forma mensual, trimestral o anual. Además, podrá anular
	 * su suscripción y editar tanto la tarjeta de crédito como la frecuencia de pago. En caso de querer cambiar el plan,
	 * tendría que cancelar el actual y contratar uno nuevo.
	 */
	@Test()
	public void testDelete() {
		final Object testingData[][] = {
			{
				"user", "user1", "subscription1", false, null
			}, {
				"user", "user2", "subscription2", false, null
			}, {
				"user", "user3", "subscription3", false, null
			}, {
				"user", "user4", "subscription4", false, null
			}, {
				"user", "user2", "subscription1", true, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "subscription1", true, IllegalArgumentException.class
			}, {
				"company", "company1", "subscription1", true, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "subscription1", true, IllegalArgumentException.class
			}, {
				"admin", "admin", "subscription1", true, IllegalArgumentException.class
			}, {
				null, null, "subscription1", true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String subscriptionBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		Subscription subscription;
		int subscriptionId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (falseId == false)
				subscriptionId = this.subscriptionService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()).getId();
			else
				subscriptionId = super.getEntityId(subscriptionBean);

			subscription = this.subscriptionService.findOneToEdit(subscriptionId);

			this.subscriptionService.delete(subscription); //Borramos la suscripción
			super.flushTransaction();

			Assert.isTrue(!this.subscriptionService.findAll().contains(subscription)); //Miramos si no está entre todos las suscripciones de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
