package usecases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.validation.DataBinder;

import domain.Comment;
import domain.Bargain;
import domain.User;

import services.CommentService;
import services.BargainService;
import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveCommentTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CommentService		commentService;
	
	@Autowired
	private BargainService		bargainService;
	
	@Autowired
	private UserService		userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Creamos un comment con todos los parámetros correctos.
	 * 		2. Creamos un comment con varias imágenes.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverPositiveTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain1", "01/02/2018 12:30", "Test", "http://example.com/image.jpg", null
			}, {
				"user1", "bargain1", "01/02/2018 12:30", "Test", "https://i.blogs.es/1a9d2d/aves-colibri/450_1000.jpg, https://t1.ea.ltmcdn.com/es/images/2/2/8/img_como_atraer_pajaros_al_jardin_20822_600.jpg", null
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
	}
	
	/*
	 * Pruebas:
	 * 		1. Creamos una respuesta con todos los parámetros correctos.
	 * 		2. Creamos una respuesta con varias imágenes.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverReplyCommentPositiveTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain2", "01/02/2018 12:30", "Test", "http://example.com/image.jpg", "comment1", null
			}, {
				"user1", "bargain2", "01/02/2018 12:30", "Test", "https://i.blogs.es/1a9d2d/aves-colibri/450_1000.jpg, https://t1.ea.ltmcdn.com/es/images/2/2/8/img_como_atraer_pajaros_al_jardin_20822_600.jpg", "comment1", null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateReplyComment((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Tratamos de crear un comentario pero le ponemos de usuario uno distinto al que está autenticado.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverUrlNegativeTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain1", "user3", "01/02/2018 12:30", "Test", "http://example.com/image.jpg", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateUrl((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario trata de crear un comentario dejando el campo moment vacío.
	 * 		2. Un usuario trata de crear un comentario eliminando el campo moment.
	 * 		3. Un usuario trata de crear un comentario introduciendo en el campo image un valor que no corresponde con el pattern URL.
	 * 		4. Un usuario trata de crear un comentario con bargain a null.
	 * 		5. Un usuario trata de crear un comentario eliminando el campo text.
	 * 		6. Un usuario trata de crear un comentario para un bargain sin introducir nada en el campo text.
	 * 		7. Una compañía trata de crear un comentario pero no lo tienen permitido.
	 * 		8. Un administrador trata de crear un comentario pero no lo tiene permitido.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain1", "", "Test", "", IllegalArgumentException.class
			}, 	{
				"user1", "bargain1", null, "Test", "", IllegalArgumentException.class
			}, {
				"user1", "bargain1", "01/01/2018 00:00", "Test", "asdf", ConstraintViolationException.class
			}, 	{
				"user6", null, "01/01/2018 00:00", "Test", "", IllegalArgumentException.class
			}, 	{
				"user1", "bargain1", "01/01/2018 00:00", null, "", ConstraintViolationException.class
			}, {
				"user1", "bargain1", "01/01/2018 00:00", "", "http://example.com/image.jpg", ConstraintViolationException.class
			}, {
				"company1", "bargain1", "01/01/2018 00:00", "Test", "http://example.com/image.jpg", IllegalArgumentException.class
			}, {
				"administrator", "bargain1", "01/01/2018 00:00", "Test", "http://example.com/image.jpg", IllegalArgumentException.class
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
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario trata de crear una respuesta con el campo moment vacío.
	 * 		2. Un usuario trata de crear una respuesta eliminando el campo moment.
	 * 		3. Un usuario trata de crea una respuesta introduciendo en el campo image un valor que no corresponde con el pattern URL.
	 * 		4. Un usuario trata de crea una respuesta en un bargain a null.
	 * 		5. Un usuario trata de crear una respuesta eliminando el campo title.
	 * 		6. Un usuario trata de crear una respuesta para un bargain sin introducir nada en el campo text.
	 * 		7. Una compañía trata de crear una respuesta pero no lo tienen permitido.
	 * 		8. Un administrador trata de crear una respuesta pero no lo tiene permitido.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverReplyCommentNegativeTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain2", "", "Test", "", "comment1", IllegalArgumentException.class
			}, {
				"user1", "bargain2", null, "Test", "", "comment1", IllegalArgumentException.class
			}, {
				"user1", "bargain2", "01/01/2018 00:00", "Test", "asdf", "comment1", ConstraintViolationException.class
			}, {
				"user6", null, "01/01/2018 00:00", "Test", "", "comment2", IllegalArgumentException.class
			}, {
				"user1", "bargain2", "01/01/2018 00:00", null, "", "comment1", ConstraintViolationException.class
			}, {
				"user1", "bargain2", "01/01/2018 00:00", "", "http://example.com/image.jpg", "comment1", ConstraintViolationException.class
			}, {
				"company1", "bargain1", "01/01/2018 00:00", "Test", "http://example.com/image.jpg", "comment1", IllegalArgumentException.class
			}, {
				"administrator", "bargain1", "01/01/2018 00:00", "Test", "http://example.com/image.jpg", "comment1", IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateReplyComment((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 		1. Tratamos de crear una respuesta a un comentario pero le ponemos de usuario uno distinto al que está autenticado.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *	 */
	@Test
	public void driverUrlReplyCommentNegativeTest() {
		final Object testingData[][] = {
			{
				"company2", "bargain1", "user3", "01/02/2018 12:30", "Test", "http://example.com/image.jpg", "comment3", IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateUrlReplyComment((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (Class<?>) testingData[i][7]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	
	// Ancillary methods ------------------------------------------------------

	/*
	 * Crear un comment.
	 * Pasos:
	 * 1. Autenticar usuario
	 * 2. Listar los bargain
	 * 3. Escoger un bargain
	 * 4. Crear un comment asociado a un bargain
	 * 5. Guardar el nuevo comment
	 * 6. Dirigir al display de bargain
	 */
	protected void template(final String user, final String bargainBean, final String moment, final String text, final String image, final Class<?> expected) {
		Class<?> caught;
		Comment savedComment, newComment;
		Bargain bargain;
		Collection<Bargain> bargaines;
		int bargainId;
		DateFormat formatter;
		DataBinder binder;
		Collection<String> images;

		images = new HashSet<String>();
		formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

		caught = null;
		try {
			Assert.notNull(bargainBean);
			bargainId = super.getEntityId(bargainBean);
			bargain = this.bargainService.findOne(bargainId);
			
			// 1. Autenticar usuario
			super.authenticate(user);

			// 2. Listar los bargain
			bargaines = this.bargainService.findBargains(1, 99, "all", 0).getContent();
			
			// 3. Escoger un bargain
			for(Bargain r: bargaines)
				if(r.getId() == bargainId) bargain = r;
			
			// 4. Crear un comment asociado a un bargain
			newComment = this.commentService.create(bargain, null);
			if(moment == null || moment.equals("")){ 
				Assert.notNull(moment);
				Assert.isTrue(!moment.equals("")); 
			} else newComment.setMoment(formatter.parse(moment));
			newComment.setText(text);
			if(image != null) images.add(image);
			if(image != null) newComment.setImages(images);
			
			// 5. Guardar el nuevo comment
			binder = new DataBinder(newComment);
			newComment = this.commentService.reconstruct(newComment, binder.getBindingResult());
			savedComment = this.commentService.save(newComment);
			this.commentService.flush();
			
			// 6. Dirigir al display de bargain
			Assert.isTrue(this.commentService.findByBargainId(bargain.getId(), 1, this.commentService.countByBargainId(bargain.getId())).getContent().contains(savedComment));

			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	/*
	 * Crear un comment a través de URL con Postman.
	 * Pasos:
	 * 1. Autenticar usuario
	 * 2. Enviar datos de comment por URL
	 */
	protected void templateUrl(final String user, final String bargainBean, final String userBean, final String moment, final String text, final String image, final Class<?> expected) {
		Class<?> caught;
		Comment savedComment, newComment;
		Bargain bargain;
		int bargainId, userId;
		DateFormat formatter;
		User userComment;
		Collection<String> images;

		images = new HashSet<String>();

		formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

		caught = null;
		try {
			
			bargainId = super.getEntityId(bargainBean);
			bargain = this.bargainService.findOne(bargainId);
			
			// 1. Autenticar usuario
			super.authenticate(user);
			
			// 2. Enviar datos de comment por URL
			newComment = this.commentService.create(bargain, null);
			if(moment == null || moment.equals("")){ 
				Assert.notNull(moment);
				Assert.isTrue(!moment.equals("")); 
			} else newComment.setMoment(formatter.parse(moment));
			newComment.setText(text);
			if(image != null) images.add(image);
			if(image != null) newComment.setImages(images);
			if(userBean != null) {
				userId = super.getEntityId(userBean);
				userComment = this.userService.findOne(userId);
				newComment.setUser(userComment);
			}
			
			savedComment = this.commentService.save(newComment);
			this.commentService.flush();

			// Comprobación
			Assert.isTrue(this.commentService.findByBargainId(bargain.getId(), 1, this.commentService.countByBargainId(bargain.getId())).getContent().contains(savedComment));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

	/*
	 * Crear una respuesta a un comment. Pasos:
	 * 1. Autenticar como usuario.
	 * 2. Listar los bargaines
	 * 3. Escoger un bargain
	 * 4. Escoger un comentario
	 * 5. Crear un respuesta a un comment
	 * 6. Salvar el nuevo comment
	 * 7. Dirigir al display del comentario padre
	 */
	protected void templateReplyComment(final String user, final String bargainBean, final String moment, final String text, final String image, final String repliedCommentBean, final Class<?> expected) {
		Class<?> caught;
		Comment savedComment, newComment, repliedComment;
		Bargain bargain;
		Collection<Bargain> bargaines;
		Collection<Comment> comments;
		int bargainId, repliedCommentId;
		DateFormat formatter;
		DataBinder binder;
		Collection<String> images;

		images = new HashSet<String>();
		formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

		caught = null;
		try {
			
			Assert.notNull(bargainBean);
			bargainId = super.getEntityId(bargainBean);
			bargain = this.bargainService.findOne(bargainId);
			
			repliedCommentId = super.getEntityId(repliedCommentBean);
			repliedComment = this.commentService.findOne(repliedCommentId);
			
			// 1. Autenticar como usuario
			super.authenticate(user);
						
			// 2. Listar los bargaines
			bargaines = this.bargainService.findBargains(1, 99, "all", 0).getContent();
			
			// 3. Escoger un bargain
			for(Bargain r: bargaines)
				if(r.getId() == bargainId) bargain = r;
			
			// 4. Escoger un comentario
			comments = this.commentService.findByBargainId(bargain.getId(), 1, this.commentService.countByBargainId(bargain.getId())).getContent();
			for(Comment c: comments)
				if(c.getId() == repliedCommentId) repliedComment = c;
			
			// 5. Crear un respuesta a un comment
			newComment = this.commentService.create(bargain, repliedComment);
			if(moment == null || moment.equals("")){ 
				Assert.notNull(moment);
				Assert.isTrue(!moment.equals("")); 
			} else newComment.setMoment(formatter.parse(moment));
			newComment.setText(text);
			if(image != null) images.add(image);
			if(image != null) newComment.setImages(images);
			
			// 6. Salvar el nuevo comment
			binder = new DataBinder(newComment);
			newComment = this.commentService.reconstruct(newComment, binder.getBindingResult());
			savedComment = this.commentService.save(newComment);
			this.commentService.flush();
			
			// 7. Dirigir al display del comentario padre
			Assert.notNull(this.commentService.findOne(savedComment.getRepliedComment().getId()));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	/*
	 * Crear una respuesta a un comment a través de la URL con Postman. Pasos:
	 * 1. Autenticar usuario
	 * 2. Enviar datos de comment por URL
	 */
	protected void templateUrlReplyComment(final String user, final String bargainBean, final String userBean, final String moment, final String text, final String image, final String repliedCommentBean, final Class<?> expected) {
		Class<?> caught;
		Comment savedComment, newComment, repliedComment;
		Bargain bargain;
		int bargainId, repliedCommentId, userId;
		DateFormat formatter;
		User userComment;
		DataBinder binder;
		Collection<String> images;

		images = new HashSet<String>();
		formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

		caught = null;
		try {			
			Assert.notNull(bargainBean);
			bargainId = super.getEntityId(bargainBean);
			bargain = this.bargainService.findOne(bargainId);
			
			repliedCommentId = super.getEntityId(repliedCommentBean);
			repliedComment = this.commentService.findOne(repliedCommentId);
			
			// 1. Autenticar usuario
			super.authenticate(user);

			// 2. Enviar datos de comment por URL
			newComment = this.commentService.create(bargain, repliedComment);
			if(moment == null || moment.equals("")){ 
				Assert.notNull(moment);
				Assert.isTrue(!moment.equals("")); 
			} else newComment.setMoment(formatter.parse(moment));
			newComment.setText(text);
			if(image != null) images.add(image);
			if(image != null) newComment.setImages(images);
			if(userBean != null) {
				userId = super.getEntityId(userBean);
				userComment = this.userService.findOne(userId);
				newComment.setUser(userComment);
			}

			binder = new DataBinder(newComment);
			newComment = this.commentService.reconstruct(newComment, binder.getBindingResult());
			savedComment = this.commentService.save(newComment);
			this.commentService.flush();
			
			// Comprobación
			Assert.notNull(this.commentService.findOne(savedComment.getRepliedComment().getId()));
			
			super.unauthenticate();
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
}

