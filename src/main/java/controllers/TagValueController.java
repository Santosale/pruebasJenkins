
package controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.AdministratorService;
import services.TagService;
import services.TagValueService;
import services.TripService;
import domain.TagValue;
import domain.Trip;

@Controller
@RequestMapping("/tagValue")
public class TagValueController extends AbstractController {

	// Services
	@Autowired
	TagService				tagService;

	@Autowired
	TagValueService			tagValueService;

	@Autowired
	AdministratorService	administratorService;

	@Autowired
	TripService				tripService;


	// Constructor
	public TagValueController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam final int tripId) {
		ModelAndView result;
		Collection<TagValue> tagValues;
		String requestURI;
		UserAccount userAccount;
		Trip trip;
		boolean canEditOrCreate;
		int userAccountId;

		userAccountId = 0;
		canEditOrCreate = false;
		//try {
		if (LoginService.isAuthenticated() == true) {
			userAccount = LoginService.getPrincipal();
			userAccountId = userAccount.getId();
		}
		//} catch (final IllegalArgumentException e) {

		//}
		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);
		if (trip.getManager().getUserAccount().getId() == userAccountId)
			canEditOrCreate = true;

		requestURI = "tagValue/list.do?tripId=";
		requestURI = requestURI + tripId;
		tagValues = this.tagValueService.findByTripId(tripId);
		Assert.notNull(tagValues);

		result = new ModelAndView("tagValue/list");
		result.addObject("tagValues", tagValues);
		result.addObject("requestURI", requestURI);
		result.addObject("tripId", tripId);
		result.addObject("canEditOrCreate", canEditOrCreate);

		return result;
	}

}
