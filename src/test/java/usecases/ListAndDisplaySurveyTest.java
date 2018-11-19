
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.CompanyService;
import services.ModeratorService;
import services.SponsorService;
import services.SurveyService;
import utilities.AbstractTest;
import domain.Company;
import domain.Moderator;
import domain.Sponsor;
import domain.Survey;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListAndDisplaySurveyTest extends AbstractTest {

	// System under test ------------------------------------------------------
	
	@Autowired
	private SurveyService	surveyService;

	@Autowired
	private CompanyService	companyService;
	
	@Autowired
	private SponsorService	sponsorService;
	
	@Autowired
	private ModeratorService	moderatorService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Listar las encuestas de company1 y desplegar una de ellas
	 * 2. Listar las encuestas de moderator2 y desplegar una de ellas
	 * 3. Listar las encuestas de sponsor1 y desplegar una de ellas
	 * 4. Listar las encuestas de company2 y desplegar una de ellas
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 */
	@Test
	public void driverPositive() {

		final Object testingData[][] = {
			{
				"company1", "survey1", null
			}, {
				"moderator2", "survey2", null
			}, {
				"sponsor1", "survey3", null
			}, {
				"company2", "survey5", null
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			
			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			this.templateDisplayUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		
		}
	}

	/*
	 * Pruebas:
	 * 1. Un usuario no autentificado intenta listar y desplegar una encuesta 
	 * 2. Un user1  intenta listar y desplegar una encuesta
	 * 3. company1 intenta listar y desplegar una encuesta  que no es suya
	 * 4. sponsor2 intenta listar y desplegar una encuesta que no es suya
	 * 
	 * Requisitos:
	 * 17.	Las empresas, patrocinadores y moderadores pueden realizar encuestas. 
	 *  Estas tienen un título más un conjunto de preguntas/respuestas ambas 
	 *  definidas por el autor de la encuesta.
	 *   */
	@Test
	public void driverDeleteNegative() {

		final Object testingData[][] = {
			{
				null, "survey1", IllegalArgumentException.class
			}, {
				"user1", "survey1", IllegalArgumentException.class
			}, {
				"company1", "survey3", IllegalArgumentException.class
			}, {
				"sponsor2", "survey5", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {

			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			this.templateDisplayUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		
		}
	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar las encuestas.
	 * 3. Escoger una encuesta.
	 */

	protected void templateListDisplay(final String userName, final String surveyName, final Class<?> expected) {
		Class<?> caught;
		Integer userAccountId;
		Page<Survey> surveys;
		Integer countSurveys;
		Survey surveyChoosen;
		Survey survey;
		Company company;
		Sponsor sponsor;
		Moderator moderator;		

		caught = null;
		userAccountId = null;
		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			surveyChoosen = null;

			survey = this.surveyService.findOne(super.getEntityId(surveyName));
			Assert.notNull(userName);
			company = this.companyService.findOne(super.getEntityId(userName));
			sponsor = this.sponsorService.findOne(super.getEntityId(userName));
			moderator = this.moderatorService.findOne(super.getEntityId(userName));

			if (company != null)
				userAccountId = company.getUserAccount().getId();
			else if (sponsor != null)
				userAccountId = sponsor.getUserAccount().getId();
			else {
				Assert.notNull(moderator);
				userAccountId = moderator.getUserAccount().getId();
			} 
			
			//Obtenemos los surveys
			surveys = this.surveyService.findByActorUserAccountId(userAccountId, 1, 1);
			countSurveys = surveys.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countSurveys; i++) {
				surveys = this.surveyService.findByActorUserAccountId(userAccountId, i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (surveys.getContent().size() == 0)
					break;

				// Navegar hasta el survey que queremos.
				for (final Survey newSurvey : surveys.getContent())
					if (newSurvey.equals(survey)) {
						surveyChoosen = newSurvey;
						break;
					}

				if (surveyChoosen != null)
					break;
			}

			Assert.notNull(surveyChoosen);

			this.surveyService.findOneToEdit(survey.getId());
			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Acceder a la encuesta por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDisplayUrl(final String userName, final String surveyName, final Class<?> expected) {
		Class<?> caught;
		Integer surveyId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			surveyId = super.getEntityId(surveyName);

			this.surveyService.findOneToEdit(surveyId);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
