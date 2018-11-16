
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Ranger;

@Repository
public interface RangerRepository extends JpaRepository<Ranger, Integer> {

	@Query("select cast(count(r) as float)/(select count(r) from Ranger r) from Ranger r where r.suspicious=true")
	Double ratioSuspicious();

	@Query("select cast(count(c.ranger) as float)/(select count(rt) from Ranger rt) from Curriculum c where c.ranger is not null")
	Double ratioRangersRegisteredCurriculum();

	@Query("select count( DISTINCT e.curriculum)/((select count(c2) from Curriculum c2)+0.0) from EndorserRecord e")
	Double ratioEndorsedCurriculum();

	@Query("select c.ranger from Curriculum c where c.id = ?1")
	Ranger findByCurriculumId(int curriculumId);

	@Query("select r from Ranger r where r.userAccount.id = ?1")
	Ranger findByUserAccountId(int userAccountId);

	@Query("select t.ranger from Trip t where t.id = ?1")
	Ranger findByTripId(int tripId);

}
