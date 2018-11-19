
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
import domain.Sponsorship;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteSponsorshipTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private SponsorshipService	sponsorshipService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Borrar Sponsorship de un bargain que tiene mas de un sponsorship.
	 * 2. Borrar Sponsorship de un bargain que tiene un sponsorship.
	 */

	//Requisitos
	//CU 24 1.Un patrocinador puede promocionarse en un chollo. El sistema debe guardar el chollo que va a patrocinar con su correspondiente aportación monetaria.
	@Test
	public void driverPositive() {

		//userName, sponsorshipName, expected
		final Object testingData[][] = {
			{
				"sponsor1", "sponsorship1", null
			}, {
				"sponsor1", "sponsorship11", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDelete((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	//Pruebas
	/*
	 * 1. Borrar Sponsorship sin autenticar.IllegalArgumentException
	 * 2. Borrar Sponsorship autenticado con moderator. IllegalArgumentException
	 * 3. Borrar Sponsorship de otro sponsor. IllegalArgumentException
	 */
	//Requisitos
	//CU 24 1.Un patrocinador puede promocionarse en un chollo. El sistema debe guardar el chollo que va a patrocinar con su correspondiente aportación monetaria.
	@Test
	public void driverNegative() {

		//userName,sponsorshipName, expected
		final Object testingData[][] = {
			{
				null, "sponsorship1", IllegalArgumentException.class
			}, {
				"moderator1", "sponsorship1", IllegalArgumentException.class
			}, {
				"sponsor2", "sponsorship1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los sponsorships del sponsor.
	 * 3. Seleccionar uno.
	 * 4. Borrarlo.
	 */

	protected void templateDelete(final String userName, final String sponsorshipName, final Class<?> expected) {
		Class<?> caught;

		Page<Sponsorship> sponsorships;
		Integer countSponsorships;
		Sponsorship sponsorshipChoosen;
		Bargain bargain;
		Sponsorship sponsorship;
		double priceBeforeSponsorship;
		double minimum;
		double priceBefore;
		int sponsorshipId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			sponsorshipChoosen = null;
			sponsorship = this.sponsorshipService.findOne(this.getEntityId(sponsorshipName));
			bargain = sponsorship.getBargain();
			minimum = bargain.getMinimumPrice();
			priceBefore = bargain.getPrice();
			priceBeforeSponsorship = sponsorship.getAmount();
			sponsorshipId = sponsorship.getId();

			//Obtenemos los sponsorships
			sponsorships = this.sponsorshipService.findBySponsorId(1, 1);
			Assert.notNull(sponsorships);
			countSponsorships = sponsorships.getTotalPages();

			//Buscamos el sponsorship
			for (int i = 0; i < countSponsorships; i++) {

				sponsorships = this.sponsorshipService.findBySponsorId(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (sponsorships.getContent().size() == 0)
					break;

				// Navegar hasta el sponsorship que queremos.
				for (final Sponsorship newSponsorship : sponsorships.getContent())
					if (newSponsorship.equals(sponsorship)) {
						sponsorshipChoosen = newSponsorship;
						break;
					}

				if (sponsorshipChoosen != null)
					break;
			}

			//Ya tenemos el sponsorship
			Assert.notNull(sponsorshipChoosen);

			//Borramos sponsorship

			this.sponsorshipService.delete(sponsorshipChoosen);

			this.sponsorshipService.flush();

			Assert.isNull(this.sponsorshipService.findOne(sponsorshipId));

			//Comprobamos que se actualice bn el precio
			Assert.isTrue(this.bargainService.findOne(bargain.getId()).getPrice() == Math.max(minimum, priceBefore + priceBeforeSponsorship));

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
	 * 2. Delete sponsorship por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDeleteUrl(final String userName, final String sponsorshipName, final Class<?> expected) {
		Class<?> caught;

		Sponsorship sponsorship;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			sponsorship = this.sponsorshipService.findOne(this.getEntityId(sponsorshipName));

			//Borramos sponsorship

			this.sponsorshipService.delete(sponsorship);

			this.sponsorshipService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
