
package forms;

import java.util.Collection;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.AutoPopulatingList;

import domain.Answer;

public class QuestionForm {
	
	private int id;
	
	private String text;
	
	private Collection<Answer> answers = new AutoPopulatingList<Answer>(Answer.class); 

	public QuestionForm() {
		super();
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}
	
	@NotBlank
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Collection<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(final Collection<Answer> answers) {
		this.answers = answers;
	}
	
}
