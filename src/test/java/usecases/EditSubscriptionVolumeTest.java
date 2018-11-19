
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
public class EditSubscriptionVolumeTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;

	@Autowired
	private VolumeService				volumeService;


	/*
	 * Test
	 * 1.Editamos los campos del subscriptionVolume1 por valores correctos logeados como customer1 (no salta excepción)
	 * 2.Editamos los campos del subscriptionVolume1 poniendo el holderName a nulo como customer1 (salta un ConstraintViolationException)
	 * 3.Editamos los campos del subscriptionVolume1 poniendo el brandName a nulo como customer1 (salta un ConstraintViolationException)
	 * 4.Editamos los campos del subscriptionVolume1 poniendo el number a nulo como customer1 (salta un ConstraintViolationException)
	 * 5.Editamos los campos del subscriptionVolume1 poniendo el todos los campos de texto a nulo como customer1 (salta un ConstraintViolationException)
	 * 6.Editamos los campos del subscriptionVolume1 poniendo el number con un formato incorrecto como customer1 (salta un ConstraintViolationException)
	 * 7.Editamos los campos del subscriptionVolume1 poniendo el el año de expiración de la tarjeta en el pasadp como customer1 (salta un IllegalArgumentException)
	 * 8.Editamos los campos del subscriptionVolume4 logeados como customer1 el cual no tiene esa suscripción (salta un IllegalArgumentException)
	 * 9.Editamos los campos del subscriptionVolume1 logeados como user1 el cual no tiene esa suscripción (salta un IllegalArgumentException)
	 * 10.Editamos los campos del subscriptionVolume1 logeados como admin el cual no tiene esa suscripción (salta un IllegalArgumentException)
	 * 11.Editamos los campos del subscriptionVolume1 logeados como agent1 el cual no tiene esa suscripción (salta un IllegalArgumentException)
	 * 12.Editamos los campos del subscriptionVolume1 sin estar logeados (salta un IllegalArgumentException)
	 */
	@Test()
	public void testEdit() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 19, 245, true, null
			}, {
				"customer", "customer1", "subscriptionVolume1", null, "Visa", "5231322555864535", 5, 19, 245, true, ConstraintViolationException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", "Paco", null, "5231322555864535", 5, 19, 245, true, ConstraintViolationException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", "Paco", "Visa", null, 5, 19, 245, true, ConstraintViolationException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", null, null, null, 5, 19, 245, true, ConstraintViolationException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", "Paco", "Visa", "5", 5, 19, 245, true, ConstraintViolationException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 17, 245, true, IllegalArgumentException.class
			}, {
				"customer", "customer1", "subscriptionVolume4", "Paco", "Visa", "5231322555864535", 5, 19, 245, false, IllegalArgumentException.class
			}, {
				"user", "user1", "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 19, 245, false, IllegalArgumentException.class
			}, {
				"admin", "admin", "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 19, 245, false, IllegalArgumentException.class
			}, {
				"agent", "agent1", "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 19, 245, false, IllegalArgumentException.class
			}, {
				null, null, "subscriptionVolume1", "Paco", "Visa", "5231322555864535", 5, 19, 245, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (int) testingData[i][6], (int) testingData[i][7],
					(int) testingData[i][8], (boolean) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1. Logeados como customer1 hacemos el findOneToEdit del subscriptionVolume1 (no salta excepción)
	 * 2.Logeados como customer1 hacemos el findOneToEdit del subscriptionVolume2 (no salta excepción)
	 * 3.Logeados como customer1 hacemos el findOne del subscriptionVolume1 (no salta excepción)
	 * 4.Logeados como customer1 hacemos el findOneToEdit del subscriptionVolume4 que no es suyo (salta un IllegalArgumentException)
	 * 5.Logeados como customer1 hacemos el findOneToEdit de una suscripción de id 0 (salta un IllegalArgumentException)
	 * 6.Logeados como customer1 hacemos el findOne de una suscripción de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "subscriptionVolume1", false, true, true, null
			}, {
				"customer", "customer1", "subscriptionVolume2", false, true, true, null
			}, {
				"customer", "customer1", "subscriptionVolume1", false, false, false, null
			}, {
				"customer", "customer1", "subscriptionVolume4", false, true, true, IllegalArgumentException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", true, false, true, IllegalArgumentException.class
			}, {
				"customer", "customer1", "subscriptionVolume1", true, false, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Existe una suscripción entre el customer1 y el volume1
	 * 2.Existe una suscripción entre el customer1 y el volume2
	 * 3.Existe una suscripción entre el customer1 y el volume3
	 * 4.No existe una suscripción entre el customer3 y el volume3
	 * 5.Se mira si existe una suscripción para un customer con id 0 y un volumen con id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindByCustomerIdAndVolumeId() {
		final Object testingData[][] = {
			{
				"customer", "customer1", "volume1", null, false, null
			}, {
				"customer", "customer1", "volume2", null, false, null
			}, {
				"customer", "customer1", "volume3", null, false, null
			}, {
				"customer", "customer3", "volume3", null, false, IllegalArgumentException.class
			}, {
				"customer", "customer1", "volume1", null, true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindByCustomerIdAndVolumeId((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String subscriptionVolumeBean, final String holderName, final String brandName, final String number, final int expirationMonth, final int expirationYear, final int cvvcode,
		final boolean mySubscription, final Class<?> expected) {
		Class<?> caught;
		SubscriptionVolume subscriptionVolume;
		SubscriptionVolume saved;
		SubscriptionVolume copySubscriptionVolume;
		int subscriptionVolumeId;
		int subscriptionVolumeIdAux;

		DataBinder binder;
		SubscriptionVolume subscriptionVolumeReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			subscriptionVolumeIdAux = super.getEntityId(subscriptionVolumeBean);
			subscriptionVolumeId = 0;
			if (user != null && mySubscription == true) {
				for (int i = 1; i <= this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), 1, 5).getTotalPages(); i++)
					for (final SubscriptionVolume s : this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), i, 5).getContent())
						if (s.getId() == subscriptionVolumeIdAux)
							subscriptionVolumeId = s.getId();
			} else
				subscriptionVolumeId = super.getEntityId(subscriptionVolumeBean);

			subscriptionVolume = this.subscriptionVolumeService.findOneToEdit(subscriptionVolumeId); //Creamos la suscripción
			copySubscriptionVolume = this.copySubscriptionVolume(subscriptionVolume);
			copySubscriptionVolume.getCreditCard().setHolderName(holderName);
			copySubscriptionVolume.getCreditCard().setBrandName(brandName);
			copySubscriptionVolume.getCreditCard().setNumber(number);
			copySubscriptionVolume.getCreditCard().setExpirationMonth(expirationMonth);
			copySubscriptionVolume.getCreditCard().setExpirationYear(expirationYear);
			copySubscriptionVolume.getCreditCard().setCvvcode(cvvcode);
			//Modificamos sus campos

			binder = new DataBinder(copySubscriptionVolume);
			subscriptionVolumeReconstruct = this.subscriptionVolumeService.reconstruct(copySubscriptionVolume, binder.getBindingResult());
			saved = this.subscriptionVolumeService.save(subscriptionVolumeReconstruct); //Guardamos la suscripción
			super.flushTransaction();

			Assert.isTrue(this.subscriptionVolumeService.findAll().contains(saved)); //Miramos si están entre todos las suscripciones a volumen de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String subscriptionVolumeBean, final boolean falseId, final boolean mySubscriptionVolume, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int subscriptionVolumeId;
		SubscriptionVolume subscriptionVolume;
		int subscriptionVolumeIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			subscriptionVolumeId = 0;
			if (mySubscriptionVolume == true) { //Si es suscripción del logeados la pillamos entre todas sus suscripciones
				subscriptionVolumeIdAux = super.getEntityId(subscriptionVolumeBean);
				for (int i = 1; i <= this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), 1, 5).getTotalPages(); i++)
					for (final SubscriptionVolume s : this.subscriptionVolumeService.findByCustomerId(super.getEntityId(username), i, 5).getContent())
						if (subscriptionVolumeIdAux == s.getId())
							subscriptionVolumeId = s.getId();
			} else
				subscriptionVolumeId = super.getEntityId(subscriptionVolumeBean); // Si no cogemos la id directamente para simular hackeos

			if (findOneToEdit == true) {
				if (falseId == false)
					subscriptionVolume = this.subscriptionVolumeService.findOneToEdit(subscriptionVolumeId); //Se prueba el findOneToEDit
				else
					subscriptionVolume = this.subscriptionVolumeService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				subscriptionVolume = this.subscriptionVolumeService.findOne(subscriptionVolumeId); //Se prueba el findOne
			else
				subscriptionVolume = this.subscriptionVolumeService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(subscriptionVolume); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindByCustomerIdAndVolumeId(final String user, final String username, final String volumeBean, final String customerBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		int volumeId;
		SubscriptionVolume subscriptionVolume;
		int volumeIdAux;
		int customerId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			if (customerBean != null)
				customerId = super.getEntityId(customerBean);
			else
				customerId = super.getEntityId(username);
			volumeIdAux = super.getEntityId(volumeBean);
			volumeId = 0;
			for (int i = 1; i <= this.volumeService.findAllPaginated(1, 5).getTotalPages(); i++)
				//BUscamos entre todos los volúmenes
				for (final Volume v : this.volumeService.findAllPaginated(i, 5).getContent())
					if (volumeIdAux == v.getId())
						volumeId = v.getId();

			if (falseId == false)
				subscriptionVolume = this.subscriptionVolumeService.findByCustomerIdAndVolumeId(customerId, volumeId); //Se mira si existe una suscripción
			else
				subscriptionVolume = this.subscriptionVolumeService.findByCustomerIdAndVolumeId(0, 0); //Se prueba con ambas id a 0

			Assert.notNull(subscriptionVolume); //Se mira si existe
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public SubscriptionVolume copySubscriptionVolume(final SubscriptionVolume subscriptionVolume) {
		SubscriptionVolume result;

		result = new SubscriptionVolume();
		result.setCreditCard(subscriptionVolume.getCreditCard());
		result.setId(subscriptionVolume.getId());
		result.setVersion(subscriptionVolume.getVersion());
		result.setCustomer(subscriptionVolume.getCustomer());
		result.setVolume(subscriptionVolume.getVolume());

		return result;
	}

}
