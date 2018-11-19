
package usecases;

import java.util.Calendar;

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

import services.AdvertisementService;
import utilities.AbstractTest;
import domain.Advertisement;
import domain.CreditCard;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditAdvertisementTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private AdvertisementService	advertisementService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Crear Advertisement con datos correctos.
	 * 2. Crear Advertisement con title null. ConstraintViolationException
	 * 3. Crear Advertisement con url banner null. ConstraintViolationException
	 * 4. Crear Advertisement con url target null. ConstraintViolationException
	 * 5. Crear Advertisement con title blank. ConstraintViolationException
	 * 6. Crear Advertisement con url banner incorrecta. ConstraintViolationException
	 * 7. Crear Advertisement con url target incorrecta. ConstraintViolationException
	 * 8. Crear Advertisement con datos correctos y taboo.
	 */

	//Requisitos
	//CU 4.2: Register an advertisement in a newspaper.
	@Test
	public void driverDataConstraintCreate() {

		//Rol,title,url banner,url target,number,expiration month, expiration year,brand name, cvv code, holder name, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, null
			}, {
				"agent1", null, "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", null, "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", null, "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "sjnds", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "sdjlfs", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio cialis", "http://working4enjoyment.com/img/logow4e.png", "sdjlfs", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", null, true, ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
				(String) testingData[i][7], (Integer) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Boolean) testingData[i][11], (Class<?>) testingData[i][12]);
		;

	}

	//Pruebas
	/*
	 * 1. Crear Advertisement con tarjeta caducada este año.IllegalArgumentException
	 * 2. Crear Advertisement autenticandose con tarjeta que caduca este mes. IllegalArgumentException
	 * 3. Crear Advertisement con tarjeta caducada hace años.IllegalArgumentException
	 */
	//Requisitos
	//CU 4.2: Register an advertisement in a newspaper.
	@Test
	public void driverStatementsConstraintsCreate() {

		//Rol,title,url banner,url target,number,expiration month, expiration year,brand name, cvv code, holder name, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", null, false, IllegalArgumentException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", "today", false, IllegalArgumentException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 16, "Visa", 123, "Carlos Ortiz", null, false, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
				(String) testingData[i][7], (Integer) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Boolean) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	/*
	 * 1. Editar Advertisement con datos correctos.
	 * 2. Editar Advertisement con title null. ConstraintViolationException
	 * 3. Editar Advertisement con url banner null. ConstraintViolationException
	 * 4. Editar Advertisement con url target null. ConstraintViolationException
	 * 5. Editar Advertisement con title blank. ConstraintViolationException
	 * 6. Editar Advertisement con url banner incorrecta. ConstraintViolationException
	 * 7. Editar Advertisement con url target incorrecta. ConstraintViolationException
	 * 8. Editar Advertisement con datos correctos y taboo.
	 */

	@Test
	public void driverDataConstraintEdit() {

		//Rol,title,url banner,url target,number,expiration month, expiration year,brand name, cvv code, holder name, advertisement, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, null
			}, {
				"agent1", null, "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", null, "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", null, "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "sjnds", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "sdjlfs", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", false, ConstraintViolationException.class
			}, {
				"agent1", "Nuevo anuncio sex", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 21, "Visa", 123, "Carlos Ortiz", "advertisement1", true, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (String) testingData[i][7],
				(Integer) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Boolean) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	//Pruebas
	/*
	 * 1. Editar Advertisement con tarjeta caducada este año.IllegalArgumentException
	 * 2. Editar Advertisement autenticandose con tarjeta que caduca este mes. IllegalArgumentException
	 * 3. Editar Advertisement con tarjeta caducada hace años.IllegalArgumentException
	 */

	@Test
	public void driverStatementsConstraintsEdit() {

		//Rol,title,url banner,url target,number,expiration month, expiration year,brand name, cvv code, holder name, ExpectedException
		final Object testingData[][] = {
			{
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", "advertisement1", false, IllegalArgumentException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", "advertisement1", false, IllegalArgumentException.class
			}, {
				"agent1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 16, "Visa", 123, "Carlos Ortiz", "advertisement1", false, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6], (String) testingData[i][7],
				(Integer) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Boolean) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	//Pruebas
	/*
	 * 1. Crear Advertisement autenticandose como un usuario. IllegalArgumentException
	 * 2. Editar Advertisement autenticandose como un customer. IllegalArgumentException
	 * 3. Editar Advertisement de otro agent. IllegalArgumentException
	 * 4. Crear Advertisement sin autenticarse. IllegalArgumentException
	 */
	//Requisitos
	//CU 4.2: Register an advertisement in a newspaper.
	@Test
	public void driverStatementsConstraintsCreateAndEditURL() {

		//Rol,title,url banner,url target,number,expiration month, expiration year,brand name, cvv code, holder name,advertisement name, ExpectedException
		final Object testingData[][] = {
			{
				"user1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", null, IllegalArgumentException.class
			}, {
				"customer1", "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", "advertisement1", IllegalArgumentException.class
			}, {
				null, "Nuevo anuncio", "http://working4enjoyment.com/img/logow4e.png", "http://working4enjoyment.com", "1111222233334444", 01, 18, "Visa", 123, "Carlos Ortiz", null, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreateEditURL((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
				(String) testingData[i][7], (Integer) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los advertisements del usuario.
	 * 3. Crear advertisement
	 */

	protected void templateCreate(final String userName, final String title, final String urlBanner, final String urlTarget, final String number, final Integer expirationMonth, final Integer expirationYear, final String brandName, final Integer cvvCode,
		final String holderName, final String changeDate, final Boolean hasTaboo, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement saved;
		Advertisement advertisement;
		DataBinder binder;
		CreditCard creditCard;
		Calendar calendar;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los advertisements
			advertisements = this.advertisementService.findByAgentId(1, 5);

			//Creamos creditCard
			creditCard = new CreditCard();

			creditCard.setBrandName(brandName);
			creditCard.setCvvcode(cvvCode);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setHolderName(holderName);
			creditCard.setNumber(number);

			//Vemos si cambiamos la fecha
			if (changeDate != null && changeDate.equals("today")) {
				calendar = Calendar.getInstance();
				creditCard.setExpirationMonth(calendar.get(Calendar.MONTH) + 1);
				creditCard.setExpirationYear(calendar.get(Calendar.YEAR) % 100);
			}

			//Creamos el advertisement
			advertisement = this.advertisementService.create();

			advertisement.setTitle(title);
			advertisement.setUrlBanner(urlBanner);
			advertisement.setUrlTarget(urlTarget);
			advertisement.setCreditCard(creditCard);

			binder = new DataBinder(advertisement);

			saved = this.advertisementService.reconstruct(advertisement, binder.getBindingResult());

			saved = this.advertisementService.save(saved);

			this.advertisementService.flush();

			//Comprobamos que se crea
			Assert.notNull(this.advertisementService.findOne(saved.getId()));
			Assert.isTrue(advertisements.getTotalPages() <= 2);
			Assert.isTrue(saved.getHasTaboo() == hasTaboo);

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
	 * 2. Crear o actualizar advertisement por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateCreateEditURL(final String userName, final String title, final String urlBanner, final String urlTarget, final String number, final Integer expirationMonth, final Integer expirationYear, final String brandName,
		final Integer cvvCode, final String holderName, final String advertisementName, final Class<?> expected) {
		Class<?> caught;

		Advertisement advertisement;
		Advertisement saved;
		DataBinder binder;
		CreditCard creditCard;
		int advertisementId;
		int countAfter;
		int countBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Obtenemos los objetos que queremos
			countBefore = this.advertisementService.findAll().size();

			//Creamos o editamos el advertisement

			if (advertisementName == null) {
				advertisement = this.advertisementService.create();

				//Creamos creditCard
				creditCard = new CreditCard();

			} else {
				advertisementId = super.getEntityId(advertisementName);
				advertisement = this.advertisementService.findOne(advertisementId);
				creditCard = advertisement.getCreditCard();

			}

			creditCard.setBrandName(brandName);
			creditCard.setCvvcode(cvvCode);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setHolderName(holderName);
			creditCard.setNumber(number);

			advertisement.setTitle(title);
			advertisement.setUrlBanner(urlBanner);
			advertisement.setUrlTarget(urlTarget);
			advertisement.setCreditCard(creditCard);

			binder = new DataBinder(advertisement);

			saved = this.advertisementService.reconstruct(advertisement, binder.getBindingResult());

			saved = this.advertisementService.save(saved);

			this.advertisementService.flush();

			//Comprobamos que No se crea
			countAfter = this.advertisementService.findAll().size();
			Assert.isTrue(countAfter == countBefore);
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
	 * 2. Listar los advertisement del usuario.
	 * 3. Navegar y escoger el advertisement deseado.
	 * 4. Editarlo
	 */
	protected void templateEdit(final String userName, final String title, final String urlBanner, final String urlTarget, final String number, final Integer expirationMonth, final Integer expirationYear, final String brandName, final Integer cvvCode,
		final String holderName, final String advertisementName, final Boolean hasTaboo, final Class<?> expected) {
		Class<?> caught;

		Page<Advertisement> advertisements;
		Advertisement advertisement;
		Advertisement advertisementChoosen;
		int countAdvertisement;
		int advertisementId;
		Advertisement saved;
		DataBinder binder;
		CreditCard creditCard;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

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

			//Editamos el advertisement
			advertisement = advertisementChoosen;
			creditCard = advertisement.getCreditCard();

			creditCard.setBrandName(brandName);
			creditCard.setCvvcode(cvvCode);
			creditCard.setExpirationMonth(expirationMonth);
			creditCard.setExpirationYear(expirationYear);
			creditCard.setHolderName(holderName);
			creditCard.setNumber(number);

			advertisement.setTitle(title);
			advertisement.setUrlBanner(urlBanner);
			advertisement.setUrlTarget(urlTarget);
			advertisement.setCreditCard(creditCard);

			binder = new DataBinder(advertisement);

			saved = this.advertisementService.reconstruct(advertisement, binder.getBindingResult());

			saved = this.advertisementService.save(saved);

			this.advertisementService.flush();

			Assert.isTrue(saved.getHasTaboo() == hasTaboo);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
