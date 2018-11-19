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

import domain.Sponsor;
import forms.SponsorForm;

import services.SponsorService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EditSponsorTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SponsorService		sponsorService;
	
	// Tests ------------------------------------------------------------------

	/*
	 * Pruebas:
	 * 		1. Probando editar compañía con telefono y direccion a null
	 * 		2. Probando editar compañía con telefono pero con direccion a null
	 * 		3. Probando editar compañía con telefono a vacio y direccion a null
	 * 		4. Probando editar compañía con telefono a null y direccion
	 * 		5. Probando editar compañía con telefono a null y direccion a vacio
	 * 		6. Probando editar compañía con telefono y direccion
	 * 		7. Probando editar compañía con telefono y direccion a vacio
	 * 
	 * Requsitos:
	 * 		23.6. Un actor que está autenticado como empresa debe ser capaz de 
	 * 		listar las rifas que ha creado, editarlas y borrarlas 
	 * 		si todavía nadie ha comprado un tique.	
	 */
	@Test
	public void positiveEditSponsorTest() {
		final Object testingData[][] = {
			{
				"sponsor1", "sponsor1", "sponsor1", "sponsor1", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", null 
			}, {
				"sponsor2", "sponsor2", "sponsor2", "sponsor2", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", null
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Carlos", "Sánchez", null, null, "carlossponsor@mail.com", "20063918Y", null
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Carlos", "Sánchez", null, "Calle Real Nº6", "paquito@mail.com", "20063918Y", null 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Carlos", "Sánchez", null, "", "manolete@mail.com", "20063918Y", null 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Carlos", "Sánchez", "321456987", "Direccion incorrecta", "pepe@mail.com", "20063918Y", null 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null
			}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Solo un usuario registrado puede registrarse a si mismo
	 * 		2. Solo un usuario registrado puede registrarse a si mismo
	 * 		3. Solo un usuario registrado puede registrarse a si mismo
	 * 		4. El EmailAddress tiene que tener el formato de un EmailAddress
	 * 		5. El nombre no puede ser nulo
	 * 		6. El apellido no puede ser nulo
	 * 		7. El nombre no puede ser vacio
	 * 		8. El apellido no puede ser vacio
	 * 		9. El EmailAddress no puede ser nulo
	 * 		10. El EmailAddress no puede ser vacio
	 * 		11. El identificador debe cumplir el patrón
	 * 		12. El identificador no puede estar vacío
	 * 		13. El identificador no puede ser nulo
	 * 
	 * Requisitos:
	 * 	 	23.6. Un actor que está autenticado como empresa debe ser capaz de 
	 * 		listar las rifas que ha creado, editarlas y borrarlas 
	 * 		si todavía nadie ha comprado un tique.	
	 */
	@Test
	public void negativeEditSponsorTest() {
		final Object testingData[][] = {
			{
				"sponsor1", "sponsor2", "sponsor2", "sponsor2", "Antonio", "Azana", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
			}, {
				"admin", "sponsor2", "sponsor2", "sponsor2", "Antonio", "Azana", "652147893", null, "ant@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"manager1", "sponsor2", "sponsor2", "sponsor2", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", "20063918Y", IllegalArgumentException.class
			}, {
				"sponsor1", "sponsor1", "sponsor1", "sponsor1", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", ConstraintViolationException.class 
			}, {
				"sponsor2", "sponsor2", "sponsor2", "sponsor2", null, "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class
			}, {
				"sponsor1", "sponsor1", "sponsor1", "sponsor1", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "", "Azana", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class
			}, {
				"sponsor1", "sponsor1", "sponsor1", "sponsor1", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
			},{
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Carlos", "Sánchez", "664857123", "Calle Novena", null, "20063918Y", ConstraintViolationException.class 
			}, {
				"sponsor1", "sponsor1", "sponsor1", "sponsor1", "Maria", "Villarin", "664254123", "Inserte direccion", "", "20063918Y", ConstraintViolationException.class 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Francisco", "Cerrada", null, "", "fran@mail.com", "20063-A-A918Y", ConstraintViolationException.class 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Francisco", "Cerrada", null, "", "fran@mail.com", "", ConstraintViolationException.class 
			}, {
				"sponsor3", "sponsor3", "sponsor3", "sponsor3", "Francisco", "Cerrada", null, "", "fran@mail.com", null, ConstraintViolationException.class 
			}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Class<?>) testingData[i][10]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * 	Editar una compañía
	 * 	Pasos:
	 * 		1. Autenticar como compañía
	 * 		2. Entramos en la vista de editar compañía
	 * 		3. Le asignamos los datos introducidos
	 * 		4. Guardamos la compañía
	 * 		5. Cerramos sesión
	 * 		6. Comprobamos que se ha editado y con los datos correspondientes
	 */
	protected void template(final String sponsorBean, final String sponsorEdit, final String username, final String password, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final Class<?> expected) {
		Class<?> caught;
		Sponsor reconstructedSponsor, sponsorSave, sponsor, sponsorEntity;
		SponsorForm sponsorForm;
		Integer sponsorId;
		DataBinder binder;

		caught = null;
		try {
			
			super.authenticate(sponsorBean);
			
			sponsorId = super.getEntityId(sponsorEdit);
			Assert.notNull(sponsorId);
			sponsorEntity = this.sponsorService.findOne(sponsorId);
			Assert.notNull(sponsorEntity);
			sponsorForm = this.copySponsorToForm(sponsorEntity);
			sponsorForm.setUsername(username);
			sponsorForm.setPassword(password);
			sponsorForm.setName(name);
			sponsorForm.setSurname(surname);
			sponsorForm.setPhone(phone);
			sponsorForm.setEmail(email);
			sponsorForm.setAddress(address);
			sponsorForm.setIdentifier(identifier);
			
			binder = new DataBinder(sponsorForm);
			reconstructedSponsor = this.sponsorService.reconstruct(sponsorForm, binder.getBindingResult());
			sponsorSave = this.sponsorService.save(reconstructedSponsor);
			Assert.notNull(sponsorSave);
			
			this.sponsorService.flush();
			
			sponsor = this.sponsorService.findOne(sponsorSave.getId());
			Assert.notNull(sponsor);
			Assert.notNull(sponsor.getUserAccount().getUsername());
			Assert.notNull(sponsor.getUserAccount().getPassword());
			Assert.notNull(sponsor.getName());
			Assert.isTrue(sponsor.getName().equals(name));
			Assert.notNull(sponsor.getSurname());
			Assert.isTrue(sponsor.getSurname().equals(surname));
			if(address != null && !address.equals(""))Assert.isTrue(sponsor.getAddress().equals(address));
			Assert.notNull(sponsor.getEmail());
			Assert.isTrue(sponsor.getEmail().equals(email));
			if(phone != null && !phone.equals(""))Assert.isTrue(sponsor.getPhone().equals(phone));
			
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.sponsorService.findAll().contains(sponsor));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	private SponsorForm copySponsorToForm(final Sponsor sponsor) {
		SponsorForm result;

		result = new SponsorForm();
		result.setId(sponsor.getId());
		result.setName(sponsor.getName());
		result.setSurname(sponsor.getSurname());
		result.setAddress(sponsor.getAddress());
		result.setEmail(sponsor.getEmail());
		result.setPhone(sponsor.getPhone());
		result.setIdentifier(sponsor.getIdentifier());

		return result;
	}

}