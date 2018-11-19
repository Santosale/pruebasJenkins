
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.EvaluationService;
import services.UserService;
import utilities.AbstractTest;
import domain.Evaluation;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListEvaluationTest extends AbstractTest {
		
	// System under test ------------------------------------------------------

	@Autowired
	private EvaluationService	evaluationService;
	
	@Autowired
	private UserService			userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAllEvaluations logueados como moderator1
	 * 2. Probamos obtener el resultado previsto para el metodo findAllEvaluations sin loguear - Solo pueden acceder los moderadores
	 * 3. Probamos obtener el resultado previsto para el metodo findAllEvaluations logueados como moderator2
	 * 4. Probamos nobtener el resultado previsto para el metodo findAllEvaluations logueados como user3 - Solo pueden acceder los moderadores
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void findAllTest() {
		final Object testingData[][] = {
			{
				"moderator1", null, "findAllEvaluations", 5, 0, 0, null
			}, {
				null, null, "findAllEvaluations", 5, 0, 0, IllegalArgumentException.class
			}, {
				"moderator2", null, "findAllEvaluations", 1, 2, 1, null
			}, {
				"user3", null, "findAllEvaluations", 1, 5, 5, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findByCreatorUserAccountId logueados como moderator1
	 * 2. Probamos obtener el resultado previsto para el metodo findByCreatorUserAccountId sin loguear - Solo pueden acceder los usuarios
	 * 3. Probamos obtener el resultado previsto para el metodo findByCreatorUserAccountId logueados como moderator2 - Solo pueden acceder los usuarios
	 * 4. Probamos no btener el resultado previsto para el metodo findByCreatorUserAccountId logueados como user2
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void findByCreatorUserAccountId() {
		final Object testingData[][] = {
			{
				"user1", null, "findByCreatorUserAccountId", 1, 0, 0, null
			}, {
				null, null, "findByCreatorUserAccountId", 4, 0, 0, IllegalArgumentException.class
			}, {
				"moderator2", null, "findByCreatorUserAccountId", 1, 2, 1, IllegalArgumentException.class
			}, {
				"user2", null, "findByCreatorUserAccountId", 2, 0, 0, null				}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findByCompanyId logueados como user1
	 * 2. Probamos obtener el resultado previsto para el metodo findByCompanyId sin loguear
	 * 3. Probamos no obtener el resultado previsto para el metodo findByCompanyId logueados como moderator2
	 * 4. Probamos obtener el resultado previsto para el metodo findByCompanyId logueados como company1
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void findByCompanyId() {
		final Object testingData[][] = {
			{
				"user1", "company1", "findByCompanyId", 1, 0, 0, null
			}, {
				null, "company3", "findByCompanyId", 1, 0, 0, null
			}, {
				"moderator2", "company4", "findByCompanyId", 5, 1, 1, IllegalArgumentException.class
			}, {
				"company1", "company2", "findByCompanyId", 1, 1, 1, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Comprobamos si el método es findAll ó findByUserAccountId
	 * 3. En el caso de que sea findByUserAccountId, obtenemos las entidades correspondientes al user para usar el método
	 * 3. Según el método que sea, se llama a su método y se guarda en la variable sizeSubscription el tamaño de los resultados de cada método
	 * 4. Comprobamos que devuelve el valor esperado
	 * 5. Nos desautentificamos
	 */
	protected void template(final String user, final String company, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<Evaluation> evaluationsCollection;
		int sizeEvaluation;
		int userId, companyId;
		User userEntity;

		sizeEvaluation = 0;
		caught = null;
		try {
			super.authenticate(user);

			if (method.equals("findAllEvaluations")) {
				evaluationsCollection = this.evaluationService.findAllEvaluations(page, size).getContent();
				sizeEvaluation = evaluationsCollection.size();
			} else if (method.equals("findByCreatorUserAccountId")) {
				Assert.notNull(user);
				userId = super.getEntityId(user);
				userEntity = this.userService.findOne(userId);
				Assert.notNull(userEntity);
				evaluationsCollection = this.evaluationService.findByCreatorUserAccountId(userEntity.getUserAccount().getId(), page, size).getContent();
				sizeEvaluation = evaluationsCollection.size();
			} 	else {
				companyId = super.getEntityId(company);
				evaluationsCollection = this.evaluationService.findByCompanyId(companyId, page, size).getContent();
				sizeEvaluation = evaluationsCollection.size();
			}
			Assert.isTrue(sizeEvaluation == tamano); 
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
