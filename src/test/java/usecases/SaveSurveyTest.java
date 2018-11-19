package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import domain.Answer;
import domain.Question;
import domain.Survey;
import forms.QuestionForm;
import forms.SurveyForm;
import services.AnswerService;
import services.QuestionService;
import services.SurveyService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveSurveyTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SurveyService				surveyService;
	
	@Autowired
	private QuestionService				questionService;
	
	@Autowired
	private AnswerService				answerService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. El usuario company1 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 2. El usuario sponsor2 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 3. El usuario moderator1 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 4. El usuario company5 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 5. El usuario sponsor4 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 6. El usuario moderator2 crea una nueva encuesta con dos preguntas y cada una con dos respuestas.
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un t�tulo m�s un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 */
	@Test
	public void positiveSaveTagTest() {
		final Object testingData[][] = {
				{
					"company1", "user", "company", "Nueva encuesta", "Pregunta 1", "Respuesta 1", "Respuesta 2", "Pregunta 1", "Respusta 1", "Respuesta 2", null
				} , {
					"sponsor2", "sponsor", "sponsor", "Tu comida favorita", "Leche", "Normal", "Desnatada", "Bebida", "Cocacola", "Pepsi", null
				} , {
					"moderator1", "user", "moderator", "Cantidad de anuncios", "M�s anuncios", "S�", "No", "Menos anuncios", "S�", "No", null
				} , {
					"company5", "sponsor", "company", "Te preguntamos por tu pelo", "Tienes piojos?", "S�", "No", "Pero te pica?", "S�", "No", null
				} , {
					"sponsor4", "user", "sponsor", "Parejas", "Tienes pareja?", "Si <3", "Estudio inform�tica", "Tienes amigas?", "Forever alone", "Pues s�", null
				} , {
					"moderator2", "user", "moderator", "Paramos las encuestas", "Te cansa trabajar?", "S�", "No", "Te gusta la p�gina?", "S�", "No", null
				} 
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. La encuesta solo puede ser creada por una compa��a o un patrocinador.
	 * 2. La encuesta solo puede ser creada por una compa��a o un patrocinador.
	 * 3. El t�tulo de la encuesta no puede ser vac�o.
	 * 4. La primera pregunta no puede ser vac�a.
	 * 5. La primera respuesta de la primera pregunta no puede ser vac�a.
	 * 6. La segunda respuesta de la primera pregunta no puede ser vac�a.
	 * 7. La segunda pregunta no puede ser vac�a.
	 * 8. La primera respuesta de la segunda pregunta no puede ser vac�a.
	 * 9. La primera respuesta de la segunda pregunta no puede ser vac�a.
	 * 
	 * 10. El t�tulo de la encuesta no puede ser vac�o.
	 * 11. La primera pregunta no puede ser vac�a.
	 * 12. La primera respuesta de la primera pregunta no puede ser vac�a.
	 * 13. La segunda respuesta de la primera pregunta no puede ser vac�a.
	 * 14. La segunda pregunta no puede ser vac�a.
	 * 15. La primera respuesta de la segunda pregunta no puede ser vac�a.
	 * 16. La primera respuesta de la segunda pregunta no puede ser vac�a.
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un t�tulo m�s un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 */
	@Test
	public void negativeSaveTagTest() {
		final Object testingData[][] = {
				{
					null, "user", "company", "Nueva encuesta", "Pregunta 1", "Respuesta 1", "Respuesta 2", "Pregunta 1", "Respusta 1", "Respuesta 2", IllegalArgumentException.class
				} , {
					"user3", "sponsor", "sponsor", "Tu comida favorita", "Leche", "Normal", "Desnatada", "Bebida", "Cocacola", "Pepsi", IllegalArgumentException.class
				} , {
					"moderator1", "user", "moderator", "", "M�s anuncios", "S�", "No", "Menos anuncios", "S�", "No", ConstraintViolationException.class
				} , {
					"company5", "sponsor", "company", "Te preguntamos por tu pelo", "", "S�", "No", "Pero te pica?", "S�", "No", ConstraintViolationException.class
				} , {
					"sponsor4", "user", "sponsor", "Parejas", "Tienes pareja?", "", "Estudio inform�tica", "Tienes amigas?", "Forever alone", "Pues s�", ConstraintViolationException.class
				} , {
					"moderator2", "user", "moderator", "Paramos las encuestas", "Te cansa trabajar?", "S�", "", "Te gusta la p�gina?", "S�", "No", ConstraintViolationException.class
				} , {
					"moderator1", "user", "moderator", "Cantidad de anuncios", "M�s anuncios", "S�", "No", "", "S�", "No", ConstraintViolationException.class
				} , {
					"company5", "sponsor", "company", "Te preguntamos por tu pelo", "Tienes piojos?", "S�", "No", "Pero te pica?", "", "No", ConstraintViolationException.class
				} , {
					"sponsor4", "user", "sponsor", "Parejas", "Tienes pareja?", "Si <3", "Estudio inform�tica", "Tienes amigas?", "Forever alone", "", ConstraintViolationException.class
				}, {
					"moderator1", "user", "moderator", null, "M�s anuncios", "S�", "No", "Menos anuncios", "S�", "No", IllegalArgumentException.class
				} , {
					"company5", "sponsor", "company", "Te preguntamos por tu pelo", null, "S�", "No", "Pero te pica?", "S�", "No", IllegalArgumentException.class
				} , {
					"sponsor4", "user", "sponsor", "Parejas", "Tienes pareja?", null, "Estudio inform�tica", "Tienes amigas?", "Forever alone", "Pues s�", IllegalArgumentException.class
				} , {
					"moderator2", "user", "moderator", "Paramos las encuestas", "Te cansa trabajar?", "S�", null, "Te gusta la p�gina?", "S�", "No", IllegalArgumentException.class
				} , {
					"moderator1", "user", "moderator", "Cantidad de anuncios", "M�s anuncios", "S�", "No", null, "S�", "No", IllegalArgumentException.class
				} , {
					"company5", "sponsor", "company", "Te preguntamos por tu pelo", "Tienes piojos?", "S�", "No", "Pero te pica?", null, "No", IllegalArgumentException.class
				} , {
					"sponsor4", "user", "sponsor", "Parejas", "Tienes pareja?", "Si <3", "Estudio inform�tica", "Tienes amigas?", "Forever alone", null, IllegalArgumentException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
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
	 * 2. Inicializamos todos los par�metros
	 * 3. Le asignamos los valores dados
	 * 4. A�adimos la pregunta 1 y sus respuestas
	 * 5. A�adimos la pregunta 2 y sus respuestas
	 * 6. Creamos surveyForm
	 * 7. Guardamos la encuesta
	 * 8. Cerramos la transacci�n
	 * 9. Nos desautentificamos
	 * 10. Realizados las comprobaciones
	 */
	protected void template(final String user, final String actor, final String model, final String title, final String question1, final String answer11, final String answer12, final String question2, final String answer21, final String answer22, final Class<?> expected) {
		Class<?> caught;
		Collection<QuestionForm> questionsForm;
		Collection<Question> questions;
		List<Answer> answers1, answers2;
		QuestionForm questionEntity1, questionEntity2;
		Answer answerEntity11, answerEntity12, answerEntity21, answerEntity22;
		Survey survey;
		SurveyForm surveyForm;
		DataBinder binder;
		Question questionReconstruct1, questionReconstruct2;
		
		caught = null;
		try {
			super.authenticate(user);
			
			// Inicializamos todos los par�metros
			questionsForm = new HashSet<QuestionForm>();
			survey = this.surveyService.create();
			surveyForm = new SurveyForm();
			questionEntity1 = new QuestionForm();
			questionEntity2 = new QuestionForm();
			questionReconstruct1 = this.questionService.reconstructFromSurvey(questionEntity1, survey, 1);
			questionReconstruct2 = this.questionService.reconstructFromSurvey(questionEntity1, survey, 1);
			answerEntity11 = this.answerService.create(questionReconstruct1);
			answerEntity12 = this.answerService.create(questionReconstruct1);
			answerEntity21 = this.answerService.create(questionReconstruct2);
			answerEntity22 = this.answerService.create(questionReconstruct2);
			answers1 = new ArrayList<Answer>();
			answers2 = new ArrayList<Answer>();
			
			// Le asignamos los valores dados
			questionEntity1.setText(question1);
			questionEntity2.setText(question2);
			answerEntity11.setText(answer11);
			answerEntity12.setText(answer12);
			answerEntity21.setText(answer21);
			answerEntity22.setText(answer22);
			
			// A�adimos la pregunta 1 y sus respuestas
			answers1.add(answerEntity11);
			answers1.add(answerEntity12);
			questionEntity1.setAnswers(answers1);
			questionsForm.add(questionEntity1);

			// A�adimos la pregunta 2 y sus respuestas
			answers2.add(answerEntity21);
			answers2.add(answerEntity22);
			questionEntity2.setAnswers(answers2);
			questionsForm.add(questionEntity2);			
			
			// Creamos surveyForm
			surveyForm.setQuestions(questionsForm);
			surveyForm.setTitle(title);
			surveyForm.setMinimumPoints(0);
			surveyForm.setToActor(actor);
			surveyForm.setHasAds(false);
			
			// Guardamos la encuesta
			binder = new DataBinder(surveyForm);
			survey = this.surveyService.reconstruct(surveyForm, model, binder.getBindingResult());
			survey = this.surveyService.save(survey, surveyForm);
			
			// Cerramos la transacci�n
			super.unauthenticate();
			super.flushTransaction();
			 
			// Comprobaciones
			Assert.isTrue(this.surveyService.findAll().contains(survey));
			questions = this.questionService.findBySurveyId(survey.getId());
			Assert.isTrue(this.questionService.findAll().containsAll(questions));
			
			for (Question q : questions) {
				for (Answer a : this.answerService.findByQuestionId(q.getId())){
					Assert.isTrue(a.getText().equals(answer11) || a.getText().equals(answer12)|| a.getText().equals(answer21) || a.getText().equals(answer22));
				}
			}

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}