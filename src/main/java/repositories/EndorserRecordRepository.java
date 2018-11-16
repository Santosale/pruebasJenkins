
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.EndorserRecord;

@Repository
public interface EndorserRecordRepository extends JpaRepository<EndorserRecord, Integer> {

	@Query("select er from EndorserRecord er where er.curriculum.id = ?1")
	Page<EndorserRecord> findByCurriculumId(int curriculumId, Pageable pageable);

	//TODO NEW
	@Query("select count(er) from EndorserRecord er where er.curriculum.id = ?1")
	Integer countByCurriculumId(int curriculumId);

}
