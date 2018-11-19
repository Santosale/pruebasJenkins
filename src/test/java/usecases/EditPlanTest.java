
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

import services.PlanService;
import utilities.AbstractTest;
import domain.Plan;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditPlanTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private PlanService	planService;


	/*
	 * Test
	 * 1.Editamos los campos del plan1 con valores correctos (no salta excepción)
	 * 2.Editamos los campos del plan1 poniendo la descripción vacía (salta un ConstraintViolationException)
	 * 3.Editamos los campos del plan1 poniendo la descripción a nula (salta un ConstraintViolationException)
	 * 4.Editamos los campos del plan1 poniendo un coste negativo (salta un ConstraintViolationException)
	 * 5.Editamos los campos del plan1 logeados como user (salta un IllegalArgumentException)
	 * 6.Editamos los campos del plan1 logeados como moderator (salta un IllegalArgumentException)
	 * 7.Editamos los campos del plan1 logeados como company (salta un IllegalArgumentException)
	 * 8.Editamos los campos del plan1 logeados como sponsor (salta un IllegalArgumentException)
	 * 9.Editamos los campos del plan1 sin estar logeados (salta un IllegalArgumentException)
	 * 
	 * Requisito 30.3: Un actor que está autenticado como administrador debe ser capaz de editar los planes de pago
	 */
	@Test()
	public void testEdit() {
		final Object testingData[][] = {
			{
				"admin", "admin", "plan1", "Descripción nueva", 25.0, null
			}, {
				"admin", "admin", "plan1", "", 25.0, ConstraintViolationException.class
			}, {
				"admin", "admin", "plan1", null, 25.0, ConstraintViolationException.class
			}, {
				"admin", "admin", "plan1", "Descripción nueva", -1.0, ConstraintViolationException.class
			}, {
				"user", "user1", "plan1", "Descripción nueva", 25.0, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "plan1", "Descripción nueva", 25.0, IllegalArgumentException.class
			}, {
				"company", "company1", "plan1", "Descripción nueva", 25.0, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "plan1", "Descripción nueva", 25.0, IllegalArgumentException.class
			}, {
				null, null, "plan1", "Descripción nueva", 25.0, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Double) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logeados como admin hacemos el findOneToEdit del plan1 (no salta excepción)
	 * 2.Logeados como admin hacemos el findOneToEdit del plan2 (no salta excepción)
	 * 3.Logeados como moderator1 hacemos el findOne del plan1 (no salta excepción)
	 * 4.Logeados como moderator1 hacemos el findOneToEdit del plan1(salta un IllegalArgumentException)
	 * 5.Logeados como admin hacemos el findOneToEdit de una plan de id 0 (salta un IllegalArgumentException)
	 * 6.Logeados como admin hacemos el findOne de una plan de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"admin", "admin", "plan1", false, true, null
			}, {
				"admin", "admin", "plan2", false, true, null
			}, {
				"moderator", "moderator1", "plan1", false, false, null
			}, {
				"moderator", "moderator1", "plan1", false, true, IllegalArgumentException.class
			}, {
				"admin", "admin", "plan1", true, false, IllegalArgumentException.class
			}, {
				"admin", "admin", "plan1", true, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logeados como admin hacemos el findByUserId del user1 (no salta excepción)
	 * 2.Logeados como admin hacemos el findByUserId del user2 (no salta excepción)
	 * 3.Logeados como moderator hacemos el findByUserId del user5 que no tiene plan, se simula con un IllegalArgumentException
	 * 4.Logeados como admin hacemos el findByUserId de un usuario con id 0(salta un IllegalArgumentException)
	 */
	@Test
	public void testFindByUserId() {
		final Object testingData[][] = {
			{
				"admin", "admin", "user1", false, null
			}, {
				"admin", "admin", "user2", false, null
			}, {
				"moderator", "moderator1", "user5", false, IllegalArgumentException.class
			}, {
				"admin", "admin", "user2", true, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindByUserId((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String planBean, final String description, final Double cost, final Class<?> expected) {
		Class<?> caught;
		Plan plan;
		Plan saved;
		Plan copyPlan;
		int planId;
		int planIdAux;

		DataBinder binder;
		Plan planReconstruct;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			planIdAux = super.getEntityId(planBean);
			planId = 0;
			for (final Plan p : this.planService.findAll())
				//Para coger el plan a editar accedemos al findAll
				if (p.getId() == planIdAux)
					planId = p.getId();

			plan = this.planService.findOneToEdit(planId); //Creamos el plan
			copyPlan = this.copyPlan(plan);
			copyPlan.setDescription(description);
			copyPlan.setCost(cost);
			//Modificamos sus campos

			binder = new DataBinder(copyPlan);
			planReconstruct = this.planService.reconstruct(copyPlan, binder.getBindingResult());
			saved = this.planService.save(planReconstruct); //Guardamos el plan
			super.flushTransaction();

			Assert.isTrue(this.planService.findAll().contains(saved)); //Miramos si están entre todos las planes a volumen de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String planBean, final boolean falseId, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int planId;
		Plan plan;
		int planIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			planId = 0;
			planIdAux = super.getEntityId(planBean);
			for (final Plan p : this.planService.findAll())
				if (planIdAux == p.getId())
					planId = p.getId();

			if (findOneToEdit == true) {
				if (falseId == false)
					plan = this.planService.findOneToEdit(planId); //Se prueba el findOneToEdit
				else
					plan = this.planService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				plan = this.planService.findOne(planId); //Se prueba el findOne
			else
				plan = this.planService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(plan); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindByUserId(final String user, final String username, final String userBean, final boolean falseId, final Class<?> expected) {
		Class<?> caught;
		Plan plan;
		int userId;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			userId = super.getEntityId(userBean);

			if (falseId == false)
				plan = this.planService.findByUserId(userId); //Se prueba el findByUserId
			else
				plan = this.planService.findByUserId(0); //Se prueba el findByUserId con id 0 

			Assert.notNull(plan); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public Plan copyPlan(final Plan plan) {
		Plan result;

		result = new Plan();
		result.setName(plan.getName());
		result.setId(plan.getId());
		result.setVersion(plan.getVersion());
		result.setDescription(plan.getDescription());
		result.setCost(plan.getCost());

		return result;
	}
}
