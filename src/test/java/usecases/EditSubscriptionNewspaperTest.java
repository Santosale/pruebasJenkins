package usecases;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.CreditCard;
import domain.Customer;
import domain.SubscriptionNewspaper;

import services.CustomerService;
import services.SubscriptionNewspaperService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditSubscriptionNewspaperTest extends AbstractTest {

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
	 * 
	 * Probamos la edicion de varias subscriptionNewspapers por parte de diferentes customers
	 * 
	 * Requsitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 * 
	 */
	//@Test
	public void positiveEditSubscriptionTest() {
		final Object testingData[][] = {
			{	
				"customer1", "subscriptionNewspaper1", "Antonio", "MasterCard", "5212472747907073", 9, 19, 258, null
			}, {
				"customer2", "subscriptionNewspaper2", "Alejandro", "Visa", "377564788646263", 8, 19, 319, null
			}, {
				"customer3", "subscriptionNewspaper4", "Paco", "American Express", "4929254799279560", 7, 20, 147, null
			}, {
				"customer1", "subscriptionNewspaper3", "Manuel", "Credit Links", "5212472747907073", 5, 20, 365, null
			}, {
				"customer2", "subscriptionNewspaper5", "Estefania", "MasterCard", "377564788646263", 2, 21, 258, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (Integer) testingData[i][7], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (Integer) testingData[i][7], (Class<?>) testingData[i][8]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 * como si accedemos a la entidad desde getEntityId:
	 * 1. Solo puede editarlo un customer
	 * 2. Solo puede editarlo un customer
	 * 3. Solo puede editarlo su customer
	 * 4. HolderName no puede estar vacio
	 * 5. HolderName no puede ser null
	 * 6. BrandName no puede estar vacio
	 * 7. BrandName no puede ser null
	 * 8. Number debe ser un numero de tarjeta de credito valido
	 * 9. El mes de expiracion debe estar comprendido entre 1 y 12
	 * 10. El mes de expiracion debe estar comprendido entre 1 y 12
	 * 11. La fecha no puede estar expirada
	 * 12. El codigo CVV debe estar comprendido entre 100 y 999
	 * 13. El codigo CVV debe estar comprendido entre 100 y 999
	 * 
	 * Requisitos:
	 * 	22. An actor who is authenticated as a customer can:
		1. Subscribe to a private newspaper by providing a valid credit card.
	 *
	 */
	@Test()
	public void negativeEditSubscriptionTest() {
		final Object testingData[][] = {
			{
				null, "subscriptionNewspaper1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, IllegalArgumentException.class 
			}, 	{
				"user1", "subscriptionNewspaper1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, IllegalArgumentException.class 
			}, {
				"customer2", "subscriptionNewspaper1", "Antonio", "MasterCard", "5471664286416252", 9, 19, 258, IllegalArgumentException.class
			}, {
				"customer1", "subscriptionNewspaper1", "", "MasterCard", "5471664286416252", 9, 19, 258, ConstraintViolationException.class 
			}, {
				"customer1", "subscriptionNewspaper1", null, "MasterCard", "5471664286416252", 9, 19, 258, ConstraintViolationException.class 
			}, {
				"customer2", "subscriptionNewspaper2", "Estefania", "", "5429007233826913", 2, 21, 258, ConstraintViolationException.class
			}, {
				"customer2", "subscriptionNewspaper2", "Estefania", null, "5429007233826913", 2, 21, 258, ConstraintViolationException.class 
			}, {
				"customer3", "subscriptionNewspaper6", "Manuel", "Credit Links", "1005", 5, 19, 365, ConstraintViolationException.class 
			}, {
				"customer3", "subscriptionNewspaper4", "Manuel", "Credit Links", "5429007233826913", 0, 19, 365, ConstraintViolationException.class
			}, {
				"customer2", "subscriptionNewspaper2", "Manuel", "Credit Links", "5429007233826913", 13, 19, 365, ConstraintViolationException.class 
			}, {
				"customer1", "subscriptionNewspaper1", "Paco", "American Express", "345035739479236", 4, 16, 147, IllegalArgumentException.class 
			}, {
				"customer2", "subscriptionNewspaper2", "Alejandro", "Visa", "4929231012264199", 8, 19, 50, ConstraintViolationException.class 
			}, {
				"customer2", "subscriptionNewspaper2", "Alejandro", "Visa", "4929231012264199", 8, 19, 5000, ConstraintViolationException.class 
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (Integer) testingData[i][7], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (Integer) testingData[i][7], (Class<?>) testingData[i][8]);
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
	 customer Accedemos a la lista de subscriptionNewspapers y tomamos la que nos interesa
	 * 4. Le creamos una copia para que no se guarde solo con un set
	 * 5. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 6. Guardamos la subscriptionNewspaper copiada con los parámetros
	 * 7. Nos desautentificamos
	 */
	protected void template(final String customer, final String subscriptionNewspaperEdit, final String holderName, final String brandName, final String number, final Integer expirationMonth, final Integer expirationYear, final Integer cvvcode, final Class<?> expected) {
		Class<?> caught;
		Integer customerId, subscriptionNewspaperId;
		Customer customerEntity;
		Collection<SubscriptionNewspaper> subscriptionNewspapers;
		SubscriptionNewspaper subscriptionNewspaper, subscriptionNewspaperEntity;
		CreditCard creditCard;

		subscriptionNewspaper = null;
		caught = null;
		try {
			super.authenticate(customer);
			Assert.notNull(customer);

			customerId = super.getEntityId(customer);
			customerEntity = this.customerService.findOne(customerId);
			Assert.notNull(customerEntity);

			subscriptionNewspaperId = super.getEntityId(subscriptionNewspaperEdit);
			Assert.notNull(subscriptionNewspaperId);
			subscriptionNewspapers = this.subscriptionNewspaperNewspaperService.findByUserAccountId(customerEntity.getUserAccount().getId(), 1, 5).getContent();
			
			for (SubscriptionNewspaper c : subscriptionNewspapers) {
				if(c.getId() == subscriptionNewspaperId){
					subscriptionNewspaper = c;
					break;
				}
			}
			Assert.notNull(subscriptionNewspaper);
			
			subscriptionNewspaperEntity = this.copySubscription(subscriptionNewspaper);
			creditCard = this.copyCreditCard(subscriptionNewspaper);
			
			creditCard.setHolderName(holderName);
			creditCard.setBrandName(brandName);
			creditCard.setNumber(number);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setCvvcode(cvvcode);
			subscriptionNewspaperEntity.setCreditCard(creditCard);
			subscriptionNewspaperEntity.setCustomer(customerEntity);

			this.subscriptionNewspaperNewspaperService.save(subscriptionNewspaperEntity);

			super.unauthenticate();
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
	 * 2. Tomamos el id y la entidad de customer y subscriptionNewspaper
	 * 3. Le creamos una copia para que no se guarde solo con un set
	 * 4. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 5. Guardamos la subscriptionNewspaper copiada con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void templateNoList(final String customer, final String subscriptionNewspaperEdit, final String holderName, final String brandName, final String number, final int expirationMonth, final int expirationYear, final int cvvcode, final Class<?> expected) {
		Class<?> caught;
		Integer customerId, subscriptionNewspaperId;
		Customer customerEntity;
		SubscriptionNewspaper subscriptionNewspaper, subscriptionNewspaperEntity;
		CreditCard creditCard;

		subscriptionNewspaper = null;
		caught = null;
		try {
			super.authenticate(customer);
			Assert.notNull(customer);
			customerId = super.getEntityId(customer);
			customerEntity = this.customerService.findOne(customerId);
			Assert.notNull(customerEntity);
			subscriptionNewspaperId = super.getEntityId(subscriptionNewspaperEdit);
			Assert.notNull(subscriptionNewspaperId);
			subscriptionNewspaper = this.subscriptionNewspaperNewspaperService.findOneToEdit(subscriptionNewspaperId);
			Assert.notNull(subscriptionNewspaper);
			subscriptionNewspaperEntity = this.copySubscription(subscriptionNewspaper);
			creditCard = this.copyCreditCard(subscriptionNewspaper);
			creditCard.setHolderName(holderName);
			creditCard.setBrandName(brandName);
			creditCard.setNumber(number);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setCvvcode(cvvcode);
			subscriptionNewspaperEntity.setCreditCard(creditCard);
			subscriptionNewspaperEntity.setCustomer(customerEntity);
			this.subscriptionNewspaperNewspaperService.save(subscriptionNewspaperEntity);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

	private CreditCard copyCreditCard(final SubscriptionNewspaper subscriptionNewspaper) {
		CreditCard result;
	
		result = new CreditCard();
		result.setBrandName(subscriptionNewspaper.getCreditCard().getBrandName());
		result.setCvvcode(subscriptionNewspaper.getCreditCard().getCvvcode());
		result.setExpirationMonth(subscriptionNewspaper.getCreditCard().getExpirationMonth());
		result.setExpirationYear(subscriptionNewspaper.getCreditCard().getExpirationYear());
		result.setNumber(subscriptionNewspaper.getCreditCard().getNumber());
		result.setHolderName(subscriptionNewspaper.getCreditCard().getHolderName());
		return result;
	}
	
	private SubscriptionNewspaper copySubscription(final SubscriptionNewspaper subscriptionNewspaper) {
		SubscriptionNewspaper result;
	
		result = new SubscriptionNewspaper();
		result.setId(subscriptionNewspaper.getId());
		result.setVersion(subscriptionNewspaper.getVersion());
		result.setCustomer(subscriptionNewspaper.getCustomer());
		result.setNewspaper(subscriptionNewspaper.getNewspaper());
		return result;
	}
	
}
