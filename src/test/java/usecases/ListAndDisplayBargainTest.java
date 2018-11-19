
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
import utilities.AbstractTest;
import domain.Bargain;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListAndDisplayBargainTest extends AbstractTest {

	// System under test ------------------------------------------------------
	@Autowired
	private BargainService	bargainService;


	// Tests ------------------------------------------------------------------

	/*
	 * 1. Listar todos los chollos buscar uno publicado sin estar autenticado
	 * 2. Listar chollos de una compañía y coger uno no publicado
	 * 3. Listar los tres chollos con más patrocinadores
	 * 4. Listar todos chollos y coger uno no publicado con user gold
	 * 5. Listar todos chollos y coger uno no publicado con moderator
	 */
	//CU: 21. 3.Listar los chollos.
	@Test
	public void driverPositive() {

		//userName, typeList, entityName, bargainName, expected
		final Object testingData[][] = {
			{
				null, "all", "bargain1", null
			}, {
				"company2", "", "bargain12", null
			}, {
				null, "sponsorship", "bargain1", null
			}, {
				"user2", "all", "bargain11", null
			}, {
				"moderator2", "all", "bargain11", null
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateListDisplay((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	/*
	 * 1. Display chollo no publico sin autenticar. IllegalArgumentException
	 * 2. Display chollo no publico con usuario sin plan gold. IllegalArgumentException
	 * 3. Display chollo no publico con company distinta a la suya. IllegalArgumentException
	 * 4. Display chollo no publico con admin. IllegalArgumentException
	 */
	//CU: 21. 3.Listar los chollos.
	@Test
	public void driverDeleteNegative() {

		//userName, bargainName, expected
		final Object testingData[][] = {
			{
				null, "bargain12", IllegalArgumentException.class
			}, {
				"user1", "bargain12", IllegalArgumentException.class
			}, {
				"company1", "bargain12", IllegalArgumentException.class
			}, {
				"admin", "bargain11", IllegalArgumentException.class
			},
		};

		for (int i = 0; i < testingData.length; i++)

			this.templateDisplayUrl((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	/*
	 * 1. Autenticarnos.
	 * 2. Listar los chollos (todos, tres sponsorships, compañía).
	 * 3. Escoger un chollo.
	 */

	protected void templateListDisplay(final String userName, final String typeList, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Page<Bargain> bargains;
		Integer countBargains;
		Bargain bargainChoosen;
		Bargain bargain;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			//Inicializamos
			bargainChoosen = null;

			bargain = this.bargainService.findOne(super.getEntityId(bargainName));

			//Obtenemos los bargains
			if (!typeList.equals(""))
				bargains = this.bargainService.findBargains(1, 1, typeList, 0);
			else
				bargains = this.bargainService.findByCompanyId(1, 1);

			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {
				if (!typeList.equals(""))
					bargains = this.bargainService.findBargains(i + 1, 5, typeList, 0);
				else
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
	 * 2. Acceder a bargain por URL. Sin usar los formularios de la aplicación
	 */
	protected void templateDisplayUrl(final String userName, final String bargainName, final Class<?> expected) {
		Class<?> caught;

		Integer bargainId;

		caught = null;

		try {
			super.startTransaction();
			this.authenticate(userName);

			bargainId = super.getEntityId(bargainName);

			this.bargainService.findOneToDisplay(bargainId);

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		} finally {
			super.rollbackTransaction();
		}

		this.checkExceptions(expected, caught);

	}

}
