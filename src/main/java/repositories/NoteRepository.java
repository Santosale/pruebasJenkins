
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

	@Query("select n from Note n where n.trip.manager.id = ?1 and n.auditor.id = ?2")
	Collection<Note> findByManagerIdAndAuditorId(int managerId, int auditorId);

	@Query("select n from Note n where n.trip.manager.id = ?1")
	Collection<Note> findByManagerId(int managerId);

	@Query("select n from Note n where n.auditor.id = ?1")
	Collection<Note> findByAuditorId(int auditorId);

	@Query("select min(cast((select count(n) from Note n where n.trip=t) as int )), max(cast((select count(n) from Note n where n.trip=t) as int )), avg(cast((select count(n) from Note n where n.trip=t) as float )), sqrt(sum((select count(n) from Note n where n.trip=t)*(select count(n) from Note n where n.trip=t))/(select count(t2) from Trip t2 where t2.publicationDate <= CURRENT_DATE)-avg(cast((select count(n) from Note n where n.trip=t) as float ))*avg(cast((select count(n) from Note n where n.trip=t) as float ))) from Trip t where t.publicationDate <= CURRENT_DATE")
	Double[] minMaxAvgStandardDerivationNotePerTrip();

	@Query("select n from Note n where n.trip.id= ?1")
	Collection<Note> findByTripId(int tripId);

}
