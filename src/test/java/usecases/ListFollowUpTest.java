
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
import services.NewspaperService;
import services.UserService;
import utilities.AbstractTest;
import domain.Article;
import domain.FollowUp;
import domain.Newspaper;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListFollowUpTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private FollowUpService		followUpService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private UserService			userService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Listar follow-up y escoger uno
	 * 2. Listar follow-up y escoger uno que no es de ese usuario. IllegalArgumentException
	 */

	@Test
	public void driverListMyFollowUps() {

		//Rol, followUp, ExpectedException
		final Object testingData[][] = {
			{
				"user2", "followUp2", null
			}, {
				"user1", "followUp3", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListMyFollowUps((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//Pruebas
	/*
	 * 1. Listar Follow-up de periodico publicado.
	 * 2. Listar follow-up de periodico no privado siendo usuario. IllegalArgumentException
	 * 3. Listar follow-up de periodico privado con customer no suscrito. IllegalArgumentException
	 */
	@Test
	public void driverListByNewspaper() {

		//Rol,article, followUp, ExpectedException
		final Object testingData[][] = {
			{
				"user1", "article1", "followUp1", null
			}, {
				"user1", "article2", "followUp2", IllegalArgumentException.class
			}, {
				"customer2", "article2", "followUp2", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListByNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los followUp de un usuario.
	 * 3. Navegar y escoger el followUp deseado.
	 */
	protected void templateListMyFollowUps(final String userName, final String followUpName, final Class<?> expected) {
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
	 * 2. Listar los newspaper.
	 * 3. Navegary escoger el newspaper deseado.
	 * 4. Navegar y escoger un artículo .
	 * 5. Navegar y escoger Follow-Up .
	 * 6. Borrar Follow-Up .
	 */
	protected void templateListByNewspaper(final String userName, final String articleName, final String followUpName, final Class<?> expected) {
		Class<?> caught;

		Page<Newspaper> newspapers;
		Page<Article> articles;
		Page<FollowUp> followUps;
		Newspaper newspaper;
		Newspaper newspaperChoosen;
		Article article;
		Article articleChoosen;
		int countNewspaper;
		int countArticle;
		int countFollowUp;
		int followUpId;
		int articleId;
		FollowUp followUp;
		FollowUp followUpChoosen;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			articleId = super.getEntityId(articleName);
			followUpId = super.getEntityId(followUpName);

			article = this.articleService.findOne(articleId);
			followUp = this.followUpService.findOne(followUpId);
			newspaper = article.getNewspaper();

			//Inicializamos
			newspaperChoosen = null;
			articleChoosen = null;
			followUpChoosen = null;

			//Obtenemos la colección de los newspaper
			newspapers = this.newspaperService.findPublicsAndPublicated(1, 1);
			countNewspaper = newspapers.getTotalPages();

			//Buscamos el newspaper
			for (int i = 0; i < countNewspaper; i++) {

				newspapers = this.newspaperService.findPublicsAndPublicated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (newspapers.getContent().size() == 0)
					break;

				//3. Navegar hasta el newspaper que queremos que queremos.
				for (final Newspaper newNewspaper : newspapers.getContent())
					if (newNewspaper.equals(newspaper)) {
						newspaperChoosen = newNewspaper;
						break;
					}

				if (newspaperChoosen != null)
					break;
			}

			//Ya tenemos el newspaper
			Assert.notNull(newspaperChoosen);

			//Obtenemos la colección de los articles
			articles = this.articleService.findByNewspaperIdPaginated(newspaperChoosen.getId(), 1, 1);
			countArticle = articles.getTotalPages();

			//Buscamos ahora el artículo
			for (int i = 0; i < countArticle; i++) {

				articles = this.articleService.findByNewspaperIdPaginated(newspaperChoosen.getId(), 1, 5);

				//Si estamos pidiendo una página mayor
				if (articles.getContent().size() == 0)
					break;

				//3. Navegar hasta el newspaper que queremos que queremos.
				for (final Article newArticle : articles.getContent())
					if (newArticle.equals(article)) {
						articleChoosen = newArticle;
						break;
					}

				if (articleChoosen != null)
					break;
			}

			//Ya tenemos el artículo
			Assert.notNull(articleChoosen);

			//Obtenemos la colección de los follow-ups
			followUps = this.followUpService.findByArticleIdPaginated(articleChoosen.getId(), 1, 1);
			countFollowUp = followUps.getTotalPages();

			//Buscamos el followUp
			for (int i = 0; i < countFollowUp; i++) {

				followUps = this.followUpService.findByArticleIdPaginated(articleChoosen.getId(), i + 1, 5);

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

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
