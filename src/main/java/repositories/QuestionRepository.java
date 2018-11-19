
package repositories;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

	@Query("select q from Question q where q.survey.surveyer.userAccount.id = ?1 order by q.number")
	Page<Question> findByCreatorUserAccountId(int userAccountId, Pageable pageable);

	@Query("select count(q) from Question q where q.survey.surveyer.userAccount.id = ?1")
	Integer countByCreatorUserAccountId(int userAccountId);

	@Query("select q from Question q where q.survey.id = ?1 order by q.number ASC")
	Page<Question> findBySurveyId(int surveyId, Pageable pageable);
	
	@Query("select q from Question q where q.survey.id = ?1 order by q.number ASC")
	Collection<Question> findBySurveyId(int surveyId);

	@Query("select count(q) from Question q where q.survey.id=?1")
	Integer countBySurveyId(int surveyId);

	@Query("select q from Question q where q.number > ?1 and q.survey.id=?2")
	Collection<Question> findByHigherNumber(int number, int surveyId);
	
}
