
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.BargainService;
import services.CommentService;
import services.UserService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Comment;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListCommentTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CommentService		commentService;
	
	@Autowired
	private BargainService		bargainService;
	
	@Autowired
	private UserService			userService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Un usuario autenticado como USER entra en la vista de detalles de comment.
	 * 		2. Un usuario sin autenticar entra en la vista de detalles de comment.
	 * 		3. Un usuario autenticado como USER entra en la vista de detalles de la respuesta a un comment.
	 * 		4. Un usuario autenticado como SPONSOR entra en la vista de detalles de la respuesta a un comment.
	 * 		5. Un usuario autenticado como COMPANY entra en la vista de detalles de la respuesta a un comment.
	 * 	
	 * 		Caso de uso en el que se comprueba los casos en los que el usuario trata de mostrar
	 * 		tanto los comentarios de un bargain como sus respuestas, navegando a través de la web.
	 */
	@Test
	public void driverPositiveTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain2", "comment1", 1, null, null, null
			}, {
				null, "bargain2", "comment1", 1, null, null, null
			}, {
				"user1", "bargain1", "comment2", 4, "comment3", 1, null
			}, {
				"sponsor1", "bargain1", "comment2", 4, "comment3", 1, null
			}, {
				"company1", "bargain1", "comment2", 4, "comment3", 1, null
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario accede a sus comentarios esperando que devuelvan 7.
	 * 		2. Un usuario accede a sus comentarios esperando que devuelvan 0.
	 * 		3. Un usuario accede a sus comentarios esperando que devuelvan 3.
	 * 		4. Un sponsor no puede acceder a los comentarios.
	 * 	
	 * 		Caso de uso en el que se comprueba el caso de uso de que un user acceda a sus comentarios.
	 */
	@Test
	public void driverMyCommentsTest() {
		final Object testingData[][] = {
			{
				"user1", 5, null
			}, {
				"user2", 1, null
			}, {
				"user3", 4, null
			}, {
				"sponsor1", 0, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateMyComments((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario autenticado como USER trata de mostrar un comentario entrando desde un bargain distinto.
	 * 		2. Un usuario autenticado como USER trata de mostrar una respuesta que no existe como tal, pues es un comentario.
	 * 		3. Un usuario autenticado como COMPANY trata de mostrar una respuesta que no existe como tal, pues es un comentario.
	 * 		4. Un usuario autenticado como ADMIN trata de mostrar una respuesta que no existe como tal, pues es un comentario.
	 * 
	 * 		Caso de uso en el que se comprueban las distintas excepciones que pueden saltar si se trata
	 * 		de navegar de una forma incorrecta a la hora de mostrar los comentarios y sus respuestas.
	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
			{
				"user1", "bargain1", "comment5", 1, null, null, IllegalArgumentException.class
			}, {
				"user1", "bargain1", "comment2", 1, "comment1", null, IllegalArgumentException.class
			}, {
				"company1", "bargain1", "comment2", 1, "comment1", null, IllegalArgumentException.class
			}, {
				"administrator", "bargain1", "comment2", 1, "comment1", null, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario sin autenticar trata de mostrar un comentario de un bargain no permitido
	 * 		2. Un usuario autenticado como USER trata de mostrar un comentario de un bargain no permitido
	 * 		3. Un usuario autenticado como SPONSOR trata de mostrar un comentario de un bargain no permitido
	 * 
	 *		Caso de uso en el que se simula como el usuario podría tratar de acceder a una sin usar la navegación
	 * 		sino utilizando la URL directamente.
	 */
	@Test
	public void driverUrlNegativeTest() {
		final Object testingData[][] = {
			{
				null, "comment7", IllegalArgumentException.class
			}, {
				"user4", "comment8", IllegalArgumentException.class
			}, {
				"sponsor1", "comment7", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
		
	// Ancillary methods ------------------------------------------------------

	/*
	 * Listar comment y su respuesta si la hubiera. Pasos:
	 * 1. Autenticar usuario
	 * 2. Listar los bargaines
	 * 3. Escoger un bargain
	 * 4. Listar comentarios
	 * 5. Entrar en el display del comentario
	 * ---- SI TIENE RESPUESTA ----
	 * 6. Listar respuestas
	 * 7. Entrar en el display de la respuesta
	 * 8. Comprobación
	 */
	protected void template(final String user, final String bargainBean, final String commentBean, final Integer tamano, final String commentReplyBean, final Integer tamanoReplies, final Class<?> expected) {
		Class<?> caught;
		int bargainId, commentReplyId, commentId;
		Integer pageCommentReply;
		Comment commentReply, comment;
		Collection<Bargain> bargaines;
		Collection<Comment> comments, commentReplies;
		Bargain bargain;

		caught = null;
		try {
			
			// 1. Autenticar usuario
			super.authenticate(user);
			
			/*******/
			bargainId = super.getEntityId(bargainBean);
			bargain = this.bargainService.findOne(bargainId);
			
			commentId = super.getEntityId(commentBean);
			comment = this.commentService.findOne(commentId);
			/*******/

			// 2. Listar los bargaines
			bargaines = this.bargainService.findBargains(1, 99, "all", 0).getContent();
						
			// 3. Escoger un bargain
			for(Bargain r: bargaines)
				if(r.getId() == bargainId) bargain = r;
						
						
			// 4. Listar comentarios
			comments = this.commentService.findByBargainId(bargain.getId(), 1, this.commentService.countByBargainId(bargain.getId())).getContent();
			Assert.notNull(comments);
			
			// Comprobación
			Assert.isTrue(comments.size() == tamano);
			
			// 5. Entrar en el display del comentario
			for(Comment c: comments) if(c.getId() == commentId) comment = c;
			
			if(commentReplyBean != null) {
				
				/*******/
				commentReplyId = super.getEntityId(commentReplyBean);
				commentReply = this.commentService.findOne(commentReplyId);
				/******/
				
				// 6. Listar respuestas
				pageCommentReply = this.getPageReplyComment(comment, commentReply);
				Assert.notNull(pageCommentReply);
				commentReplies = this.commentService.findByRepliedCommentId(commentId, pageCommentReply, 5).getContent();
				
				// 7. Entrar en el display de la respuesta
				for(Comment c: commentReplies) if(c.getId() == commentReplyId) commentReply = c;
				
				// 8. Comprobación
				Assert.isTrue(commentReplies.size() == tamanoReplies);
				Assert.isTrue(commentReply.getRepliedComment().equals(comment));
			}

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	
	/*
	 * Listar comment y su respuesta si la hubiera. Pasos:
	 * 1. Autenticar usuario
	 * 1. Listar comentarios
	 */
	protected void templateMyComments(final String user, final Integer tamano, final Class<?> expected) {
		Class<?> caught;
		int userId;
		Collection<Comment> comments;
		User userEntity;
		caught = null;
		try {
			
			// 1. Autenticar usuario
			super.authenticate(user);
			
			userId = super.getEntityId(user);
			userEntity = this.userService.findOne(userId);
			Assert.notNull(userEntity);
			
			// 2. Listar comentarios
			comments = this.commentService.findByUserAccountId(userEntity.getUserAccount().getId(), 1, this.commentService.countByUserAccountnId(userEntity.getUserAccount().getId())).getContent();
			Assert.notNull(comments);
			
			// Comprobación
			Assert.isTrue(comments.size() == tamano);
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	
	/*
	 * Listar comment y su respuesta si la hubiera. Pasos:
	 * 1. Autenticar usuario
	 * 1. Escoger comentario
	 */
	protected void templateUrl(final String user, final String commentBean, final Class<?> expected) {
		Class<?> caught;
		int commentId;

		caught = null;
		try {
			
			// 1. Autenticar usuario
			super.authenticate(user);

			// 2. Entrar en el display de comment	
			commentId = super.getEntityId(commentBean);
			this.commentService.findOneToDisplay(commentId);	
			super.unauthenticate();
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private Integer getPageReplyComment(final Comment comment, final Comment commentReply) {
		Integer result, collectionSize, pageNumber;
		Collection<Comment> comments;

		collectionSize = this.commentService.countByRepliedCommentId(comment.getId());
		pageNumber = (int) Math.floor(((collectionSize / (5.0)) - 0.1) + 1);
				
		result = null;

		for (int i = 1; i <= pageNumber; i++) {
			comments = this.commentService.findByRepliedCommentId(comment.getId(), i, 5).getContent();
			if (comments.contains(commentReply)){
				result = i;
				break;
			}
		}

		return result;
	}
	
}
