
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Bargain;
import domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("select c from Category c where c.fatherCategory.id=?1")
	Page<Category> findByFatherCategoryId(int fatherCategoryId, Pageable pageable);

	@Query("select c from Category c where c.fatherCategory.id=?1")
	Collection<Category> findAllByFatherCategoryId(int fatherCategoryId);

	@Query("select c from Category c where c.fatherCategory is null")
	Page<Category> findWithoutFather(Pageable pageable);

	@Query("select c from Category c where c.fatherCategory is null")
	Collection<Category> findAllWithoutFather();

	@Query("select c from Category c join c.bargains b where ?1 IN b.id")
	Page<Category> findByBargainId(int bargainId, Pageable pageable);

	@Query("select c from Category c where ?1 NOT MEMBER OF c.bargains")
	Page<Category> findByNotBargainId(Bargain bargain, Pageable pageable);

	@Query("select c from Category c join c.bargains b where ?1 IN b.id")
	Collection<Category> findAllByBargainId(int bargainId);

	@Query("select c from Category c where c.defaultCategory=true")
	Category findByDefaultCategory();

	@Query("select c from Category c")
	Page<Category> findAllPaginated(Pageable pageable);

	@Query("select c from Category c where c.fatherCategory=?1 and c.name=?2")
	Collection<Category> findOneLevelByFatherAndName(Category fatherCategory, String name);

	@Query("select c from Category c where c.fatherCategory is null and c.name=?1")
	Collection<Category> findOneLevelByFatherAndNameRoot(String name);

	//Dashboard
	@Query("select c from Category c where (cast((select count(b) from Bargain b where b member of c.bargains)as float)) >= (cast((select avg(cast((select count(b2) from Bargain b2 where b2 member of c2.bargains)as float)) from Category c2)as float))")
	Page<Category> moreBargainThanAverage(Pageable pageable);

}
