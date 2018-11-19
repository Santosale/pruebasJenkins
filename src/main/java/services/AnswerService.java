
package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.AnswerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Answer;
import domain.Question;

@Service
@Transactional
public class AnswerService {

	// Managed repository
	@Autowired
	private AnswerRepository	answerRepository;

	// Services

	@Autowired
	private Validator			validator;


	// Constructor
	public AnswerService() {
		super();
	}

	// Simple CRUD methods----------
	
	public Answer create(final Question question) {
		Answer result;

		Assert.notNull(question);

		result = new Answer();
		
		result.setCounter(0);
		result.setQuestion(question);

		return result;
	}

	public Collection<Answer> findAll() {
		Collection<Answer> result;

		result = this.answerRepository.findAll();

		return result;
	}

	public Answer findOne(final int answerId) {
		Answer result;

		Assert.isTrue(answerId != 0);

		result = this.answerRepository.findOne(answerId);

		return result;
	}

	public Answer save(final Answer answer) {
		Answer result;

		Assert.notNull(answer);
		Assert.notNull(answer.getText());
		Assert.notNull(answer.getQuestion());

		result = this.answerRepository.save(answer);

		return result;

	}

	public void delete(final Answer answer) {
		Authority authority1, authority2, authority3;
		UserAccount userAccount;
		Answer savedAnswer;

		Assert.notNull(answer);
		userAccount = LoginService.getPrincipal();
		
		authority1 = new Authority();
		authority1.setAuthority("COMPANY");
		authority2 = new Authority();
		authority2.setAuthority("MODERATOR");
		authority3 = new Authority();
		authority3.setAuthority("SPONSOR");
		
		Assert.isTrue(userAccount.getAuthorities().contains(authority1) 
				|| userAccount.getAuthorities().contains(authority2) 
				|| userAccount.getAuthorities().contains(authority3));

		savedAnswer = this.answerRepository.findOne(answer.getId());

		this.answerRepository.delete(savedAnswer);

	}

	public void flush() {
		this.answerRepository.flush();
	}
	
	public void addCounter(final Answer answer) {
		Authority authority1, authority2;
		UserAccount userAccount;

		Assert.notNull(answer);
		userAccount = LoginService.getPrincipal();
		
		authority1 = new Authority();
		authority1.setAuthority("USER");
		authority2 = new Authority();
		authority2.setAuthority("SPONSOR");
		
		Assert.isTrue(userAccount.getAuthorities().contains(authority1) 
				|| userAccount.getAuthorities().contains(authority2));		
		Assert.isTrue(LoginService.isAuthenticated());
		
		answer.setCounter(answer.getCounter() + 1);
		
		this.save(answer);
	}
	
	// Pruned object domain
	
	public Answer reconstruct(final Answer answer, final BindingResult binding) {
		Answer saved;

		if (answer.getId() != 0) {
			saved = this.answerRepository.findOne(answer.getId());
			Assert.notNull(saved);
			answer.setVersion(saved.getVersion());
			answer.setQuestion(saved.getQuestion());
		}

		this.validator.validate(answer, binding);

		return answer;
	}
	
	public Answer reconstructFromSurvey(final Answer answer, final Question question) {
		Answer result;
		
		if(answer.getId() == 0) {
			result = this.create(question);
			result.setId(0);
			result.setVersion(0);
			result.setText(answer.getText());
		} else {
			result = this.answerRepository.findOne(answer.getId());
			result.setText(answer.getText());
		}
		
		return result;
	}

	public Collection<Answer> saveAnswers(final Collection<Answer> answers) {
		final Collection<Answer> result;
		Answer saved;

		result = new ArrayList<Answer>();

		for (final Answer answer : answers) {
			saved = this.save(answer);
			result.add(saved);
		}

		return result;

	}
	
	public Collection<Answer> findByQuestionId(final int questionId) {
		Collection<Answer> result;

		Assert.isTrue(questionId != 0);
		result = this.answerRepository.findByQuestionId(questionId);

		return result;
	}
	
	public Integer countSurveyId(final int surveyId) {
		Integer result;

		Assert.isTrue(surveyId != 0);
		result = this.answerRepository.countSurveyId(surveyId);

		return result;
	}
	
	public Double ratioAnswerPerQuestion(final int questionId, final int answerId) {
		Double result;

		Assert.isTrue(questionId != 0 && answerId != 0);
		result = this.answerRepository.ratioAnswerPerQuestion(questionId, answerId);

		return result;
	}

}
