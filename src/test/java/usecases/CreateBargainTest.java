
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
import services.CategoryService;
import services.NotificationService;
import services.UserService;
import utilities.AbstractTest;
import domain.Actor;
import domain.Bargain;
import domain.Category;
import forms.BargainForm;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CreateBargainTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService		bargainService;

	@Autowired
	private CategoryService		categoryService;

	@Autowired
	private UserService			userService;

	@Autowired
	private NotificationService	notificationService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Crear Bargain con datos correctos.
	 * 2. Crear Bargain con precio original y mínimo igual.
	 * 3. Crear Bargain con product name null. ConstraintViolationException
	 * 4. Crear Bargain con description un espacio. ConstraintViolationException
	 * 5. Crear Bargain con estimatedSells negativo. ConstraintViolationException
	 * 6. Crear Bargain con originalPrice negativo. ConstraintViolationException
	 * 7. Crear Bargain con minimumPrice negativo. ConstraintViolationException
	 * 8. Crear Bargain con productUrl no siendo una url válida. ConstraintViolationException
	 */

	//CU: 23. 1.Crear chollos.

	@Test
	public void driverDataConstraintCreate() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, categoryName, tagsName, expected
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo"
		};

		final Object testingData[][] = {
			{
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, null
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 300.0, 300.0, "category1", tagsName, null
			}, {
				"company1", null, "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, " ", 20, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", -1, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 2, "sdkjs89", false, -1.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 4, "sdkjs89", false, -123.0, -3.0, "category1", tagsName, ConstraintViolationException.class
			}, {
				"company1", "Nuevo chollo", "hola no soy url correcta", productImages, "El mejor chollo", 4, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);

	}
	//Pruebas
	/*
	 * 1. Crear Bargain con precio mínimo mayor al original.IllegalArgumentException
	 * 2. Crear Bargain con una de las url del producto mal. ConstraintViolationException
	 */
	//Requisitos
	//CU: 23. 1.Crear chollos.

	@Test
	public void driverStatementsConstraintsCreateAndUrlCollectionConstraint() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, categoryName, tagsName, expected) {
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] productImagesError = {
			"http://working4enjoyment.com/imag1.jpg", "ng4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo", "otra"
		};
		final Object testingData[][] = {
			{
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 1203.0, 300.0, "category1", tagsName, IllegalArgumentException.class
			}, {
				"company1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImagesError, "El mejor chollo", 20, "sdkjs89", false, 103.0, 300.0, "category1", tagsName, ConstraintViolationException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);
	}

	//Pruebas
	/*
	 * 1. Crear Bargain autenticandose como un usuario. IllegalArgumentException
	 * 2. Crear Bargain sin autenticarse. IllegalArgumentException
	 */
	//Requisitos
	//CU: 23. 1.Crear chollos.

	@Test
	public void driverStatementsConstraintsCreateURL() {

		//userName, productName, productUrl, productImagesNames, description, estimatedSells, discountCode, isPublished, minimumPrice, originalPrice, categoryName, tagsName, expected) {
		final String[] productImages = {
			"http://working4enjoyment.com/imag1.jpg", "http://working4enjoyment.com/imag2.jpg"
		};
		final String[] tagsName = {
			"mejorTag", "chollazo"
		};

		final Object testingData[][] = {
			{
				"user1", "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, IllegalArgumentException.class
			}, {
				null, "Nuevo chollo", "http://working4enjoyment.com/chollo", productImages, "El mejor chollo", 20, "sdkjs89", false, 123.0, 300.0, "category1", tagsName, IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateCreateURL((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String[]) testingData[i][3], (String) testingData[i][4], (Integer) testingData[i][5], (String) testingData[i][6],
				(Boolean) testingData[i][7], (Double) testingData[i][8], (Double) testingData[i][9], (String) testingData[i][10], (String[]) testingData[i][11], (Class<?>) testingData[i][12]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollo de la compañía.
	 * 3. Listar las categorías.
	 * 4. Escoger una categoría.
	 * 5. Crear chollo
	 */

	protected void templateCreate(final String userName, final String productName, final String productUrl, final String[] productImagesNames, final String description, final Integer estimatedSells, final String discountCode, final Boolean isPublished,
		final Double minimumPrice, final Double originalPrice, final String categoryName, final String[] tagsName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Page<Category> categories;
		Integer countCategories;
		Category category;
		Category categoryChoosen;
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

			//Inicializamos
			categoryChoosen = null;
			category = this.categoryService.findOne(super.getEntityId(categoryName));

			//Vemos las notificaciones, luego tendrán otra más
			userNumberOfNotifications = new HashMap<Actor, Integer>();
			for (final Actor actor : this.userService.findWithGoldPremium()) {
				numberNotifications = this.notificationService.countNotVisitedByActorId(actor.getId());
				userNumberOfNotifications.put(actor, numberNotifications);
			}

			//Obtenemos los bargains
			bargains = this.bargainService.findByCompanyId(1, 5);
			Assert.notNull(bargains);

			//Obtenemos la colección de Category
			categories = this.categoryService.findAllPaginated(1, 1);
			countCategories = categories.getTotalPages();

			//Buscamos la categoría
			for (int i = 0; i < countCategories; i++) {

				categories = this.categoryService.findAllPaginated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (categories.getContent().size() == 0)
					break;

				// Navegar hasta la category que queremos.
				for (final Category newCategory : categories.getContent())
					if (newCategory.equals(category)) {
						categoryChoosen = newCategory;
						break;
					}

				if (categoryChoosen != null)
					break;
			}

			//Ya tenemos la category
			Assert.notNull(categoryChoosen);

			//Creamos bargain
			bargainForm = new BargainForm();

			bargainForm.setCategoryId(categoryChoosen.getId());

			bargain = this.bargainService.create();

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

			//Comprobamos que se crea y llegan las notificaciones
			Assert.notNull(this.bargainService.findOne(saved.getId()));

			//Comprobamos que el precio se actualiza solo
			Assert.isTrue(saved.getPrice() == originalPrice);

			for (final Actor actor : userNumberOfNotifications.keySet())
				Assert.isTrue(userNumberOfNotifications.get(actor) + 1 == this.notificationService.countNotVisitedByActorId(actor.getId()));

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
	 * 2. Crear bargain por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateCreateURL(final String userName, final String productName, final String productUrl, final String[] productImagesNames, final String description, final Integer estimatedSells, final String discountCode, final Boolean isPublished,
		final Double minimumPrice, final Double originalPrice, final String categoryName, final String[] tagsName, final Class<?> expected) {
		Class<?> caught;

		Category category;
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

			//Inicializamos
			category = this.categoryService.findOne(super.getEntityId(categoryName));

			//Vemos las notificaciones, luego tendrán las mismas
			userNumberOfNotifications = new HashMap<Actor, Integer>();
			for (final Actor actor : this.userService.findWithGoldPremium()) {
				numberNotifications = this.notificationService.countNotVisitedByActorId(actor.getId());
				userNumberOfNotifications.put(actor, numberNotifications);
			}

			//Creamos bargain
			bargainForm = new BargainForm();

			bargainForm.setCategoryId(category.getId());

			bargain = this.bargainService.create();

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

			//Comprobamos que no se crea y llegan las notificaciones
			Assert.isNull(this.bargainService.findOne(saved.getId()));

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

}
