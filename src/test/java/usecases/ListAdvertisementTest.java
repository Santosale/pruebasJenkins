
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AdvertisementService;
import services.ArticleService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Article;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListAdvertisementTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private ArticleService			articleService;

	@Autowired
	private NewspaperService		newspaperService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Listar advertisements y escoger uno
	 * 2. Listar advertisements y escoger uno que no es de ese usuario. IllegalArgumentException
	 */

	@Test
	public void driverListMyAdvertisements() {

		//Rol, advertisement, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "advertisement5", null
			}, {
				"agent2", "advertisement3", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListMyAdvertisements((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//Pruebas
	/*
	 * 1. Display advertisement de periodico publicado.
	 * 2. Display advertisement de periodico no privado siendo usuario. IllegalArgumentException
	 * 3. Display advertisement de periodico privado con customer no suscrito. IllegalArgumentException
	 * 4. Display advertisement de periodico publicado sin anuncios.
	 */
	@Test
	public void driverDisplayByArticle() {

		//Rol,article, ExpectedException
		final Object testingData[][] = {
			{
				"user1", "article1", true, null
			}, {
				"user1", "article2", false, IllegalArgumentException.class
			}, {
				"customer2", "article2", false, IllegalArgumentException.class
			}, {
				"customer2", "article3", false, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDisplayByArticle((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	//Pruebas
	/*
	 * 1. List advertisements con admin autenticado.
	 * 2. List advertisements sin usuario autenticado. IllegalArgumentException
	 * 3. List advertisements con customer autenticado. IllegalArgumentException
	 */
	//Requisitos:
	//CU 5.1: List the advertisements that contain taboo words in its title.

	@Test
	public void driverListPerAdmin() {

		//Rol,article, ExpectedException
		final Object testingData[][] = {
			{
				"admin", 1, null
			}, {
				null, 1, IllegalArgumentException.class
			}, {
				"customer2", 1, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListPerAdmin((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los advertisements de un agent.
	 * 3. Navegar y escoger el advertisement deseado.
	 */
	protected void templateListMyAdvertisements(final String userName, final String advertisementName, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement advertisement;
		Advertisement advertisementChoosen;
		int countAdvertisement;
		int advertisementId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			advertisementId = super.getEntityId(advertisementName);

			advertisement = this.advertisementService.findOne(advertisementId);

			//Inicializamos
			advertisementChoosen = null;

			//Obtenemos la colección de los advertisements
			advertisements = this.advertisementService.findByAgentId(1, 1);
			countAdvertisement = advertisements.getTotalPages();

			//Buscamos el advertisement
			for (int i = 0; i < countAdvertisement; i++) {

				advertisements = this.advertisementService.findByAgentId(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (advertisements.getContent().size() == 0)
					break;

				//3. Navegar hasta el advertisement que queremos.
				for (final Advertisement newAdvertisement : advertisements.getContent())
					if (newAdvertisement.equals(advertisement)) {
						advertisementChoosen = newAdvertisement;
						break;
					}

				if (advertisementChoosen != null)
					break;
			}

			//Ya tenemos el advertisement
			Assert.notNull(advertisementChoosen);

			this.advertisementService.findOneToDisplay(advertisementId);

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
	 * 3. Navegar y escoger el newspaper deseado.
	 * 4. Navegar y escoger un artículo .
	 * 5. Comprobar los advertisements.
	 */
	protected void templateDisplayByArticle(final String userName, final String articleName, final Boolean hasAdvertisement, final Class<?> expected) {
		Class<?> caught;

		Page<Newspaper> newspapers;
		Page<Article> articles;
		Newspaper newspaper;
		Newspaper newspaperChoosen;
		Article article;
		Article articleChoosen;
		int countNewspaper;
		int countArticle;
		int articleId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			articleId = super.getEntityId(articleName);

			article = this.articleService.findOne(articleId);
			newspaper = article.getNewspaper();

			//Inicializamos
			newspaperChoosen = null;
			articleChoosen = null;

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

			//Vemos algún anuncio
			if (hasAdvertisement)
				Assert.notNull(this.advertisementService.findRandomAdvertisement(newspaper.getId()));
			else
				Assert.isNull(this.advertisementService.findRandomAdvertisement(newspaper.getId()));

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
	 * 2. Listar los advertisement.
	 * 3. Ver los taboo
	 */
	protected void templateListPerAdmin(final String userName, final Integer numberOfTabooExpected, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement advertisementChoosen;
		int countAdvertisement;
		int numberOfTaboo;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			advertisementChoosen = null;
			numberOfTaboo = 0;

			//Obtenemos la colección de los advertisements
			advertisements = this.advertisementService.findAllPaginated(1, 1);
			countAdvertisement = advertisements.getTotalPages();

			//Buscamos el advertisement
			for (int i = 0; i < countAdvertisement; i++) {

				advertisements = this.advertisementService.findAllPaginated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (advertisements.getContent().size() == 0)
					break;

				//3. Navegar hasta el advertisement que queremos.
				for (final Advertisement newAdvertisement : advertisements.getContent()) {
					if (newAdvertisement.getHasTaboo())
						numberOfTaboo++;

					if (i == 0)
						advertisementChoosen = newAdvertisement;
				}

			}

			//Ya tenemos el advertisement
			Assert.notNull(advertisementChoosen);

			//Accedemos al advertisement
			this.advertisementService.findOneToDisplay(advertisementChoosen.getId());

			//Comprobamos el número de taboos
			Assert.isTrue(numberOfTaboo == numberOfTabooExpected);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
