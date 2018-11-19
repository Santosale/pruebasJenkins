package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.ActorService;
import services.AdministratorService;
import services.CompanyService;
import services.LevelService;
import services.ModeratorService;
import services.SponsorService;
import services.UserService;
import domain.Actor;
import domain.Administrator;
import domain.Company;
import domain.Level;
import domain.Moderator;
import domain.Sponsor;
import domain.User;
import forms.ActorForm;
import forms.CompanyForm;
import forms.ModeratorForm;
import forms.SponsorForm;
import forms.UserForm;

@Controller
@RequestMapping("/actor/{actor}")
public class ActorController extends AbstractController {

	// Services
	@Autowired
	private ActorService	actorService;

	@Autowired
	private UserService		userService;
	
	@Autowired
	private SponsorService		sponsorService;

	@Autowired
	private ModeratorService	moderatorService;

	@Autowired
	private CompanyService	companyService;
	
	@Autowired
	private AdministratorService	administratorService;
	
	@Autowired
	private LevelService levelService;

	// Constructors
	public ActorController() {
		super();
	}

	// Profile
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public ModelAndView profile(@PathVariable(value="actor") final String model, @RequestParam(required=false) final Integer actorId) {
		ModelAndView result;
		Actor actor;
		User user; 
		Level level;
		
		level = null; 

		if(actorId != null) {
			actor = this.actorService.findOne(actorId);
		} else {
			actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		}
		Assert.notNull(actor);
		
		if(model.equals("user")) {
			user = (User) actor;
			level = this.levelService.findByPoints(user.getPoints()); 
		}

		result = new ModelAndView("actor/display");
		result.addObject("actor", actor);
		result.addObject("model", model);
		result.addObject("isPublic", false);
		if(model.equals("user")) result.addObject("level", level);
		
		return result;
	}
	
	// Profile
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editProfile(@PathVariable(value="actor") final String model) {
		ModelAndView result;
		Actor actor;

		actor = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.notNull(actor);

		result = this.editModelAndView(actor, model);

		return result;
	}
	
	// Creation
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@PathVariable(value="actor") final String actor) {
		ModelAndView result;
		
		result = new ModelAndView("actor/edit");
		
		result.addObject("requestURI", "actor/"+actor+"/edit.do");

		if(actor.equals("sponsor")) {
			SponsorForm actorForm;
			actorForm = new SponsorForm();
			result.addObject("actorForm", actorForm);
		}
		
		if(actor.equals("moderator")) {
			ModeratorForm actorForm;
			actorForm = new ModeratorForm();
			result.addObject("actorForm", actorForm);
		}
		
		result.addObject("model", actor);

		return result;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(final ActorForm actorForm, final BindingResult binding, @RequestParam final String model) {
		ModelAndView result;
		Actor actor;
		boolean next;

		next = true;
		result = null;
		actor = null;
		try {
			if(model.equals("sponsor")) actor = this.sponsorService.reconstruct(actorForm, binding);
			if(model.equals("moderator")) actor = this.moderatorService.reconstruct(actorForm, binding);
			if(model.equals("administrator")) actor = this.administratorService.reconstruct(actorForm, binding);
		} catch (final Throwable e) {

			if (binding.hasErrors())
				result = this.createModelAndView(actorForm, model);
			else
				result = this.createModelAndView(actorForm, model, "actor.commit.error");

			next = false;
		}

		if (next)
			if (binding.hasErrors()) {
				result = this.createModelAndView(actorForm, model);
			} else
				try {
					if(model.equals("sponsor")) this.sponsorService.save((Sponsor) actor);
					if(model.equals("moderator")) this.moderatorService.save((Moderator) actor);
					if(model.equals("administrator")) this.administratorService.save((Administrator) actor);
					result = new ModelAndView("redirect:/");
				} catch (final Throwable oops) {
					result = this.createModelAndView(actorForm, model, "actor.commit.error");
				}

		return result;
	}
	
	// Ancillary methods
	protected ModelAndView createModelAndView(final ActorForm actorForm, final String model) {
		ModelAndView result;

		result = this.createModelAndView(actorForm, model, null);

		return result;
	}

	protected ModelAndView createModelAndView(final ActorForm actorForm, final String model, final String messageCode) {
		ModelAndView result;

		result = new ModelAndView("actor/edit");

		result.addObject("model", model);
		result.addObject("actorForm", actorForm);
		result.addObject("message", messageCode);
		result.addObject("requestURI", "actor/"+model+"/edit.do");

		return result;
	}

	// Ancillary methods
	protected ModelAndView editModelAndView(final Actor actor, final String model) {
		ModelAndView result;

		result = this.editModelAndView(actor, model, null);

		return result;
	}

	protected ModelAndView editModelAndView(final Actor actor, final String model, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		CompanyForm companyForm;
		Company company;
		ActorForm actorForm;
		UserForm userForm;
		User user;

		canEdit = false;
		
		result = new ModelAndView("actor/edit");

		if (actor.getUserAccount().getId() == LoginService.getPrincipal().getId()) canEdit = true;

		if (model.equals("moderator") || model.equals("sponsor") || model.equals("administrator")) {

			actorForm = new ActorForm();

			actorForm.setAddress(actor.getAddress());
			actorForm.setEmail(actor.getEmail());
			actorForm.setId(actor.getId());
			actorForm.setName(actor.getName());
			actorForm.setPhone(actor.getPhone());
			actorForm.setSurname(actor.getSurname());
			actorForm.setUsername(actor.getUserAccount().getUsername());
			actorForm.setIdentifier(actor.getIdentifier());

			result.addObject("actorForm", actorForm);

		} else if (model.equals("company")) {

			companyForm = new CompanyForm();

			companyForm.setAddress(actor.getAddress());
			companyForm.setEmail(actor.getEmail());
			companyForm.setId(actor.getId());
			companyForm.setName(actor.getName());
			companyForm.setPhone(actor.getPhone());
			companyForm.setSurname(actor.getSurname());
			companyForm.setUsername(actor.getUserAccount().getUsername());
			companyForm.setIdentifier(actor.getIdentifier());
			
			company = this.companyService.findOne(actor.getId());
			Assert.notNull(company);
			companyForm.setCompanyName(company.getCompanyName());
			companyForm.setType(company.getType());

			result.addObject("actorForm", companyForm);

		} else if (model.equals("user")) {
			
			userForm = new UserForm();

			userForm.setAddress(actor.getAddress());
			userForm.setEmail(actor.getEmail());
			userForm.setId(actor.getId());
			userForm.setName(actor.getName());
			userForm.setPhone(actor.getPhone());
			userForm.setSurname(actor.getSurname());
			userForm.setUsername(actor.getUserAccount().getUsername());
			userForm.setIdentifier(actor.getIdentifier());
			
			user = this.userService.findOne(actor.getId());
			Assert.notNull(user);
			
			userForm.setAvatar(user.getAvatar());

			result.addObject("actorForm", userForm);
			
		}

		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("requestURI", "actor/" + model + "/edit.do");
		result.addObject("model", model);

		return result;
	}

}
