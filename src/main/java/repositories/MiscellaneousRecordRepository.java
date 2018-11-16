
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.MiscellaneousRecord;

@Repository
public interface MiscellaneousRecordRepository extends JpaRepository<MiscellaneousRecord, Integer> {

	@Query("select mr from MiscellaneousRecord mr where mr.curriculum.id = ?1")
	Page<MiscellaneousRecord> findByCurriculumId(int curriculumId, Pageable pageable);

	//TODO NEW
	@Query("select count(mr) from MiscellaneousRecord mr where mr.curriculum.id = ?1")
	Integer countByCurriculumId(int curriculumId);

}
