package usecases;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Answer;
import domain.Company;
import domain.Moderator;
import domain.Question;
import domain.Sponsor;
import domain.Survey;
import services.AnswerService;
import services.CompanyService;
import services.ModeratorService;
import services.QuestionService;
import services.SponsorService;
import services.SurveyService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteSurveyTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SurveyService		surveyService;
	
	@Autowired
	private CompanyService		companyService;
	
	@Autowired
	private SponsorService		sponsorService;
	
	@Autowired
	private ModeratorService	moderatorService;
	
	@Autowired
	private QuestionService		questionService;
	
	@Autowired
	private AnswerService		answerService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 *  como si accedemos a la entidad desde getEntityId:
	 *  Solo pueden borrar company, sponsor y moderator y solo sus propias encuestas.
	 * 	1. Probando que el company1 borra la survey1
	 * 	2. Probando que el sponsor1 borra la survey3
	 *  3. Probando que el company2 borra la survey5
	 *  4. Probando que el moderator2 borra la survey4
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 *
	 */
	@Test
	public void positiveDeleteSurveyTest() {
		final Object testingData[][] = {
			{
				"company1", "survey1", null
			} , {
				"sponsor1", "survey3", null
			} , {
				"company2", "survey5", null
			} , {
				"moderator2", "survey4", null
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
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 *  como si accedemos a la entidad desde getEntityId:
	 *  Solo pueden borrar company, sponsor y moderator y solo sus propias encuestas.
	 * 	1. Probando que el user1 borra la survey1
	 * 	2. Probando que el sponsor2 borra la survey3
	 * 	3. Probando que el company5 borra la survey3
	 * 	4. Probando que el moderator1 borra la survey1
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 *
	 */
	 @Test
	public void negativeDeleteSurveyTest() {
		final Object testingData[][] = {
				{
					"user1", "survey1", IllegalArgumentException.class
				} , {
					"sponsor2", "survey3", IllegalArgumentException.class
				} , {
					"company5", "survey3", IllegalArgumentException.class
				} , {
					"moderator1", "survey1", IllegalArgumentException.class
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
		 * 3. Accedemos a la lista de surveys y tomamos la que nos interesa
		 * 4. Borramos el survey
		 * 5. Comprobaciones
		 * 6. Nos desautentificamos
		 */
	protected void template(final String user, final String survey, final Class<?> expected) {
		Class<?> caught;
		int userId, surveyId;
		Company companyEntity;
		Sponsor	sponsorEntity;
		Moderator moderatorEntity;
		Survey surveyEntity;
		Collection<Survey> surveys;
		Collection<Question> questions;
		Collection<Answer> answers;

		surveyEntity = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			surveyId = super.getEntityId(survey);
			Assert.notNull(surveyId);
			
			surveys = new HashSet<Survey>();
			if (this.companyService.findOne(userId) != null) {
				companyEntity = this.companyService.findOne(userId);
				Assert.notNull(companyEntity);
				surveys = this.surveyService.findByActorUserAccountId(companyEntity.getUserAccount().getId(), 1, 5).getContent();
			} if (this.sponsorService.findOne(userId) != null) {
				sponsorEntity = this.sponsorService.findOne(userId);
				Assert.notNull(sponsorEntity);
				surveys = this.surveyService.findByActorUserAccountId(sponsorEntity.getUserAccount().getId(), 1, 5).getContent();
			} if (this.moderatorService.findOne(userId) != null) {
				moderatorEntity = this.moderatorService.findOne(userId);
				Assert.notNull(moderatorEntity);
				surveys = this.surveyService.findByActorUserAccountId(moderatorEntity.getUserAccount().getId(), 1, 5).getContent();
			}
			
			Assert.notEmpty(surveys);
			
			for (Survey a : surveys) {
				if(a.getId() == surveyId){
					surveyEntity = a;
					break;
				}
			}
			
			questions = this.questionService.findBySurveyId(surveyEntity.getId());
			answers = new HashSet<Answer>();
			for (Question q : questions)
				answers.addAll(this.answerService.findByQuestionId(q.getId()));
			
			this.surveyService.delete(surveyEntity);
			
			Assert.isTrue(!this.surveyService.findAll().contains(surveyEntity));

			for (Question q:questions)
				Assert.isNull(this.questionService.findOne(q.getId()));
			
			for (Answer a: answers)
				Assert.isNull(this.answerService.findOne(a.getId()));
			
			super.unauthenticate();
			
			Assert.isTrue(!this.surveyService.findAll().contains(surveyEntity));
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Tomamos el id y la entidad de survey
	 * 3. Borramos la evaluación
	 * 4. Nos desautentificamos
	 * 5. Comprobamos que no existe la encuesta, ni sus preguntas o respuestas
	 */
	protected void templateNoList(final String user, final String survey, final Class<?> expected) {
		Class<?> caught;
		int surveyId;
		Survey surveyEntity = null;
		Collection<Question> questions;
		Collection<Answer> answers;

		caught = null;
		try {
			super.authenticate(user);
			surveyId = super.getEntityId(survey);
			surveyEntity = this.surveyService.findOneToEdit(surveyId);
			
			questions = this.questionService.findBySurveyId(surveyEntity.getId());
			answers = new HashSet<Answer>();
			for (Question q : questions)
				answers.addAll(this.answerService.findByQuestionId(q.getId()));
			
			this.surveyService.delete(surveyEntity);
			super.unauthenticate();
			
			Assert.isTrue(!this.surveyService.findAll().contains(surveyEntity));

			for (Question q:questions)
				Assert.isNull(this.questionService.findOne(q.getId()));
			
			for (Answer a: answers)
				Assert.isNull(this.answerService.findOne(a.getId()));
			
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}

