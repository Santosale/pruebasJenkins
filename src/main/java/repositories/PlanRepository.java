
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

	@Query("select s.plan from Subscription s where s.user.id = ?1")
	Plan findByUserId(int userId);
}
