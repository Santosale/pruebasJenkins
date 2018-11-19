
package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import services.LevelService;
import utilities.AbstractTest;
import domain.Level;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SaveLevelTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private LevelService	levelService;


	/*
	 * Pruebas:
	 * 1. Creamos un nivel con el rango menor al nivel mínimo sin dejar rango vacío (no salta excepción)
	 * 2. Creamos un nivel con el rango menor al nivel mínimo sin dejando rango vacío (no salta excepción)
	 * 3. Creamos un nivel con el rango mayor al nivel máximo sin dejar rango vacío (no salta excepción)
	 * 4. Creamos un nivel con el rango mayor al nivel máximo dejando rango vacío (no salta excepción)
	 * 
	 * Requisito 30.4: Un actor autenticado como admin puede editar los niveles de puntuación de los usuarios y borrarlos cuando hay más de dos niveles en el sistema..
	 */
	@Test()
	public void testSavePositive() {
		final Object testingData[][] = {
			{
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, null
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1040, null
			}, {
				"admin", "admin", "Nivel superior", "https://www.imagenes.com/imagen1", 10000, 20000, null
			}, {
				"admin", "admin", "Nivel superior", "https://www.imagenes.com/imagen1", 10050, 20000, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas
	 * 1. Intentamos crear un nivel con el nombre y la imagen vacíos (salta un ConstraintViolationException)
	 * 2. Intentamos crear un nivel con la imagen vacía (salta un ConstraintViolationException)
	 * 3. Intentamos crear un nivel con el nombre vacío (salta un ConstraintViolationException)
	 * 4. Intentamos crear un nivel con el nombre y la imagen a null (salta un ConstraintViolationException)
	 * 5. Intentamos crear un nivel siendo el minPoints más grande que el maxPoints (salta un IllegalArgumentException)
	 * 6. Intentamos crear un nivel entrando en el rango del menor nivel al poner los maxPoints a 1000 (salta un IllegalArgumentException)
	 * 7. Intentamos crear un nivel entrando en el rango de niveles existentes(salta un IllegalArgumentException)
	 * 8. Intentamos crear un nivel entrando completamente en el rango de niveles existentes(salta un IllegalArgumentException)
	 * 9. Intentamos crear un nivel entrando en el rango de niveles existentes en el límite con el máximo nivel ya que los minPoints son 9999(salta un IllegalArgumentException)
	 * 10.Intentamos crear un nivel entrando en el rango de niveles existentes por arriba(salta un IllegalArgumentException)
	 * 11.Intentamos crear un nivel con un rango fuera de los niveles existentes pero con el minPoints mayor que los maxPoints (salta un IllegalArgumentException)
	 * 12.Intentamos crear un nivel como user (salta un IllegalArgumentException)
	 * 13.Intentamos crear un nivel como sponsor (salta un IllegalArgumentException)
	 * 14.Intentamos crear un nivel como moderator (salta un IllegalArgumentException)
	 * 15.Intentamos crear un nivel como company (salta un IllegalArgumentException)
	 * 16.Intentamos crear un nivel sin estar autenticado (salta un IllegalArgumentException)
	 */
	@Test()
	public void testSaveNegative() {
		final Object testingData[][] = {
			{
				"admin", "admin", "", "", -2000, -1001, ConstraintViolationException.class
			}, {
				"admin", "admin", "eufeu", "", -2000, -1001, ConstraintViolationException.class
			}, {
				"admin", "admin", "", "https://www.imagenes.com/imagen1", -2000, -1001, ConstraintViolationException.class
			}, {
				"admin", "admin", null, null, -2000, -1001, ConstraintViolationException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", -1500, -1600, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", -1500, -1000, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, 4, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", 3, 4, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", 9999, 10005, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", 9000, 10005, IllegalArgumentException.class
			}, {
				"admin", "admin", "Nivel inferior", "https://www.imagenes.com/imagen1", 10006, 10002, IllegalArgumentException.class
			}, {
				"user", "user1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, IllegalArgumentException.class
			}, {
				"company", "company1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, IllegalArgumentException.class
			}, {
				null, null, "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1001, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (int) testingData[i][4], (int) testingData[i][5], (Class<?>) testingData[i][6]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	protected void template(final String user, final String username, final String name, final String image, final int minPoints, final int maxPoints, final Class<?> expected) {
		Class<?> caught;
		Level level;
		Level saved;
		int minPointsBefore;
		int maxPointsBefore;

		DataBinder binder;
		Level levelReconstruct;

		caught = null;
		minPointsBefore = this.levelService.minPoints();
		maxPointsBefore = this.levelService.maxPoints();
		try {
			if (user != null)
				super.authenticate(username); //Nos logeamos si es necesario

			level = this.levelService.create(); //Creamos el nivel

			level.setName(name);
			level.setImage(image);
			level.setMinPoints(minPoints);
			level.setMaxPoints(maxPoints);
			//Editamos los valores

			binder = new DataBinder(level);
			levelReconstruct = this.levelService.reconstruct(level, binder.getBindingResult()); //Lo reconstruimos
			saved = this.levelService.save(levelReconstruct); //Guardamos el nivel
			super.flushTransaction();
			if (level.getMaxPoints() < (minPointsBefore - 1))
				Assert.isTrue(this.levelService.findByPoints(minPointsBefore).getMinPoints() == (saved.getMaxPoints() + 1)); //Vemos que los rangos son consistentes
			else if (level.getMinPoints() > (maxPointsBefore + 1))
				Assert.isTrue(this.levelService.findByPoints(maxPointsBefore).getMaxPoints() == (saved.getMinPoints() - 1)); //Vemos que los rangos son consistentes

			Assert.isTrue(this.levelService.findAll().contains(saved)); //Miramos si están entre todos los niveles de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}
}
