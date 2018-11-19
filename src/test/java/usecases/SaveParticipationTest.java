
package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.GrouponService;
import services.ParticipationService;
import utilities.AbstractTest;
import domain.Groupon;
import domain.Participation;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveParticipationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private GrouponService			grouponService;


	/*
	 * Pruebas:
	 * 1. Creamos una participación al groupon5 con el user3 con cantidad de producto 1 (no salta excepción)
	 * 2. Creamos una participación al groupon2 con el user2 con cantidad de producto 10 (no salta excepción)
	 * 3. Creamos una participación al groupon2 con el user3 con cantidad de producto 10 (no salta excepción)
	 * 4. Creamos una participación al groupon4 que ya ha pasado con el user3 que es premium con cantidad de producto 10 (no salta excepción)
	 * 5. Creamos una participación al groupon5 con el user3 con cantidad de producto 0 (salta un ConstraintViolationException)
	 * 6. Creamos una participación al groupon5 con el user3 con cantidad de producto -1 (salta un ConstraintViolationException)
	 * 7. Creamos una participación al groupon1 con el user1 que es el creador de ese groupon (salta un IllegalArgumentException)
	 * 8. Creamos una participación al groupon2 con el user2 que ya tiene una participación a ese groupon (salta un IllegalArgumentException)
	 * 9. Creamos una participación al groupon3 que ya ha pasado con el user6 que es un usuario no premium (salta un IllegalArgumentException)
	 * 10.Creamos una participación al groupon2 logueados como sponsor (salta un IllegalArgumentException)
	 * 11.Creamos una participación al groupon2 logueados como moderator (salta un IllegalArgumentException)
	 * 12.Creamos una participación al groupon2 logueados como company (salta un IllegalArgumentException)
	 * 13.Creamos una participación al groupon2 logueados como admin (salta un IllegalArgumentException)
	 * 14.Creamos una participación al groupon2 sin estar logueados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.2: Un usuario logueado como usuario debe poder participar en una conjunta, además de borrar y editar su participación.
	 */
	@Test()
	public void testSave() {
		final Object testingData[][] = {
			{
				"user", "user3", 1, "groupon5", true, null
			}, {
				"user", "user6", 10, "groupon2", false, null
			}, {
				"user", "user3", 10, "groupon2", true, null
			}, {
				"user", "user3", 10, "groupon4", true, null
			}, {
				"user", "user3", 0, "groupon5", true, ConstraintViolationException.class
			}, {
				"user", "user3", -1, "groupon5", true, ConstraintViolationException.class
			}, {
				"user", "user1", 5, "groupon1", true, IllegalArgumentException.class
			}, {
				"user", "user2", 5, "groupon1", true, IllegalArgumentException.class
			}, {
				"user", "user6", 5, "groupon3", false, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", 10, "groupon2", false, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", 10, "groupon2", true, IllegalArgumentException.class
			}, {
				"company", "company1", 10, "groupon2", false, IllegalArgumentException.class
			}, {
				"admin", "admin", 10, "groupon2", false, IllegalArgumentException.class
			}, {
				null, null, 10, "groupon2", false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (int) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final int amountProduct, final String grouponBean, final boolean useFindAll, final Class<?> expected) {
		Class<?> caught;
		Participation participation;
		Participation saved;
		int grouponId;
		int grouponIdAux;

		DataBinder binder;
		Participation participationReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			grouponIdAux = super.getEntityId(grouponBean);
			grouponId = 0;
			if (useFindAll == true) {
				for (int i = 1; i <= this.grouponService.findAllPaginated(1, 5).getTotalPages(); i++)
					//Cogemos el groupon entre todos
					for (final Groupon g : this.grouponService.findAllPaginated(i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
			} else
				for (int i = 1; i <= this.grouponService.findWithMaxDateFuture(1, 5).getTotalPages(); i++)
					//Cogemos el groupon entre todos
					for (final Groupon g : this.grouponService.findWithMaxDateFuture(i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();

			participation = this.participationService.create(grouponId); //Creamos la participación

			participation.setAmountProduct(amountProduct);
			//Editamos los valores

			binder = new DataBinder(participation);
			participationReconstruct = this.participationService.reconstruct(participation, binder.getBindingResult()); //Lo reconstruimos
			saved = this.participationService.save(participationReconstruct); //Guardamos la participación
			super.flushTransaction();

			Assert.isTrue(this.participationService.findAll().contains(saved)); //Miramos si están entre todos las participaciones de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
