
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.ParticipationService;
import services.UserService;
import utilities.AbstractTest;
import domain.Participation;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteParticipationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private UserService				userService;


	/*
	 * Pruebas:
	 * 1. Borramos la participation1 autenticados como user2 (no salta excepción)
	 * 2. Borramos la participation2 autenticados como user3 (no salta excepción)
	 * 3. Borramos la participation3 autenticados como user4 (no salta excepción)
	 * 4. Borramos la participation4 autenticados como user5 (no salta excepción)
	 * 5. Borramos la participation5 autenticados como user6 (no salta excepción)
	 * 6. Borramos la participation2 autenticados como user2 que no tiene esa participación (salta un IllegalArgumentException)
	 * 7. Borramos la participation2 autenticados como sponsor (salta un IllegalArgumentException)
	 * 8. Borramos la participation2 autenticados como company (salta un IllegalArgumentException)
	 * 9. Borramos la participation2 autenticados como moderator (salta un IllegalArgumentException)
	 * 10.Borramos la participation2 autenticados como admin (salta un IllegalArgumentException)
	 * 11.Borramos la participation2 sin estar autenticados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.2: Un usuario logueado como usuario debe poder participar en una conjunta, además de borrar y editar su participación.
	 */
	@Test()
	public void testDelete() {
		final Object testingData[][] = {
			{
				"user", "user2", "participation1", false, null
			}, {
				"user", "user3", "participation2", false, null
			}, {
				"user", "user4", "participation3", false, null
			}, {
				"user", "user5", "participation4", false, null
			}, {
				"user", "user6", "participation5", false, null
			}, {
				"user", "user2", "participation2", true, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "participation2", true, IllegalArgumentException.class
			}, {
				"company", "company1", "participation2", true, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "participation2", true, IllegalArgumentException.class
			}, {
				"admin", "admin", "participation2", true, IllegalArgumentException.class
			}, {
				null, null, "participation2", true, IllegalArgumentException.class
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

	protected void template(final String user, final String username, final String participationBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		final Participation participation;
		int participationId;
		int participationIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (falseId == false) {
				participationIdAux = super.getEntityId(participationBean);
				participationId = 0;
				for (int i = 1; i <= this.participationService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), 1, 5).getTotalPages(); i++)
					//Si es tuya la cogemos desde el listado
					for (final Participation p : this.participationService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), i, 5).getContent())
						if (p.getId() == participationIdAux)
							participationId = p.getId();
			} else
				participationId = super.getEntityId(participationBean);

			participation = this.participationService.findOneToEdit(participationId);

			this.participationService.delete(participation); //Borramos la participación
			super.flushTransaction();

			Assert.isTrue(!this.participationService.findAll().contains(participation)); //Miramos si no está entre todos las participaciones de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
