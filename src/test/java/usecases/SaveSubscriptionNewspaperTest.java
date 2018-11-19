package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.CreditCard;
import domain.Newspaper;
import domain.Customer;
import domain.SubscriptionNewspaper;

import services.CustomerService;
import services.NewspaperService;
import services.SubscriptionNewspaperService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveSubscriptionNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SubscriptionNewspaperService		subscriptionNewspaperService;

	@Autowired
	private CustomerService				customerService;
	
	@Autowired
	private NewspaperService				newspaperService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * Probamos la creación de varias subscriptions por parte de diferentes usuarios.
	 * 
	 * Requisitos:
	 * 22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 */
	@Test
	public void positiveSaveSubscriptionTest() {
		final Object testingData[][] = {
		{
				"customer1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, "customer1", "newspaper5", null
			}, {
				"customer2", "Alejandro", "Visa", "377564788646263", 8, 20, 319, "customer2", "newspaper2",  null
			}, {
				"customer3", "Paco", "American Express", "345035739479236", 4, 19, 147, "customer3", "newspaper5", null
			}, {
				"customer2", "Manuel", "Credit Links", "6011516686715805", 5, 20, 365, "customer2", "newspaper2", null
			}, {
				"customer1", "Estefania", "MasterCard", "5429007233826913", 2, 21, 258, "customer1", "newspaper5", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Solo puede crearlo un customer
	 * 2. Solo puede crearlo un customer
	 * 3. Solo puede crearlo su customer
	 * 4. HolderName no puede estar vacío
	 * 5. HolderName no puede ser null
	 * 6. BrandName no puede estar vacío
	 * 7. BrandName no peude ser null
	 * 8. Number debe ser un número de tarjeta de crédito válido
	 * 9. El mes de expiración debe estar comprendido entre 1 y 12
	 * 10. El mes de expiración debe estar comprendido entre 1 y 12
	 * 11. La fecha no puede estar expirada
	 * 12. El código CVV debe estar comprendido entre 100 y 999
	 * 13. El código CVV debe estar comprendido entre 100 y 999
	 * 14. El customer debe ser válido 
	 * 15. El customer no puede ser nulo
	 * 16. Un customer solo puede tener un subscription por cada newspaper
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 */
	@Test()
	public void negativeSaveCustomersSubscriptionTest() {
		final Object testingData[][] = {
			{
				null, "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, "user1", "newspaper2", IllegalArgumentException.class 
			}, 	{
				"user1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, "user1", "newspaper2", IllegalArgumentException.class 
			}, {
				"user2", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, "user1", "newspaper2",  IllegalArgumentException.class
			}, {
				"customer1", "", "MasterCard", "5471664286416252", 9, 19, 258, "customer1", "newspaper5", ConstraintViolationException.class 
			}, {
				"customer1", null, "MasterCard", "5471664286416252", 9, 19, 258, "customer1", "newspaper5", ConstraintViolationException.class 
			}, {
				"customer3", "Estefania", "", "4929254799279560", 2, 21, 258, "customer3", "newspaper5", ConstraintViolationException.class 
			}, {
				"customer2", "Estefania", null, "5429007233826913", 2, 21, 258, "customer2", "newspaper2", ConstraintViolationException.class 
			}, {
				"customer2", "Manuel", "Credit Links", "1005", 5, 19, 365, "customer2", "newspaper2", ConstraintViolationException.class 
			}, {
				"customer2", "Manuel", "Credit Links", "5450634754850139", 0, 19, 365, "customer2", "newspaper2", ConstraintViolationException.class
			}, {
				"customer3", "Manuel", "Credit Links", "5429007233826913", 13, 19, 365, "customer3", "newspaper5", ConstraintViolationException.class 
			}, {
				"customer3", "Paco", "American Express", "345035739479236", 4, 17, 147, "customer3", "newspaper5", IllegalArgumentException.class 
			}, {
				"customer2", "Alejandro", "Visa", "4929231012264199", 8, 20, 50, "customer2", "newspaper2",  ConstraintViolationException.class 
			}, {
				"customer2", "Alejandro", "Visa", "4929231012264199", 8, 21, 5000, "customer2", "newspaper2", ConstraintViolationException.class 
			}, {
				"customer1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, "user1", "newspaper5", IllegalArgumentException.class 
			}, {
				"customer1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, null, "newspaper2", IllegalArgumentException.class 
			}, {
				"customer1", "Estefania", "MasterCard", "5429007233826913", 2, 21, 258, "customer1", "newspaper2", IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (String) testingData[i][7],  (String) testingData[i][8], (Class<?>) testingData[i][9]);
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
	 * 1. Nos autentificamos como el usuario customer
	 * 2. Tomamos el id de customerCredit
	 * 3. Tomamos la entidad correspondiente a al id de customerCredit
	 * 4. Creamos una nueva subscription pasando el customer como parámetros
	 * 5. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 6. Guardamos la nueva subscription
	 * 7. Nos desautentificamos
	 * 8. Comprobamos se ha creado y existe
	 */
	protected void template(final String customer, final String holderName, final String brandName, final String number, final int expirationMonth, final int expirationYear, final int cvvcode, final String customerCredit,  final String newspaper, final Class<?> expected) {
		Class<?> caught;
		Integer customerId, newspaperId;
		Customer customerEntity;
		Newspaper newspaperEntity;
		SubscriptionNewspaper subscription, subscriptionEntity;
		CreditCard creditCard;

		caught = null;
		try {
			super.authenticate(customer);
			Assert.notNull(customerCredit);
			customerId = super.getEntityId(customerCredit);
			Assert.notNull(customerId);
			customerEntity = this.customerService.findOne(customerId);
			Assert.notNull(customerEntity);
			Assert.notNull(newspaper);
			newspaperId = super.getEntityId(newspaper);
			Assert.notNull(newspaperId);
			newspaperEntity = this.newspaperService.findOne(newspaperId);
			Assert.notNull(newspaperEntity);
			subscription = this.subscriptionNewspaperService.create(customerEntity, newspaperEntity);

			creditCard = new CreditCard();;

			creditCard.setHolderName(holderName);
			creditCard.setBrandName(brandName);
			creditCard.setNumber(number);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setCvvcode(cvvcode);
			
			subscription.setCreditCard(creditCard);

			subscriptionEntity = this.subscriptionNewspaperService.save(subscription);
			
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.subscriptionNewspaperService.findAll().contains(subscriptionEntity));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}

