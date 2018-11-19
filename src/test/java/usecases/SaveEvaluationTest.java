package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Company;
import domain.Evaluation;
import domain.User;

import services.CompanyService;
import services.EvaluationService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveEvaluationTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private EvaluationService		evaluationService;

	@Autowired
	private UserService				userService;
	
	@Autowired
	private CompanyService			companyService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * Probamos la creación de varios evaluations por parte de diferentes usuarios.
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void positiveSaveEvaluationTest() {
		final Object testingData[][] = {
				{
					"user3", "user3", "company1", "Test", 2, true, null
				}, {
					"user1", "user1", "company2", "Nuevo contenido", 5, false, null
				}, {
					"user2", "user2", "company3", "Tengo que dar mi opinión", 4, true, null
				}, {
					"user4", "user4", "company4", "Una gran empresa", 3, false, null
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Integer) testingData[i][4], (boolean) testingData[i][5], (Class<?>) testingData[i][6]);
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
	 * 3. La puntuación debe estar entre 1 y 5
	 * 4. La puntuación debe estar entre 1 y 5
	 * 5. Debe editarlo el usuario de la evaluación.
	 * 6. Solo puede editarlo un usuario.
	 * 
	 * Requisitos:
	 * 25.	Un actor autenticado como usuario debe ser capaz de:
	 * 		6.	Valorar las empresas.
	 */
	@Test
	public void negativeSaveEvaluationTest() {
		final Object testingData[][] = {
				{
					"user3", "user3", "company1", "", 2, true, ConstraintViolationException.class
				}, {
					"user1", "user1", "company2", null, 5, false, ConstraintViolationException.class
				}, {
					"user2", "user2", "company3", "Tengo que dar mi opinión", -2, true, ConstraintViolationException.class
				}, {
					"user4", "user4", "company4", "Una gran empresa", 6, false, ConstraintViolationException.class
				}, {
					"user1", "user3", "company4", "Vaya caca", 1, false, IllegalArgumentException.class
				}, {
					"sponsor1", "user3", "company4", "Vaya caca", 1, false, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Integer) testingData[i][4], (boolean) testingData[i][5], (Class<?>) testingData[i][6]);
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
	 * 1. Nos autentificamos como el usuario user
	 * 2. Tomamos el id de writer
	 * 3. Tomamos la entidad correspondiente a al id de writer
	 * 4. Creamos una nueva evaluation pasando el user como parámetros
	 * 5. Le asignamos los parametros correspondientes
	 * 6. Guardamos la nueva evaluación
	 * 7. Nos desautentificamos
	 * 8. Comprobamos se ha creado y existe
	 */
	protected void template(final String user, final String wrtiter, final String company, final String content, final Integer puntuation, final boolean isAnonymous,final Class<?> expected) {
		Class<?> caught;
		Integer userId, companyId;
		User userEntity;
		Evaluation evaluation, evaluationEntity;
		Company companyEntity;

		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(wrtiter);
			userId = super.getEntityId(wrtiter);
			Assert.notNull(userId);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			Assert.notNull(company);
			companyId = super.getEntityId(company);
			Assert.notNull(companyId);
			companyEntity = this.companyService.findOne(companyId);
			Assert.notNull(companyEntity);
			evaluation = this.evaluationService.create(companyEntity, userEntity);
			evaluation.setContent(content);
			evaluation.setPuntuation(puntuation);
			evaluation.setIsAnonymous(isAnonymous);
			evaluationEntity = this.evaluationService.save(evaluation);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.evaluationService.findAll().contains(evaluationEntity));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}