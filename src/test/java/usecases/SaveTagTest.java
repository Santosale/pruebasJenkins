package usecases;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

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
public class SaveTagTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private TagService					tagService;

	@Autowired
	private BargainService				bargainService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 
	 * Probamos la creación de varios evaluations por parte de diferentes compañías.
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
	public void positiveSaveTagTest() {
		final Object testingData[][] = {
				{
					"company1", "Tag de prueba", "bargain1", "bargain2", null
				} , {
					"company2", "Etiqueta nueva", "bargain3", "bargain4", null
				} , {
					"company3", "Muebles", "bargain7", "bargain4", null
				} , {
					"company4", "Casa", "bargain3", "bargain4", null
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 
	 * 1. Creamos una etiqueta logueados como usuario. Solo puede editarlo una compañía o un moderador.
	 * 2. Creamos una etiqueta sin estar logueados. Solo puede editarlo una compañía o un moderador.
	 * 3. Creamos una etiqueta logueados como sponsor. Solo puede editarlo una compañía o un moderador.
	 * 4. Name no puede ser vacío.
	 * 5. Name no puede ser null.
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
	public void negativeSaveTagTest() {
		final Object testingData[][] = {
				{
					"user1", "Tag de prueba", "bargain11", "bargain12", IllegalArgumentException.class
				} , {
					null, "Etiqueta nueva", "bargain9", "bargain8", IllegalArgumentException.class
				} , {
					"sponsor1", "Casa", "bargain6", "bargain7", IllegalArgumentException.class
				} , {
					"company3", "", "bargain1", "bargain10", ConstraintViolationException.class
				} , {
					"company4", null, "bargain1", "bargain10", ConstraintViolationException.class
				}
			};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
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
	 * 1. Nos autentificamos como el usuario user
	 * 2. Tomamos el id de writer
	 * 3. Tomamos la entidad correspondiente a al id de writer
	 * 4. Creamos una nueva evaluation pasando el user como parámetros
	 * 5. Le asignamos los parametros correspondientes
	 * 6. Guardamos la nueva evaluación
	 * 7. Nos desautentificamos
	 * 8. Comprobamos se ha creado y existe
	 */
	protected void template(final String user, final String name, final String bargain1, final String bargain2, final Class<?> expected) {
		Class<?> caught;
		Integer bargain1Id, bargain2Id;
		Bargain bargain1Entity, bargain2Entity;
		Collection<Bargain> bargains;
		Tag tag;

		caught = null;
		try {
			super.authenticate(user);
			bargain1Id = super.getEntityId(bargain1);
			bargain1Entity = this.bargainService.findOne(bargain1Id);
			bargain2Id = super.getEntityId(bargain2);
			bargain2Entity = this.bargainService.findOne(bargain2Id);
			
			bargains = new HashSet<Bargain>();
			tag = this.tagService.create(bargain1Entity);
			tag.setName(name);
			bargains.addAll(tag.getBargains());
			bargains.add(bargain2Entity);
			tag = this.tagService.save(tag);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.tagService.findAll().contains(tag));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
	}

}