
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Article;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
	
	@Query("select a from Article a where a.writer.id=?1 and a.newspaper.isPublished=true and a.isFinalMode=true and (a.newspaper.isPrivate=false)")
	Page<Article> findAllUserPaginated(final int userId, final Pageable pageable);
	
	@Query("select a from Article a where a.writer.id=?1 and a.newspaper.isPublished=true and a.isFinalMode=true and ((a.writer.id=?2) or (a.newspaper.id IN (select (s.newspaper.id) from SubscriptionNewspaper s where s.customer.id=?2) and a.newspaper.isPrivate=true or a.newspaper.id IN (select (n1) from SubscriptionVolume sv join sv.volume.newspapers n1 where sv.customer.id=?2)))")
	Page<Article> findAllUserPaginatedByCustomer(final int userId, int principalId, final Pageable pageable);
	
	@Query("select a from Article a where a.writer.id=?1 and a.isFinalMode=true")
	Page<Article> findAllUserPaginatedByAdmin(final int userId, final Pageable pageable);
		
	@Query("select a from Article a where a.writer.id=?1 and a.newspaper.id=?2 and a.newspaper.isPublished=true and a.isFinalMode=true")
	Page<Article> findAllNewspaperPaginated(final int userId, final int newspaperId, final Pageable pageable);
	
	@Query("select a from Article a where a.hasTaboo = true")
	Page<Article> findAllTabooPaginated(final Pageable pageable);

	@Query("select a from Article a where a.writer.id = ?1")
	Page<Article> findByWritterId(final int userId, final Pageable pageable);
	
	@Query("select a from Article a where a.isFinalMode=true")
	Page<Article> findAllPaginated(final Pageable pageable);

	@Query("select a from Article a where a.writer.id=?1 and a.newspaper.id=?2 and a.newspaper.isPublished=true and a.isFinalMode=true")
	Collection<Article> findByUserIdAndNewspaperId(final int userId, final int newspaperId);
	
	@Query("select a from Article a where a.newspaper.id=?1 and a.newspaper.isPublished=true ")
	Collection<Article> findByNewspaperId(final int newspaperId);
	
	@Query("select a from Article a where a.newspaper.id=?1 and a.isFinalMode=true")
	Page<Article> findByNewspaperIdPaginated(final int newspaperId, final Pageable pageable);
	
	@Query("select a from Article a where a.writer.id=?1 and a.newspaper.isPublished=true and a.isFinalMode=true and (a.title like CONCAT('%',?2,'%') or a.summary like CONCAT('%',?2,'%') or a.body like CONCAT('%',?2,'%'))")
	Page<Article> findPublicsPublishedSearch(final int userId, String keyWord, Pageable pageable);

	@Query("select a from Article a where a.writer.id=?1 and (a.title like CONCAT('%',?2,'%') or a.summary like CONCAT('%',?2,'%') or a.body like CONCAT('%',?2,'%'))")
	Page<Article> findPublishedSearch(final int userId, String keyWord, Pageable pageable);
	
	@Query("select a from Article a where a.isFinalMode=true and (a.title like CONCAT('%',?1,'%') or a.summary like CONCAT('%',?1,'%') or a.body like CONCAT('%',?1,'%'))")
	Page<Article> findPublishedSearchNoAuth(String keyWord, Pageable pageable);
	
	@Query("select a from Article a where a.hasTaboo = true and (a.title like CONCAT('%',?1,'%') or a.summary like CONCAT('%',?1,'%') or a.body like CONCAT('%',?1,'%'))")
	Page<Article> findPublishedSearchTaboo(String keyWord, Pageable pageable);
	
	// Queries Dashboard
	
	@Query("select  avg(cast((select count(a) from Article a where a.writer.id=u.id) as float)), sqrt(sum((select count(a) from Article a where a.writer.id=u.id)*(select count(a) from Article a where a.writer.id=u.id))/(select count(u2) from User u2)-avg(cast((select count(a) from Article a where a.writer.id=u.id) as float ))*avg(cast((select count(a) from Article a where a.writer.id=u.id) as float ))) from User u")
	Double[] avgStandartDerivationArticlesPerWriter();

	@Query("select  avg(cast((select count(a) from Article a where a.newspaper.id=n.id) as float )), sqrt(sum((select count(a) from Article a where a.newspaper.id=n.id)*(select count(a) from Article a where a.newspaper.id=n.id))/(select count(n2) from Newspaper n2)-avg(cast((select count(a) from Article a where a.newspaper.id=n.id) as float ))*avg(cast((select count(a) from Article a where a.newspaper.id=n.id) as float ))) from Newspaper n")
	Double[] avgStandartDerivationArticlesPerNewspaper();
	
	@Query("select avg(cast((n.articles.size) as float)) from Newspaper n where n.isPrivate=true")
	Double avgArticlesPerPrivateNewpaper();
	
	@Query("select avg(cast((n.articles.size) as float)) from Newspaper n where n.isPrivate=false")
	Double avgArticlesPerPublicNewpaper();

}
