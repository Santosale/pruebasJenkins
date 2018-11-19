
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

	@Query("select s from Subscription s where s.user.id = ?1")
	Subscription findByUserId(int userId);

	@Query("select count(s) from Subscription s where s.creditCard.id = ?1")
	Integer countByCreditCardId(int creditCardId);
}
