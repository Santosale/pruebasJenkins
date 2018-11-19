
package controllers.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.BargainService;
import services.GrouponService;
import services.NotificationService;
import services.SponsorshipService;
import services.TagService;
import services.TicketService;
import services.UserService;
import controllers.AbstractController;

@Controller
@RequestMapping("/dashboard/administrator")
public class DashboardAdministratorController extends AbstractController {

	//Services
	@Autowired
	private UserService			userservice;

	@Autowired
	private BargainService		bargainservice;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private TicketService		ticketservice;

	@Autowired
	private NotificationService	notificationService;

	@Autowired
	private GrouponService		grouponService;

	@Autowired
	private TagService			tagservice;


	// constructor
	public DashboardAdministratorController() {
		super();
	}

	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display() {
		ModelAndView result;

		Double[] avgMinMaxStandarDesviationBannersPerSponsor;
		Double avgRatioTagsPerBargain;
		Double ratioNotificationsPerTotal;
		Double avgUsersWithParticipationsPerTotal;
		Double ratioUsersWithComments;
		Double avgRatioBargainPerCategory;
		Double[] minMaxAvgStandarDesviationDiscountPerBargain;
		Double[] minMaxAvgStandarDesviationDiscountPerGroupon;
		Double avgTicketsPurchaseByUsersPerRaffle;

		avgMinMaxStandarDesviationBannersPerSponsor = this.sponsorshipService.avgMinMaxStandarDesviationBannersPerSponsor();
		avgRatioTagsPerBargain = this.tagservice.avgRatioTagsPerBargain();
		ratioNotificationsPerTotal = this.notificationService.ratioNotificationsPerTotal();
		avgUsersWithParticipationsPerTotal = this.userservice.avgUsersWithParticipationsPerTotal();
		ratioUsersWithComments = this.userservice.ratioUsersWithComments();
		avgRatioBargainPerCategory = this.bargainservice.avgRatioBargainPerCategory();
		minMaxAvgStandarDesviationDiscountPerBargain = this.bargainservice.minMaxAvgStandarDesviationDiscountPerBargain();
		minMaxAvgStandarDesviationDiscountPerGroupon = this.grouponService.minMaxAvgStandarDesviationDiscountPerGroupon();
		avgTicketsPurchaseByUsersPerRaffle = this.ticketservice.avgTicketsPurchaseByUsersPerRaffle();

		result = new ModelAndView("dashboard/display");

		result.addObject("avgMinMaxStandarDesviationBannersPerSponsor", avgMinMaxStandarDesviationBannersPerSponsor);
		result.addObject("avgRatioTagsPerBargain", avgRatioTagsPerBargain);
		result.addObject("ratioNotificationsPerTotal", ratioNotificationsPerTotal);
		result.addObject("avgUsersWithParticipationsPerTotal", avgUsersWithParticipationsPerTotal);
		result.addObject("ratioUsersWithComments", ratioUsersWithComments);
		result.addObject("avgRatioBargainPerCategory", avgRatioBargainPerCategory);
		result.addObject("minMaxAvgStandarDesviationDiscountPerBargain", minMaxAvgStandarDesviationDiscountPerBargain);
		result.addObject("minMaxAvgStandarDesviationDiscountPerGroupon", minMaxAvgStandarDesviationDiscountPerGroupon);
		result.addObject("avgTicketsPurchaseByUsersPerRaffle", avgTicketsPurchaseByUsersPerRaffle);

		return result;

	}
}
