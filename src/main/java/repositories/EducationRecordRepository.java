
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.EducationRecord;

@Repository
public interface EducationRecordRepository extends JpaRepository<EducationRecord, Integer> {

	@Query("select er from EducationRecord er where er.curriculum.id = ?1")
	Page<EducationRecord> findByCurriculumId(int curriculumId, Pageable pageable);

	//TODO NEW
	@Query("select count(er) from EducationRecord er where er.curriculum.id = ?1")
	Integer countByCurriculumId(int curriculumId);
}
