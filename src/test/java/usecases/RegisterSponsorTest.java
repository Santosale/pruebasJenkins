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
public class RegisterSponsorTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private SponsorService		sponsorService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar compañía con telefono y dirección a null
	 *		2. Probando registrar compañía con telefono pero con dirección a null
	 * 		3. Probando registrar compañía con telefono a vacío y dirección a null
	 * 		4. Probando registrar compañía con telefono a null y dirección
	 * 		5. Probando registrar compañía con telefono a null y dirección a vacío
	 * 		6. Probando registrar compañía con telefono y dirección
	 * 		7. Probando registrar compañía con telefono y dirección a vacío
	 * 		8. Probando registrar compañía con la imagen vacía
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no está autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void positiveRegisterSponsorTest() {
		final Object testingData[][] = {
				{
					null, "antonio1", "antonio1", "antonio1", true, "Antonio", "Azaña", null, null, "ant@mail.com", "20063918Y", null 
				}, {
					null, "alexito", "alexito", "alexito", true, "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", null 
				}, {
					null, "carlos", "carlos", "carlos", true, "Carlos", "Sánchez", null, null, "carlossponsor@mail.com", "20063918Y", null 
				}, {
					null, "paquito", "paquito", "paquito", true, "Paco", "Millán", null, "Calle Real Nº6", "paquito@mail.com", "20063918Y", null 
				}, {
					null, "manolo", "manolo", "manolo", true, "Manolo", "Guillen", null, "", "manolete@mail.com", "20063918Y", null 
				}, {
					null, "pepito", "pepito", "pepito", true, "Pepe", "Escolar", "321456987", "Dirección incorrecta", "pepe@mail.com", "20063918Y", null
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null 
				}, {
					null, "francisco", "francisco", "francisco",  true,"Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null 
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Una compañía no puede registrar un usuario
	 * 		2. Un patrocinador no puede registrar un usuario
	 * 		3. Un usuario no puede registrar un usuario
	 * 		4. Un administrador no puede registrar un usuario
	 * 		5. Un moderador no puede registrar un usuario
	 * 		6. El correo electrónico debe cumplir un patrón determinado
	 * 		7. El nombre no puede ser nulo
	 * 		8. El apellido no puede ser nulo
	 * 		9. El nombre no puede estar vacío
	 * 		10. El apellido no puede estar vacío
	 * 		11. El correo electrónico no puede ser nulo
	 * 		12. El electrónico no puede ser vacío
	 * 		13. El sponsorname debe estar entre 5 y 32 caracteres
	 * 		14. El sponsorname debe estar entre 5 y 32 caracteres
	 * 		15. El identificador debe tener un formato válido
	 * 		16. El identificador no puede estar vacío
	 * 		17. El identificador no puede ser nulo
	 * 		18. La confirmación de la contraseña tiene que coincidir con la contraseña
	 * 		19. Se debe aceptar los términos y condiciones
	 * 		20. El nombre de usuario no puede estar vacío
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no está autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void negativeRegisterSponsorTest() {
		final Object testingData[][] = {
				{
					"sponsor1", "sponsor13", "sponsor13", "sponsor13", true, "Antonio", "Azaña", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"sponsor1", "sponsor13", "sponsor13", "sponsor13", true, "Antonio", "Azaña", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"sponsor2", "sponsor13", "sponsor13", "sponsor13", true, "Antonio", "Azaña", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"admin", "sponsor23", "sponsor23", "sponsor23", true, "Antonio", "Azaña", "652147893", null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"moderator1", "sponsor23", "sponsor23", "sponsor23", true, "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					null, "marta", "marta", "marta", true, "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "azaña", "azaña", "azaña", true, null, "Azaña", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "marta", "marta", "marta", true, "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "azaña2", "azaña2", "azaña2", true, "", "Azaña", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "marta2", "marta2", "marta2", true, "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
				},{
					null, "marta3", "marta3", "marta3", true, "Marta", "Azaña", "664857123", "Calle Novena", null, "20063918Y", ConstraintViolationException.class 
				}, {
					null, "maria", "maria", "maria", true, "María", "Villarín", "664254123", "Inserte dirección", "", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "gost", "gostino", "gostino", true, "Gostin", "Perez", "", "Calle Sponsor Nº41", "gostin@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "administratoradministratoradministrator", "admin", "admin", true, "Gostin", "Perez", "", "Calle Sponsor Nº41", "gostin@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "2006-A-3-A918Y", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "", ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", null, ConstraintViolationException.class 
				}, {
					null, "francisco", "francisco", "asd", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					null, "francisco", "francisco", "francisco", false, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class  
				}, {
					null, null, "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class  
				}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (boolean) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Class<?>) testingData[i][11]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}

	// Ancillary methods ------------------------------------------------------

	/*
	 * Registrarse como usuario
	 * 	Pasos:
	 * 		1. Creamos un nuevo usuario
	 * 		2. Le asignamos los datos introducidos
	 * 		3. Guardamos el nuevo usuario
	 * 		4. Nos desautentificamos
	 * 		5. Comprobamos que se ha creado y con los datos correspondientes
	 */
	protected void template(final String sponsorBean, final String username, final String password, final String checkPassword, final boolean check, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final Class<?> expected) {
		Class<?> caught;
		Sponsor reconstructedSponsor, sponsorSave, sponsor;
		SponsorForm sponsorForm;
		DataBinder binder;

		caught = null;
		try {
			
			super.authenticate(sponsorBean);
			
			sponsorForm = new SponsorForm();
			sponsorForm.setUsername(username);
			sponsorForm.setPassword(password);
			sponsorForm.setCheckPassword(checkPassword);
			sponsorForm.setName(name);
			sponsorForm.setSurname(surname);
			sponsorForm.setPhone(phone);
			sponsorForm.setEmail(email);
			sponsorForm.setAddress(address);
			sponsorForm.setIdentifier(identifier);
			sponsorForm.setCheck(check);
			
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

}

