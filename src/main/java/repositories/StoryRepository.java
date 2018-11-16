
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Story;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {

	@Query("select s from Story s where s.writer.id=?1")
	Collection<Story> findByExplorerId(int explorerId);

	@Query("select s from Story s where s.trip.id= ?1")
	Collection<Story> findByTripId(int tripId);
}
