
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.Authority;
import security.LoginService;
import services.BargainService;
import services.CategoryService;
import services.CompanyService;
import services.GrouponService;
import services.NotificationService;
import services.SponsorshipService;
import services.SurveyService;
import services.TagService;
import services.TicketService;
import services.UserService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Category;
import domain.Company;
import domain.Groupon;
import domain.Survey;
import domain.User;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListDashboardTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private CompanyService		companyService;

	@Autowired
	private BargainService		bargainService;

	@Autowired
	private GrouponService		grouponService;

	@Autowired
	private UserService			userService;

	@Autowired
	private SurveyService		surveyService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private TagService			tagService;

	@Autowired
	private NotificationService	notificationService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private TicketService		ticketService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Un usuario autenticado como ADMIN entra en el dashboard.
	 * 
	 * Requisito 30.5: Un actor autenticado como administrador debe ser capaz de mostrar un dashboard con la siguiente información
	 * Mínimo, máximo, media y desviación estándar de los banners por cada patrocinador.
	 * La media de la ratio de etiquetas por chollos.
	 * Clasificación de los chollos por su presencia en listas de deseos.
	 * Las encuesta/s más popular/es
	 * Ratio de notificaciones vistas sobre el total.
	 * Media de usuarios que tienen una participación en una rifa respecto al total.
	 * Las empresas que han realizado más del 15%, 10% y 5% de las encuestas llevadas a cabo por empresas.
	 * Top-5 usuarios que más valoraciones han realizado.
	 * Ratio de usuarios que realizan comentarios.
	 * Usuarios que han realizado más del 10% de interacciones con el sistema.
	 * Categorías con más chollos que la media.
	 * Las empresas que proveen más etiquetas a sus chollos.
	 * La media del ratio de chollos por categorías.
	 * Usuario con mayor media de número de caracteres escritos en sus valoraciones a las empresas.
	 * Conjuntas con un 10% de más participación que la media.
	 * Mínimo, máximo, media y desviación estándar del descuento de un chollo.
	 * Mínimo, máximo, media y desviación estándar del descuento de una conjunta.
	 * Usuario que más rifas ha ganado.
	 * Usuario que ha comprado más tiques para una rifa y el que menos.
	 * Media de tiques por cada una de las rifa.
	 * Usuario que ha comprado más del 25% del total de tiques vendidos en total para todas las rifas.
	 */
	@Test
	public void testPositiveTest() {
		final Object testingData[][] = {
			{
				"admin", null
			},
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Un usuario autenticado como SPONSOR trata de entrar en la vista de dashboard
	 * 2. Un usuario autenticado como USER trata de entrar en la vista de dashboard
	 * 3. Un usuario autenticado como COMPANY trata de entrar en la vista de dashboard
	 * 4. Un usuario autenticado como MODERATOR trata de entrar en la vista de dashboard
	 * 5. Un usuario sin estar autenticado trata de entrar en la vista de dashboard
	 */
	@Test
	public void testNegativeTest() {
		final Object testingData[][] = {
			{
				"sponsor1", IllegalArgumentException.class
			}, {
				"user1", IllegalArgumentException.class
			}, {
				"company1", IllegalArgumentException.class
			}, {
				"moderator1", IllegalArgumentException.class
			}, {
				null, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (Class<?>) testingData[i][1]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Listar las queries en Dashboard. Pasos:
	 * 1. Autenticar administrador
	 * 2. Obtener todos los datos
	 */
	protected void template(final String user, final Class<?> expected) {
		Class<?> caught;
		Authority authority;

		Double[] avgMinMaxStandarDesviationBannersPerSponsor;
		double avgRatioTagsPerBargain;
		Page<Bargain> listWithMoreSponsorships;
		Page<Bargain> listWithLessSponsorships;
		Page<Bargain> findAreInMoreWishList;
		Page<Survey> surveyMorePopular;
		Double ratioNotificationsPerTotal;
		Double avgUsersWithParticipationsPerTotal;
		Page<Company> findWithMoreAvgPercentageSurveys15;
		Page<Company> findWithMoreAvgPercentageSurveys10;
		Page<Company> findWithMoreAvgPercentageSurveys5;
		Page<User> topFiveUsersMoreValorations;
		Double ratioUsersWithComments;
		Page<User> more10PercentageInteractions;
		Page<Category> moreBargainThanAverage;
		Page<Company> companiesWithMoreTags;
		Double avgRatioBargainPerCategory;
		Page<User> moreAverageCharacterLenght;
		Page<Groupon> tenPercentageMoreParticipationsThanAverage;
		Double[] minMaxAvgStandarDesviationDiscountPerBargain;
		Double[] minMaxAvgStandarDesviationDiscountPerGroupon;
		Page<User> moreWonRaffles;
		Page<User> purchaseMoreTickets;
		Page<User> purchaseLessTickets;
		Double avgTicketsPurchaseByUsersPerRaffle;
		Page<User> purchase25PercentageMoreTotalForAllRaffles;

		caught = null;
		try {

			authority = new Authority();
			authority.setAuthority("ADMIN");

			// 1. Autenticar administrador
			super.authenticate(user);

			// Comprobar que el usuario autenticado es un administrador
			Assert.isTrue(LoginService.isAuthenticated() && LoginService.getPrincipal().getAuthorities().contains(authority));

			// 2. Obtener todos los datos
			//Mínimo, máximo, media y desviación estándar de los banners por cada patrocinador.
			avgMinMaxStandarDesviationBannersPerSponsor = this.sponsorshipService.avgMinMaxStandarDesviationBannersPerSponsor();
			Assert.isTrue(avgMinMaxStandarDesviationBannersPerSponsor[0] == 3.25);
			Assert.isTrue(avgMinMaxStandarDesviationBannersPerSponsor[1] == 1);
			Assert.isTrue(avgMinMaxStandarDesviationBannersPerSponsor[2] == 7);
			Assert.isTrue(avgMinMaxStandarDesviationBannersPerSponsor[3] == 2.277608394786075);

			//La media de la ratio de etiquetas por chollos.
			avgRatioTagsPerBargain = this.tagService.avgRatioTagsPerBargain();
			Assert.isTrue(avgRatioTagsPerBargain == 0.166666666);

			// Chollo con más y menos patrocinadores. 
			listWithMoreSponsorships = this.bargainService.listWithMoreSponsorships(1, 10);
			listWithLessSponsorships = this.bargainService.listWithLessSponsorships(1, 10);
			Assert.isTrue(listWithMoreSponsorships.getContent().size() == 1);
			Assert.isTrue(listWithLessSponsorships.getContent().size() == 5);

			//Los chollos que estén en más listas de  deseo
			findAreInMoreWishList = this.bargainService.findAreInMoreWishList(1, 20);
			Assert.isTrue(findAreInMoreWishList.getContent().size() == 12);

			//Encuesta/s mas popular/es.
			surveyMorePopular = this.surveyService.surveyMorePopular(1, 10);
			Assert.isTrue(surveyMorePopular.getContent().size() == 2);

			//Ratio de notificaciones vistas sobre el total.
			ratioNotificationsPerTotal = this.notificationService.ratioNotificationsPerTotal();
			Assert.isTrue(ratioNotificationsPerTotal == 0.7142999768257141);

			//Media de usuarios que tienen una participación en una rifa respecto al total.
			avgUsersWithParticipationsPerTotal = this.userService.avgUsersWithParticipationsPerTotal();
			Assert.isTrue(avgUsersWithParticipationsPerTotal == 0.30952381);

			//Las empresas que han realizado más del 15%, 10% y 5% de las encuestas llevadas a  cabo por empresas.
			findWithMoreAvgPercentageSurveys15 = this.companyService.findWithMoreAvgPercentageSurveys15(1, 10);
			findWithMoreAvgPercentageSurveys10 = this.companyService.findWithMoreAvgPercentageSurveys10(1, 10);
			findWithMoreAvgPercentageSurveys5 = this.companyService.findWithMoreAvgPercentageSurveys5(1, 10);
			Assert.isTrue(findWithMoreAvgPercentageSurveys15.getContent().size() == 2);
			Assert.isTrue(findWithMoreAvgPercentageSurveys10.getContent().size() == 2);
			Assert.isTrue(findWithMoreAvgPercentageSurveys5.getContent().size() == 2);

			//Top-5 usuarios que más valoraciones han  realizado.
			topFiveUsersMoreValorations = this.userService.topFiveUsersMoreValorations(1, 5);
			Assert.isTrue(topFiveUsersMoreValorations.getContent().size() == 5);

			//Ratio de usuarios que realizan  comentarios.
			ratioUsersWithComments = this.userService.ratioUsersWithComments();
			Assert.isTrue(ratioUsersWithComments == 0.5);

			//Usuarios que han realizado más del 10% de interacciones con el sistema. 
			more10PercentageInteractions = this.userService.more10PercentageInteractions(1, 10);
			Assert.isTrue(more10PercentageInteractions.getContent().size() == 3);

			//Categorías con más chollos que la  media. 
			moreBargainThanAverage = this.categoryService.moreBargainThanAverage(1, 20);
			Assert.isTrue(moreBargainThanAverage.getContent().size() == 7);

			//Las empresas que proveen más etiquetas  a sus chollos. 
			companiesWithMoreTags = this.companyService.companiesWithMoreTags(1, 10);
			Assert.isTrue(companiesWithMoreTags.getContent().size() == 6);

			//La media del ratio de chollos por  categorías.
			avgRatioBargainPerCategory = this.bargainService.avgRatioBargainPerCategory();

			Assert.isTrue(avgRatioBargainPerCategory == 0.078947368);

			//Usuario con mayor media de número de caracteres escritos en sus valoraciones a las  empresas. 
			moreAverageCharacterLenght = this.userService.moreAverageCharacterLenght(1, 10);
			Assert.isTrue(moreAverageCharacterLenght.getContent().size() == 1);

			//Conjuntas con un 10% de más  participación que la media.
			tenPercentageMoreParticipationsThanAverage = this.grouponService.tenPercentageMoreParticipationsThanAverage(1, 10);
			Assert.isTrue(tenPercentageMoreParticipationsThanAverage.getContent().size() == 6);

			//Mínimo, máximo, media y desviación estándar del descuento de un chollo.
			minMaxAvgStandarDesviationDiscountPerBargain = this.bargainService.minMaxAvgStandarDesviationDiscountPerBargain();
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerBargain[0] == 0.0);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerBargain[1] == 0.6);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerBargain[2] == 0.1550925925925926);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerBargain[3] == 0.1891944469367816);

			//Mínimo, máximo, media y desviación estándar del descuento de una conjunta
			minMaxAvgStandarDesviationDiscountPerGroupon = this.grouponService.minMaxAvgStandarDesviationDiscountPerGroupon();
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerGroupon[0] == 0.14285714285714285);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerGroupon[1] == 0.6551724137931034);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerGroupon[2] == 0.3469873578452313);
			Assert.isTrue(minMaxAvgStandarDesviationDiscountPerGroupon[3] == 0.1807530722380895);

			//Usuario que más rifas ha ganado
			moreWonRaffles = this.userService.moreWonRaffles(1, 10);
			Assert.isTrue(moreWonRaffles.getContent().size() == 1);

			//Usuario que ha comprado más tiques para una rifa y el que menos
			purchaseMoreTickets = this.userService.purchaseMoreTickets(1, 10);
			purchaseLessTickets = this.userService.purchaseLessTickets(1, 10);
			Assert.isTrue(purchaseMoreTickets.getContent().size() == 1);
			Assert.isTrue(purchaseLessTickets.getContent().size() == 2);

			//Media de tiques comprados por los usuarios para una rifa
			avgTicketsPurchaseByUsersPerRaffle = this.ticketService.avgTicketsPurchaseByUsersPerRaffle();
			Assert.isTrue(avgTicketsPurchaseByUsersPerRaffle == 2.2143);

			//Usuario que ha comprado más del 25% del total de tiques vendidos en total para todas  las rifas
			purchase25PercentageMoreTotalForAllRaffles = this.userService.purchase25PercentageMoreTotalForAllRaffles(1, 10);
			Assert.isTrue(purchase25PercentageMoreTotalForAllRaffles.getContent().size() == 2);

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
}
