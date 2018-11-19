
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

	@Query("select c from Comment c")
	Page<Comment> findAllComments(Pageable pageable);
	
	@Query("select c from Comment c where c.user.id=?1")
	Page<Comment> findByUserId(int userId, Pageable pageable);
	
	@Query("select count(c) from Comment c where c.user.id=?1")
	Integer countByUserId(int userId);

	@Query("select c from Comment c where c.repliedComment is null")
	Page<Comment> findByNoRepliedComment(Pageable pageable);

	@Query("select c from Comment c where c.repliedComment.id=?1")
	Page<Comment> findByRepliedCommentId(int commentId, Pageable pageable);
	
	@Query("select count(c) from Comment c where c.repliedComment.id=?1")
	Integer countByRepliedCommentId(int commentId);

	@Query("select c from Comment c where c.bargain.id=?1")
	Page<Comment> findByBargainId(int bargainId, Pageable pageable);
	
	@Query("select count(c) from Comment c where c.bargain.id=?1")
	Integer countByBargainId(int bargainId);
	
	@Query("select c from Comment c where c.user.userAccount.id=?1")
	Page<Comment> findByUserAccountId(int userAccountId, Pageable pageable);
	
	@Query("select count(c) from Comment c where c.user.userAccount.id=?1")
	Integer countByUserAccountId(int userAccountId);
	
	@Query("select c from Comment c where c.repliedComment is null and c.bargain.id=?1")
	Page<Comment> findByBargainIdAndNoRepliedComment(int bargainId, Pageable pageable);

	@Query("select count(c) from Comment c where c.repliedComment is null and c.bargain.id=?1")
	Integer countByBargainIdAndNoRepliedComment(int bargainId);

}
