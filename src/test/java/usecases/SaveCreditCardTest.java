package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import domain.CreditCard;
import domain.User;
import security.LoginService;
import services.CreditCardService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveCreditCardTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CreditCardService		creditCardService;
	
	@Autowired
	private UserService 		userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario crea una tarjeta de crédito
	 * 		2. Un usuario crea una tarjeta de crédito
	 *
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"user1", "Alejandro Martín", "MasterCard", "5256332484910150", 05, 20, 533, null
			}, {
				"user3", "Manuel Macías", "Visa", "4811183747278021", 12, 30, 432, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (Class<?>) testingData[i][7]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 		1. Una tarjeta de crédito tiene que tener un número válido
	 * 		2. Una tarjeta de crédito no puede estar caducada
	 * 		3. Una tarjeta de crédito no puede estar en el mes actual
	 * 		4. Una tarjeta de crédito debe tener un CVV entre 100 y 999
	 * 		5. Una tarjeta de crédito no puede ser creada por una compañía, solo un usuario
	 * 		6. Una tarjeta de crédito no puede ser creada por un patrocinador, solo un usuario
	 * 		7. Una tarjeta de crédito no puede ser creada por un moderador, solo un usuario
	 * 		8. Una tarjeta de crédito tiene que tener un holderName relleno
	 * 		9. Una tarjeta de crédito no puede tener un holderName a nulo
	 * 		10. Una tarjeta de crédito tiene que tener un brandName relleno
	 * 		11. Una tarjeta de crédito no puede tener un brandName a nulo
	 * 		12. Una tarjeta de crédito tiene que tener un número
	 * 		13. Una tarjeta de crédito no puede tener un número a nulo
	 * 	
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"user1", "Alejandro Martín", "MasterCard", "asdf", 05, 20, 533, ConstraintViolationException.class
				}, {
					"user1", "Alejandro Martín", "MasterCard", "5256332484910150", 03, 18, 533, IllegalArgumentException.class
				}, {
					"user1", "Alejandro Martín", "MasterCard", "5256332484910150", 06, 18, 533, IllegalArgumentException.class
				}, {
					"user1", "Alejandro Martín", "MasterCard", "5256332484910150", 9, 18, 99, ConstraintViolationException.class
				}, {
					"company1", "Alejandro Martín", "MasterCard", "5256332484910150", 9, 18, 101, IllegalArgumentException.class
				}, {
					"sponsor1", "Alejandro Martín", "MasterCard", "5256332484910150", 9, 18, 101, IllegalArgumentException.class
				}, {
					"moderator1", "Alejandro Martín", "MasterCard", "5256332484910150", 9, 18, 101, IllegalArgumentException.class
				}, {
					"user1", "", "MasterCard", "5256332484910150", 9, 18, 101, ConstraintViolationException.class
				}, {
					"user1", null, "MasterCard", "5256332484910150", 9, 18, 101, ConstraintViolationException.class
				}, {
					"user1", "Alejandro Martín", "", "5256332484910150", 05, 20, 533, ConstraintViolationException.class
				}, {
					"user1", "Alejandro Martín", null, "5256332484910150", 05, 20, 533, ConstraintViolationException.class
				}, {
					"user1", "Alejandro Martín", "MasterCard", "", 05, 20, 533, ConstraintViolationException.class
				}, {
					"user1", "Alejandro Martín", "MasterCard", null, 05, 20, 533, ConstraintViolationException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (Class<?>) testingData[i][7]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Crear una tarjeta de crédito
	 * Pasos:
	 * 		1. Autenticar como usuario
	 * 		2. Listar tarjeta de crédito
	 * 		3. Entrar en vista de crear tarjeta de crédito
	 * 		4. Crear tarjeta de crédito
	 * 		5. Volver al listado de rifas
	 */
	protected void template(final String actorBean, final String holderName, final String brandName, final String number, final int expirationMonth, final int expirationYear, final int cvvcode, final Class<?> expected) {
		Class<?> caught;
		Page<CreditCard> creditCardPage;
		Integer userId;
		Long oldTotalElements;
		DataBinder binder;
		User user;
		CreditCard creditCard, reconstructedCreditCard;

		caught = null;
		try {
			
			// 1. Autenticar como usuario
			super.authenticate(actorBean);
			
			userId = super.getEntityId(actorBean);
			Assert.notNull(userId);
			
			user = this.userService.findOne(userId);
			Assert.notNull(user);
			
			// 2. Listar tarjeta de crédito
			creditCardPage = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(creditCardPage);
			
			oldTotalElements = creditCardPage.getTotalElements();
			
			// 3. Entrar en vista de crear tarjeta de crédito
			creditCard = this.creditCardService.create(user);
			creditCard.setHolderName(holderName);
			creditCard.setBrandName(brandName);
			creditCard.setNumber(number);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setCvvcode(cvvcode);
			
			// 4. Crear tarjeta de crédito
			binder = new DataBinder(creditCard);
			reconstructedCreditCard = this.creditCardService.reconstruct(creditCard, binder.getBindingResult());
			this.creditCardService.save(reconstructedCreditCard);
			
			this.creditCardService.flush();
									
			// 5. Volver al listado de rifas
			creditCardPage = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(creditCardPage);
			Assert.isTrue(oldTotalElements != creditCardPage.getTotalElements());
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

