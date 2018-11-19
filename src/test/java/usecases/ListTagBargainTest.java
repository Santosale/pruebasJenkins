
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
import services.TagService;
import utilities.AbstractTest;
import domain.Bargain;
import domain.Tag;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ListTagBargainTest extends AbstractTest {
		
	// System under test ------------------------------------------------------

	@Autowired
	private TagService	tagService;
	
	@Autowired
	private BargainService	bargainService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto logueados como user2 (usuario gold) y listar un chollo solo disponible para usuarios gold
	 * 2. Probamos obtener el resultado previsto sin loguear y listar un chollo normal
	 * 3. Probamos obtener el resultado previsto logueados como company2 y listar un chollo normal
	 * 4. Probamos obtener el resultado previsto logueados como sponsor1 y listar un chollo normal
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
	public void driverPositiveListTagBargain() {
		final Object testingData[][] = {
			{
				"user2", "tag2", "bargain12", null
			} , {
				null, "tag2", "bargain5", null
			}, {
				"company2", "tag2", "bargain7", null
			}, {
				"sponsor1", "tag4", "bargain10", null
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
	
	/*
	 * Pruebas:
	 * 
	 * 1. Probamos obtener el resultado previsto logueados como user1 (usuario no gold) y listar un chollo solo disponible para usuarios gold
	 * 2. Probamos obtener el resultado previsto sin loguear y listar un chollo normal que no está en la etiqueta que entramos
	 * 3. Probamos obtener el resultado previsto logueados como company1 y listar un chollo para usuarios gold
	 * 4. Probamos obtener el resultado previsto logueados como sponsor1 y listar un chollo normal que no se halla en la etiqueta
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
	public void driverNegativeListTagBargain() {
		final Object testingData[][] = {
				{
					"user1", "tag2", "bargain12", IllegalArgumentException.class
				} , {
					null, "tag3", "bargain5", IllegalArgumentException.class
				}, {
					"company1", "tag2", "bargain12", IllegalArgumentException.class
				}, {
					"sponsor1", "tag4", "bargain1", IllegalArgumentException.class
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
	protected void template(final String user, final String tag, final String bargain, final Class<?> expected) {
		Class<?> caught;
		Page<Tag> tags;
		int countTags;
		int tagId, bargainId;
		Tag tagChoosen, tagEntity;
		Page<Bargain> bargains;
		int countBargains;
		Bargain bargainChoosen;
		Bargain bargainEntity;

		tagChoosen = null;
		bargainChoosen = null;
		caught = null;
		try {
			super.authenticate(user);
			
			tagId = this.getEntityId(tag);
			tagEntity = this.tagService.findOne(tagId);
			
			bargainId = this.getEntityId(bargain);
			bargainEntity = this.bargainService.findOne(bargainId);

			tags = this.tagService.findAllPaginated(1, 1);
			countTags = tags.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countTags; i++) {

				tags = this.tagService.findAllPaginated(i + 1, 5);

				//Si estamos pidiendo una página mayor
				if (tags.getContent().size() == 0)
					break;

				// Navegar hasta la tag que queremos.
				for (final Tag newTag : tags.getContent())
					if (newTag.equals(tagEntity)) {
						tagChoosen = newTag;
						break;
					}
				
				if (tagChoosen != null)
					break;
			}

			//Ya tenemos la etiqueta
			
			Assert.notNull(tagChoosen);
							
			//Obtenemos los bargains
			bargains = this.bargainService.findBargains(1, 5, "tag", tagEntity.getId());
			countBargains = bargains.getTotalPages();

			//Buscamos el que queremos modificar
			for (int i = 0; i < countBargains; i++) {

				bargains = this.bargainService.findBargains(i + 1, 5, "tag", tagEntity.getId());

				//Si estamos pidiendo una página mayor
				if (bargains.getContent().size() == 0)
					break;

				// Navegar hasta el bargain que queremos.
				for (final Bargain newBargain : bargains.getContent())
					if (newBargain.equals(bargainEntity)) {
						bargainChoosen = newBargain;
						break;
					}

				if (bargainChoosen != null)
					break;
			}

			//Ya tenemos el bargain
			Assert.notNull(bargainChoosen);
			
			this.bargainService.findOneToDisplay(bargainChoosen.getId());
			
			super.unauthenticate();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
