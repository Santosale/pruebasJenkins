package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import domain.Question;
import domain.Answer;
import domain.Survey;
import forms.QuestionForm;

import repositories.QuestionRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class QuestionService {

	// Managed repository
	@Autowired
	private QuestionRepository questionRepository;
	
	// Supporting services
	@Autowired
	private AnswerService answerService;
	
	@Autowired
	private Validator			validator;
	
	// Constructor
	public QuestionService(){
		super();
	}
	
	// Simple CRUD methods
	public Question create(final Survey survey) {
		Question result;
		
		result = new Question();
		result.setSurvey(survey);
		
		return result;
	}
	
	public Question create(final Survey survey, final Integer number) {
		Question result;
		
		result = new Question();
		result.setNumber(number);
		result.setSurvey(survey);
		
		return result;
	}
	
	public Collection<Question> findAll() {
		Collection<Question> result;
		
		result = this.questionRepository.findAll();
		
		return result;
	}
	
	public Question findOne(final int questionId) {
		Question result;
		
		Assert.isTrue(questionId != 0);
		
		result = this.questionRepository.findOne(questionId);
		
		return result;
	}
	
	public Question findOneToEdit(final int questionId) {
		Question result;
		
		Assert.isTrue(questionId != 0);
		
		result = this.questionRepository.findOne(questionId);
		Assert.notNull(result);
		
		return result;
	}
	
	public Question save(final Question question) {
		Question result, saved;
		
		if(question.getId() != 0) {
			saved = this.questionRepository.findOne(question.getId());
			Assert.isTrue(saved.getNumber() == question.getNumber());
		}
		
		Assert.notNull(question);
		Assert.notNull(question.getText());
		
		result = this.questionRepository.save(question);
		
		return result;
	}
	
	public void delete(final Question question) {
		Question savedQuestion;
		Authority authority1, authority2, authority3;
		UserAccount userAccount;

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
		
		Assert.notNull(question);
		
		savedQuestion = this.questionRepository.findOne(question.getId());
		
		for(final Answer a: this.answerService.findByQuestionId(question.getId())){
			this.answerService.delete(a);
		}
		
		for(final Question q: this.findByHigherNumber(question.getNumber(), savedQuestion.getSurvey().getId())){
			q.setNumber(q.getNumber()-1);
			this.questionRepository.save(q);
		}
		
		this.questionRepository.delete(question);
	}
	
	// Other business methods
	
	public Collection<Question> findByCreatorUserAccountId(final int userAccountId, final int page, final int size) {
		Collection<Question> result;
		
		Assert.isTrue(userAccountId != 0);
		
		result = this.questionRepository.findByCreatorUserAccountId(userAccountId, this.getPageable(page, size)).getContent();
		
		return result;
	}
	
	public Integer countByCreatorUserAccountId(final int userAccountId) {
		Integer result;
		
		Assert.isTrue(userAccountId != 0);
		
		result = this.questionRepository.countByCreatorUserAccountId(userAccountId);
		
		return result;
	}
	
	public Page<Question> findBySurveyId(final int surveyId, final int page, final int size) {
		Page<Question> result;
		
		Assert.isTrue(surveyId != 0);
		
		result = this.questionRepository.findBySurveyId(surveyId, this.getPageable(page, size));
		
		return result;
	}
	
	public Collection<Question> findBySurveyId(final int surveyId) {
		Collection<Question> result;
		
		Assert.isTrue(surveyId != 0);
		
		result = this.questionRepository.findBySurveyId(surveyId);
		
		return result;
	}
	
	public Integer countBySurveyId(final int surveyId) {
		Integer result;

		Assert.isTrue(surveyId != 0);

		result = this.questionRepository.countBySurveyId(surveyId);

		return result;
	}
	
	public Collection<Question> findByHigherNumber(final int number, final int surveyId) {
		Collection<Question> result;
				
		result = this.questionRepository.findByHigherNumber(number, surveyId);
		
		return result;
	}
	
	// Auxiliary methods
	private Pageable getPageable(final int page, final int size) {
		Pageable result;
		
		if (page == 0 || size <= 0)
			result = new PageRequest(0, 5);
		else
			result = new PageRequest(page - 1, size);
		
		return result;
	}
	
	public void flush(){
		this.questionRepository.flush();
	}
	
	// Pruned object domain
	public Question reconstruct(final Question question, final BindingResult binding) {
		Question aux;

		if(question.getId() != 0) {
			aux = this.questionRepository.findOne(question.getId());
			
			question.setVersion(aux.getVersion());
			question.setSurvey(aux.getSurvey());
		}
		
		this.validator.validate(question, binding);

		return question;
	}
	
	public Question reconstructFromSurvey(final QuestionForm questionForm, final Survey survey, final Integer number) {
		Question question;
		
		if(questionForm.getId() == 0) {
			question = this.create(survey, number);
			question.setSurvey(survey);
		} else {
			question = this.findOne(questionForm.getId());
		}

		question.setText(questionForm.getText());
				
		return question;
	}
	
}