
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.TagValue;

@Repository
public interface TagValueRepository extends JpaRepository<TagValue, Integer> {

	@Query("select tv from TagValue tv where tv.tag.id= ?1 and tv.trip.id= ?2")
	TagValue findByTagAndTrip(int tagId, int tripId);

	@Query("select tv from TagValue tv where tv.trip.id= ?1")
	Collection<TagValue> findByTripId(int tripId);

	@Query("select tv from TagValue tv where tv.tag.id= ?1")
	Collection<TagValue> findByTagId(int tagId);
}
