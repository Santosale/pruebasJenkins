
package usecases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.CategoryService;
import utilities.AbstractTest;
import domain.Category;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ReorganisingCategoryTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private CategoryService	categoryService;


	// Tests ------------------------------------------------------------------
	//Pruebas
	/*
	 * 1. Reorganizar Category sin padre, a una Category padre sin padre.
	 * 2. Reorganizar Category sin padre, a una Category padre con padre.
	 * 3. Reorganizar Category con padre, a una Category padre sin padre.
	 * 4. Reorganizar Category con padre, a una Category padre con padre.
	 * 5. Reorganizar Category colocándola en la raíz sin hijos.
	 * 6. Reorganizar Category colocándola en la raíz con hijos.
	 */

	//CU 26. 3.	Gestionar las categorías y reorganizarlas.
	@Test
	public void driverReorganisingPositive() {

		//Rol, categoryToMove, categoryNewFather, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", "category6", "category2", null
			}, {
				"moderator1", "category6", "category3", null
			}, {
				"moderator1", "category3", "category6", null
			}, {
				"moderator1", "category3", "category8", null
			}, {
				"moderator1", "category8", null, null
			}, {
				"moderator1", "category2", null, null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateReorganising((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	//Pruebas
	/*
	 * 1. Reorganizar Category siendo su nuevo padre ella misma. IllegalArgumentException esperada.
	 * 2. Reorganizar Category null. IllegalArgumentException esperada.
	 * 3. Reorganizar Category autenticados como user. IllegalArgumentException esperada.
	 * 4. Reorganizar Category sin autenticar. IllegalArgumentException esperada.
	 * 5. Reorganizar default Category. Excepción IllegalArgumentException esperada.
	 */

	//CU 26. 3.	Gestionar las categorías y reorganizarlas.
	@Test
	public void driverReorganisingNegative() {

		//Rol, categoryToMove, categoryNewFather, ExpectedException
		final Object testingData[][] = {
			{
				"moderator1", "category2", "category2", IllegalArgumentException.class
			}, {
				"moderator1", null, "category4", IllegalArgumentException.class
			}, {
				"user1", "category4", "category10", IllegalArgumentException.class
			}, {
				null, "category4", "category10", IllegalArgumentException.class
			}, {
				"moderator1", "category1", "category4", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateReorganising((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	//Plantilla para reorganizar y checkear posibles fallos con dos métodos auxiliares
	/*
	 * 1.Autenticarnos.
	 * 2.Buscar category a mover.
	 * 3.Seleccionar la category.
	 * 4.Buscar la nueva category padre.
	 * 5.Seleccionar la category
	 * 6.Reorganizar
	 */
	protected void templateReorganising(final String userName, final String categoryToMoveName, final String categoryNewFatherName, final Class<?> expected) {
		Class<?> caught;
		Category categoryToMove;
		Category fatherCategoryToMove;
		Category grandfatherCategoryToMove;
		Category fatherNewCategory;
		Category grandfatherNewCategory;
		Category categoryNewFather;
		Category categoryToMoveCopy;
		List<Category> childrenCategoriesAfterChange;
		Collection<Category> childrenCategoriesAux;
		List<Category> categories;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			fatherCategoryToMove = null;
			grandfatherCategoryToMove = null;
			grandfatherNewCategory = null;
			fatherNewCategory = null;

			//Sacamos la categoria a mover
			if (categoryToMoveName != null) {
				categoryToMove = this.categoryService.findOne(this.getEntityId(categoryToMoveName));

				if (categoryToMove.getFatherCategory() != null) {
					fatherCategoryToMove = categoryToMove.getFatherCategory();

					if (fatherCategoryToMove.getFatherCategory() != null)
						grandfatherCategoryToMove = fatherCategoryToMove.getFatherCategory();
				}
				categories = new ArrayList<Category>();
				categories.add(grandfatherCategoryToMove);
				categories.add(fatherCategoryToMove);
				categories.add(categoryToMove);

				categoryToMove = this.searchCategory(categories);

			} else
				categoryToMove = null;

			//Sacamos la nueva categoria padre, si es null
			if (categoryNewFatherName != null) {
				categoryNewFather = this.categoryService.findOne(this.getEntityId(categoryNewFatherName));

				if (categoryNewFather.getFatherCategory() != null) {
					fatherNewCategory = categoryNewFather.getFatherCategory();

					if (fatherNewCategory.getFatherCategory() != null)
						grandfatherNewCategory = fatherNewCategory.getFatherCategory();
				}
				categories = new ArrayList<Category>();
				categories.add(grandfatherNewCategory);
				categories.add(fatherNewCategory);
				categories.add(categoryNewFather);

				categoryNewFather = this.searchCategory(categories);

			} else
				categoryNewFather = null;

			//Copiamos la categoria para que no cambie
			categoryToMoveCopy = this.copyCategory(categoryToMove);

			//Cogemos los hijos de la categoria antes de moverla
			childrenCategoriesAfterChange = new ArrayList<Category>();
			if (categoryToMove != null) {
				childrenCategoriesAux = this.categoryService.findAllByFatherCategoryId(categoryToMove.getId());
				childrenCategoriesAfterChange = new ArrayList<Category>(childrenCategoriesAux);
			}

			this.categoryService.reorganising(categoryToMove, categoryNewFather);
			this.categoryService.flush();

			//Vemos que cumple las restricciones
			this.checkChildrenCategory(categoryToMoveCopy, childrenCategoriesAfterChange);
			this.checkFatherCategoryToMove(categoryToMove, categoryNewFather, categoryToMoveCopy.getFatherCategory());

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	/*
	 * 1.Autenticarnos.
	 * 2.Intentar reorganizar sin usar la interfaz de la app
	 */
	protected void templateReorganisingUrl(final String userName, final String categoryToMoveName, final String categoryNewFatherName, final Class<?> expected) {
		Class<?> caught;
		Category categoryToMove;
		Category categoryNewFather;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			categoryToMove = null;
			categoryNewFather = null;

			//Sacamos la categoria a mover
			if (categoryToMoveName != null)
				categoryToMove = this.categoryService.findOne(this.getEntityId(categoryToMoveName));

			if (categoryNewFatherName != null)
				categoryNewFather = this.categoryService.findOne(this.getEntityId(categoryNewFatherName));

			this.categoryService.reorganising(categoryToMove, categoryNewFather);
			this.categoryService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

	//Auxiliary Methods
	//Miramos que el padre antiguo de la categoría y el padre nuevo no tenga ninguna inconsistencia
	private void checkFatherCategoryToMove(final Category categoryAfterMove, final Category categoryNewFather, final Category categoryOldFather) {
		Collection<Category> childrenCategories;

		//Si el padre no era null, el nuevo padre debe ser ese
		if (categoryNewFather != null)
			Assert.isTrue(categoryAfterMove.getFatherCategory().equals(categoryNewFather));
		else
			Assert.isNull(categoryAfterMove.getFatherCategory());

		//Si el padre no era null, el nuevo padre debe contener como hija la categoría que se ha modificado
		if (categoryNewFather != null) {
			childrenCategories = this.categoryService.findAllByFatherCategoryId(categoryNewFather.getId());
			Assert.isTrue(childrenCategories.contains(categoryAfterMove));

			//Si el padre es null, debe estar contenida en la raíz	
		} else {
			childrenCategories = this.categoryService.findAllWithoutFather();
			Assert.isTrue(childrenCategories.contains(categoryAfterMove));
		}

		//Vemos si el padre ha cambiado
		if (!(categoryNewFather == null && categoryOldFather == null))
			if (categoryNewFather == null || categoryOldFather == null || !categoryNewFather.equals(categoryOldFather))
				//Si ha cambiado vemos si el anterior no es null y en ese caso vemos que no este en sus hijas
				if (categoryOldFather != null) {
					childrenCategories = this.categoryService.findAllByFatherCategoryId(categoryOldFather.getId());
					Assert.isTrue(!childrenCategories.contains(categoryAfterMove));

					//Si el anterior padre es null, como el nuevo no es igual, la categoría no debe estar contenida en la raíz	
				} else {
					childrenCategories = this.categoryService.findAllWithoutFather();
					Assert.isTrue(!childrenCategories.contains(categoryAfterMove));
				}

	}

	//Miramos que a los hijos que tenía la categoría se le haya actualizado su padre
	private void checkChildrenCategory(final Category categoryBeforeMove, final List<Category> childrenCategories) {

		//Si la categoría que se ha movido tenía hijos y padre, estos serán hijos de su padre
		if (categoryBeforeMove.getFatherCategory() != null)
			for (final Category category : childrenCategories)
				Assert.isTrue(category.getFatherCategory().equals(categoryBeforeMove.getFatherCategory()));
		else
			for (final Category category : childrenCategories)
				Assert.isTrue(category.getFatherCategory() == null);
	}

	//Método copia de category
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

	private Category searchCategory(final List<Category> categoriesName) {
		Category category;
		Collection<Category> categories;
		Category result;

		result = null;

		//Categoría a buscar
		for (final Category categoryName : categoriesName)
			if (categoryName != null) {

				category = this.categoryService.findOne(categoryName.getId());

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
