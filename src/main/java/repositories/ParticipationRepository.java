
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Integer> {

	@Query("select sum(p.amountProduct) from Participation p where p.groupon.id = ?1")
	Integer requestedProductsByGrouponId(int grouponId);

	@Query("select p from Participation p where p.user.id = ?1")
	Page<Participation> findByUserId(int userId, Pageable pageable);

	@Query("select p from Participation p where p.groupon.id = ?1")
	Collection<Participation> findByGrouponId(int grouponId);

	@Query("select p from Participation p where p.groupon.id = ?1 and  p.user.id = ?2")
	Participation findByGrouponIdAndUserId(int grouponId, int userId);

}
