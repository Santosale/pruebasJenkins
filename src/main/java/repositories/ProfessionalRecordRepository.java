
package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.ProfessionalRecord;

@Repository
public interface ProfessionalRecordRepository extends JpaRepository<ProfessionalRecord, Integer> {

	@Query("select pr from ProfessionalRecord pr where pr.curriculum.id = ?1")
	Page<ProfessionalRecord> findByCurriculumId(int curriculumId, Pageable pageable);

	//TODO NEW
	@Query("select count(pr) from ProfessionalRecord pr where pr.curriculum.id = ?1")
	Integer countByCurriculumId(int curriculumId);

}
