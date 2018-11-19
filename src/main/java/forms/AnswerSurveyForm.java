
package forms;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.AutoPopulatingList;

import domain.Answer;
import domain.Question;

public class AnswerSurveyForm {
	
	private Map<Question, Collection<Answer>> mapQuestionAnswers;
	
	private List<Integer> answers = new AutoPopulatingList<Integer>(Integer.class); 

	@NotBlank
	public Map<Question, Collection<Answer>> getMapQuestionAnswers() {
		return mapQuestionAnswers;
	}

	public void setMapQuestionAnswers(Map<Question, Collection<Answer>> mapQuestionAnswers) {
		this.mapQuestionAnswers = mapQuestionAnswers;
	}

	public List<Integer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Integer> answers) {
		this.answers = answers;
	}
	
}
