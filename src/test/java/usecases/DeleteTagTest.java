package usecases;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Bargain;
import domain.Tag;
import services.BargainService;
import services.TagService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class DeleteTagTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private TagService		tagService;
	
	@Autowired
	private BargainService		bargainService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * 	Primero se realizarán las pruebas desde un listado y luego
	 *  como si accedemos a la entidad desde getEntityId:
	 * 	1. Probando que el company1 borra la bargain1
	 * 	2. Probando que el moderator1 borra la bargain2
	 * 	3. Probando que el company3 borra la bargain3
	 * 	4. Probando que el moderator1 borra la bargain4
	 * 
	 * Requisitos:
	 * 3.	Las empresas pueden asignar etiquetas a los chollos. 
	 * 		El sistema debe guardar solamente el nombre de la etiqueta.
	 * 23.	Un actor que está autenticado como empresa debe ser capaz de: 
	 *		2.	Durante la creación de los chollos, se pueden asociar 
	 *			etiquetas ya existentes o añadir tus propias etiquetas.
	 *		3.	Gestionar las etiquetas editando el chollo.
	 *
	 */
	@Test
	public void positiveDeleteTagTest() {
		final Object testingData[][] = {
			{
				"company1", "bargain1", null
			} , {
				"moderator1", "bargain2", null
			} , {
				"company3", "bargain3", null
			} , {
				"moderator1", "bargain4", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	
	for (int i = 0; i < testingData.length; i++)
		try {
			super.startTransaction();
			this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		} catch (final Throwable oops) {
			throw new RuntimeException(oops);
		} finally {
			super.rollbackTransaction();
		}
	}
	
	/*
	 * Pruebas:
	 * 
	 * Primero se realizarán las pruebas desde un listado y luego
	 * como si accedemos a la entidad desde getEntityId:
	 * 
	 * 1. No puede borrarlo un usuario no logueado
	 * 2. Solo puede borrarlo una compañía o un moderador
	 * 3. Solo puede borrarlo una compañía o un moderador
	 * 4. Solo puede borrarlo una compañía o un moderador
	 * 5. Solo puede borrarlo una compañía o un moderador
	 * 
	 * Requisitos:
	 * 3.	Las empresas pueden asignar etiquetas a los chollos. 
	 * 		El sistema debe guardar solamente el nombre de la etiqueta.
	 * 23.	Un actor que está autenticado como empresa debe ser capaz de: 
	 *		2.	Durante la creación de los chollos, se pueden asociar 
	 *			etiquetas ya existentes o añadir tus propias etiquetas.
	 *		3.	Gestionar las etiquetas editando el chollo.
	 *
	 */
	 @Test
	public void negativeDeleteTagTest() {
		final Object testingData[][] = {
			{
				null, "bargain5", IllegalArgumentException.class 
			}, 	{
				"administrator", "bargain6", IllegalArgumentException.class
			}, {
				"sponsor3", "bargain7", IllegalArgumentException.class 
			}, {
				"user3", "bargain8", IllegalArgumentException.class 
			} , {
				"user1", "bargain9", IllegalArgumentException.class 
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.templateNoList((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
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
		 * 2. Tomamos el id y la entidad de user
		 * 3. Accedemos a la lista de tags y tomamos la que nos interesa
		 * 4. Borramos el tag
		 * 5. Nos desautentificamos
		 */
	protected void template(final String user, final String bargain, final Class<?> expected) {
		Class<?> caught;
		int bargainId;
		Bargain bargainEntity;
		Collection<Bargain> bargains;
		Collection<Tag> tags;

		bargainEntity = null;
		caught = null;
		try {
			super.authenticate(user);
			bargainId = super.getEntityId(bargain);
			Assert.notNull(bargainId);
			
			bargains = this.bargainService.findBargains(1, 99, "all", 0).getContent();
			for (Bargain b : bargains){
				if (b.getId() == bargainId)
					bargainEntity = b;
			}
			
			tags = this.tagService.findByBargainId(bargainEntity.getId());
			
			for (Tag t : tags) {
				if (t.getBargains().size() == 1)
					Assert.isTrue(this.tagService.findAll().contains(t));
				Assert.isTrue(t.getBargains().contains(bargainEntity));
			}
			
			this.tagService.deleteByBargain(bargainEntity);
			
			tags = this.tagService.findByBargainId(bargainEntity.getId());
			
			Assert.isTrue(tags.size() == 0);
			
			super.unauthenticate();
			super.flushTransaction();
			
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}
	/*
	 * 	Pasos:
	 * 
	 * 1. Nos autentificamos como user
	 * 2. Tomamos el id y la entidad de tag
	 * 3. Borramos la evaluación
	 * 4. Nos desautentificamos
	 * 5. Comprobamos que no existe la evaluación borrado
	 */
	protected void templateNoList(final String user, final String bargain, final Class<?> expected) {
		Class<?> caught;
		int bargainId;
		Bargain bargainEntity;
		Collection<Tag> tags;

		caught = null;
		try {
			super.authenticate(user);
			bargainId = super.getEntityId(bargain);
			bargainEntity = this.bargainService.findOne(bargainId);
			
			tags = this.tagService.findByBargainId(bargainEntity.getId());
			
			for (Tag t : tags) {
				if (t.getBargains().size() == 1)
					Assert.isTrue(this.tagService.findAll().contains(t));
				Assert.isTrue(t.getBargains().contains(bargainEntity));
			}
			
			this.tagService.deleteByBargain(bargainEntity);
			
			tags = this.tagService.findByBargainId(bargainEntity.getId());
			
			Assert.isTrue(tags.size() == 0);
			
			super.unauthenticate();
			
			super.flushTransaction();
		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}

