package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Actor;
import domain.Sponsor;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Integer> {

	@Query("select s from Sponsor s where s.userAccount.id = ?1")
	Sponsor findByUserAccountId(final int userAccountId);
	
	@Query("select distinct s from Sponsor s, Sponsorship sp where sp.sponsor.id = s.id and sp.bargain.company.id = ?1")
	Collection<Actor> findByIfHaveAds(int companyId);
	
	@Query("select s from Sponsor s")
	Collection<Actor> findAllActor();
	
}
