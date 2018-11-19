
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.BargainService;
import services.SponsorshipService;
import utilities.AbstractTest;
import domain.Bargain;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListAndDisplaySponsorshipTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private BargainService		bargainService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Listar los sponsorships de un bargain publico sin autenticar
	 * 2. Listar los sponsorships de un bargain publico autenticado como user no gold
	 * 3. Listar los sponsorships de un bargain no publico autenticado como user gold
	 * 4. Listar los sponsorships de un bargain no publico autenticado como la company del bargain
	 */
	//CU: listar sponsorships
	@Test
	public void driverPositive() {

		//userName, bargainName, expected
		final Object testingData[][] = {
			{
				null, "bargain1", null
			}, {
				"user1", "bargain1", null
			}, {
				"user2", "bargain12", null
			}, {
				"company2", "bargain12", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Listar sponsorships de un bargain no publico sin autenticar. IllegalArgumentException
	 * 2. Listar sponsorships de un bargain no publico autenticado como user no gold. IllegalArgumentException
	 * 3. Listar sponsorships de un bargain no publico autenticado como company distinta a la suya. IllegalArgumentException
	 */
	//CU: listar sponsorships
	@Test
	public void driverDeleteNegative() {

		//userName, notificationName, expected
		final Object testingData[][] = {
			{
				null, "bargain12", IllegalArgumentException.class
			}, {
				"user1", "bargain12", IllegalArgumentException.class
			}, {
				"company1", "bargain12", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDisplayUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar Bargains
	 * 3. Acceder al bargain
	 * 4. Listar sponsorships.
	 */

	protected void templateListDisplay(final String userName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			//Obtenemos los bargains
			bargains = this.bargainService.findBargains(1, 1, "all", 0);

			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {
				bargains = this.bargainService.findBargains(i + 1, 5, "all", 0);

				//Si estamos pidiendo una página mayor
				if (bargains.getContent().size() == 0)
					break;

				// Navegar hasta el bargain que queremos.
				for (final Bargain newBargain : bargains.getContent())
					if (newBargain.equals(bargain)) {
						bargainChoosen = newBargain;
						break;
					}

				if (bargainChoosen != null)
					break;
			}

			Assert.notNull(bargainChoosen);

			Assert.isTrue(this.bargainService.canDisplay(bargainChoosen));
			this.sponsorshipService.findByBargainIdPageable(bargainChoosen.getId(), 1, 5);

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
	 * 2. Acceder a sponsorships por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDisplayUrl(final String userName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Bargain bargain;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			Assert.isTrue(this.bargainService.canDisplay(bargain));
			this.sponsorshipService.findByBargainIdPageable(bargain.getId(), 1, 5);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
