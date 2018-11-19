package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.User;
import security.LoginService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ShowHideWishListTest extends AbstractTest {

	// System under test ------------------------------------------------------
	
	@Autowired
	private UserService 		userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario cambia el estado de su lista de deseo a público
	 * 		2. Un usuario cambia el estado de su lista de deseo a privado
	 *
	 * Requisitos:
	 * 		25.4. Un actor autenticado como usuario debe ser capaz de cambiar la visibilidad de su lista de deseo entre público y privado.
	 * 
	 */
	@Test
	public void driverPositiveTest() {		
		final Object testingData[][] = {
			{
				"user1", null, null
			}, {
				"user2", null, null
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
	 * 		1. Un administrador trata de cambiar el estado de su lista de deseo, pero solo puede el usuario
	 * 		2. Un patrocinador trata de cambiar el estado de su lista de deseo, pero solo puede el usuario
	 * 		3. Una compañía trata de cambiar el estado de su lista de deseo, pero solo puede el usuario
	 * 	 	4. Un moderador trata de cambiar el estado de su lista de deseo, pero solo puede el usuario
	 * 
	 * Requisitos:
	 * 		25.4. Un actor autenticado como usuario debe ser capaz de cambiar la visibilidad de su lista de deseo entre público y privado.
	 * 
	 */
	@Test
	public void driverNegativeTest() {		
		final Object testingData[][] = {
				{
					"admin", "user1", IllegalArgumentException.class
				}, {
					"sponsor1", "user3", IllegalArgumentException.class
				}, {
					"company1", "user1", IllegalArgumentException.class
				}, {
					"moderator1", "user1", IllegalArgumentException.class
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
	 * Hacer público/oculto una lista de deseo
	 * Pasos:
	 * 		1. Autenticar como usuario
	 * 		2. Entrar en perfil
	 * 		3. Publicar/ocultar lista de deseo
	 * 		4. Volver al perfil
	 */
	protected void template(final String userBean, String hackBean, final Class<?> expected) {
		Class<?> caught;
		Integer userId;
		User user, oldUser;
		boolean oldWishListState;
		
		caught = null;
		try {
			
			// 1. Autenticar como usuario
			super.authenticate(userBean);
			
			if(hackBean == null) hackBean = userBean;
			
			// 2. Entrar en perfil
			userId = super.getEntityId(hackBean);
			Assert.notNull(userId);
			
			oldUser = this.userService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.notNull(oldUser);
			
			oldWishListState = oldUser.getIsPublicWishList();
			
			// 3. Publicar/ocultar lista de deseo
			this.userService.changeWishList(oldUser);
			this.userService.flush();

			// 4. VVolver al perfil
			user = this.userService.findOne(oldUser.getId());
			Assert.notNull(user);
			Assert.isTrue(oldWishListState != user.getIsPublicWishList());
				
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.checkExceptions(expected, caught);
	}

}

