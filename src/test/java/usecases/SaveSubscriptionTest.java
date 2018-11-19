
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

import services.CreditCardService;
import services.PlanService;
import services.SubscriptionService;
import utilities.AbstractTest;
import domain.CreditCard;
import domain.Plan;
import domain.Subscription;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveSubscriptionTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private PlanService			planService;

	@Autowired
	private SubscriptionService	subscriptionService;

	@Autowired
	private CreditCardService	creditCardService;


	/*
	 * Pruebas:
	 * 1. Probamos el save con el user5 para suscribirse al plan1 con frecuencia mensual
	 * 2. Probamos el save con el user5 para suscribirse al plan1 con frecuencia cuatrimestral
	 * 3. Probamos el save con el user5 para suscribirse al plan1 con frecuencia anual
	 * 4. Probamos el save con el user5 para suscribirse al plan2 con frecuencia mensual
	 * 5. Probamos el save con el user5 para suscribirse al plan2 con frecuencia cuatrimestral
	 * 6. Probamos el save con el user5 para suscribirse al plan2 con frecuencia anual
	 * 
	 * Requisito 25.8: Un usuario logueado como usuario debe poder suscribirse a un plan de pago indicando la tarjeta de
	 * crédito a la que se realizará el cargo y si quiere que el pago se realice de forma mensual,
	 * trimestral o anual. Además, podrá anular su suscripción y editar tanto la tarjeta de crédito como la frecuencia de pago.
	 * En caso de querer cambiar el plan, tendría que cancelar el actual y contratar uno nuevo.
	 */
	@Test()
	public void testSavePositive() {
		final Object testingData[][] = {
			{
				"user", "user5", "Monthly", "creditCard11", "plan1", null
			}, {
				"user", "user5", "Quarterly", "creditCard11", "plan1", null
			}, {
				"user", "user5", "Anually", "creditCard11", "plan1", null
			}, {
				"user", "user5", "Monthly", "creditCard11", "plan2", null
			}, {
				"user", "user5", "Quarterly", "creditCard11", "plan2", null
			}, {
				"user", "user5", "Anually", "creditCard11", "plan2", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Probamos a crear una suscripción con la frecuencia de pago vacía (salta un ConstraintViolationEXception)
	 * 2. Probamos a crear una suscripción con la frecuencia de pago a nulo (salta un ConstraintViolationEXception)
	 * 3. Probamos a crear una suscripción con la frecuencia de pago con un formato incorrecto (salta un ConstraintViolationEXception)
	 * 4. Probamos a crear una suscripción con una tarjeta de crédito caducada (salta un IllegalArgumentException)
	 * 5. Probamos a crear una suscripción con una tarjeta de crédito de otro usuario (salta un IllegalArgumentException)
	 * 6. Probamos a crear una suscripción con un usuario que ya tiene una suscripción hecha (salta un IllegalArgumentException)
	 * 7. Probamos a crear una suscripción logueados como moderador (salta un IllegalArgumentException)
	 * 8. Probamos a crear una suscripción logueados como company (salta un IllegalArgumentException)
	 * 9. Probamos a crear una suscripción logueados como sponsor (salta un IllegalArgumentException)
	 * 10.Probamos a crear una suscripción logueados como admin (salta un IllegalArgumentException)
	 * 11.Probamos a crear una suscripción sin estar logueado (salta un IllegalArgumentException)
	 */
	@Test()
	public void testSaveNegative() {
		final Object testingData[][] = {
			{
				"user", "user5", "", "creditCard11", "plan1", ConstraintViolationException.class
			}, {
				"user", "user5", null, "creditCard11", "plan1", ConstraintViolationException.class
			}, {
				"user", "user5", "dbueued", "creditCard11", "plan1", ConstraintViolationException.class
			}, {
				"user", "user5", "Anually", "creditCard12", "plan1", IllegalArgumentException.class
			}, {
				"user", "user5", "Monthly", "creditCard1", "plan2", IllegalArgumentException.class
			}, {
				"user", "user1", "Quarterly", "creditCard1", "plan2", IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "Monthly", "creditCard1", "plan1", IllegalArgumentException.class
			}, {
				"comapany", "company1", "Monthly", "creditCard1", "plan1", IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "Monthly", "creditCard1", "plan1", IllegalArgumentException.class
			}, {
				"admin", "admin", "Monthly", "creditCard1", "plan1", IllegalArgumentException.class
			}, {
				null, null, "Monthly", "creditCard1", "plan1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String payFrecuency, final String creditCardBean, final String planBean, final Class<?> expected) {
		Class<?> caught;
		Subscription subscription;
		Subscription saved;
		int planId;
		int planIdAux;
		CreditCard creditCard;

		DataBinder binder;
		Subscription subscriptionReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			planIdAux = super.getEntityId(planBean);
			planId = 0;
			for (final Plan p : this.planService.findAll())
				if (p.getId() == planIdAux)
					planId = p.getId();

			subscription = this.subscriptionService.create(planId); //Creamos la suscripción
			if (creditCardBean != null)
				creditCard = this.creditCardService.findOne(super.getEntityId(creditCardBean));
			else
				creditCard = null;
			subscription.setCreditCard(creditCard);
			subscription.setPayFrecuency(payFrecuency);
			//Editamos los valores

			binder = new DataBinder(subscription);
			subscriptionReconstruct = this.subscriptionService.reconstruct(subscription, binder.getBindingResult()); //Lo reconstruimos
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

}
