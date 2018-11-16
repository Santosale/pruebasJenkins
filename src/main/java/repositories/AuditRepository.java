
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Integer> {

	@Query("select  min(cast((select count(a) from Audit a where a.trip=t) as int )), max(cast((select count(a) from Audit a where a.trip=t) as int )), avg(cast((select count(a) from Audit a where a.trip=t) as float )), sqrt(sum((select count(a) from Audit a where a.trip=t)*(select count(a) from Audit a where a.trip=t))/(select count(t2) from Trip t2 where t2.publicationDate <= CURRENT_DATE)-avg(cast((select count(a) from Audit a where a.trip=t) as float ))*avg(cast((select count(a) from Audit a where a.trip=t) as float ))) from Trip t where t.publicationDate <= CURRENT_DATE")
	Double[] minMaxAvgStandardDAuditsByTrip();

	@Query("select au from Audit au where au.auditor.id= ?1")
	Collection<Audit> findAuditsByAuditorId(int auditorId);

	@Query("select au from Audit au where au.trip.id= ?1")
	Collection<Audit> findByTripId(int tripId);

	@Query("select au from Audit au where au.trip.id= ?1 and au.auditor.id= ?2")
	Audit findAuditByTripIdAndAuditorId(int tripId, int auditorId);

}
