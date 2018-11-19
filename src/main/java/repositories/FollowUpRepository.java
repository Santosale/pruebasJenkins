
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.FollowUp;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Integer> {

	@Query("select f from FollowUp f where f.user.id=?1")
	Page<FollowUp> findByUserIdPaginated(int userId, Pageable pageable);

	@Query("select f from FollowUp f where f.article.id=?1")
	Page<FollowUp> findByArticleIdPaginated(int articleId, Pageable pageable);

	@Query("select f from FollowUp f where f.article.id=?1")
	Collection<FollowUp> findByArticleId(int articleId);

	@Query("select avg(cast((select count(f) from FollowUp f where f.article.id=a.id)as float)) from Article a")
	Double numberFollowUpPerArticle();

	@Query("select avg(cast((select count(f) from FollowUp f where f.article=a and (DATEDIFF(f.publicationMoment , a.newspaper.publicationDate)<7 or (DATEDIFF(f.publicationMoment , a.newspaper.publicationDate)=7 AND (hour(f.publicationMoment)*60*60+minute(f.publicationMoment )*60+second(f.publicationMoment )<=hour(a.newspaper.publicationDate)*60*60+minute(a.newspaper.publicationDate)*60+second( a.newspaper.publicationDate)) )))as float)) from Article a where a.newspaper.isPublished=true and a.newspaper.publicationDate<CURRENT_TIMESTAMP")
	Double averageFollowUpPerArticleOneWeek();

	@Query("select avg(cast((select count(f) from FollowUp f where f.article=a and (DATEDIFF(f.publicationMoment , a.newspaper.publicationDate)<14 or (DATEDIFF(f.publicationMoment , a.newspaper.publicationDate)=14 AND (hour(f.publicationMoment)*60*60+minute(f.publicationMoment )*60+second(f.publicationMoment )<=hour(a.newspaper.publicationDate)*60*60+minute(a.newspaper.publicationDate)*60+second( a.newspaper.publicationDate)) )))as float)) from Article a where a.newspaper.isPublished=true and a.newspaper.publicationDate<CURRENT_TIMESTAMP")
	Double averageFollowUpPerArticleTwoWeek();
}
