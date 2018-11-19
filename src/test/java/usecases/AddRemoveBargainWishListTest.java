package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Bargain;
import domain.User;
import security.LoginService;
import services.BargainService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AddRemoveBargainWishListTest extends AbstractTest {

	// System under test ------------------------------------------------------
	
	@Autowired
	private UserService 		userService;
	
	@Autowired
	private	BargainService		bargainService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario autenticado como user1 añade el bargain1 a su lista de deseo.
	 * 		2. Un usuario autenticado como user2 añade el bargain2 a su lista de deseo.
	 * 		3. Un usuario autenticado como user3 añade el bargain3 a su lista de deseo.
	 *
	 * Requisitos:
	 * 		11.	Los usuarios dispondrán de una lista de deseos a la que podrán añadir los chollos que deseen. 
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"user1", "bargain1", null
			}, {
				"user2", "bargain2", null
			}, {
				"user3", "bargain3", null
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
	 * 		1. Un patrocinador trata de añadir un chollo a su lista de deseo pero no tiene
	 * 		2. Una compañía trata de añadir un chollo a su lista de deseo pero no tiene
	 * 		3. Un moderador trata de añadir un chollo a su lista de deseo pero no tiene
	 * 		4. Un usuario sin plan Gold Premium trata de añadir un chollo no publicado a su lista de deseo
	 * 
	 * Requisitos:
	 * 		11.	Los usuarios dispondrán de una lista de deseos a la que podrán añadir los chollos que deseen. 
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"sponsor1", "bargain1", IllegalArgumentException.class
				}, {
					"company1", "bargain2", IllegalArgumentException.class
				}, {
					"moderator1", "bargain3", IllegalArgumentException.class
				}, {
					"user1", "bargain11", IllegalArgumentException.class
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
	 * Añadir/eliminar chollo de la lista de deseo
	 * Pasos:
	 * 		1. Autenticar como usuario
	 * 		2. Listar los chollos públicos y desplegar uno
	 * 		3. Añadir/eliminar chollo de la lista de deseo
	 * 		4. Volver a la vista del chollo
	 */
	protected void template(final String userBean, String bargainBean, final Class<?> expected) {
		Class<?> caught;
		Integer userId, bargainId;
		User user, oldUser;
		boolean oldWishListState;
		Page<Bargain> bargains;
		int countBargains;
		Bargain bargain, bargainChoosen;
		
		caught = null;
		bargainChoosen = null;
		try {
			
			// 1. Autenticar como usuario
			super.authenticate(userBean);
						
			// 2. Listar los chollos públicos y desplegar uno
			bargains = this.bargainService.findBargains(1, 5, "all", 0);
			countBargains = bargains.getTotalPages();
			
			bargainId = super.getEntityId(bargainBean);
			Assert.notNull(bargainId);
			bargain = this.bargainService.findOne(bargainId);
			Assert.notNull(bargain);

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findBargains(1, 5, "all", 0);

				//Si estamos pidiendo una página mayor
				if (bargains.getContent().size() == 0)
					break;

				// Navegar hasta el bargain que queremos.
				for (final Bargain newBargain : bargains.getContent())
					if (newBargain.equals(bargain)) {
						bargainChoosen = newBargain;
						break;
					}

				if (bargainChoosen != null)
					break;
			}

			//Ya tenemos el bargain
			Assert.notNull(bargainChoosen);
			
			userId = super.getEntityId(userBean);
			Assert.notNull(userId);
			
			oldUser = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(oldUser);
			
			oldWishListState = oldUser.getWishList().contains(bargain);
			
			// 3. Añadir/eliminar chollo de la lista de deseo
			this.userService.addRemoveBargainToWishList(bargainId);
			this.userService.flush();

			// 4. Volver a la vista del chollo
			user = this.userService.findOne(oldUser.getId());
			Assert.notNull(user);
			Assert.isTrue(oldWishListState != user.getWishList().contains(bargain));
				
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

