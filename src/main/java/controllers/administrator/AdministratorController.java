/*
 * AdministratorController.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers.administrator;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.AdministratorService;
import services.ConfigurationService;
import controllers.AbstractController;
import domain.Administrator;

@Controller
@RequestMapping("/actor/administrator")
public class AdministratorController extends AbstractController {

	// Services
	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructors -----------------------------------------------------------

	public AdministratorController() {
		super();
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final Administrator administrator, final BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors())
			result = this.createEditModelAndView(administrator);
		else
			try {
				this.administratorService.save(administrator);
				result = new ModelAndView("redirect:/");
			} catch (final Throwable oops) {

				result = this.createEditModelAndView(administrator, "actor.commit.error");
			}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final Administrator administrator) {
		ModelAndView result;

		result = this.createEditModelAndView(administrator, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final Administrator administrator, final String messageCode) {
		ModelAndView result;
		boolean canEdit;
		String countryCode;
		String requestURI;

		requestURI = "actor/administrator/edit.do";
		countryCode = this.configurationService.findCountryCode();

		canEdit = false;

		if (administrator.getUserAccount().getId() == LoginService.getPrincipal().getId())
			canEdit = true;
		result = new ModelAndView("administrator/edit");

		result.addObject("modelo", "administrator");
		result.addObject("administrator", administrator);
		result.addObject("message", messageCode);
		result.addObject("canEdit", canEdit);
		result.addObject("countryCode", countryCode);
		result.addObject("requestURI", requestURI);

		return result;
	}

}
