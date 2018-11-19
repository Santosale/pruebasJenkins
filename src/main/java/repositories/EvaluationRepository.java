package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

	@Query("select e from Evaluation e")
	Page<Evaluation> findAllEvaluations(Pageable pageable);
	
	@Query("select c from Evaluation c where c.company.id = ?1")
	Page<Evaluation> findByCompanyId(int companyId, Pageable pageable);

	@Query("select count(c) from Evaluation c where c.company.id = ?1")
	Integer countByCompanyId(int companyId);
	
	@Query("select c from Evaluation c where c.user.userAccount.id = ?1")
	Page<Evaluation> findByCreatorUserAccountId(int userAccountId, Pageable pageable);

	@Query("select count(c) from Evaluation c where c.user.userAccount.id = ?1")
	Integer countByCreatorUserAccountId(int userAccountId);
	
}
