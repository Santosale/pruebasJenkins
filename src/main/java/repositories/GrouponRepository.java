
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Groupon;

public interface GrouponRepository extends JpaRepository<Groupon, Integer> {

	@Query("select g from Groupon g where g.maxDate > CURRENT_TIMESTAMP")
	Page<Groupon> findWithMaxDateFuture(Pageable pageable);

	@Query("select g from Groupon g")
	Page<Groupon> findAllPaginated(Pageable pageable);

	@Query("select g from Groupon g where g.creator.id = ?1")
	Page<Groupon> findByCreatorId(int creatorId, Pageable pageable);

	@Query("select min((g.originalPrice - g.price)/(g.originalPrice * 1.0)), max((g.originalPrice - g.price)/(g.originalPrice * 1.0)), avg((g.originalPrice - g.price)/(g.originalPrice * 1.0)), stddev((g.originalPrice - g.price)/(g.originalPrice * 1.0)) from Groupon g")
	Double[] minMaxAvgStandarDesviationDiscountPerGroupon();

	@Query("select g from Groupon g where (cast((select count(p) from Participation p where p.groupon.id = g.id)as float)) >= 0.1*(cast((select avg(cast((select count(p2) from Participation p2 where p2.groupon.id = g2.id)as float)) from Groupon g2)as float))")
	Page<Groupon> tenPercentageMoreParticipationsThanAverage(Pageable pageable);
}
