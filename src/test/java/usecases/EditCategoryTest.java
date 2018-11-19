
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.CategoryService;
import utilities.AbstractTest;
import domain.Category;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditCategoryTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private CategoryService	categoryService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Actualizar Category con padre, excepción no esperada.
	 */

	//CU 26. 3.	Gestionar las categorías.
	@Test
	public void driverUpdatePositive() {

		//Rol, granFatherCategory, fatherCategory, categoryForUpdate, name, url, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", null, null, "category3", "Name Category28", "http://web.com/img.jpg", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateUpdate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	//Pruebas
	/*
	 * 1. Actualizar Category sin estar autenticado, IllegalArgumentException esperada.
	 * 2. Actualizar Category con rol admin, IllegalArgumentException esperada.
	 */

	//CU 26. 3.	Gestionar las categorías.
	@Test
	public void driverUpdateNegativeStatementConstraint() {

		//Rol, categoryForUpdate, name, url, ExpectedException
		final Object testingData[][] = {
			{
				null, "category2", "Name Category28", "http://web.com/img.jpg", IllegalArgumentException.class
			}, {
				"admin", "category2", "Name Category28", "http://web.com/img.jpg", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateUpdateUrl((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	//Pruebas
	/*
	 * 1. Actualizar Category con nombre a null, ConstraintViolationException esperada.
	 * 2. Actualizar Category con nombre vacío, ConstraintViolationException esperada.
	 * 3. Actualizar Category con url a null, ConstraintViolationException esperada.
	 * 4. Actualizar Category con url errónea, ConstraintViolationException esperada.
	 * 5. Actualizar Category sin padre, y nombre igual. IllegalArgumentException esperada.
	 * 6. Actualizar Category con padre, y nombre igual. IllegalArgumentException esperada.
	 */

	//CU 26. 3.	Gestionar las categorías. 
	@Test
	public void driverUpdateDataConstraint() {

		//Rol, grandfatherCategory, fatherCategory, categoryForUpdate, name, url, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", null, null, "category2", null, "http://image/web.png", ConstraintViolationException.class
			}, {
				"moderator2", null, null, "category2", "", "http://image/web.png", ConstraintViolationException.class
			}, {
				"moderator1", null, null, "category2", "Name Category28", null, ConstraintViolationException.class
			}, {
				"moderator2", null, null, "category2", "Name Category28", "url", ConstraintViolationException.class
			}, {
				"moderator2", null, null, "category2", "Hogar", "http://sdf.dsg", IllegalArgumentException.class
			}, {
				"moderator2", null, "category2", "category4", "Portátiles", "http://sdf.dsg", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateUpdate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	//Pruebas
	/*
	 * 1. Actualizar default category. Excepcion no esperada
	 * 2. Actualizar intentamos actualizar el father. Excepcion no esperada (no se actualiza por el reconstruct)
	 */

	//CU 26. 3.	Gestionar las categorías.
	@Test
	public void driverUpdateDefaultCategory() {

		//Rol, newfatherCategory, categoryForUpdate, defaultCategory, name, url, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", null, "category1", true, "Updating default category", "http://web.com/img.jpg", null
			}, {
				"moderator1", "category2", "category1", true, "Updating default category", "http://web.com/img.jpg", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateUpdateDefaultCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar las categorías de la raíz.
	 * 3. Navegar hasta la categry que queremos seleccionar.
	 * 3. Escoger una y actualizarla.
	 */
	protected void templateUpdate(final String userName, final String grandfatherCategoryName, final String fatherCategoryName, final String categoryName, final String name, final String image, final Class<?> expected) {
		Class<?> caught;
		Category category;
		Category saved;
		List<String> categoriesSearch;
		DataBinder binder;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Navegamos para seleccionar la categoría
			categoriesSearch = new ArrayList<String>();
			categoriesSearch.add(grandfatherCategoryName);
			categoriesSearch.add(fatherCategoryName);
			categoriesSearch.add(categoryName);

			category = this.searchCategory(categoriesSearch);

			category = this.copyCategory(category);
			category.setImage(image);
			category.setName(name);
			category.setDefaultCategory(true);

			binder = new DataBinder(category);

			saved = this.categoryService.reconstruct(category, binder.getBindingResult());
			saved = this.categoryService.save(saved);
			this.categoryService.flush();

			Assert.isTrue(!saved.getDefaultCategory());
			Assert.notNull(saved.getBargains());

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
	 * 2. Actualizar categoría sin usar formularios de la app.
	 */
	protected void templateUpdateUrl(final String userName, final String categoryName, final String name, final String image, final Class<?> expected) {
		Class<?> caught;
		Category category;
		Category saved;
		DataBinder binder;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			category = this.categoryService.findOne(this.getEntityId(categoryName));

			category = this.copyCategory(category);
			category.setImage(image);
			category.setName(name);
			category.setDefaultCategory(true);

			binder = new DataBinder(category);

			saved = this.categoryService.reconstruct(category, binder.getBindingResult());
			saved = this.categoryService.save(saved);
			this.categoryService.flush();

			Assert.isTrue(!saved.getDefaultCategory());

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
	 * 2. Listar las categorías de la raíz.
	 * 3. Escoger una y actualizarla.
	 */
	protected void templateUpdateDefaultCategory(final String userName, final String newFatherName, final String categoryName, final Boolean defaultCategory, final String name, final String image, final Class<?> expected) {
		Class<?> caught;
		Category category;
		Category fatherBefore;
		Category saved;
		DataBinder binder;
		Collection<Category> categories;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Navegamos para seleccionar la categoría
			categories = this.categoryService.findAllWithoutFather();
			category = this.categoryService.findOne(this.getEntityId(categoryName));

			for (final Category c : categories)
				if (c.equals(category))
					category = this.copyCategory(category);

			category.setImage(image);
			category.setName(name);
			category.setDefaultCategory(defaultCategory);

			fatherBefore = null;
			if (newFatherName != null) {
				fatherBefore = this.copyCategory(category.getFatherCategory());
				category.setFatherCategory(this.categoryService.findOne(this.getEntityId(newFatherName)));
			}

			binder = new DataBinder(category);

			saved = this.categoryService.reconstruct(category, binder.getBindingResult());
			saved = this.categoryService.save(saved);
			this.categoryService.flush();

			if (defaultCategory)
				Assert.isTrue(saved.getDefaultCategory());
			else
				Assert.isTrue(!saved.getDefaultCategory());

			Assert.isTrue((saved.getFatherCategory() == null && fatherBefore == null) || saved.getFatherCategory().equals(fatherBefore));

			this.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	//Auxiliary Methods
	private Category searchCategory(final List<String> categoriesName) {
		Category category;
		Collection<Category> categories;
		Category result;

		result = null;

		//Categoría a buscar
		for (final String categoryName : categoriesName)
			if (categoryName != null) {

				category = this.categoryService.findOne(this.getEntityId(categoryName));

				//Cojo el padre donde debe estar la category a buscar
				if (category.getFatherCategory() == null)
					categories = this.categoryService.findAllWithoutFather();
				else
					categories = this.categoryService.findAllByFatherCategoryId(category.getFatherCategory().getId());

				//Recogemos el resultado cuando se encuentre
				for (final Category categoryIterator : categories)
					if (categoryIterator.equals(category)) {
						result = categoryIterator;
						break;
					}
			}

		return result;
	}

	private Category copyCategory(final Category category) {
		Category result;

		if (category == null)
			result = null;
		else {
			result = this.categoryService.create(category);

			result.setImage(category.getImage());
			result.setFatherCategory(category.getFatherCategory());
			result.setId(category.getId());
			result.setName(category.getName());
			result.setVersion(category.getVersion());
			result.setDefaultCategory(category.getDefaultCategory());

		}

		return result;
	}
}
