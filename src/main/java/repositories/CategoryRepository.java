
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("select t.category from Trip t where t.id= ?1")
	Category findCategoryByTripId(int tripId);

	@Query("select c from Category c where c.fatherCategory=null")
	Category findCategoryWithoutFather();
}
