package usecases;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Answer;
import domain.Question;
import domain.Survey;
import services.AnswerService;
import services.QuestionService;
import services.SurveyService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AnswerSurveyTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SurveyService		surveyService;

	@Autowired
	private QuestionService		questionService;
	
	@Autowired
	private AnswerService		answerService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probando que el user1 contesta a la survey1.
	 * 2. Probando que el sponsor2 contesta a la survey2.
	 * 3. Probando que el user3 contesta a la survey3.
	 * 4. Probando que el sponsor4 contesta a la survey4.
	 * 5. Probando que el user5 contesta a la survey5.
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 */
	@Test
	public void positiveSaveSurveyTest() {
		final Object testingData[][] = {
			{
				"user1", "survey1", null
			} , {
				"sponsor2", "survey2", null
			} , {
				"user3", "survey3", null
			} , {
				"sponsor4", "survey4", null
			} , {
				"user5", "survey5", null
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
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Probando que alguien sin loguear contesta a la survey1.
	 * 2. Probando que la company1 contesta a la survey2. - Solo pueden usuarios y patrocinadores.
	 * 3. Probando que el administrator contesta a la survey3. - Solo pueden usuarios y patrocinadores.
	 * 4. Probando que el moderator2 contesta a la survey4. - Solo pueden usuarios y patrocinadores.
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 */
	@Test
	public void negativeSaveSurveyTest() {
		final Object testingData[][] = {
				{
					null, "survey1", IllegalArgumentException.class
				} , {
					"company1", "survey2", IllegalArgumentException.class
				} , {
					"administrador", "survey3", IllegalArgumentException.class
				} , {
					"moderator2", "survey4", IllegalArgumentException.class
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
		
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Accedemos a la vista para responder la encuesta
	 * 3. Escogemos las preguntas de la encuesta, así como las respuestas de cada una de ellas.
	 * 4. Se escogerá la primera respuesta para cada pregunta.
	 * 5. Actualizamos los contadores de esas respuestas
	 * 6. Comprobaciones
     * 7. Nos desautentificamos
	 * 
	 */
	protected void template(final String user, final String surveyAnswer, final Class<?> expected) {
		Class<?> caught;
		Integer surveyId;
		Collection<Question> questions;
		Collection<Answer> answers, answersCount;
		Survey survey;
		Answer answer;
		Map<Answer,Integer> contadores;

		contadores = new HashMap<Answer, Integer>();
		answersCount = new HashSet<Answer>();
		answers = new HashSet<Answer>();
		answer = new Answer();
		survey = new Survey();
		caught = null;
		try {
			super.authenticate(user);
			surveyId = super.getEntityId(surveyAnswer);
			Assert.notNull(surveyId);
			
			// Accedemos a la vista para responder la encuesta
			survey = this.surveyService.findOne(surveyId);
			Assert.notNull(survey);
			
			// Escogemos las preguntas de la encuesta, así como las respuestas de cada una de ellas.
			questions = this.questionService.findBySurveyId(survey.getId());

			for (Question question : questions) {

				// Se escogerá la primera respuesta para cada pregunta.
				answers = this.answerService.findByQuestionId(question.getId());
				for (Answer a : answers) {
					answersCount.add(a);
					contadores.put(a, a.getCounter());
					answer = a;
					break;
				}

				// Actualizamos los contadores de esas respuestas
				Assert.notNull(answer);
				this.answerService.addCounter(answer);
			}
			
			// Comprobaciones
			
			Assert.isTrue(contadores.size() == questions.size());
			
			for (Answer a : answersCount) {
				Assert.isTrue((contadores.get(a) + 1) == a.getCounter());
			}
			
			super.unauthenticate();
			super.flushTransaction();
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}