
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
public class ListBargainsByCategoryTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private CategoryService	categoryService;

	@Autowired
	private BargainService	bargainService;


	// Tests ------------------------------------------------------------------

	//Pruebas
	/*
	 * 1. Listamos las categorias y escogemos uno publico sin estar autenticados
	 * 2. Listamos las categorias y escogemos uno publico como sponsor
	 * 3. Listamos las categorias y escogemos uno publico como admin
	 * 4. Listamos las categorias y escogemos uno publico como moderator
	 * 5. Listamos las categorias y escogemos uno publico como user
	 * 6. Listamos las categorias y escogemos uno publico como company
	 * 7. Listamos las categorias y escogemos uno no publico como company distinta a la creadora. IllegalArgumentException
	 * 8. Listamos las categorias y escogemos uno que no es de esa category. IllegalArgumentException
	 * 9. Listamos las categorias y escogemos uno no publico con user gold.
	 */

	//CU 21. 8.	Navegar entre las categorías accediendo a sus chollos.

	@Test
	public void driverBargainsByCategory() {

		//Rol, grandfatherCategoryName, fatherName, categorName, bargainName, ExpectedException
		final Object testingData[][] = {
			{
				null, null, "category2", "category4", "bargain1", null
			}, {
				"sponsor1", null, "category2", "category4", "bargain1", null
			}, {
				"admin", null, "category2", "category4", "bargain1", null
			}, {
				"moderator1", null, "category2", "category4", "bargain1", null
			}, {
				"user1", null, "category2", "category4", "bargain1", null
			}, {
				"company1", null, "category2", "category4", "bargain1", null
			}, {
				"company2", null, null, "category2", "bargain11", IllegalArgumentException.class
			}, {
				"company2", null, null, "category2", "bargain4", IllegalArgumentException.class
			}, {
				"user2", null, null, "category2", "bargain11", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateBargainsByCategory((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (Class<?>) testingData[i][5]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar las categorías de la raíz.
	 * 3. Navegar hasta la category que queremos seleccionar.
	 * 3. Ver sus bargains
	 */
	protected void templateBargainsByCategory(final String userName, final String grandfatherCategoryName, final String fatherCategoryName, final String categoryName, final String bargainName, final Class<?> expected) {
		Class<?> caught;
		Category category;
		List<String> categoriesSearch;
		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			bargainChoosen = null;

			//Comprobamos si la categoria padre es null para evitar NullPointerException
			if (categoryName != null) {
				//Navegamos para seleccionar la categoría
				categoriesSearch = new ArrayList<String>();
				categoriesSearch.add(grandfatherCategoryName);
				categoriesSearch.add(fatherCategoryName);
				categoriesSearch.add(categoryName);

				this.searchCategory(categoriesSearch);

			}

			category = this.categoryService.findOne(super.getEntityId(categoryName));
			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			//Obtenemos los bargains
			bargains = this.bargainService.findBargains(1, 1, "category", category.getId());
			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findBargains(i + 1, 1, "category", category.getId());

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

			this.bargainService.findOneToDisplay(bargainChoosen.getId());

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

}
