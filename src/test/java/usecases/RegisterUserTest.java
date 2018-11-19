package usecases;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.User;

import services.UserService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
		"classpath:spring/junit.xml"
	})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RegisterUserTest extends AbstractTest {

	// System under test ------------------------------------------------------

	@Autowired
	private UserService		userService;
	
	// Tests ------------------------------------------------------------------
	
	/*
	 * Pruebas:
	 * 		1. Probando registrar usuario con telefono y dirección a null
	 *		2. Probando registrar usuario con telefono pero con dirección a null
	 * 		3. Probando registrar usuario con telefono a vacío y dirección a null
	 * 		4. Probando registrar usuario con telefono a null y dirección
	 * 		5. Probando registrar usuario con telefono a null y dirección a vacío
	 * 		6. Probando registrar usuario con telefono y dirección
	 * 		7. Probando registrar usuario con telefono y dirección a vacío
	 * 
	 * Requisitos:
	 * 		4. An actor who is not authenticated must be able to:
	 * 			1. Register to the system as a user.	 
	 */
	@Test
	public void positiveRegisterUserTest() {
		final Object testingData[][] = {
				{
					null, "antonio1", "antonio1", "Antonio", "Azaña", null, null, "ant@mail.com", null 
				}, {
					null, "alexito", "alexito", "Alejandro", "Perez", "987532146", null, "a@hotmail.com", null 
				}, {
					null, "carlos", "carlos", "Carlos", "Sánchez", null, null, "carlosuser@mail.com", null 
				}, {
					null, "paquito", "paquito", "Paco", "Millán", null, "Calle Real Nº6", "paquito@mail.com", null 
				}, {
					null, "manolo", "manolo", "Manolo", "Guillen", null, "", "manolete@mail.com", null 
				}, {
					null, "pepito", "pepito", "Pepe", "Escolar", "321456987", "Dirección incorrecta", "pepe@mail.com", null
				}, {
					null, "francisco", "francisco", "Francisco", "Cerrada", null, "", "fran@mail.com", null 
				}
		};
			
	for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Class<?>) testingData[i][8]);
			} catch (final Throwable oops) {
				throw new RuntimeException(oops);
			} finally {
				super.rollbackTransaction();
			}
	}
	
	/*
	 * Pruebas:
	 * 		1. Un usuario logueado no puede registrar a otro
	 * 		2. Un usuario logueado no puede registrar a otro
	 * 		3. Un usuario logueado no puede registrar a otro
	 * 		4. El email tiene que tener el formato de un email
	 * 		5. El nombre no puede ser nulo
	 * 		6. El apellido no puede ser nulo
	 * 		7. El nombre no puede ser vacío
	 * 		8. El apellido no puede ser vacío
	 * 		9. El email no puede ser nulo
	 * 		10. El email no puede ser vacío
	 * 		11. El username debe estar entre 5 y 32
	 * 		12. La password debe estar entre 5 y 32
	 * 
	 * Requisitos:
	 * 		4. An actor who is not authenticated must be able to:
	 * 			1. Register to the system as a user.
	 */
	@Test
	public void negativeRegisterUserTest() {
		final Object testingData[][] = {
				{
					"user2", "user13", "user13", "Antonio", "Azaña", null, null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"admin", "user23", "user23", "Antonio", "Azaña", "652147893", null, "ant@mail.com", IllegalArgumentException.class 
				}, {
					"manager1", "user23", "user23", "Antonio", "Perez", "", "Calle Manager Nº41", "ant@mail.com", IllegalArgumentException.class 
				}, {
					null, "marta", "marta", "Marta", "Sanchez", "664857123", "Calle Falsa 23", "manuelito", ConstraintViolationException.class 
				}, {
					null, "azaña", "azaña", null, "Azaña", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class 
				}, {
					null, "marta", "marta", "Marta", null, "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
				}, {
					null, "azaña2", "azaña2", "", "Azaña", "664857123", "Calle Inventada", "m@mail.com", ConstraintViolationException.class 
				}, {
					null, "marta2", "marta2", "Marta", "", "664857123", "Calle sin numero", "martita@gmail.es", ConstraintViolationException.class 
				},{
					null, "marta3", "marta3", "Marta", "Azaña", "664857123", "Calle Novena", null, ConstraintViolationException.class 
				}, {
					null, "maria", "maria", "María", "Villarín", "664254123", "Inserte dirección", "", ConstraintViolationException.class 
				}, {
					null, "gost", "gostino", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}, {
					null, "administratoradministratoradministrator", "admin", "Gostin", "Perez", "", "Calle User Nº41", "gostin@mail.com", ConstraintViolationException.class 
				}
		};
		
		for (int i = 0; i < testingData.length; i++)
			try {
				super.startTransaction();
				this.template((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7], (Class<?>) testingData[i][8]);
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
	 * 2. Creamos un nuevo user
	 * 3. Le asignamos el username, el password, el name, el surname, el phone, la address, el birthdate y el email correspondientes
	 * 4. Guardamos el nuevo user
	 * 5. Nos desautentificamos
	 * 6. Comprobamos se ha creado y existe
	 */
	protected void template(final String user, final String username, final String password, final String name, final String surname, final String phoneNumber, final String postalAddress, final String emailAddress, final Class<?> expected) {
		Class<?> caught;
		User userEntity, userSave;

		caught = null;
		try {
			super.authenticate(user);

			userEntity = this.userService.create();
			userEntity.getUserAccount().setUsername(username);
			userEntity.getUserAccount().setPassword(password);
			userEntity.setName(name);
			userEntity.setSurname(surname);
			userEntity.setPhoneNumber(phoneNumber);
			userEntity.setPostalAddress(postalAddress);
			userEntity.setEmailAddress(emailAddress);
			
			userSave = this.userService.save(userEntity);
			super.unauthenticate();
			super.flushTransaction();
			
			Assert.isTrue(this.userService.findAll().contains(userSave));

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

}

