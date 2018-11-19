
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import services.CommentService;
import services.BargainService;
import services.ModeratorService;
import utilities.AbstractTest;
import domain.Comment;
import domain.Bargain;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteCommentTest extends AbstractTest {

	@Autowired
	private CommentService	commentService;

	@Autowired
	private BargainService bargainService;
	
	@Autowired
	private ModeratorService moderatorService;
	
	/*
	 * Pruebas:
	 * 		1. Un moderador trata de eliminar un comentario.
	 * 		2. Un moderador trata de eliminar un comentario que tiene respuestas.
	 * 		3. Un moderador trata de eliminar la respuesta de un comentario.
	 * 		4. Un user borra un comentario suyo.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *		39.	Por defecto los usuarios comienzan con 50 puntos y podrán verse penalizados 
	 *			con -10 puntos cada vez que el moderador tenga que borrar alguna de sus 
	 *			publicaciones (conjuntas, comentarios, etc) por inapropiadas. 
	 */
	@Test
	public void driverPostiveTest() {
		final Object testingData[][] = {
			{
				"moderator1", "bargain1", "comment1", null, null
			}, {
				"moderator1", "bargain1", "comment2", null, null
			}, {
				"moderator1", "bargain1", "comment2", "comment3", null
			} , {
				"user3", "bargain1", "comment1", null, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un admin trata de eliminar un comentario cuando no lo tiene permitido.
	 * 		2. Un sponsor trata de eliminar un comentario cuando no lo tiene permitido.
	 *		3. Un user trata de eliminar un comentario que no es suyo.
	 * 
	 * Requisitos:
	 * 		25.	Un actor autenticado como usuario debe ser capaz de: 
	 *			3.	Puede escribir y borrar un comentario, además de escribir una respuesta.
	 *		39.	Por defecto los usuarios comienzan con 50 puntos y podrán verse penalizados 
	 *			con -10 puntos cada vez que el moderador tenga que borrar alguna de sus 
	 *			publicaciones (conjuntas, comentarios, etc) por inapropiadas. 
	 *	 */
	@Test
	public void driverNegativeTest() {
		final Object testingData[][] = {
			{
				"administrator", "bargain1", "comment1", null, IllegalArgumentException.class
			}, {
				"sponsor1", "bargain1", "comment2", null, IllegalArgumentException.class
			}, {
				"user1", "bargain1", "comment1", null, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	// Ancillary methods ------------------------------------------------------

	/*
	 * Eliminar un comment. Pasos:
	 * 1. Autenticar usuario
	 * 1. Listar bargains
	 * 2. Escoger un bargain 
	 * 3. Listar comentarios
	 * 4. Escoger comentario
	 * 5. Eliminar comentario
	 * ---- SI TIENE RESPUESTA ----
	 * 6. Listas respuestas
	 * 7. Escoger respuesta
	 * 8. Eliminar respuesta
	 */
	protected void template(final String user, final String bargainBean, final String commentBean, final String commentReplyBean, final Class<?> expected) {
		Class<?> caught;
		int bargainId, commentId, commentReplyId, pageCommentReply;
		Comment comment, commentReply;
		Collection<Comment> comments, commentReplies;
		Collection<Bargain> bargains;
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

			// 2. Listar bargains
			bargains = this.bargainService.findBargains(1, 99, "all", 0).getContent();
			Assert.notNull(bargains);
			
			// 3. Escoger bargain
			for(Bargain r: bargains) if(r.getId() == bargainId) bargain = r;
			Assert.notNull(bargain);
			
			// 4. Listar comentarios
			comments = this.commentService.findByBargainId(bargain.getId(), 1, this.commentService.countByBargainId(bargain.getId())).getContent();
			Assert.notNull(comments);
			
			// 5. Entrar en el display del comentario
			for(Comment c: comments) if(c.getId() == commentId) comment = c;
			if(commentReplyBean == null) {
				if (this.moderatorService.findByUserAccountId(LoginService.getPrincipal().getId()) == null)
					this.commentService.delete(comment);
				else
					this.commentService.deleteModerator(comment);
				//Comprobación
				Assert.isNull(this.commentService.findOne(commentId));
				Assert.isTrue(this.commentService.findByRepliedCommentId(commentId, 0, 99).getContent().size() == 0);
			}
						
			if(commentReplyBean != null) {
				
				/*******/
				commentReplyId = super.getEntityId(commentReplyBean);
				commentReply = this.commentService.findOne(commentReplyId);
				/******/
				
				// 6. Listar respuestas
				pageCommentReply = this.getPageReplyComment(comment, commentReply);
				Assert.notNull(pageCommentReply);
				commentReplies = this.commentService.findByRepliedCommentId(commentId, pageCommentReply, 5).getContent();
				
				// 7. Escoger respuesta
				for(Comment c: commentReplies) if(c.getId() == commentReplyId) commentReply = c;
				
				// 8. Eliminar respuesta
				if (this.moderatorService.findByUserAccountId(LoginService.getPrincipal().getId()) == null)
					this.commentService.delete(commentReply);
				else
					this.commentService.deleteModerator(commentReply);
				
				// Comprobación
				Assert.isNull(this.commentService.findOne(commentReplyId));
				Assert.isTrue(this.commentService.findByRepliedCommentId(commentReplyId, 0, 99).getContent().size() == 0);
				Assert.notNull(this.commentService.findOne(commentId));
			}
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		
		super.unauthenticate();
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
