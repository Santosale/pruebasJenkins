
package usecases;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.LevelService;
import utilities.AbstractTest;
import domain.Level;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteLevelTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private LevelService	levelService;


	/*
	 * Pruebas:
	 * 1. Borramos el nivel1 (no salta excepción)
	 * 2. Borramos el nivel2 (no salta excepción)
	 * 3. Borramos el nivel3 (no salta excepción)
	 * 4. Borramos el nivel4 (no salta excepción)
	 * 5. Borramos el nivel5 (no salta excepción)
	 * 6. Borramos el nivel1 autenticados como user (salta un IllegalArgumentException)
	 * 7. Borramos el nivel1 autenticados como sponsor (salta un IllegalArgumentException)
	 * 8. Borramos el nivel1 autenticados como comoany (salta un IllegalArgumentException)
	 * 9. Borramos el nivel1 sin estar autenticado (salta un IllegalArgumentException)
	 * 
	 * Requisito 30.4: Un actor autenticado como admin puede editar los niveles de puntuación de los usuarios y borrarlos cuando hay más de dos niveles en el sistema.
	 */
	@Test()
	public void testDelete() {
		final Object testingData[][] = {
			{
				"admin", "admin", "level1", null
			}, {
				"admin", "admin", "level2", null
			}, {
				"admin", "admin", "level3", null
			}, {
				"admin", "admin", "level4", null
			}, {
				"admin", "admin", "level5", null
			}, {
				"user", "user1", "level1", IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "level1", IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "level1", IllegalArgumentException.class
			}, {
				"company", "company1", "level1", IllegalArgumentException.class
			}, {
				null, null, "level1", IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String levelBean, final Class<?> expected) {
		Class<?> caught;
		Level level;
		int minPointsBefore;
		int maxPointsBefore;
		int minPointsLevelBefore;
		int maxPointsLevelBefore;
		int levelIdAux;
		int levelId;

		caught = null;
		minPointsBefore = this.levelService.minPoints();
		maxPointsBefore = this.levelService.maxPoints();
		minPointsLevelBefore = this.levelService.findOne(super.getEntityId(levelBean)).getMinPoints();
		maxPointsLevelBefore = this.levelService.findOne(super.getEntityId(levelBean)).getMaxPoints();
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			levelIdAux = super.getEntityId(levelBean);
			levelId = 0;
			for (int i = 1; i <= this.levelService.findAllPaginated(1, 5).getTotalPages(); i++)
				//Lo buscamos entre todos los niveles
				for (final Level l : this.levelService.findAllPaginated(i, 5).getContent())
					if (l.getId() == levelIdAux)
						levelId = l.getId();

			level = this.levelService.findOneToEdit(levelId); //Cogemos el nivel

			this.levelService.delete(level); // Borramos
			super.flushTransaction();
			if (minPointsBefore != minPointsLevelBefore && maxPointsBefore != maxPointsLevelBefore) {
				Assert.isTrue(this.levelService.findByPoints(minPointsLevelBefore - 1).getMaxPoints() == ((level.getMinPoints() + level.getMaxPoints()) / 2)); //Vemos que los rangos son consistentes
				Assert.isTrue(this.levelService.findByPoints(maxPointsLevelBefore + 1).getMinPoints() == ((level.getMinPoints() + level.getMaxPoints()) / 2) + 1); //Vemos que los rangos son consistentes
			}
			Assert.isTrue(!this.levelService.findAll().contains(level)); //Miramos si están entre todos los niveles de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
