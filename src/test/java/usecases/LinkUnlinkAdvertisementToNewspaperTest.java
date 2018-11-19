
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.AdvertisementService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class LinkUnlinkAdvertisementToNewspaperTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private NewspaperService		newspaperService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Link advertisement a newspaper público con agent1
	 * 2. Link advertisement a newspaper privado con agent2
	 */
	//Requisitos
	//CU 4.2: Place an advertisement in a newspaper.
	@Test
	public void driverLinkToNewspaper() {

		//Rol, advertisement, newspaper, action, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "advertisement5", "newspaper1", "link", null
			}, {
				"agent2", "advertisement6", "newspaper2", "link", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateLinkUnlinkNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	/*
	 * 1. Unlink advertisement a newspaper público con agent1
	 * 2. Unlink advertisement a newspaper privado con agent1
	 */
	//Requisitos
	//CU 4.2: Place an advertisement in a newspaper.
	@Test
	public void driverUnLinkToNewspaper() {

		//Rol, advertisement, newspaper, action, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "advertisement1", "newspaper1", "unlink", null
			}, {
				"agent1", "advertisement2", "newspaper2", "unlink", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateLinkUnlinkNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	/*
	 * 1. Link advertisement a newspaper, cuando ya existe este link
	 * 2. Unlink advertisement a newspaper, cuando no existe el link
	 * 3. Link advertisement a newspaper que no es del agent
	 * 4. Unlink advertisement a newspaper que no es del agent
	 * 5. Link advertisement a newspaper sin autenticar
	 */
	//Requisitos
	//CU 4.2: Place an advertisement in a newspaper.
	@Test
	public void driverUnLinkToNewspaperURL() {

		//Rol, advertisement, newspaper, action, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "advertisement1", "newspaper1", "link", IllegalArgumentException.class
			}, {
				"agent2", "advertisement6", "newspaper2", "unlink", IllegalArgumentException.class
			}, {
				"agent1", "advertisement6", "newspaper2", "link", IllegalArgumentException.class
			}, {
				"agent2", "advertisement1", "newspaper1", "unlink", IllegalArgumentException.class
			}, {
				null, "advertisement1", "newspaper4", "link", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateLinkUnlinkNewspaper((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los newspapers.
	 * 3. Navegar y escoger el newspaper deseado.
	 * 4. Link o Unlink un advertisement
	 */
	protected void templateLinkUnlinkNewspaper(final String userName, final String advertisementName, final String newspaperName, final String action, final Class<?> expected) {
		Class<?> caught;

		Page<Newspaper> newspapers;
		Newspaper newspaper;
		Newspaper newspaperChoosen;
		int countNewspaper;
		int numberAdvertisementBefore;
		int numberAdvertisementAfter;
		Advertisement advertisement;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
			advertisement = this.advertisementService.findOne(super.getEntityId(advertisementName));

			//Guardamos el número
			numberAdvertisementBefore = newspaper.getAdvertisements().size();
			if (action.equals("unlink"))
				Assert.isTrue(newspaper.getAdvertisements().contains(advertisement));
			else
				Assert.isTrue(!newspaper.getAdvertisements().contains(advertisement));

			//Inicializamos
			newspaperChoosen = null;

			//Obtenemos la colección de los newspaper
			newspapers = this.newspaperService.findAllPaginated(1, 1);
			countNewspaper = newspapers.getTotalPages();

			//Buscamos el newspaper
			for (int i = 0; i < countNewspaper; i++) {

				newspapers = this.newspaperService.findAllPaginated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (newspapers.getContent().size() == 0)
					break;

				//3. Navegar hasta el newspaper que queremos que queremos.
				for (final Newspaper newNewspaper : newspapers.getContent())
					if (newNewspaper.equals(newspaper)) {
						newspaperChoosen = newNewspaper;
						break;
					}

				if (newspaperChoosen != null)
					break;
			}

			//Ya tenemos el newspaper
			Assert.notNull(newspaperChoosen);

			if (action.equals("link"))
				this.newspaperService.addAdvertisementToNewspaper(advertisement.getId(), newspaperChoosen.getId());
			else
				this.newspaperService.deleteAdvertisementToNewspaper(advertisement.getId(), newspaperChoosen.getId());

			this.newspaperService.flush();

			//Guardamos el número
			newspaper = this.newspaperService.findOne(newspaperChoosen.getId());
			numberAdvertisementAfter = newspaper.getAdvertisements().size();

			if (action.equals("link")) {
				Assert.isTrue(numberAdvertisementBefore + 1 == numberAdvertisementAfter);
				Assert.isTrue(newspaper.getAdvertisements().contains(advertisement));
			} else {
				Assert.isTrue(numberAdvertisementBefore == numberAdvertisementAfter + 1);
				Assert.isTrue(!newspaper.getAdvertisements().contains(advertisement));
			}

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1. Link o Unlink un advertisement por URL
	 */
	protected void templateLinkUnlinkNewspaperURL(final String userName, final String advertisementName, final String newspaperName, final String action, final Class<?> expected) {
		Class<?> caught;

		Newspaper newspaper;
		Advertisement advertisement;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
			advertisement = this.advertisementService.findOne(super.getEntityId(advertisementName));

			if (action.equals("link"))
				this.newspaperService.addAdvertisementToNewspaper(advertisement.getId(), newspaper.getId());
			else
				this.newspaperService.deleteAdvertisementToNewspaper(advertisement.getId(), newspaper.getId());

			this.newspaperService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}
}
