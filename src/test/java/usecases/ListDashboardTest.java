
package usecases;

import java.util.Map;

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
import services.ArticleService;
import services.ChirpService;
import services.CustomerService;
import services.FollowUpService;
import services.NewspaperService;
import services.UserService;
import utilities.AbstractTest;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListDashboardTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private UserService			userService;

	@Autowired
	private NewspaperService	newspaperService;

	@Autowired
	private FollowUpService		followUpService;

	@Autowired
	private ArticleService		articleService;

	@Autowired
	private ChirpService		chirpService;

	@Autowired
	private CustomerService		customerService;


	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 1. Un usuario autenticado como ADMIN entra en el dashboard.
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
	 * 1. Un usuario autenticado como MANAGER trata de entrar en la vista de dashboard
	 * 2. Un usuario autenticado como USER trata de entrar en la vista de dashboard
	 */
	@Test
	public void testNegativeTest() {
		final Object testingData[][] = {
			{
				"manager1", IllegalArgumentException.class
			}, {
				"user1", IllegalArgumentException.class
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

		Double[] newspaperCreatesPerUser;
		Double[] articlesPerWritter;
		Double[] articlesPerNewspaper;
		Page<Newspaper> moreNewspaper;
		final Page<Newspaper> fewerNewspaper;
		Double ratioUserNewspaper;
		Double ratioUserArticle;
		Double averageFollowArticle;
		Double followOneWeek;
		Double followTwoWeek;
		Double[] chirpPerUser;
		Double ratioUser75Posted;
		Double ratioPrivatePublicNewspaper;
		Double averageArticlesPrivateNewspaper;
		Double averageArticlesPublicNewspaper;
		Map<Newspaper, Double> ratioSuscribers;
		Double privateVsPublicNewspaperPerPublisher;

		caught = null;
		try {

			authority = new Authority();
			authority.setAuthority("ADMIN");

			// 1. Autenticar administrador
			super.authenticate(user);
			
			// Comprobar que el usuario autenticado es un administrador
			Assert.isTrue(LoginService.getPrincipal().getAuthorities().contains(authority));

			// 2. Obtener todos los datos
			//The average and the standard deviation of newspapers created per user.
			newspaperCreatesPerUser = this.newspaperService.avgStandarDevNewspapersCreatedPerUser();
			Assert.isTrue(newspaperCreatesPerUser[0] == 1.0);
			Assert.isTrue(newspaperCreatesPerUser[1] == 0.5773502689009506);

			//The average and the standard deviation of articles written by writer. 
			articlesPerWritter = this.articleService.avgStandartDerivationArticlesPerWriter();
			Assert.isTrue(articlesPerWritter[0] == 0.6667);
			Assert.isTrue(articlesPerWritter[1] == 0.47140452102673397);

			//The average and the standard deviation of articles per newspaper. 
			articlesPerNewspaper = this.articleService.avgStandartDerivationArticlesPerNewspaper();
			Assert.isTrue(articlesPerNewspaper[0] == 0.6667);
			Assert.isTrue(articlesPerNewspaper[1] == 0.47140452102673397);

			//The newspapers that have at least 10% more articles than the average
			moreNewspaper = this.newspaperService.find10PercentageMoreAvg(1, 6);
			Assert.isTrue(moreNewspaper.getContent().size() == 4);

			//The newspapers that have at least 10% fewer articles than the average.
			fewerNewspaper = this.newspaperService.find10PercentageLessAvg(1, 6);
			Assert.isTrue(fewerNewspaper.getContent().size() == 4);

			//The ratio of users who have ever created a newspaper.
			ratioUserNewspaper = this.userService.ratioUsersWhoHaveCreatedNewspaper();
			Assert.isTrue(ratioUserNewspaper == 0.833299994468689);

			//The ratio of users who have ever written an article.
			ratioUserArticle = this.userService.ratioUserWhoHaveWrittenArticle();
			Assert.isTrue(ratioUserArticle == 0.11111111);

			//The average number of follow-ups per article.
			averageFollowArticle = this.followUpService.numberFollowUpPerArticle();
			Assert.isTrue(averageFollowArticle == 1.25);

			//The average number of follow-ups per article up to one week after the corresponding newspaper's been published.
			followOneWeek = this.followUpService.averageFollowUpPerArticleOneWeek();
			Assert.isTrue(followOneWeek == 0.0);

			//The average number of follow-ups per article up to two weeks after the corresponding newspaper's been published.
			followTwoWeek = this.followUpService.averageFollowUpPerArticleTwoWeek();
			Assert.isTrue(followTwoWeek == 0.6667);

			//The average and the standard deviation of the number of chirps per user. 
			chirpPerUser = this.chirpService.avgStandartDeviationNumberChirpsPerUser();
			Assert.isTrue(chirpPerUser[0] == 1.1667);
			Assert.isTrue(chirpPerUser[1] == 0.8975274683509382);

			//The ratio of users who have posted above 75% the average number of chirps per user. 
			ratioUser75Posted = this.userService.ratioUserWhoHavePostedAbove75Chirps();
			Assert.isTrue(ratioUser75Posted == 0.0);

			//The ratio of public versus private newspapers. 
			ratioPrivatePublicNewspaper = this.newspaperService.ratioPublicVsPrivateNewspaper();
			Assert.isTrue(ratioPrivatePublicNewspaper == 0.5);

			//The average number of articles per private newspapers.
			averageArticlesPrivateNewspaper = this.articleService.avgArticlesPerPrivateNewpaper();

			Assert.isTrue(averageArticlesPrivateNewspaper == 0.5);

			//The average number of articles per public newspapers. 
			averageArticlesPublicNewspaper = this.articleService.avgArticlesPerPublicNewpaper();

			Assert.isTrue(averageArticlesPublicNewspaper == 0.75);

			//The ratio of subscribers per private newspaper versus the total number of customers.
			ratioSuscribers = this.customerService.ratioSuscribersPerPrivateNewspaperVersusNumberCustomers(1);
			Assert.isTrue(ratioSuscribers.size() == 5);

			//The average ratio of private versus public newspapers per publisher.
			privateVsPublicNewspaperPerPublisher = this.newspaperService.ratioPrivateVersusPublicNewspaperPerPublisher();
			Assert.isTrue(privateVsPublicNewspaperPerPublisher == 0.0);

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
}
