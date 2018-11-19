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
import domain.Newspaper;
import domain.User;

import services.ArticleService;
import services.NewspaperService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveArticleTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private UserService				userService;
	
	@Autowired
	private NewspaperService				newspaperService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * Probamos la creación de varios articles por parte de diferentes usuarios.
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
				"user4", "Titulo para mi articulo", date, "Esto es un resumen muy bueno", "Aqui explico mi teoria de forma educada y paciente", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, false, "user4", "newspaper4", null
			}, {
				"user4", "La historia de un coche volador", date, "Esto es un resumen bastante malo", "Vuela coche por Madrid pilotado por la AN", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, false, "user4", "newspaper4", null
			}, {
				"user4", "Pepe el conductor", date, "Esto es un resumen bueno", "Que bien conduce Don Pepe", null, false, false, "user4", "newspaper4", null
			}, {
				"user4", "Soy el mejor", date, "En serio lo soy", "No se porque no soy presi", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, false, "user4", "newspaper4", null
			}, {
				"user4", "Ya poco hay que contar", date, "Ni voy a resumir", "Se acabó lo que se acaba", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, true, "user4", "newspaper4", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Date) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (boolean) testingData[i][6], (boolean) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
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
					null, "Titulo para mi articulo", dateGood, "Esto es un resumen muy bueno", "Aqui explico mi teoria de forma educada y paciente", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, false, "user4", "newspaper4", IllegalArgumentException.class
				}, {
					"user4", "", dateGood, "Esto es un resumen bastante malo", "Vuela coche por Madrid pilotado por la AN", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, false, "user4", "newspaper4", ConstraintViolationException.class
				}, {
					"user4", null, dateGood, "Esto es un resumen bastante malo", "Vuela coche por Madrid pilotado por la AN", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", false, false, "user4", "newspaper4", ConstraintViolationException.class
				},{
					"user4", "Pepe el conductor", dateGood, "", "Que bien conduce Don Pepe", null, false, false, "user4", "newspaper4", ConstraintViolationException.class
				}, {
					"user4", "Pepe el conductor", dateGood, null, "Que bien conduce Don Pepe", null, false, false, "user4", "newspaper4", ConstraintViolationException.class
				}, {
					"user4", "Soy el mejor", dateGood, "En serio lo soy", "", "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, false, "user4", "newspaper4", ConstraintViolationException.class
				}, {
					"user4", "Soy el mejor", dateGood, "En serio lo soy", null, "http://entertainment.ie//images_content/rectangle/620x372/E-T.jpg", true, false, "user4", "newspaper4", ConstraintViolationException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Date) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (boolean) testingData[i][6], (boolean) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
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
	 * 2. Tomamos el id de writer
	 * 3. Tomamos la entidad correspondiente a al id de writer
	 * 4. Creamos una nueva article pasando el user como parámetros
	 * 5. Le asignamos los parametros correspondientes
	 * 6. Guardamos el nuevo articulo
	 * 7. Nos desautentificamos
	 * 8. Comprobamos se ha creado y existe
	 */
	protected void template(final String user, final String title, final Date moment, final String summary, final String body, final String pictures, final boolean isFinalMode, final boolean hasTaboo, final String wrtiter,  final String newspaper, final Class<?> expected) {
		Class<?> caught;
		Integer userId, newspaperId;
		User userEntity;
		Newspaper newspaperEntity;
		Article article, articleEntity;
		Collection<String> pics;

		caught = null;
		try {
			super.authenticate(user);
			Assert.notNull(wrtiter);
			userId = super.getEntityId(wrtiter);
			Assert.notNull(userId);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			Assert.notNull(newspaper);
			newspaperId = super.getEntityId(newspaper);
			Assert.notNull(newspaperId);
			newspaperEntity = this.newspaperService.findOne(newspaperId);
			Assert.notNull(newspaperEntity);
			article = this.articleService.create(userEntity, newspaperEntity);
			article.setTitle(title);
			article.setSummary(summary);
			article.setBody(body);
			pics = new HashSet<String>();
			pics.add(pictures);
			article.setPictures(pics);
			article.setIsFinalMode(isFinalMode);
			article.setHasTaboo(hasTaboo);
			articleEntity = this.articleService.save(article);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.articleService.findAll().contains(articleEntity));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}

