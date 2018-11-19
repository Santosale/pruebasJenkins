
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
import services.AgentService;
import services.NewspaperService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.Agent;
import domain.Newspaper;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteAdvertisementTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private AdvertisementService	advertisementService;

	@Autowired
	private NewspaperService		newspaperService;

	@Autowired
	private AgentService			agentService;


	// Tests ------------------------------------------------------------------

	//--- AGENT ----
	//Pruebas
	/*
	 * 1. Delete advertisement enlazado a newspaper con agent1.
	 * 2. Delete advertisement sin enlazar con agent2.
	 */

	@Test
	public void driverPositivePerAgent() {

		//Rol,advertisement, newspaper, ExpectedException
		final Object testingData2[][] = {
			{
				"agent1", "advertisement1", "newspaper1", null
			}, {
				"agent2", "advertisement6", null, null
			},
		};

		for (int i = 0; i < testingData2.length; i++)

			this.templateDeletePerAgent((String) testingData2[i][0], (String) testingData2[i][1], (String) testingData2[i][2], (Class<?>) testingData2[i][3]);

	}

	//Pruebas
	/*
	 * 1. Eliminar advertisement sin autenticarse.IllegalArgumentException
	 * 2. Eliminar advertisement autenticandose como otro agent distinto al creador. IllegalArgumentException
	 * 3. Eliminar advertisement con otro rol. IllegalArgumentException
	 * 4. Eliminar advertisement autenticandose como otro agent y cambiando el agent. IllegalArgumentException
	 */

	@Test
	public void driverNegativePerAgent() {

		//Rol,advertisement, Exception
		final Object testingData[][] = {
			{
				null, "advertisement1", false, IllegalArgumentException.class
			}, {
				"agent2", "advertisement1", false, IllegalArgumentException.class
			}, {
				"customer2", "advertisement4", false, IllegalArgumentException.class
			}, {
				"agent2", "advertisement4", true, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteNegativePerAgent((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los advertisement de un usuario.
	 * 3. Navegar y escoger el advertisement deseado.
	 * 4. Borrar advertisement deseado.
	 */
	protected void templateDeletePerAgent(final String userName, final String advertisementName, final String newspaperName, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement advertisement;
		Advertisement advertisementChoosen;
		int countAdvertisement;
		int advertisementId;
		Integer countAdvertisementNewspaper;
		Newspaper newspaper;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Sacamos cuantos advertisement tiene asociado el newspaper
			countAdvertisementNewspaper = null;
			if (newspaperName != null) {

				newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
				countAdvertisementNewspaper = newspaper.getAdvertisements().size();
			}

			//Obtenemos los objetos que queremos
			advertisementId = super.getEntityId(advertisementName);

			advertisement = this.advertisementService.findOne(advertisementId);

			//Inicializamos
			advertisementChoosen = null;

			//Obtenemos la colección de los advertisements
			advertisements = this.advertisementService.findByAgentId(1, 1);
			countAdvertisement = advertisements.getTotalPages();

			//Buscamos el advertisement
			for (int i = 0; i < countAdvertisement; i++) {

				advertisements = this.advertisementService.findByAgentId(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (advertisements.getContent().size() == 0)
					break;

				//3. Navegar hasta el advertisement que queremos.
				for (final Advertisement newAdvertisement : advertisements.getContent())
					if (newAdvertisement.equals(advertisement)) {
						advertisementChoosen = newAdvertisement;
						break;
					}

				if (advertisementChoosen != null)
					break;
			}

			//Ya tenemos el advertisement
			Assert.notNull(advertisementChoosen);

			//Borramos el follow up
			this.advertisementService.delete(advertisementChoosen);

			this.advertisementService.flush();

			//Comprobamos que se ha borrado
			Assert.isNull(this.advertisementService.findOne(advertisementId));

			//Si estaba enlazado a algún newspaper
			if (countAdvertisementNewspaper != null) {
				newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
				Assert.isTrue((countAdvertisementNewspaper - 1) == newspaper.getAdvertisements().size());
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
	 * 1. Autenticarnos.
	 * 2. Delete advertisement por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDeleteNegativePerAgent(final String userName, final String advertisementName, final Boolean changeAgent, final Class<?> expected) {
		Class<?> caught;

		Advertisement advertisement;
		int advertisementId;
		Agent agent;
		int agentId;
		int countAfter;
		int countBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			countBefore = this.advertisementService.findAll().size();

			advertisementId = super.getEntityId(advertisementName);
			advertisement = this.advertisementService.findOne(advertisementId);

			//Vemos si cambiamos el agent
			if (changeAgent) {
				agentId = super.getEntityId(userName);
				agent = this.agentService.findOne(agentId);
				advertisement.setAgent(agent);
			}

			this.advertisementService.delete(advertisement);

			this.advertisementService.flush();

			//Comprobamos que No se puede
			countAfter = this.advertisementService.findAll().size();
			Assert.isNull(countAfter == countBefore);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	//--- ADMIN ----
	//Pruebas
	/*
	 * 1. Delete advertisement enlazado a newspaper con admin.
	 * 2. Delete advertisement sin enlazar con admin.
	 */
	//Requisito:
	//CU 5.2: Remove an advertisement that he or she thinks is inappropriate
	@Test
	public void driverPositivePerAdmin() {

		//Rol,advertisement, newspaper, numTabooExpectedBefore, numTabooExpectedAfter,  ExpectedException
		final Object testingData2[][] = {
			{
				"admin", "advertisement1", "newspaper1", null
			}, {
				"admin", "advertisement6", null, null
			},
		};

		for (int i = 0; i < testingData2.length; i++)

			this.templateDeletePerAdmin((String) testingData2[i][0], (String) testingData2[i][1], (String) testingData2[i][2], (Class<?>) testingData2[i][3]);

	}

	//Pruebas
	/*
	 * 1. Eliminar advertisement sin autenticarse.IllegalArgumentException
	 * 2. Eliminar advertisement con otro rol. IllegalArgumentException
	 */
	//Requisito:
	//CU 5.2: Remove an advertisement that he or she thinks is inappropriate
	@Test
	public void driverNegativePerAdmin() {

		//Rol,advertisement, Exception
		final Object testingData[][] = {
			{
				null, "advertisement1", IllegalArgumentException.class
			}, {
				"customer2", "advertisement4", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteNegativePerAdmin((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los advertisement.
	 * 3. Navegar y escoger el advertisement deseado.
	 * 4. Borrar advertisement deseado.
	 */
	protected void templateDeletePerAdmin(final String userName, final String advertisementName, final String newspaperName, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement advertisement;
		Advertisement advertisementChoosen;
		int countAdvertisement;
		int advertisementId;
		Integer countAdvertisementNewspaper;
		Newspaper newspaper;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Sacamos cuantos advertisement tiene asociado el newspaper
			countAdvertisementNewspaper = null;
			if (newspaperName != null) {

				newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
				countAdvertisementNewspaper = newspaper.getAdvertisements().size();
			}

			//Obtenemos los objetos que queremos
			advertisementId = super.getEntityId(advertisementName);

			advertisement = this.advertisementService.findOne(advertisementId);

			//Inicializamos
			advertisementChoosen = null;

			//Obtenemos la colección de los advertisements
			advertisements = this.advertisementService.findAllPaginated(1, 1);
			countAdvertisement = advertisements.getTotalPages();

			//Buscamos el advertisement
			for (int i = 0; i < countAdvertisement; i++) {

				advertisements = this.advertisementService.findAllPaginated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (advertisements.getContent().size() == 0)
					break;

				//3. Navegar hasta el advertisement que queremos.
				for (final Advertisement newAdvertisement : advertisements.getContent())
					if (newAdvertisement.equals(advertisement)) {
						advertisementChoosen = newAdvertisement;
						break;
					}

				if (advertisementChoosen != null)
					break;
			}

			//Ya tenemos el advertisement
			Assert.notNull(advertisementChoosen);

			//Borramos el follow up
			this.advertisementService.deleteInappropiate(advertisementChoosen);

			this.advertisementService.flush();

			//Comprobamos que se ha borrado
			Assert.isNull(this.advertisementService.findOne(advertisementId));

			//Si estaba enlazado a algún newspaper
			if (countAdvertisementNewspaper != null) {
				newspaper = this.newspaperService.findOne(super.getEntityId(newspaperName));
				Assert.isTrue((countAdvertisementNewspaper - 1) == newspaper.getAdvertisements().size());
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
	 * 1. Autenticarnos.
	 * 2. Delete advertisement por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDeleteNegativePerAdmin(final String userName, final String advertisementName, final Class<?> expected) {
		Class<?> caught;

		Advertisement advertisement;
		int advertisementId;
		int countAfter;
		int countBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			countBefore = this.advertisementService.findAll().size();

			advertisementId = super.getEntityId(advertisementName);
			advertisement = this.advertisementService.findOne(advertisementId);

			this.advertisementService.deleteInappropiate(advertisement);

			this.advertisementService.flush();

			//Comprobamos que No se puede
			countAfter = this.advertisementService.findAll().size();
			Assert.isNull(countAfter == countBefore);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
