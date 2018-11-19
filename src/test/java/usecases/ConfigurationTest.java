
package usecases;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AdvertisementService;
import services.ArticleService;
import services.ChirpService;
import services.ConfigurationService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Article;
import domain.Chirp;
import domain.Configuration;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ConfigurationTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ChirpService			chirpService;

	@Autowired
	private ArticleService			articleService;

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private AdvertisementService	advertisementService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Borramos una taboo word, el chirp se debe no marcar como taboo.
	 * 2. Borramos una taboo word, el newspaper se debe no marcar como taboo.
	 * 3. Guardamos una taboo word, el article se debe no marcar como taboo.
	 * 4. Dejar mismas tabooWords, ver que no cambia el valor en un newspaper;
	 * 5. Borramos una taboo word, el advertisement se debe no marcar como taboo.
	 * 6. Guardamos una taboo word, el advertisement se debe no marcar como taboo.
	 */

	@Test
	public void driverPositive() {

		//rol, entityName, lastEntityName, tabooToDelete, tabooToAdd, entityTest, expected) {
		final Object testingData[][] = {
			{
				"admin", "chirp6", "delete", "sex", "", "chirp", null
			}, {
				"admin", "newspaper2", "delete", "sex", "", "newspaper", null
			}, {
				"admin", "article3", "add", "", "felicidad", "article", null
			}, {
				"admin", "newspaper4", "equal", "", "", "newspaper", null
			}, {
				"admin", "advertisement1", "delete", "sex", "", "advertisement", null
			}, {
				"admin", "advertisement3", "add", "", "Lanjarón", "advertisement", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	//Pruebas
	/*
	 * 1. List taboo word.
	 * 2. List taboo word autenticado con otro actor.
	 */

	@Test
	public void listTabooWord() {

		//rol, expected) {
		final Object testingData[][] = {
			{
				"admin", null
			}, {
				"customer1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListTabooWord((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}
	//Pruebas
	/*
	 * 1. Edita otro actor
	 */

	@Test
	public void driverNegative() {

		//rol, entityName, lastEntityName, tabooToDelete, tabooToAdd, entityTest, expected) {
		final Object testingData[][] = {
			{
				"customer1", "newspaper2", "delete", "sex", "", "newspaper", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	/*
	 * 1. Autenticarnos como admin.
	 * 2. Acceder a la configuración.
	 */
	public void templateListTabooWord(final String userName, final Class<?> expected) {
		Configuration configuration;
		Class<?> caught;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Sacamos la configuration
			configuration = this.configurationService.findUnique();
			Assert.isTrue(configuration.getTabooWords().size() == 4);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Acceder a la configuración.
	 * 3. Editar configuración
	 */
	protected void templateEditConfiguration(final String username, final String entityName, final String actionName, final String tabooToDelete, final String tabooToAdd, final String entityTest, final Class<?> expected) {
		Class<?> caught;

		Article article;
		int entityId;
		Chirp chirp;
		Advertisement advertisement;
		Newspaper newspaper;
		Configuration configuration;
		boolean auxiliar;
		boolean auxiliarBefore;
		List<String> tabooWords;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(username);

			//Sacamos la configuration
			configuration = this.configurationService.findUnique();
			tabooWords = new ArrayList<String>();
			tabooWords.addAll(configuration.getTabooWords());

			//Metemos las nuevas palabras
			for (final String newTaboo : tabooToAdd.split(","))
				if (!newTaboo.equals(""))
					tabooWords.add(newTaboo);

			//Sacamos las nuevas palabras
			for (final String deleteTaboo : tabooToDelete.split(","))
				if (!deleteTaboo.equals(""))
					tabooWords.remove(deleteTaboo);

			configuration.setTabooWords(tabooWords);
			entityId = super.getEntityId(entityName);

			if (entityTest.equals("chirp")) {
				chirp = this.chirpService.findOne(entityId);
				auxiliarBefore = chirp.getHasTaboo();

			} else if (entityTest.equals("newspaper")) {
				newspaper = this.newspaperService.findOne(entityId);
				auxiliarBefore = newspaper.getHasTaboo();

			} else if (entityTest.equals("advertisement")) {
				advertisement = this.advertisementService.findOne(entityId);
				auxiliarBefore = advertisement.getHasTaboo();

			} else {

				article = this.articleService.findOne(entityId);
				auxiliarBefore = article.getHasTaboo();
			}

			this.configurationService.save(configuration);
			this.configurationService.flush();
			this.configurationService.updateTabooWords();
			this.configurationService.flush();

			//Comprobamos la acción
			if (entityTest.equals("chirp")) {
				chirp = this.chirpService.findOne(entityId);
				auxiliar = chirp.getHasTaboo();

			} else if (entityTest.equals("newspaper")) {
				newspaper = this.newspaperService.findOne(entityId);
				auxiliar = newspaper.getHasTaboo();

			} else if (entityTest.equals("advertisement")) {
				advertisement = this.advertisementService.findOne(entityId);
				auxiliar = advertisement.getHasTaboo();

			} else {

				article = this.articleService.findOne(entityId);
				auxiliar = article.getHasTaboo();
			}

			//Vemos la acción
			if (actionName.equals("add"))
				Assert.isTrue(auxiliar == true && auxiliarBefore == false);
			else if (actionName.equals("delete"))
				Assert.isTrue(auxiliar == false && auxiliarBefore == true);
			else
				Assert.isTrue(auxiliarBefore == auxiliar);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}
}
