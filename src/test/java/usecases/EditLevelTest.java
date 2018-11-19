
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
public class EditLevelTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private LevelService	levelService;


	/*
	 * Pruebas:
	 * 1. Editamos el nivel1 disminuyendo la puntuaci�n m�nima (no salta excepci�n)
	 * 2. Editamos el nivel1 disminuyendo la puntuaci�n m�nima y la m�xima (no salta excepci�n)
	 * 3. Editamos el nivel5 aumentando la puntuaci�n m�xima (no salta excepci�n)
	 * 4. Editamos el nivel5 aumentando la puntuaci�n m�xima y la m�nima (no salta excepci�n)
	 * 5. Editamos el nivel2 disminuyendo la puntuaci�n m�xima (no salta excepci�n)
	 * 6. Editamos el nivel2 aumentando la puntuaci�n m�nima (no salta excepci�n)
	 * 7. Editamos el nivel2 aumentando la puntuaci�n m�nima y disminuyendo la m�xima (no salta excepci�n)
	 * 
	 * Requisito 30.4: Un usuario autenticado como admin puede editar los niveles de puntuaci�n de los usuarios y borrarlos cuando hay m�s de dos niveles en el sistema.
	 */
	@Test()
	public void testEditPositive() {
		final Object testingData[][] = {
			{
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1, null
			}, {
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, null
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 1000, 10000, null
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 1100, 15000, null
			}, {
				"admin", "admin", "level2", "Nivel medio", "https://www.imagenes.com/imagen1", 0, 88, null
			}, {
				"admin", "admin", "level2", "Nivel medio", "https://www.imagenes.com/imagen1", 7, 100, null
			}, {
				"admin", "admin", "level2", "Nivel medio", "https://www.imagenes.com/imagen1", 7, 77, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (Class<?>) testingData[i][7]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Pruebas:
	 * 1. Editamos el nivel1 dejando el nombre y la imagen vac�a (salta un ConstraintViolationException)
	 * 2. Editamos el nivel1 la imagen vac�a (salta un ConstraintViolationException)
	 * 3. Editamos el nivel1 dejando el nombre vac�o (salta un ConstraintViolationException)
	 * 4. Editamos el nivel1 dejando el nombre y la imagen a nulos (salta un ConstraintViolationException)
	 * 5. Editamos el nivel1 dejando la imagen con un formato erroneo (salta un ConstraintViolationException)
	 * 6. Editamos el nivel1 poniendo los puntos m�nimos mayores que los m�ximos (salta un IllegalArgumentException)
	 * 7. Editamos el nivel1 poniendo los puntos m�ximos en el inicio del rango del siguiente nivel (salta un IllegalArgumentException)
	 * 8. Editamos el nivel1 poniendo los puntos m�ximos en el rango del siguiente nivel (salta un IllegalArgumentException)
	 * 9. Editamos el nivel1 poniendo los puntos m�ximos y m�nimos en el inicio del rango del siguiente nivel (salta un IllegalArgumentException)
	 * 10.Editamos el nivel5 poniendo los puntos m�ximos menores que los puntos m�nimos (salta un IllegalArgumentException)
	 * 11.Editamos el nivel1 poniendo los puntos m�nimos en el final del rango del anterior nivel (salta un IllegalArgumentException)
	 * 12.Editamos el nivel1 poniendo los puntos m�nimos en el rango del anterior nivel (salta un IllegalArgumentException)
	 * 13.Editamos el nivel1 poniendo los puntos m�nimos y m�ximos en el final del rango del anterior nivel (salta un IllegalArgumentException)
	 * 14.Editamos el nivel2 poniendo los puntos m�nimos en el final del rango del anterior nivel (salta un IllegalArgumentException)
	 * 15.Editamos el nivel2 poniendo los puntos m�ximos en el inicio del rango del siguiente nivel (salta un IllegalArgumentException)
	 * 16.Editamos el nivel2 poniendo los puntos m�nimos en el rango del anterior nivel (salta un IllegalArgumentException)
	 * 17.Editamos el nivel2 poniendo los puntos m�ximos en el rango del siguiente nivel (salta un IllegalArgumentException)
	 * 18.Editamos el nivel2 poniendo los puntos m�nimos mayores que los puntos m�ximos (salta un IllegalArgumentException)
	 * 19.Editamos el nivel1 autenticados como user (salta un IllegalArgumentException)
	 * 20.Editamos el nivel1 autenticados como sponsor (salta un IllegalArgumentException)
	 * 21.Editamos el nivel1 autenticados como moderator (salta un IllegalArgumentException)
	 * 22.Editamos el nivel1 autenticados como company (salta un IllegalArgumentException)
	 * 23.Editamos el nivel1 sin estar autenticados (salta un IllegalArgumentException)
	 */
	@Test()
	public void testEditNegative() {
		final Object testingData[][] = {
			{
				"admin", "admin", "level1", "", "", -2000, -1, ConstraintViolationException.class
			}, {
				"admin", "admin", "level1", "edwwe", "", -2000, -1, ConstraintViolationException.class
			}, {
				"admin", "admin", "level1", "", "http://www.imagenes.com/imagen1", -2000, -1, ConstraintViolationException.class
			}, {
				"admin", "admin", "level1", null, null, -2000, -1, ConstraintViolationException.class
			}, {
				"admin", "admin", "level1", "frfe", "rege", -2000, -1, ConstraintViolationException.class
			}, {
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -99, -100, IllegalArgumentException.class
			}, {
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -100, 0, IllegalArgumentException.class
			}, {
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -88, 5, IllegalArgumentException.class
			}, {
				"admin", "admin", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", 5, 6, IllegalArgumentException.class
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 9999, 1005, IllegalArgumentException.class
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 999, 10000, IllegalArgumentException.class
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 998, 10000, IllegalArgumentException.class
			}, {
				"admin", "admin", "level5", "Nivel superior", "https://www.imagenes.com/imagen1", 996, 997, IllegalArgumentException.class
			}, {
				"admin", "admin", "level2", "Nivel intermedio", "https://www.imagenes.com/imagen1", -1, 100, IllegalArgumentException.class
			}, {
				"admin", "admin", "level2", "Nivel intermedio", "https://www.imagenes.com/imagen1", 0, 101, IllegalArgumentException.class
			}, {
				"admin", "admin", "level2", "Nivel intermedio", "https://www.imagenes.com/imagen1", -2, 100, IllegalArgumentException.class
			}, {
				"admin", "admin", "level2", "Nivel intermedio", "https://www.imagenes.com/imagen1", 0, 102, IllegalArgumentException.class
			}, {
				"admin", "admin", "level2", "Nivel intermedio", "https://www.imagenes.com/imagen1", 30, 20, IllegalArgumentException.class
			}, {
				"user", "user1", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, IllegalArgumentException.class
			}, {
				"sponsor", "sponsor1", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, IllegalArgumentException.class
			}, {
				"moderator", "moderator1", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, IllegalArgumentException.class
			}, {
				"company", "company1", "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, IllegalArgumentException.class
			}, {
				null, null, "level1", "Nivel inferior", "https://www.imagenes.com/imagen1", -2000, -1000, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (int) testingData[i][5], (int) testingData[i][6], (Class<?>) testingData[i][7]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	/*
	 * Test
	 * 1.Logeados como admin hacemos el findOneToEdit del level1 (no salta excepci�n)
	 * 2.Logeados como admin hacemos el findOneToEdit del level2 (no salta excepci�n)
	 * 3.Logeados como moderator1 hacemos el findOne del level1 (no salta excepci�n)
	 * 4.Logeados como moderator1 hacemos el findOneToEdit del level1(salta un IllegalArgumentException)
	 * 5.Logeados como admin hacemos el findOneToEdit de una nivel de id 0 (salta un IllegalArgumentException)
	 * 6.Logeados como admin hacemos el findOne de una nivel de id 0 (salta un IllegalArgumentException)
	 */
	@Test
	public void testFindOneFindOneToEdit() {
		final Object testingData[][] = {
			{
				"admin", "admin", "level1", false, true, null
			}, {
				"admin", "admin", "level2", false, true, null
			}, {
				"moderator", "moderator1", "level1", false, false, null
			}, {
				"moderator", "moderator1", "level1", false, true, IllegalArgumentException.class
			}, {
				"admin", "admin", "level1", true, false, IllegalArgumentException.class
			}, {
				"admin", "admin", "level1", true, false, IllegalArgumentException.class
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindOneFindOneToEdit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	/*
	 * Test
	 * 1.Con -1001 puntos eres nivel1
	 * 2.Con -32 puntos eres nivel1
	 * 3.Con -1 puntos eres nivel1
	 * 4.Con 0 puntos eres nivel2
	 * 5.Con 50 puntos eres nivel2
	 * 6.Con 100 puntos eres nivel2
	 * 7.Con 101 puntos eres nivel3
	 * 8.Con 250 puntos eres nivel3
	 * 9.Con 500 puntos eres nivel3
	 * 10.Con 501 puntos eres nivel4
	 * 11.Con 800 puntos eres nivel4
	 * 12.Con 999 puntos eres nivel4
	 * 13.Con 1000 puntos eres nivel5
	 * 14.Con 4500 puntos eres nivel5
	 * 15.Con 9999 puntos eres nivel5
	 * 16.Con 10000 puntos eres nivel5
	 */
	@Test
	public void testFindByPoints() {
		final Object testingData[][] = {
			{
				"admin", "admin", "level1", -1001, null
			}, {
				"admin", "admin", "level1", -32, null
			}, {
				"admin", "admin", "level1", -1, null
			}, {
				"admin", "admin", "level2", 0, null
			}, {
				"admin", "admin", "level2", 50, null
			}, {
				"admin", "admin", "level2", 100, null
			}, {
				"admin", "admin", "level3", 101, null
			}, {
				"admin", "admin", "level3", 250, null
			}, {
				"admin", "admin", "level3", 500, null
			}, {
				"admin", "admin", "level4", 501, null
			}, {
				"admin", "admin", "level4", 800, null
			}, {
				"admin", "admin", "level4", 999, null
			}, {
				"admin", "admin", "level5", 1000, null
			}, {
				"admin", "admin", "level5", 4500, null
			}, {
				"admin", "admin", "level5", 9999, null
			}, {
				"admin", "admin", "level5", 10000, null
			}
		};
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateFindByPoints((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (int) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	protected void template(final String user, final String username, final String levelBean, final String name, final String image, final int minPoints, final int maxPoints, final Class<?> expected) {
		Class<?> caught;
		Level level;
		Level saved;
		Level copyLevel;
		int minPointsBefore;
		int maxPointsBefore;
		int minPointsLevelBefore;
		int maxPointsLevelBefore;
		int levelIdAux;
		int levelId;

		DataBinder binder;
		Level levelReconstruct;

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
				for (final Level l : this.levelService.findAllPaginated(i, 5).getContent())
					if (l.getId() == levelIdAux)
						levelId = l.getId();

			level = this.levelService.findOneToEdit(levelId); //Creamos el nivel
			copyLevel = this.copyLevel(level);
			copyLevel.setName(name);
			copyLevel.setImage(image);
			copyLevel.setMinPoints(minPoints);
			copyLevel.setMaxPoints(maxPoints);
			//Editamos los valores

			binder = new DataBinder(copyLevel);
			levelReconstruct = this.levelService.reconstruct(copyLevel, binder.getBindingResult()); //Lo reconstruimos
			saved = this.levelService.save(levelReconstruct); //Guardamos el nivel
			super.flushTransaction();
			if (minPointsBefore == minPointsLevelBefore) {
				if (copyLevel.getMaxPoints() < (maxPointsLevelBefore - 1))
					Assert.isTrue(this.levelService.findByPoints(maxPointsLevelBefore + 1).getMinPoints() == (saved.getMaxPoints() + 1)); //Vemos que los rangos son consistentes
			} else if (maxPointsBefore == maxPointsLevelBefore) {
				if (copyLevel.getMinPoints() > (minPointsLevelBefore + 1))
					Assert.isTrue(this.levelService.findByPoints(minPointsLevelBefore - 1).getMaxPoints() == (saved.getMinPoints() - 1)); //Vemos que los rangos son consistentes

			} else if (copyLevel.getMinPoints() > (minPointsLevelBefore + 1) && copyLevel.getMaxPoints() == maxPointsLevelBefore)
				Assert.isTrue(this.levelService.findByPoints(minPointsLevelBefore - 1).getMaxPoints() == (saved.getMinPoints() - 1)); //Vemos que los rangos son consistentes
			else if (copyLevel.getMinPoints() == minPointsLevelBefore && copyLevel.getMaxPoints() < (maxPointsLevelBefore - 1))
				Assert.isTrue(this.levelService.findByPoints(maxPointsLevelBefore + 1).getMinPoints() == (saved.getMaxPoints() + 1)); //Vemos que los rangos son consistentes
			else if (copyLevel.getMinPoints() > (minPointsLevelBefore + 1) && copyLevel.getMaxPoints() < (maxPointsLevelBefore - 1)) {
				Assert.isTrue(this.levelService.findByPoints(minPointsLevelBefore - 1).getMaxPoints() == (saved.getMinPoints() - 1)); //Vemos que los rangos son consistentes
				Assert.isTrue(this.levelService.findByPoints(maxPointsLevelBefore + 1).getMinPoints() == (saved.getMaxPoints() + 1)); //Vemos que los rangos son consistentes
			}
			Assert.isTrue(this.levelService.findAll().contains(saved)); //Miramos si est�n entre todos los niveles de la BD

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	public Level copyLevel(final Level level) {
		Level result;

		result = new Level();
		result.setName(level.getName());
		result.setImage(level.getImage());
		result.setMinPoints(level.getMinPoints());
		result.setMaxPoints(level.getMaxPoints());
		result.setId(level.getId());
		result.setVersion(level.getVersion());

		return result;
	}

	protected void templateFindOneFindOneToEdit(final String user, final String username, final String levelBean, final boolean falseId, final boolean findOneToEdit, final Class<?> expected) {
		Class<?> caught;
		int levelId;
		Level level;
		int levelIdAux;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			levelId = 0;
			levelIdAux = super.getEntityId(levelBean);
			for (int i = 1; i <= this.levelService.findAllPaginated(1, 5).getTotalPages(); i++)
				for (final Level l : this.levelService.findAllPaginated(i, 5).getContent())
					if (l.getId() == levelIdAux)
						levelId = l.getId();

			if (findOneToEdit == true) {
				if (falseId == false)
					level = this.levelService.findOneToEdit(levelId); //Se prueba el findOneToEdit
				else
					level = this.levelService.findOneToEdit(0); //Se prueba el findOneEdit con id 0 

			} else if (falseId == false)
				level = this.levelService.findOne(levelId); //Se prueba el findOne
			else
				level = this.levelService.findOne(0); //Se prueba el findOne con id 0
			Assert.notNull(level); //Se mira que exista
			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

	protected void templateFindByPoints(final String user, final String username, final String levelBean, final int points, final Class<?> expected) {
		Class<?> caught;
		Level level;

		caught = null;
		try {
			if (user != null)
				super.authenticate(username);//Nos logeamos si es necesario

			level = this.levelService.findOne(super.getEntityId(levelBean));

			Assert.isTrue(this.levelService.findByPoints(points).equals(level)); //Se compara el nivel a partir de los puntos con el nivel esperado

			super.flushTransaction();

			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();
		super.checkExceptions(expected, caught);
	}

}
