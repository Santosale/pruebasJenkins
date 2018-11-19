
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Bargain;

@Repository
public interface BargainRepository extends JpaRepository<Bargain, Integer> {

	@Query("select b from User u join u.wishList b where u.id = ?1")
	Page<Bargain> findBargainByActorId(int userAccountId, Pageable pageable);

	//Todos
	@Query("select b from Bargain b where b.isPublished=true")
	Page<Bargain> findAllPublished(Pageable pageable);

	@Query("select b from Bargain b where b.isPublished=true or b.company.id=?1")
	Page<Bargain> findAllPublishedOrMine(int companyId, Pageable pageable);

	@Query("select b from Bargain b")
	Page<Bargain> findAllPaginated(Pageable pageable);

	//Por categorías
	@Query("select b from Category c join c.bargains b where b.isPublished=true and c.id=?1")
	Page<Bargain> findAllPublishedByCategoryId(int categoryId, Pageable pageable);

	@Query("select b from Category c join c.bargains b where (b.isPublished=true or b.company.id=?1) and c.id=?2")
	Page<Bargain> findAllPublishedOrMineByCategoryId(int companyId, int categoryId, Pageable pageable);

	@Query("select b from Category c join c.bargains b where c.id=?1")
	Page<Bargain> findAllPaginatedByCategoryId(int categoryId, Pageable pageable);

	//Por etiquetas
	@Query("select b from Tag t join t.bargains b where b.isPublished=true and ?1 IN t.id")
	Page<Bargain> findAllPublishedByTagId(int tagId, Pageable pageable);

	@Query("select b from Tag t join t.bargains b where (b.isPublished=true or b.company.id=?1) and ?2 IN t.id")
	Page<Bargain> findAllPublishedOrMineByTagId(int companyId, int tagId, Pageable pageable);

	@Query("select b from Tag t join t.bargains b where ?1 IN t.id")
	Page<Bargain> findAllPaginatedByTagId(int tagId, Pageable pageable);

	//More sponsorships
	@Query("select b from Bargain b order by cast((select count(s) from Sponsorship s where s.bargain.id=b.id) as int) DESC")
	Page<Bargain> findWithMoreSponsorshipsAllPaginated(Pageable pageable);

	@Query("select b from Bargain b where b.isPublished=true order by cast((select count(s) from Sponsorship s where s.bargain.id=b.id) as int) DESC")
	Page<Bargain> findWithMoreSponsorshipsAllPublished(Pageable pageable);

	@Query("select b from Bargain b where b.isPublished=true or b.company.id=?1 order by cast((select count(s) from Sponsorship s where s.bargain.id=b.id) as int) DESC")
	Page<Bargain> findWithMoreSponsorshipsAllPublishedOrMine(int companyId, Pageable pageable);

	//

	@Query("select b from Bargain b where b.company.id=?1")
	Page<Bargain> findByCompanyId(int companyId, Pageable pageable);

	@Query("select b from Bargain b where b not in (select s.bargain from Sponsorship s where s.sponsor.id=?1)")
	Page<Bargain> findBySponsorIdWithNoSponsorship(int sponsorId, Pageable pageable);

	//Dashboard
	@Query("select b from Bargain b where cast((select count(s) from Sponsorship s where s.bargain.id = b.id )as float) = (select max(cast((select count(s2) from Sponsorship s2 where s2.bargain.id=b2.id)as float)) from Bargain b2)")
	Page<Bargain> listWithMoreSponsorships(Pageable pageable);

	@Query("select b from Bargain b where cast((select count(s) from Sponsorship s where s.bargain.id = b.id )as float) = (select min(cast((select count(s2) from Sponsorship s2 where s2.bargain.id=b2.id)as float)) from Bargain b2)")
	Page<Bargain> listWithLessSponsorships(Pageable pageable);

	@Query("select avg(1.0*(select cast((count(c)) as float)/cast((select count(c1)*1.0 from Category c1) as int) from Category c where b member of c.bargains)) from Bargain b")
	Double avgRatioBargainPerCategory();

	@Query("select b from Bargain b ORDER BY cast((select count(u) from User u where b member of u.wishList) as float)")
	Page<Bargain> findAreInMoreWishList(Pageable pageable);

	@Query("select min((b.originalPrice - b.price)/(b.originalPrice * 1.0)), max((b.originalPrice - b.price)/(b.originalPrice * 1.0)), avg((b.originalPrice - b.price)/(b.originalPrice * 1.0)), stddev((b.originalPrice - b.price)/(b.originalPrice * 1.0)) from Bargain b")
	Double[] minMaxAvgStandarDesviationDiscountPerBargain();
}
