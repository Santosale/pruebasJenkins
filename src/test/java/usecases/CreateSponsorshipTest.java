
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

import security.LoginService;
import services.BargainService;
import services.SponsorService;
import services.SponsorshipService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Sponsor;
import domain.Sponsorship;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CreateSponsorshipTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private SponsorService		sponsorService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Crear Sponsorship con datos correctos.
	 * 2. Crear Sponsorship a bargain con un sponsorship
	 * 3. Crear Sponsorship a bargain sin sponsorship
	 * 4. Crear Sponsorship a bargain con mas de un sponsorship
	 */

	//Requisitos
	//CU 5.	El sistema debe guardar información sobre los distintos patrocinios: el patrocinador que lo crea, la imagen y la URL a la que direcciona. 
	//24. 1.Un patrocinador puede promocionarse en un chollo. El sistema debe guardar el chollo que va a patrocinar con su correspondiente aportación monetaria.
	@Test
	public void driverPositive() {

		//userName, amount, url, image, bargainName, expected
		final Object testingData[][] = {
			{
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain9", null
			}, {
				"sponsor2", 20.0, "http://working4enjoyment.com/marca2", "http://working4enjoyment.com/marca2.jpg", "bargain8", null
			}, {
				"sponsor2", 30.0, "http://working4enjoyment.com/marca4", "http://working4enjoyment.com/marca4.jpg", "bargain4", null
			}, {
				"sponsor4", 500.0, "http://working4enjoyment.com/marca4", "http://working4enjoyment.com/marca4.jpg", "bargain2", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	//Pruebas
	/*
	 * 1. Crear Sponsorship sin autenticar.IllegalArgumentException
	 * 2. Crear Sponsorship autenticado con moderator. IllegalArgumentException
	 * 3. Crear Sponsorship a un bargain que ya tiene un sponsorship para ese sponsor. IllegalArgumentException
	 */
	//Requisitos
	//CU 5.	El sistema debe guardar información sobre los distintos patrocinios: el patrocinador que lo crea, la imagen y la URL a la que direcciona. 
	//24. 1.Un patrocinador puede promocionarse en un chollo. El sistema debe guardar el chollo que va a patrocinar con su correspondiente aportación monetaria.
	@Test
	public void driverStatementsConstraintsCreate() {

		//userName, amount, url, image, bargainName, expected
		final Object testingData[][] = {
			{
				null, 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain1", IllegalArgumentException.class
			}, {
				"moderator1", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain1", IllegalArgumentException.class
			}, {
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreateUrl((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	//Pruebas
	/*
	 * 1. Crear sponsorship con amount 0. ConstraintViolationException
	 * 2. Crear sponsorship con url inválida. ConstraintViolationException
	 * 3. Crear sponsorship con image inválida. ConstraintViolationException
	 * 4. Crear sponsorship con url vacía. ConstraintViolationException
	 * 5. Crear sponsorship con image vacía. ConstraintViolationException
	 */
	//Requisitos
	//CU 5.	El sistema debe guardar información sobre los distintos patrocinios: el patrocinador que lo crea, la imagen y la URL a la que direcciona.  
	//24. 1.Un patrocinador puede promocionarse en un chollo. El sistema debe guardar el chollo que va a patrocinar con su correspondiente aportación monetaria.
	@Test
	public void driverDataConstraintsCreate() {

		//userName, amount, url, image, bargainName, expected
		final Object testingData[][] = {
			{
				"sponsor1", 0.0, "http://working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain9", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "working4enjoyment.com/marca", "http://working4enjoyment.com/marca.jpg", "bargain9", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", "//working4enjoyment.com/marca.jpg", "bargain9", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "", "http://working4enjoyment.com/marca.jpg", "bargain9", ConstraintViolationException.class
			}, {
				"sponsor1", 10.0, "http://working4enjoyment.com/marca", " ", "bargain9", ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (Double) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollos disponibles.
	 * 3. Seleccionar uno.
	 * 4. Crear el patrocinio.
	 */

	protected void templateCreate(final String userName, final Double amount, final String url, final String image, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;
		Sponsorship saved;
		DataBinder binder;
		Sponsor sponsor;
		Sponsorship sponsorship;
		double minimum;
		double priceBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));
			minimum = bargain.getMinimumPrice();
			priceBefore = bargain.getPrice();

			//Obtenemos los bargains
			sponsor = this.sponsorService.findByUserAccountId(LoginService.getPrincipal().getId());
			bargains = this.bargainService.findBySponsorIdWithNoSponsorship(sponsor.getId(), 1, 1);
			Assert.notNull(bargains);
			countBargains = bargains.getTotalPages();

			//Buscamos el bargain
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findBySponsorIdWithNoSponsorship(sponsor.getId(), i + 1, 5);

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

			//Ya tenemos el bargain
			Assert.notNull(bargainChoosen);

			//Creamos sponsorship

			sponsorship = this.sponsorshipService.create(bargainChoosen);

			sponsorship.setAmount(amount);
			sponsorship.setImage(image);
			sponsorship.setUrl(url);

			binder = new DataBinder(sponsorship);

			sponsorship = this.sponsorshipService.reconstruct(sponsorship, binder.getBindingResult());

			saved = this.sponsorshipService.save(sponsorship);

			this.sponsorshipService.flush();

			Assert.notNull(this.sponsorshipService.findOne(saved.getId()));

			//Comprobamos que se actualice bn el precio
			Assert.isTrue(this.bargainService.findOne(bargain.getId()).getPrice() == Math.max(minimum, priceBefore - amount));

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
	 * 2. Crear sponsorship por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateCreateUrl(final String userName, final Double amount, final String url, final String image, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Bargain bargain;
		DataBinder binder;
		Sponsorship sponsorship;
		Sponsorship saved;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			//Creamos sponsorship

			sponsorship = this.sponsorshipService.create(bargain);

			sponsorship.setAmount(amount);
			sponsorship.setImage(image);
			sponsorship.setUrl(url);

			binder = new DataBinder(sponsorship);

			sponsorship = this.sponsorshipService.reconstruct(sponsorship, binder.getBindingResult());

			saved = this.sponsorshipService.save(sponsorship);

			this.sponsorshipService.flush();

			Assert.isNull(this.sponsorService.findOne(saved.getId()));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
