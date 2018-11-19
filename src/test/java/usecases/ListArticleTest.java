
package usecases;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ArticleService;
import utilities.AbstractTest;
import domain.Article;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListArticleTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ArticleService	articleService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAll logueados como user1
	 * 	2. Probamos obtener el resultado previsto para el metodo findAll sin loguear
	 * 3. Probamos obtener el resultado previsto para el metodo findAll logueados como user2
	 * 
	 * Requisitos:
	 * 2. A user may create a newspaper, for which the system must store a title, a publication date
		(year, month, and day), a description, an optional picture, and the list of articles of which it is
		composed.
		3. For each article, the system must store a title, the moment when it is published, a summary,
		a piece of text (the body), and some optional pictures. An article is published when the corresponding
		newspaper is published.
	 */
	@Test()
	public void findAllTest() {
		final Object testingData[][] = {
			{
				"customer1", "findAll", 4, 0, 0, null
			}, {
				null, "findAll", 4, 0, 0, null
			}, {
				"user2", "findAll", 4, 0, 0, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAllTabooPaginated logueados como admin
	 * 	2. Probamos obtener el resultado previsto para el metodo findAllTabooPaginated sin loguear
	 * 3. Probamos obtener el resultado previsto para el metodo findAllTabooPaginated logueados como admin
	 * 
	 * Requisitos:
	 * 2. A user may create a newspaper, for which the system must store a title, a publication date
		(year, month, and day), a description, an optional picture, and the list of articles of which it is
		composed.
		3. For each article, the system must store a title, the moment when it is published, a summary,
		a piece of text (the body), and some optional pictures. An article is published when the corresponding
		newspaper is published.
		17. An actor who is authenticated as an administrator must be able to:
		2. List the articles that contain taboo words.
	 */
	@Test()
	public void findTabooTest() {
		final Object testingData[][] = {
			{
				"admin", "findAllTabooPaginated", 2, 1, 2, null
			}, {
				null, "findAllTabooPaginated", 3, 1, 1, IllegalArgumentException.class
			}, {
				"admin", "findAllTabooPaginated", 1, 1, 1, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAllUserPaginated logueados como user1, para la pagina 1 y el tamano 5
	 * 	2. Probamos  no obtener el resultado previsto para el metodo findAllUserPaginated sin loguear, para la pagina 2 y el tamano 4
	 * 3. Probamos obtener el resultado previsto para el metodo findAllUserPaginated logueados como user2, para la pagina 1 y el tamano 1
	 * 4. Probamos obtener el resultado previsto para el metodo findAllUserPaginated logueados como user2, para la pagina 3 y el tamano 1
	 * 
	 * Requisitos:
	 * 2. A user may create a newspaper, for which the system must store a title, a publication date
		(year, month, and day), a description, an optional picture, and the list of articles of which it is
		composed.
		3. For each article, the system must store a title, the moment when it is published, a summary,
		a piece of text (the body), and some optional pictures. An article is published when the corresponding
		newspaper is published.
	 * 
	 */
	@Test()
	public void findAllUserPaginatedTest() {
		final Object testingData[][] = {
				{
					"user1", "findAllUserPaginated", 1, 1, 5, null
				}, {
					null, "findAllUserPaginated", 0, 2, 4, IllegalArgumentException.class
				}, {
					"user2", "findAllUserPaginated", 1, 1, 1, null
				}, {
					"user3", "findAllUserPaginated", 0, 1, 1, IllegalArgumentException.class
				}

		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
		
	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAllPaginated logueados como user1, para la pagina 1 y el tamano 5
	 * 	2. Probamos  no obtener el resultado previsto para el metodo findAllPaginated sin loguear, para la pagina 2 y el tamano 4
	 * 3. Probamos obtener el resultado previsto para el metodo findAllPaginated logueados como user2, para la pagina 1 y el tamano 1
	 * 4. Probamos obtener el resultado previsto para el metodo findAllPaginated logueados como user2, para la pagina 3 y el tamano 1
	 * 
	 * Requisitos:
	 * 2. A user may create a newspaper, for which the system must store a title, a publication date
		(year, month, and day), a description, an optional picture, and the list of articles of which it is
		composed.
		3. For each article, the system must store a title, the moment when it is published, a summary,
		a piece of text (the body), and some optional pictures. An article is published when the corresponding
		newspaper is published.
		7. An actor who is authenticated as an administrator must be able to:
		1. Remove an article that he or she thinks is inappropriate.
	 * 
	 */
	@Test()
	public void findAllPaginated() {
		final Object testingData[][] = {
				{
					"admin", "findAllPaginated", 2, 1, 2, null
				}, {
					"user2", "findAllPaginated", 0, 1, 5, IllegalArgumentException.class
				}, {
					"admin", "findAllPaginated", 3, 1, 3, null
				}

		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
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
	 * 2. Comprobamos si el método es findAll ó findByUserAccountId
	 * 3. En el caso de que sea findByUserAccountId, obtenemos las entidades correspondientes al user para usar el método
	 * 3. Según el método que sea, se llama a su método y se guarda en la variable sizeSubscription el tamaño de los resultados de cada método
	 * 4. Comprobamos que devuelve el valor esperado
	 * 5. Nos desautentificamos
	 */
	protected void template(final String user, final String method, final Integer tamano, final int page, final int size, final Class<?> expected) {
		Class<?> caught;
		Collection<Article> articlesCollection;
		List<Article> articlesList;
		int sizeArticle;
		int userId;

		sizeArticle = 0;
		caught = null;
		try {
			super.authenticate(user);

			if (method.equals("findAll")) {
				articlesCollection = this.articleService.findAll();
				sizeArticle = articlesCollection.size();
			} else if (method.equals("findAllUserPaginated")) {
				Assert.notNull(user);
				userId = super.getEntityId(user);
				Assert.notNull(userId);
				articlesList = this.articleService.findAllUserPaginated(userId, page, size).getContent();
				sizeArticle = articlesList.size();
			} 	else if (method.equals("findAllTabooPaginated")) {
				articlesCollection = this.articleService.findAllTabooPaginated(page, size).getContent();
				sizeArticle = articlesCollection.size();
			} else {
				articlesCollection = this.articleService.findAllPaginated(page, size).getContent();
				sizeArticle = articlesCollection.size();
			}
			Assert.isTrue(sizeArticle == tamano); 
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
