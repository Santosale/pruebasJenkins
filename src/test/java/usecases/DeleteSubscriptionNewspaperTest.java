package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Customer;
import domain.SubscriptionNewspaper;
import services.SubscriptionNewspaperService;
import services.CustomerService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteSubscriptionNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionNewspaperService		subscriptionNewspaperNewspaperService;
	
	@Autowired
	private CustomerService				customerService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 * como si accedemos a la entidad desde getEntityId:
	 * 1. Probando que el customer2 borra la subscriptionNewspaper2
	 * 	2. Probando que el customer3 borra la subscriptionNewspaper6
	 * 	3. Probando que el customer1 borra la subscriptionNewspaper3
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 *
	 */
	@Test
	public void positiveDeleteSubscriptionTest() {
		final Object testingData[][] = {
			{
				"customer2", "subscriptionNewspaper2", null
			}, {
				"customer3", "subscriptionNewspaper6", null
			} , {
				"customer1", "subscriptionNewspaper3", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 
	 * Primero se realizarán las pruebas desde un listado y luego
	 * como si accedemos a la entidad desde getEntityId:
	 * 
	 * 1. No puede borrarlo un usuario no logueado
	 * 2. Solo puede borrarlo un customer
	 * 3. Solo puede borrarlo un customer
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 *
	 */
	 @Test()
	public void negativeDeleteSubscriptionTest() {
		final Object testingData[][] = {
			{
				null, "subscriptionNewspaper1", IllegalArgumentException.class 
			}, 	{
				"administrator", "subscriptionNewspaper1", IllegalArgumentException.class
			}, {
				"user1", "subscriptionNewspaper2", IllegalArgumentException.class 
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

		/*
		 * 	Pasos:
		 * 
		 * 1. Nos autentificamos como customer
		 * 2. Tomamos el id y la entidad de customer
		 * 3. Accedemos a la lista de subscriptionNewspapers y tomamos la que nos interesa
		 * 4. Borramos la subscriptionNewspaper
		 * 5. Nos desautentificamos
		 * 6. Comprobamos que no existe la subscriptionNewspaper borrada
		 */
	protected void template(final String customer, final String subscriptionNewspaper, final Class<?> expected) {
		Class<?> caught;
		int customerId, subscriptionNewspaperId;
		Customer customerEntity;
		SubscriptionNewspaper subscriptionNewspaperEntity;
		Collection<SubscriptionNewspaper> subscriptionNewspapers;

		subscriptionNewspaperEntity = null;
		caught = null;
		try {
			super.authenticate(customer);
			Assert.notNull(customer);
			customerId = super.getEntityId(customer);
			customerEntity = this.customerService.findOne(customerId);
			Assert.notNull(customerEntity);
			subscriptionNewspaperId = super.getEntityId(subscriptionNewspaper);
			Assert.notNull(subscriptionNewspaperId);
			subscriptionNewspapers = this.subscriptionNewspaperNewspaperService.findByUserAccountId(customerEntity.getUserAccount().getId(), 1, 5).getContent();
			for (SubscriptionNewspaper c : subscriptionNewspapers) {
				if(c.getId() == subscriptionNewspaperId){
					subscriptionNewspaperEntity = c;
					break;
				}
			}
			this.subscriptionNewspaperNewspaperService.delete(subscriptionNewspaperEntity);
			super.unauthenticate();
			
			Assert.isTrue(!this.subscriptionNewspaperNewspaperService.findAll().contains(subscriptionNewspaperEntity));

			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer y de subscriptionNewspaper
	 * 3. Borramos la subscriptionNewspaper
	 * 4. Nos desautentificamos
	 * 5. Comprobamos que no existe la subscriptionNewspaper borrada
	 */
	protected void templateNoList(final String customer, final String subscriptionNewspaper, final Class<?> expected) {
		Class<?> caught;
		int subscriptionNewspaperId;
		SubscriptionNewspaper subscriptionNewspaperEntity = null;

		caught = null;
		try {
			super.authenticate(customer);
			subscriptionNewspaperId = super.getEntityId(subscriptionNewspaper);
			Assert.notNull(subscriptionNewspaperId);

			subscriptionNewspaperEntity = this.subscriptionNewspaperNewspaperService.findOneToEdit(subscriptionNewspaperId);
			this.subscriptionNewspaperNewspaperService.delete(subscriptionNewspaperEntity);
			super.unauthenticate();
			
			Assert.isTrue(!this.subscriptionNewspaperNewspaperService.findAll().contains(subscriptionNewspaperEntity));

			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

