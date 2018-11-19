package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

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
public class DeleteEvaluationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private EvaluationService		evaluationService;
	
	@Autowired
	private UserService				userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 *  como si accedemos a la entidad desde getEntityId:
	 * 	1. Probando que el user2 borra la evaluation2
	 * 	2. Probando que el moderator1 borra la evaluation3
	 *  3. Probando que el user1 borra la evaluation1
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 * 39.	Por defecto los usuarios comienzan con 50 puntos y podrán verse penalizados 
	 * con -10 puntos cada vez que el moderador tenga que borrar alguna de sus publicaciones 
	 * (conjuntas, comentarios, etc) por inapropiadas. 
	 *
	 */
	@Test
	public void positiveDeleteEvaluationTest() {
		final Object testingData[][] = {
			{
				"user2", "evaluation2", null
			} , {
				"moderator1", "evaluation3", null
			} , {
				"user1", "evaluation1", null
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
	
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 
	 * Primero se realizarán las pruebas desde un listado y luego
	 * como si accedemos a la entidad desde getEntityId:
	 * 
	 * 1. No puede borrarlo un usuario no logueado
	 * 2. Solo puede borrarlo un user
	 * 3. Solo puede borrarlo un user
	 * 4. Probando que el user3 borra la evaluation3 - No puede borrarlo ya que no le pertenece
	 * 5. Probando que el user1 borra la evaluation4 - No puede borrarlo ya que no le pertenece
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 * 39.	Por defecto los usuarios comienzan con 50 puntos y podrán verse penalizados 
	 * con -10 puntos cada vez que el moderador tenga que borrar alguna de sus publicaciones 
	 * (conjuntas, comentarios, etc) por inapropiadas. 
	 *
	 */
	 @Test
	public void negativeDeleteEvaluationTest() {
		final Object testingData[][] = {
			{
				null, "evaluation1", IllegalArgumentException.class 
			}, 	{
				"administrator", "evaluation1", IllegalArgumentException.class
			}, {
				"company1", "evaluation2", IllegalArgumentException.class 
			}, {
				"user3", "evaluation3", IllegalArgumentException.class 
			} , {
				"user1", "evaluation4", IllegalArgumentException.class 
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
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
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
		 * 2. Tomamos el id y la entidad de user
		 * 3. Accedemos a la lista de evaluations y tomamos la que nos interesa
		 * 4. Borramos el evaluation
		 * 5. Nos desautentificamos
		 */
	protected void template(final String user, final String evaluation, final Class<?> expected) {
		Class<?> caught;
		int userId, evaluationId;
		User userEntity;
		Evaluation evaluationEntity;
		Collection<Evaluation> evaluations;

		evaluationEntity = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			evaluationId = super.getEntityId(evaluation);
			Assert.notNull(evaluationId);
			if (userEntity != null) {
				evaluations = this.evaluationService.findByCreatorUserAccountId(userEntity.getUserAccount().getId(), 1, 99).getContent();
				for (Evaluation a : evaluations) {
					if(a.getId() == evaluationId){
						evaluationEntity = a;
						break;
					}
				}
				this.evaluationService.delete(evaluationEntity);
			} else { 
				evaluations = this.evaluationService.findAllEvaluations(1, 99).getContent();
				for (Evaluation a : evaluations) {
					if(a.getId() == evaluationId){
						evaluationEntity = a;
						break;
					}
				}
				this.evaluationService.deleteModerator(evaluationEntity);
			}
			super.unauthenticate();
			
			Assert.isTrue(!this.evaluationService.findAll().contains(evaluationEntity));

			super.flushTransaction();
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Tomamos el id y la entidad de evaluation
	 * 3. Borramos la evaluación
	 * 4. Nos desautentificamos
	 * 5. Comprobamos que no existe la evaluación borrado
	 */
	protected void templateNoList(final String user, final String evaluation, final Class<?> expected) {
		Class<?> caught;
		int userId, evaluationId;
		User userEntity;		
		Evaluation evaluationEntity;

		userEntity = null;
		caught = null;
		try {
			super.authenticate(user);
			evaluationId = super.getEntityId(evaluation);
			evaluationEntity = this.evaluationService.findOne(evaluationId);
			if (user != null) {
				userId = super.getEntityId(user);
				userEntity = this.userService.findOne(userId);
			} 
			if (userEntity != null) 
				this.evaluationService.delete(evaluationEntity);
			else 
				this.evaluationService.deleteModerator(evaluationEntity);
			
			Assert.isTrue(!this.evaluationService.findAll().contains(evaluationEntity));
		
			super.unauthenticate();
			
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}

