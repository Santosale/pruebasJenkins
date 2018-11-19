
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ArticleService;
import services.FollowUpService;
import services.UserService;
import utilities.AbstractTest;
import domain.Article;
import domain.FollowUp;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteFollowUpTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private FollowUpService	followUpService;

	@Autowired
	private ArticleService	articleService;

	@Autowired
	private UserService		userService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Delete Follow-up navegando por user, followUp, con user2.
	 * 2. Delete Follow-up navegando por user, followUp, con user1.
	 */

	@Test
	public void driverPositive() {

		//Rol,article, followUp, ExpectedException
		final Object testingData2[][] = {
			{
				"user2", "followUp2", null
			}, {
				"user1", "followUp1", null
			},
		};

		for (int i = 0; i < testingData2.length; i++)

			this.templateDeleteMyFollowUps((String) testingData2[i][0], (String) testingData2[i][1], (Class<?>) testingData2[i][2]);

	}

	//Pruebas
	/*
	 * 1. Eliminar Follow-up sin autenticarse.IllegalArgumentException
	 * 2. Eliminar Follow-up autenticandose como otro usuario al creador. IllegalArgumentException
	 * 3. Eliminar Follow-up con otro rol. IllegalArgumentException
	 * 4. Eliminar Follow-up sin artículo. IllegalArgumentException
	 */

	@Test
	public void driverNegative() {

		//Rol,article,followUp, Exception
		final Object testingData[][] = {
			{
				null, "article1", "followUp1", IllegalArgumentException.class
			}, {
				"user2", "article1", "followUp1", IllegalArgumentException.class
			}, {
				"customer2", "article1", "followUp1", IllegalArgumentException.class
			}, {
				"user1", null, "followUp1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteNegative((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los followUp de un usuario.
	 * 3. Navegar y escoger el followUp deseado.
	 */
	protected void templateDeleteMyFollowUps(final String userName, final String followUpName, final Class<?> expected) {
		Class<?> caught;

		User user;
		Page<FollowUp> followUps;
		int countFollowUp;
		int followUpId;
		int userId;
		FollowUp followUp;
		FollowUp followUpChoosen;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			followUpId = super.getEntityId(followUpName);
			userId = super.getEntityId(userName);

			followUp = this.followUpService.findOne(followUpId);
			user = this.userService.findOne(userId);

			//Inicializamos
			followUpChoosen = null;

			//Obtenemos la colección de los follow-ups
			followUps = this.followUpService.findByUserIdPaginated(user.getId(), 1, 1);
			countFollowUp = followUps.getTotalPages();

			//Buscamos el followUp
			for (int i = 0; i < countFollowUp; i++) {

				followUps = this.followUpService.findByUserIdPaginated(user.getId(), i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (followUps.getContent().size() == 0)
					break;

				//3. Navegar hasta el followUp que queremos que queremos.
				for (final FollowUp newFollowUp : followUps.getContent())
					if (newFollowUp.equals(followUp)) {
						followUpChoosen = newFollowUp;
						break;
					}

				if (followUpChoosen != null)
					break;
			}

			//Ya tenemos el followUp
			Assert.notNull(followUpChoosen);

			//Borramos el follow up
			this.followUpService.delete(followUpChoosen);

			this.followUpService.flush();

			//Comprobamos que se ha borrado
			Assert.isNull(this.followUpService.findOne(followUpId));

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
	 * 2. Delete Follow-Up por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDeleteNegative(final String userName, final String articleName, final String followUpName, final Class<?> expected) {
		Class<?> caught;

		Article article;
		int articleId;
		FollowUp followUp;
		int followUpId;
		User user;
		int userId;
		int countAfter;
		int countBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			countBefore = this.followUpService.findAll().size();

			followUp = null;
			if (followUpName != null) {
				followUpId = super.getEntityId(followUpName);
				followUp = this.followUpService.findOne(followUpId);
			}

			article = null;
			if (articleName != null) {
				articleId = super.getEntityId(articleName);
				article = this.articleService.findOne(articleId);
			}

			user = null;
			if (userName != null) {
				userId = super.getEntityId(userName);
				user = this.userService.findOne(userId);
			}

			followUp.setUser(user);
			followUp.setArticle(article);

			this.followUpService.delete(followUp);

			this.followUpService.flush();

			//Comprobamos que No se crea
			countAfter = this.followUpService.findAll().size();
			Assert.isNull(countAfter == countBefore);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
