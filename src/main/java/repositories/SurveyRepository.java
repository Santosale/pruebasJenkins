package repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Survey;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {

	@Query("select s from Survey s where s.surveyer.userAccount.id = ?1")
	Page<Survey> findByActorUserAccountId(int userAccountId, Pageable pageable);
	
	@Query("select s from Survey s where (cast((select sum(a2.counter) from Answer a2 where a2.question.survey.id = s.id) as float)) >= (cast((select avg(cast(( select sum(a2.counter) from Answer a2 where a2.question.survey.id = s2.id) as float)) from Survey s2) as float ))")
	Page<Survey> surveyMorePopular(Pageable pageable);
}
