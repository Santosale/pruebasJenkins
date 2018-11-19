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

import domain.Moderator;
import forms.ModeratorForm;

import services.ModeratorService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterModeratorTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private ModeratorService		moderatorService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar compa��a con telefono y direcci�n a null
	 *		2. Probando registrar compa��a con telefono pero con direcci�n a null
	 * 		3. Probando registrar compa��a con telefono a vac�o y direcci�n a null
	 * 		4. Probando registrar compa��a con telefono a null y direcci�n
	 * 		5. Probando registrar compa��a con telefono a null y direcci�n a vac�o
	 * 		6. Probando registrar compa��a con telefono y direcci�n
	 * 		7. Probando registrar compa��a con telefono y direcci�n a vac�o
	 * 		8. Probando registrar compa��a con la imagen vac�a
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no est� autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void positiveRegisterModeratorTest() {
		final Object testingData[][] = {
				{
					"admin", "antonio1", "antonio1", "antonio1", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", null 
				}, {
					"admin", "alexito", "alexito", "alexito", true, "Alejandro", "Perez", "987532146", null, "a@hotmail.com", "20063918Y", null 
				}, {
					"admin", "carlos", "carlos", "carlos", true, "Carlos", "S�nchez", null, null, "carlosmoderator@mail.com", "20063918Y", null 
				}, {
					"admin", "paquito", "paquito", "paquito", true, "Paco", "Mill�n", null, "Calle Real N�6", "paquito@mail.com", "20063918Y", null 
				}, {
					"admin", "manolo", "manolo", "manolo", true, "Manolo", "Guillen", null, "", "manolete@mail.com", "20063918Y", null 
				}, {
					"admin", "pepito", "pepito", "pepito", true, "Pepe", "Escolar", "321456987", "Direcci�n incorrecta", "pepe@mail.com", "20063918Y", null
				}, {
					"admin", "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null 
				}, {
					"admin", "francisco", "francisco", "francisco",  true,"Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", null 
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
	 * 		1. Una compa��a no puede registrar un usuario
	 * 		2. Un patrocinador no puede registrar un usuario
	 * 		3. Un usuario no puede registrar un usuario
	 * 		4. Hay que estar autenticado como admin para registrar un moderador
	 * 		5. Un moderador no puede registrar un usuario
	 * 		6. El correo electr�nico debe cumplir un patr�n determinado
	 * 		7. El nombre no puede ser nulo
	 * 		8. El apellido no puede ser nulo
	 * 		9. El nombre no puede estar vac�o
	 * 		10. El apellido no puede estar vac�o
	 * 		11. El correo electr�nico no puede ser nulo
	 * 		12. El electr�nico no puede ser vac�o
	 * 		13. El moderatorname debe estar entre 5 y 32 caracteres
	 * 		14. El moderatorname debe estar entre 5 y 32 caracteres
	 * 		15. El identificador debe tener un formato v�lido
	 * 		16. El identificador no puede estar vac�o
	 * 		17. El identificador no puede ser nulo
	 * 		18. La confirmaci�n de la contrase�a tiene que coincidir con la contrase�a
	 * 		19. Se debe aceptar los t�rminos y condiciones
	 * 		20. El nombre de usuario no puede estar vac�o
	 * 
	 * Requisitos:
	 * 		21.	Un actor que no est� autenticado debe ser capaz de:
	 *			2.	Registrarse en el sistema como empresa, patrocinador o usuario.	 
	 *
	 */
	@Test
	public void negativeRegisterModeratorTest() {
		final Object testingData[][] = {
				{
					"company1", "moderator13", "moderator13", "moderator13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"sponsor1", "moderator13", "moderator13", "moderator13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"user2", "moderator13", "moderator13", "moderator13", true, "Antonio", "Aza�a", null, null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					null, "moderator23", "moderator23", "moderator23", true, "Antonio", "Aza�a", "652147893", null, "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"moderator1", "moderator23", "moderator23", "moderator23", true, "Antonio", "Perez", "", "Calle Manager N�41", "ant@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"admin", "marta", "marta", "marta", true, "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "aza�a", "aza�a", "aza�a", true, null, "Aza�a", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "marta", "marta", "marta", true, "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "aza�a2", "aza�a2", "aza�a2", true, "", "Aza�a", "664857123", "Calle Inventada", "m@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "marta2", "marta2", "marta2", true, "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", "20063918Y", ConstraintViolationException.class 
				},{
					"admin", "marta3", "marta3", "marta3", true, "Marta", "Aza�a", "664857123", "Calle Novena", null, "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "maria", "maria", "maria", true, "Mar�a", "Villar�n", "664254123", "Inserte direcci�n", "", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "gost", "gostino", "gostino", true, "Gostin", "Perez", "", "Calle Moderator N�41", "gostin@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "administratoradministratoradministrator", "admin", "admin", true, "Gostin", "Perez", "", "Calle Moderator N�41", "gostin@mail.com", "20063918Y", ConstraintViolationException.class 
				}, {
					"admin", "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "2006-A-3-A918Y", ConstraintViolationException.class 
				}, {
					"admin", "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "", ConstraintViolationException.class 
				}, {
					"admin", "francisco", "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", null, ConstraintViolationException.class 
				}, {
					"admin", "francisco", "francisco", "asd", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class 
				}, {
					"admin", "francisco", "francisco", "francisco", false, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class  
				}, {
					"admin", null, "francisco", "francisco", true, "Francisco", "Cerrada", null, "", "fran@mail.com", "20063918Y", IllegalArgumentException.class  
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
	protected void template(final String adminBean, final String username, final String password, final String checkPassword, final boolean check, final String name, final String surname, final String phone, final String address, final String email, final String identifier, final Class<?> expected) {
		Class<?> caught;
		Moderator reconstructedModerator, moderatorSave, moderator;
		ModeratorForm moderatorForm;
		DataBinder binder;

		caught = null;
		try {
			
			super.authenticate(adminBean);
			
			moderatorForm = new ModeratorForm();
			moderatorForm.setUsername(username);
			moderatorForm.setPassword(password);
			moderatorForm.setCheckPassword(checkPassword);
			moderatorForm.setName(name);
			moderatorForm.setSurname(surname);
			moderatorForm.setPhone(phone);
			moderatorForm.setEmail(email);
			moderatorForm.setAddress(address);
			moderatorForm.setIdentifier(identifier);
			moderatorForm.setCheck(check);
			
			binder = new DataBinder(moderatorForm);
			reconstructedModerator = this.moderatorService.reconstruct(moderatorForm, binder.getBindingResult());
			moderatorSave = this.moderatorService.save(reconstructedModerator);
			Assert.notNull(moderatorSave);
			
			this.moderatorService.flush();
			
			moderator = this.moderatorService.findOne(moderatorSave.getId());
			Assert.notNull(moderator);
			Assert.notNull(moderator.getUserAccount().getUsername());
			Assert.notNull(moderator.getUserAccount().getPassword());
			Assert.notNull(moderator.getName());
			Assert.isTrue(moderator.getName().equals(name));
			Assert.notNull(moderator.getSurname());
			Assert.isTrue(moderator.getSurname().equals(surname));
			if(address != null && !address.equals(""))Assert.isTrue(moderator.getAddress().equals(address));
			Assert.notNull(moderator.getEmail());
			Assert.isTrue(moderator.getEmail().equals(email));
			if(phone != null && !phone.equals(""))Assert.isTrue(moderator.getPhone().equals(phone));
			
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.moderatorService.findAll().contains(moderator));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

