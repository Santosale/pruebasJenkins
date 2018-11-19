
package controllers.actor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.NotificationService;
import controllers.AbstractController;
import domain.Notification;

@Controller
@RequestMapping("/notification/actor")
public class NotificationActorController extends AbstractController {

	// Services
	@Autowired
	private NotificationService	notificationService;


	// Constructor
	public NotificationActorController() {
		super();
	}

	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false, defaultValue = "1") final int page) {
		ModelAndView result;
		Page<Notification> notificationPage;

		notificationPage = this.notificationService.findByActorId(page, 5);
		Assert.notNull(notificationPage);

		result = new ModelAndView("notification/list");
		result.addObject("notifications", notificationPage.getContent());
		result.addObject("pageNumber", notificationPage.getTotalPages());
		result.addObject("page", page);

		return result;
	}

	// Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam final int notificationId) {
		ModelAndView result;
		Notification notification;

		notification = this.notificationService.findOneToDisplayAndDelete(notificationId, "display");
		Assert.notNull(notification);

		result = new ModelAndView("notification/display");
		result.addObject("notification", notification);

		return result;
	}

	// Delete
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam final int notificationId) {
		ModelAndView result;
		Notification notification;

		notification = this.notificationService.findOneToDisplayAndDelete(notificationId, "delete");
		Assert.notNull(notification);

		this.notificationService.delete(notification);

		result = new ModelAndView("redirect:/notification/actor/list.do");

		return result;
	}

}
