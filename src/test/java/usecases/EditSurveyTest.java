package usecases;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Answer;
import domain.Question;
import domain.Survey;
import forms.QuestionForm;
import forms.SurveyForm;

import security.LoginService;
import services.AnswerService;
import services.QuestionService;
import services.SurveyService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditSurveyTest extends AbstractTest {

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
	 * 1. Probando que la company1 edita la survey1, una de sus preguntas y una de sus respuestas
	 * 2. Probando que el moderator2 edita la survey2, una de sus preguntas y una de sus respuestas
	 * 3. Probando que el sponsor1 edita la survey3, una de sus preguntas y una de sus respuestas
	 * 4. Probando que el moderator2 edita la survey4, una de sus preguntas y una de sus respuestas
	 * 5. Probando que el company2 edita la survey5, una de sus preguntas y una de sus respuestas
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
				"company1", "survey1", "Nueva encuesta", "¿Te gusta la página?", "Que va, nada.", null
			} , {
				"moderator2", "survey2", "Los exámenes finales", "¿Vas a aprobar?", "No :(", null
			} , {
				"sponsor1", "survey3", "Quieres más chollos?", "De que tipo?", "Electrónica", null
			} , {
				"moderator2", "survey4", "El futuro", "¿Es negro?", "Cada vez peor", null
			} , {
				"company2", "survey5", "Como haces las encuestas?", "No sabes que poner?", "Me lo invento", null
			}
		};
			
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Solo pueden editarlo su moderador, su empresa o su patrocinador.
	 * 2. Solo pueden editarlo su moderador, su empresa o su patrocinador.
	 * 3. Solo pueden editarlo su moderador, su empresa o su patrocinador.
	 * 4. El título de la encuesta no puede estar vacío.
	 * 5. El título de la encuesta no puede estar nulo.
	 * 6. La pregunta no puede estar vacía.
	 * 7. La pregunta no puede estar nula.
	 * 8. La respuesta no puede estar vacía.
	 * 9. La respuesta no puede estar nula.
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
					"user2", "survey1", "Nueva encuesta", "¿Te gusta la página?", "Que va, nada.", IllegalArgumentException.class
				} , {
					null, "survey2", "Los exámenes finales", "¿Vas a aprobar?", "No :(", IllegalArgumentException.class
				} , {
					"company5", "survey3", "Quieres más chollos?", "De que tipo?", "Electrónica", IllegalArgumentException.class
				} , {
					"moderator2", "survey4", "", "¿Es negro?", "Cada vez peor", ConstraintViolationException.class
				} , {
					"company2", "survey5", null, "No sabes que poner?", "Me lo invento", IllegalArgumentException.class
				} , {
					"company1", "survey1", "Nueva encuesta", "", "Que va, nada.", ConstraintViolationException.class
				} , {
					"moderator2", "survey2", "Los exámenes finales", null, "No :(", IllegalArgumentException.class
				} , {
					"sponsor1", "survey3", "Quieres más chollos?", "De que tipo?", "", ConstraintViolationException.class
				} , {
					"moderator2", "survey4", "El futuro", "¿Es negro?", null, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);
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
	 * 2. Tomamos el id y la entidad de survey
	 * 3. Accedemos al listado de encuestas
	 * 4. Escogemos y editamos la encuesta
	 * 5. Escogemos y editamos la pregunta
	 * 6. Creamos QuestionForm
	 * 7. Creamos SurveyForm
	 * 8. Guardamos
	 * 9. Comprobaciones
     * 10. Nos desautentificamos
	 * 
	 */
	protected void template(final String user, final String surveyEdit, final String titleSurvey, final String textQuestion, final String textAnswer, final Class<?> expected) {
		Class<?> caught;
		Integer surveyId;
		Collection<Survey> surveys;
		Collection<Question> questions;
		Collection<Answer> answers;
		Survey survey, surveyEntity, surveySaved;
		Question question, questionEntity, questionSaved;
		Answer answer, answerEntity, answerSaved;
		SurveyForm surveyForm;
		Collection<QuestionForm> questionsForm;
		QuestionForm questionForm;

		questionForm = new QuestionForm();
		question = new Question();
		answer = new Answer();
		survey = new Survey();
		surveyForm = new SurveyForm();
		caught = null;
		try {
			super.authenticate(user);
			surveyId = super.getEntityId(surveyEdit);
			Assert.notNull(surveyId);
			
			// Escogemos y editamos la encuesta
			surveys = this.surveyService.findByActorUserAccountId(LoginService.getPrincipal().getId(), 1, 10).getContent();
			for (Survey a : surveys) {
				if(a.getId() == surveyId){
					survey = a;
					break;
				}
			}
			
			Assert.notNull(survey);
			surveyEntity = this.copySurvey(survey);
			surveyEntity.setTitle(titleSurvey);

			// Escogemos y editamos la pregunta
			questions = this.questionService.findBySurveyId(survey.getId());
			for (Question q : questions) {
				if(q.getNumber() == 1){
					question = q;
					break;
				}
			}
			
			Assert.notNull(question);
			questionEntity = this.copyQuestion(question);
			questionEntity.setText(textQuestion);
			
			// Escogemos y editamos la respuesta
			answers = this.answerService.findByQuestionId(questionEntity.getId());
			for (Answer a : answers) {
				answer = a;
				break;
			}
			
			Assert.notNull(answer);
			answerEntity = this.copyAnswer(answer);
			answerEntity.setText(textAnswer);
			
			// Creamos QuestionForm
			questionsForm = new HashSet<QuestionForm>();
			for (Question q : questions){
				questionForm.setAnswers(this.answerService.findByQuestionId(q.getId()));
				questionForm.setText(q.getText());
				questionsForm.add(questionForm);
			}
			
			// Creamos SurveyForm
			surveyForm.setSurveyer(survey.getSurveyer());
			surveyForm.setQuestions(questionsForm);
			surveyForm.setHasAds(false);
			surveyForm.setTitle(survey.getTitle());
			surveyForm.setToActor("USER");
			surveyForm.setMinimumPoints(0);
						
			// Guardamos
			surveySaved = this.surveyService.save(surveyEntity, surveyForm);
			questionSaved = this.questionService.save(questionEntity);
			answerSaved = this.answerService.save(answerEntity);
			
			// Comprobaciones
			Assert.isTrue(this.surveyService.findOne(surveySaved.getId()).getTitle().equals(titleSurvey));
			Assert.isTrue(this.questionService.findOne(questionSaved.getId()).getText().equals(textQuestion));
			Assert.isTrue(this.answerService.findOne(answerSaved.getId()).getText().equals(textAnswer));
			
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
	 * 1. Nos autentificamos como user
	 * 2. Tomamos el id y la entidad de survey
	 * 3. Accedemos directamente a la encuesta
	 * 4. Escogemos y editamos la encuesta
	 * 5. Escogemos y editamos la pregunta
	 * 6. Creamos QuestionForm
	 * 7. Creamos SurveyForm
	 * 8. Guardamos
	 * 9. Comprobaciones
     * 10. Nos desautentificamos
	 * 
	 */
	protected void templateNoList(final String user, final String surveyEdit, final String titleSurvey, final String textQuestion, final String textAnswer, final Class<?> expected) {
		Class<?> caught;
		Integer surveyId;
		Survey survey, surveyEntity, surveySaved;
		Question question, questionEntity, questionSaved;
		Answer answer, answerEntity, answerSaved;
		Collection<Question> questions;
		Collection<Answer> answers;
		SurveyForm surveyForm;
		Collection<QuestionForm> questionsForm;
		QuestionForm questionForm;

		questionForm = new QuestionForm();
		question = new Question();
		answer = new Answer();
		survey = new Survey();
		surveyForm = new SurveyForm();
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			
			// Escogemos y editamos la encuesta
			surveyId = super.getEntityId(surveyEdit);
			Assert.notNull(surveyId);
			survey = this.surveyService.findOne(surveyId);
			Assert.notNull(survey);
			surveyEntity = this.copySurvey(survey);
			surveyEntity.setTitle(titleSurvey);

			// Escogemos y editamos la pregunta
			questions = this.questionService.findBySurveyId(survey.getId());
			for (Question q : questions) {
				if(q.getNumber() == 1){
					question = q;
					break;
				}
			}
			
			Assert.notNull(question);
			questionEntity = this.copyQuestion(question);
			questionEntity.setText(textQuestion);
			
			// Escogemos y editamos la respuesta
			answers = this.answerService.findByQuestionId(questionEntity.getId());
			for (Answer a : answers) {
				answer = a;
				break;
			}
			
			Assert.notNull(answer);
			answerEntity = this.copyAnswer(answer);
			answerEntity.setText(textAnswer);
			
			// Creamos QuestionForm
			questionsForm = new HashSet<QuestionForm>();
			for (Question q : questions){
				questionForm.setAnswers(this.answerService.findByQuestionId(q.getId()));
				questionForm.setText(q.getText());
				questionsForm.add(questionForm);
			}
			
			// Creamos SurveyForm
			surveyForm.setSurveyer(survey.getSurveyer());
			surveyForm.setQuestions(questionsForm);
			surveyForm.setHasAds(false);
			surveyForm.setTitle(survey.getTitle());
			surveyForm.setToActor("USER");
			surveyForm.setMinimumPoints(0);
						
			// Guardamos
			surveySaved = this.surveyService.save(surveyEntity, surveyForm);
			questionSaved = this.questionService.save(questionEntity);
			answerSaved = this.answerService.save(answerEntity);
			
			// Comprobaciones
			Assert.isTrue(this.surveyService.findOne(surveySaved.getId()).getTitle().equals(titleSurvey));
			Assert.isTrue(this.questionService.findOne(questionSaved.getId()).getText().equals(textQuestion));
			Assert.isTrue(this.answerService.findOne(answerSaved.getId()).getText().equals(textAnswer));
			
			
			super.unauthenticate();
			super.flushTransaction();
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private Survey copySurvey(final Survey survey) {
		Survey result;
	
		result = new Survey();
		result.setId(survey.getId());
		result.setVersion(survey.getVersion());
		result.setSurveyer(survey.getSurveyer());
		result.setTitle(survey.getTitle());
		
		return result;
	}
	
	private Question copyQuestion(final Question question) {
		Question result;
	
		result = new Question();
		result.setId(question.getId());
		result.setVersion(question.getVersion());
		result.setNumber(question.getNumber());
		result.setSurvey(question.getSurvey());
		result.setText(question.getText());
		
		return result;
	}
	
	private Answer copyAnswer(final Answer answer) {
		Answer result;
	
		result = new Answer();
		result.setId(answer.getId());
		result.setVersion(answer.getVersion());
		result.setCounter(answer.getCounter());
		result.setText(answer.getText());
		result.setQuestion(answer.getQuestion());
		
		return result;
	}

}