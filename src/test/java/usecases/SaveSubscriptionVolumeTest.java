
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

import services.CustomerService;
import services.SubscriptionVolumeService;
import services.VolumeService;
import utilities.AbstractTest;
import domain.CreditCard;
import domain.SubscriptionVolume;
import domain.Volume;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveSubscriptionVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private VolumeService				volumeService;

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;

	@Autowired
	private CustomerService				customerService;


	/*
	 * Test
	 * 1. Logueados como customer3 creamos una suscripción para el volume1 con datos correctos (no salta excepción)
	 * 2. Logueados como customer3 creamos una suscripción para el volume2 con datos correctos (no salta excepción)
	 * 3. Logueados como customer3 creamos una suscripción para el volume3 con datos correctos (no salta excepción)
	 * 4. Logueados como customer2 creamos una suscripción para el volume3 con datos correctos (no salta excepción)
	 * 5. Logueados como customer3 creamos una suscripción para el volume1 sobreescribiendo el user por ti mismo con datos correctos (no salta excepción)
	 * 6. Logueados como customer3 creamos una suscripción para el volume1 sobreescribiendo el volume1 con datos correctos (no salta excepción)
	 * 
	 * Requisitos
	 * B.9.1: An actor who is authenticated as a customer must be able to subscribe to a volume by providing a credit card. Note that subscribing to a volume
	 * implies subscribing automatically to all of the newspapers of which it is composed,
	 * including newspapers that might be published after the subscription takes place
	 */
	@Test()
	public void testCreatePositive() {
		final Object testingData[][] = {
			{
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, null
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 8, 18, 333, null, "volume2", null, null
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 8, 18, 333, null, "volume3", null, null
			}, {
				"customer", "customer2", "Antonio", "MasterCard", "5150802693647048", 8, 18, 333, null, "volume3", null, null
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, "customer3", "volume1", null, null
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", "volume1", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (int) testingData[i][7],
					(String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Logueados como customer3 creamos una suscripción para el volume1 con los campos de texto a nulo (salta un ConstraintViolationException)
	 * 2. Logueados como customer3 creamos una suscripción para el volume1 con el number a nulo (salta un ConstraintViolationException)
	 * 3. Logueados como customer3 creamos una suscripción para el volume1 con el brandName a nulo (salta un ConstraintViolationException)
	 * 4. Logueados como customer3 creamos una suscripción para el volume1 con el holderName a nulo (salta un ConstraintViolationException)
	 * 5. Logueados como customer3 creamos una suscripción para el volume1 con un number erróneo a nulo (salta un ConstraintViolationException)
	 * 6. Logueados como customer1 creamos una suscripción para el volume1 al cual ya tienes una suscripción (salta un IllegalArgumentException)
	 * 7. Logueados como customer3 creamos una suscripción para el volume1 con el mes y año de expiración el actual (salta un IllegalArgumentException)
	 * 8. Logueados como customer3 creamos una suscripción para el volume1 con el mes y año de expiración en el pasado (salta un IllegalArgumentException)
	 * 9. Logueados como customer3 creamos una suscripción para el volume1 sobreescribiendo el creador por el customer1 (salta un IllegalArgumentException)
	 * 10.Logueados como customer2 creamos una suscripción para el volume3 sobreescribiendo el volume por el volume2 al cual ya estás suscrito (salta un IllegalArgumentException)
	 * 11.Logueados como user1 creamos una suscripción para el volume1 (salta un IllegalArgumentException)
	 * 12.Logueados como agent1 creamos una suscripción para el volume1 (salta un IllegalArgumentException)
	 * 13.Logueados como admin creamos una suscripción para el volume1 (salta un IllegalArgumentException)
	 * 14.Sin estar logueados creamos una suscripción para el volume1 (salta un IllegalArgumentException)
	 */
	@Test()
	public void testCreateNegative() {
		final Object testingData[][] = {
			{
				"customer", "customer3", null, null, null, 5, 19, 245, null, "volume1", null, ConstraintViolationException.class
			}, {
				"customer", "customer3", "Antonio", "MasterCard", null, 5, 19, 245, null, "volume1", null, ConstraintViolationException.class
			}, {
				"customer", "customer3", "Antonio", null, "5150802693647048", 5, 19, 245, null, "volume1", null, ConstraintViolationException.class
			}, {
				"customer", "customer3", null, "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, ConstraintViolationException.class
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "1", 5, 19, 245, null, "volume1", null, ConstraintViolationException.class
			}, {
				"customer", "customer1", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 5, 18, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 4, 18, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				"customer", "customer3", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, "customer1", "volume1", null, IllegalArgumentException.class
			}, {
				"customer", "customer2", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume3", "volume2", IllegalArgumentException.class
			}, {
				"user", "user1", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				"agent", "agent1", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				"admin", "admin", "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, IllegalArgumentException.class
			}, {
				null, null, "Antonio", "MasterCard", "5150802693647048", 5, 19, 245, null, "volume1", null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (int) testingData[i][7],
					(String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String holderName, final String brandName, final String number, final int expirationMonth, final int expirationYear, final int cvvcode, final String customerBean,
		final String volumeBean, final String volumeBean2, final Class<?> expected) {
		Class<?> caught;
		SubscriptionVolume subscriptionVolume;
		SubscriptionVolume saved;
		int volumeId;
		int volumeIdAux;
		CreditCard creditCard;

		DataBinder binder;
		SubscriptionVolume subscriptionVolumeReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			volumeIdAux = super.getEntityId(volumeBean);
			volumeId = 0;
			for (int i = 1; i <= this.volumeService.findAllPaginated(1, 5).getTotalPages(); i++)
				//Cogemos el volumen entre todos los volúmenes
				for (final Volume v : this.volumeService.findAllPaginated(i, 5))
					if (v.getId() == volumeIdAux)
						volumeId = v.getId();

			subscriptionVolume = this.subscriptionVolumeService.create(volumeId); //Creamos la suscripción
			creditCard = new CreditCard();
			creditCard.setHolderName(holderName);
			creditCard.setBrandName(brandName);
			creditCard.setNumber(number);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setCvvcode(cvvcode);
			subscriptionVolume.setCreditCard(creditCard);
			//Editamos los valores

			if (customerBean != null)
				subscriptionVolume.setCustomer(this.customerService.findOne(super.getEntityId(customerBean))); //Cambiamos el customer para hackeos
			if (volumeBean2 != null)
				subscriptionVolume.setVolume(this.volumeService.findOne(super.getEntityId(volumeBean2))); //Cambiamos el volume para hackeos

			binder = new DataBinder(subscriptionVolume);
			subscriptionVolumeReconstruct = this.subscriptionVolumeService.reconstruct(subscriptionVolume, binder.getBindingResult()); //Lo reconstruimos
			saved = this.subscriptionVolumeService.save(subscriptionVolumeReconstruct); //Guardamos la suscripción
			super.flushTransaction();

			Assert.isTrue(this.subscriptionVolumeService.findAll().contains(saved)); //Miramos si están entre todos las suscripciones de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
