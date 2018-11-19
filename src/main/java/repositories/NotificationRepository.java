
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	@Query("select n from Notification n where n.actor.id = ?1")
	Page<Notification> findByActorId(int actorId, Pageable pageable);

	@Query("select count(n) from Notification n where n.actor.id = ?1 and n.visited=false")
	Integer countNotVisitedByActorId(int actorId);

	@Query("select cast((count(n)) as float)/(select count(n1) from Notification n1) from Notification n where n.visited=true")
	Double ratioNotificationsPerTotal();
}
