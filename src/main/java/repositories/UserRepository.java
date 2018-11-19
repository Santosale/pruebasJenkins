
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Actor;
import domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.userAccount.id = ?1")
	User findByUserAccountId(int id);

	@Query("select u from User u")
	Page<User> findAllPageable(Pageable pageable);

	@Query("select u from User u order by u.points desc")
	Page<User> findOrderByPoints(Pageable pageable);

	@Query("select s.user from Subscription s where s.plan.name = 'Gold Premium'")
	Collection<Actor> findWithGoldPremium();

	@Query("select s.user from Subscription s where s.plan.name = 'Basic Premium'")
	Collection<Actor> findWithBasicPremium();

	@Query("select u from User u join u.wishList b where b.id = ?1")
	Collection<User> findByBargainId(final int bargainId);

	@Query("select u from User u where u.points >= ?1")
	Collection<Actor> findByMinimumPoints(int points);

	// Dashboard
	@Query("select u from User u ORDER BY cast((select count(e.user) from Evaluation e where e.user=u) as float) DESC")
	Page<User> topFiveUsersMoreValorations(Pageable pageable);

	@Query("select u from User u where (cast((select count(t2) from Ticket t2 where t2.user.id = u.id)as float)) = (select max(cast((select count(t) from Ticket t where t.user.id = u2.id)as float)) from User u2)")
	Page<User> purchaseMoreTickets(Pageable pageable);

	@Query("select u from User u where (cast((select count(t2) from Ticket t2 where t2.user.id = u.id)as float)) = (select min(cast((select count(t) from Ticket t where t.user.id = u2.id)as float)) from User u2)")
	Page<User> purchaseLessTickets(Pageable pageable);

	@Query("select u from User u where u.points > (select sum(u2.points) from User u2)*0.1")
	Page<User> more10PercentageInteractions(Pageable pageable);

	@Query("select u from User u where cast((select avg(length(ev.content)) from Evaluation ev where ev.user.id = u.id )as float) = (select max(cast((select avg(length(ev2.content)) from Evaluation ev2 where ev2.user.id = u2.id)as float)) from User u2)")
	Page<User> moreAverageCharacterLenght(Pageable pageable);

	@Query("select u from User u where cast((select count(r.winner) from Raffle r where r.winner.id = u.id )as float) = (select max(cast((select count(r2.winner) from Raffle r2 where r2.winner.id=u2.id)as float)) from User u2)")
	Page<User> moreWonRaffles(Pageable pageable);

	@Query("select u from User u where cast((select count(t2) from Ticket t2 where t2.user.id = u.id) as float ) >= 0.25*(select count(t) from Ticket t)")
	Page<User> purchase25PercentageMoreTotalForAllRaffles(Pageable pageable);

	@Query("select avg(1.0*(select cast((count(u)) as float) from Participation p, User u where p.groupon.id = b.id and p.user.id = u.id))/cast((select count(u1)*1.0 from User u1) as int) from Groupon b")
	Double avgUsersWithParticipationsPerTotal();

	@Query("select (cast((count(distinct u))as float))/(cast((select count(distinct u1) from User u1)as float)) from User u, Comment c where c.user.id = u.id")
	Double ratioUsersWithComments();

}
