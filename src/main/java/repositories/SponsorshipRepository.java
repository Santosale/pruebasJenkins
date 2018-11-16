
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Sponsorship;

@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Integer> {

	@Query("select s from Sponsorship s where s.trip.id= ?1")
	Collection<Sponsorship> findByTripId(int tripId);

	@Query("select s from Sponsorship s where s.sponsor.id= ?1")
	Collection<Sponsorship> findBySponsorId(int sponsorId);

	@Query("select s from Sponsorship s where s.trip.id=?1 order by rand()")
	public Page<Sponsorship> findRandomSponsorship(Pageable pageable, int tripId);
}
