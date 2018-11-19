
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Integer> {

	@Query("select l from Level l where ?1 >= l.minPoints and ?1 <= l.maxPoints")
	Level findByPoints(int points);

	@Query("select max(l.maxPoints) from Level l")
	Integer maxPoints();

	@Query("select min(l.minPoints) from Level l")
	Integer minPoints();

	@Query("select l from Level l where l.maxPoints = (select max(l2.maxPoints) from Level l2)")
	Level maxLevel();

	@Query("select l from Level l where l.minPoints = (select min(l2.minPoints) from Level l2)")
	Level minLevel();

	@Query("select l from Level l order by l.minPoints")
	Page<Level> findAllPaginated(Pageable pageable);

}
