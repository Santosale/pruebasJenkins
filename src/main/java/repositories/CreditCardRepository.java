
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

	@Query("select a.creditCard from Application a where a.applicant.id=?1")
	Collection<CreditCard> findByExplorerId(int explorerId);
	
	@Query("select s.creditCard from Sponsorship s where s.sponsor.id=?1")
	Collection<CreditCard> findBySpnsorId(int sponsorId);
	
	@Query("select cc from CreditCard cc where cc.actor.id = ?1")
	Collection<CreditCard> findByActorId(int actorId);

}
