
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Stage;

@Repository
public interface StageRepository extends JpaRepository<Stage, Integer> {

	@Query("select s from Stage s where s.numStage > ?1 and s.trip.id= ?2")
	Collection<Stage> findByTripIdMinNumStage(int numMin, int tripId);

	@Query("select s from Stage s where s.trip.id= ?1 group by s.numStage")
	Collection<Stage> findByTripIdOrderByNumStage(int tripId);

	@Query("select s from Stage s where s.trip.id= ?1")
	Collection<Stage> findByTripId(int tripId);
}
