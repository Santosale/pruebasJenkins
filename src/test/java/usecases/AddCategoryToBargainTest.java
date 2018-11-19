
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.BargainService;
import services.CategoryService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Category;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AddCategoryToBargainTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService	bargainService;

	@Autowired
	private CategoryService	categoryService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Añadir category a bargain publicado
	 * 2. Añadir category a bargain no publicado
	 */
	//CU:4.	Los chollos pertenecen a categorías. Para cada categoría, el sistema debe guardar un nombre, único entre las categorías de un mismo nivel, y una imagen. 

	@Test
	public void driverPositive() {

		//userName, categoryName, bargainName, expected
		final Object testingData[][] = {
			{
				"company1", "category1", "bargain1", null
			}, {
				"company2", "category1", "bargain12", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateAddCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Añadir category a bargain que ya está añadida. IllegalArgumentException
	 * 2. Añadir category a bargain que no es de esa compañía. IllegalArgumentException
	 * 2. Añadir category sin estar autenticado. IllegalArgumentException
	 */
	//CU:4.	Los chollos pertenecen a categorías. Para cada categoría, el sistema debe guardar un nombre, único entre las categorías de un mismo nivel, y una imagen. 

	@Test
	public void driverNegative() {

		//userName, categoryName, bargainName, expected
		final Object testingData[][] = {
			{
				"company1", "category2", "bargain1", IllegalArgumentException.class
			}, {
				"company2", "category1", "bargain1", IllegalArgumentException.class
			}, {
				null, "category1", "bargain1", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateAddCategoryUrl((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollos (compañía).
	 * 3. Escoger un chollo.
	 * 4. Añadirle una etiqueta
	 */

	protected void templateAddCategory(final String userName, final String categoryName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;
		Category category;
		Category categoryChoosen;
		Integer countCategories;
		Page<Category> categories;
		int countBargainsCategory;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;
			categoryChoosen = null;

			bargain = this.bargainService.findOne(super.getEntityId(bargainName));
			category = this.categoryService.findOne(super.getEntityId(categoryName));
			countBargainsCategory = category.getBargains().size();

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

			Assert.notNull(bargainChoosen);

			this.bargainService.findOneToDisplay(bargainChoosen.getId());

			//Obtenemos las categories
			categories = this.categoryService.findByNotBargainId(bargain, 1, 1);

			countCategories = categories.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countCategories; i++) {
				categories = this.categoryService.findByNotBargainId(bargain, 1 + i, 1);

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

			Assert.notNull(categoryChoosen);

			this.categoryService.addBargain(bargainChoosen, categoryChoosen);

			this.categoryService.flush();

			//Comprobamos que tenga un bargain más
			Assert.isTrue(this.categoryService.findOne(category.getId()).getBargains().size() == countBargainsCategory + 1);

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
	 * 2. Intentar asignar una category a un chollo sin usar la interfaz de la app.
	 */

	protected void templateAddCategoryUrl(final String userName, final String categoryName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Bargain bargain;
		Category category;
		int countBargainsCategory;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			bargain = this.bargainService.findOne(super.getEntityId(bargainName));
			category = this.categoryService.findOne(super.getEntityId(categoryName));
			countBargainsCategory = category.getBargains().size();

			this.categoryService.addBargain(bargain, category);

			this.categoryService.flush();

			//Comprobamos que tenga un bargain más
			Assert.isTrue(this.categoryService.findOne(category.getId()).getBargains().size() == countBargainsCategory);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}
}
