
package usecases;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.GrouponService;
import utilities.AbstractTest;
import domain.Groupon;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveGrouponTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private GrouponService	grouponService;


	/*
	 * Pruebas:
	 * 1. Creamos una conjunta como user3 con todos los parámetros correctos (no salta excepción)
	 * 2. Creamos una conjunta como user1 con todos los parámetros correctos (no salta excepción)
	 * 3. Creamos una conjunta como user6 con todos los parámetros correctos (no salta excepción)
	 * 4. Creamos una conjunta como user6 con todos los parámetros de texto a nulos (salta un ConstraintViolationException)
	 * 5. Creamos una conjunta como user6 con todos los parámetros de texto vacíos (salta un ConstraintViolationException)
	 * 6. Creamos una conjunta como user6 con todos los parámetros de texto vacíos menos el título (salta un ConstraintViolationException)
	 * 7. Creamos una conjunta como user6 con todos los parámetros de texto vacíos menos el título y la descripción (salta un ConstraintViolationException)
	 * 8. Creamos una conjunta como user6 con todos los parámetros de texto vacíos menos el título, la descripción y el nombre del producto (salta un ConstraintViolationException)
	 * 9. Creamos una conjunta como user6 con la url del producto con un formato erróneo (salta un ConstraintViolationException)
	 * 10.Creamos una conjunta como user6 con el mínimo de productos negativos (salta un ConstraintViolationException)
	 * 11.Creamos una conjunta como user6 con el precio original y de la conjunta iguales (salta un IllegalArgumentException)
	 * 12.Creamos una conjunta como user6 con el precio original menor que el de la conjunta (salta un IllegalArgumentException)
	 * 13.Creamos una conjunta como user6 con la fecha en pasado (salta un IllegalArgumentException)
	 * 14.Creamos una conjunta como sponsor (salta un IllegalArgumentException)
	 * 15.Creamos una conjunta como moderator (salta un IllegalArgumentException)
	 * 16.Creamos una conjunta como company (salta un IllegalArgumentException)
	 * 17.Creamos una conjunta como admin (salta un IllegalArgumentException)
	 * 18.Creamos una conjunta sin estar logeados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.1: Un usuario logueado como usuario debe poder
	 * organizar una conjunta. El código de descuento puede ser modificado siempre que se supere el mínimo de productos requeridos.
	 * Una vez superado por primera vez el mínimo de productos, no se puede editar hasta que aporte el código de descuento.
	 * Un usuario debe poder borrar sus conjuntas
	 */
	@Test()
	public void testSave() {
		final Object testingData[][] = {
			{
				"user", "user3", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, null
			}, {
				"user", "user1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, null
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, null
			}, {
				"user", "user6", null, null, null, null, 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "", "", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono movil", "", 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono movil", "rgarrw", 2, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", -1, "12/8/2018 12:34", 50.0, 25.0, ConstraintViolationException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2017 12:34", 50.0, 25.0, IllegalArgumentException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 25.0, 25.0, IllegalArgumentException.class
			}, {
				"user", "user6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 25.0, 26.0, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, IllegalArgumentException.class
			}, {
				"company", "company1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, IllegalArgumentException.class
			}, {
				"admin", "admin", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, IllegalArgumentException.class
			}, {
				null, null, "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (int) testingData[i][6], (String) testingData[i][7],
					(double) testingData[i][8], (double) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String title, final String description, final String productName, final String productUrl, final int minAmountProduct, final String maxDate, final double originalPrice,
		final double price, final Class<?> expected) {
		Class<?> caught;
		Groupon groupon;
		Groupon saved;

		DataBinder binder;
		Groupon grouponReconstruct;
		SimpleDateFormat format;
		Date date;

		caught = null;
		date = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			groupon = this.grouponService.create(); //Creamos la conjunta

			groupon.setTitle(title);
			groupon.setDescription(description);
			groupon.setProductName(productName);
			groupon.setProductUrl(productUrl);
			groupon.setMinAmountProduct(minAmountProduct);
			if (maxDate != null) {
				format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				date = format.parse(maxDate); //Si el momento no es nulo creamos el momento
			}
			groupon.setMaxDate(date);
			groupon.setOriginalPrice(originalPrice);
			groupon.setPrice(price);
			//Editamos los valores

			binder = new DataBinder(groupon);
			grouponReconstruct = this.grouponService.reconstruct(groupon, binder.getBindingResult()); //Lo reconstruimos
			saved = this.grouponService.save(grouponReconstruct); //Guardamos la conjunta
			super.flushTransaction();

			Assert.isTrue(this.grouponService.findAll().contains(saved)); //Miramos si están entre todos las conjunta de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
