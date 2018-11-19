
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.PlanService;
import utilities.AbstractTest;
import domain.Plan;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListPlanTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private PlanService	planService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Probamos el findAll estando logeados como user
	 * 2. Probamos el findAll estando logeados como moderator
	 * 3. Probamos el findAll estando logeados como admin
	 * 4. Probamos el findAll estando logeados como company
	 * 5. Probamos el findAll estando logeados como sponsor
	 * 6. Probamos el findAll sin estar logeados
	 * 
	 * Requisito 21.11: Un usuario no autenticado puede ver los planes de pago
	 */
	@Test()
	public void testFindAll() {
		final Object testingData[][] = {
			{
				"user", "user1", "findAll", false, null, null, 2, null, null, null
			}, {
				"moderator", "moderator1", "findAll", false, null, null, 2, null, null, null
			}, {
				"admin", "admin", "findAll", false, null, null, 2, null, null, null
			}, {
				"company", "company1", "findAll", false, null, null, 2, null, null, null
			}, {
				"sponsor", "sponsor1", "findAll", false, null, null, 2, null, null, null
			}, {
				null, null, "findAll", false, null, null, 2, null, null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
					(Integer) testingData[i][7], (Integer) testingData[i][8], (Class<?>) testingData[i][9]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String method, final boolean falseId, final String bean, final Integer page, final Integer size, final Integer tam, final Integer numPages, final Class<?> expected) {
		Class<?> caught;
		Collection<Plan> plans;

		caught = null;
		plans = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (method.equals("findAll"))
				plans = this.planService.findAll(); //Cogemos todos los planes usando el findAll

			Assert.isTrue(plans.size() == size); //Se compara el tamaño con el esperado
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
