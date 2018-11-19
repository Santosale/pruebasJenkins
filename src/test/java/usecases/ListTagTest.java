
package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.TagService;
import utilities.AbstractTest;
import domain.Tag;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListTagTest extends AbstractTest {
		
	// System under test ------------------------------------------------------

	@Autowired
	private TagService	tagService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAll logueados como moderator1
	 * 2. Probamos obtener el resultado previsto para el metodo findAll sin loguear
	 * 3. Probamos obtener el resultado previsto para el metodo findAll logueados como company2
	 * 4. Probamos no obtener el resultado previsto para el metodo findAll logueados como user3
	 * 5. Probamos no obtener el resultado previsto para el metodo findAll logueados como administrator
	 * 
	 * Requisitos:
	 * 3.	Las empresas pueden asignar etiquetas a los chollos. 
	 * 		El sistema debe guardar solamente el nombre de la etiqueta.
	 * 23.	Un actor que está autenticado como empresa debe ser capaz de: 
	 *		2.	Durante la creación de los chollos, se pueden asociar 
	 *			etiquetas ya existentes o añadir tus propias etiquetas.
	 *		3.	Gestionar las etiquetas editando el chollo.
	 */
	@Test
	public void findAllTest() {
		final Object testingData[][] = {
			{
				"moderator1", "findAll", 11, null, 0, null
			} , {
				null, "findAll", 11, null, null, null
			} , {
				"company2", "findAll", 11, null, null, null
			} , {
				"user3", "findAll", 5, null, null, IllegalArgumentException.class
			} , {
				"administrator", "findAll", 3, null, null, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto para el metodo findAllPaginated logueados como moderator1
	 * 2. Probamos obtener el resultado previsto para el metodo findAllPaginated sin loguear
	 * 3. Probamos obtener el resultado previsto para el metodo findAllPaginated logueados como company2
	 * 4. Probamos no obtener el resultado previsto para el metodo findAllPaginated logueados como user3
	 * 5. Probamos no obtener el resultado previsto para el metodo findAllPaginated logueados como administrator
	 * 
	 * Requisitos:
	 * 3.	Las empresas pueden asignar etiquetas a los chollos. 
	 * 		El sistema debe guardar solamente el nombre de la etiqueta.
	 * 23.	Un actor que está autenticado como empresa debe ser capaz de: 
	 *		2.	Durante la creación de los chollos, se pueden asociar 
	 *			etiquetas ya existentes o añadir tus propias etiquetas.
	 *		3.	Gestionar las etiquetas editando el chollo.
	 */
	@Test
	public void findAllPaginatedTest() {
		final Object testingData[][] = {
			{
				"moderator1", "findAllPaginated", 5, 0, 0, null
			} , {
				null, "findAllPaginated", 5, 0, 0, null
			} , {
				"company2", "findAllPaginated", 1, 2, 1, null
			} , {
				"user3", "findAllPaginated", 7, 5, 5, IllegalArgumentException.class
			} , {
				"administrator", "findAllPaginated", 3, 5, 5, IllegalArgumentException.class
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Integer) testingData[i][3], (Integer) testingData[i][4], (Class<?>) testingData[i][5]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Comprobamos si el método es findAll ó findByUserAccountId
	 * 3. En el caso de que sea findByUserAccountId, obtenemos las entidades correspondientes al user para usar el método
	 * 3. Según el método que sea, se llama a su método y se guarda en la variable sizeSubscription el tamaño de los resultados de cada método
	 * 4. Comprobamos que devuelve el valor esperado
	 * 5. Nos desautentificamos
	 */
	protected void template(final String user, final String method, final Integer tamano, final Integer page, final Integer size, final Class<?> expected) {
		Class<?> caught;
		Collection<Tag> tagsCollection;
		int sizeTag;

		sizeTag = 0;
		caught = null;
		try {
			super.authenticate(user);

			if (method.equals("findAll")) {
				tagsCollection = this.tagService.findAll();
				sizeTag = tagsCollection.size();
			} else if (method.equals("findAllPaginated")) {
				tagsCollection = this.tagService.findAllPaginated(page, size).getContent();
				sizeTag = tagsCollection.size();
			} 
			Assert.isTrue(sizeTag == tamano); 
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
