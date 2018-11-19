
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
public class EditParticipationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ParticipationService	participationService;

	@Autowired
	private UserService				userService;


	/*
	 * Pruebas:
	 * 1. Editamos la participación1 con el user2 (no salta excepción)
	 * 2. Editamos la participación2 con el user3 (no salta excepción)
	 * 3. Editamos la participación1 con el user2 poniendo la cantidad de productos a cero (salta un ConstraintViolationException)
	 * 4. Editamos la participación1 con el user2 poniendo la cantidad de productos a -1 (salta un ConstraintViolationException)
	 * 5. Editamos la participación2 con el user2 el cual no tiene esa participación (salta un IllegalArgumentException)
	 * 6. Editamos la participación2 como sponsor (salta un IllegalArgumentException)
	 * 7. Editamos la participación2 como company (salta un IllegalArgumentException)
	 * 8. Editamos la participación2 como moderator (salta un IllegalArgumentException)
	 * 9. Editamos la participación2 como admin (salta un IllegalArgumentException)
	 * 10.Editamos la participación2 sin estar autenticado (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.2: Un usuario logueado como usuario debe poder participar en una conjunta, además de borrar y editar su participación.
	 */
	@Test()
	public void testSave() {
		final Object testingData[][] = {
			{
				"user", "user2", "participation1", 1, true, null
			}, {
				"user", "user3", "participation2", 1, true, null
			}, {
				"user", "user2", "participation1", 0, true, ConstraintViolationException.class
			}, {
				"user", "user2", "participation1", -1, true, ConstraintViolationException.class
			}, {
				"user", "user2", "participation2", 5, false, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "participation2", 5, false, IllegalArgumentException.class
			}, {
				"company", "company1", "participation2", 5, false, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "participation2", 5, false, IllegalArgumentException.class
			}, {
				"admin", "admin", "participation2", 5, false, IllegalArgumentException.class
			}, {
				null, null, "participation2", 5, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (int) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logueados como user2 hacemos el findOneToEdit de la participación1 (no salta excepción)
	 * 2.Logueados como user2 hacemos el findOneToEdit de la participación6 (no salta excepción)
	 * 3.Logueados como moderator1 hacemos el findOne de la participación1 (no salta excepción)
	 * 4.Logueados como moderator1 hacemos el findOneToEdit de la participación1 (salta un IllegalArgumentException)
	 * 5.Logueados como user2 hacemos el findOneToEdit de una participación de id 0 (salta un IllegalArgumentException)
	 * 6.Logueados como user2 hacemos el findOne de una participación de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"user", "user2", "participation1", true, false, true, null
			}, {
				"user", "user2", "participation6", true, false, true, null
			}, {
				"moderator", "moderator1", "participation1", false, false, false, null
			}, {
				"moderator", "moderator1", "participation1", false, false, true, IllegalArgumentException.class
			}, {
				"user", "user2", "participation1", true, true, true, IllegalArgumentException.class
			}, {
				"user", "user2", "participation1", true, true, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas
	 * 
	 * 1.Hay participación entre el user2 y el groupon1
	 * 2.Hay participación entre el user3 y el groupon1
	 * 3.No hay participación entre el user1 y el groupon1
	 * 4.No hay participación entre el sponsor1 y el groupon1
	 * 5.No hay participación entre el company1 y el groupon1
	 * 6.No hay participación entre el moderator1 y el groupon1
	 * 7.No hay participación entre el admin y el groupon1
	 * 8.No hay participación entre un usuario no logueado y el groupon1
	 */
	@Test()
	public void testFindByGrouponIdAndUserId() {
		final Object testingData[][] = {
			{
				"user", "user2", "participation1", "groupon1", null
			}, {
				"user", "user3", "participation2", "groupon1", null
			}, {
				"user", "user1", null, "groupon1", null
			}, {
				"sponsor", "sponsor1", null, "groupon1", null
			}, {
				"company", "company1", null, "groupon1", null
			}, {
				"moderator", "moderator1", null, "groupon1", null
			}, {
				"admin", "admin", null, "groupon1", null
			}, {
				null, null, null, "groupon1", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindByGrouponIdAndUserId((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String participationBean, final int amountProduct, final boolean isMyParticipation, final Class<?> expected) {
		Class<?> caught;
		Participation participation;
		Participation saved;
		Participation copyParticipation;
		int participationIdAux;
		int participationId;

		DataBinder binder;
		Participation participationReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (isMyParticipation == true) {
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
			copyParticipation = this.copyParticipation(participation);

			copyParticipation.setAmountProduct(amountProduct);
			//Editamos los valores

			binder = new DataBinder(copyParticipation);
			participationReconstruct = this.participationService.reconstruct(copyParticipation, binder.getBindingResult()); //Lo reconstruimos
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

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String participationBean, final boolean isMyParticipation, final boolean falseId, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int participationId;
		Participation participation;
		int participationIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			participationId = 0;
			participationIdAux = super.getEntityId(participationBean);
			if (isMyParticipation == true) {
				participationIdAux = super.getEntityId(participationBean);
				participationId = 0;
				for (int i = 1; i <= this.participationService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), 1, 5).getTotalPages(); i++)
					//Si es tuya la cogemos desde el listado
					for (final Participation p : this.participationService.findByUserId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), i, 5).getContent())
						if (p.getId() == participationIdAux)
							participationId = p.getId();
			} else
				participationId = super.getEntityId(participationBean);

			if (findOneToEdit == true) {
				if (falseId == false)
					participation = this.participationService.findOneToEdit(participationId); //Se prueba el findOneToEdit
				else
					participation = this.participationService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				participation = this.participationService.findOne(participationId); //Se prueba el findOne
			else
				participation = this.participationService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(participation); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindByGrouponIdAndUserId(final String user, final String username, final String participationBean, final String grouponbean, final Class<?> expected) {
		Class<?> caught;
		Participation participation;
		int grouponId;

		caught = null;
		grouponId = super.getEntityId(grouponbean);
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (user != null && user.equals("user"))
				participation = this.participationService.findByGrouponIdAndUserId(grouponId, this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId()); //Cogemos si existe participación entre el usuario y la conjunta
			else
				participation = null;

			if (participationBean != null)
				Assert.isTrue(participation.equals(this.participationService.findOne(super.getEntityId(participationBean)))); //Se compara con el esperado
			else
				Assert.isTrue(participation == null);

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public Participation copyParticipation(final Participation participation) {
		Participation result;

		result = new Participation();
		result.setId(participation.getId());
		result.setVersion(participation.getVersion());
		result.setAmountProduct(participation.getAmountProduct());
		result.setGroupon(participation.getGroupon());
		result.setUser(participation.getUser());

		return result;
	}

}
