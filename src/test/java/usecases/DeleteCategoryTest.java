
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.CategoryService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Category;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteCategoryTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private CategoryService	categoryService;


	//Fixtures

	// Tests ------------------------------------------------------------------
	//Pruebas
	/*
	 * 2. Borrar Category sin padre, con hijos excepción no esperada.
	 * 4. Borrar Category con padre, sin hijos excepción no esperada.
	 */

	//CU 26. 3.	Gestionar las categorías.
	@Test
	public void driverDeleteFatherAndChildren() {

		//Rol, grandFatherCategory, fatherCategory, category, hasFather, hasChildren, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", null, null, "category2", false, true, null
			}, {
				"moderator1", null, null, "category7", true, false, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteFatherAndChildrenAllCases((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Boolean) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);

	}

	//Pruebas
	/*
	 * 1. Borrar Category que es la única de un bargain, excepción no esperada.
	 * 2. Borrar Category que tiene más de un bargain, excepción no esperada.
	 * 3. Borrar Category que no tiene bargains, excepción no esperada.
	 */

	//CU //CU 26. 3.Gestionar las categorías.
	@Test
	public void driverDeleteAndUpdateBargain() {

		//Rol,granfatherCategory, fatherCategory, category, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", null, "category2", "category6", null
			}, {
				"moderator1", null, null, "category2", null
			}, {
				"moderator1", null, null, "category9", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteAndUpdateService((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	//Pruebas
	/*
	 * 1. Borrar Category estando autenticados como un admin, excepción IllegalArgumentException.
	 * 2. Borrar Category sin estar autenticados, excepción IllegalArgumentException.
	 * 3. Borrar default Category, excepción IllegalArgumentException.
	 */

	//CU 26. 3.	Gestionar las categorías.
	@Test
	public void driverDeleteNegativeTest() {

		//Rol, category, ExpectedException
		final Object testingData[][] = {
			{
				"admin", "category5", IllegalArgumentException.class
			}, {
				null, "category2", IllegalArgumentException.class
			}, {
				"moderator1", "category1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDeleteNegativeTest((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * Borrar Category comprobando borrado de esta. Pasos:
	 * 1. Autenticarnos.
	 * 2. Listar las category.
	 * 3. Navegar hasta la category que queramos.
	 * 4. Escoger category.
	 */
	protected void templateDeleteFatherAndChildrenAllCases(final String userName, final String grandfatherCategoryName, final String fatherCategoryName, final String categoryName, final Boolean hasFather, final Boolean hasChildren, final Class<?> expected) {
		Class<?> caught;
		Category category;
		Collection<Category> categoriesIfHasChildren;
		Collection<Category> categoriesIfHasFather;
		Category fatherCategory;
		List<String> categoriesSearch;

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

			category = this.categoryService.findOne(this.getEntityId(categoryName));
			fatherCategory = null;
			categoriesIfHasChildren = new ArrayList<Category>();

			//Comprobamos que al borrar, sus hijos se borren y no sea hija de su padre
			//Si tenia hijos
			if (hasChildren)
				categoriesIfHasChildren = this.getAllDescendant(category);

			//Si tenia padres
			if (hasFather)
				fatherCategory = category.getFatherCategory();

			this.categoryService.delete(category);
			this.categoryService.flush();

			//Vemos lo ocurrido después

			Assert.isNull(this.categoryService.findOne(category.getId()));

			if (hasChildren)
				for (final Category c : categoriesIfHasChildren)
					Assert.isNull(this.categoryService.findOne(c.getId()));

			if (hasFather) {
				categoriesIfHasFather = this.getAllDescendant(fatherCategory);
				Assert.isTrue(!categoriesIfHasFather.contains(category));
			}

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * Borrar Category comprobando cambio services. Pasos:
	 * 1. Autenticarnos.
	 * 2. Listar las category.
	 * 3. Navegar hasta la category que queramos.
	 * 4. Escoger category.
	 * 5. Borrar category.
	 */
	protected void templateDeleteAndUpdateService(final String userName, final String grandfatherCategoryName, final String fatherCategoryName, final String categoryName, final Class<?> expected) {
		Class<?> caught;
		final Category categoryChoosen;
		List<String> categoriesSearch;
		Page<Category> categories;
		Category category;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Navegamos para seleccionar la categoría
			categoriesSearch = new ArrayList<String>();
			categoriesSearch.add(grandfatherCategoryName);
			categoriesSearch.add(fatherCategoryName);
			categoriesSearch.add(categoryName);

			categoryChoosen = this.searchCategory(categoriesSearch);

			//Sacamos las categorias de un bargain
			category = this.categoryService.findOne(this.getEntityId(categoryName));

			this.categoryService.delete(categoryChoosen);
			this.categoryService.flush();

			//Vemos que se haya borrado
			Assert.isNull(this.categoryService.findOne(categoryChoosen.getId()));

			//Si tenía servicios vemos que ya no tengan dicha categoría
			for (final Bargain bargain : category.getBargains()) {
				categories = this.categoryService.findByBargainId(bargain.getId(), 1, 1);
				Assert.isTrue(this.categoryService.findByBargainId(bargain.getId(), 1, categories.getTotalPages()).getContent().size() >= 1);
			}

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * Borramos una category. Pasos:
	 * 1. Autenticarnos.
	 * 2. Intentar borrar esa category
	 */
	protected void templateDeleteNegativeTest(final String userName, final String categoryName, final Class<?> expected) {
		Class<?> caught;
		Category category;

		caught = null;

		try {
			super.startTransaction();

			//1. Autenticarnos
			this.authenticate(userName);

			//2. Intentar borrar esa category
			category = this.categoryService.findOne(this.getEntityId(categoryName));

			this.categoryService.delete(category);
			this.categoryService.flush();

			//Vemos que se haya borrado
			Assert.isNull(this.categoryService.findOne(category.getId()));

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	//Auxilary Methods
	private Collection<Category> getAllDescendant(final Category category) {
		Collection<Category> result;
		Collection<Category> categoriesAux;
		Collection<Category> categoriesAuxChildren;

		result = new ArrayList<Category>();
		categoriesAuxChildren = new ArrayList<Category>();

		//Cogemos los primeros hijos
		categoriesAux = this.categoryService.findAllByFatherCategoryId(category.getId());
		do {

			//Si no es la primera vez, actualizamos el bucle
			if (categoriesAux.size() == 0) {
				categoriesAux = new ArrayList<Category>(categoriesAuxChildren);
				categoriesAuxChildren.clear();
			}

			//Lo añadimos al general
			result.addAll(categoriesAux);

			//Cogemos categoryAux, las que aún no hemos recorrido y añadimos sus hijos para recorrerlos, eliminamos las que recorremos
			for (final Category c : categoriesAux)
				categoriesAuxChildren.addAll(this.categoryService.findAllByFatherCategoryId(c.getId()));

			categoriesAux.clear();
		} while (categoriesAuxChildren.size() != 0);

		return result;
	}

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

}
