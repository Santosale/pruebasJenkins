
package controllers.administrator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AdvertisementService;
import services.ArticleService;
import services.ChirpService;
import services.CustomerService;
import services.FollowUpService;
import services.NewspaperService;
import services.SubscriptionVolumeService;
import services.UserService;
import controllers.AbstractController;
import domain.Newspaper;

@Controller
@RequestMapping("/dashboard/administrator")
public class DashboardAdministratorController extends AbstractController {

	//Services
	@Autowired
	private UserService					userService;

	@Autowired
	private NewspaperService			newspaperService;

	@Autowired
	private FollowUpService				followUpService;

	@Autowired
	private ArticleService				articleService;

	@Autowired
	private ChirpService				chirpService;

	@Autowired
	private CustomerService				customerService;

	@Autowired
	private AdvertisementService		advertisementService;

	@Autowired
	private SubscriptionVolumeService	subscriptionVolumeService;


	// Constructor
	public DashboardAdministratorController() {
		super();
	}

	//Display
	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView display(@RequestParam(required = false, defaultValue = "1") final Integer page, @RequestParam(required = false) final Integer size) {
		final ModelAndView result;
		final Double[] newspaperPerUser;
		final Double[] articlesPerWriter;
		final Double[] articlesPerNewspaper;
		final Double ratioUserCreateNewspaper;
		final Double ratioUserWrittenArticle;

		final Double followUpPerArticle;
		final Double followUpPerArticleOneWeek;
		final Double followUpPerArticleTwoWeek;
		final Double[] chirpPerUser;
		final Double ratioUserPostNumberChirps;

		final Double ratioPublicVsPrivateNewspaper;
		final Double numberArticlesPerPrivateNewspaper;
		final Double numberArticlesPerPublicNewspaper;
		final Map<Newspaper, Double> ratioSuscribersPrivateVsTotalCustomer;
		final Double averageRatioPrivateVsPublicNewspaperPerPublisher;

		final Double ratioNewspaperHaveAdvertisementVsHavent;
		final Double ratioAdvertisementHaveTaboo;
		final Double avgNewspaperPerVolume;
		final Double ratioSubscriptionVolumeVsNewspaper;

		int pageNumber;

		newspaperPerUser = this.newspaperService.avgStandarDevNewspapersCreatedPerUser();
		articlesPerWriter = this.articleService.avgStandartDerivationArticlesPerWriter();
		articlesPerNewspaper = this.articleService.avgStandartDerivationArticlesPerWriter();
		ratioUserCreateNewspaper = this.userService.ratioUsersWhoHaveCreatedNewspaper();
		ratioUserWrittenArticle = this.userService.ratioUserWhoHaveWrittenArticle();

		followUpPerArticle = this.followUpService.numberFollowUpPerArticle();
		followUpPerArticleOneWeek = this.followUpService.averageFollowUpPerArticleOneWeek();
		followUpPerArticleTwoWeek = this.followUpService.averageFollowUpPerArticleTwoWeek();
		chirpPerUser = this.chirpService.avgStandartDeviationNumberChirpsPerUser();
		ratioUserPostNumberChirps = this.userService.ratioUserWhoHavePostedAbove75Chirps();

		ratioPublicVsPrivateNewspaper = this.newspaperService.ratioPublicVsPrivateNewspaper();
		numberArticlesPerPrivateNewspaper = this.articleService.avgArticlesPerPrivateNewpaper();
		numberArticlesPerPublicNewspaper = this.articleService.avgArticlesPerPublicNewpaper();
		ratioSuscribersPrivateVsTotalCustomer = this.customerService.ratioSuscribersPerPrivateNewspaperVersusNumberCustomers(page);
		pageNumber = this.customerService.countRatioSuscribersPerPrivateNewspaperVersusNumberCustomers();

		pageNumber = (int) Math.floor(((pageNumber / (5 + 0.0)) - 0.1) + 1);
		averageRatioPrivateVsPublicNewspaperPerPublisher = this.newspaperService.ratioPrivateVersusPublicNewspaperPerPublisher();

		ratioNewspaperHaveAdvertisementVsHavent = this.advertisementService.ratioNewspaperHaveAtLeastOneAdvertisementVSNoAdvertisement();
		ratioAdvertisementHaveTaboo = this.advertisementService.ratioHaveTabooWords();
		avgNewspaperPerVolume = this.newspaperService.averageNewspaperPerVolume();
		ratioSubscriptionVolumeVsNewspaper = this.subscriptionVolumeService.ratioSubscritionVolumeVsSubscriptionNewspaper();

		result = new ModelAndView("dashboard/display");

		result.addObject("newspaperPerUser", newspaperPerUser);
		result.addObject("articlesPerWriter", articlesPerWriter);
		result.addObject("articlesPerNewspaper", articlesPerNewspaper);
		result.addObject("ratioUserCreateNewspaper", ratioUserCreateNewspaper);
		result.addObject("ratioUserWrittenArticle", ratioUserWrittenArticle);

		result.addObject("followUpPerArticle", followUpPerArticle);
		result.addObject("followUpPerArticleOneWeek", followUpPerArticleOneWeek);
		result.addObject("followUpPerArticleTwoWeek", followUpPerArticleTwoWeek);
		result.addObject("chirpPerUser", chirpPerUser);
		result.addObject("ratioUserPostNumberChirps", ratioUserPostNumberChirps);

		result.addObject("ratioPublicVsPrivateNewspaper", ratioPublicVsPrivateNewspaper);
		result.addObject("numberArticlesPerPrivateNewspaper", numberArticlesPerPrivateNewspaper);
		result.addObject("numberArticlesPerPublicNewspaper", numberArticlesPerPublicNewspaper);
		result.addObject("ratioSuscribersPrivateVsTotalCustomer", ratioSuscribersPrivateVsTotalCustomer);
		result.addObject("averageRatioPrivateVsPublicNewspaperPerPublisher", averageRatioPrivateVsPublicNewspaperPerPublisher);
		result.addObject("ratioNewspaperHaveAdvertisementVsHavent", ratioNewspaperHaveAdvertisementVsHavent);
		result.addObject("ratioAdvertisementHaveTaboo", ratioAdvertisementHaveTaboo);
		result.addObject("avgNewspaperPerVolume", avgNewspaperPerVolume);
		result.addObject("ratioSubscriptionVolumeVsNewspaper", ratioSubscriptionVolumeVsNewspaper);
		result.addObject("pageNumber", pageNumber);
		result.addObject("page", page);

		return result;

	}
}
