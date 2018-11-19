package usecases;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Evaluation;
import domain.User;

import services.EvaluationService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditEvaluationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private EvaluationService		evaluationService;

	@Autowired
	private UserService				userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probando que el user3 edita la evaluation4
	 * 2. Probando que el user1 edita la evaluation1
	 * 3. Probando que el user2 edita la evaluation2
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void positiveSaveEvaluationTest() {
		final Object testingData[][] = {
		 {
				"user3", "evaluation4", "Test", 2, true, null
			}, {
				"user1", "evaluation1", "Nuevo contenido", 5, false, null
			}, {
				"user2", "evaluation2", "Cambié de opinión", 4, true, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. El content no puede ser vacio
	 * 2. El content no puede ser nulo
	 * 3. Debe editarlo el usuario de la evaluación.
	 * 4. Solo puede editarlo un usuario.
	 * 5. La puntuación debe estar entre 1 y 5
	 * 6. La puntuación debe estar entre 1 y 5
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void negativeSaveEvaluationTest() {
		final Object testingData[][] = {
				{
					"user3", "evaluation4", "", 2, true, ConstraintViolationException.class
				}, {
					"user1", "evaluation1", null, 5, true, ConstraintViolationException.class
				}, {
					"user5", "evaluation1", "Nueva prueba", 4, true, IllegalArgumentException.class
				}, {
					"sponsor1", "evaluation4", "Buena empresa", 5, true, IllegalArgumentException.class
				}, {
					"user1", "evaluation1", "Gran pedido", -2, true, ConstraintViolationException.class
				}, {
					"user2", "evaluation3", "Regulin regulag", 7, true, ConstraintViolationException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (boolean) testingData[i][4], (Class<?>) testingData[i][5]);
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
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer
	 customer Accedemos a la lista de subscriptions y tomamos la que nos interesa
	 * 4. Le creamos una copia para que no se guarde solo con un set
	 * 5. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 6. Guardamos la subscription copiada con los parámetros
	 * 7. Nos desautentificamos
	 */
	protected void template(final String user, final String evaluationEdit, final String content, final Integer puntuation, final boolean isAnonymous, final Class<?> expected) {
		Class<?> caught;
		Integer userId, evaluationId;
		User userEntity;
		Collection<Evaluation> evaluations;
		Evaluation evaluation, evaluationEntity;

		evaluation = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			evaluationId = super.getEntityId(evaluationEdit);
			Assert.notNull(evaluationId);
			evaluations = this.evaluationService.findByCreatorUserAccountId(userEntity.getUserAccount().getId(), 1, 10).getContent();
			for (Evaluation a : evaluations) {
				if(a.getId() == evaluationId){
					evaluation = a;
					break;
				}
			}
			Assert.notNull(evaluation);
			evaluationEntity = this.copyEvaluation(evaluation);
			evaluationEntity.setContent(content);
			evaluationEntity.setPuntuation(puntuation);
			evaluationEntity.setIsAnonymous(isAnonymous);			
			this.evaluationService.save(evaluationEntity);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer y subscription
	 * 3. Le creamos una copia para que no se guarde solo con un set
	 * 4. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 5. Guardamos la subscription copiada con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void templateNoList(final String user, final String evaluationEdit, final String content, final Integer puntuation, final boolean isAnonymous, final Class<?> expected) {
		Class<?> caught;
		Integer userId, evaluationId;
		User userEntity;
		Evaluation evaluation, evaluationEntity;

		evaluation = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			evaluationId = super.getEntityId(evaluationEdit);
			Assert.notNull(evaluationId);
			evaluation = this.evaluationService.findOne(evaluationId);
			Assert.notNull(evaluation);
			evaluationEntity = this.copyEvaluation(evaluation);
			evaluationEntity.setContent(content);
			evaluationEntity.setPuntuation(puntuation);
			evaluationEntity.setIsAnonymous(isAnonymous);
			this.evaluationService.save(evaluationEntity);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private Evaluation copyEvaluation(final Evaluation evaluation) {
		Evaluation result;
	
		result = new Evaluation();
		result.setId(evaluation.getId());
		result.setVersion(evaluation.getVersion());
		result.setCompany(evaluation.getCompany());
		result.setContent(evaluation.getContent());
		result.setIsAnonymous(evaluation.getIsAnonymous());
		result.setPuntuation(evaluation.getPuntuation());
		result.setUser(evaluation.getUser());
		
		return result;
	}

}