
package controllers.manager;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
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
import controllers.AbstractController;
import domain.Tag;
import domain.TagValue;
import domain.Trip;

@Controller
@RequestMapping("/tagValue/manager")
public class TagValueManagerController extends AbstractController {

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
	public TagValueManagerController() {
		super();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam final int tripId) {
		ModelAndView result;
		TagValue tagValue;
		Trip trip;

		trip = this.tripService.findOne(tripId);
		Assert.notNull(trip);

		tagValue = this.tagValueService.create(trip);

		result = this.createEditModelAndView(tagValue);

		return result;

	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam final int tagValueId) {
		ModelAndView result;
		TagValue tagValue;

		tagValue = this.tagValueService.findOne(tagValueId);
		Assert.notNull(tagValue);

		result = this.createEditModelAndView(tagValue);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final TagValue tagValue, final BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors())
			result = this.createEditModelAndView(tagValue);
		else
			try {
				this.tagValueService.save(tagValue);
				result = new ModelAndView("redirect:/tagValue/list.do?tripId=" + tagValue.getTrip().getId());
			} catch (final Throwable oops) {
				result = this.createEditModelAndView(tagValue, "tagValue.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(final TagValue tagValue, final BindingResult binding) {
		ModelAndView result;

		try {
			this.tagValueService.delete(tagValue);
			result = new ModelAndView("redirect:/tagValue/list.do?tripId=" + tagValue.getTrip().getId());
		} catch (final Throwable oops) {
			result = this.createEditModelAndView(tagValue, "tagValue.commit.error");
		}

		return result;
	}

	// Ancillary methods
	protected ModelAndView createEditModelAndView(final TagValue tagValue) {
		ModelAndView result;

		result = this.createEditModelAndView(tagValue, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final TagValue tagValue, final String messageCode) {
		ModelAndView result;
		Collection<Tag> tags;
		Collection<TagValue> tagValues;
		Collection<Tag> currentTags;
		UserAccount userAccount;
		boolean canEdit;

		canEdit = false;
		userAccount = LoginService.getPrincipal();
		if (tagValue.getTrip().getManager().getUserAccount().getId() == userAccount.getId())
			canEdit = true;

		if (tagValue.getId() > 0)
			result = new ModelAndView("tagValue/edit");
		else
			result = new ModelAndView("tagValue/create");

		currentTags = new ArrayList<Tag>();
		tagValues = this.tagValueService.findByTripId(tagValue.getTrip().getId());
		for (final TagValue t : tagValues)
			currentTags.add(t.getTag());

		tags = this.tagService.findAll();
		tags.removeAll(currentTags);

		result.addObject("tagValue", tagValue);
		result.addObject("message", messageCode);
		result.addObject("tags", tags);
		result.addObject("canEdit", canEdit);

		return result;
	}
}
