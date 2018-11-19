
package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.AnswerService;
import services.CompanyService;
import services.ModeratorService;
import services.QuestionService;
import services.SponsorService;
import services.SurveyService;
import services.UserService;
import domain.Answer;
import domain.Question;
import domain.Surveyer;
import domain.Survey;
import forms.AnswerSurveyForm;
import forms.QuestionForm;
import forms.SurveyForm;

@Controller
@RequestMapping("/survey/{actor}")
public class SurveyController extends AbstractController {

	// Services
	@Autowired
	private SurveyService			surveyService;

	@Autowired
	private AnswerService		answerService;

	@Autowired
	private QuestionService		questionService;
	
	@Autowired
	private ModeratorService	moderatorService;
	
	@Autowired
	private CompanyService	companyService;

	@Autowired
	private SponsorService	sponsorService;

	@Autowired
	private UserService	userService;
	
	// Constructor
	public SurveyController() {
		super();
	}

	// List
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		Page<Survey> surveyPage;
		    
		surveyPage = this.surveyService.findByActorUserAccountId(LoginService.getPrincipal().getId(), page, 5);
		Assert.notNull(surveyPage);
		
		result = new ModelAndView("survey/list");
		result.addObject("surveys", surveyPage.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", surveyPage.getTotalPages());
		result.addObject("requestURI", "survey/"+model+"/list.do");
		result.addObject("model", model);
		    
		return result;
	}
	
