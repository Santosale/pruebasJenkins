
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

import security.LoginService;
import services.GrouponService;
import services.UserService;
import utilities.AbstractTest;
import domain.Groupon;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditGrouponTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private GrouponService	grouponService;

	@Autowired
	private UserService		userService;


	/*
	 * Pruebas:
	 * 1. Editamos la conjunta 1 como user1 con todos los parámetros correctos (no salta excepción)
	 * 2. Editamos la conjunta 2 como user1 con todos los parámetros correctos (no salta excepción)
	 * 3. Editamos la conjunta 1 como user1 con todos los parámetros de texto a nulos (salta un ConstraintViolationException)
	 * 4. Editamos la conjunta 1 como user1 con todos los parámetros de texto vacíos (salta un ConstraintViolationException)
	 * 6. Editamos la conjunta 1 como user1 con todos los parámetros de texto vacíos menos el título (salta un ConstraintViolationException)
	 * 7. Editamos la conjunta 1 como user1 con todos los parámetros de texto vacíos menos el título y la descripción (salta un ConstraintViolationException)
	 * 8. Editamos la conjunta 1 como user1 con todos los parámetros de texto vacíos menos el título, la descripción y el nombre del producto (salta un ConstraintViolationException)
	 * 9. Editamos la conjunta 1 como user1 con la url del producto con un formato erróneo (salta un ConstraintViolationException)
	 * 10.Editamos la conjunta 1 como user1 con el mínimo de productos negativos (salta un ConstraintViolationException)
	 * 11.Editamos la conjunta 1 como user1 con la fecha en pasado (salta un IllegalArgumentException)
	 * 12.Editamos la conjunta 1 como user1 con el precio original igual al de la conjunta (salta un IllegalArgumentException)
	 * 13.Editamos la conjunta 1 como user1 con el precio original menor que el de la conjunta (salta un IllegalArgumentException)
	 * 14.Editamos la conjunta 6 como user1 estableciendo el código de descuento a nulo cuando aún no ha sido puesto nunca (salta un IllegalArgumentException)
	 * 15.Editamos la conjunta 6 como user1 estableciendo el código de descuento a vacío cuando aún no ha sido puesto nunca (salta un IllegalArgumentException)
	 * 16.Editamos la conjunta 1 como user2 que no es el creador de esa conjunta (salta un IllegalArgumentException)
	 * 17.Editamos la conjunta 1 como sponsor (salta un IllegalArgumentException)
	 * 18.Editamos la conjunta 1 como moderator (salta un IllegalArgumentException)
	 * 19.Editamos la conjunta 1 como company (salta un IllegalArgumentException)
	 * 20.Editamos la conjunta 1 como admin (salta un IllegalArgumentException)
	 * 21.Editamos la conjunta 1 sin estar autenticados (salta un IllegalArgumentException)
	 * 
	 * Requisito 25.1: Un actor autenticado como usuario puede organizar una conjunta. El código de descuento puede ser modificado siempre que se supere el mínimo de productos requeridos.
	 * Una vez superado por primera vez el mínimo de productos, no se puede editar hasta que aporte el código de descuento.
	 * Un usuario debe poder borrar sus conjuntas.
	 */
	@Test()
	public void testEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, null
			},
			{
				"user", "user1", "groupon2", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, false, null
			},
			{
				"user", "user1", "groupon1", null, null, null, null, 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "", "", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "", "", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono movil", "", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono movil", "rgarrw", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true, ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", -1, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", true, true,
				ConstraintViolationException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2017 12:34", 50.0, 25.0, "AB-SDE-RET", true, true,
				IllegalArgumentException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 25.0, 25.0, "AB-SDE-RET", true, true,
				IllegalArgumentException.class
			},
			{
				"user", "user1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 25.0, 26.0, "AB-SDE-RET", true, true,
				IllegalArgumentException.class
			},
			{
				"user", "user1", "groupon6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 26.0, null, true, true,
				IllegalArgumentException.class
			},
			{
				"user", "user1", "groupon6", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 26.0, "", true, true,
				IllegalArgumentException.class
			},
			{
				"user", "user2", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			},
			{
				"sponsor", "sponsor1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			},
			{
				"moderator", "moderator1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			},
			{
				"company", "company1", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			},
			{
				"admin", "admin", "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			},

			{
				null, null, "groupon1", "Groupon nuevo", "Descripción nueva", "Teléfono móvil", "https://tiendas.mediamarkt.es/p/movil-iphone-x-64-gb-super-retina-de-5.8-1382454", 2, "12/8/2018 12:34", 50.0, 25.0, "AB-SDE-RET", false, true,
				IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (int) testingData[i][7],
					(String) testingData[i][8], (double) testingData[i][9], (double) testingData[i][10], (String) testingData[i][11], (boolean) testingData[i][12], (boolean) testingData[i][13], (Class<?>) testingData[i][14]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Logueados como user1 hacemos el findOneToEdit de la conjunta 1 (no salta excepción)
	 * 2.Logueados como user1 hacemos el findOneToEdit de la conjunta 2 (no salta excepción)
	 * 3.Logueados como moderator1 hacemos el findOne de la conjunta 1 (no salta excepción)
	 * 4.Logueados como moderator1 hacemos el findOneToEdit de la conjunta 1 (salta un IllegalArgumentException)
	 * 5.Logueados como user1 hacemos el findOneToEdit de una conjunta de id 0 (salta un IllegalArgumentException)
	 * 6.Logueados como user1 hacemos el findOne de una conjunta de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"user", "user1", "groupon1", true, false, true, null
			}, {
				"user", "user1", "groupon2", true, false, true, null
			}, {
				"moderator", "moderator1", "groupon1", false, false, false, null
			}, {
				"moderator", "moderator1", "groupon1", false, false, true, IllegalArgumentException.class
			}, {
				"user", "user2", "groupon1", true, true, true, IllegalArgumentException.class
			}, {
				"user", "user2", "groupon1", true, true, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	protected void template(final String user, final String username, final String grouponBean, final String title, final String description, final String productName, final String productUrl, final int minAmountProduct, final String maxDate,
		final double originalPrice, final double price, final String discountCode, final boolean isMyGroupon, final boolean canEditDiscountCode, final Class<?> expected) {
		Class<?> caught;
		Groupon groupon;
		Groupon saved;
		Groupon copyGroupon;
		int grouponIdAux;
		int grouponId;

		DataBinder binder;
		Groupon grouponReconstruct;
		SimpleDateFormat format;
		Date date;

		caught = null;
		date = null;
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			if (isMyGroupon == true) {
				grouponIdAux = super.getEntityId(grouponBean);
				grouponId = 0;
				for (int i = 1; i <= this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), 1, 5).getTotalPages(); i++)
					//Si es tuya la cogemos desde el listado
					for (final Groupon g : this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
			} else
				grouponId = super.getEntityId(grouponBean);

			groupon = this.grouponService.findOneToEdit(grouponId);
			copyGroupon = this.copyGroupon(groupon);

			copyGroupon.setTitle(title);
			copyGroupon.setDescription(description);
			copyGroupon.setProductName(productName);
			copyGroupon.setProductUrl(productUrl);
			copyGroupon.setMinAmountProduct(minAmountProduct);
			if (maxDate != null) {
				format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				date = format.parse(maxDate);
			}
			copyGroupon.setMaxDate(date);
			copyGroupon.setOriginalPrice(originalPrice);
			copyGroupon.setPrice(price);
			copyGroupon.setDiscountCode(discountCode);
			//Editamos los valores

			binder = new DataBinder(copyGroupon);
			grouponReconstruct = this.grouponService.reconstruct(copyGroupon, binder.getBindingResult()); //Lo reconstruimos
			if (canEditDiscountCode == false)
				if (grouponReconstruct.getDiscountCode() != null)
					Assert.isTrue(grouponReconstruct.getDiscountCode().equals(this.grouponService.findOne(groupon.getId()).getDiscountCode())); //Si no se puede editar el código vemos que al reconstruirlo no ha cambiado
				else
					Assert.isNull((this.grouponService.findOne(groupon.getId()).getDiscountCode())); //Si aún era nulo miramos que anteriormente es nulo
			saved = this.grouponService.save(grouponReconstruct); //Guardamos la conjunta
			super.flushTransaction();

			Assert.isTrue(this.grouponService.findAll().contains(saved)); //Miramos si están entre todos las conjuntas de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String grouponBean, final boolean isMyGroupon, final boolean falseId, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int grouponId;
		Groupon groupon;
		int grouponIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			grouponId = 0;
			grouponIdAux = super.getEntityId(grouponBean);
			if (isMyGroupon == true) {
				grouponIdAux = super.getEntityId(grouponBean);
				grouponId = 0;
				for (int i = 1; i <= this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), 1, 5).getTotalPages(); i++)
					//Si es tuya la cogemos desde el listado
					for (final Groupon g : this.grouponService.findByCreatorId(this.userService.findByUserAccountId(LoginService.getPrincipal().getId()).getId(), i, 5).getContent())
						if (g.getId() == grouponIdAux)
							grouponId = g.getId();
			} else
				grouponId = super.getEntityId(grouponBean);

			if (findOneToEdit == true) {
				if (falseId == false)
					groupon = this.grouponService.findOneToEdit(grouponId); //Se prueba el findOneToEdit
				else
					groupon = this.grouponService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				groupon = this.grouponService.findOne(grouponId); //Se prueba el findOne
			else
				groupon = this.grouponService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(groupon); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public Groupon copyGroupon(final Groupon groupon) {
		Groupon result;

		result = new Groupon();
		result.setId(groupon.getId());
		result.setVersion(groupon.getVersion());
		result.setTitle(groupon.getTitle());
		result.setDescription(groupon.getDescription());
		result.setProductName(groupon.getProductName());
		result.setProductUrl(groupon.getProductUrl());
		result.setMinAmountProduct(groupon.getMinAmountProduct());
		result.setMaxDate(groupon.getMaxDate());
		result.setOriginalPrice(groupon.getOriginalPrice());
		result.setPrice(groupon.getPrice());
		result.setDiscountCode(groupon.getDiscountCode());
		result.setCreator(groupon.getCreator());

		return result;
	}

}
