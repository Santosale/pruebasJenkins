
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
	@Query("select count(a) from Answer a where a.question.survey.id=?1")
	Integer countSurveyId(int surveyId);

	@Query("select a from Answer a where a.question.id=?1")
	Collection<Answer> findByQuestionId(int questionId);
	
	@Query("select cast((sum(a.counter)) as float)/(select cast((sum(an.counter)) as float) from Answer an where an.question.id = ?1) from Answer a where a.id = ?2")
	Double ratioAnswerPerQuestion(int questionId, int answerId);
	
}
