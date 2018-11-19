package usecases;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Article;
import domain.User;

import services.ArticleService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditArticleTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private UserService				userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * Solo se puede editar el article4 ya que es el unico en modo final
	 * 
	 * Requisitos:
	 * 2. A user may create a newspaper, for which the system must store a title, a publication date
		(year, month, and day), a description, an optional picture, and the list of articles of which it is
		composed.
		3. For each article, the system must store a title, the moment when it is published, a summary,
		a piece of text (the body), and some optional pictures. An article is published when the corresponding
		newspaper is published.
	 */
	@Test
	public void positiveSaveArticleTest() {
		Calendar calendar;
		Date date;
		
		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		date = calendar.getTime();
		
		final Object testingData[][] = {
		 {
				"user4", "article4", "Soy el mejor", date, "En serio lo soy", "No se porque no soy presi", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Date) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (boolean) testingData[i][7], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Solo puede crearlo un user
	 * 2. El titulo no puede ser vacio
	 * 3. El titulo no puede ser nulo
	 * 4. El resumen no puede ser vacio
	 * 5. El resumen no puede ser nulo
	 * 6. El cuerpo no puede ser vacio
	 * 7. El cuerpo no puede ser nulo
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
	public void negativeSaveArticleTest() {
		Calendar calendar;
		Date dateGood;
	
		calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		dateGood = calendar.getTime();
		
		final Object testingData[][] = {
				{
					null, "article4", "Titulo para mi articulo", dateGood, "Esto es un resumen muy bueno", "Aqui explico mi teoria de forma educada y paciente", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, IllegalArgumentException.class
				}, {
					"user4", "article4", "", dateGood, "Esto es un resumen bastante malo", "Vuela coche por Madrid pilotado por la AN", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, ConstraintViolationException.class
				}, {
					"user4", "article4", null, dateGood, "Esto es un resumen bastante malo", "Vuela coche por Madrid pilotado por la AN", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, ConstraintViolationException.class
				},{
					"user4", "article4", "Pepe el conductor", dateGood, "", "Que bien conduce Don Pepe", "", false, ConstraintViolationException.class
				}, {
					"user4", "article4", "Pepe el conductor", dateGood, null, "Que bien conduce Don Pepe", "", false, ConstraintViolationException.class
				}, {
					"user4", "article4", "Soy el mejor", dateGood, "En serio lo soy", "", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, ConstraintViolationException.class
				}, {
					"user4", "article4", "Soy el mejor", dateGood, "En serio lo soy", null, "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, ConstraintViolationException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Date) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (boolean) testingData[i][7], (Class<?>) testingData[i][8]);
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
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer
	 customer Accedemos a la lista de subscriptions y tomamos la que nos interesa
	 * 4. Le creamos una copia para que no se guarde solo con un set
	 * 5. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 6. Guardamos la subscription copiada con los parámetros
	 * 7. Nos desautentificamos
	 */
	protected void template(final String user, final String articleEdit, final String title, final Date moment, final String summary, final String body, final String pictures,  final boolean isFinalMode, final Class<?> expected) {
		Class<?> caught;
		Integer userId, articleId;
		User userEntity;
		Collection<Article> articles;
		Article article, articleEntity;
		Collection<String> pics;

		article = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			articleId = super.getEntityId(articleEdit);
			Assert.notNull(articleId);
			articles = this.articleService.findByWritterId(userId, 1, 10).getContent();
			for (Article a : articles) {
				if(a.getId() == articleId){
					article = a;
					break;
				}
			}
			Assert.notNull(article);
			articleEntity = this.copyArticle(article);
			articleEntity.setTitle(title);
			articleEntity.setMoment(moment);
			articleEntity.setSummary(summary);
			articleEntity.setBody(body);
			pics = new HashSet<String>();
			pics.add(pictures);
			articleEntity.setPictures(pics);
			articleEntity.setIsFinalMode(isFinalMode);
			
			this.articleService.save(articleEntity);
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
	 * 1. Nos autentificamos como customer
	 * 2. Tomamos el id y la entidad de customer y subscription
	 * 3. Le creamos una copia para que no se guarde solo con un set
	 * 4. Le asignamos el holderName, el brandName, el number, la expirationMonth y el cvvCode correspondientes
	 * 5. Guardamos la subscription copiada con los parámetros
	 * 6. Nos desautentificamos
	 */
	protected void templateNoList(final String user, final String articleEdit, final String title, final Date moment, final String summary, final String body, final String pictures,  final boolean isFinalMode, final Class<?> expected) {
		Class<?> caught;
		Integer userId, articleId;
		User userEntity;
		Article article, articleEntity;
		Collection<String> pics;

		article = null;
		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(user);
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			articleId = super.getEntityId(articleEdit);
			Assert.notNull(articleId);
			article = this.articleService.findOneToEdit(articleId);
			Assert.notNull(article);
			articleEntity = this.copyArticle(article);
			articleEntity.setTitle(title);
			articleEntity.setMoment(moment);
			articleEntity.setSummary(summary);
			articleEntity.setBody(body);
			pics = new HashSet<String>();
			pics.add(pictures);
			articleEntity.setPictures(pics);
			articleEntity.setIsFinalMode(isFinalMode);
			this.articleService.save(articleEntity);
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private Article copyArticle(final Article article) {
		Article result;
	
		result = new Article();
		result.setId(article.getId());
		result.setVersion(article.getVersion());
		result.setTitle(article.getTitle());
		result.setMoment(article.getMoment());
		result.setBody(article.getBody());
		result.setPictures(article.getPictures());
		result.setIsFinalMode(article.getIsFinalMode());
		result.setHasTaboo(article.getHasTaboo());
		result.setNewspaper(article.getNewspaper());
		result.setWriter(article.getWriter());
		
		return result;
	}

}

