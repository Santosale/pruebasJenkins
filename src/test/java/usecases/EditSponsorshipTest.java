
package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

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
public class EditSponsorshipTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private SponsorshipService	sponsorshipService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Editar Sponsorship de un bargain que tiene mas de un sponsorship.
	 * 2. Editar Sponsorship de un bargain que tiene un sponsorship.
	 */

	//Requisitos
	//CU Editar patrocinador por sponsor.
	@Test
	public void driverPositive() {

		//userName, amount, url, image, sponsorshipName, expected
		final Object testingData[][] = {
			{
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", null
			}, {
				"sponsor1", 20.0, "http://working4enjoyment.com/marca2", "http://working4enjoyment.com/marca2.jpg", "sponsorship11", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	//Pruebas
	/*
	 * 1. Editar Sponsorship sin autenticar.IllegalArgumentException
	 * 2. Editar Sponsorship autenticado con moderator. IllegalArgumentException
	 * 3. Editar Sponsorship de otro sponsor. IllegalArgumentException
	 */
	//Requisitos
	//CU Editar patrocinador por sponsor. 
	@Test
	public void driverStatementsConstraintsEdit() {

		//userName, amount, url, image, sponsorshipName, expected
		final Object testingData[][] = {
			{
				null, 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", IllegalArgumentException.class
			}, {
				"moderator1", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", IllegalArgumentException.class
			}, {
				"sponsor2", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditUrl((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	//Pruebas
	/*
	 * 1. Editar sponsorship con amount 0. ConstraintViolationException
	 * 2. Editar sponsorship con url inválida. ConstraintViolationException
	 * 3. Editar sponsorship con image inválida. ConstraintViolationException
	 * 4. Editar sponsorship con url vacía. ConstraintViolationException
	 * 5. Editar sponsorship con image vacía. ConstraintViolationException
	 */
	//Requisitos
	//CU Editar patrocinador por sponsor.
	@Test
	public void driverDataConstraintsEdit() {

		//userName, amount, url, image, bargainName, expected
		final Object testingData[][] = {
			{
				"sponsor1", 0.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "sponsorship1", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", "//working4enjoyment.com/marca.jpg", "sponsorship1", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "", "http://working4enjoyment.com/marca.jpg", "sponsorship1", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", " ", "sponsorship1", ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los sponsorships del sponsor.
	 * 3. Seleccionar uno.
	 * 4. Editarlo.
	 */

	protected void templateEdit(final String userName, final Double amount, final String url, final String image, final String sponsorshipName, final Class<?> expected) {
		Class<?> caught;

		Page<Sponsorship> sponsorships;
		Integer countSponsorships;
		Sponsorship sponsorshipChoosen;
		Bargain bargain;
		Sponsorship saved;
		DataBinder binder;
		Sponsorship sponsorship;
		final double priceBeforeSponsorship;
		double minimum;
		double priceBefore;

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

			//Editamos sponsorship
			sponsorshipChoosen.setAmount(amount);
			sponsorshipChoosen.setImage(image);
			sponsorshipChoosen.setUrl(url);

			binder = new DataBinder(sponsorshipChoosen);

			sponsorship = this.sponsorshipService.reconstruct(sponsorshipChoosen, binder.getBindingResult());

			saved = this.sponsorshipService.save(sponsorship);

			this.sponsorshipService.flush();

			Assert.notNull(this.sponsorshipService.findOne(saved.getId()));

			//Comprobamos que se actualice bn el precio
			Assert.isTrue(this.bargainService.findOne(bargain.getId()).getPrice() == Math.max(minimum, priceBefore - amount + priceBeforeSponsorship));

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
	 * 2. Edit sponsorship por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateEditUrl(final String userName, final Double amount, final String url, final String image, final String sponsorshipName, final Class<?> expected) {
		Class<?> caught;

		DataBinder binder;
		Sponsorship sponsorship;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			sponsorship = this.sponsorshipService.findOne(this.getEntityId(sponsorshipName));

			//Editamos sponsorship

			sponsorship.setAmount(amount);
			sponsorship.setImage(image);
			sponsorship.setUrl(url);

			binder = new DataBinder(sponsorship);

			sponsorship = this.sponsorshipService.reconstruct(sponsorship, binder.getBindingResult());

			this.sponsorshipService.save(sponsorship);

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
