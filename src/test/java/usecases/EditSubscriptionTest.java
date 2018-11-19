
package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import security.LoginService;
import services.ActorService;
import services.CreditCardService;
import services.SubscriptionService;
import services.UserService;
import utilities.AbstractTest;
import domain.CreditCard;
import domain.Subscription;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditSubscriptionTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionService	subscriptionService;

	@Autowired
	private CreditCardService	creditCardService;

	@Autowired
	private UserService			userService;

	@Autowired
	private ActorService		actorService;


	/*
	 * Pruebas:
	 * 1. Probamos a editar la suscripción1 logueados como user1 usando la creditcard3 (no salta excepción)
	 * 2. Probamos a editar la suscripción2 logueados como user2 usando la creditcard2 (no salta excepción)
	 * 3. Probamos a editar la suscripción3 logueados como user3 usando la creditcard4 (no salta excepción)
	 * 4. Probamos a editar la suscripción4 logueados como user4 usando la creditcard5 (no salta excepción)
	 * 5. Probamos a editar la suscripción2 logueados como user2 poniendo el pago de frecuencia vacío (salta un ConstraintViolationException)
	 * 6. Probamos a editar la suscripción2 logueados como user2 poniendo el pago de frecuencia a nulo (salta un ConstraintViolationException)
	 * 7. Probamos a editar la suscripción2 logueados como user2 poniendo el pago de frecuencia con un formato incorrecto (salta un ConstraintViolationException)
	 * 8. Probamos a editar la suscripción2 logueados como user1 el cual no posee esa descripción (salta un IllegalArgumentException)
	 * 9. Probamos a editar la suscripción1 logueados como user1 usando una tarjeta de crédito caducada (salta un IllegalArgumentException)
	 * 10.Probamos a editar la suscripción2 logueados como moderador (salta un IllegalArgumentException)
	 * 11.Probamos a editar la suscripción2 logueados como company (salta un IllegalArgumentException)
	 * 12.Probamos a editar la suscripción2 logueados como sponsor (salta un IllegalArgumentException)
	 * 13.Probamos a editar la suscripción2 logueados como admin (salta un IllegalArgumentException)
	 * 14.Probamos a editar la suscripción2 sin estar logueado (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.8: Un actor autenticado como user puede
	 * suscribirse a un plan de pago indicando la tarjeta de crédito a la que se realizará
	 * el cargo y si quiere que el pago se realice de forma mensual, trimestral o anual. Además, podrá anular
	 * su suscripción y editar tanto la tarjeta de crédito como la frecuencia de pago. En caso de querer cambiar el plan,
	 * tendría que cancelar el actual y contratar uno nuevo.
	 */
	@Test()
	public void testEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "subscription1", "Monthly", "creditCard3", false, null
			}, {
				"user", "user2", "subscription2", "Anually", "creditCard2", false, null
			}, {
				"user", "user3", "subscription3", "Anually", "creditCard4", false, null
			}, {
				"user", "user4", "subscription4", "Anually", "creditCard5", false, null
			}, {
				"user", "user2", "subscription2", "", "creditCard2", false, ConstraintViolationException.class
			}, {
				"user", "user2", "subscription2", null, "creditCard2", false, ConstraintViolationException.class
			}, {
				"user", "user2", "subscription2", "iejei", "creditCard2", false, ConstraintViolationException.class
			}, {
				"user", "user1", "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}, {
				"user", "user1", "subscription1", "Anually", "creditCard10", false, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}, {
				"company", "company1", "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}, {
				"admin", "admin", "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}, {
				null, null, "subscription2", "Anually", "creditCard2", true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (boolean) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logeados como user1 hacemos el findOneToEdit de la suscripción1 (no salta excepción)
	 * 2.Logeados como user2 hacemos el findOneToEdit de la suscripción 2 (no salta excepción)
	 * 3.Logeados como admin hacemos el findOne de la suscripción1 (no salta excepción)
	 * 4.Logeados como admin hacemos el findOneToEdit de la suscripción1 (salta un IllegalArgumentException)
	 * 5.Logeados como user1 hacemos el findOneToEdit de un suscripción de id 0 (salta un IllegalArgumentException)
	 * 6.Logeados como user1 hacemos el findOne de una suscripción de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "subscription1", false, true, null
			}, {
				"user", "user2", "subscription2", false, true, null
			}, {
				"admin", "admin", "subscription1", false, false, null
			}, {
				"admin", "admin", "subscription1", false, true, IllegalArgumentException.class
			}, {
				"user", "user1", "subscription1", true, false, IllegalArgumentException.class
			}, {
				"user", "user1", "subscription1", true, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Vemos que la suscripcion1 es del user1
	 * 2. Vemos que la suscripcion2 es del user2
	 * 3. Vemos que la suscripcion3 es del user3
	 * 4. Vemos que la suscripcion4 es del user4
	 * 5. Vemos que el user5 no tiene suscripcion
	 * 6. Vemos que el user6 no tiene suscripcion
	 * 7. Vemos que los sponsors no tienen suscripción
	 * 8. Vemos que las compañias no tienen suscripción
	 * 9. Vemos que los moderadores no tienen suscripción
	 * 10.Vemos que los admin no tienen suscripción
	 * 11.Vemos que los no autenticados no tienen suscripción
	 */
	@Test()
	public void testFindByUser() {
		final Object testingData[][] = {
			{
				"user", "user1", "subscription1", null
			}, {
				"user", "user2", "subscription2", null
			}, {
				"user", "user3", "subscription3", null
			}, {
				"user", "user4", "subscription4", null
			}, {
				"user", "user5", null, null
			}, {
				"user", "user6", null, null
			}, {
				"sponsor", "sponsor1", null, null
			}, {
				"company", "company1", null, null
			}, {
				"moderator", "moderator1", null, null
			}, {
				"admin", "admin", null, null
			}, {
				null, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindByUser((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String subscriptionBean, final String payFrecuency, final String creditCardBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		Subscription subscription;
		Subscription saved;
		Subscription copySubscription;
		int subscriptionId;
		CreditCard creditCard;

		DataBinder binder;
		Subscription subscriptionReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (falseId == false)
				subscriptionId = this.subscriptionService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()).getId();
			else
				subscriptionId = super.getEntityId(subscriptionBean);

			subscription = this.subscriptionService.findOneToEdit(subscriptionId);
			copySubscription = this.copySubscription(subscription);
			if (creditCardBean != null)
				creditCard = this.creditCardService.findOne(super.getEntityId(creditCardBean));
			else
				creditCard = null;
			copySubscription.setCreditCard(creditCard);
			copySubscription.setPayFrecuency(payFrecuency);
			//Editamos los valores

			binder = new DataBinder(copySubscription);
			subscriptionReconstruct = this.subscriptionService.reconstruct(copySubscription, binder.getBindingResult()); //Lo reconstruimos
			saved = this.subscriptionService.save(subscriptionReconstruct); //Guardamos la suscripción
			super.flushTransaction();

			Assert.isTrue(this.subscriptionService.findAll().contains(saved)); //Miramos si están entre todos las suscripciones de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String subscriptionBean, final boolean falseId, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		Subscription subscription;
		int subscriptionId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			subscriptionId = super.getEntityId(subscriptionBean);

			if (findOneToEdit == true) {
				if (falseId == false)
					subscription = this.subscriptionService.findOneToEdit(subscriptionId); //Se prueba el findOneToEdit
				else
					subscription = this.subscriptionService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				subscription = this.subscriptionService.findOne(subscriptionId); //Se prueba el findOne
			else
				subscription = this.subscriptionService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(subscription); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindByUser(final String user, final String username, final String subscriptionBean, final Class<?> expected) {
		Class<?> caught;
		Subscription subscription;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (user != null)
				subscription = this.subscriptionService.findByUserId(this.actorService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()); //Cogemos la suscripción si la tiene el usuario
			else
				subscription = null;

			if (subscriptionBean != null)
				Assert.isTrue(subscription.equals(this.subscriptionService.findOne(super.getEntityId(subscriptionBean))));
			else
				Assert.isTrue(subscription == null);

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public Subscription copySubscription(final Subscription subscription) {
		Subscription result;

		result = new Subscription();
		result.setCreditCard(subscription.getCreditCard());
		result.setId(subscription.getId());
		result.setVersion(subscription.getVersion());
		result.setPayFrecuency(subscription.getPayFrecuency());
		result.setPlan(subscription.getPlan());
		result.setUser(subscription.getUser());

		return result;
	}

}
