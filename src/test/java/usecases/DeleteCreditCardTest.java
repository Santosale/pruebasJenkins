package usecases;

import javax.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.CreditCard;
import security.LoginService;
import services.CreditCardService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteCreditCardTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CreditCardService		creditCardService;
	
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario autenticado como user1 borra la creditCard8
	 * 		2. Un usuario autenticado como user4 borra la creditCard6
	 *
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"user1", "creditCard8", null
			}, {
				"user4", "creditCard6", null
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
	}
	
	/*
	 * Pruebas:
	 * 		1. Una compañía trata de borrar una tarjeta de crédito cuando solo pueden los usuarios
	 * 		2. Un usuario trata de eliminar una tarjeta de crédito que ha sido usada para comprar algo
	 * 		3. Un usuario trata de borrar una tarjeta de crédito que no es suya
	 * 		4. Un patrocinador trata de borrar una tarjeta de crédito cuando solo pueden los usuarios
	 * 		5. Un moderador trata de borrar una tarjeta de crédito cuando solo pueden los usuarios
	 * 
	 * Requisitos:
	 * 		13.	Los usuarios pueden almacenar en el sistema distintas tarjetas de crédito...
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"company4", "creditCard1", IllegalArgumentException.class
				}, {
					"user1", "creditCard1", IllegalArgumentException.class
				}, {
					"user1", "creditCard6", IllegalArgumentException.class
				}, {
					"sponsor2", "creditCard1", IllegalArgumentException.class
				}, {
					"moderator1", "creditCard1", IllegalArgumentException.class
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
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Eliminar una tarjeta de crédito
	 * Pasos:
	 * 		1. Autenticar como usuario
	 * 		2. Listar tarjeta de crédito
	 * 		3. Entrar en vista de editar tarjeta de crédito
	 * 		4. Eliminar tarjeta de crédito
	 * 		5. Volver al listado de tarjetas de crédito
	 */
	protected void template(final String actorBean, final String creditCardBean, final Class<?> expected) {
		Class<?> caught;
		CreditCard creditCard;
		Page<CreditCard> creditCardPage;
		Integer creditCardId;
		Long oldTotalElements;

		caught = null;
		try {
			
			// 1. Autenticar como usuario
			super.authenticate(actorBean);
			
			creditCardId = super.getEntityId(creditCardBean);
			Assert.notNull(creditCardId);
			
			// 2. Listar tarjeta de crédito
			creditCardPage = this.creditCardService.findByUserAccountId(LoginService.getPrincipal().getId(), 1, 5);
			Assert.notNull(creditCardPage);
			
			oldTotalElements = creditCardPage.getTotalElements();
			
			// 3. Entrar en vista de editar tarjeta de crédito
			creditCard = this.creditCardService.findOneToDisplayEdit(creditCardId);
			Assert.notNull(creditCard);
			
			// 4. Eliminar tarjeta de crédito
			this.creditCardService.delete(creditCard);
			
			this.creditCardService.flush();
									
			// 5. Volver al listado de tarjetas de crédito
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

