
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

	@Query("select s from Sponsorship s where s.bargain.id=?1 and s.amount>0 order by rand()")
	Page<Sponsorship> findRandomSponsorships(int bargainId, Pageable pageable);

	@Query("select s from Sponsorship s where s.bargain.id=?1")
	Collection<Sponsorship> findByBargainId(int bargainId);

	@Query("select s from Sponsorship s where s.bargain.id=?1")
	Page<Sponsorship> findByBargainIdPageable(int bargainId, Pageable pageable);

	@Query("select s from Sponsorship s where s.sponsor.id=?1")
	Page<Sponsorship> findBySponsorId(int sponsorId, Pageable pageable);

	@Query("select s from Sponsorship s where s.sponsor.id=?1 and s.bargain.id=?2")
	Sponsorship findBySponsorIdAndBargainId(int sponsorId, int bargainId);

	@Query("select sum(s.amount) from Sponsorship s where s.bargain.id=?1 and s.id!=?2")
	Double sumAmountByBargainIdAndNotSponsorshipId(int bargainId, int sponsorshipId);

	@Query("select  avg(cast((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id) as float )), min(cast((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id) as int )), max(cast((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id) as int )), sqrt(sum((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id)*(select count(sp) from Sponsorship sp where sp.sponsor.id=s.id))/(select count(s2) from Sponsor s2)-avg(cast((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id) as float ))*avg(cast((select count(sp) from Sponsorship sp where sp.sponsor.id=s.id) as float ))) from Sponsor s")
	Double[] avgMinMaxStandarDesviationBannersPerSponsor();
}