	// More popular
	@RequestMapping(value="/morePopular", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue="1") Integer page) {
		ModelAndView result;
		Page<Survey> surveyPage;
			    
		surveyPage = this.surveyService.surveyMorePopular(page, 5);
		Assert.notNull(surveyPage);
	
		result = new ModelAndView("survey/list");
		result.addObject("surveys", surveyPage.getContent());
		result.addObject("page", page);
		result.addObject("pageNumber", surveyPage.getTotalPages());
		result.addObject("requestURI", "survey/administrator/morePopular.do");
			    
		return result;
	}
	
	// Display
	@RequestMapping(value="/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam int surveyId, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		Survey survey;
		Collection<Question> questions;
		Collection<Answer> answers;
		LinkedHashMap <Answer, Double> answersRatio;
		LinkedHashMap <Question, Map<Answer, Double>> questionsAnswerRatio;
		
		survey = this.surveyService.findOneToEdit(surveyId);
		Assert.notNull(survey);
		
		questions = this.questionService.findBySurveyId(surveyId, 1, this.questionService.countBySurveyId(surveyId)).getContent();
		
		questionsAnswerRatio = new LinkedHashMap <Question, Map<Answer, Double>>();
		
		for(Question q : questions) {
			answersRatio = new LinkedHashMap <Answer, Double>();
			answers = this.answerService.findByQuestionId(q.getId());
			for (Answer a : answers)
				answersRatio.put(a, this.answerService.ratioAnswerPerQuestion(q.getId(), a.getId()));
			questionsAnswerRatio.put(q, answersRatio);
		}
		
		result = new ModelAndView("survey/display");
		result.addObject("requestURI", "survey/"+model+"/list.do");
		result.addObject("survey", survey);
		result.addObject("questions", questions);
		result.addObject("questionsAnswerRatio", questionsAnswerRatio);
				
		return result;
	}

	// Create-------------------------------------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@PathVariable(value="actor") final String model) {
		ModelAndView result;
		SurveyForm surveyForm;
		Surveyer surveyer;
		
		surveyForm = new SurveyForm();
		
		surveyer = null;
		if(model.equals("moderator")) {
			surveyer = this.moderatorService.findByUserAccountId(LoginService.getPrincipal().getId());
		} else if (model.equals("company")) {
			surveyer = this.companyService.findByUserAccountId(LoginService.getPrincipal().getId());
		} else if (model.equals("sponsor")) {
			surveyer = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
		}
		Assert.notNull(surveyer);
		
		surveyForm.setSurveyer(surveyer);
		
		result = this.createEditModelAndView(surveyForm, model);
		
		return result;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int surveyId, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		SurveyForm surveyForm;
		Survey survey;
		Collection<Question> questions;
		Collection<QuestionForm> listQuestionForm;
		Collection<Answer> answers;
		QuestionForm questionForm;
		
		survey = this.surveyService.findOneToEdit(surveyId);
		Assert.notNull(survey);
		
		surveyForm = new SurveyForm();
		surveyForm.setTitle(survey.getTitle());
		surveyForm.setId(survey.getId());
		surveyForm.setSurveyer(survey.getSurveyer());
		
		questions = this.questionService.findBySurveyId(survey.getId());
		Assert.notNull(questions);
		
		listQuestionForm = new ArrayList<QuestionForm>();
		for(Question q: questions) {
			answers = this.answerService.findByQuestionId(q.getId());
			Assert.notNull(answers);
			questionForm = new QuestionForm();
			questionForm.setId(q.getId());
			questionForm.setText(q.getText());
			for(Answer a: answers) questionForm.getAnswers().add(a);
			listQuestionForm.add(questionForm);
		}
		
		surveyForm.setQuestions(listQuestionForm);
		surveyForm.setSurveyer(survey.getSurveyer());
		
		result = this.createEditModelAndView(surveyForm, model);
		
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final SurveyForm surveyForm, final BindingResult binding, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		Survey survey;
		DataBinder bindingSurvey;
		
		survey = null;
		if(binding.hasErrors()) {
			result = createEditModelAndView(surveyForm, model);
		} else {
			bindingSurvey = new DataBinder(survey);
			survey = this.surveyService.reconstruct(surveyForm, model, bindingSurvey.getBindingResult());
			Assert.notNull(survey);
				
			if(bindingSurvey.getBindingResult().hasErrors()) {
				result = createEditModelAndView(surveyForm, model);
			} else {
				try {
					this.surveyService.save(survey, surveyForm);
					result = new ModelAndView("redirect:list.do");
				} catch (final Throwable oops) {
					result = this.createEditModelAndView(surveyForm, model, "survey.commit.error");
				}
			}
		}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final SurveyForm surveyForm, final BindingResult binding, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		Survey survey;

		survey = this.surveyService.reconstruct(surveyForm, model, binding);
		Assert.notNull(survey);
			
		if(binding.hasErrors()) {
			result = createEditModelAndView(surveyForm, model);
		} else {
			try {
				this.surveyService.delete(survey);
				result = new ModelAndView("redirect:list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(surveyForm, model, "survey.commit.error");
			}
		}			
		
		return result;
	}
	
	protected ModelAndView createEditModelAndView(final SurveyForm surveyForm, final String model) {
		ModelAndView result;

		result = this.createEditModelAndView(surveyForm, model, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final SurveyForm surveyForm, final String model, final String messageCode) {
		ModelAndView result;

		if (surveyForm.getId() != 0)
			result = new ModelAndView("survey/edit");
		else 
			result = new ModelAndView("survey/create");

		result.addObject("surveyForm", surveyForm);
		result.addObject("message", messageCode);
		result.addObject("model", model);

		return result;
	}

	// Answer -------------------------------------------------------------------------------
	
	@RequestMapping(value = "/answer", method = RequestMethod.GET)
	public ModelAndView answer(@RequestParam int surveyId, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		AnswerSurveyForm answerSurveyForm;
		Survey survey;
		Collection<Question> questions;
		Collection<Answer> answers;
		Map<Question, Collection<Answer>> questionAnswers;
		
		survey = this.surveyService.findOne(surveyId);
		Assert.notNull(survey);
		
		questions = this.questionService.findBySurveyId(surveyId, 1, this.questionService.countBySurveyId(surveyId)).getContent();
		
		questionAnswers = new HashMap<Question, Collection<Answer>>();
		
		for(Question q : questions) {
			answers = this.answerService.findByQuestionId(q.getId());
			questionAnswers.put(q, answers);
		}
		
		answerSurveyForm = new AnswerSurveyForm();
		answerSurveyForm.setMapQuestionAnswers(questionAnswers);
		
		result = this.createEditModelAndView2(answerSurveyForm, model);
		
		return result;
	}

	@RequestMapping(value = "/answer", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final AnswerSurveyForm answerSurveyForm, final BindingResult binding, @PathVariable(value="actor") final String model) {
		ModelAndView result;
		Answer answer, answerPunt;
		Collection<Answer> answers, answersReconstruct, answersCollection;
		Integer counter;
		
		answers = new HashSet<Answer>();
		answersReconstruct = new HashSet<Answer>();
		answerPunt = new Answer();
		
		Assert.notNull(answerSurveyForm.getAnswers());
		
		for (int i : answerSurveyForm.getAnswers()) {
			answerPunt = this.answerService.findOne(i);
			answers.add(this.answerService.findOne(i));
		}
		
		// Por cada pregunta solo y al menos debe existir una respuesta
		
		for (Question q : this.questionService.findBySurveyId(answerPunt.getQuestion().getSurvey().getId(), 1, this.questionService.countBySurveyId(answerPunt.getQuestion().getSurvey().getId()))) {
			answersCollection = this.answerService.findByQuestionId(q.getId());
			counter = 0;
			for (Answer a : answersCollection) {
				if (answers.contains(a))
					counter ++;
			}
			Assert.isTrue(counter == 1);	
		}
		
		for (Answer a : answers) {
			answer = this.answerService.reconstruct(a, binding);
			answersReconstruct.add(answer);
		}
			
		if(binding.hasErrors()) {
			result = createEditModelAndView2(answerSurveyForm, model);
		} else {
			try {
				for (Answer a : answersReconstruct) {
					this.answerService.addCounter(a);
				}
				if(model.equals("user"))
					this.userService.addPoints(10);
				result = new ModelAndView("redirect:/notification/actor/list.do");
			} catch (final Throwable oops) {
				result = this.createEditModelAndView2(answerSurveyForm, "survey.commit.error");
			}
		}
			
		return result;
	}

	protected ModelAndView createEditModelAndView2(final AnswerSurveyForm answerSurveyForm, final String model) {
		ModelAndView result;

		result = this.createEditModelAndView2(answerSurveyForm, model, null);

		return result;
	}

	protected ModelAndView createEditModelAndView2(final AnswerSurveyForm answerSurveyForm, final String model, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("survey/answer");

		result.addObject("answerSurveyForm", answerSurveyForm);
		result.addObject("message", messageCode);
		result.addObject("model", model);

		return result;
	}
}