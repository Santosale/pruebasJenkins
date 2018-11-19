
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.GrouponService;
import services.UserService;
import utilities.AbstractTest;
import domain.Groupon;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteGrouponTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private GrouponService	grouponService;

	@Autowired
	private UserService		userService;


	/*
	 * Pruebas:
	 * 1. Borramos la conjunta 1 autenticados como user1 (no salta excepción)
	 * 2. Borramos la conjunta 2 autenticados como user1 (no salta excepción)
	 * 3. Borramos la conjunta 3 autenticados como user1 (no salta excepción)
	 * 4. Borramos la conjunta 4 autenticados como user1 (no salta excepción)
	 * 5. Borramos la conjunta 5 autenticados como user1 (no salta excepción)
	 * 6. Borramos la conjunta 6 autenticados como user1 (no salta excepción)
	 * 7. Borramos la conjunta 7 autenticados como user2 (no salta excepción)
	 * 8. Borramos la conjunta 1 autenticados como user2 (salta un IllegalArgumentException)
	 * 9. Borramos la conjunta 1 autenticados como sponsor (salta un IllegalArgumentException)
	 * 10.Borramos la conjunta 1 autenticados como company (salta un IllegalArgumentException)
	 * 11.Borramos la conjunta 1 autenticados como moderator (salta un IllegalArgumentException)
	 * 12.Borramos la conjunta 1 autenticados como admin (salta un IllegalArgumentException)
	 * 13.Borramos la conjunta 1 sin estar autenticados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.1:
	 * Puede organizar una conjunta. El código de descuento puede ser modificado siempre que se supere el mínimo de productos requeridos.
	 * Una vez superado por primera vez el mínimo de productos, no se puede editar hasta que aporte el código de descuento.
	 * Un usuario debe poder borrar sus conjuntas.
	 */
	@Test()
	public void testDelete() {
		final Object testingData[][] = {
			{
				"user", "user1", "groupon1", false, null
			}, {
				"user", "user1", "groupon2", false, null
			}, {
				"user", "user1", "groupon3", false, null
			}, {
				"user", "user1", "groupon4", false, null
			}, {
				"user", "user1", "groupon5", false, null
			}, {
				"user", "user1", "groupon6", false, null
			}, {
				"user", "user2", "groupon7", false, null
			}, {
				"user", "user2", "groupon1", true, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "groupon1", true, IllegalArgumentException.class
			}, {
				"company", "company1", "groupon1", true, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "groupon1", true, IllegalArgumentException.class
			}, {
				"admin", "admin", "groupon1", true, IllegalArgumentException.class
			}, {
				null, null, "groupon1", true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Borramos la conjunta 1 autenticados como moderator (no salta excepción)
	 * 2. Borramos la conjunta 2 autenticados como moderator (no salta excepción)
	 * 3. Borramos la conjunta 3 autenticados como moderator (no salta excepción)
	 * 4. Borramos la conjunta 4 autenticados como moderator (no salta excepción)
	 * 5. Borramos la conjunta 5 autenticados como moderator (no salta excepción)
	 * 6. Borramos la conjunta 6 autenticados como moderator (no salta excepción)
	 * 7. Borramos la conjunta 7 autenticados como moderator (no salta excepción)
	 * 8. Borramos la conjunta 1 autenticados como user2 (salta un IllegalArgumentException)
	 * 9. Borramos la conjunta 1 autenticados como sponsor (salta un IllegalArgumentException)
	 * 10.Borramos la conjunta 1 autenticados como company (salta un IllegalArgumentException)
	 * 11.Borramos la conjunta 1 autenticados como moderator (salta un IllegalArgumentException)
	 * 12.Borramos la conjunta 1 autenticados como admin (salta un IllegalArgumentException)
	 * 13.Borramos la conjunta 1 sin estar autenticados (salta un IllegalArgumentException)
	 * 
	 * Requisito 26.8: Un actor que está autenticado como moderador debe ser capaz de eliminar las conjuntas que considere inadecuadas.
	 */
	@Test()
	public void testDeleteFromModerator() {
		final Object testingData[][] = {
			{
				"moderator", "moderator1", "groupon1", false, null
			}, {
				"moderator", "moderator1", "groupon2", false, null
			}, {
				"moderator", "moderator1", "groupon3", false, null
			}, {
				"moderator", "moderator1", "groupon4", false, null
			}, {
				"moderator", "moderator1", "groupon5", false, null
			}, {
				"moderator", "moderator1", "groupon6", false, null
			}, {
				"moderator", "moderator1", "groupon7", false, null
			}, {
				"user", "user1", "groupon1", true, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "groupon1", true, IllegalArgumentException.class
			}, {
				"company", "company1", "groupon1", true, IllegalArgumentException.class
			}, {
				"admin", "admin", "groupon1", true, IllegalArgumentException.class
			}, {
				null, null, "groupon1", true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateModerator((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	protected void template(final String user, final String username, final String grouponBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		Groupon groupon;
		int grouponId;
		int grouponIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (falseId == false) {
				grouponIdAux = super.getEntityId(grouponBean);
				grouponId = 0;
				for (int i = 1; i <= this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), 1, 5).getTotalPages(); i++)
					//Si es tuya la cogemos desde el listado
					for (final Groupon g : this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
			} else
				grouponId = super.getEntityId(grouponBean);

			groupon = this.grouponService.findOneToEdit(grouponId);

			this.grouponService.delete(groupon); //Borramos la conjunta
			super.flushTransaction();

			Assert.isTrue(!this.grouponService.findAll().contains(groupon)); //Miramos si no está entre todos las conjuntas de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateModerator(final String user, final String username, final String grouponBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		Groupon groupon;
		int grouponId;
		int grouponIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (falseId == false) {
				grouponIdAux = super.getEntityId(grouponBean);
				grouponId = 0;
				for (int i = 1; i <= this.grouponService.findAllPaginated(1, 5).getTotalPages(); i++)
					//Buscamos entre todas las conjuntas si eres moderator
					for (final Groupon g : this.grouponService.findAllPaginated(i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
			} else
				grouponId = super.getEntityId(grouponBean);

			groupon = this.grouponService.findOne(grouponId);

			this.grouponService.deleteFromModerator(groupon); //Borramos la conjunta
			super.flushTransaction();

			Assert.isTrue(!this.grouponService.findAll().contains(groupon)); //Miramos si no está entre todos las conjuntas de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
