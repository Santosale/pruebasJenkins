
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Auditor;

@Repository
public interface AuditorRepository extends JpaRepository<Auditor, Integer> {

	@Query("select a from Auditor a where a.userAccount.id = ?1")
	Auditor findByUserAccountId(int userAccountId);

	@Query("select n.auditor from Note n where n.id= ?1")
	Auditor findByNoteId(int noteId);

	@Query("select au.auditor from Audit au where au.trip.id= ?1")
	Collection<Auditor> findByTripId(int tripId);

}
