
package usecases;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.SubscriptionNewspaperService;
import services.CustomerService;
import utilities.AbstractTest;
import domain.Customer;
import domain.SubscriptionNewspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListSubscriptionNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionNewspaperService	subscriptionNewspaperService;

	@Autowired
	private CustomerService 		customerService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAll logueados como customer1
	 * 	2. Probamos obtener el resultado previsto para el metodo findAll sin loguear
	 * 3. Probamos obtener el resultado previsto para el metodo findAll logueados como manager2
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"customer1", "findAll", 6, 0, 0, null
			}, {
				null, "findAll", 6, 0, 0, null
			}, {
				"user2", "findAll", 6, 0, 0, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findByCustomerAccountId logueados como customer1, para la pagina 1 y el tamano 5
	 * 	2. Probamos obtener el resultado previsto para el metodo findByCustomerAccountId sin loguear, para la pagina 2 y el tamano 4
	 * 3. Probamos obtener el resultado previsto para el metodo findByCustomerAccountId logueados como customer2, para la pagina 2 y el tamano 3
	 * 4. Probamos no poder obtener el resultado previsto para el metodo findByCustomerAccountId logueados como un user
	 * 5. Probamos no poder obtener el resultado previsto para el metodo findByCustomerAccountId logueados como un admin
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 * 
	 */
	@Test()
	public void testFindByCustomerAccountId() {
		final Object testingData[][] = {
				{
					"customer1", "findByCustomerAccountId", 2, 1, 5, null
				}, {
					null, "findByCustomerAccountId", null, 2, 4, IllegalArgumentException.class
				}, {
					"customer2", "findByCustomerAccountId", 1, 1, 1, null
				}, {
					"user2", "findByCustomerAccountId", 1, 2, 1, IllegalArgumentException.class
				}, {
					"administrator", "findByCustomerAccountId", 1, 1, 5, IllegalArgumentException.class
				}

		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
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
	 * 2. Comprobamos si el método es findAll ó findByUserAccountId
	 * 3. En el caso de que sea findByUserAccountId, obtenemos las entidades correspondientes al customer para usar el método
	 * 3. Según el método que sea, se llama a su método y se guarda en la variable sizeSubscription el tamaño de los resultados de cada método
	 * 4. Comprobamos que devuelve el valor esperado
	 * 5. Nos desautentificamos
	 */
	protected void template(final String customer, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<SubscriptionNewspaper> subscriptionsCollection;
		List<SubscriptionNewspaper> subscriptionsList;
		int sizeSubscription;
		int customerId;
		int customerAccountId;
		Customer customerEntity;

		caught = null;
		try {
			super.authenticate(customer);

			if (method.equals("findAll")) {
				subscriptionsCollection = this.subscriptionNewspaperService.findAll();
				sizeSubscription = subscriptionsCollection.size();
			} else {
				Assert.notNull(customer);
				customerId = super.getEntityId(customer);
				Assert.notNull(customerId);
				customerEntity = this.customerService.findOne(customerId);
				Assert.notNull(customerEntity);
				customerAccountId = customerEntity.getUserAccount().getId();
				subscriptionsList = this.subscriptionNewspaperService.findByUserAccountId(customerAccountId, page, size).getContent();
				sizeSubscription = subscriptionsList.size();
			}
			Assert.isTrue(sizeSubscription == tamano); 
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
