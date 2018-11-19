
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import services.NotificationService;
import services.UserService;
import utilities.AbstractTest;
import domain.Actor;
import domain.Bargain;
import forms.BargainForm;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditBargainTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private UserService			userService;

	@Autowired
	private NotificationService	notificationService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Editar chollo con datos correctos.
	 * 2. Editar Bargain con precio original y mínimo igual.
	 * 3. Editar Bargain publicandolo.
	 * 4. Editar Bargain con product name null. ConstraintViolationException
	 * 5. Editar Bargain con description un espacio. ConstraintViolationException
	 * 6. Editar Bargain con estimatedSells 0. ConstraintViolationException
	 * 7. Editar Bargain con originalPrice negativo. ConstraintViolationException
	 * 8. Editar Bargain con minimumPrice negativo. ConstraintViolationException
	 * 9. Editar Bargain con productUrl no siendo una url válida. ConstraintViolationException
	 */
	//CU: Editar su chollo
	@Test
	public void driverDataConstraintEdit() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, bargainName, tagsName, expected
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo"
		};
		final Object testingData[][] = {
			{
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", true, 123.0, 300.0, "bargain1", tagsName, null
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", true, 300.0, 300.0, "bargain1", tagsName, null
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", true, 123.0, 300.0, "bargain11", tagsName, null
			}, {
				"company1", null, "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", true, 120.0, 300.0, "bargain1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, " ", 20, "sdkjs89", true, 120.0, 300.0, "bargain1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 0, "sdkjs89", true, 120.0, 300.0, "bargain1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 10, "sdkjs89", true, -120.0, -100.0, "bargain1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 10, "sdkjs89", true, -120.0, 300.0, "bargain1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "working4enjoyment.com/chollo", productImages, "El mejor chollo", 10, "sdkjs89", true, 120.0, 300.0, "bargain1", tagsName, ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	//Pruebas
	/*
	 * 1. Editar chollo publicado a no publicado.No se espera excepción gracias al reconstruct
	 * 2. Editar chollo precio minimo mayor que el precio actual, debe actualizarse este.
	 */
	//CU: Editar su chollo
	@Test
	public void driverStatementsConstraintsEdit() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, bargainName, tagsName, expected
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo"
		};
		final Object testingData[][] = {
			{
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 123.0, 300.0, "bargain1", tagsName, null
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 201.1, 300.0, "bargain1", tagsName, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	//Pruebas
	/*
	 * 1. Editar chollo de otra compañía. IllegalArgumentException
	 * 2. Editar chollo sin autenticarse. IllegalArgumentException
	 */
	//Requisitos
	//CU: Editar su chollo
	@Test
	public void driverStatementsConstraintsCreateAndEditURL() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, bargainName, tagsName, expected
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo"
		};
		final Object testingData[][] = {
			{
				"company2", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 100.1, 300.0, "bargain1", tagsName, IllegalArgumentException.class
			}, {
				null, "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 100.1, 300.0, "bargain1", tagsName, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateEditURL((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollo de la compañía.
	 * 3. Escoger un chollo.
	 * 4. Editarlo
	 */

	protected void templateEdit(final String userName, final String productName, final String productUrl, final String[] productImagesNames, final String description, final Integer estimatedSells, final String discountCode, final Boolean isPublished,
		final Double minimumPrice, final Double originalPrice, final String bargainName, final String[] tagsName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Collection<String> productImages;
		List<String> tags;
		Bargain bargain;
		Bargain saved;
		BargainForm reconstructBargainForm;
		BargainForm bargainForm;
		DataBinder binder;
		Map<Actor, Integer> userNumberOfNotifications;
		Integer numberNotifications;
		boolean publishBefore;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));
			publishBefore = bargain.getIsPublished();

			//Vemos las notificaciones, luego tendrán otra más
			userNumberOfNotifications = new HashMap<Actor, Integer>();
			for (final Actor actor : this.userService.findWithBasicPremium()) {
				numberNotifications = this.notificationService.countNotVisitedByActorId(actor.getId());
				userNumberOfNotifications.put(actor, numberNotifications);
			}

			//Obtenemos los bargains
			bargains = this.bargainService.findByCompanyId(1, 1);
			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findByCompanyId(i + 1, 5);

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

			//Creamos bargainForm
			bargainForm = new BargainForm();

			bargain = this.bargainCopy(bargain);

			bargain.setProductName(productName);
			bargain.setProductUrl(productUrl);

			productImages = new ArrayList<String>();
			for (final String productImage : productImagesNames)
				productImages.add(productImage);
			bargain.setProductImages(productImages);
			bargain.setDescription(description);
			bargain.setEstimatedSells(estimatedSells);
			bargain.setDiscountCode(discountCode);
			bargain.setIsPublished(isPublished);
			bargain.setMinimumPrice(minimumPrice);
			bargain.setOriginalPrice(originalPrice);

			tags = new ArrayList<String>();
			for (final String tagName : tagsName)
				tags.add(tagName);
			bargainForm.setTagsName(tags);
			bargainForm.setBargain(bargain);
			binder = new DataBinder(bargainForm);

			reconstructBargainForm = this.bargainService.reconstruct(bargainForm, binder.getBindingResult());

			saved = this.bargainService.save(reconstructBargainForm.getBargain(), new ArrayList<String>(reconstructBargainForm.getTagsName()), reconstructBargainForm.getCategoryId());

			this.bargainService.flush();

			//Comprobamos que se crea y llegan las notificaciones si se ha publicado
			Assert.notNull(this.bargainService.findOne(saved.getId()));

			for (final Actor actor : userNumberOfNotifications.keySet())
				if (saved.getIsPublished() && !publishBefore)
					Assert.isTrue(userNumberOfNotifications.get(actor) + 1 == this.notificationService.countNotVisitedByActorId(actor.getId()));
				else
					Assert.isTrue(userNumberOfNotifications.get(actor).equals(this.notificationService.countNotVisitedByActorId(actor.getId())));

			Assert.isTrue(saved.getPrice() >= saved.getMinimumPrice());

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
	 * 2. Actualizar bargain por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateEditURL(final String userName, final String productName, final String productUrl, final String[] productImagesNames, final String description, final Integer estimatedSells, final String discountCode, final Boolean isPublished,
		final Double minimumPrice, final Double originalPrice, final String bargainName, final String[] tagsName, final Class<?> expected) {
		Class<?> caught;

		Collection<String> productImages;
		List<String> tags;
		Bargain bargain;
		Bargain saved;
		BargainForm reconstructBargainForm;
		BargainForm bargainForm;
		DataBinder binder;
		Map<Actor, Integer> userNumberOfNotifications;
		Integer numberNotifications;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Vemos las notificaciones, luego tendrán las mismas
			userNumberOfNotifications = new HashMap<Actor, Integer>();
			for (final Actor actor : this.userService.findWithBasicPremium()) {
				numberNotifications = this.notificationService.countNotVisitedByActorId(actor.getId());
				userNumberOfNotifications.put(actor, numberNotifications);
			}

			//Creamos bargain
			bargainForm = new BargainForm();

			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			bargain = this.bargainCopy(bargain);

			bargain.setProductName(productName);
			bargain.setProductUrl(productUrl);

			productImages = new ArrayList<String>();
			for (final String productImage : productImagesNames)
				productImages.add(productImage);
			bargain.setProductImages(productImages);
			bargain.setDescription(description);
			bargain.setEstimatedSells(estimatedSells);
			bargain.setDiscountCode(discountCode);
			bargain.setIsPublished(isPublished);
			bargain.setMinimumPrice(minimumPrice);
			bargain.setPrice(12.0);
			bargain.setOriginalPrice(originalPrice);
			tags = new ArrayList<String>();
			for (final String tagName : tagsName)
				tags.add(tagName);
			bargainForm.setTagsName(tags);
			bargainForm.setBargain(bargain);
			binder = new DataBinder(bargainForm);

			reconstructBargainForm = this.bargainService.reconstruct(bargainForm, binder.getBindingResult());

			saved = this.bargainService.save(reconstructBargainForm.getBargain(), new ArrayList<String>(reconstructBargainForm.getTagsName()), reconstructBargainForm.getCategoryId());

			this.bargainService.flush();

			//Comprobamos que existe y llegan las notificaciones
			Assert.notNull(this.bargainService.findOne(saved.getId()));

			for (final Actor actor : userNumberOfNotifications.keySet())
				Assert.isTrue(userNumberOfNotifications.get(actor) == this.notificationService.countNotVisitedByActorId(actor.getId()));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	private Bargain bargainCopy(final Bargain bargain) {
		Bargain result;

		result = new Bargain();

		result.setProductImages(bargain.getProductImages());
		result.setDescription(bargain.getDescription());
		result.setEstimatedSells(bargain.getEstimatedSells());
		result.setDiscountCode(bargain.getDiscountCode());
		result.setIsPublished(bargain.getIsPublished());
		result.setMinimumPrice(bargain.getMinimumPrice());
		result.setPrice(bargain.getPrice());
		result.setOriginalPrice(bargain.getOriginalPrice());
		result.setProductName(bargain.getProductName());
		result.setProductUrl(bargain.getProductUrl());
		result.setCompany(bargain.getCompany());
		result.setId(bargain.getId());
		result.setVersion(bargain.getVersion());

		return result;
	}

}
