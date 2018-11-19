
package usecases;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.ArticleService;
import services.FollowUpService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Article;
import domain.FollowUp;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CreateFollowUpTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private FollowUpService		followUpService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private NewspaperService	newspaperService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Crear Follow-up con datos correctos y dos imágenes.
	 * 2. Crear Follow-up con text null. ConstraintViolationException
	 * 3. Crear Follow-up con title null. ConstraintViolationException
	 * 4. Crear Follow-up sin imagenes.
	 */

	@Test
	public void driverDataConstraint() {

		//Rol,article,title,text,summary,pictures, ExpectedException
		final Object testingData[][] = {
			{
				"user1", "article1", "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", null
			}, {
				"user1", "article1", "Nueva noticia", null, "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", ConstraintViolationException.class
			}, {
				"user1", "article1", null, "En esta....", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", ConstraintViolationException.class
			}, {
				"user1", "article1", "Nueva noticia", "En esta....", "Novedad importante", "", null
			}, {
				"user1", "article1", "Nueva noticia", "En esta....", null, "", ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	//Pruebas
	/*
	 * 1. Crear Follow-up sin autenticarse.IllegalArgumentException
	 * 2. Crear Follow-up autenticandose como otro usuario al creador. IllegalArgumentException
	 * 3. Crear Follow-up con otro rol.IllegalArgumentException
	 * 4. Crear Follow-up sin artículo. IllegalArgumentException
	 * 5. Crear Follow-up a artículo no guardado en finalMode y newspaper no publicado. IllegalArgumentException
	 */

	@Test
	public void driverStatementsConstraints() {

		//Rol,article,title,text,summary,pictures, ExpectedException
		final Object testingData[][] = {
			{
				null, "article1", "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", IllegalArgumentException.class
			}, {
				"user2", "article1", "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", IllegalArgumentException.class
			}, {
				"customer1", "article1", "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", IllegalArgumentException.class
			}, {
				"user1", null, "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", IllegalArgumentException.class
			}, {
				"user4", "article4", "Nueva noticia", "En esta noticia...", "Novedad importante", "https://miImagen.com,http://www.otraweb.com/image.png", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreateStatements((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los newspaper.
	 * 3. Navegary escoger el newspaper deseado.
	 * 4. Navegar y escoger un artículo .
	 * 5. Crear Follow-Up .
	 */
	protected void templateCreate(final String userName, final String articleName, final String title, final String text, final String summary, final String picturesString, final Class<?> expected) {
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
		Collection<String> pictures;
		FollowUp followUp;
		FollowUp saved;
		DataBinder binder;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Metemos las imágenes
			pictures = new ArrayList<String>();

			for (final String picture : picturesString.split(","))
				pictures.add(picture);

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

			//Creamos el follow up
			followUp = this.followUpService.create(articleChoosen);

			followUp.setTitle(title);
			followUp.setPictures(pictures);
			followUp.setSummary(summary);
			followUp.setText(text);

			binder = new DataBinder(followUp);

			saved = this.followUpService.reconstruct(followUp, binder.getBindingResult());

			saved = this.followUpService.save(saved);

			this.followUpService.flush();

			//Comprobamos que se crea
			Assert.notNull(this.followUpService.findOne(saved.getId()));

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
	 * 2. Crear Follow-Up por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateCreateStatements(final String userName, final String articleName, final String title, final String text, final String summary, final String picturesString, final Class<?> expected) {
		Class<?> caught;

		Article article;
		int articleId;
		Collection<String> pictures;
		FollowUp followUp;
		FollowUp saved;
		DataBinder binder;
		int countAfter;
		int countBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Metemos las imágenes
			pictures = new ArrayList<String>();

			for (final String picture : picturesString.split(","))
				pictures.add(picture);

			//Obtenemos los objetos que queremos
			countBefore = this.followUpService.findAll().size();

			article = null;
			if (articleName != null) {
				articleId = super.getEntityId(articleName);
				article = this.articleService.findOne(articleId);
			}

			//Creamos el follow up
			followUp = this.followUpService.create(article);

			followUp.setTitle(title);
			followUp.setPictures(pictures);
			followUp.setSummary(summary);
			followUp.setText(text);

			binder = new DataBinder(followUp);

			saved = this.followUpService.reconstruct(followUp, binder.getBindingResult());

			saved = this.followUpService.save(saved);

			this.followUpService.flush();

			//Comprobamos que No se crea
			countAfter = this.followUpService.findAll().size();
			Assert.isTrue(countAfter == countBefore);
			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
